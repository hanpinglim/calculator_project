package FDM;

public class Calculator implements ICalculator{

    @Override
    public double evaluate(String number) {
        // Removing whitespaces
        String expr = number.replace(" ", "");

        // handle leading unary + or -
        if (expr.startsWith("+")) {
            return evaluate(expr.substring(1));
        }

        if (expr.startsWith("-")) {
            return -evaluate(expr.substring(1));
        }

        // Detect addition
        int plusIndex = expr.lastIndexOf("+");
        if (plusIndex != -1){
            String left = expr.substring(0, plusIndex);
            String right = expr.substring(plusIndex + 1);
            return evaluate(left) + evaluate(right);
        }
        // Detect subtraction (binary)
        // Use the first '-' that can be binary (so "1--2" splits as "1" and "-2")
        int minusIndex = expr.indexOf('-', 1); // start from 1 to ignore leading unary '-'
        if (minusIndex != -1) {
            char prev = expr.charAt(minusIndex - 1);

            // binary minus only if previous char ends a value
            if ((prev >= '0' && prev <= '9') || prev == ')') {
                String left = expr.substring(0, minusIndex);
                String right = expr.substring(minusIndex + 1);
                return evaluate(left) - evaluate(right);
            }
        }

        // Detect multiplication
        int mulIndex = expr.lastIndexOf('*');
        if (mulIndex != -1) {
            String left = expr.substring(0, mulIndex);
            String right = expr.substring(mulIndex + 1);
            return evaluate(left) * evaluate(right);
        }

        // detect division
        int divIndex = expr.lastIndexOf('/');
        if (divIndex != -1) {
            String left = expr.substring(0, divIndex);
            String right = expr.substring(divIndex + 1);
            return evaluate(left) / evaluate(right);
        }

        return Double.parseDouble(expr);
    }
}
