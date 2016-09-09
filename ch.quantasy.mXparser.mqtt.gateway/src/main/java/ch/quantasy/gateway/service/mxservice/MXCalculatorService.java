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
import ch.quantasy.mqtt.gateway.service.AbstractService;
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
public class MXCalculatorService<S extends MXCalculatorServiceContract> extends AbstractService<MXCalculatorServiceContract> implements MxCalculatorCallback {

    private final HashMap<String, MxCalculator> mxCalculatorMap;

    public MXCalculatorService(URI mqttURI, String instanceName) throws MqttException {
        super(mqttURI, instanceName, new MXCalculatorServiceContract("Calculator", instanceName));
        mxCalculatorMap = new HashMap<>();
        addDescription(getServiceContract().INTENT_ARGUMENTS, "id: <String> \n map: \n   <variableName>: <variableValue>\n  ...");
        addDescription(getServiceContract().INTENT_EXPRESSION, "id: <String> \n expression: <mathExpression>");

    }

    @Override
    public void messageArrived(String topic, byte[] payload) throws Exception {
        if (topic.startsWith(getServiceContract().INTENT_EXPRESSION)) {
            MXExpression mxExpression = getMapper().readValue(payload, MXExpression.class);
            String owner = topic.replace(getServiceContract().INTENT_EXPRESSION, "");
            synchronized (mxCalculatorMap) {
                MxCalculator calculator = mxCalculatorMap.get(owner);
                if (calculator == null) {
                    calculator = new MxCalculator(this, owner);
                    mxCalculatorMap.put(owner, calculator);

                }
                calculator.setMxExpression(mxExpression);
            }
        }
        if (topic.startsWith(getServiceContract().INTENT_ARGUMENTS)) {
            MXArgument mxArgument = getMapper().readValue(payload, MXArgument.class);
            String owner = topic.replace(getServiceContract().INTENT_ARGUMENTS, "");
            synchronized (mxCalculatorMap) {
                MxCalculator calculator = mxCalculatorMap.get(owner);
                if (calculator == null) {
                    calculator = new MxCalculator(this, owner);
                    mxCalculatorMap.put(owner, calculator);
                }
                calculator.setMxArgument(mxArgument);
            }
        }
    }

    @Override
    public void argumentsChanged(String owner, MXArgument argument) {
        addStatus(getServiceContract().STATUS_ARGUMENTS + owner, argument);

    }

    @Override
    public void expressionChanged(String owner, MXExpression expression) {
        addStatus(getServiceContract().STATUS_EXPRESSION + owner, expression);
    }

    @Override
    public void expressionEvaluated(String owner, MXEvaluation evaluation) {
        addStatus(getServiceContract().STATUS_EVALUATING + owner,null);
        addEvent(getServiceContract().EVENT_EVALUATION + owner, new EvaluationEvent(evaluation));
    }

    @Override
    public void evaluationInProgress(String owner, String mxExpressionID, String mxArgumentID) {
        addStatus(getServiceContract().STATUS_EVALUATING + owner,true);
    }

    public static class EvaluationEvent {

        protected long timestamp;
        protected MXEvaluation evaluation;

        public EvaluationEvent(MXEvaluation evaluation) {
            this(evaluation, System.currentTimeMillis());
        }

        public EvaluationEvent(MXEvaluation evaluation, long timeStamp) {
            this.evaluation = evaluation;
            this.timestamp = timeStamp;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public MXEvaluation getEvaluation() {
            return evaluation;
        }

    }

}
