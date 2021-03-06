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

import ch.quantasy.gateway.message.ArgumentStatus;
import ch.quantasy.gateway.message.ExpressionStatus;
import ch.quantasy.gateway.message.MxIntent;
import ch.quantasy.mqtt.gateway.client.contract.AyamlServiceContract;
import ch.quantasy.mqtt.gateway.client.message.Message;
import ch.quantasy.mxparser.MXEvaluationEvent;
import ch.quantasy.mxparser.MXEvaluationStatus;
import java.util.Map;

/**
 *
 * @author reto
 */
public class MXCalculatorServiceContract extends AyamlServiceContract {

    public final String STATUS_EXPRESSION;
    public final String EVENT_EVALUATION;
    public final String EVALUATION;
    public final String EXPRESSION;
    public final String ARGUMENTS;
    public final String STATUS_ARGUMENTS;
    public final String STATUS_EVALUATING;
    public final String EVALUATING;

    public MXCalculatorServiceContract(String instance) {
        super("MX", "Calculator", instance);
        EXPRESSION = "expression";
        STATUS_EXPRESSION = STATUS + "/" + EXPRESSION;
        EVALUATION = "evaluation";
        EVENT_EVALUATION = EVENT + "/" + EVALUATION;
        ARGUMENTS = "arguments";
        STATUS_ARGUMENTS = STATUS + "/" + ARGUMENTS;
        EVALUATING = "evaluating";
        STATUS_EVALUATING = STATUS + "/" + EVALUATING;
    }

    @Override
    public void setMessageTopics(Map<String, Class<? extends Message>> messageTopicMap) {
        messageTopicMap.put(STATUS_ARGUMENTS + "/<id>", ArgumentStatus.class);
        messageTopicMap.put(STATUS_EVALUATING + "/<id>", MXEvaluationStatus.class);
        messageTopicMap.put(STATUS_EXPRESSION + "/<id>", ExpressionStatus.class);
        messageTopicMap.put(INTENT, MxIntent.class);
        messageTopicMap.put(EVENT_EVALUATION + "/<id>", MXEvaluationEvent.class);

    }

    public static String getDataFormatDescription(Class o) {
        return getDataFormatDescription(o, "");
    }

}
