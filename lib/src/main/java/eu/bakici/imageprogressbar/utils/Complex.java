package eu.bakici.imageprogressbar.utils;

import java.math.BigDecimal;
import java.math.MathContext;

/**************************************************************************
 **
 *
 **    Complex Numbers
 **
 **************************************************************************
 **    Axion004
 **    08/20/2015
 **
 **    This class is necessary as a helper class for the calculation of
 **    imaginary numbers. The calculation of Zeta(z) inside AbelMain is in
 **    the form of z = a + i*b.
 **************************************************************************/

public final class Complex {
    /**
     * BigDecimal constant for zero
     */
    final static Complex ZERO = new Complex(BigDecimal.ZERO);
    /**
     * BigDecimal constant for one half
     */
    final static Complex ONEHALF = new Complex(new BigDecimal(0.5));
    /**
     * BigDecimal constant for one
     */
    final static Complex ONE = new Complex(BigDecimal.ONE);
    /**
     * BigDecimal constant for two
     */
    final static Complex TWO = new Complex(new BigDecimal(2.0));
    /**
     * BigDecimal constant for four
     */
    final static Complex FOUR = new Complex(new BigDecimal(4.0));
    /**
     * BigDecimal constant for fifteen
     */
    final static Complex FIFTEEN = new Complex(new BigDecimal(15.0));
    private final BigDecimal re;
    private final BigDecimal im;

    /**
     * Default constructor equivalent to zero
     */
    public Complex() {
        re = BigDecimal.ZERO;
        im = BigDecimal.ZERO;
    }

    /**
     * Constructor with real part only
     *
     * @param x Real part, BigDecimal
     */
    public Complex(BigDecimal x) {
        re = x;
        im = BigDecimal.ZERO;
    }

    /**
     * Constructor with real part only
     *
     * @param x Real part, double
     */
    public Complex(double x) {
        re = new BigDecimal(x);
        im = BigDecimal.ZERO;
    }

    /**
     * Constructor with real and imaginary parts in double format.
     *
     * @param x Real part
     * @param y Imaginary part
     */
    public Complex(double x, double y) {
        re = new BigDecimal(x);
        im = new BigDecimal(y);
    }

    /**
     * Constructor for the complex number z = a + i*b
     *
     * @param re Real part
     * @param im Imaginary part
     */
    public Complex(BigDecimal re, BigDecimal im) {
        this.re = re;
        this.im = im;
    }

    /**
     * Real part of the Complex number
     *
     * @return Re[z] where z = a + i*b.
     */
    public BigDecimal real() {
        return re;
    }

    /**
     * Imaginary part of the Complex number
     *
     * @return Im[z] where z = a + i*b.
     */
    public BigDecimal imag() {
        return im;
    }

    /**
     * Complex conjugate of the Complex number
     * in which the conjugate of z is z-bar.
     *
     * @return z-bar where z = a + i*b and z-bar = a - i*b
     */
    public Complex conjugate() {
        return new Complex(re, im.negate());
    }

    /**
     * Returns the sum of this and the parameter.
     *
     * @param augend the number to add
     * @param mc     the context to use
     * @return this + augend
     */
    public Complex add(Complex augend, MathContext mc) {
        //(a+bi)+(c+di) = (a + c) + (b + d)i
        return new Complex(
                re.add(augend.re, mc),
                im.add(augend.im, mc));
    }

    /**
     * Equivalent to add(augend, MathContext.UNLIMITED)
     *
     * @param augend the number to add
     * @return this + augend
     */
    public Complex add(Complex augend) {
        return add(augend, MathContext.UNLIMITED);
    }

    /**
     * Addition of Complex number and a double.
     *
     * @param d is the number to add.
     * @return z+d where z = a+i*b and d = double
     */
    public Complex add(double d) {
        BigDecimal augend = new BigDecimal(d);
        return new Complex(this.re.add(augend, MathContext.UNLIMITED),
                this.im);
    }

