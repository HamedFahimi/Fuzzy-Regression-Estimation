package fuzzyregressionestimation;

import java.io.*;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFRow;
import java.io.IOException;

public class TT {

    private static int NumberOfSamples; // = NumberOfRowsOfDataFile
    private static final int numOfIterations = 10;
    private final int numberOfUnknownParameters; //i.e. number of Alpha_i's (Beta_is, Gamma_i's)
    private final int numberOfKnownParameters; //i.e. number of Alpha_i's (Beta_is, Gamma_i's)
    private static int counter;
    private static HSSFWorkbook workbook;
    private static HSSFSheet sheet;
    static float[] fileBestParams;
    private static float sumOfAllbestAlpha_0sSoFar;
    private static float sumOfAllbestAlpha_1sSoFar;
    private static float sumOfAllbestBeta_0sSoFar;
    private static float sumOfAllbestBeta_1sSoFar;
    private static float sumOfAllbestGamma_0sSoFar;
    private static float sumOfAllbestGamma_1sSoFar;
    private static float squareSumOfAllbestAlpha_0sSoFar;
    private static float squareSumOfAllbestAlpha_1sSoFar;
    private static float squareSumOfAllbestBeta_0sSoFar;
    private static float squareSumOfAllbestBeta_1sSoFar;
    private static float squareSumOfAllbestGamma_0sSoFar;
    private static float squareSumOfAllbestGamma_1sSoFar;
    public static final String directoryAsAString = "C:\\Users\\Win10\\Documents\\NetBeansProjects\\fuzzyregressionestimation\\";
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

