/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.flow.flowinfo;

import com.google.common.base.Preconditions;
import org.snlab.maple.packet.MaplePacket;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HTTPFlowInfo extends AbstractFlowInfo{

    private String method;
    private String requestURL;
    private String responseStatusCode;
    private Map<String,String> requestHeader;
    private Map<String,String> responseHeader;

    protected Set<MaplePacket> methodTrackSet= Collections.synchronizedSet(new HashSet<MaplePacket>());
    protected Set<MaplePacket> requestURLTrackSet= Collections.synchronizedSet(new HashSet<MaplePacket>());

    public HTTPFlowInfo(String method, String requestURL) {
        super(FlowType.HTTP);
        this.method = method;
        this.requestURL = requestURL;
    }

    public String getMethod(MaplePacket pkt) {
        if(pkt!=null) {
            methodTrackSet.add(pkt);
        }
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestURL(MaplePacket pkt) {
        if(pkt!=null) {
            requestURLTrackSet.add(pkt);
        }
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getResponseStatusCode() {
        return responseStatusCode;
    }

    public void setResponseStatusCode(String responseStatusCode) {
        this.responseStatusCode = responseStatusCode;
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader;
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    @Override
    public Set<MaplePacket> getAndremoveAllTrack() {
        HashSet<MaplePacket> ret = new HashSet<>(typeTrackSet);
        typeTrackSet.clear();
        ret.addAll(methodTrackSet);
        methodTrackSet.clear();
        ret.addAll(requestURLTrackSet);
        requestURLTrackSet.clear();
        return ret;
    }

    @Override
    public Set<MaplePacket> updateAndreturnTrack(AbstractFlowInfo flowInfo) {
        Preconditions.checkState(flowInfo instanceof HTTPFlowInfo);
        HTTPFlowInfo f1 = (HTTPFlowInfo) flowInfo;
        Set<MaplePacket> ret=new HashSet<>();
        if(!this.method.equals(f1.method)){
            this.method=f1.method;
            ret.addAll(this.methodTrackSet);
            this.methodTrackSet.clear();
        }
        if(!this.requestURL.equals(f1.requestURL)){
            this.requestURL=f1.requestURL;
            ret.addAll(this.requestURLTrackSet);
            this.requestURLTrackSet.clear();
        }

        //TODO

        return ret;
    }

    @Override
    public String toString() {
        return "HTTPFlowInfo{" +
                "method='" + method + '\'' +
                ", requestURL='" + requestURL + '\'' +
                ", responseStatusCode='" + responseStatusCode + '\'' +
                ", requestHeader=" + requestHeader +
                ", responseHeader=" + responseHeader +
                '}';
    }
}
