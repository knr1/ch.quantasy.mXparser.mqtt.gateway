/*
 * 
 *   "MxMqWay"
 *
 *    MxMqWay(tm): A gateway to provide an MQTT-View for the mXparser built by MARIUSZ GROMADA (mXparser-MQTT-Gateway).
 *
 *    Copyright (c) 2016 Bern University of Applied Sciences (BFH),
 *    Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *    Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *    Licensed under Dual License consisting of:
 *    1. GNU Affero General Public License (AGPL) v3
 *    and
 *    2. Commercial license
 *
 *
 *    1. This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *    2. Licensees holding valid commercial licenses for TiMqWay may use this file in
 *     accordance with the commercial license agreement provided with the
 *     Software or, alternatively, in accordance with the terms contained in
 *     a written agreement between you and Bern University of Applied Sciences (BFH),
 *     Research Institute for Security in the Information Society (RISIS), Wireless Communications & Secure Internet of Things (WiCom & SIoT),
 *     Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *     For further information contact <e-mail: reto.koenig@bfh.ch>
 *
 *
 */
package ch.quantasy.gateway.service.mxservice;

import ch.quantasy.gateway.message.Argument;
import ch.quantasy.gateway.message.ArgumentStatus;
import ch.quantasy.gateway.message.Expression;
import ch.quantasy.gateway.message.ExpressionStatus;
import ch.quantasy.gateway.message.MxIntent;
import ch.quantasy.mxparser.MXExpression;
import ch.quantasy.mxparser.MXArgument;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.message.MessageCollector;
import ch.quantasy.mqtt.gateway.client.message.PublishingMessageCollector;
import ch.quantasy.mxparser.MXEvaluationEvent;
import ch.quantasy.mxparser.MXEvaluationStatus;
import ch.quantasy.mxparser.MxCalculator;
import ch.quantasy.mxparser.MxCalculatorCallback;
import java.net.URI;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 * @param <G>
 * @param <S>
 */
public class MXCalculatorService extends GatewayClient<MXCalculatorServiceContract> implements MxCalculatorCallback {

    private final HashMap<String, MxCalculator> mxCalculatorMap;

    private final MessageCollector collector;
    private PublishingMessageCollector<MXCalculatorServiceContract> publishingCollector;

    public MXCalculatorService(URI mqttURI, String instanceName) throws MqttException {
        super(mqttURI, instanceName, new MXCalculatorServiceContract(instanceName));
        collector = new MessageCollector();
        mxCalculatorMap = new HashMap<>();
        subscribe(getContract().INTENT + "/#", (topic, payload) -> {
            Set<MxIntent> mxIntents = super.toMessageSet(payload, MxIntent.class);
            String owner = topic.replace(getContract().INTENT, "");
            synchronized (mxCalculatorMap) {
                MxCalculator calculator = mxCalculatorMap.get(owner);
                if (calculator == null) {
                    calculator = new MxCalculator(this, owner);
                    mxCalculatorMap.put(owner, calculator);

                }
                for (MxIntent mxIntent : mxIntents) {
                    if (mxIntent.expression != null) {
                        MXExpression expression = new MXExpression(mxIntent.expression.getId(), mxIntent.expression.getValue());
                        calculator.setMxExpression(expression);
                    }
                    if (mxIntent.argument != null) {
                        MXArgument argument = new MXArgument(mxIntent.argument.getId(), mxIntent.argument.getMap());
                        calculator.setMxArgument(argument);
                    }
                }
            }
        });
        connect();
        publishingCollector = new PublishingMessageCollector<MXCalculatorServiceContract>(collector, this);
    }

    @Override
    public void argumentsChanged(String owner, MXArgument mxArgument) {
        Argument argument = new Argument(mxArgument.getId(), mxArgument.getMap());
        publishingCollector.readyToPublish(getContract().STATUS_ARGUMENTS + owner, new ArgumentStatus(argument));

    }

    @Override
    public void expressionChanged(String owner, MXExpression mxExpression) {
        Expression expression = new Expression(mxExpression.getId(), mxExpression.getExpression());
        publishingCollector.readyToPublish(getContract().STATUS_EXPRESSION + owner, new ExpressionStatus(expression));
    }

    @Override
    public void expressionEvaluated(String owner, MXEvaluationEvent evaluation) {
        publishingCollector.clearPublish(getContract().STATUS_EVALUATING + owner);
        publishingCollector.readyToPublish(getContract().EVENT_EVALUATION + owner, evaluation);
    }

    @Override
    public void evaluationInProgress(String owner, MXEvaluationStatus status) {
        publishingCollector.readyToPublish(getContract().STATUS_EVALUATING + owner, status);
    }
}
