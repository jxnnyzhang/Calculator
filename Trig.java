public class Trig extends Function {

    private final String function;
    private final Function operand;

    public Trig(String function, Function operand) {
        this.function = function;
        this.operand = operand;
    }

    @Override
    public double value() {
        // This method will throw an UnsupportedOperationException as it requires an input value to evaluate the trigonometric function.
        throw new UnsupportedOperationException("Input value required.");
    }

    @Override
    public double value(double x) {
        double value = operand.value(x);
        switch (function.toLowerCase()) {
            case "sin":
                return Math.sin(value);
            case "cos":
                return Math.cos(value);
            default:
                throw new UnsupportedOperationException("Unsupported trigonometric function: " + function);
        }
    }

    @Override
    public Function derivative() {
        Function operandDerivative = operand.derivative();
        switch (function.toLowerCase()) {
            case "sin":
                return new BinaryOp(BinaryOp.Operator.Multiply, new Trig("cos", operand), operandDerivative);
            case "cos":
                return new BinaryOp(BinaryOp.Operator.Multiply,
                        new Number(-1), new BinaryOp(BinaryOp.Operator.Multiply, new Trig("sin", operand), operandDerivative));
            default:
                throw new UnsupportedOperationException("Unsupported trigonometric function: " + function);
        }
    }

    @Override
    public String toString() {
        return function + "(" + operand.toString() + ")";
    }
}
