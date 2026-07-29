package fuzzyregressionestimation;

import static fuzzyregressionestimation.initializeRequiredVectors.establishTheSystemOfEquationsAndSolve2;
import java.io.*;
import java.io.IOException;

public class TT2 {

    private static int NumberOfSamples; // = NumberOfRowsOfDataFile
    private final int numberOfUnknownParameters; //i.e. number of Alpha_i's (Beta_is, Gamma_i's)
    private final int numberOfKnownParameters;
    private static final int NUMBER_OF_GENERATED_INSTANCES = 10;
    static float[] fileBestParams;
    public static float centralAlpha_0Fixed;
    public static float centralAlpha_1Fixed;
    public static float centralAlpha_2Fixed;
    public static float centralAlpha_3Fixed;
    public static float centralAlpha_4Fixed;
    public static float centralAlpha_5Fixed;
    public static float centralBeta_0Fixed;
    public static float centralBeta_1Fixed;
    public static float centralBeta_2Fixed;
    public static float centralBeta_3Fixed;
    public static float centralBeta_4Fixed;
    public static float centralBeta_5Fixed;
    public static float centralGamma_0Fixed;
    public static float centralGamma_1Fixed;
    public static float centralGamma_2Fixed;
    public static float centralGamma_3Fixed;
    public static float centralGamma_4Fixed;
    public static float centralGamma_5Fixed;

    public TT2(int experimentNumber) {
        if (experimentNumber == 1) {
            NumberOfSamples = 20;
            numberOfKnownParameters = 5;
            numberOfUnknownParameters = 2;
            centralAlpha_0Fixed = 10f;
            centralAlpha_1Fixed = 3f;
            centralBeta_0Fixed = -15f;
            centralBeta_1Fixed = 0.1f;
            centralGamma_0Fixed = -40f;
            centralGamma_1Fixed = 0.2f;
        } else {
            NumberOfSamples = 10;
            numberOfKnownParameters = 9;
            numberOfUnknownParameters = 6;
            centralAlpha_0Fixed = 1.0f;
            centralAlpha_1Fixed = 1.0f;
            centralAlpha_2Fixed = 1.0f;
            centralAlpha_3Fixed = 1.0f;
            centralAlpha_4Fixed = 1.0f;
            centralAlpha_5Fixed = 1.0f;
            centralBeta_0Fixed = 0.0f;
            centralBeta_1Fixed = 0.01f;
            centralBeta_2Fixed = 0.01f;
            centralBeta_3Fixed = 0.01f;
            centralBeta_4Fixed = 0.01f;
            centralBeta_5Fixed = 1.0f;
            centralGamma_0Fixed = centralBeta_0Fixed;
            centralGamma_1Fixed = centralBeta_1Fixed;
            centralGamma_2Fixed = centralBeta_2Fixed;
            centralGamma_3Fixed = centralBeta_3Fixed;
            centralGamma_4Fixed = centralBeta_4Fixed;
            centralGamma_5Fixed = centralBeta_5Fixed;
        }
    }

    public static void main(String[] args) throws IOException {
        int experimentNumber = 1;
        TT2 A = new TT2(experimentNumber);
       generateDataDependingOnTheExperiment(experimentNumber);
        A.launchTheEntireExperiment(experimentNumber);
    }

    public void launchTheEntireExperiment(int experimentNumber) throws FileNotFoundException, IOException {

        String dataName;
        for (int r = 0; r < NUMBER_OF_GENERATED_INSTANCES; r++) {
            dataName = "generatedData" + r + ".xls";
            runExperimentsOverThisData(dataName, experimentNumber);
            System.out.println("got rid of running experiments on instance " + r);
        }
    }

    public void runExperimentsOverThisData(String dataName, int experimentNumber) {
        //initialize the required data structures
        initializeRequiredVectors X = new initializeRequiredVectors(NumberOfSamples, numberOfKnownParameters, experimentNumber);
        try {
            if (experimentNumber == 1) {
                X.developTheMatrixOfData(dataName, experimentNumber);
            } else {
                X.developTheMatrixOfData3(dataName);
            }
            int indexOfRightvestorInDataFile = 2;
            if (experimentNumber == 2) {
                indexOfRightvestorInDataFile = 6;
            }
            createNewColumnsOfExcelOutputFile(experimentNumber, numberOfUnknownParameters, 1, X, indexOfRightvestorInDataFile);
            createNewColumnsOfExcelOutputFile(experimentNumber, numberOfUnknownParameters, 2, X, indexOfRightvestorInDataFile + 1);
            if (experimentNumber == 1) {
                createNewColumnsOfExcelOutputFile(experimentNumber, numberOfUnknownParameters, 3, X, indexOfRightvestorInDataFile + 2);
            }
        } catch (IOException e) {
        }

    }

