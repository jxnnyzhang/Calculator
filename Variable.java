/**
 * The Variable class represents the variable "x"
 * Variable Class is a subclass of function
 * @author Jenny Zhang
 **/
public class Variable extends Function{


    public String toString() {
        return "x";
    }

 
    @Override
    public double value() {
        throw new UnsupportedOperationException("Input expected.");
    }

    @Override
    public double value(double input) {
        return input;
    }


    @Override
    public Function derivative() {
        return new Number (1);
    }

  
    public boolean equals(Object obj) {
        return obj instanceof Variable;
    }


}