    /**
     * Returns the difference of this and the parameter.
     *
     * @param subtrahend the number to subtract
     * @param mc         the context to use
     * @return this - subtrahend
     */
    public Complex subtract(Complex subtrahend, MathContext mc) {
        //(a+bi)-(c+di) = (a - c) + (b - d)i
        return new Complex(
                re.subtract(subtrahend.re, mc),
                im.subtract(subtrahend.im, mc));
    }

    /**
     * Equivalent to subtract(subtrahend, MathContext.UNLIMITED)
     *
     * @param subtrahend the number to subtract
     * @return this - subtrahend
     */
    public Complex subtract(Complex subtrahend) {
        return subtract(subtrahend, MathContext.UNLIMITED);
    }

    /**
     * Subtraction of Complex number and a double.
     *
     * @param d is the number to subtract.
     * @return z-d where z = a+i*b and d = double
     */
    public Complex subtract(double d) {
        BigDecimal subtrahend = new BigDecimal(d);
        return new Complex(this.re.subtract(subtrahend, MathContext.UNLIMITED),
                this.im);
    }

    /**
     * Returns the product of this and the parameter.
     *
     * @param multiplicand the number to multiply by
     * @param mc           the context to use
     * @return this * multiplicand
     */
    public Complex multiply(Complex multiplicand, MathContext mc) {
        //(a+bi)(c+di) = (ac - bd) + (ad + bc)i
        return new Complex(
                re.multiply(multiplicand.re, mc).subtract(im.multiply
                        (multiplicand.im, mc), mc),
                re.multiply(multiplicand.im, mc).add(im.multiply
                        (multiplicand.re, mc), mc));
    }

    /**
     * Equivalent to multiply(multiplicand, MathContext.UNLIMITED)
     *
     * @param multiplicand the number to multiply by
     * @return this * multiplicand
     */
    public Complex multiply(Complex multiplicand) {
        return multiply(multiplicand, MathContext.UNLIMITED);
    }

    /**
     * Complex multiplication by a double.
     *
     * @param d is the double to multiply by.
     * @return z*d where z = a+i*b and d = double
     */
    public Complex multiply(double d) {
        BigDecimal multiplicand = new BigDecimal(d);
        return new Complex(this.re.multiply(multiplicand, MathContext.UNLIMITED)
                , this.im.multiply(multiplicand, MathContext.UNLIMITED));
    }

    /**
     * Modulus of a Complex number or the distance from the origin in
     * the polar coordinate plane.
     *
     * @return |z| where z = a + i*b.
     */
    public double mod() {
        if (re.doubleValue() != 0.0 || im.doubleValue() != 0.0)
            return Math.sqrt(re.multiply(re).add(im.multiply(im))
                    .doubleValue());
        else
            return 0.0;
    }

    /**
     * Modulus of a Complex number squared
     *
     * @param z = a + i*b
     * @return |z|^2 where z = a + i*b
     */
    public double abs(Complex z) {
        double doubleRe = re.doubleValue();
        double doubleIm = im.doubleValue();
        return doubleRe * doubleRe + doubleIm * doubleIm;
    }

    public Complex divide(Complex divisor) {
        return divide(divisor, MathContext.UNLIMITED);
    }

    /**
     * The absolute value squared.
     *
     * @return The sum of the squares of real and imaginary parts.
     * This is the square of Complex.abs() .
     */
    public BigDecimal norm() {
        return re.multiply(re).add(im.multiply(im));
    }

    /**
     * The inverse of the the Complex number.
     *
     * @param mc amount of precision
     * @return 1/this
     */
    public Complex inverse(MathContext mc) {
        final BigDecimal hyp = norm();
        /* 1/(x+iy)= (x-iy)/(x^2+y^2 */
        return new Complex(re.divide(hyp, mc), im.divide(hyp, mc)
                .negate());
    }

