public class Number extends Function {

    private double value;

    public Number(double value) {
        this.value = value;
    }

    @Override
    public double value() {
        return value;
    }

    @Override
    public double value(double x) {
        return value;
    }

    @Override
    public Function derivative() {
        return new Number(0);
    }

    public double getValue() {
        return value;
    }

}
