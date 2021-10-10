
public class Complex {
    public final double real;
    public final double imaginary;

    Complex() {
        real = 0;
        imaginary = 0;
    }

    public Complex(double r, double i) {
        this.real = r;
        this.imaginary = i;
    }


    final public Complex add(Complex z) {
        return new Complex(this.real + z.real, this.imaginary + z.imaginary);
    }

    final public Complex subtract(Complex z) {
        return new Complex(this.real - z.real, this.imaginary - z.imaginary);
    }

    final public Complex multiply(Complex z) {
        return new Complex(this.real*z.real - this.imaginary*z.imaginary, this.real*z.real + this.imaginary*z.imaginary);
    }
}
