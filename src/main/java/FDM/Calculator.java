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




        int powIndex = expr.lastIndexOf('^');
        if (powIndex != -1) {

            int baseStart = findBaseStart(expr, powIndex);

            String leftPart = expr.substring(0, baseStart);
            String baseStr  = expr.substring(baseStart, powIndex);

            // exponent should be the immediate right operand (number or parentheses)
            int expEnd = findExponentEnd(expr, powIndex);
            String expStr = expr.substring(powIndex + 1, expEnd + 1);
            String rightRest = expr.substring(expEnd + 1);

            double base = evaluate(baseStr);

            // FRACTION FIRST (from the STRING)
            System.out.println("DEBUG expr=" + expr + " expStr=" + expStr);
            Fraction frac = tryParseFraction(expStr);
            double value;

            if (frac != null) {
                value = powFraction(base, frac);
            } else if (expStr.indexOf('/') != -1) {
                // If there's a slash, it was intended as a fraction exponent.
                // If we couldn't parse it, that's a real error (don't fall back to decimal).
                throw new IllegalArgumentException("Could not parse fractional exponent: " + expStr);
            } else {
                double exponent = evaluate(expStr);
                value = pow(base, exponent);
            }

            String rebuilt = leftPart + value + rightRest;
            rebuilt = normalizeSigns(rebuilt);
            return evaluate(rebuilt);
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
        // Detect subtraction (binary only)
        int minusIndex = expr.lastIndexOf('-');
        if (minusIndex > 0) {
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

    private double pow(double base, double exponent) {

        if (!isInteger(exponent)) {
            throw new IllegalArgumentException("Non-integer exponent not supported here: " + exponent);
        }

        int exp = (int) exponent;

        if (exp == 0) return 1.0;
        if (exp < 0) return 1.0 / powInt(base, -exp);

        return powInt(base, exp);
    }



    private double powInt(double base, int exp) {
        if (exp == 0) {
            return 1.0;
        }
        // recursive multiplication, no loops
        return base * powInt(base, exp - 1);
    }

    private boolean isInteger(double x) {
        return x == (int) x;
    }

    private int findBaseStart(String expr, int powIndex) {
        // base ends at powIndex-1
        int end = powIndex - 1;

        // If base ends with ')', find matching '('
        if (expr.charAt(end) == ')') {
            return findMatchingOpen(expr, end, end, 0);
        }

        // Otherwise it's a number (maybe with leading sign)
        return findNumberStart(expr, end);
    }

    private int findNumberStart(String expr, int index) {
        if (index < 0) {
            return 0;
        }

        char c = expr.charAt(index);

        // digits or decimal point → keep moving left
        if ((c >= '0' && c <= '9') || c == '.') {
            return findNumberStart(expr, index - 1);
        }

        // unary sign is allowed ONLY if it is at start or follows '('
        if ((c == '+' || c == '-') &&
                (index == 0 || expr.charAt(index - 1) == '(')) {
            return index;
        }

        // otherwise, stop
        return index + 1;
    }


    // Recursively find matching '(' for a ')' at closeIndex
    private int findMatchingOpen(String expr, int closeIndex, int index, int depth) {
        char c = expr.charAt(index);

        if (c == ')') depth++;
        else if (c == '(') depth--;

        if (depth == 0) {
            return index;
        }

        return findMatchingOpen(expr, closeIndex, index - 1, depth);
    }

    private Fraction tryParseFraction(String s) {
        if (s == null) {
            return null;
        }

        String exp = s.trim();

        // If exponent is wrapped like "(1/2)", strip ONE outer pair.
        if (exp.startsWith("(") && exp.endsWith(")")) {
            exp = exp.substring(1, exp.length() - 1).trim();
        }

        int slashIndex = exp.indexOf('/');
        if (slashIndex == -1) {
            return null;
        }

        String left = exp.substring(0, slashIndex).trim();
        String right = exp.substring(slashIndex + 1).trim();

        if (left.isEmpty() || right.isEmpty()) {
            return null;
        }

        double numeratorValue = evaluate(left);
        double denominatorValue = evaluate(right);

        if (!isInteger(numeratorValue) || !isInteger(denominatorValue)) {
            return null;
        }

        int numerator = (int) numeratorValue;
        int denominator = (int) denominatorValue;

        if (denominator == 0) {
            throw new IllegalArgumentException("Exponent denominator cannot be zero: " + s);
        }

        int g = gcd(Math.abs(numerator), Math.abs(denominator));
        numerator /= g;
        denominator /= g;

        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }

        return new Fraction(numerator, denominator);
    }


    private int gcd(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd(b, a % b);
    }

    private static class Fraction {
        int numerator;
        int denominator;

        Fraction(int numerator, int denominator) {
            this.numerator = numerator;
            this.denominator = denominator;
        }
    }

    private double powFraction(double base, Fraction exp) {

        int p = exp.numerator;
        int q = exp.denominator;

        if (q == 1) {
            return pow(base, p);
        }

        double root = nthRoot(base, q);

        double result;
        if (p < 0) {
            result = 1.0 / powInt(root, -p);
        } else {
            result = powInt(root, p);
        }

        return roundTo3dp(result);
    }

    private double nthRoot(double value, int n) {

        if (value == 0.0) {
            return 0.0;
        }

        if (n == 1) {
            return value;
        }

        // initial guess
        double guess = value / n;
        return nthRootIter(value, n, guess);
    }

    private double nthRootIter(double value, int n, double guess) {

        double denom = powInt(guess, n - 1);
        double next = ((n - 1) * guess + (value / denom)) / n;

        if (Math.abs(next - guess) < 1e-10) {
            return next;
        }

        return nthRootIter(value, n, next);
    }

    private double roundTo3dp(double x) {
        return Math.round(x * 10000.0) / 10000.0;
    }

    private int findExponentEnd(String expr, int powIndex) {
        int start = powIndex + 1;

        if (start >= expr.length()) {
            throw new IllegalArgumentException("Missing exponent: " + expr);
        }

        if (expr.charAt(start) == '(') {
            return findMatchingClose(expr, start, start, 0);
        }

        return findNumberEnd(expr, start, start);
    }

    private int findNumberEnd(String expr, int index, int start) {
        if (index >= expr.length()) {
            return expr.length() - 1;
        }

        char c = expr.charAt(index);

        // allow unary sign only at the start of this number
        if ((c == '+' || c == '-') && index == start) {
            return findNumberEnd(expr, index + 1, start);
        }

        if ((c >= '0' && c <= '9') || c == '.') {
            return findNumberEnd(expr, index + 1, start);
        }

        return index - 1;
    }

    private int findMatchingClose(String expr, int openIndex, int index, int depth) {
        char c = expr.charAt(index);

        if (c == '(') depth++;
        else if (c == ')') depth--;

        if (depth == 0) {
            return index;
        }

        return findMatchingClose(expr, openIndex, index + 1, depth);
    }



}
