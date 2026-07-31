package fuzzyregressionestimation;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Comparator;
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

public class initializeRequiredVectors {

    private static final java.util.List<float[]> allFileResults = new java.util.ArrayList<>();
    private static float[] centralAlphas;
    private static float[] centralBetas;
    private static float[] centralGammas;
    private static final double EPS = 1e-10;
    private static int numberOfSamplesPerDataFile;         //n = numberOfSamples
    private static List<List<Float>> alphaSortedParameters;
    private static List<List<Float>> betaSortedParameters;
    private static List<List<Float>> gammaSortedParameters;
    static float[][] matrixOfData;
    private final int NUMBER_OF_GENERATED_INSTANCES;

    //the name of this method looks inconvenient. Later change it to something like initializeExperiment ...
    public initializeRequiredVectors(int n, int numberOfKnownParameters, int NUMBER_OF_GENERATED_INSTANCES, int experimentNumber) {
        numberOfSamplesPerDataFile = n;
        this.NUMBER_OF_GENERATED_INSTANCES = NUMBER_OF_GENERATED_INSTANCES;
        if (experimentNumber == 1) {
            matrixOfData = new float[n][numberOfKnownParameters];
            centralAlphas = new float[2];
            centralBetas = new float[2];
            centralGammas = new float[2];
            centralAlphas[0] = 10.0f;
            centralAlphas[1] = 3.0f;
            centralBetas[0] = -15.0f;
            centralBetas[1] = 0.1f;
            centralGammas[0] = -40.0f;
            centralGammas[1] = 0.2f;

        } else if (experimentNumber == 2) {
            matrixOfData = new float[n][numberOfKnownParameters - 1];
            //I substract 1, because the last two columns
            //in this case are identical
            centralAlphas = new float[numberOfKnownParameters];
            centralBetas = new float[numberOfKnownParameters];
            centralGammas = new float[numberOfKnownParameters];
            centralAlphas[0] = 1.0f;
            centralAlphas[1] = 1.0f;
            centralAlphas[2] = 1.0f;
            centralAlphas[3] = 1.0f;
            centralAlphas[4] = 1.0f;
            centralAlphas[5] = 1.0f;
            centralBetas[0] = 0.0f;
            centralBetas[1] = 0.1f;
            centralBetas[2] = 0.1f;
            centralBetas[3] = 0.1f;
            centralBetas[4] = 0.1f;
            centralBetas[5] = 1.0f;
        }
    }

