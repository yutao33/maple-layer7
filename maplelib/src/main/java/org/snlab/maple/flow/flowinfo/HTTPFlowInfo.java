/*
 * Copyright © 2017 SNLab and others. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */

package org.snlab.maple.flow.flowinfo;

import java.util.Map;

public class HTTPFlowInfo extends AbstractFlowInfo{

    private String method;
    private String requestURL;
    private String responseStatusCode;
    private Map<String,String> requestHeader;
    private Map<String,String> responseHeader;

    public HTTPFlowInfo(String method, String requestURL) {
        super(FlowType.HTTP);
        this.method = method;
        this.requestURL = requestURL;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestURL() {
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
