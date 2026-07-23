/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyregressionestimation;
import java.io.IOException;

/**
 *
 * @author HAMED FAHIMI
 */
public class desiredParametersToReport2 {
    
    private final double[] params;

    public desiredParametersToReport2(int n) {
        params = new double[n];
    }

    public void setParam(int index, double value) {
        params[index] = value;
    }

    public double getParam(int index) {
        return params[index];
    }

    public void printParams() {
        for (int i = 0; i < params.length; i++) {
            System.out.println("Param " + i + " = " + params[i]);
        }
    }

    public static void main(String[] args) throws IOException {
        desiredParametersToReport2 a = new desiredParametersToReport2(2);
        a.setParam(0, 5.4);
        a.setParam(1, 8.1);
        a.printParams();

        desiredParametersToReport2 b = new desiredParametersToReport2(6);
        b.setParam(3, 10.5);
        b.printParams();
    }

}
