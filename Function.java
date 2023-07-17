/**
 *An abstract class to represent a mathematical function with the ability to evaluate the function's value, derivative, and value at the given input
 * @author Jenny Zhang
 */
public abstract class Function {


     public Function() {
    }

    public abstract double value();

    public abstract double value(double input);

    public abstract Function derivative();

}


