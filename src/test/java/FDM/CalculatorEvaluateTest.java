package FDM;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.*;


@TestMethodOrder(OrderAnnotation.class)
class CalculatorEvaluateTest {

    private Calculator calculator;

    // Detecting the accuracy of answers to the 1000th
    private static final double EPS = 0.00005;

    @BeforeEach
    void setup() {
        calculator = new Calculator();
    }

    private void assert_eval(String expression, double expected) {
        assertEquals(expected, calculator.evaluate(expression), EPS, "Expression: " + expression);
    }

    // -------------------------
    // Phase 1: Single numbers + whitespace
    // -------------------------

    @Test @Order(1)
    void evaluates_single_integer() {
        assert_eval("5", 5.0);
    }

    @Test @Order(2)
    void evaluates_single_decimal() {
        assert_eval("5.25", 5.25);
    }

    @Test @Order(3)
    void evaluates_integer_with_whitespace() {
        assert_eval("   7   ", 7.0);
    }

    @Test @Order(4)
    void evaluates_expression_with_internal_whitespace_removed() {
        assert_eval("  3 +  4 ", 7.0);
    }

    @Test @Order(5)
    void evaluates_decimal_with_whitespace() {
        assert_eval("  3.5  +  0.5 ", 4.0);
    }

    // -------------------------
    // Phase 1: + and - basics
    // -------------------------

    @Test @Order(6)
    void evaluates_simple_addition() {
        assert_eval("3+4", 7.0);
    }

    @Test @Order(7)
    void evaluates_simple_subtraction() {
        assert_eval("7-2", 5.0);
    }

    @Test @Order(8)
    void evaluates_addition_with_decimals() {
        assert_eval("1.2+3.4", 4.6);
    }

    @Test @Order(9)
    void evaluates_subtraction_with_decimals() {
        assert_eval("5.5-2.25", 3.25);
    }

    @Test @Order(10)
    void evaluates_left_to_right_for_same_precedence_plus_minus_1() {
        assert_eval("10-3+2", 9.0);
    }

    @Test @Order(11)
    void evaluates_left_to_right_for_same_precedence_plus_minus_2() {
        assert_eval("10+3-2", 11.0);
    }

    // -------------------------
    // Phase 1: * and / basics
    // -------------------------

    @Test @Order(12)
    void evaluates_simple_multiplication() {
        assert_eval("4*5", 20.0);
    }

    @Test @Order(13)
    void evaluates_simple_division() {
        assert_eval("8/2", 4.0);
    }

    @Test @Order(14)
    void evaluates_multiplication_with_decimals() {
        assert_eval("2.5*4", 10.0);
    }

    @Test @Order(15)
    void evaluates_division_with_decimals() {
        assert_eval("7.5/2.5", 3.0);
    }

    @Test @Order(16)
    void evaluates_left_to_right_for_same_precedence_mul_div_1() {
        assert_eval("20/5*2", 8.0);
    }

    @Test @Order(17)
    void evaluates_left_to_right_for_same_precedence_mul_div_2() {
        assert_eval("20*5/2", 50.0);
    }

    // -------------------------
    // Phase 1: Order of operations (*/ before +-)
    // -------------------------

    @Test @Order(18)
    void respects_precedence_multiplication_before_addition() {
        assert_eval("2+3*4", 14.0);
    }

    @Test @Order(19)
    void respects_precedence_division_before_subtraction() {
        assert_eval("10-8/2", 6.0);
    }

    @Test @Order(20)
    void respects_precedence_mixed_operators() {
        assert_eval("2+3*4-6/2", 11.0);
    }

    @Test @Order(21)
    void respects_precedence_with_multiple_terms() {
        assert_eval("1+2*3+4*5", 27.0);
    }

    @Test @Order(22)
    void respects_precedence_with_multiple_divisions() {
        assert_eval("100/5/2+1", 11.0);
    }