    /**
     * Divide through another BigComplex number.
     *
     * @param oth the other complex number
     * @param mc  amount of precision
     * @return this/other
     */
    public Complex divide(Complex oth, MathContext mc) {
        /* implementation: (x+iy)/(a+ib)= (x+iy)* 1/(a+ib) */
        return multiply(oth.inverse(mc), mc);
    }

    /**
     * Division of Complex number by a double.
     *
     * @param d is the double to divide
     * @return new Complex number z/d where z = a+i*b
     */
    public Complex divide(double d) {
        BigDecimal divisor = new BigDecimal(d);
        return new Complex(this.re.divide(divisor, MathContext.UNLIMITED),
                this.im.divide(divisor, MathContext.UNLIMITED));
    }

    /**
     * Exponential of a complex number (z is unchanged).
     * <br> e^(a+i*b) = e^a * e^(i*b) = e^a * (cos(b) + i*sin(b))
     *
     * @return exp(z) where z = a+i*b
     */
    public Complex exp() {
        return new Complex(Math.exp(re.doubleValue()) * Math.cos(im.
                doubleValue()), Math.exp(re.doubleValue()) *
                Math.sin(im.doubleValue()));
    }

    /**
     * The Argument of a Complex number or the angle in radians
     * with respect to polar coordinates.
     * <br> Tan(theta) = b / a, theta = Arctan(b / a)
     * <br> a is the real part on the horizontal axis
     * <br> b is the imaginary part of the vertical axis
     *
     * @return arg(z) where z = a+i*b.
     */
    public double arg() {
        return Math.atan2(im.doubleValue(), re.doubleValue());
    }

    /**
     * The log or principal branch of a Complex number (z is unchanged).
     * <br> Log(a+i*b) = ln|a+i*b| + i*Arg(z) = ln(sqrt(a^2+b^2))
     * + i*Arg(z) = ln (mod(z)) + i*Arctan(b/a)
     *
     * @return log(z) where z = a+i*b
     */
    public Complex log() {
        return new Complex(Math.log(this.mod()), this.arg());
    }

    /**
     * The square root of a Complex number (z is unchanged).
     * Returns the principal branch of the square root.
     * <br> z = e^(i*theta) = r*cos(theta) + i*r*sin(theta)
     * <br> r = sqrt(a^2+b^2)
     * <br> cos(theta) = a / r, sin(theta) = b / r
     * <br> By De Moivre's Theorem, sqrt(z) = sqrt(a+i*b) =
     * e^(i*theta / 2) = r(cos(theta/2) + i*sin(theta/2))
     *
     * @return sqrt(z) where z = a+i*b
     */
    public Complex sqrt() {
        double r = this.mod();
        double halfTheta = this.arg() / 2;
        return new Complex(Math.sqrt(r) * Math.cos(halfTheta), Math.sqrt(r) *
                Math.sin(halfTheta));
    }

    /**
     * The real cosh function for Complex numbers.
     * <br> cosh(theta) = (e^(theta) + e^(-theta)) / 2
     *
     * @return cosh(theta)
     */
    private double cosh(double theta) {
        return (Math.exp(theta) + Math.exp(-theta)) / 2;
    }

    /**
     * The real sinh function for Complex numbers.
     * <br> sinh(theta) = (e^(theta) - e^(-theta)) / 2
     *
     * @return sinh(theta)
     */
    private double sinh(double theta) {
        return (Math.exp(theta) - Math.exp(-theta)) / 2;
    }

    /**
     * The sin function for the Complex number (z is unchanged).
     * <br> sin(a+i*b) = cosh(b)*sin(a) + i*(sinh(b)*cos(a))
     *
     * @return sin(z) where z = a+i*b
     */
    public Complex sin() {
        return new Complex(cosh(im.doubleValue()) * Math.sin(re.doubleValue()),
                sinh(im.doubleValue()) * Math.cos(re.doubleValue()));
    }

