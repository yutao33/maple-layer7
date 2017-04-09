#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import re

i=r'LazyDataObjectModification{identifier = org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.node.TerminationPoint[key=TerminationPointKey [_tpId=Uri [_value=openflow:2:2]]], domData = ChildNode{mod = NodeModification [identifier=(urn:TBD:params:xml:ns:yang:network-topology?revision=2013-10-21)termination-point[{(urn:TBD:params:xml:ns:yang:network-topology?revision=2013-10-21)tp-id=openflow:2:2}], modificationType=DELETE, childModification={}], oldMeta = SimpleContainerNode{version=org.opendaylight.yangtools.yang.data.api.schema.tree.spi.Version@c8b5896, data=ImmutableMapEntryNode{nodeIdentifier=(urn:TBD:params:xml:ns:yang:network-topology?revision=2013-10-21)termination-point[{(urn:TBD:params:xml:ns:yang:network-topology?revision=2013-10-21)tp-id=openflow:2:2}], value=[ImmutableLeafNode{nodeIdentifier=(urn:TBD:params:xml:ns:yang:network-topology?revision=2013-10-21)tp-id, value=openflow:2:2, attributes={}}, ImmutableAugmentationNode{nodeIdentifier=AugmentationIdentifier{childNames=[(urn:opendaylight:model:topology:inventory?revision=2013-10-30)inventory-node-connector-ref]}, value=[ImmutableLeafNode{nodeIdentifier=(urn:opendaylight:model:topology:inventory?revision=2013-10-30)inventory-node-connector-ref, value=/(urn:opendaylight:inventory?revision=2013-08-19)nodes/node/node[{(urn:opendaylight:inventory?revision=2013-08-19)id=openflow:2}]/node-connector/node-connector[{(urn:opendaylight:inventory?revision=2013-08-19)id=openflow:2:2}], attributes={}}]}], attributes={}}}, newMeta = null}}'

tabnum=0;

str=''

def nextline():
    global str
    str+='\n'+'\t'*tabnum;

for k in i:
    if(k=='{' or k=='['):
        nextline()
        tabnum=tabnum+1;
        str+=k;
        nextline()
    elif (k=='}' or k==']'):
        tabnum=tabnum-1;
        nextline()
        str+=k;
        nextline()
    elif (k==','):
        str+=k
        nextline()
    else:
        str+=k
str1 = re.sub("\n\t*\n","\n",str)
print(str1)