    public static void createNewColumnsOfExcelOutputFile(int experimentNumber, int numberOfUnknownParameters, int whichParameter, initializeRequiredVectors X, int dimensionOfRightvestorInDataFile) {
        //    List<float[]> allOneOfAlphaOrBetaOrGammaSolutions = establishTheSystemOfEquationsAndSolve(experimentNumber, numberOfUnknownParameters, dimensionOfRightvestorInDataFile);
        //    organizeAllSolutionsInASortedList(experimentNumber, numberOfUnknownParameters, allOneOfAlphaOrBetaOrGammaSolutions, dimensionOfRightvestorInDataFile);
        establishTheSystemOfEquationsAndSolve2(experimentNumber, numberOfUnknownParameters, dimensionOfRightvestorInDataFile);
        if ((experimentNumber == 1 && dimensionOfRightvestorInDataFile > numberOfUnknownParameters + 1)
                || (experimentNumber == 2 && dimensionOfRightvestorInDataFile > numberOfUnknownParameters)) {
            //dimensionOfRightvestorInDataFile = numberOfUnknownParameters;
            //    X.tmpi(numberOfUnknownParameters, dimensionOfRightvestorInDataFile);
            //   X.findGlobalMinimumOOptimized(experimentNumber);
            X.findGlobalMinimumOOptimized2(experimentNumber);

        }
    }

    // Function to compute nCr iteratively
    public final int Compute_nCr(int n, int r) {
        if (r > n - r) {
            r = n - r;
        }

// Use symmetry property nCr = nC(n-r)
        int result = 1;
        for (int i = 0; i < r; i++) {
            result = result * (n - i) / (i + 1);

        }
        return result;
    }

    public static float square(float h) {
        return h * h;
    }

    public static void runMasterOptimization(int totalFiles) throws IOException {
        java.util.List<float[]> allFileResults = new java.util.ArrayList<>();

        // 1. Automatically loop from 0 up to your total count (e.g., 9 for 10 files)
        for (int i = 0; i < totalFiles; i++) {
            // Construct the file name dynamically matching your directory layout
            String file = "generatedData " + i + ".xls";

            System.out.println("Loading and processing: " + file);

            // ==============================================================
            // CRITICAL STEP: Update your data structures for this specific file
            // ==============================================================
            // matrixOfData = yourDataLoader.readMatrix(file);
            // alphaSortedParameters = yourDataLoader.readAlphas(file);
            // betaSortedParameters = yourDataLoader.readBetas(file);
            // gammaSortedParameters = yourDataLoader.readGammas(file);
            // Run the window scanning calculation
            //    float[] fileBestParams = findGlobalMinimumOOptimized();
            if (fileBestParams != null) {
                allFileResults.add(fileBestParams);
            } else {
                System.out.println("-> Warning: No valid optimal window solution met criteria for " + file);
            }
        }

        int totalFilesProcessed = allFileResults.size();
        if (totalFilesProcessed == 0) {
            System.out.println("Error: No data matrices were processed successfully.");
            return;
        }

        double[] columnSums = new double[6];
        double[] columnSumSquares = new double[6];

        String filename = "OptimizationResults.csv";
        try (java.io.FileWriter fw = new java.io.FileWriter(filename); java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {

            // Column Headers
            pw.println("alpha0,alpha1,beta0,beta1,gamma0,gamma1");

            // Write optimization rows
            for (float[] rowParams : allFileResults) {
                pw.printf("%f,%f,%f,%f,%f,%f%n",
                        rowParams[0], rowParams[1], rowParams[2], rowParams[3], rowParams[4], rowParams[5]);

                for (int col = 0; col < 6; col++) {
                    columnSums[col] += rowParams[col];
                    columnSumSquares[col] += Math.pow(rowParams[col], 2);
                }
            }

            // Leave an exact blank row block separating raw parameters from summary metrics
            pw.println(",,,,,");

            // Calculate and print Averages row
            pw.print("AVERAGE");
            for (int col = 0; col < 6; col++) {
                double avg = columnSums[col] / totalFilesProcessed;
                pw.printf(",%f", avg);
            }
            pw.println();

            // Calculate and print MSE row
            pw.print("MSE");
            for (int col = 0; col < 6; col++) {
                double mse = columnSumSquares[col] / totalFilesProcessed;
                pw.printf(",%f", mse);
            }
            pw.println();

            System.out.println("Batch processing done. Complete matrix saved to: " + filename);

        } catch (java.io.IOException e) {
            System.err.println("Fatal Error writing tracking results matrix to drive.");
            e.printStackTrace();
        }
    }

    public static void generateDataDependingOnTheExperiment(int experimentNumber) {
        if (experimentNumber == 1) {
            CreateDataInExcelFile2Parameters L1 = new CreateDataInExcelFile2Parameters();
            for (int q = 0; q < NUMBER_OF_GENERATED_INSTANCES; q++) {
                L1.generateDataOfExperiment1(q, NumberOfSamples);
            }
        } else if (experimentNumber == 2) {
            CreateDataInExcelFile5Parameters L2 = new CreateDataInExcelFile5Parameters();
            for (int q = 0; q < NUMBER_OF_GENERATED_INSTANCES; q++) {
                int numberOfOkSamples = (int) (0.9 * NumberOfSamples);
                L2.generateDataOfExperiment2(q, NumberOfSamples, numberOfOkSamples);
            }
        }
    }
}
