public class Exp extends Function {

    private final Function operand;

    public Exp(Function operand) {
        this.operand = operand;
    }

    @Override
    public double value() {
        throw new UnsupportedOperationException("Input value required");
    }

    @Override
    public double value(double x) {
        return Math.exp(operand.value(x));
    }

    @Override
    public Function derivative() {
        return new BinaryOp(BinaryOp.Operator.Multiply,
                new Exp(operand),
                operand.derivative()
        );
    }

    @Override
    public String toString() {
        return "exp(" + operand.toString() + ")";
    }

}

