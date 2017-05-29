/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.opendaylight.maple.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.opendaylight.openflowplugin.api.OFConstants;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.IpVersion;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Ipv4Prefix;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.PortNumber;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.yang.types.rev130715.MacAddress;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.DropActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.DropActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCase;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.OutputActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetDlDstActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetDlSrcActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetNwDstActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.SetNwSrcActionCaseBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.drop.action._case.DropAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.drop.action._case.DropActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.output.action._case.OutputActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.dst.action._case.SetDlDstAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.dst.action._case.SetDlDstActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.src.action._case.SetDlSrcAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.dl.src.action._case.SetDlSrcActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.dst.action._case.SetNwDstAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.dst.action._case.SetNwDstActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.src.action._case.SetNwSrcAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.action.set.nw.src.action._case.SetNwSrcActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.Action;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.list.ActionKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.address.address.Ipv4;
import org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.address.address.Ipv4Builder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowCapableNode;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.FlowId;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.Table;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.TableKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.Flow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.inventory.rev130819.tables.table.FlowKey;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.AddFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.FlowTableRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.RemoveFlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.RemoveFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.SalFlowService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.UpdateFlowInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.UpdateFlowInputBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.flow.update.OriginalFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.flow.update.OriginalFlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.flow.update.UpdatedFlow;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.service.rev130819.flow.update.UpdatedFlowBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowCookie;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowModFlags;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.FlowRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.OutputPortValues;
import org.opendaylight.yang.gen.v1.urn.opendaylight.flow.types.rev131026.flow.Instructions;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.NodeRef;
import org.opendaylight.yang.gen.v1.urn.opendaylight.inventory.rev130819.nodes.Node;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.TcpMatchBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.model.match.types.rev131026.match.layer._4.match.UdpMatchBuilder;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.env.MapleTopology;
import org.snlab.maple.rule.MapleRule;
import org.snlab.maple.rule.field.MapleMatchField;
import org.snlab.maple.rule.match.ByteArray;
import org.snlab.maple.rule.match.MapleMatch;
import org.snlab.maple.rule.match.ValueMaskPair;
import org.snlab.maple.rule.route.Forward;
import org.snlab.maple.rule.route.ForwardAction;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class SalFlowManager {

    private static final Logger LOG = LoggerFactory.getLogger(SalFlowManager.class);

    private AtomicLong flowIdInc = new AtomicLong(10L);

    private AtomicLong flowCookieInc = new AtomicLong(0x1200000000000000L);

    private final SalFlowService salFlowService;

    private Map<MapleTopology.NodeId, Map<MapleRule,List<SalFlowEntry>>> rulesForOneNode = new HashMap<>();
    private Map<MapleRule,List<SalFlowEntry>> rulesForAllNodes =new HashMap<>();

    public SalFlowManager(SalFlowService salFlowService) {
        this.salFlowService = salFlowService;
        initDefaultLLDPrule();
    }

    private void initDefaultLLDPrule() {
        Map<MapleMatchField,MapleMatch> matches = new EnumMap<>(MapleMatchField.class);
        ValueMaskPair value = new ValueMaskPair(new ByteArray(new byte[]{(byte)0x88,(byte)0xcc}), null);
        MapleMatch match = new MapleMatch(MapleMatchField.ETH_TYPE, value);
        matches.put(MapleMatchField.ETH_TYPE,match);
        MapleRule lldprule=new MapleRule(matches, Collections.singletonList(Forward.PUNT));
        lldprule.setPriority(65535);
        lldprule.setStatus(MapleRule.Status.INSTALLED);
        rulesForAllNodes.put(lldprule,new LinkedList<>());
    }


    public void updateRules(List<MapleRule> rules) {

        Set<MapleTopology.NodeId> installNodes = findInstallNodes(rules);

        updateDeleteRules(rules, installNodes);

        updateUpdateRules(rules);

        updateInstallRules(rules);

    }

    private Set<MapleTopology.NodeId> findInstallNodes(List<MapleRule> rules) {
        Set<MapleTopology.NodeId> ret=new HashSet<>();
        for (MapleRule rule : rules) {
            if(rule.getStatus().equals(MapleRule.Status.INSTALL)){
                Map<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> rulesMap = rule.getRoute().getRulesMap();
                for (MapleTopology.NodeId nodeId : rulesMap.keySet()) {
                    if(nodeId!=null){
                        ret.add(nodeId);
                    }
                }
            }
        }
        return ret;
    }

    private void updateDeleteRules(List<MapleRule> rules, Set<MapleTopology.NodeId> installNodes) {

        for (MapleRule rule : rules) {
            if(rule.getStatus().equals(MapleRule.Status.DELETE)){
                Map<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> rulesMap = rule.getRoute().getRulesMap();
                for (Map.Entry<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> entry : rulesMap.entrySet()) {
                    MapleTopology.NodeId node = entry.getKey();
                    if(node==null){
                        List<SalFlowEntry> flows = rulesForAllNodes.remove(rule); // != null
                        for (SalFlowEntry flowEntry : flows) {
                            deleteODLRule(flowEntry);
                        }
                    } else {
                        Map<MapleRule, List<SalFlowEntry>> ruleListMap = rulesForOneNode.get(node); // !=null
                        List<SalFlowEntry> flows = ruleListMap.remove(rule); // !=null
                        for (SalFlowEntry flowEntry : flows) {
                            deleteODLRule(flowEntry);
                        }
                        if(ruleListMap.size()==0&&!installNodes.contains(node)){
                            rulesForOneNode.remove(node);
                            for (List<SalFlowEntry> entry1 : rulesForAllNodes.values()) {
                                Iterator<SalFlowEntry> iterator = entry1.iterator();
                                while (iterator.hasNext()) {
                                    SalFlowEntry next = iterator.next();
                                    if(next.nodeId.equals(node)){
                                        deleteODLRule(next);
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }
                }
                rule.setStatus(MapleRule.Status.DELETED);
            }
        }
    }

    private void updateUpdateRules(List<MapleRule> rules) {
        for (MapleRule rule : rules) {
            if(rule.getStatus().equals(MapleRule.Status.UPDATE)) {
                Match odlPktFieldMatch = buildODLPktFieldMatch(rule.getMatches());

                Map<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> rulesMap = rule.getRoute().getRulesMap();

                for (Map.Entry<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> nodeMapEntry : rulesMap.entrySet()) {

                    MapleTopology.NodeId node = nodeMapEntry.getKey();
                    Map<MapleTopology.PortId, Forward> portForward = nodeMapEntry.getValue();

                    if (node != null) {
                        List<SalFlowEntry> flows = rulesForOneNode.get(node).get(rule);  // !=null
                        for (SalFlowEntry fe : flows) {
                            Forward forward = portForward.get(fe.portId);
                            fe.priority = rule.getPriority();
                            fe.forward = forward;
                            updateRuleForNode(fe,odlPktFieldMatch);
                        }
                    } else {
                        List<SalFlowEntry> flows = rulesForAllNodes.get(rule);
                        for (SalFlowEntry fe : flows) {
                            Preconditions.checkState(fe.portId==null);
                            Forward forward = portForward.get(null);
                            fe.priority = rule.getPriority();
                            fe.forward = forward;
                            updateRuleForNode(fe,odlPktFieldMatch);
                        }
                    }

                }

                rule.setStatus(MapleRule.Status.INSTALLED);
            }
        }
    }

    private void updateInstallRules(List<MapleRule> rules){

        List<MapleTopology.NodeId> incNodes = new ArrayList<>();

        for (MapleRule rule : rules) {
            if(rule.getStatus().equals(MapleRule.Status.INSTALL)) {

                Match odlPktFieldMatch = buildODLPktFieldMatch(rule.getMatches());
                Map<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> rulesMap = rule.getRoute().getRulesMap();

                for (Map.Entry<MapleTopology.NodeId, Map<MapleTopology.PortId, Forward>> nodeMapEntry : rulesMap.entrySet()) {
                    MapleTopology.NodeId node = nodeMapEntry.getKey();
                    if (node != null) {
                        Map<MapleRule, List<SalFlowEntry>> ruleFlowsMap = rulesForOneNode.get(node);
                        if(ruleFlowsMap==null){
                            ruleFlowsMap=new HashMap<>();
                            rulesForOneNode.put(node,ruleFlowsMap);
                            incNodes.add(node);
                        }

                        List<SalFlowEntry> flows = ruleFlowsMap.get(rule);
                        if(flows==null){
                            flows=new LinkedList<>();
                            ruleFlowsMap.put(rule,flows);
                        }

                        Map<MapleTopology.PortId, Forward> portForwardMap = nodeMapEntry.getValue();
                        for (Map.Entry<MapleTopology.PortId, Forward> portForwardEntry : portForwardMap.entrySet()) {
                            MapleTopology.PortId port = portForwardEntry.getKey();

                            if(port!=null&&port.toString().equals("openflow:3:2")){
                                LOG.warn("gotit");
                            }
                            Forward forward = portForwardEntry.getValue();
                            SalFlowEntry salFlowEntry = new SalFlowEntry(node, port, rule.getPriority(), forward);
                            installRuleforNode(salFlowEntry, odlPktFieldMatch);
                            flows.add(salFlowEntry);
                        }
                    }
                }

            }
        }

        for (Map.Entry<MapleRule, List<SalFlowEntry>> entry : rulesForAllNodes.entrySet()) {
            MapleRule rule = entry.getKey();
            List<SalFlowEntry> flows = entry.getValue();
            Match odlPktFieldMatch = buildODLPktFieldMatch(rule.getMatches());
            Forward forward = rule.getRoute().getRulesMap().get(null).get(null);
            for (MapleTopology.NodeId incNode : incNodes) {
                SalFlowEntry salFlowEntry = new SalFlowEntry(incNode, null, rule.getPriority(), forward);
                installRuleforNode(salFlowEntry, odlPktFieldMatch);
                flows.add(salFlowEntry);
            }
        }

        for (MapleRule rule : rules) {
            if(rule.getStatus().equals(MapleRule.Status.INSTALL)) {

                Map<MapleTopology.PortId, Forward> portForwardMap = rule.getRoute().getRulesMap().get(null);
                if (portForwardMap != null) {
                    Forward allNodesForward = portForwardMap.get(null);
                    if (allNodesForward != null) {

                        List<SalFlowEntry> flows = new LinkedList<>();
                        rulesForAllNodes.put(rule,flows);

                        Match odlPktFieldMatch = buildODLPktFieldMatch(rule.getMatches());
                        for (MapleTopology.NodeId node : rulesForOneNode.keySet()) {
                            SalFlowEntry salFlowEntry = new SalFlowEntry(node, null, rule.getPriority(), allNodesForward);
                            installRuleforNode(salFlowEntry, odlPktFieldMatch);
                            flows.add(salFlowEntry);
                        }
                    }
                }

                rule.setStatus(MapleRule.Status.INSTALLED);
            }
        }
    }



    private void deleteODLRule(SalFlowEntry entry) {
        RemoveFlowInputBuilder builder = new RemoveFlowInputBuilder(entry.flow);
        InstanceIdentifier<Flow> flowPath = entry.flowPath;
        FlowId flowId = entry.flow.getId();

        RemoveFlowInput input = builder.setNode(new NodeRef(flowPath.firstIdentifierOf(Node.class)))
                .setFlowRef(new FlowRef(flowPath))
                .setFlowTable(new FlowTableRef(flowPath.firstIdentifierOf(Table.class)))
                .setTransactionUri(new Uri(flowId))
                .build();
        salFlowService.removeFlow(input);
    }

    private void updateRuleForNode(SalFlowEntry entry, Match odlPktFieldMatch) {
        Match odlMatch = odlPktFieldMatch;
        if (entry.portId != null) {
            MatchBuilder matchBuilder = new MatchBuilder(odlPktFieldMatch);
            matchBuilder.setInPort(new NodeConnectorId(entry.portId.toString()));
            odlMatch = matchBuilder.build();
        }
        Instructions instructions = buildODLInstructions(entry.forward);

        FlowId flowId=entry.flowPath.firstKeyOf(Flow.class).getId();
        FlowBuilder flowBuilder = new FlowBuilder()
                .setId(flowId)
                .setTableId((short) 0)
                .setMatch(odlMatch)
                .setInstructions(instructions)
                .setPriority(entry.priority)
                .setBufferId(OFConstants.OFP_NO_BUFFER)
                .setHardTimeout(null)
                .setIdleTimeout(null)
                .setCookie(new FlowCookie(BigInteger.valueOf(flowCookieInc.getAndIncrement())))
                .setFlags(new FlowModFlags(false, false, false, false, false));
        Flow flow = flowBuilder.build();

        OriginalFlow originalFlow = new OriginalFlowBuilder(entry.flow).build();
        UpdatedFlow updatedFlow = new UpdatedFlowBuilder(flow).build();

        UpdateFlowInputBuilder builder = new UpdateFlowInputBuilder();
        UpdateFlowInput input = builder.setFlowRef(new FlowRef(entry.flowPath))
                .setNode(new NodeRef(entry.flowPath.firstIdentifierOf(Node.class)))
                .setOriginalFlow(originalFlow)
                .setUpdatedFlow(updatedFlow)
                .setTransactionUri(new Uri(flowId))
                .build();

        salFlowService.updateFlow(input);

        entry.flow = flow;
    }

    private void installRuleforNode(SalFlowEntry entry, Match odlPktFieldMatch) {
        Match odlMatch = odlPktFieldMatch;
        if (entry.portId != null) {
            MatchBuilder matchBuilder = new MatchBuilder(odlPktFieldMatch);
            matchBuilder.setInPort(new NodeConnectorId(entry.portId.toString()));
            odlMatch = matchBuilder.build();
        }
        Instructions instructions = buildODLInstructions(entry.forward);

        FlowId flowId = new FlowId("maple" + flowIdInc.getAndIncrement());

        InstanceIdentifier<Node> iid = InstanceIdentifierUtils.genNodeIId(entry.nodeId.toString());

        InstanceIdentifier<Flow> flowPath = iid.builder().augmentation(FlowCapableNode.class)
                .child(Table.class, new TableKey((short) 0))
                .child(Flow.class, new FlowKey(flowId))
                .build();

        FlowBuilder flowBuilder = new FlowBuilder()
                .setId(flowId)
                .setTableId((short) 0)
                .setMatch(odlMatch)
                .setInstructions(instructions)
                .setPriority(entry.priority)
                .setBufferId(OFConstants.OFP_NO_BUFFER)
                .setHardTimeout(null)
                .setIdleTimeout(null)
                .setCookie(new FlowCookie(BigInteger.valueOf(flowCookieInc.getAndIncrement())))
                .setFlags(new FlowModFlags(false, false, false, false, false));
        Flow flow = flowBuilder.build();

        entry.flowPath = flowPath;
        entry.flow = flow;

        AddFlowInputBuilder builder = new AddFlowInputBuilder(flow);
        AddFlowInput input = builder.setNode(new NodeRef(iid))
                .setFlowRef(new FlowRef(flowPath))
                .setFlowTable(new FlowTableRef(flowPath.<Table>firstIdentifierOf(Table.class)))
                .setTransactionUri(new Uri(flowId))
                .build();

        salFlowService.addFlow(input);
    }

    private Match buildODLPktFieldMatch(Map<MapleMatchField, MapleMatch> matches) {
        MatchBuilder matchBuilder = new MatchBuilder();

        EthernetMatchBuilder ethernetMatchBuilder = null;
        Ipv4MatchBuilder ipv4MatchBuilder = null;
        Ipv6MatchBuilder ipv6MatchBuilder = null;
        ArpMatchBuilder arpMatchBuilder = null;
        IpMatchBuilder ipMatchBuilder = null;
        TcpMatchBuilder tcpMatchBuilder = null;
        UdpMatchBuilder udpMatchBuilder = null;

        for (Map.Entry<MapleMatchField, MapleMatch> entry : matches.entrySet()) {
            MapleMatchField field = entry.getKey();
            if (field.equals(MapleMatchField.INPORT)) {
                continue;
            }
            Preconditions.checkState(entry.getValue()!=null);
            ValueMaskPair valuemask = entry.getValue().getMatch();
            ByteArray value = valuemask.getValue();
            ByteArray mask = valuemask.getMask();
            switch (field) {
                case ETH_DST:
                    EthernetDestinationBuilder ethernetDestinationBuilder = new EthernetDestinationBuilder();
                    ethernetDestinationBuilder.setAddress(new MacAddress(value.toMacAddressString()));
                    if (mask != null) {
                        ethernetDestinationBuilder.setMask(new MacAddress(mask.toMacAddressString()));
                    }
                    if (ethernetMatchBuilder == null) {
                        ethernetMatchBuilder = new EthernetMatchBuilder();
                    }
                    ethernetMatchBuilder.setEthernetDestination(ethernetDestinationBuilder.build());
                    break;
                case ETH_SRC:
                    EthernetSourceBuilder ethernetSourceBuilder = new EthernetSourceBuilder();
                    ethernetSourceBuilder.setAddress(new MacAddress(value.toMacAddressString()));
                    if (mask != null) {
                        ethernetSourceBuilder.setMask(new MacAddress(mask.toMacAddressString()));
                    }
                    if (ethernetMatchBuilder == null) {
                        ethernetMatchBuilder = new EthernetMatchBuilder();
                    }
                    ethernetMatchBuilder.setEthernetSource(ethernetSourceBuilder.build());
                    break;
                case ETH_TYPE:
                    int type = value.toShort() & 0xffff;
                    EthernetType ethernetType = new EthernetTypeBuilder().setType(new EtherType((long) type)).build();
                    if (ethernetMatchBuilder == null) {
                        ethernetMatchBuilder = new EthernetMatchBuilder();
                    }
                    ethernetMatchBuilder.setEthernetType(ethernetType);
                    if (type == 0x0800 || type == 0x86dd) {
                        if (ipMatchBuilder == null) {
                            ipMatchBuilder = new IpMatchBuilder();
                        }
                        ipMatchBuilder.setIpProto(type == 0x0800 ? IpVersion.Ipv4 : IpVersion.Ipv6);
                    }
                    break;
                case IPv4_SRC:
                    if (ipv4MatchBuilder == null) {
                        ipv4MatchBuilder = new Ipv4MatchBuilder();
                    }
                    ipv4MatchBuilder.setIpv4Source(buildIpv4Prefix(valuemask));
                    break;
                case IPv4_DST:
                    if (ipv4MatchBuilder == null) {
                        ipv4MatchBuilder = new Ipv4MatchBuilder();
                    }
                    ipv4MatchBuilder.setIpv4Destination(buildIpv4Prefix(valuemask));
                    break;
                case IP_PROTO:
                    if (ipMatchBuilder == null) {
                        ipMatchBuilder = new IpMatchBuilder();
                    }
                    short val = (short) (value.toByte() & 0xff);
                    ipMatchBuilder.setIpProtocol(val);
                    break;
                case IPv6_SRC:
                    if (ipv6MatchBuilder == null) {
                        ipv6MatchBuilder = new Ipv6MatchBuilder();
                    }
                    //ipv6MatchBuilder.setIpv6Source(new IPv6Prefix())
                    throw new UnsupportedOperationException();
                    //break;
                case IPv6_DST:
                    throw new UnsupportedOperationException();
                    //break;
                case TCP_SPORT:
                    //FIXME
                    if(tcpMatchBuilder==null){
                        tcpMatchBuilder = new TcpMatchBuilder();
                    }
                    tcpMatchBuilder.setTcpSourcePort(new PortNumber(value.toShort()&0xffff));
                    break;
                case TCP_DPORT:
                    //FIXME
                    if(tcpMatchBuilder==null){
                        tcpMatchBuilder = new TcpMatchBuilder();
                    }
                    tcpMatchBuilder.setTcpDestinationPort(new PortNumber(value.toShort()&0xffff));
                    break;
                case UDP_SPORT:
                    //FIXME
                    if(udpMatchBuilder==null){
                        udpMatchBuilder= new UdpMatchBuilder();
                    }
                    udpMatchBuilder.setUdpSourcePort(new PortNumber(value.toShort()&0xffff));
                    break;
                case UDP_DPORT:
                    //FIXME
                    if(udpMatchBuilder==null){
                        udpMatchBuilder= new UdpMatchBuilder();
                    }
                    udpMatchBuilder.setUdpDestinationPort(new PortNumber(value.toShort()&0xffff));
                    break;
                default:
                    throw new Error("type error");
            }
        }

        if (ethernetMatchBuilder != null) {
            matchBuilder.setEthernetMatch(ethernetMatchBuilder.build());
        }
        if (ipMatchBuilder != null) {
            matchBuilder.setIpMatch(ipMatchBuilder.build());
        }
        if (ipv4MatchBuilder != null) {
            matchBuilder.setLayer3Match(ipv4MatchBuilder.build());
        } else if (ipv6MatchBuilder != null) {
            matchBuilder.setLayer3Match(ipv6MatchBuilder.build());
        } else if (arpMatchBuilder != null) {
            matchBuilder.setLayer3Match(arpMatchBuilder.build());
        }

        if(tcpMatchBuilder!=null){
            matchBuilder.setLayer4Match(tcpMatchBuilder.build());
        } else if(udpMatchBuilder!=null){
            matchBuilder.setLayer4Match(udpMatchBuilder.build());
        }

        return matchBuilder.build();
    }

    private Ipv4Prefix buildIpv4Prefix(ValueMaskPair parm) {
        String ipstr = parm.getValue().toIpv4AddressString();
        ByteArray mask = parm.getMask();
        String maskstr = "32";
        if (mask != null) {
            maskstr = String.valueOf(mask.toPrefixMaskNum(32));
        }
        return new Ipv4Prefix(ipstr + "/" + maskstr);
    }

    private Ipv4Prefix buildIpv4Prefix(ByteArray ip, ByteArray mask) {
        String ipstr = ip.toIpv4AddressString();
        String maskstr = "32";
        if (mask != null) {
            maskstr = String.valueOf(mask.toPrefixMaskNum(32));
        }
        return new Ipv4Prefix(ipstr + "/" + maskstr);
    }

    private Instructions buildODLInstructions(Forward forward) {
        ApplyActionsCase applyActionsCase = buildODLApplyActionsCase(forward);
        Instruction instruction = new InstructionBuilder()
                .setOrder(0)
                .setInstruction(applyActionsCase)
                .setKey(new InstructionKey(0))
                .build();
        return new InstructionsBuilder().setInstruction(ImmutableList.of(instruction)).build();
    }

    private ApplyActionsCase buildODLApplyActionsCase(Forward forward) {
        List<Action> odlActionList = new ArrayList<>();
        int instOrder = 0;

        for (ForwardAction.Action action : forward.getActions()) {
            if (action instanceof ForwardAction.OutPut) {
                MapleTopology.PortId outPort = ((ForwardAction.OutPut) action).getPortId();
                OutputAction outputAction = new OutputActionBuilder()
                        .setOutputNodeConnector(new NodeConnectorId(outPort.toString()))
                        .build();
                OutputActionCase outputActionCase = new OutputActionCaseBuilder()
                        .setOutputAction(outputAction)
                        .build();
                Action odlaction = new ActionBuilder()
                        .setOrder(instOrder)
                        .setKey(new ActionKey(instOrder))
                        .setAction(outputActionCase)
                        .build();
                odlActionList.add(odlaction);
            } else if (action instanceof ForwardAction.Punt) {

                OutputAction punt = new OutputActionBuilder()
                        .setMaxLength(0xffff)
                        .setOutputNodeConnector(new Uri(OutputPortValues.CONTROLLER.toString()))
                        .build();
                OutputActionCase puntOutputActionCase = new OutputActionCaseBuilder()
                        .setOutputAction(punt)
                        .build();
                Action odlaction = new ActionBuilder()
                        .setOrder(instOrder)
                        .setKey(new ActionKey(instOrder))
                        .setAction(puntOutputActionCase)
                        .build();
                odlActionList.add(odlaction);
            } else if (action instanceof ForwardAction.Drop) {
                DropAction dropAction = new DropActionBuilder()
                        .build();
                DropActionCase dropActionCase = new DropActionCaseBuilder()
                        .setDropAction(dropAction)
                        .build();
                Action odlaction = new ActionBuilder()
                        .setOrder(instOrder)
                        .setKey(new ActionKey(instOrder))
                        .setAction(dropActionCase)
                        .build();
                odlActionList.add(odlaction);
            } else if (action instanceof ForwardAction.SetField){
                ForwardAction.SetField setField = (ForwardAction.SetField) action;
                MapleMatchField field = setField.getField();
                ByteArray value = setField.getValue();
                ByteArray mask = setField.getMask();
                org.opendaylight.yang.gen.v1.urn.opendaylight.action.types.rev131112.action.Action setFieldAction = null;
                switch(field){
                    case ETH_SRC:
                        SetDlSrcAction setDlSrcAction = new SetDlSrcActionBuilder()
                                .setAddress(new MacAddress(value.toMacAddressString()))
                                .build();
                        setFieldAction = new SetDlSrcActionCaseBuilder()
                                .setSetDlSrcAction(setDlSrcAction)
                                .build();
                        break;
                    case ETH_DST:
                        SetDlDstAction setDlDstAction = new SetDlDstActionBuilder()
                                .setAddress(new MacAddress(value.toMacAddressString()))
                                .build();
                        setFieldAction = new SetDlDstActionCaseBuilder()
                                .setSetDlDstAction(setDlDstAction)
                                .build();
                        break;
                    case IPv4_SRC:
                        Ipv4 ipv4 = new Ipv4Builder()
                                .setIpv4Address(new Ipv4Prefix(buildIpv4Prefix(value, mask)))
                                .build();
                        SetNwSrcAction setNwSrcAction = new SetNwSrcActionBuilder()
                                .setAddress(ipv4)
                                .build();
                        setFieldAction = new SetNwSrcActionCaseBuilder()
                                .setSetNwSrcAction(setNwSrcAction)
                                .build();
                        break;
                    case IPv4_DST:
                        Ipv4 ipv4b = new Ipv4Builder()
                                .setIpv4Address(new Ipv4Prefix(buildIpv4Prefix(value, mask)))
                                .build();
                        SetNwDstAction setNwDstAction = new SetNwDstActionBuilder()
                                .setAddress(ipv4b)
                                .build();
                        setFieldAction = new SetNwDstActionCaseBuilder()
                                .setSetNwDstAction(setNwDstAction)
                                .build();
                        break;
                    default:
                        throw new Error("type error");
                }
                Action odlaction = new ActionBuilder()
                        .setOrder(instOrder)
                        .setKey(new ActionKey(instOrder))
                        .setAction(setFieldAction)
                        .build();
                odlActionList.add(odlaction);
            } else {
                throw new UnsupportedOperationException();
            }
            instOrder++;
        }

        ApplyActions applyActions = new ApplyActionsBuilder().setAction(odlActionList).build();
        return new ApplyActionsCaseBuilder().setApplyActions(applyActions).build();
    }
}
