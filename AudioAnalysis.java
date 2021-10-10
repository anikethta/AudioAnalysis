import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AudioAnalysis {
    static int n;

    public static void FFT(Complex[] x) {
        /**
         OVERVIEW:
         The following FFT method is the Cooley-Tukey FFT Algorithm
         The main part of this algorithm breaks down the Length-N DFTs (Discrete Fourier Transforms) into Length N/2 DFTs.
         These Length-2 DFTs are later combined to give us our Complex[] output

         This particular algorithm runs on O(Nlog(N)) time. Significantly better than the definition of DFT, which runs on O(n^2) time.
         Limitations: The length of our dataset must be a power of 2.


         For more info:
         https://introcs.cs.princeton.edu/java/97data/InplaceFFT.java.html
         https://www.cs.cmu.edu/afs/andrew/scs/cs/15-463/2001/pub/www/notes/fourier/fourier.pdf

         **/

        // check that length is a power of 2
        int n = x.length;

        if (n % 2 != 0) {
            throw new RuntimeException("This FFT Algorithm only works for values of n which satisfy n = 2^(m) for int m");
        }

        // Bit reversing!!!. Doing this makes the actual FFT algorithm significantly more efficient.
        int shift = 1 + Integer.numberOfLeadingZeros(n);
        for (int k = 0; k < n; k++) {
            int j = Integer.reverse(k) >>> shift;
            if (j > k) {
                Complex temp = x[j];
                x[j] = x[k];
                x[k] = temp;
            }
        }

        for (int L = 2; L <= n; L = L+L) {
            for (int k = 0; k < L/2; k++) {
                double kth = -2 * k * Math.PI / L;
                Complex w = new Complex(Math.cos(kth), Math.sin(kth));
                for (int j = 0; j < n/L; j++) {
                    Complex t = w.multiply(x[j*L + k + L/2]);
                    x[j*L + k + L/2] = x[j*L + k].subtract(t);
                    x[j*L + k]       = x[j*L + k].add(t);
                }
            }
        }
    }

    public static Complex[] RetrieveData() {
        /**Following method retrieves data from audio.in and:
         1. Returns a Complex[] filled with values derived from audio.in
         2. intializes n (the length of the dataset)
         **/
        Complex[] c;
        try(BufferedReader br = new BufferedReader(new FileReader("/Users/anikethtarikonda/Desktop/AudioAnalysis/src/main/audio.in"))) {
            double x_val = 0.0;
            int i = 0;

            n = Integer.parseInt(br.readLine()); // this will read the first line
            c = new Complex[n];
            String line=null;

            while((line = br.readLine()) != null) {
                Double val = Double.parseDouble(line);
                c[i] = new Complex(val, 0);

                i++;
            }

            return c;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // The WriteToOutput() method is purely for debugging and visualizing the frequency data. It is not NEEDED, yet it is helpful
    /**
     public static void WriteToOutput(double[] x) {
     try {
     FileWriter myWriter = new FileWriter("/Users/anikethtarikonda/Desktop/AudioAnalysis/src/main/audio.out");

     for(int i = 0; i < x.length; i++) {
     myWriter.write(x[i] + "\n");
     }

     myWriter.close();
     } catch (IOException e) {
     System.out.println("An error occurred.");
     e.printStackTrace();
     }

     GraphData.graph();
     }
     **/

    public static double[] TransformedFFT(Complex[] x) {
        // What we are interested for the (y-axis) is the magnitude of our data. This method returns a double[] containing magnitudes of the Complex[] input
        FFT(x);
        double[] magnitude = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            magnitude[i] = Math.sqrt(x[i].real*x[i].real + x[i].imaginary*x[i].imaginary);
        }

        return magnitude;
    }

    public static double max(double[] a) {
        // Your standard max value algorithm

        double val = 0;

        for (int i = 0; i < a.length; i++) {
            if (a[i] > val) val = a[i];
        }
        return val;
    }

    public static double MaximumDev(double[] data) {
        // This method takes the average deviation of each data point in a double[] from the max val in double[].
        // This method is used for creating thresholds in our magnitude data
        double max = max(data);
        double fsum = 0;
        double[] devs = new double[data.length];
        for (int i = 0; i < data.length; i++) devs[i] = Math.pow(max - data[i], 2);
        for (int i = 0; i < devs.length; i++) fsum += devs[i];
        return Math.sqrt(fsum/devs.length);
    }

    public static void removeNoise(double[] frequencies) {
        /**
         When looking at our final magnitude data, we can see that there is a lot of irrelevant data. This irrelevant data can be in the form of:
         1. "Noise" in our data. Small data points which are likely not signals and don't provide any significant to the context of our project
         2. Unexpected peaks at high frequencies which again, are likely not intended and just noise

         Any datapoints which get passed into the od
         **/
        for (int i  = 0; i < frequencies.length; i++) {
            double threshold = MaximumDev(frequencies)/3;
            if (frequencies[i] < threshold) {
                frequencies[i] = 0;
            }

            if (i > (frequencies.length/2)) {
                frequencies[i] = 0;
            }
        }
    }

    public static void main(String[] args) {
        Complex[] x = RetrieveData();

        double[] frequencies = TransformedFFT(x);
        removeNoise(frequencies);
    
        /**
        Luckily for us, we dont need to make any major conversion to hertz. Heres why:
        frequency (f) in hertz = i * sr/n
        where i = index of peak
        sr = sample rate of data
        n = the size of our fft data
        
        The sample rate given to us is the first line of our audio.in. Our FFT algorithm does not limit the domain of our time.v.amplitude data,
        therefore our sample rate is equal to the size of our fft data
        
        Because of this, the frequency in hertz will likely always be equal to the index.
        yay!
        
        **/
        for (int i = 1; i < frequencies.length-1; i++) {
            // We are only interested in peaks in the data, isPeak checks to sea if
            boolean isPeak = !(frequencies[i] < frequencies[i+1] || frequencies[i] < frequencies[i-1]);
            if (frequencies[i] != 0.0 && isPeak) {
                System.out.println(i);
            }
        }



        // WriteToOutput() will graph the frequency data. Again, it is not needed for this assignment and is purely used for debugging
        // WriteToOutput(frequencies);
    }

}

