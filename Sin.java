// Sin.java
public class Sin extends Function {

    private final Function operand;

    public Sin(Function operand) {
        this.operand = operand;
    }

     @Override
    public double value() {
        return Math.sin(operand.value());
    }
    
    @Override
    public double value(double x) {
        return Math.sin(operand.value(x));
    }

    @Override
    public Function derivative() {
        return new BinaryOp(BinaryOp.Operator.Multiply, new Cos(operand), operand.derivative());
    }

    @Override
    public String toString() {
        return "sin(" + operand.toString() + ")";
    }
}
