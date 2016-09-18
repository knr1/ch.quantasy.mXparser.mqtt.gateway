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
    public final String idArguments;
    public final String idExpression;
    public final double result;

    public MXEvaluation(String argumentsID, String expressionID, double result) {
        this.idArguments = argumentsID;
        this.idExpression = expressionID;
        this.result = result;
    }

    public String getIdArguments() {
        return idArguments;
    }

    public String getIdExpression() {
        return idExpression;
    }

    public double getResult() {
        return result;
    }
    
    
}
