package FDM;

public class Calculator implements ICalculator{

    private String normalizeSigns(String s) {

        String result = s;

        result = result.replace("++", "+");
        result = result.replace("+-", "-");
        result = result.replace("-+", "-");
        result = result.replace("--", "+");

        // If no changes were made, we are done
        if (result.equals(s)) {
            return result;
        }

        // Otherwise, keep normalizing recursively
        return normalizeSigns(result);
    }


    @Override
    public double evaluate(String number) {
        // Removing whitespaces
        String expr = number.replace(" ", "");

        expr = normalizeSigns(expr);

        // unwrap outer parentheses like "(2+3)" or "((2+3))"
        if (expr.startsWith("(") && expr.endsWith(")") && matching_outer_parentheses(expr)) {
            return evaluate(expr.substring(1, expr.length() - 1));
        }




        int openIndex = expr.lastIndexOf('(');
        if (openIndex != -1) {
            int closeIndex = expr.indexOf(')', openIndex);
            if (closeIndex == -1) {
                throw new IllegalArgumentException("Mismatched parentheses: " + expr);
            }

            String inside = expr.substring(openIndex + 1, closeIndex);

            if (inside.isEmpty()) {
                throw new IllegalArgumentException("Empty parentheses: " + expr);
            }

            double insideValue = evaluate(inside);

            String rebuilt =
                    expr.substring(0, openIndex) +
                            insideValue +
                            expr.substring(closeIndex + 1);
            rebuilt = normalizeSigns(rebuilt);
            return evaluate(rebuilt);
        }

        // Detect addition (binary only)
        int plusIndex = expr.lastIndexOf('+');
        if (plusIndex > 0) {
            char prev = expr.charAt(plusIndex - 1);
            // binary plus only if previous char ends a value
            if ((prev >= '0' && prev <= '9') || prev == ')') {
                String left = expr.substring(0, plusIndex);
                String right = expr.substring(plusIndex + 1);
                return evaluate(left) + evaluate(right);
            }
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

    private boolean matching_outer_parentheses(String expr) {
        // Checks whether the outermost '(' matches the final ')'
        return checkMatchingOuter(expr, 0, 0);
    }

    private boolean checkMatchingOuter(String expr, int index, int depth) {

        // If depth ever goes negative, parentheses are invalid
        if (depth < 0) {
            return false;
        }

        // If we've reached the end, parentheses match only if depth is zero
        if (index == expr.length()) {
            return depth == 0;
        }

        char current = expr.charAt(index);

        if (current == '(') {
            depth++;
        } else if (current == ')') {
            depth--;
        }

        // If depth returns to zero before the final character,
        // then the outer parentheses do NOT wrap the whole expression
        if (depth == 0 && index < expr.length() - 1) {
            return false;
        }

        return checkMatchingOuter(expr, index + 1, depth);
    }



}