    // -------------------------
    // Phase 1: Unary / sign combos from marking scheme
    // Handle: 4*-5, 1- -2, 2++4, +-, -+.
    // -------------------------

    @Test @Order(23)
    void handles_negative_number_after_multiplication() {
        assert_eval("4*-5", -20.0);
    }

    @Test @Order(24)
    void handles_double_negative_after_minus_operator() {
        assert_eval("1--2", 3.0);
    }

    @Test @Order(25)
    void handles_plus_plus_as_unary_plus() {
        assert_eval("2++4", 6.0);
    }

    @Test @Order(26)
    void handles_plus_minus_as_unary_negative() {
        assert_eval("2+-4", -2.0);
    }

    @Test @Order(27)
    void handles_minus_plus_as_unary_positive() {
        assert_eval("2-+4", -2.0);
    }

    @Test @Order(28)
    void handles_leading_unary_plus() {
        assert_eval("+5", 5.0);
    }

    @Test @Order(29)
    void handles_leading_unary_minus() {
        assert_eval("-5", -5.0);
    }

    @Test @Order(30)
    void handles_leading_plus_minus_combo() {
        assert_eval("+-5", -5.0);
    }

    @Test @Order(31)
    void handles_leading_minus_plus_combo() {
        assert_eval("-+5", -5.0);
    }

    @Test @Order(32)
    void handles_unary_minus_on_decimal() {
        assert_eval("-2.5", -2.5);
    }

    @Test @Order(33)
    void handles_unary_minus_after_opening_context() {
        assert_eval("3*-2", -6.0);
    }

    @Test @Order(34)
    void handles_unary_plus_after_opening_context() {
        assert_eval("3*+2", 6.0);
    }

    // -------------------------
    // Phase 2: Brackets (80–84 / 85–94 with nesting)
    // -------------------------

    @Test @Order(35)
    void evaluates_simple_parentheses_override_precedence() {
        assert_eval("(2+3)*4", 20.0);
    }

    @Test @Order(36)
    void evaluates_parentheses_on_right_side() {
        assert_eval("2*(3+4)", 14.0);
    }

    @Test @Order(37)
    void evaluates_parentheses_inside_expression() {
        assert_eval("10-(2+3)", 5.0);
    }

    @Test @Order(38)
    void evaluates_parentheses_with_whitespace() {
        assert_eval(" ( 2 + 3 ) * 4 ", 20.0);
    }

    @Test @Order(39)
    void evaluates_nested_parentheses_level_2() {
        assert_eval("1+(2*3)", 7.0);
    }

    @Test @Order(40)
    void evaluates_nested_parentheses_level_3() {
        assert_eval("1+(2*(3+4))", 15.0);
    }

    @Test @Order(41)
    void evaluates_nested_parentheses_multiple() {
        assert_eval("(1+2)*(3+4)", 21.0);
    }

    @Test @Order(42)
    void evaluates_nested_parentheses_deep() {
        assert_eval("((2+3)*(4+5))", 45.0);
    }

    @Test @Order(43)
    void evaluates_parentheses_with_unary_minus_inside() {
        assert_eval("2*(-3+4)", 2.0);
    }

    @Test @Order(44)
    void evaluates_parentheses_with_unary_minus_outside() {
        assert_eval("-(3+4)", -7.0);
    }

    @Test @Order(45)
    void evaluates_parentheses_with_mixed_ops() {
        assert_eval("(2+3*4)-(6/2)", 11.0);
    }

    @Test @Order(46)
    void evaluates_multiple_nested_parentheses() {
        assert_eval("((1+2)+((3+4)*2))", 17.0);
    }

    // -------------------------
    // Phase 2: Integer exponents (positive + negative whole numbers)
    // Must be infix, no Math.pow
    // -------------------------

    @Test @Order(47)
    void evaluates_simple_positive_integer_exponent() {
        assert_eval("2^3", 8.0);
    }

    @Test @Order(48)
    void evaluates_exponent_precedence_over_multiplication() {
        assert_eval("2*3^2", 18.0);
    }

