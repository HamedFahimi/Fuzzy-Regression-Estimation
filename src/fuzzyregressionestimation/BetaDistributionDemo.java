/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fuzzyregressionestimation;

import org.apache.commons.math3.distribution.BetaDistribution;

public class BetaDistributionDemo {

    public static void main(String[] args) {

        // Shape parameters
        double alpha = 2.0;
        double beta = 5.0;

        BetaDistribution dist =
                new BetaDistribution(alpha, beta);

        double x = 0.4;   // must be in [0,1]

        // PDF
        System.out.println("PDF = " + dist.density(x));

        // CDF
        System.out.println("CDF = " + dist.cumulativeProbability(x));


        // Quantile / inverse CDF
        System.out.println("Quantile (0.95) = "
                + dist.inverseCumulativeProbability(0.95));

        // Mean and variance
        System.out.println("Mean = " + dist.getNumericalMean());
        System.out.println("Variance = " + dist.getNumericalVariance());
    }
}

