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
package org.wso2.carbon.esb.mediator.test.send;

import org.apache.axiom.om.OMElement;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.xml.namespace.QName;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class SendIntegrationSequenceAtConfigRegistryTestCase extends ESBIntegrationTest {
    private ResourceAdminServiceClient resourceAdminServiceStub;

    @BeforeClass(alwaysRun = true)
    public void uploadSynapseConfig() throws Exception {
        super.init();
    }

    @Test(groups = { "wso2.esb" }, description = "Receiving sequence at config registry build message")
    public void testSequenceAtConfigRegistryBuildMessage() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("sendMediatorReceiveSeqAtConfigBuildMessageTrueTestProxy"), null, "WSO2");
        assertNotNull(response, "Response is null");
        assertEquals(response.getLocalName(), "getQuoteResponse", "getQuoteResponse mismatch");
        OMElement omElement = response.getFirstElement();
        String symbolResponse = omElement.getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbolResponse, "WSO2", "Symbol is not match");
    }

    @Test(groups = { "wso2.esb" }, description = "Receiving sequence at config registry not build message")
    public void testSequenceAtConfigRegistryBuildMessageNo() throws Exception {
        OMElement response = axis2Client.sendSimpleStockQuoteRequest(
                getProxyServiceURLHttp("sendMediatorReceiveSeqAtConfigBuildMessageFalseTestProxy"), null, "WSO2");
        assertNotNull(response, "Response is null");
        assertEquals(response.getLocalName(), "getQuoteResponse", "getQuoteResponse mismatch");
        OMElement omElement = response.getFirstElement();
        String symbolResponse = omElement.getFirstChildWithName(new QName("http://services.samples/xsd", "symbol"))
                .getText();
        assertEquals(symbolResponse, "WSO2", "Symbol is not match");
    }

}
