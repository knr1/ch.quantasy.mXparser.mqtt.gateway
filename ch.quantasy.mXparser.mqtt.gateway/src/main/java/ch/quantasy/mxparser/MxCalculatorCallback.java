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
public interface MxCalculatorCallback {
    public void argumentsChanged(String owner, MXArgument argument);
    public void expressionChanged(String owner,MXExpression expression);
    public void expressionEvaluated(String owner, MXEvaluation evaluation);
    public void evaluationInProgress(String owner, String mxExpressionID,String mxArgumentID);
}
