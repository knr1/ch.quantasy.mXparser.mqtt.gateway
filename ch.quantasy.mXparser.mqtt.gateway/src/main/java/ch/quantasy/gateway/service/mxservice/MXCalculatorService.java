/*
 * /*
 *  *   "TiMqWay"
 *  *
 *  *    TiMqWay(tm): A gateway to provide an MQTT-View for the Tinkerforge(tm) world (Tinkerforge-MQTT-Gateway).
 *  *
 *  *    Copyright (c) 2016 Bern University of Applied Sciences (BFH),
 *  *    Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *  *    Quellgasse 21, CH-2501 Biel, Switzerland
 *  *
 *  *    Licensed under Dual License consisting of:
 *  *    1. GNU Affero General Public License (AGPL) v3
 *  *    and
 *  *    2. Commercial license
 *  *
 *  *
 *  *    1. This program is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU Affero General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     This program is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU Affero General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU Affero General Public License
 *  *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *  *
 *  *
 *  *    2. Licensees holding valid commercial licenses for TiMqWay may use this file in
 *  *     accordance with the commercial license agreement provided with the
 *  *     Software or, alternatively, in accordance with the terms contained in
 *  *     a written agreement between you and Bern University of Applied Sciences (BFH),
 *  *     Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *  *     Quellgasse 21, CH-2501 Biel, Switzerland.
 *  *
 *  *
 *  *     For further information contact <e-mail: reto.koenig@bfh.ch>
 *  *
 *  *
 */
package ch.quantasy.gateway.service.mxservice;

import ch.quantasy.mxparser.MXExpression;
import ch.quantasy.mxparser.MXArgument;
import ch.quantasy.gateway.service.MXCalculatorServiceContract;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mxparser.MXEvaluation;
import ch.quantasy.mxparser.MxCalculator;
import ch.quantasy.mxparser.MxCalculatorCallback;
import java.net.URI;
import java.util.HashMap;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 * @param <G>
 * @param <S>
 */
public class MXCalculatorService extends GatewayClient<MXCalculatorServiceContract> implements MxCalculatorCallback {

    private final HashMap<String, MxCalculator> mxCalculatorMap;

    public MXCalculatorService(URI mqttURI, String instanceName) throws MqttException {
        super(mqttURI, instanceName, new MXCalculatorServiceContract("Calculator", instanceName));
        mxCalculatorMap = new HashMap<>();
        subscribe(getContract().INTENT_EXPRESSION + "/#", (topic, payload) -> {
            MXExpression mxExpression = getMapper().readValue(payload, MXExpression.class);
            String owner = topic.replace(getContract().INTENT_EXPRESSION, "");
            synchronized (mxCalculatorMap) {
                MxCalculator calculator = mxCalculatorMap.get(owner);
                if (calculator == null) {
                    calculator = new MxCalculator(this, owner);
                    mxCalculatorMap.put(owner, calculator);

                }
                calculator.setMxExpression(mxExpression);
            }
        });
        subscribe(getContract().INTENT_ARGUMENTS + "/#", (topic, payload) -> {
            MXArgument mxArgument = getMapper().readValue(payload, MXArgument.class);
            String owner = topic.replace(getContract().INTENT_ARGUMENTS, "");
            synchronized (mxCalculatorMap) {
                MxCalculator calculator = mxCalculatorMap.get(owner);
                if (calculator == null) {
                    calculator = new MxCalculator(this, owner);
                    mxCalculatorMap.put(owner, calculator);
                }
                calculator.setMxArgument(mxArgument);
            }
        });
        connect();

        addDescription(getContract().INTENT_ARGUMENTS, "id: <String> \n map: \n   <String>: <String>\n  ...");
        addDescription(getContract().INTENT_EXPRESSION, "id: <String> \n expression: <String>");
        addDescription(getContract().EVENT_EVALUATION, "timestamp: [0.." + Long.MAX_VALUE + "]\n idArgument: <String> \n idExpression: <String> \n result: <Double>");

    }

    @Override
    public void argumentsChanged(String owner, MXArgument argument) {
        addStatus(getContract().STATUS_ARGUMENTS + owner, argument);

    }

    @Override
    public void expressionChanged(String owner, MXExpression expression) {
        addStatus(getContract().STATUS_EXPRESSION + owner, expression);
    }

    @Override
    public void expressionEvaluated(String owner, MXEvaluation evaluation) {
        addStatus(getContract().STATUS_EVALUATING + owner, null);
        addEvent(getContract().EVENT_EVALUATION + owner, evaluation);
    }

    @Override
    public void evaluationInProgress(String owner, String mxExpressionID, String mxArgumentID) {
        addStatus(getContract().STATUS_EVALUATING + owner, true);
    }
}
