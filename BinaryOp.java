// BinaryOp.java
public class BinaryOp extends Function {

    public enum Operator {
        Add ("+"),
        Subtract ("-"),
        Multiply ("*"),
        Divide("/"),
        Power("^"),
        LeftParenthesis("(");

        private final String symbol;

        Operator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

    }

    private final Operator operator;
    private final Function leftOperand;
    private final Function rightOperand;

    public BinaryOp(Operator operator, Function leftOperand, Function rightOperand) {
        this.operator = operator;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }

     @Override
    public double value() {
        double leftValue = leftOperand.value();
        double rightValue = rightOperand.value();

        switch (operator) {
            case Add:
                return leftValue + rightValue;
            case Subtract:
                return leftValue - rightValue;
            case Multiply:
                return leftValue * rightValue;
            case Divide:
                return leftValue / rightValue;
            case Power:
                return Math.pow(leftValue, rightValue);
            default:
                throw new UnsupportedOperationException("Unsupported binary operator: " + operator);
        }
    }

    @Override
    public double value(double x) {
        double leftValue = leftOperand.value(x);
        double rightValue = rightOperand.value(x);

        switch (operator) {
            case Add:
                return leftValue + rightValue;
            case Subtract:
                return leftValue - rightValue;
            case Multiply:
                return leftValue * rightValue;
            case Divide:
                return leftValue / rightValue;
            case Power:
                return Math.pow(leftValue, rightValue);
            default:
                throw new UnsupportedOperationException("Unsupported binary operator: " + operator);
        }
    }

    @Override
    public Function derivative() {
        switch (operator) {
            case Add:
            case Subtract:
                return new BinaryOp(operator, leftOperand.derivative(), rightOperand.derivative());
            case Multiply:
                return new BinaryOp(Operator.Add,
                        new BinaryOp(Operator.Multiply, leftOperand.derivative(), rightOperand),
                        new BinaryOp(Operator.Multiply, leftOperand, rightOperand.derivative()));
            case Divide:
                return new BinaryOp(Operator.Divide,
                        new BinaryOp(Operator.Subtract,
                                new BinaryOp(Operator.Multiply, leftOperand.derivative(), rightOperand),
                                new BinaryOp(Operator.Multiply, leftOperand, rightOperand.derivative())),
                        new BinaryOp(Operator.Power, rightOperand, new Number(2)));
            case Power:
                if (rightOperand instanceof Number) {
                    double power = ((Number) rightOperand).getValue();
                    return new BinaryOp(Operator.Multiply,
                            new BinaryOp(Operator.Multiply, new Number(power), new BinaryOp(Operator.Power, leftOperand, new Number(power - 1))),
                            leftOperand.derivative());
                } else {
                    throw new UnsupportedOperationException("Differentiated power is supported only for constant exponents.");
                }
            default:
                throw new UnsupportedOperationException("Unsupported binary operator: " + operator);
        }
    }

    @Override
    public String toString() {
        return "(" + leftOperand.toString() + " " + operatorToString() + " " + rightOperand.toString() + ")";
    }

    private String operatorToString() {
        switch (operator) {
            case Add:
                return "+";
            case Subtract:
                return "-";
            case Multiply:
                return "*";
            case Divide:
                return "/";
            case Power:
                return "^";
            default:
                throw new UnsupportedOperationException("Unsupported binary operator: " + operator);
        }
    }

}