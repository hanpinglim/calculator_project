package FDM;

public class Calculator implements ICalculator{

    @Override
    public double evaluate(String number) {
        // Removing whitespaces
        String expr = number.replace(" ", "");
        return Double.parseDouble(expr);
    }
}
