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
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.openflowplugin.api.OFConstants;
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
import org.opendaylight.yang.gen.v1.urn.opendaylight.port.statistics.rev131214.FlowCapableNodeConnectorStatisticsData;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snlab.maple.IMapleAdaptor;
import org.snlab.maple.rule.MapleRule;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.List;
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
    public void outputtracetree() {

    }

    @Override
    public void updateRules(List<MapleRule> rules) {

    }

    private void installRule(){

        FlowId flowId = new FlowId("maple" + flowIdInc.getAndIncrement());

        InstanceIdentifier<Node> iid = InstanceIdentifier.builder(Nodes.class).child(Node.class, new NodeKey(new NodeId("openflow:1"))).build();
        InstanceIdentifier<Flow> flowPath = iid.builder().augmentation(FlowCapableNode.class)
                .child(Table.class, new TableKey((short) 0))
                .child(Flow.class, new FlowKey(flowId))
                .build();

        //TcpMatch tcpMatch = new TcpMatchBuilder().setTcpDestinationPort(new PortNumber(12)).build();


        MatchBuilder builder1=new MatchBuilder();
        //builder1.setLayer4Match(tcpMatch);
        builder1.setInPort(new NodeConnectorId("openflow:1:2"));
        Match match = builder1.build();

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
                .setPriority(0)
                .setBufferId(OFConstants.OFP_NO_BUFFER)
                .setHardTimeout(null)
                .setIdleTimeout(null)
                .setCookie(new FlowCookie(BigInteger.valueOf(flowCookieInc.getAndIncrement())))
                .setFlags(new FlowModFlags(false, false, false, false, false));

        Flow flow = flowBuilder.build();


        final InstanceIdentifier<Table> tableInstanceId = flowPath.<Table>firstIdentifierOf(Table.class);
        final InstanceIdentifier<Node> nodeInstanceId = flowPath.<Node>firstIdentifierOf(Node.class);
/*
        final AddFlowInputBuilder builder = new AddFlowInputBuilder(flow);

        builder.setNode(new NodeRef(nodeInstanceId));
        builder.setFlowRef(new FlowRef(flowPath));
        builder.setFlowTable(new FlowTableRef(tableInstanceId));
        builder.setTransactionUri(new Uri(flow.getId().getValue()));

        try {
            RpcResult<AddFlowOutput> addFlowOutputRpcResult = salFlowService.addFlow(builder.build()).get();
            if(addFlowOutputRpcResult.isSuccessful()){
                //AddFlowOutput result = addFlowOutputRpcResult.getResult();
                //LOG.warn(result.toString());
            } else {
                Collection<RpcError> errors = addFlowOutputRpcResult.getErrors();
                Iterator<RpcError> iterator = errors.iterator();
                while (iterator.hasNext()) {
                    RpcError next = iterator.next();
                    LOG.warn(next.toString());
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }*/

        //KeyedInstanceIdentifier<Flow, FlowKey> flowiid = tableInstanceId.child(Flow.class, new FlowKey(new FlowId("1")));
        WriteTransaction wt = dataBroker.newWriteOnlyTransaction();
        //ReadWriteTransaction wt = dataBroker.newReadWriteTransaction();
        wt.put(LogicalDatastoreType.CONFIGURATION,flowPath,flow,true);
        CheckedFuture<Void, TransactionCommitFailedException> submit = wt.submit();
        Futures.addCallback(submit, new FutureCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void result) {
                LOG.info("success");
            }

            @Override
            public void onFailure(Throwable t) {
                LOG.warn("failed");
            }
        });

    }

}
