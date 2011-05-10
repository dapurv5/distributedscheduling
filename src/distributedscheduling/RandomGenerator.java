package distributedscheduling;

import java.util.Random;
import static java.lang.System.out;

/**
 *
 * @author apurv verma
 */
public class RandomGenerator {

    Random r;

    /*lambda denotes number of arrivals per unit time interval. */
    double lambda;

    public RandomGenerator(double lambda){
        r=new Random();
        this.lambda=lambda;
    }

    public int nextPoisson() {

        double L = Math.exp(-lambda);
        double p = 1.0;
        int k = 0;

        do {
        k++;
        p *= r.nextDouble();
        } while (p > L);

        return k - 1;
    }

    /*
    public int getBinomial(int n, double p) {
        int x = 0;
        for(int i = 0; i < n; i++) {
        if(Math.random() < p)
          x++;
        }
        return x;
    }
    */


    public static void main(String...args){
        int lambda=2;
        RandomGenerator rg=new RandomGenerator(lambda);

        int sum=0;
        double n_trials=100.0;
        for(int i=0;i<n_trials;i++){
            int a=rg.nextPoisson();
            sum+=a;
            out.print(a);
        }
        out.println("\nsum "+sum);
        out.println("avg: "+(double)sum/n_trials+ " -> lambda: "+lambda);
    }

}
