/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.TracetreeHistory;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.TtNodeType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.history.History;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.history.HistoryBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.history.HistoryCount;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.history.HistoryCountBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.history.HistoryKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.history.history.Pkt;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.history.history.PktBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.TracetreeV3;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.TracetreeV3Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.TtLinkV3;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.TtLinkV3Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.TtNodeV3;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.TtNodeV3Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.tt.node.v3.nodeattr.Lnodeattr;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.tt.node.v3.nodeattr.LnodeattrBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.tt.node.v3.nodeattr.Tnodeattr;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.tt.node.v3.nodeattr.TnodeattrBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.tt.node.v3.nodeattr.Vnodeattr;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.odlmaple.rev170512.tracetree.v3.grouping.tracetree.v3.tt.node.v3.nodeattr.VnodeattrBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.packet.MaplePacket;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.route.Forward;
import org.snlab.maple.tracetree.TraceTree;
import org.snlab.maple.tracetree.TraceTreeLNode;
import org.snlab.maple.tracetree.TraceTreeNode;
import org.snlab.maple.tracetree.TraceTreeTNode;
import org.snlab.maple.tracetree.TraceTreeVNode;

import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TraceTreeWriter {

    private static final Logger LOG = LoggerFactory.getLogger(TraceTreeWriter.class);

    private final DataBroker dataBroker;
    private volatile long historycount = 0;
    private List<TtLinkV3> ttLinkList = new ArrayList<>();
    private List<TtNodeV3> ttNodeList = new ArrayList<>();

    public TraceTreeWriter(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }

    public void writeTraceTree(TraceTree traceTree, MaplePacket pkt) {
        TraceTreeNode treeRoot = traceTree.getTreeRoot();

        this.ttLinkList.clear();
        this.ttNodeList.clear();
        recurse(treeRoot,new int[]{0});

        final WriteTransaction wt = this.dataBroker.newWriteOnlyTransaction();

        InstanceIdentifier<HistoryCount> historycountIID =
                InstanceIdentifier.builder(TracetreeHistory.class).child(HistoryCount.class).build();
        InstanceIdentifier<History> historyIID =
                InstanceIdentifier.create(TracetreeHistory.class).child(History.class,new HistoryKey(historycount));

        TracetreeV3 ttitem = new TracetreeV3Builder()
                .setTtLinkV3(this.ttLinkList)
                .setTtNodeV3(this.ttNodeList)
                .build();
        Pkt itempkt = new PktBuilder()
                .setPktStr(pkt.toString())
                .setTimestamp(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:SSS").format(new Date()))
                .build();
        History historyitem = new HistoryBuilder()
                .setSeq(historycount)
                .setPkt(itempkt)
                .setTracetreeV3(ttitem).build();

        try {
            wt.put(LogicalDatastoreType.OPERATIONAL,historyIID,historyitem);
            this.historycount++;
            HistoryCount hc = new HistoryCountBuilder().setCount(this.historycount).build();
            wt.put(LogicalDatastoreType.OPERATIONAL,historycountIID,hc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        submit(wt);
        writeDot();
    }

    private void writeDot() {
        StringBuilder sb=new StringBuilder("");


    }


    private void recurse(TraceTreeNode node, int[] parm) {
        if(node instanceof TraceTreeTNode){
            addTNode((TraceTreeTNode)node, parm);
        } else if(node instanceof TraceTreeVNode){
            addVNode((TraceTreeVNode)node, parm);
        } else if(node instanceof TraceTreeLNode){
            addLNode((TraceTreeLNode)node,parm[0]);
            parm[0]++;
        } else {
            throw new Error("error type");
        }
    }

    private void addTNode(TraceTreeTNode tNode, int[] parm) {
        String s = tNode.getField().toString();
        String s1 = tNode.getCondition().toString();
        Tnodeattr tnodeattr = new TnodeattrBuilder().setTestfield(s).setTestcondition(s1).build();

        String myid = String.valueOf(parm[0]);
        TtNodeV3 node = new TtNodeV3Builder()
                .setId(myid)
                .setType(TtNodeType.TNODE)
                .setNodeattr(tnodeattr)
                .build();

        this.ttNodeList.add(node);

        TraceTreeNode branchFalse = tNode.getBranchFalse();
        if(branchFalse!=null){
            int nextNodeId = ++parm[0];
            String cid = String.valueOf(nextNodeId);
            addLink(myid,cid,"False");
            recurse(branchFalse,parm);
        }
        Map<MapleMatch, TraceTreeTNode.TNodeEntry> map = tNode.getBranchTrueMap();
        for (Map.Entry<MapleMatch, TraceTreeTNode.TNodeEntry> entry : map.entrySet()) {
            TraceTreeNode child = entry.getValue().getChild();
            if(child!=null) {
                MapleMatch match = entry.getKey();
                int nextNodeId = ++parm[0];
                String cid = String.valueOf(nextNodeId);
                addLink(myid, cid, match.toString());
                recurse(child,parm);
            }
        }
    }

    private void addVNode(TraceTreeVNode vNode, int[] parm) {
        String s = vNode.getField().toString();
        ByteArray mask = vNode.getMask();
        if(mask!=null){
            s = s + "/" + mask;
        }
        Vnodeattr vnodeattr = new VnodeattrBuilder().setMatchfield(s).build();

        String myid = String.valueOf(parm[0]);
        TtNodeV3 node = new TtNodeV3Builder()
                .setId(myid)
                .setType(TtNodeType.VNODE)
                .setNodeattr(vnodeattr)
                .build();

        this.ttNodeList.add(node);

        Map<ByteArray, TraceTreeVNode.VNodeEntry> map = vNode.getMatchEntries();
        for (TraceTreeVNode.VNodeEntry entry : map.values()) {
            int nextNodeId = ++parm[0];
            String cid = String.valueOf(nextNodeId);
            addLink(myid,cid,entry.getMatch().toString());
            recurse(entry.getChild(),parm);
        }
    }

    private void addLink(String pid, String cid, String match) {
        int linkId = ttLinkList.size();
        TtLinkV3 link = new TtLinkV3Builder()
                .setId(String.valueOf(linkId))
                .setPredicateId(pid)
                .setDestinationId(cid)
                .setCondition(match)
                .build();
        this.ttLinkList.add(link);
    }


    private void addLNode(TraceTreeLNode lNode, int myId) {
        List<Forward> route = lNode.getRoute();
        List<String> forwardList = new ArrayList<>(route.size());
        for (Forward forward : route) {
            forwardList.add(forward.toString());
        }
        Lnodeattr lnodeattr = new LnodeattrBuilder().setForward(forwardList).build();
        TtNodeV3 ttNode = new TtNodeV3Builder()
                .setId(String.valueOf(myId))
                .setType(TtNodeType.LNODE)
                .setNodeattr(lnodeattr)
                .build();
        this.ttNodeList.add(ttNode);
    }

    private void submit(final WriteTransaction wt) {
        CheckedFuture<Void, TransactionCommitFailedException> future = wt.submit();
        Futures.addCallback(future, new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void result) {
                LOG.info("write tt succeed");
            }

            @Override
            public void onFailure(Throwable t) {
                LOG.info("write tt failed " + t.getMessage());
            }
        });
    }
}
