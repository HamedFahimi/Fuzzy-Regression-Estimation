/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyregressionestimation;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;



public class ChiSquaredExample {

    public static void main(String[] args) {
        double degreesOfFreedom = 10;
        double noncentralParameter = 2.0;
        ChiSquaredDistribution chiSquared = new ChiSquaredDistribution(degreesOfFreedom, noncentralParameter);
        double value = chiSquared.sample();
        System.out.println("Sampled value: " + value);
    }
}
