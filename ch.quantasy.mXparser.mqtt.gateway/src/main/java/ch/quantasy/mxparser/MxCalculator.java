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
public class MxCalculator {

    private final MxCalculatorCallback callback;
    private final String owner;
    private MXExpression mxExpression;
    private MXArgument mxArgument;

    public MxCalculator(MxCalculatorCallback callback, String owner) {
        this.callback = callback;
        this.owner = owner;
    }

    public void setMxExpression(MXExpression mxExpression) {
        if (mxExpression == null) {
            return;
        }
        this.mxExpression = mxExpression;
        callback.expressionChanged(owner,mxExpression);
        evaluate();
    }

    public void setMxArgument(MXArgument mxArgument) {
        if (mxArgument == null) {
            return;
        }
        try {
            mxArgument.getMXArguments();
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
        this.mxArgument = mxArgument;
        if(mxExpression!=null){
            mxExpression.getMxExpression().removeAllArguments();
        }
        callback.argumentsChanged(owner,mxArgument);
        evaluate();
    }

    private void evaluate() {
        if (mxExpression == null) {
            return;
        }
        Expression expression = this.mxExpression.getMxExpression();
        String mxArgumentID = null;
        try {
            if (mxArgument != null) {
                expression.addArguments(this.mxArgument.getMXArguments());
                mxArgumentID = mxArgument.getId();
            }
            if (expression.checkSyntax()) {
                callback.evaluationInProgress(owner,mxExpression.getId(),mxArgument.getId());
                double result = expression.calculate();
                callback.expressionEvaluated(owner,new MXEvaluation(mxArgumentID, mxExpression.getId(), result));
            }else{
                System.out.println(expression.getErrorMessage());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
