/*
 *Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *WSO2 Inc. licenses this file to you under the Apache License,
 *Version 2.0 (the "License"); you may not use this file except
 *in compliance with the License.
 *You may obtain a copy of the License at
 *
 *http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing,
 *software distributed under the License is distributed on an
 *"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *KIND, either express or implied.  See the License for the
 *specific language governing permissions and limitations
 *under the License.
 */
package org.wso2.carbon.esb.mediator.test.xquery;

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class XQueryReplaceEmptyMessageBody extends ESBIntegrationTest {
    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
    }

    @AfterClass(alwaysRun = true)
    private void destroy() throws Exception {
        super.cleanup();
    }

    @Test(groups = { "wso2.esb" }, description = "Do XQuery transformation for empty message body")
    public void testXQueryTransformationForEmptyBody() throws AxisFault {
        OMElement response;

        response = sendReceive(getProxyServiceURLHttp("xQueryMediatorReplaceEmptyMessageBodyTestProxy"));
        assertNotNull(response, "Response message null");
        assertEquals(response.getFirstElement()
                        .getFirstChildWithName(new QName("http://services.samples/xsd", "symbol", "ax21")).getText(), "WSO2",
                "Symbol name mismatched");

    }

    private OMElement sendReceive(String endPointReference) throws AxisFault {
        ServiceClient sender;
        Options options;
        OMElement response;

        sender = new ServiceClient();
        options = new Options();
        options.setTo(new EndpointReference(endPointReference));
        options.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
        options.setAction("urn:getQuote");

        sender.setOptions(options);

        response = sender.sendReceive(null);

        return response;
    }

}
