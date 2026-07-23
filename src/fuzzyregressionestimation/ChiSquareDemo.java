/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyregressionestimation;


import org.apache.commons.math3.distribution.ChiSquaredDistribution;

public class ChiSquareDemo {

    public static void main(String[] args) {

        // degrees of freedom
        double df = 4;

        ChiSquaredDistribution chi =
                new ChiSquaredDistribution(df);

        double x = 5.0;

        System.out.println("PDF: " + chi.density(x));
        System.out.println("CDF: " + chi.cumulativeProbability(x));
        System.out.println("Critical value (0.95): "
                + chi.inverseCumulativeProbability(0.95));
    }
}
