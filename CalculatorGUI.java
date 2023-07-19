import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Stack;

public class CalculatorGUI extends JFrame implements ActionListener {

    private JLabel displayField;
    private StringBuilder inputExpression;
    private int cursorPosition;

    public CalculatorGUI() {
        setTitle("Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(300, 400);

        displayField = new JLabel("<html></html>");
        displayField.setPreferredSize(new Dimension(280, 50));
        displayField.setOpaque(true);
        displayField.setBackground(Color.WHITE);
        displayField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(displayField, BorderLayout.NORTH);

        String[] buttonLabels = {"(", ")",".","clear", "7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "x", "0", "y", "+", "=", "'", "^", "\u221A ","sin", "cos", "log", "<-", "->", "delete"};
        JPanel buttonPanel = new JPanel(new GridLayout(8, 4));

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
        cursorPosition = 0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("=")) {
            evaluateExpression();
            resetCursorPosition(); // Reset cursor position after evaluating the expression
        } else if (command.equals("clear")) {
            handleClearButton();
            resetCursorPosition(); // Reset cursor position after clearing the expression
        } else if (command.equals("sin") || command.equals("cos") ||
                command.equals("log") || command.equals("^") || command.equals("\u221A")) {
            // Handle special functions
            adjustCursorPositionForSpecialFunctions(command);
        } else if (command.equals("<-")) {
            // Handle backspace arrow
            handleBackspace();
        } else if (command.equals("->")) {
            handleForward();
        } else if (command.equals("delete")) {
            // Handle delete button
            handleDelete();
        } else {
            // Insert the command at the cursor position
            inputExpression.insert(cursorPosition, command);
            cursorPosition++;
            displayField.setText(insertCursorIndicator(inputExpression.toString(), cursorPosition));
        }
    }

    private void evaluateExpression() {
    String expression = inputExpression.toString();

    if (containsVariable(expression)) {
        if (containsDerivative(expression)) {
        calculateDerivative(expression);
        } else if (containsSecondDerivative(expression)) {
        calculateSecondDerivative(expression);
        }
    } else {
        try {
            // Check if the expression contains special functions (sin, cos, log)
            if (expression.contains("sin(") || expression.contains("cos(") || expression.contains("log(") || expression.contains("\u221A(")) {
                // Calculate the expression containing special functions
                Function resultFunction = parseExpression(expression);
                double result = resultFunction.value();
                displayField.setText("Result: " + result);
            } else {
                // Calculate regular expressions
                Function resultFunction = parseExpression(expression);
                double result = resultFunction.value();
                displayField.setText("Result: " + result);
            }
        } catch (IllegalArgumentException e) {
            // Catch the IllegalArgumentException and display an error message
            displayField.setText(e.getMessage());
        }
        inputExpression.setLength(0);
    }
}

private Function parseExpression(String expression) throws IllegalArgumentException {
    Stack<Function> operands = new Stack<>();
    Stack<BinaryOp.Operator> operators = new Stack<>();

    // Remove any whitespace from the expression to simplify parsing
    expression = expression.replaceAll("\\s+", "");

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
        } else if (Character.isLetter(ch)) {
            // If the character is a letter, check for special functions (sin, cos, log)
            int startIndex = i;
            while (i < expression.length() && Character.isLetter(expression.charAt(i))) {
                i++;
            }
            String functionName = expression.substring(startIndex, i);

            // Check if the function is followed by an opening parenthesis
            if (i < expression.length() && expression.charAt(i) == '(') {
                // Special function with opening parenthesis (e.g., sin(x), cos(x), log(x))
                Function argument = parseExpression(expression.substring(i + 1, findMatchingClosingParenthesis(expression, i)));

                // Check if there is a constant multiplier 'a' before the special function
                if (startIndex - 1 >= 0 && (Character.isDigit(expression.charAt(startIndex - 1)) || expression.charAt(startIndex - 1) == '.')) {
                    // Parse the constant multiplier 'a' before the special function
                    double constantMultiplier = Double.parseDouble(expression.substring(startIndex - 1, startIndex));
                    operands.push(new Number(constantMultiplier));
                    operators.push(BinaryOp.Operator.Multiply);

                    // Update the start index to exclude the constant multiplier
                    startIndex--;
                }

                // Create the corresponding special function based on the function name
                Function specialFunction;
                if (functionName.equalsIgnoreCase("sin")) {
                    specialFunction = new Sin(argument);
                } else if (functionName.equalsIgnoreCase("cos")) {
                    specialFunction = new Cos(argument);
                } else if (functionName.equalsIgnoreCase("log")) {
                    specialFunction = new Log(argument);
                } else {
                    throw new IllegalArgumentException("Invalid function name: " + functionName);
                }

                operands.push(specialFunction);
                i = findMatchingClosingParenthesis(expression, i) + 1;
            } else {
                // Regular variable (e.g., x)
                Function operand = new Variable();
                operands.push(operand);
                i--; // Decrement i to handle the index increment in the loop
            }
        } else if (ch == '+' || ch == '-' || ch == '*' || ch == '/') {
            BinaryOp.Operator currentOperator = getOperator(ch);
            while (!operators.isEmpty() && hasPrecedence(operators.peek(), currentOperator)) {
                applyOperator(operands, operators);
            }
            operators.push(currentOperator);
        } else if (ch == '^') {
            // Exponentiation operator
            operators.push(BinaryOp.Operator.Power);
        } else if (ch == '(') {
            operators.push(BinaryOp.Operator.LeftParenthesis);
        } else if (ch == '\u221A') {
            // Square root function
            int startIndex = i + 1;
            int closingIndex = findMatchingClosingParenthesis(expression, i + 1);
            Function argument = parseExpression(expression.substring(startIndex + 1, closingIndex));
            Function squareRoot = new Square(argument);
            operands.push(squareRoot);
            i = closingIndex;
        } else if (ch == ')') {
            // Pop operators until a matching opening parenthesis is found
            while (!operators.isEmpty() && operators.peek() != BinaryOp.Operator.LeftParenthesis) {
                applyOperator(operands, operators);
            }
            operators.pop(); // Pop the matching opening parenthesis
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


private int findMatchingClosingParenthesis(String expression, int startIndex) {
    int count = 1;
    for (int i = startIndex + 1; i < expression.length(); i++) {
        char ch = expression.charAt(i);
        if (ch == '(') {
            count++;
        } else if (ch == ')') {
            count--;
            if (count == 0) {
                return i;
            }
        }
    }
    throw new IllegalArgumentException("No matching closing parenthesis found.");
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
            case '^':
                return BinaryOp.Operator.Power;
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
            case Power:
                return 3;
            case Square:
                return 4;
            default:
                return 0;
        }
    }

    private void applyOperator(Stack<Function> operands, Stack<BinaryOp.Operator> operators) {
        Function rightOperand = operands.pop();
        Function leftOperand = operands.isEmpty() ? new Number(1) : operands.pop(); // Use 1 as the left operand if none specified (implicit multiplication)
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

       private void resetCursorPosition() {
        cursorPosition = inputExpression.length(); // Reset cursor position to the end
    }

   private void handleBackspace() {
        if (cursorPosition > 0) {
            // Remove the character at the cursor position and update the cursor position
            cursorPosition--;
            displayField.setText(insertCursorIndicator(inputExpression.toString(), cursorPosition));
        }
    }

    private void handleForward() {
        if(cursorPosition >= 0) {
            cursorPosition++;
            displayField.setText(insertCursorIndicator(inputExpression.toString(), cursorPosition));
        }
    }

    private void handleDelete() {
        if (cursorPosition < inputExpression.length()) {
            // Remove the character after the cursor position (delete)
            inputExpression.deleteCharAt(cursorPosition - 1);
            displayField.setText(insertCursorIndicator(inputExpression.toString(), cursorPosition));
        }
    }

    private String insertCursorIndicator(String expression, int position) {
        if (position < 0 || position > expression.length()) {
            return expression; // Invalid position, return the original expression
        }
        StringBuilder expressionWithCursor = new StringBuilder(expression);
        expressionWithCursor.insert(position, "|");
        return expressionWithCursor.toString();
    }

    private void adjustCursorPositionForSpecialFunctions(String functionName) {
        // Handle cursor position adjustments for special functions
        if (cursorPosition > 0) {
            if (isFunctionCharacter(inputExpression.charAt(cursorPosition - 1))) {
                // If the character before the cursor is part of a special function, move the cursor outside the function
                int openingIndex = findOpeningFunctionIndex(inputExpression.toString(), cursorPosition - 1);
                cursorPosition = openingIndex + 1;
            }
        }
        // Append the special function to the input expression at the cursor position
        inputExpression.insert(cursorPosition, functionName + "(");
        cursorPosition += functionName.length() + 1;
        displayField.setText(insertCursorIndicator(inputExpression.toString(), cursorPosition));
    }

    private boolean isFunctionCharacter(char ch) {
        return ch == 's' || ch == 'c' || ch == 'l' || ch == '^' || ch == '√';
    }

    private int findOpeningFunctionIndex(String expression, int closingIndex) {
        int count = 1;
        for (int i = closingIndex - 1; i >= 0; i--) {
            char ch = expression.charAt(i);
            if (ch == '(') {
                count--;
            } else if (ch == ')') {
                count++;
                if (count == 0) {
                    return i;
                }
            }
        }
        throw new IllegalArgumentException("No matching opening parenthesis found.");
    }


    private boolean containsDerivative(String expression) {
        return expression.indexOf("'") >= 0;
    }

    private boolean containsSecondDerivative(String expression) {
        return expression.indexOf("''") >= 0;
    }

    private boolean containsVariable(String expression) {
    // Check if the expression contains a variable (e.g., 'x')
    return expression.contains("x") || expression.contains("X");
    }

    private void calculateDerivative(String expression) {
    // Remove the apostrophes from the expression
    expression = expression.replace("'", "");

    if (containsDerivative(expression)) {
        try {
            Function resultFunction = parseExpression(expression);
            Function derivative = resultFunction.derivative();
            displayField.setText("Derivative: " + derivative);
            inputExpression.setLength(0);
        } catch (Exception e) {
            displayField.setText("Invalid expression: " + expression);
            inputExpression.setLength(0);
        }
    } else {
        displayField.setText("Invalid expression: " + expression);
        inputExpression.setLength(0);
    }
}

    private void calculateSecondDerivative(String expression) {
        expression = expression.replace("''", ""); // Remove the double apostrophes from the expression

        try {
            Function resultFunction = parseExpression(expression);
            Function secondDerivative = resultFunction.derivative().derivative();
            displayField.setText("Second Derivative: " + secondDerivative);
            inputExpression.setLength(0);
        } catch (Exception e) {
            displayField.setText("Error");
            inputExpression.setLength(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CalculatorGUI());
    }
}
