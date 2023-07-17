/**
 * The Polynomial class represents a polynomial function, where an operand is raised to a certain power.
 * Polynomial class is a subclass of Function.
 * This implementation assumes that the exponent is a constant value (not a function of the variable 'x').
 * If the exponent varies with 'x', additional logic needs to be implemented.
 */
public class Polynomial extends Function {
    private Function operand;
    private double power;

    public Polynomial(Function operand, double power) {
        this.operand = operand;
        this.power = power;
    }

    public Function getOperand() {
        return operand;
    }

    public double getPower() {
        return power;
    }
    
    @Override
    public double value() {
        return Math.pow(operand.value(), power);
    }


    @Override
    public double value(double x) {
        return Math.pow(operand.value(x), power);
    }

    @Override
    public Function derivative() {
        // Apply the chain rule: f(g(x))^n = n * f(g(x))^(n-1) * g'(x)
        if (power == 0.0) {
            // Derivative of constant function is zero
            return new Number(0);
        } else if (power == 1.0) {
            return operand.derivative();
        } else {
            Function left = new BinaryOp(BinaryOp.Operator.Multiply, new Number(power),
                    new Polynomial(operand, power - 1));
            Function right = operand.derivative();
            return new BinaryOp(BinaryOp.Operator.Multiply, left, right);
        }
    }

    @Override
    public String toString() {
        String operandString = getOperand().toString();
        if (getOperand() instanceof BinaryOp) {
            operandString = "(" + operandString + ")";
        }
        return operandString + "^" + Double.toString(getPower());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Polynomial) {
            Polynomial power = (Polynomial) obj;
            return (getOperand().equals(power.getOperand()) && (getPower() == power.getPower()));
        } else {
            return false;
        }
    }
}

