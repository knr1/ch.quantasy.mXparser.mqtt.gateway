/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.mxparser;

import org.mariuszgromada.math.mxparser.Expression;

/**
 *
 * @author reto
 */
public class MXExpression {

    private String id;
    private String expression;
    private transient Expression mxExpression;

    public MXExpression() {

    }

    public MXExpression(String id, String expression) {
        this.id=id;
        this.expression = expression;
    }

    public String getExpression() {
        return expression;
    }

    public Expression getMxExpression() {
        if(mxExpression==null){
            mxExpression=new Expression(expression);
        }
        return mxExpression;
    }

    public String getId() {
        return id;
    }

}
