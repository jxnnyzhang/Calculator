import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class CalculatorGUI extends JFrame implements ActionListener {

    private JTextField displayField;
    private StringBuilder inputExpression;
    


    public CalculatorGUI() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(300, 400);

        displayField = new JTextField();
        displayField.setEditable(false);
        add(displayField, BorderLayout.NORTH);

        String[] buttonLabels = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "/", "*", "-", ".", "=", "+", "(",")","'","sin", "cos", "log", "exp", "clear"};
        JPanel buttonPanel = new JPanel(new GridLayout(6, 4));

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(this);
            buttonPanel.add(button);
        }

        add(buttonPanel, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        inputExpression = new StringBuilder();
    }

@Override
public void actionPerformed(ActionEvent e) {
    String command = e.getActionCommand();

    // Handle button clicks here
    if (command.equals("=")) {
        evaluateExpression();
    } else if (command.equals("clear")) {
        handleClearButton();
    } else if (command.equals("sin")) {
        handleSpecialFunction("sin");
    } else if (command.equals("cos")) {
        handleSpecialFunction("cos");
    } else if (command.equals("log")) {
        handleSpecialFunction("log");
    } else if (command.equals("exp")) {
        handleSpecialFunction("exp");
    } else {
        inputExpression.append(command);
        displayField.setText(inputExpression.toString());
    }
}

private void evaluateExpression() {
    String expression = inputExpression.toString();

    try {
        // Check for apostrophes to determine whether to compute the derivative
        if (expression.contains("'")) {
            expression = expression.replace("'", ""); // Remove the apostrophes from the expression
            Function resultFunction = parseExpression(expression);
            Function derivative = resultFunction.derivative();
            double result = derivative.value(); // Compute the derivative value
            displayField.setText("Derivative: " + result);
        } else if (expression.contains("''")) {
            expression = expression.replace("''", ""); // Remove the double apostrophes from the expression
            Function resultFunction = parseExpression(expression);
            Function derivative = resultFunction.derivative().derivative();
            double result = derivative.value(); // Compute the second derivative value
            displayField.setText("Second Derivative: " + result);
        } else {
            Function resultFunction = parseExpression(expression);
            double result = resultFunction.value();
            displayField.setText("Result: " + result);
        }
    } catch (Exception e) {
        displayField.setText("Error");
    }

    inputExpression.setLength(0);
}

private Function parseExpression(String expression) throws IllegalArgumentException {
    expression = expression.replaceAll("\\s+", ""); // Remove whitespace from the expression

    Stack<Function> operands = new Stack<>();
    Stack<BinaryOp.Operator> operators = new Stack<>();
    boolean expectOperand = true;

    for (int i = 0; i < expression.length(); i++) {
        char ch = expression.charAt(i);

        if (Character.isDigit(ch) || ch == '.') {
            // If the character is a digit or a decimal point, parse the number and push it to the operands stack
            int startIndex = i;
            while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                i++;
            }
            double num = Double.parseDouble(expression.substring(startIndex, i));
            operands.push(new Number(num));
            i--; // Decrement i to handle the index increment in the loop
            expectOperand = false;
        } else if (Character.isLetter(ch)) {
            // If the character is a letter, check for Log, Cos, Sin, and Exp functions
            int startIndex = i;
            while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                i++;
            }
            String functionName = expression.substring(startIndex, i);

            if (i < expression.length() && expression.charAt(i) == '(') {
                // Special function with opening parenthesis (e.g., sin(x), cos(x), log(x), exp(x))
                i++; // Move past the opening parenthesis
                Function operand = parseExpression(expression.substring(i)); // Recursively parse the operand
                i += findClosingParenthesis(expression.substring(i)) + 1; // Find the index of the closing parenthesis
                operands.push(createSpecialFunction(functionName, operand));
            } else {
                // Regular variable (e.g., x)
                operands.push(new Variable());
                i--; // Decrement i to handle the index increment in the loop
            }
            expectOperand = false;
        } else if (isOperator(ch)) {
            // If the character is an operator
            BinaryOp.Operator currentOperator = getOperator(ch);

            if (expectOperand && (currentOperator == BinaryOp.Operator.Add || currentOperator == BinaryOp.Operator.Subtract)) {
                // Handle unary plus and minus
                if (currentOperator == BinaryOp.Operator.Subtract) {
                    // Convert unary minus to binary minus with zero as the left operand
                    operands.push(new Number(0));
                } else {
                    // Unary plus can be ignored
                    continue;
                }
            } else {
                // Binary operator
                while (!operators.isEmpty() && hasPrecedence(operators.peek(), currentOperator)) {
                    applyOperator(operands, operators);
                }
            }

            operators.push(currentOperator);
            expectOperand = true;
        } else if (ch == '(') {
            // If the character is an opening parenthesis, push it to the operators stack
            operators.push(BinaryOp.Operator.LeftParenthesis);
            expectOperand = true;
        } else if (ch == ')') {
            // If the character is a closing parenthesis, apply operators until a matching opening parenthesis is found
            while (!operators.isEmpty() && operators.peek() != BinaryOp.Operator.LeftParenthesis) {
                applyOperator(operands, operators);
            }
            operators.pop(); // Pop the matching opening parenthesis
            expectOperand = false;
        } else {
            throw new IllegalArgumentException("Invalid character: " + ch);
        }
    }

    // Apply remaining operators
    while (!operators.isEmpty()) {
        applyOperator(operands, operators);
    }

    if (operands.size() != 1 || !operators.isEmpty()) {
        throw new IllegalArgumentException("Invalid expression");
    }

    return operands.pop();
}

