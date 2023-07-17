/**
 *The Inverses class represents a mathematical function that computes log and its inverse exp
 * It is an abstract class that extends the Function class.
 * @author Jenny Zhang
 */
public abstract class Inverses extends Function{

    /**Stores the operand function*/
    private Function operand;

    /**
     * Constructs a new Inverses object with the given operand function
     * @param operand the function to compute the inverse of operand
     */
    public Inverses(Function operand) {
        this.operand = operand;
    }
    /**
     * Returns the operand function
     * @return the operand function
     */
    public Function getOperand() {
        return operand;
    }

    /**
     * Abstract method to get the name of the log and exp
     * @return the name of the inverse function
     */
    public abstract String getInverses();

    /**
     * Returns the string representation of the function, including its name and operand function
     * @return the string representation of the function
     */
    public String toString() {
        return getInverses() + "[" + getOperand().toString() + "]";
    }

    /**
     * Checks if this inverses object is equal to another
     * @param obj the object to compare to
     * @return true if the objects are equal, false otherwise
     */
    public boolean equals(Object obj){
        if(obj instanceof Inverses){
            Inverses inverse = (Inverses) obj;
            return (getInverses().equals(inverse.getInverses())) && (getOperand().equals(inverse.getOperand()));
        }
        else {
            return false;
        }
    }
}
