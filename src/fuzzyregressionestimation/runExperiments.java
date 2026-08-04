package fuzzyregressionestimation;

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class runExperiments {

    private final int numberOfSamples; // = NumberOfRowsOfDataFile
    private final int numberOfUnknownParameters; //i.e. number of Alpha_i's (Beta_is, Gamma_i's)
    private final int numberOfKnownParameters;
    private final int NUMBER_OF_GENERATED_INSTANCES;
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
    private static float[] centralAlphas;
    private static float[] centralBetas;
    private static float[] centralGammas;
    private static List<List<Float>> alphaSortedParameters;
    private static List<List<Float>> betaSortedParameters;
    private static List<List<Float>> gammaSortedParameters;
    private static final java.util.List<float[]> allFileResults = new java.util.ArrayList<>();
    static float[][] matrixOfData;

    public runExperiments(int experimentNumber, int n, int NUMBER_OF_GENERATED_INSTANCES, int numberOfKnownParameters, int numberOfUnknownParameters) {
        numberOfSamples = n;
        this.NUMBER_OF_GENERATED_INSTANCES = NUMBER_OF_GENERATED_INSTANCES;
        this.numberOfKnownParameters = numberOfKnownParameters;
        this.numberOfUnknownParameters = numberOfUnknownParameters;
        if (experimentNumber == 1) {
            matrixOfData = new float[n][numberOfKnownParameters];
            centralAlphas = new float[numberOfUnknownParameters];
            centralBetas = new float[numberOfUnknownParameters];
            centralGammas = new float[numberOfUnknownParameters];
            centralAlphas[0] = 10.0f;
            centralAlphas[1] = 3.0f;
            centralBetas[0] = -15.0f;
            centralBetas[1] = 0.1f;
            centralGammas[0] = -40.0f;
            centralGammas[1] = 0.2f;
        } else if (experimentNumber == 2) {
            matrixOfData = new float[n][numberOfKnownParameters - 1];    //I substract 1, because the last two columns in this case are identical
            centralAlphas = new float[numberOfKnownParameters];
            centralBetas = new float[numberOfKnownParameters];
            for (int r = 0; r < numberOfUnknownParameters; r++) {
                centralAlphas[r] = 1.0f;
            }
            centralBetas[0] = 0.0f;
            for (int r = 1; r < numberOfUnknownParameters - 1; r++) {
                centralBetas[r] = 0.1f;
            }
            centralBetas[numberOfUnknownParameters - 1] = 1.0f;
        }
    }

    public static void main(String[] args) throws IOException {
        String directory = Config.getDirectory();       
        Scanner input = new Scanner(System.in);
        System.out.println("How many samples per data file is desired?");
        int n = input.nextInt();
        System.out.println("How many data files would you like to be generated?");
        int NUMBER_OF_GENERATED_INSTANCES = input.nextInt();
        System.out.println("Which experiment would you like to launch?");
        int experimentNumber = input.nextInt();
         
        int numberOfKnownParameters = 0;
        int numberOfUnknownParameters = 0;
        if (experimentNumber == 1) {
            numberOfKnownParameters = 5;
            numberOfUnknownParameters = 2;
        }
        if (experimentNumber == 2) {
            numberOfKnownParameters = 9;
            numberOfUnknownParameters = 6;
        }
    //    int n = 20;
    //    int NUMBER_OF_GENERATED_INSTANCES = 10;
        runExperiments A = new runExperiments(experimentNumber, n, NUMBER_OF_GENERATED_INSTANCES, numberOfKnownParameters, numberOfUnknownParameters);
        A.deletedEveryExistingExcelFileInTheDirectory(directory);
        A.launchTheEntireExperiment(experimentNumber);
    }

    public void deletedEveryExistingExcelFileInTheDirectory(String directory) {
        File folder = new File(directory);
        File[] files
                = folder.listFiles((dir, name)
                        -> name.toLowerCase().endsWith(".xls")
                || name.toLowerCase().endsWith(".xls")
                || name.toLowerCase().endsWith(".csv"));

        if (files != null) {
            for (File file : files) {
                if (file.delete()) {
                    System.out.println("Deleted: " + file.getName());
                } else {
                    System.out.println("Could not delete: " + file.getName());
                }
            }
        }
    }

    public void launchTheEntireExperiment(int experimentNumber) throws FileNotFoundException, IOException {
        generateDataDependingOnTheExperiment(experimentNumber);
        String dataName;
        for (int r = 0; r < NUMBER_OF_GENERATED_INSTANCES; r++) {
            dataName = "generatedData" + r + ".xls";
            runExperimentsOverThisData(dataName, experimentNumber, getNumberOfUnknownParameters());
            System.out.println("got rid of running experiments on instance " + r);
        }
    }

    public void generateDataDependingOnTheExperiment(int experimentNumber) {
        if (experimentNumber == 1) {
            CreateDataInExcelFile2Parameters L1 = new CreateDataInExcelFile2Parameters();
            for (int q = 0; q < numberOfGeneratedInstances(); q++) {
                L1.generateDataOfExperiment1(q, getNumberOfSamples());
            }
        } else if (experimentNumber == 2) {
            CreateDataInExcelFile5Parameters L2 = new CreateDataInExcelFile5Parameters();
            for (int q = 0; q < numberOfGeneratedInstances(); q++) {
                int numberOfOkSamples = (int) (0.9 * getNumberOfSamples());
                L2.generateDataOfExperiment2(q, getNumberOfSamples(), numberOfOkSamples);
            }
        }
    }

    public int numberOfGeneratedInstances() {
        return NUMBER_OF_GENERATED_INSTANCES;
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public int getNumberOfknownParameters() {
        return numberOfKnownParameters;
    }

    public int getNumberOfUnknownParameters() {
        return numberOfUnknownParameters;
    }

    public void runExperimentsOverThisData(String dataName, int experimentNumber, int numberOfUnknownParameters) {
        try {
            if (experimentNumber == 1) {
                developTheMatrixOfData(dataName, experimentNumber);
            } else {
                developTheMatrixOfData3(dataName);
            }
            int indexOfRightvestorInDataFile = 2;
            if (experimentNumber == 2) {
                indexOfRightvestorInDataFile = 6;
            }
            establishTheSystemOfEquationsAndSolve2(experimentNumber, numberOfUnknownParameters, indexOfRightvestorInDataFile);
            establishTheSystemOfEquationsAndSolve2(experimentNumber, numberOfUnknownParameters, indexOfRightvestorInDataFile + 1);
            if (experimentNumber == 1) {
                establishTheSystemOfEquationsAndSolve2(experimentNumber, numberOfUnknownParameters, indexOfRightvestorInDataFile + 2);
            }
            findGlobalMinimumOOptimized2(experimentNumber);
        } catch (IOException e) {
        }

    }

    public void developTheMatrixOfData3(String dataName) throws FileNotFoundException, IOException {
        int rowIndex = 0;

        try (FileInputStream fis = new FileInputStream(new File(Config.getDirectory() + dataName)); HSSFWorkbook wb = new HSSFWorkbook(fis)) {

            HSSFSheet sheet = wb.getSheetAt(0);

            for (Row row : sheet) {
                // Set first column to 1
                matrixOfData[rowIndex][0] = 1.0f;

                // Get total columns in Excel row
                int totalColumns = row.getLastCellNum();

                // Read from column 1 (Excel column 0) up to totalColumns-2 (skip last)
                for (int excelCol = 0; excelCol < totalColumns - 1; excelCol++) {
                    Cell cell = row.getCell(excelCol);
                    if (cell != null) {
                        // Matrix column = excelCol + 1 (because matrix[0] is the "1" column)
                        matrixOfData[rowIndex][excelCol + 1] = truncate((float) cell.getNumericCellValue(), 2);
                    }
                }

                rowIndex++;
                if (rowIndex >= getNumberOfSamples()) {
                    break;
                }
            }
        }
    }

    public void developTheMatrixOfData(String dataName, int experimentNumber) throws FileNotFoundException, IOException {
        int n = getNumberOfSamples();
        int i;
        for (i = 0; i < n; i++) {
            matrixOfData[i][0] = 1.0f;
        }
        i = 0;
        int j;
        FileInputStream fis = new FileInputStream(new File(Config.getDirectory() + dataName));
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        HSSFSheet sheet = wb.getSheetAt(0);
        for (Row row : sheet) {
            j = 1;
            for (Cell cell : row) {
                matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                j++;
            }
            i++;
        }
    }

    public void createNewColumnsOfExcelOutputFile(int experimentNumber, int numberOfUnknownParameters, int whichParameter, int dimensionOfRightvestorInDataFile) {
        establishTheSystemOfEquationsAndSolve2(experimentNumber, numberOfUnknownParameters, dimensionOfRightvestorInDataFile);
        if ((experimentNumber == 1 && dimensionOfRightvestorInDataFile > numberOfUnknownParameters + 1)
                || (experimentNumber == 2 && dimensionOfRightvestorInDataFile > numberOfUnknownParameters)) {
            findGlobalMinimumOOptimized2(experimentNumber);

        }
    }

    public void establishTheSystemOfEquationsAndSolve2(int experimentNumber, int numberOfUnknownParameters, int dimensionOfRightvestorInDataFile) {

        // =====================================================
        // ONE-SHOT CONFIGURATION: INITIALIZE TARGET PARAMETER TRACKS
        // =====================================================
        // This replaces 'allOneOfAlphaOrBetaOrGammaSolutions'. 
        // It creates a row container for each individual parameter group.
        List<List<Float>> sortedParameters = new ArrayList<>(numberOfUnknownParameters);
        for (int i = 0; i < numberOfUnknownParameters; i++) {
            sortedParameters.add(new ArrayList<>());
        }

        // =====================================================
        // INITIAL COMBINATION & TRACKING CACHE
        // =====================================================
        int[] comb = new int[numberOfUnknownParameters];
        int[] lastUsedRowIndex = new int[numberOfUnknownParameters];

        for (int i = 0; i < numberOfUnknownParameters; i++) {
            comb[i] = i;
            lastUsedRowIndex[i] = -1;
        }

        // =====================================================
        // PERSISTENT SYSTEM MATRICES (DOUBLE-BASED AS ORIGINAL)
        // =====================================================
        double[][] A = new double[numberOfUnknownParameters][numberOfUnknownParameters];
        double[] B = new double[numberOfUnknownParameters];
        float[] solution;

        // OPTIMIZATION: Set the first column entries to 1.0 EXACTLY ONCE
        for (int eq = 0; eq < numberOfUnknownParameters; eq++) {
            A[eq][0] = 1.0;
        }

        while (true) {

            // =================================================
            // BUILD A AND B (WITH FIRST COLUMN SKIP & DELTA CHECKS)
            // =================================================
            for (int eq = 0; eq < numberOfUnknownParameters; eq++) {
                int rowIndex = comb[eq];

                // If this row slot hasn't changed, skip entirely
                if (rowIndex == lastUsedRowIndex[eq]) {
                    continue;
                }

                float[] sourceRow = matrixOfData[rowIndex];

                // OPTIMIZATION: Skip column 0 entirely! 
                for (int var = 1; var < numberOfUnknownParameters; var++) {
                    A[eq][var] = (double) sourceRow[var];
                }

                // Fill Right Hand Side vector
                B[eq] = (double) sourceRow[dimensionOfRightvestorInDataFile];

                // Mark this slot as updated
                lastUsedRowIndex[eq] = rowIndex;
            }

            // =================================================
            // SOLVE SYSTEM & DIRECT DEPOSITION
            // =================================================
            if (!MatrixUtils.isSingular(A)) {
                solution = solveTheSystem(A, B);

                //test
/*
                float temp = 0.0f;
                for (int i = 0; i < numberOfUnknownParameters; i++)
                    temp += A[4][i] * solution[i];
                System.out.println(temp);
                System.out.println(B[4]);
                 */
                // ONE-SHOT VERTICAL DEPOSITION:
                // Distribute parameter elements straight into their target tracks.
                // Completely bypasses temporary horizontal float[] array allocations.
                for (int p = 0; p < numberOfUnknownParameters; p++) {
                    sortedParameters.get(p).add(solution[p]);
                }
            }

            // =================================================
            // NEXT COMBINATION (Lexicographical Generator)
            // =================================================
            int pos = numberOfUnknownParameters - 1;

            while (pos >= 0 && comb[pos] == getNumberOfSamples() - numberOfUnknownParameters + pos) {
                pos--;
            }

            if (pos < 0) {
                break;
            }

            comb[pos]++;

            for (int i = pos + 1; i < numberOfUnknownParameters; i++) {
                comb[i] = comb[i - 1] + 1;
            }
        }

        // =====================================================
        // NATIVE IN-PLACE SORTING BEFORE RETURN
        // =====================================================
        // Sort each parameter track cleanly using high-speed native Timsort loops
        //    for (int p = 0; p < numberOfUnknownParameters; p++) {
        //    java.util.Collections.sort(sortedParameters.get(p));
        //    }
        //why would we ever sort here?
        if (experimentNumber == 1) {
            switch (dimensionOfRightvestorInDataFile) {
                case 2 ->
                    alphaSortedParameters = sortedParameters;
                case 3 ->
                    betaSortedParameters = sortedParameters;
                case 4 ->
                    gammaSortedParameters = sortedParameters;
                default -> {
                }
            }
        } else if (experimentNumber == 2) {
            switch (dimensionOfRightvestorInDataFile) {
                case 6 ->
                    alphaSortedParameters = sortedParameters;
                case 7 -> {
                    betaSortedParameters = sortedParameters;
                    gammaSortedParameters = betaSortedParameters;
                }
                default -> {
                }
            }
        }

        // Returns the fully isolated, fully sorted lists directly
        //    return sortedParameters;
    }

    public static float[] solveTheSystem(double[][] Adata, double[] bdata) {
        RealMatrix A = new Array2DRowRealMatrix(Adata);
        RealVector b = new ArrayRealVector(bdata);
        DecompositionSolver solver = new QRDecomposition(A).getSolver();
        RealVector solution = solver.solve(b);
        float[] solu = new float[solution.getDimension()];
        //      System.out.println("Solution:");
        for (int i = 0; i < solution.getDimension(); i++) {
            solu[i] = (float) solution.getEntry(i);
        }
        return solu;
    }

    public void findGlobalMinimumOOptimized2(int experimentNumber) {
        int N = matrixOfData.length;
        if (N == 0) {
            return;
        }

        int M = matrixOfData[0].length;
        int numX;
        if (experimentNumber == 1) {
            numX = M - 3;
        } else {
            numX = M - 2;
        }
        int h1Max = N / 4 - 1;
        int h2Start = 3 * N / 4;
        //    int minWindowSize = N / 2;

        double globalMinO = Double.MAX_VALUE;
        int bestH1, bestH2, bestSolIdx = -1;

        int numSolutions = alphaSortedParameters.get(0).size();

        float[] currentAlphas = new float[numX];
        float[] currentBetas = new float[numX];
        float[] currentGammas = new float[numX];

        for (int s = 0; s < numSolutions; s++) {
            for (int j = 0; j < numX; j++) {
                currentAlphas[j] = alphaSortedParameters.get(j).get(s);
                currentBetas[j] = betaSortedParameters.get(j).get(s);
                currentGammas[j] = gammaSortedParameters.get(j).get(s);
            }

            double[] E = new double[N];
            //        double[] prefixSum = new double[N + 1];
            double[] prefixSum2 = new double[N];

            //      prefixSum[0] = 0;
// First, compute E values for all i
            // float[] E = new float[N];
            for (int i = 0; i < N; i++) {
                float sumAlphaX = 0, sumBetaX = 0, sumGammaX = 0;
                float[] row = matrixOfData[i];

                for (int j = 0; j < numX; j++) {
                    float xVal = row[j];
                    sumAlphaX += currentAlphas[j] * xVal;
                    sumBetaX += currentBetas[j] * xVal;
                    //    sumGammaX += currentGammas[j] * xVal;
                }
                if (experimentNumber == 1) {
                    for (int j = 0; j < numX; j++) {
                        float xVal = row[j];
                        sumGammaX += currentGammas[j] * xVal;
                    }
                }

                if (experimentNumber == 1) {
                    E[i] = (float) (Math.abs(row[numX] - sumAlphaX)
                            + 0.5 * Math.abs(row[numX + 1] - sumBetaX)
                            + 0.5 * Math.abs(row[numX + 2] - sumGammaX));
                } else {
                    E[i] = (float) (Math.abs(row[numX] - sumAlphaX)
                            + Math.abs(row[numX + 1] - sumBetaX));
                }
            }

// Sort E and get sorted indices
            Integer[] sortedIndices = new Integer[N];
            for (int i = 0; i < N; i++) {
                sortedIndices[i] = i;
            }
            // for (int i = 0; i < N; i++)
            //  System.out.println(E[i]);
            Arrays.sort(sortedIndices, Comparator.comparingDouble(i -> E[i]));

// Build prefixSum based on SORTED E
            float[] sortedE = new float[N];
            //    float[] prefixSum = new float[N + 1];
            //   float[] prefixSum2 = new float[N]; // if you still need this

            for (int idx = 0; idx < N; idx++) {
                int originalIndex = sortedIndices[idx];
                sortedE[idx] = (float) E[originalIndex];
                //   prefixSum[idx + 1] = prefixSum[idx] + sortedE[idx];

                if (idx == 0) {
                    prefixSum2[idx] = sortedE[idx];
                } else {
                    prefixSum2[idx] = prefixSum2[idx - 1] + sortedE[idx];
                }
            }

// Now iterate over SORTED indices
            double totalSumE = prefixSum2[N - 1];
            if (totalSumE < 1e-12) {
                continue;
            }

// IMPORTANT: h1 and h2 now refer to positions in the SORTED array
            for (int h1 = 0; h1 < Math.ceil(N / 4.0) - 1; h1++) {
                for (int h2 = h2Start; h2 < N; h2++) {
                    int currentWindowSize = h2 - h1 + 1;
                    if (currentWindowSize < N / 2.0) {
                        continue;
                    }

                    //    double numerator = prefixSum[h2 + 1] - prefixSum[h1];
                    double numerator;
                    if (h1 == 0) {
                        numerator = prefixSum2[h2];
                    } else {
                        numerator = prefixSum2[h2] - prefixSum2[h1 - 1];
                    }

                    double currentO = numerator / totalSumE;

                    if (currentO < globalMinO) {
                        globalMinO = currentO;
                        bestH1 = h1;        // These are now indices in the SORTED array
                        bestH2 = h2;        // These are now indices in the SORTED array
                        bestSolIdx = s;
                    }
                }
            }

            // =========================================================================
            // MEMORY-COLLECTOR & SINGLE-SHOT DYNAMIC WRITE ACTION
            // =========================================================================
            //  if (bestSolIdx != -1) {
            if (s + 1 == numSolutions) {
                int K = numX; // Dynamic number of parameters per group
                int totalParams = 3 * K;

                // Extract all dynamic winning parameters into one row array
                float[] currentResult = new float[totalParams];
                int pIdx = 0;
                System.out.println("solution number " + bestSolIdx + " gave the best");
                for (int j = 0; j < K; j++) {
                    currentResult[pIdx++] = alphaSortedParameters.get(j).get(bestSolIdx);
                }
                for (int j = 0; j < K; j++) {
                    currentResult[pIdx++] = betaSortedParameters.get(j).get(bestSolIdx);
                }
                for (int j = 0; j < K; j++) {
                    currentResult[pIdx++] = gammaSortedParameters.get(j).get(bestSolIdx);
                }

                // Save row to memory list
                allFileResults.add(currentResult);

                // Process file generation on the 10th result load
                if (allFileResults.size() == NUMBER_OF_GENERATED_INSTANCES) {

                    //    if( s + 1 == numSolutions) {
                    String filename = "OptimizationResults.csv";
                    java.io.File file = new java.io.File(filename);

                    // Stitch together your pre-determined, unique baseline lists
                    double[] centralBaselines = new double[totalParams];
                    int bIdx = 0;

                    for (int j = 0; j < K; j++) {
                        centralBaselines[bIdx++] = centralAlphas[j]; // Match to alpha0, alpha1...
                    }
                    for (int j = 0; j < K; j++) {
                        centralBaselines[bIdx++] = centralBetas[j];  // Match to beta0, beta1...
                    }
                    if (experimentNumber == 1) {
                        for (int j = 0; j < K; j++) {
                            centralBaselines[bIdx++] = centralGammas[j]; // Match to gamma0, gamma1...
                        }
                    }
                    double[] columnSums = new double[totalParams];
                    double[] columnSumSquaredDeviations = new double[totalParams];

                    // Overwrite old file execution completely
                    try (java.io.FileWriter fw = new java.io.FileWriter(file, false); java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {

                        // Row 1: Generate Dynamic CSV Labels
                        StringBuilder sbHeaders = new StringBuilder();
                        for (int j = 0; j < K; j++) {
                            sbHeaders.append("alpha").append(j).append(",");
                        }
                        for (int j = 0; j < K; j++) {
                            sbHeaders.append("beta").append(j).append(",");
                        }
                        for (int j = 0; j < K; j++) {
                            sbHeaders.append("gamma").append(j);
                            if (j < K - 1) {
                                sbHeaders.append(",");
                            }
                        }
                        pw.println(sbHeaders.toString());

                        // Rows 2 to 11: The exact 10 data rows from memory
                        for (float[] rowParams : allFileResults) {
                            for (int col = 0; col < totalParams; col++) {
                                pw.printf("%f", rowParams[col]);
                                if (col < totalParams - 1) {
                                    pw.print(",");
                                }

                                double val = rowParams[col];
                                columnSums[col] += val;

                                // Deviation calculation using unique individual parameter centers
                                double deviation = val - centralBaselines[col];
                                columnSumSquaredDeviations[col] += Math.pow(deviation, 2);
                            }
                            pw.println();
                        }

                        // Row 12: Blank row spacer matching cell width dynamically
                        for (int col = 0; col < totalParams - 1; col++) {
                            pw.print(",");
                        }
                        pw.println();

                        // Row 13: Pure numeric Averages
                        for (int col = 0; col < totalParams; col++) {
                            pw.print(col == 0 ? String.format("%f", columnSums[col] / NUMBER_OF_GENERATED_INSTANCES)
                                    : String.format(",%f", columnSums[col] / NUMBER_OF_GENERATED_INSTANCES));
                        }
                        pw.println();

                        // Row 14: Pure numeric Squared Deviations from unique baselines
                        for (int col = 0; col < totalParams; col++) {
                            double msd = columnSumSquaredDeviations[col] / NUMBER_OF_GENERATED_INSTANCES;
                            pw.print(col == 0 ? String.format("%f", msd) : String.format(",%f", msd));
                        }
                        pw.println();

                        System.out.println("Excel file successfully created fresh with exactly 14 rows.");

                        // Clear the cache for clean resets next time main runs
                        allFileResults.clear();

                    } catch (java.io.IOException e) {
                        System.err.println("Error writing out final dynamic Excel spreadsheet matrix.");
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static float truncate(float number, int precision) {

        double prec = Math.pow(10, precision);
        int integerPart = (int) number;
        float fractionalPart = number - integerPart;
        fractionalPart *= prec;
        int fractPart = (int) fractionalPart;
        fractionalPart = (float) (integerPart) + (fractPart) / (float) prec;
        return (float) fractionalPart;
    }
}