private boolean isOperator(char ch) {
    return ch == '+' || ch == '-' || ch == '*' || ch == '/';
}

private Function createSpecialFunction(String functionName, Function operand) {
    switch (functionName.toLowerCase()) {
        case "sin":
            return new Sin(operand);
        case "cos":
            return new Cos(operand);
        case "log":
            return new Log(operand);
        case "exp":
            return new Exp(operand);
        default:
            throw new IllegalArgumentException("Invalid function name: " + functionName);
    }
}

// Rest of the code remains the same

private int findClosingParenthesis(String expression) {
    int openParentheses = 1;
    int i = 0;
    while (openParentheses > 0 && i < expression.length()) {
        char ch = expression.charAt(i);
        if (ch == '(') {
            openParentheses++;
        } else if (ch == ')') {
            openParentheses--;
        }
        i++;
    }
    if (openParentheses > 0) {
        throw new IllegalArgumentException("Unbalanced parentheses");
    }
    return i - 1;
}

private BinaryOp.Operator getOperator(char ch) {
    switch (ch) {
        case '+':
            return BinaryOp.Operator.Add;
        case '-':
            return BinaryOp.Operator.Subtract;
        case '*':
            return BinaryOp.Operator.Multiply;
        case '/':
            return BinaryOp.Operator.Divide;
        default:
            throw new IllegalArgumentException("Invalid operator: " + ch);
    }
}

private boolean hasPrecedence(BinaryOp.Operator op1, BinaryOp.Operator op2) {
    if (op2 == BinaryOp.Operator.LeftParenthesis) {
        return false; // Parenthesis has the highest precedence
    }
    return getPrecedence(op1) >= getPrecedence(op2);
}

private int getPrecedence(BinaryOp.Operator op) {
    switch (op) {
        case Add:
        case Subtract:
            return 1;
        case Multiply:
        case Divide:
            return 2;
        default:
            return 0;
    }
}

private void applyOperator(Stack<Function> operands, Stack<BinaryOp.Operator> operators) {
    Function rightOperand = operands.pop();
    Function leftOperand = operands.pop();
    BinaryOp.Operator op = operators.pop();
    BinaryOp binaryOp = new BinaryOp(op, leftOperand, rightOperand);
    operands.push(binaryOp);
}

private void handleClearButton() {
    // Clear the input expression field
    inputExpression.setLength(0);

    // Clear the display field
    displayField.setText("");
}

private void handleSpecialFunction(String functionName) {
        // Append the special function with opening and closing parenthesis to the input expression
        inputExpression.append(functionName).append("(");
        displayField.setText(inputExpression.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> new CalculatorGUI());
    }

}