    @Test @Order(49)
    void evaluates_exponent_precedence_over_addition() {
        assert_eval("2+3^2", 11.0);
    }

    @Test @Order(50)
    void evaluates_parentheses_with_exponent() {
        assert_eval("(2+3)^2", 25.0);
    }

    @Test @Order(51)
    void evaluates_negative_integer_exponent() {
        assert_eval("2^-3", 0.125);
    }

    @Test @Order(52)
    void evaluates_negative_integer_exponent_with_parentheses_base() {
        assert_eval("(2+1)^-2", 1.0 / 9.0);
    }

    @Test @Order(53)
    void evaluates_exponent_with_unary_negative_base_in_parentheses_even_power() {
        assert_eval("(-2)^4", 16.0);
    }

    @Test @Order(54)
    void evaluates_exponent_with_unary_negative_base_in_parentheses_odd_power() {
        assert_eval("(-2)^3", -8.0);
    }

    @Test @Order(55)
    void evaluates_exponent_of_one() {
        assert_eval("7^1", 7.0);
    }

    @Test @Order(56)
    void evaluates_exponent_of_zero() {
        assert_eval("7^0", 1.0);
    }

    @Test @Order(57)
    void evaluates_exponent_then_division() {
        assert_eval("9^2/3", 27.0);
    }

    @Test @Order(58)
    void evaluates_division_inside_parentheses_as_exponent() {
        assert_eval("2^(6/3)", 4.0);
    }

    // -------------------------
    // Phase 3: Fractional exponents (95–100)
    // Examples: 3^2/3 (interpretation should be 3^(2/3) via precedence/parentheses)
    // We'll include explicit parentheses to avoid ambiguity.
    // -------------------------

    @Test @Order(59)
    void evaluates_square_root_as_fractional_exponent() {
        assert_eval("9^(1/2)", 3.0);
    }

    @Test @Order(60)
    void evaluates_cube_root_as_fractional_exponent() {
        assert_eval("8^(1/3)", 2.0);
    }

    @Test @Order(61)
    void evaluates_fractional_exponent_two_thirds() {
        // 3^(2/3) = cbrt(9) ≈ 2.0800838230519
        assert_eval("3^(2/3)", 2.0800838230519);
    }

    @Test @Order(62)
    void evaluates_fractional_exponent_one_fourth() {
        assert_eval("16^(1/4)", 2.0);
    }

    @Test @Order(63)
    void evaluates_fractional_exponent_with_parentheses_base() {
        assert_eval("(27)^(2/3)", 9.0); // (cbrt 27)^2 = 3^2 = 9
    }

    // -------------------------
    // Extra coverage / robustness (still valid inputs)
    // These help you reach 65+ tests and strengthen confidence.
    // -------------------------

    @Test @Order(64)
    void evaluates_longer_expression_without_parentheses() {
        assert_eval("1+2*3-4/2+5*2", 15.0);
    }

    @Test @Order(65)
    void evaluates_longer_expression_with_parentheses() {
        assert_eval("1+(2*3)-(4/2)+(5*2)", 15.0);
    }

    @Test @Order(66)
    void evaluates_nested_parentheses_with_exponent() {
        assert_eval("((1+1)*(2+1))^2", 36.0);
    }

    @Test @Order(67)
    void evaluates_unary_minus_on_parenthesized_power() {
        assert_eval("-(2^3)", -8.0);
    }

    @Test @Order(68)
    void evaluates_power_of_parenthesized_negative_base_then_addition() {
        assert_eval("(-3)^2+1", 10.0);
    }

    @Test @Order(69)
    void evaluates_multiple_parentheses_layers_with_signs() {
        assert_eval("-(1+(-2+3))*2", -4.0);
    }

    @Test @Order(70)
    void evaluates_fractional_exponent_with_whitespace() {
        assert_eval("  9  ^ (  1 / 2 ) ", 3.0);
    }
}
