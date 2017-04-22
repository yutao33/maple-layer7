/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.openflowplugin.api.OFConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowCookie;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.InstructionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Match;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.ApplyActionsCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActions;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.instruction.apply.actions._case.ApplyActionsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.Instruction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.instruction.list.InstructionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeConnectorId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.Nodes;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.node.NodeConnector;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.NodeKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.l2.types.rev130827.EtherType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetDestinationBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetSourceBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.ethernet.match.fields.EthernetTypeBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.EthernetMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.IpMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.ArpMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv4MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._3.match.Ipv6MatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.port.statistics.rev131214.FlowCapableNodeConnectorStatisticsData;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.IMapleAdaptor;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.match.ValueMaskPair;
import org.snlab.maple.rule.route.Forward;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ODLMapleAdaptor implements IMapleAdaptor {

    private static final Logger LOG = LoggerFactory.getLogger(ODLMapleAdaptor.class);

    private final DataBroker dataBroker;

    private final SalFlowService salFlowService;

    private AtomicLong flowIdInc = new AtomicLong(10L);

    private AtomicLong flowCookieInc = new AtomicLong(0x3a00000000000000L);

    public ODLMapleAdaptor(DataBroker dataBroker, SalFlowService salFlowService){
        this.dataBroker = dataBroker;
        this.salFlowService = salFlowService;
    }

    @Override
    public void sendPacket() {

        InstanceIdentifier<FlowCapableNodeConnectorStatisticsData> iid = InstanceIdentifier
                .builder(Nodes.class)
                .child(Node.class)
                .child(NodeConnector.class).augmentation(FlowCapableNodeConnectorStatisticsData.class).build();

    }

    @Override
    public void outPutTraceTree() {

        LOG.info("outPutTraceTree");

    }

    @Override
    public void updateRules(List<MapleRule> rules) {
        ReadWriteTransaction rwt = dataBroker.newReadWriteTransaction();

        deleteAllRules(rwt);

        for (MapleRule rule : rules) {
            installRule(rwt,rule);
        }

        rwtSubmit(rwt);
    }

    private void rwtSubmit(ReadWriteTransaction rwt){
        CheckedFuture<Void, TransactionCommitFailedException> future = rwt.submit();
        Futures.addCallback(future, new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void result) {
                LOG.info("success");
            }

            @Override
            public void onFailure(Throwable t) {
                LOG.info("failed "+t.getMessage());
            }
        });
    }

    private void deleteAllRules(ReadWriteTransaction rwt){
        InstanceIdentifier<Nodes> nodesIId = InstanceIdentifier.builder(Nodes.class).build();
        rwt.delete(LogicalDatastoreType.CONFIGURATION,nodesIId);
    }

    private void installRule1(WriteTransaction wt,MapleRule rule){
        Map<MapleMatchField, MapleMatch> matches = rule.getMatches();
        List<Forward> route = rule.getRoute();

    }

    private void installRule(WriteTransaction wt,MapleRule rule){
        FlowId flowId = new FlowId("maple" + flowIdInc.getAndIncrement());

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(Nodes.class)
                .child(Node.class, new NodeKey(new NodeId("openflow:1")))
                .build();
        InstanceIdentifier<Flow> flowPath = iid.builder().augmentation(FlowCapableNode.class)
                .child(Table.class, new TableKey((short) 0))
                .child(Flow.class, new FlowKey(flowId))
                .build();

        Match match = buildODLPktFieldMatch(rule);

        int priority = rule.getPriority();

        OutputAction outputAction = new OutputActionBuilder().setOutputNodeConnector(new NodeConnectorId("openflow:1:1")).build();
        OutputActionCase outputActionCase = new OutputActionCaseBuilder().setOutputAction(outputAction).build();
        Action action = new ActionBuilder().setOrder(0).setKey(new ActionKey(0)).setAction(outputActionCase).build();
        ApplyActions applyActions = new ApplyActionsBuilder().setAction(ImmutableList.of(action)).build();
        ApplyActionsCase applyActionsCase = new ApplyActionsCaseBuilder().setApplyActions(applyActions).build();
        Instruction instruction = new InstructionBuilder().setOrder(0).setInstruction(applyActionsCase).setKey(new InstructionKey(0)).build();

        FlowBuilder flowBuilder = new FlowBuilder().setId(flowId).setTableId((short)0)
                .setMatch(match)
                .setInstructions(new InstructionsBuilder()
                        .setInstruction(ImmutableList.of(instruction))
                        .build())
                .setPriority(priority)
                .setBufferId(OFConstants.OFP_NO_BUFFER)
                .setHardTimeout(null)
                .setIdleTimeout(null)
                .setCookie(new FlowCookie(BigInteger.valueOf(flowCookieInc.getAndIncrement())))
                .setFlags(new FlowModFlags(false, false, false, false, false));

        Flow flow = flowBuilder.build();

        wt.put(LogicalDatastoreType.CONFIGURATION,flowPath,flow,true);
    }

    private Match buildODLPktFieldMatch(MapleRule rule){
        MatchBuilder matchBuilder = new MatchBuilder();

        Map<MapleMatchField, MapleMatch> matches = rule.getMatches();

        EthernetMatchBuilder ethernetMatchBuilder = null;
        Ipv4MatchBuilder ipv4MatchBuilder = null;
        Ipv6MatchBuilder ipv6MatchBuilder = null;
        ArpMatchBuilder arpMatchBuilder = null;
        IpMatchBuilder ipMatchBuilder = null;

        for (Map.Entry<MapleMatchField, MapleMatch> entry : matches.entrySet()) {
            ValueMaskPair valuemask=entry.getValue().getMatch();
            ByteArray value = valuemask.getValue();
            ByteArray mask = valuemask.getMask();
            switch(entry.getKey()){
                case ETH_DST:
                    EthernetDestinationBuilder ethernetDestinationBuilder = new EthernetDestinationBuilder();
                    ethernetDestinationBuilder.setAddress(new MacAddress(value.toMacAddressString()));
                    if(mask!=null){
                        ethernetDestinationBuilder.setMask(new MacAddress(mask.toMacAddressString()));
                    }
                    if(ethernetMatchBuilder==null){
                        ethernetMatchBuilder=new EthernetMatchBuilder();
                    }
                    ethernetMatchBuilder.setEthernetDestination(ethernetDestinationBuilder.build());
                    break;
                case ETH_SRC:
                    EthernetSourceBuilder ethernetSourceBuilder = new EthernetSourceBuilder();
                    ethernetSourceBuilder.setAddress(new MacAddress(value.toMacAddressString()));
                    if(mask!=null){
                        ethernetSourceBuilder.setMask(new MacAddress(mask.toMacAddressString()));
                    }
                    if(ethernetMatchBuilder==null){
                        ethernetMatchBuilder=new EthernetMatchBuilder();
                    }
                    ethernetMatchBuilder.setEthernetSource(ethernetSourceBuilder.build());
                    break;
                case ETH_TYPE:
                    EthernetType ethernetType = new EthernetTypeBuilder().setType(new EtherType(0L)).build();
                    if(ethernetMatchBuilder==null){
                        ethernetMatchBuilder=new EthernetMatchBuilder();
                    }
                    ethernetMatchBuilder.setEthernetType(ethernetType);
                    break;
                case IPv4_SRC:
                    if(ipv4MatchBuilder==null){
                        ipv4MatchBuilder=new Ipv4MatchBuilder();
                    }
                    ipv4MatchBuilder.setIpv4Source(buildIpv4Prefix(valuemask));
                    break;
                case IPv4_DST:
                    if(ipv4MatchBuilder==null){
                        ipv4MatchBuilder=new Ipv4MatchBuilder();
                    }
                    ipv4MatchBuilder.setIpv4Destination(buildIpv4Prefix(valuemask));
                    break;
                case IP_PROTO:
                    if(ipMatchBuilder==null){
                        ipMatchBuilder=new IpMatchBuilder();
                    }
                    ipMatchBuilder.setIpProtocol(value.toShort());
                default:
                    if(ipv6MatchBuilder==null){
                        ipv6MatchBuilder=new Ipv6MatchBuilder();
                    }
                    //ipv6MatchBuilder.setIpv6Source(new IPv6Prefix())
                    break;
            }
        }

        if(ethernetMatchBuilder!=null) {
            matchBuilder.setEthernetMatch(ethernetMatchBuilder.build());
        }
        if(ipMatchBuilder!=null){
            matchBuilder.setIpMatch(ipMatchBuilder.build());
        }
        if(ipv4MatchBuilder!=null) {
            matchBuilder.setLayer3Match(ipv4MatchBuilder.build());
        } else if(ipv6MatchBuilder!=null){
            matchBuilder.setLayer3Match(ipv6MatchBuilder.build());
        } else if(arpMatchBuilder!=null){
            matchBuilder.setLayer3Match(arpMatchBuilder.build());
        }

        return matchBuilder.build();
    }

    private Ipv4Prefix buildIpv4Prefix(ValueMaskPair parm){
        String ipstr=parm.getValue().toIpv4AddressString();
        ByteArray mask = parm.getMask();
        String maskstr="32";
        if(mask!=null){
            maskstr=String.valueOf(mask.toPrefixMaskNum(32));
        }
        return new Ipv4Prefix(ipstr+"/"+maskstr);
    }

}
