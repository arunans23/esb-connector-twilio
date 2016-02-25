/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 * 
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.carbon.connector.twilio.phonenumbers;

import java.util.HashMap;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.synapse.MessageContext;
import org.apache.synapse.SynapseException;
import org.apache.synapse.SynapseLog;
import org.wso2.carbon.connector.core.AbstractConnector;
import org.wso2.carbon.connector.core.ConnectException;
import org.wso2.carbon.connector.core.util.ConnectorUtils;
import org.wso2.carbon.connector.twilio.util.TwilioUtil;

import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.resource.instance.OutgoingCallerId;

/*
 * Class mediator for purchasing a phone numbers.
 * For more information, see
 * http://www.twilio.com/docs/api/rest/incoming-phone-numbers#list-post
 */
public class UpdateOutgoingPhoneNumber extends AbstractConnector {

    @Override
    public void connect(MessageContext messageContext) throws ConnectException {

        SynapseLog log = getLog(messageContext);
        log.auditLog("Start: update outgoing phone number");
        String outgoingCallerId =
                (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                        TwilioUtil.PARAM_OUTGOING_CALLERID);
        String friendlyName =
                (String) ConnectorUtils.lookupTemplateParamater(messageContext,
                        TwilioUtil.PARAM_FRIENDLY_NAME);
        Map<String, String> params = new HashMap<String, String>();
        if (friendlyName != null) {
            params.put(TwilioUtil.TWILIO_FRIENDLY_NAME, friendlyName);
        }

        try {
            TwilioRestClient twilioRestClient = TwilioUtil.getTwilioRestClient(messageContext);
            OutgoingCallerId callerId = twilioRestClient.getAccount().getOutgoingCallerId(outgoingCallerId);
            callerId.update(params);
            OMElement omResponse = TwilioUtil.parseResponse("outgoingphonenumber.update.success");
            TwilioUtil.preparePayload(messageContext, omResponse);
        } catch (Exception e) {
            log.error(e.getMessage());
            TwilioUtil.handleException(e, "0005", messageContext);
            throw new SynapseException(e);
        }
        log.auditLog("End: update outgoing phone number");
    }
}