    /**
     * The cos function for the Complex number (z is unchanged).
     * <br> cos(a +i*b) = cosh(b)*cos(a) + i*(-sinh(b)*sin(a))
     *
     * @return cos(z) where z = a+i*b
     */
    public Complex cos() {
        return new Complex(cosh(im.doubleValue()) * Math.cos(re.doubleValue()),
                -sinh(im.doubleValue()) * Math.sin(re.doubleValue()));
    }

    /**
     * The hyperbolic sin of the Complex number (z is unchanged).
     * <br> sinh(a+i*b) = sinh(a)*cos(b) + i*(cosh(a)*sin(b))
     *
     * @return sinh(z) where z = a+i*b
     */
    public Complex sinh() {
        return new Complex(sinh(re.doubleValue()) * Math.cos(im.doubleValue()),
                cosh(re.doubleValue()) * Math.sin(im.doubleValue()));
    }

    /**
     * The hyperbolic cosine of the Complex number (z is unchanged).
     * <br> cosh(a+i*b) = cosh(a)*cos(b) + i*(sinh(a)*sin(b))
     *
     * @return cosh(z) where z = a+i*b
     */
    public Complex cosh() {
        return new Complex(cosh(re.doubleValue()) * Math.cos(im.doubleValue()),
                sinh(re.doubleValue()) * Math.sin(im.doubleValue()));
    }

    /**
     * The tan of the Complex number (z is unchanged).
     * <br> tan (a+i*b) = sin(a+i*b) / cos(a+i*b)
     *
     * @return tan(z) where z = a+i*b
     */
    public Complex tan() {
        return (this.sin()).divide(this.cos());
    }

    /**
     * The arctan of the Complex number (z is unchanged).
     * <br> tan^(-1)(a+i*b) = 1/2 i*(log(1-i*(a+b*i))-log(1+i*(a+b*i))) =
     * <br> -1/2 i*(log(i*a - b+1)-log(-i*a + b+1))
     *
     * @return arctan(z) where z = a+i*b
     */
    public Complex atan() {
        Complex ima = new Complex(0.0, -1.0);    //multiply by negative i
        Complex num = new Complex(this.re.doubleValue(), this.im.doubleValue()
                - 1.0);
        Complex den = new Complex(this.re.negate().doubleValue(), this.im
                .negate().doubleValue() - 1.0);
        Complex two = new Complex(2.0, 0.0);    // divide by 2
        return ima.multiply(num.divide(den).log()).divide(two);
    }

    /**
     * The Math.pow equivalent of two Complex numbers.
     *
     * @param z - the complex base in the form z = a + i*b
     * @return z^y where z = a + i*b and y = c + i*d
     */
    public Complex pow(Complex z) {
        Complex a = z.multiply(this.log(), MathContext.UNLIMITED);
        return a.exp();
    }

    /**
     * The Math.pow equivalent of a Complex number to the power
     * of a double.
     *
     * @param d - the double to be taken as the power.
     * @return z^d where z = a + i*b and d = double
     */
    public Complex pow(double d) {
        Complex a = (this.log()).multiply(d);
        return a.exp();
    }

    /**
     * Override the .toString() method to generate complex numbers, the
     * string representation is now a literal Complex number.
     *
     * @return a+i*b, a-i*b, a, or i*b as desired.
     */
    public String toString() {
        if (re.doubleValue() != 0.0 && im.doubleValue() > 0.0) {
            return re + " + " + im + "*i";
        }
        if (re.doubleValue() != 0.0 && im.doubleValue() < 0.0) {
            return re + " - " + (-im.doubleValue()) + "*i";
        }
        if (im.doubleValue() == 0.0) {
            return String.valueOf(re);
        }
        if (re.doubleValue() == 0.0) {
            return im + "*i";
        }
        return re + " + i*" + im;
    }
}
