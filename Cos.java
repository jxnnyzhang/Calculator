public class Cos extends Function {

    private final Function operand;

    public Cos(Function operand) {
        this.operand = operand;
    }

    @Override
    public double value() {
        throw new UnsupportedOperationException("Input value required");
    }

    @Override
    public double value(double x) {
        return Math.cos(operand.value(x));
    }

    @Override
    public Function derivative() {
        return new BinaryOp(BinaryOp.Operator.Multiply,
                new Number(-1),
                new BinaryOp(BinaryOp.Operator.Multiply,
                        new Sin(operand),
                        operand.derivative()
                )
        );
    }

    @Override
    public String toString() {
        return "cos(" + operand.toString() + ")";
    }

}
