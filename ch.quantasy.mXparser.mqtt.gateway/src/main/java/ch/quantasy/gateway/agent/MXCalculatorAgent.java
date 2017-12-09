/*
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
package ch.quantasy.gateway.agent;

import ch.quantasy.gateway.message.Argument;
import ch.quantasy.gateway.message.Expression;
import ch.quantasy.gateway.message.MxIntent;
import ch.quantasy.gateway.service.mxservice.MXCalculatorServiceContract;
import ch.quantasy.mqtt.gateway.client.GatewayClient;
import ch.quantasy.mqtt.gateway.client.message.MessageCollector;
import ch.quantasy.mqtt.gateway.client.message.PublishingMessageCollector;
import ch.quantasy.mxparser.MXArgument;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.paho.client.mqttv3.MqttException;

/**
 *
 * @author reto
 */
public class MXCalculatorAgent {

    private GatewayClient gwc;
    private MessageCollector messageCollector;
    private PublishingMessageCollector intentCollector;

    public MXCalculatorAgent(URI mqttURI, String clientID) throws MqttException {
        gwc = new GatewayClient(mqttURI, clientID, new MXCalculatorServiceContract("prisma"));
        messageCollector = new MessageCollector();
        intentCollector = new PublishingMessageCollector(messageCollector, gwc);
        gwc.connect();

        MxIntent intent = new MxIntent();
        Map<String, String> arguments = new HashMap<>();
        arguments.put("x", "1");
        arguments.put("y", "2");
        arguments.put("z", "3");
        intent.argument = new Argument("question1", arguments);
        intent.expression = new Expression("question1", "sin(x+y)+cos(z)");
        intentCollector.readyToPublish(gwc.getContract().INTENT + "/IamPrisma", intent);
        
        intent = new MxIntent();
        arguments = new HashMap<>();
        arguments.put("x", "3");
        arguments.put("y", "4");
        intent.argument = new Argument("question2", arguments);
        intent.expression = new Expression("question2", "sqrt(x^2+y^2)");
        intentCollector.readyToPublish(gwc.getContract().INTENT + "/IamPrisma", intent);
        
        intent = new MxIntent();
        arguments = new HashMap<>();
        arguments.put("x", "7");
        arguments.put("y", "7");
        arguments.put("z", "7");
        intent.argument = new Argument("question1", arguments);
        intent.expression = new Expression("question1", "x+y+z");
        intentCollector.readyToPublish(gwc.getContract().INTENT + "/IamBorg", intent);
        
        intent = new MxIntent();
        arguments = new HashMap<>();
        arguments.put("x", "5");
        arguments.put("y", "12");
        intent.argument = new Argument("question2", arguments);
        intent.expression = new Expression("question2", "sqrt(x^2+y^2)");
        intentCollector.readyToPublish(gwc.getContract().INTENT + "/IamBorg", intent);

    }

    public static void main(String[] args) throws MqttException, IOException {
        URI mqttURI = URI.create("tcp://127.0.0.1:1883");
        //URI mqttURI = URI.create("tcp://iot.eclipse.org:1883");

        if (args.length > 0) {
            mqttURI = URI.create(args[0]);
        } else {
            System.out.printf("Per default, 'tcp://127.0.0.1:1883' is chosen.\nYou can provide another address as first argument i.e.: tcp://iot.eclipse.org:1883\n");
        }
        System.out.printf("\n%s will be used as broker address.\n", mqttURI);

        MXCalculatorAgent manager = new MXCalculatorAgent(mqttURI, "MXCalculatorAgent");
        System.in.read();
    }
}
