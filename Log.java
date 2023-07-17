public class Log extends Function {

    private final Function operand;

    public Log(Function operand) {
        this.operand = operand;
    }

    @Override
    public double value() {
        throw new UnsupportedOperationException("Input value required");
    }

    @Override
    public double value(double x) {
        return Math.log(operand.value(x));
    }

    @Override
    public Function derivative() {
        return new BinaryOp(BinaryOp.Operator.Divide,
                operand.derivative(),
                operand
        );
    }

    @Override
    public String toString() {
        return "log(" + operand.toString() + ")";
    }

}