    public void developTheMatrixOfData(String dataName, int experimentNumber) throws FileNotFoundException, IOException {
        int n = numberOfSamplesPerDataFile;
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
                if (rowIndex >= numberOfSamplesPerDataFile) {
                    break;
                }
            }
        }

        //   return matrixOfData;
    }

    public void solveAllEquationsAndSetup(int experimentNumber, int numberOfUnknownParameters, int whichPar, int dimensionOfRightvestorInDataFile) {
        List<float[]> allOneOfAlphaOrBetaOrGammaSolutions = establishTheSystemOfEquationsAndSolve(experimentNumber, numberOfUnknownParameters, dimensionOfRightvestorInDataFile);
        //    List<List<Float>> allOneOfAlphaOrBetaOrGammaSolutions2 = establishTheSystemOfEquationsAndSolve2(experimentNumber, numberOfUnknownParameters, dimensionOfRightvestorInDataFile);
        organizeAllSolutionsInASortedList(experimentNumber, numberOfUnknownParameters, allOneOfAlphaOrBetaOrGammaSolutions, dimensionOfRightvestorInDataFile);
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
                    for (int j = 0; j < K; j++) {
                        centralBaselines[bIdx++] = centralGammas[j]; // Match to gamma0, gamma1...
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

    private static void saveCleanParametersToCSV(int bestSolIdx) {
        String filename = "OptimizationResults.csv";

        // Create a file object to check if it already exists on your disk
        File file = new File(filename);
        boolean fileExists = file.exists();

        // Passing 'true' as the second argument enables APPEND MODE
        try (FileWriter fw = new FileWriter(file, true); PrintWriter pw = new PrintWriter(fw)) {

            // Step 1: Only write the headers if this is a brand new file
            if (!fileExists) {
                pw.println("alpha0,alpha1,beta0,beta1,gamma0,gamma1");
            }

            // Step 2: Extract values for j = 0 and j = 1 from your winning package
            float alpha0 = alphaSortedParameters.get(0).get(bestSolIdx);
            float alpha1 = alphaSortedParameters.get(1).get(bestSolIdx);

            float beta0 = betaSortedParameters.get(0).get(bestSolIdx);
            float beta1 = betaSortedParameters.get(1).get(bestSolIdx);

            float gamma0 = gammaSortedParameters.get(0).get(bestSolIdx);
            float gamma1 = gammaSortedParameters.get(1).get(bestSolIdx);

            // Step 3: Append the new data row to the bottom of the file
            pw.printf("%f,%f,%f,%f,%f,%f%n", alpha0, alpha1, beta0, beta1, gamma0, gamma1);

            System.out.println("Result successfully appended to: " + filename);

        } catch (IOException e) {
            System.err.println("Error appending the parameter data to the CSV file.");
            e.printStackTrace();
        }
    }

    public static int[] indexSort(final float[] v, boolean keepUnsorted) {
        final Integer[] II = new Integer[v.length];
        for (int i = 0; i < v.length; i++) {
            II[i] = i;
        }
        Arrays.sort(II, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return Float.compare(v[o1], v[o2]);
            }
        });
        int[] ii = new int[v.length];
        for (int i = 0; i < v.length; i++) {
            ii[i] = II[i];
        }
        if (!keepUnsorted) {
            float[] clon = v.clone();
            for (int i = 0; i < v.length; i++) {
                v[i] = clon[II[i]];
            }
        }
        return ii;
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

    static void printArray(int arr[]) {
        //   int n = arr.length;
        for (int i = 0; i < arr.length; ++i) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    static void printArray(float arr[]) {
        //   int n = arr.length;
        for (int i = 0; i < arr.length; ++i) {
            System.out.print("\ni: " + i + "->" + arr[i] + " ");
        }
        System.out.println();
    }

    public float[] fillInAnArrayWithCoefficient(int whichColumn, float fixed_something_0) {
        //    int n = getNumberOfSamples();

        float[] tableOf_column_something_MinusParameter_0s = new float[numberOfSamplesPerDataFile];
        for (int w = 0; w < numberOfSamplesPerDataFile; w++) {
            tableOf_column_something_MinusParameter_0s[w] = matrixOfData[w][whichColumn] - fixed_something_0;
        }
        return tableOf_column_something_MinusParameter_0s;
    }

    public int[] fillIdenticalArray(int[] a) {
        for (int r = 0; r < numberOfSamplesPerDataFile; r++) {
            a[r] = r;
        }
        return a;
    }

    public List<List<Float>> establishTheSystemOfEquationsAndSolve20(int experimentNumber, int numberOfUnknownParameters, int dimensionOfRightvestorInDataFile) {

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

            while (pos >= 0 && comb[pos] == numberOfSamplesPerDataFile - numberOfUnknownParameters + pos) {
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
        for (int p = 0; p < numberOfUnknownParameters; p++) {
            java.util.Collections.sort(sortedParameters.get(p));
        }

        // Returns the fully isolated, fully sorted lists directly
        return sortedParameters;
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

    public static boolean isSingular(double[][] A) {
        //   int n = A.length;

        // Make a copy so original matrix is not modified
        double[][] M = new double[A.length][A.length];
        for (int i = 0; i < A.length; i++) {
            System.arraycopy(A[i], 0, M[i], 0, A.length);
        }

        for (int i = 0; i < A.length; i++) {

            // Find pivot
            int maxRow = i;
            for (int k = i + 1; k < A.length; k++) {
                if (Math.abs(M[k][i]) > Math.abs(M[maxRow][i])) {
                    maxRow = k;
                }

            }

            // Swap rows
            double[] temp = M[i];
            M[i] = M[maxRow];
            M[maxRow] = temp;

            // Check if pivot is zero (or very small)
            if (Math.abs(M[i][i]) < EPS) {
                return true; // Singular
            }

            // Eliminate below
            for (int k = i + 1; k < A.length; k++) {
                double factor = M[k][i] / M[i][i];
                for (int j = i; j < A.length; j++) {
                    M[k][j] -= factor * M[i][j];
                }
            }
        }

        return false; // Not singular
    }

    public static List<float[]> establishTheSystemOfEquationsAndSolve(int experimentNumber, int numberOfUnknownParameters, int dimensionOfRightvestorInDataFile) {
        // =====================================================
        // STORE ALL SOLUTIONS
        // =====================================================
        List<float[]> allOneOfAlphaOrBetaOrGammaSolutions = new ArrayList<>();
        // =====================================================
        // INITIAL COMBINATION
        // =====================================================
        int[] comb = new int[numberOfUnknownParameters];
        for (int i = 0; i < numberOfUnknownParameters; i++) {
            comb[i] = i;
        }

        // =====================================================
        // LOOP THROUGH ALL COMBINATIONS
        // =====================================================
        double[][] A = new double[numberOfUnknownParameters][numberOfUnknownParameters];
        double[] B = new double[numberOfUnknownParameters];
        float[] solution = null;
        //   int tm = 0;
        while (true) {

            // =================================================
            // BUILD A AND B
            // =================================================
            for (int eq = 0; eq < numberOfUnknownParameters; eq++) {

                int rowIndex = comb[eq];

                // fill coefficients
                for (int var = 0; var < numberOfUnknownParameters; var++) {
                    A[eq][var] = matrixOfData[rowIndex][var];
                    //here is work to do for later. We fill the same entries of A2 over and over.
                    //Think about getting rid of doing he same work a lot!
                }

                // fill RHS
                //   int oo = secondDimensionNumberOFMatrix - whichPar - 2;
                B[eq] = matrixOfData[rowIndex][dimensionOfRightvestorInDataFile];
            }

            // =================================================
            // SOLVE SYSTEM
            // =================================================
            if (!MatrixUtils.isSingular(A)) {
                solution = solveTheSystem(A, B);

                // =================================================
                // STORE SOLUTION
                // 
//=================================================
                allOneOfAlphaOrBetaOrGammaSolutions.add(solution);
                //    tm++;
            }

            // =================================================
            // NEXT COMBINATION
            // =================================================
            int pos = numberOfUnknownParameters - 1;

            while (pos >= 0
                    && comb[pos] == numberOfSamplesPerDataFile - numberOfUnknownParameters + pos) {

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
        return allOneOfAlphaOrBetaOrGammaSolutions;

    }

    public static void establishTheSystemOfEquationsAndSolve2(int experimentNumber, int numberOfUnknownParameters, int dimensionOfRightvestorInDataFile) {

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

            while (pos >= 0 && comb[pos] == numberOfSamplesPerDataFile - numberOfUnknownParameters + pos) {
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

    public static void organizeAllSolutionsInASortedList(int experimentNumber, int numberOfUnknownParameters, List<float[]> allOneOfAlphaOrBetaOrGammaSolutions, int dimensionOfRightvestorInDataFile) {
        if (experimentNumber == 1) {
            switch (dimensionOfRightvestorInDataFile) {
                case 2:
                    alphaSortedParameters = putSolutionsInASortedThing(numberOfUnknownParameters, allOneOfAlphaOrBetaOrGammaSolutions);
                    break;
                case 3:
                    betaSortedParameters = putSolutionsInASortedThing(numberOfUnknownParameters, allOneOfAlphaOrBetaOrGammaSolutions);
                    break;
                case 4:
                    gammaSortedParameters = putSolutionsInASortedThing(numberOfUnknownParameters, allOneOfAlphaOrBetaOrGammaSolutions);
                    break;
                default:
                    break;
            }
        } else if (experimentNumber == 2) {
            switch (dimensionOfRightvestorInDataFile) {
                case 6:
                    alphaSortedParameters = putSolutionsInASortedThing(numberOfUnknownParameters, allOneOfAlphaOrBetaOrGammaSolutions);
                    break;
                case 7:
                    betaSortedParameters = putSolutionsInASortedThing(numberOfUnknownParameters, allOneOfAlphaOrBetaOrGammaSolutions);
                    gammaSortedParameters = betaSortedParameters;
                    break;
                default:
                    break;
            }
        }
    }

    public static List<List<Float>> putSolutionsInASortedThing(int numberOfUnknownParameters, List<float[]> allOneOfAlphaOrBetaOrGammaSolutions) {
        // =====================================================
// SORT VALUES OF EACH PARAMETER SEPARATELY
// =====================================================
        List<List<Float>> sortedParameters = new ArrayList<>();

// create one list per parameter
        for (int i = 0; i < numberOfUnknownParameters; i++) {
            sortedParameters.add(new ArrayList<>());
        }

// =====================================================
// COLLECT VALUES
// =====================================================
        for (float[] sol : allOneOfAlphaOrBetaOrGammaSolutions) {
            for (int param = 0; param < numberOfUnknownParameters; param++) {
                sortedParameters.get(param).add(sol[param]);
            }
        }

// =====================================================
// SORT EACH PARAMETER
// =======================================
//==============
        for (int param = 0; param < numberOfUnknownParameters; param++) {
            Collections.sort(sortedParameters.get(param));
        }

        return sortedParameters;

    }

}
