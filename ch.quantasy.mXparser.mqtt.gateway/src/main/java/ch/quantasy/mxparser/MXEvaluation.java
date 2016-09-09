/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.quantasy.mxparser;

/**
 *
 * @author reto
 */
public class MXEvaluation {
    public final String argumentsID;
    public final String expressionID;
    public final double result;

    public MXEvaluation(String argumentsID, String expressionID, double result) {
        this.argumentsID = argumentsID;
        this.expressionID = expressionID;
        this.result = result;
    }

    public String getArgumentsID() {
        return argumentsID;
    }

    public String getExpressionID() {
        return expressionID;
    }

    public double getResult() {
        return result;
    }
    
    
}