    public TT(int experimentNumber) {
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
            centralAlpha_0Fixed = 1f;
            centralAlpha_1Fixed = 1f;
            centralAlpha_2Fixed = 1f;
            centralAlpha_3Fixed = 1f;
            centralAlpha_4Fixed = 1f;
            centralAlpha_5Fixed = 1f;
            centralBeta_0Fixed = 0f;
            centralBeta_1Fixed = 0.01f;
            centralBeta_2Fixed = 0.01f;
            centralBeta_3Fixed = 0.01f;
            centralBeta_4Fixed = 0.01f;
            centralBeta_5Fixed = 1f;
            centralGamma_0Fixed = 0f;
            centralGamma_1Fixed = 0.01f;
            centralGamma_2Fixed = 0.01f;
            centralGamma_3Fixed = 0.01f;
            centralGamma_4Fixed = 0.01f;
            centralGamma_5Fixed = 1f;
        }
        //    NUMBER_OF_All_EQUATIONS_In_THE_SYSTEM_TO_BE_SOLVED = Compute_nCr(NumberOfSamples, numberOfUnknownParameters);

    }

    public static void main(String[] args) throws IOException {
        int experimentNumber = 2;
        TT A = new TT(experimentNumber);
        int howManyInstanceToGenerate = 10;
        if (experimentNumber == 1) {
            CreateDataInExcelFile2Parameters L1 = new CreateDataInExcelFile2Parameters();
            for (int q = 0; q < howManyInstanceToGenerate; q++) {
                L1.generateDataOfExperiment1(q, NumberOfSamples);
            }
        } else if (experimentNumber == 2) {
            CreateDataInExcelFile5Parameters L2 = new CreateDataInExcelFile5Parameters();
            for (int q = 0; q < howManyInstanceToGenerate; q++) {
                int numberOfOkSamples = (int) (0.9 * NumberOfSamples);
                L2.generateDataOfExperiment2(q, NumberOfSamples, numberOfOkSamples);
            }
        }
        A.launchExperiment(experimentNumber);
    }

    public void launchExperiment(int experimentNumber) throws FileNotFoundException, IOException {

        String dataName;
        writeOutTheContentsOfTheFirstRowOfTheReportFile();

        for (int r = 0; r < numOfIterations; r++) {
            //  dataName = "sampleData(n=20)-1" + ".xls";
            //   dataName = CreateExcelDataFile.generateData(r);
            dataName = "generatedData" + r + ".xls";
            runAllExperiments(dataName, experimentNumber);
            System.out.println("got rid of instance " + r);
        }

        writeOutTheContentsOfTheLastRowOfTheReportFile();
    }

    public void writeOutTheContentsOfTheFirstRowOfTheReportFile() {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet();
        counter = 0;
        int j = 0;
        HSSFRow row = sheet.createRow((short) counter++);
        for (int i = 0; i < numberOfUnknownParameters; i++) {
            row.createCell(j++).setCellValue("alpha_" + i);
        }
        row.createCell(j++).setCellValue("Number of outliers");
        for (int i = 0; i < numberOfUnknownParameters; i++) {
            row.createCell(j++).setCellValue("beta_" + i);
        }
        row.createCell(j++).setCellValue("Number of outliers");
        for (int i = 0; i < numberOfUnknownParameters; i++) {
            row.createCell(j++).setCellValue("gamma_" + i);
        }
        row.createCell(j++).setCellValue("Number of outliers");
        row.createCell(j).setCellValue("Elapsed time");
    }

    public static void writeOutTheContentsOfTheLastRowOfTheReportFile() throws FileNotFoundException, IOException {
        HSSFRow row;
        row = sheet.createRow((short) counter++);
        row = sheet.createRow((short) counter++);
        row.createCell(0).setCellValue(averageOfAllbestAlpha_0s());
        row.createCell(1).setCellValue(averageOfAllbestAlpha_1s());
        row.createCell(2).setCellValue(" ");
        row.createCell(3).setCellValue(averageOfAllbestBeta_0s());
        row.createCell(4).setCellValue(averageOfAllbestBeta_1s());
        row.createCell(5).setCellValue(" ");
        row.createCell(6).setCellValue(averageOfAllbestGamma_0s());
        row.createCell(7).setCellValue(averageOfAllbestGamma_1s());
        row = sheet.createRow((short) counter++);
        row.createCell(0).setCellValue(squareAverageOfAllbestAlpha_0s());
        row.createCell(1).setCellValue(squareAverageOfAllbestAlpha_1s());
        row.createCell(2).setCellValue(" ");
        row.createCell(3).setCellValue(squareAverageOfAllbestBeta_0s());
        row.createCell(4).setCellValue(squareAverageOfAllbestBeta_1s());
        row.createCell(5).setCellValue(" ");
        row.createCell(6).setCellValue(squareAverageOfAllbestGamma_0s());
        row.createCell(7).setCellValue(squareAverageOfAllbestGamma_1s());
        try (
                FileOutputStream fileOut = new FileOutputStream(directoryAsAString + "report.xls")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    public void runAllExperiments(String dataName, int experimentNumber) {
        //initialize the required data structures
        initializeRequiredVectors X = new initializeRequiredVectors(NumberOfSamples, numberOfKnownParameters, experimentNumber);
        String myString = "report.xls";
        try {
            String filename = directoryAsAString + myString;
            desiredParametersToReport desiredResult = null;
        //    X.developTheMatrixOfData(dataName, experimentNumber);
            X.developTheMatrixOfData2(dataName);

            boolean sortWithInsertionsort = false;
            HSSFRow row = sheet.createRow((short) counter++);
            int indexOfRightvestorInDataFile = 2;
            if (experimentNumber == 2) {
                indexOfRightvestorInDataFile = 6;
            }
            long startTime = System.currentTimeMillis();
            createNewColumnsOfExcelOutputFile(experimentNumber, numberOfUnknownParameters, 1, X, sortWithInsertionsort, desiredResult, row, indexOfRightvestorInDataFile);
            createNewColumnsOfExcelOutputFile(experimentNumber, numberOfUnknownParameters, 2, X, sortWithInsertionsort, desiredResult, row, indexOfRightvestorInDataFile + 1);
            if (experimentNumber == 1) {
                createNewColumnsOfExcelOutputFile(experimentNumber, numberOfUnknownParameters, 3, X, sortWithInsertionsort, desiredResult, row, indexOfRightvestorInDataFile + 2);
            }
            long endTime = System.currentTimeMillis() - startTime;

            //    System.out.println("Time for execution in minutes:" + (float) endTime / 60000);
            row.createCell(9).setCellValue((float) endTime / 60000);
            try (
                    FileOutputStream fileOut = new FileOutputStream(filename)) {
                workbook.write(fileOut);
            }
            workbook.close();
        } catch (IOException e) {
        }

    }

    public static void createNewColumnsOfExcelOutputFile(int experimentNumber, int numberOfUnknownParameters, int whichParameter, initializeRequiredVectors X, boolean sortWithInsertionsort, desiredParametersToReport desiredResult, HSSFRow row, int dimensionOfRightvestorInDataFile) {
        X.solveAllEquationsAndSetup(experimentNumber, numberOfUnknownParameters, whichParameter, dimensionOfRightvestorInDataFile);
        if ((experimentNumber == 1 && dimensionOfRightvestorInDataFile > numberOfUnknownParameters + 1)
                || (experimentNumber == 2 && dimensionOfRightvestorInDataFile > numberOfUnknownParameters)) {
            //dimensionOfRightvestorInDataFile = numberOfUnknownParameters;
            //    X.tmpi(numberOfUnknownParameters, dimensionOfRightvestorInDataFile);
            X.findGlobalMinimumOOptimized(experimentNumber);
        }
        if (experimentNumber == 1) {
            desiredResult = X.findBestParameters(experimentNumber, numberOfUnknownParameters, sortWithInsertionsort, whichParameter);
            float desiredFirstParameter = 0;
            float desiredSecondParameter = 0;

            switch (whichParameter) {
                case 1:
                    desiredFirstParameter = desiredResult.getFirstParameter();
                    row.createCell(0).setCellValue(desiredFirstParameter);
                    sumOfAllbestAlpha_0sSoFar += desiredFirstParameter;
                    squareSumOfAllbestAlpha_0sSoFar += square(desiredFirstParameter - centralAlpha_0Fixed);
                    //working onn.
                    desiredSecondParameter = desiredResult.getSecondParameter();
                    row.createCell(1).setCellValue(desiredSecondParameter);
                    sumOfAllbestAlpha_1sSoFar += desiredSecondParameter;
                    squareSumOfAllbestAlpha_1sSoFar += square(desiredSecondParameter - centralAlpha_1Fixed);
                    row.createCell(2).setCellValue(desiredResult.getNumbOfOutliers());
                    break;
                case 2:
                    desiredFirstParameter = desiredResult.getFirstParameter();
                    row.createCell(3).setCellValue(desiredFirstParameter);
                    sumOfAllbestBeta_0sSoFar += desiredFirstParameter;
                    squareSumOfAllbestBeta_0sSoFar += square(desiredFirstParameter - centralBeta_0Fixed);
                    desiredSecondParameter = desiredResult.getSecondParameter();
                    row.createCell(4).setCellValue(desiredSecondParameter);
                    sumOfAllbestBeta_1sSoFar += desiredSecondParameter;
                    squareSumOfAllbestBeta_1sSoFar += square(desiredSecondParameter - centralBeta_1Fixed);
                    row.createCell(5).setCellValue(desiredResult.getNumbOfOutliers());
                    break;
                case 3:
                    desiredFirstParameter = desiredResult.getFirstParameter();
                    row.createCell(6).setCellValue(desiredFirstParameter);
                    sumOfAllbestGamma_0sSoFar += desiredFirstParameter;
                    squareSumOfAllbestGamma_0sSoFar += square(desiredFirstParameter - centralGamma_0Fixed);
                    desiredSecondParameter = desiredResult.getSecondParameter();
                    row.createCell(7).setCellValue(desiredSecondParameter);
                    sumOfAllbestGamma_1sSoFar += desiredSecondParameter;
                    squareSumOfAllbestGamma_1sSoFar += square(desiredSecondParameter - centralGamma_1Fixed);
                    row.createCell(8).setCellValue(desiredResult.getNumbOfOutliers());
                    break;
                default:
                    break;
            }
        }
        //  System.out.println(sumOfAllbestAlpha_0sSoFar);
        //  System.out.println(sumOfAllbestAlpha_1sSoFar);
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

    public static float averageOfAllbestAlpha_0s() {
        return sumOfAllbestAlpha_0sSoFar / numOfIterations;
    }

    public static float averageOfAllbestAlpha_1s() {
        return sumOfAllbestAlpha_1sSoFar / numOfIterations;
    }

    public static float averageOfAllbestBeta_0s() {
        return sumOfAllbestBeta_0sSoFar / numOfIterations;
    }

    public static float averageOfAllbestBeta_1s() {
        return sumOfAllbestBeta_1sSoFar / numOfIterations;
    }

    public static float averageOfAllbestGamma_0s() {
        return sumOfAllbestGamma_0sSoFar / numOfIterations;
    }

    public static float averageOfAllbestGamma_1s() {
        return sumOfAllbestGamma_1sSoFar / numOfIterations;
    }

    public static float squareAverageOfAllbestAlpha_0s() {
        return squareSumOfAllbestAlpha_0sSoFar / numOfIterations;
    }

    public static float squareAverageOfAllbestAlpha_1s() {
        return squareSumOfAllbestAlpha_1sSoFar / numOfIterations;
    }

    public static float squareAverageOfAllbestBeta_0s() {
        return squareSumOfAllbestBeta_0sSoFar / numOfIterations;
    }

    public static float squareAverageOfAllbestBeta_1s() {
        return squareSumOfAllbestBeta_1sSoFar / numOfIterations;
    }

    public static float squareAverageOfAllbestGamma_0s() {
        return squareSumOfAllbestGamma_0sSoFar / numOfIterations;
    }

    public static float squareAverageOfAllbestGamma_1s() {
        return squareSumOfAllbestGamma_1sSoFar / numOfIterations;
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
}
