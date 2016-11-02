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
package ch.quantasy.mxparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.mariuszgromada.math.mxparser.Argument;

/**
 *
 * @author reto
 */
public class MXArgument {

    private String id;
    private Map<String,String> map;
    private transient List<Argument> args;

    public MXArgument() {

    }

    public MXArgument(String id, Map<String,String> arguments) {
        if (arguments == null) {
            throw new IllegalArgumentException();
        }
        this.map = new HashMap<>(arguments);
        this.id=id;
    }

    public Map<String,String> getMap() {
        return new HashMap<>(map);
    }
    
    public Argument[] getMXArguments() throws Exception {
        if (args == null) {
            args = new ArrayList<>();
            for (Map.Entry<String,String> entry: map.entrySet()) {
                Argument argument = new Argument(entry.getKey(), entry.getValue());
                args.add(argument);
            }
            Argument[] argArray=args.toArray(new Argument[0]);
            for(Argument arg:args){
                arg.addArguments(argArray);
            }
        }
        return args.toArray(new Argument[0]);
    }

    public String getId() {
        return id;
    }
    
    
}
