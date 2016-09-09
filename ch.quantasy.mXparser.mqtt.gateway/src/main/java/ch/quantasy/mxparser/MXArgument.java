/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
