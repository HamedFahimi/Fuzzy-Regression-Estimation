package fuzzyregressionestimation;

import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;

//import static fuzzyregressionestimation.runExperimentsOverNumerousData2Parameters.directoryAsAString;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

    // Keeps track of the 10 rows in memory so we never have to append to a file
    private static final java.util.List<float[]> allFileResults = new java.util.ArrayList<>();

    private static float[] centralAlphas;
    private static float[] centralBetas;
    private static float[] centralGammas;

//    private static float[] centralAlphas = {1f, 1f, 1f, 1f,1f, 1f};
//    private static float[] centralBetas = {0f, 0.1f, 0.1f, 0.1f,0.1f, 1f};
//    private static float[] centralGammas = {0f, 0.1f, 0.1f, 0.1f,0.1f, 1f};
    private static final double EPS = 1e-10;
    private static int numberOfSamples;         //n = numberOfSamples
    private static int[] indices_of_samples_sorted_by_error;
    private static List<List<Float>> alphaSortedParameters;
    private static List<List<Float>> betaSortedParameters;
    private static List<List<Float>> gammaSortedParameters;
    private static ArrayList<Float> alpha_2WithoutDuplicates;
    private static ArrayList<Float> alpha_3WithoutDuplicates;
    private static ArrayList<Float> alpha_4WithoutDuplicates;
    private static ArrayList<Float> alpha_5WithoutDuplicates;
    private static ArrayList<Float> beta_2WithoutDuplicates;
    private static ArrayList<Float> beta_3WithoutDuplicates;
    private static ArrayList<Float> beta_4WithoutDuplicates;
    private static ArrayList<Float> beta_5WithoutDuplicates;
    private static ArrayList<Float> gamma_2WithoutDuplicates;
    private static ArrayList<Float> gamma_3WithoutDuplicates;
    private static ArrayList<Float> gamma_4WithoutDuplicates;
    private static ArrayList<Float> gamma_5WithoutDuplicates;
    private static int sizeOfAlpha_2WithoutDuplicates;
    private static int sizeOfAlpha_3WithoutDuplicates;
    private static int sizeOfAlpha_4WithoutDuplicates;
    private static int sizeOfAlpha_5WithoutDuplicates;
    private static int sizeOfBeta_2WithoutDuplicates;
    private static int sizeOfBeta_3WithoutDuplicates;
    private static int sizeOfBeta_4WithoutDuplicates;
    private static int sizeOfBeta_5WithoutDuplicates;
    private static int sizeOfGamma_2WithoutDuplicates;
    private static int sizeOfGamma_3WithoutDuplicates;
    private static int sizeOfGamma_4WithoutDuplicates;
    private static int sizeOfGamma_5WithoutDuplicates;
    private static float[] bestParam_iSoFar;
    private static float bestParam_0SoFar;
    private static float bestParam_1SoFar;
    private static float bestParam_2SoFar;
    private static float bestParam_3SoFar;
    private static float bestParam_4SoFar;
    private static float bestParam_5SoFar;
    private static float[] bestAlpha_iSoFar;
    private static float bestAlpha_0SoFar;
    private static float bestAlpha_1SoFar;
    private static float bestAlpha_2SoFar;
    private static float bestAlpha_3SoFar;
    private static float bestAlpha_4SoFar;
    private static float bestAlpha_5SoFar;
    private static float bestBeta_0SoFar;
    private static float bestBeta_1SoFar;
    private static float bestBeta_2SoFar;
    private static float bestBeta_3SoFar;
    private static float bestBeta_4SoFar;
    private static float bestBeta_5SoFar;
    private static float bestGamma_0SoFar;
    private static float bestGamma_1SoFar;
    private static float bestGamma_2SoFar;
    private static float bestGamma_3SoFar;
    private static float bestGamma_4SoFar;
    private static float bestGamma_5SoFar;
    private static float[] sumOfAllParamTimesX_jsSoFar;
    private static int sampleWithBesth_1SoFar;
    private static int sampleWithBesth_2SoFar;
    private static float bestValueOfObjectiveFoundSoFar;
    private static float[] errorTerm;
    boolean[] signOfThisCorrespondingErrorTermIsNegative;
    static float[][] matrixOfData;
    float[][] tableOfAlpha_1sMultipliedInX_is;
    float[][] tableOfBeta_1sMultipliedInX_is;
    float[][] tableOfGamma_1sMultipliedInX_is;
    static float maintainedSumOfAllComputedErrorTerms;

    //the name of this method looks inconvenient. Later change it to something like initializeExperiment ...
    public initializeRequiredVectors(int n, int numberOfKnownParameters, int experimentNumber) {
        numberOfSamples = n;
        sumOfAllParamTimesX_jsSoFar = new float[n];
        if (experimentNumber == 1) {
            matrixOfData = new float[n][numberOfKnownParameters];
            centralAlphas = new float[numberOfKnownParameters];
            centralBetas = new float[numberOfKnownParameters];
            centralGammas = new float[numberOfKnownParameters];
            centralAlphas[0] = 10f;
            centralAlphas[1] = 3f;
            centralBetas[0] = -15f;
            centralBetas[1] = 0.1f;
            centralGammas[0] = -40f;
            centralGammas[1] = 0.2f;

        } else if (experimentNumber == 2) {
            matrixOfData = new float[n][numberOfKnownParameters - 1];
            //I substract 1, because the last two columns
            //in this case are identical
            centralAlphas = new float[numberOfKnownParameters];
            centralBetas = new float[numberOfKnownParameters];
            centralGammas = new float[numberOfKnownParameters];
            centralAlphas[0] = 1f;
            centralAlphas[1] = 1f;
            centralAlphas[2] = 1f;
            centralAlphas[3] = 1f;
            centralAlphas[4] = 1f;
            centralAlphas[5] = 1f;
            centralBetas[0] = 0f;
            centralBetas[1] = 0.1f;
            centralBetas[2] = 0.1f;
            centralBetas[3] = 0.1f;
            centralBetas[4] = 0.1f;
            centralBetas[5] = 1f;
        }
        //    NanDetected = false;
        errorTerm = new float[n];
        signOfThisCorrespondingErrorTermIsNegative = new boolean[n];
        indices_of_samples_sorted_by_error = new int[n];
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public int choose2OverN() {
        int m = getNumberOfSamples();
        return m * (m - 1) / 2;
    }

    public int factorial(int n) {
        if (n == 0) {
            return 1;
        } else {
            return factorial(n - 1) * n;
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
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\Win10\\Documents\\NetBeansProjects\\fuzzyregressionestimation" + dataName));
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        HSSFSheet sheet = wb.getSheetAt(0);
        if (experimentNumber == 1) {
            for (Row row : sheet) {
                j = 1;
                for (Cell cell : row) {
                    switch (j) {
                        case 1:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        case 2:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        case 3:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        case 4:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;

                        //}
//                    case 8:
//                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
//                        break;
                    }
                    j++;
                }
                i++;
            }
        } else if (experimentNumber == 2) {
            for (Row row : sheet) {
                j = 1;
                for (Cell cell : row) {
                    switch (j) {
                        case 1:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        case 2:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        case 3:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        case 4:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        //normally for experiment 2 here counts
                        //    if (experimentNumber == 2) {
                        case 5:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        case 6:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        case 7:
                            matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                            break;
                        //}
//                    case 8:
//                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
//                        break;
                    }
                    j++;
                }
                i++;
            }
        }
    }

    public float[][] developTheMatrixOfData2(String dataName) throws FileNotFoundException, IOException {
        int i;
        for (i = 0; i < numberOfSamples; i++) {
            matrixOfData[i][0] = 1.0f;
        }
        i = 0;
        int j = 0;
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\Win10\\Documents\\NetBeansProjects\\fuzzyregressionestimation\\" + dataName));
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        HSSFSheet sheet = wb.getSheetAt(0);
        for (Row row : sheet) {
            j = 1;
            for (Cell cell : row) {
                switch (j) {
                    case 1:
                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 2:
                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 3:
                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 4:
                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 5:
                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 6:
                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 7:
                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 8:
                        matrixOfData[i][j] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                }
                j++;
            }
            i++;
        }
        return matrixOfData;
    }

    public void solveAllEquationsAndSetup(int experimentNumber, int numberOfUnknownParameters, int whichPar, int dimensionOfRightvestorInDataFile) {
        List<float[]> allOneOfAlphaOrBetaOrGammaSolutions = establishTheSystemOfEquationsAndSolve(experimentNumber, numberOfUnknownParameters, whichPar, dimensionOfRightvestorInDataFile);
        organizeAllSolutionsInASortedList(experimentNumber, numberOfUnknownParameters, allOneOfAlphaOrBetaOrGammaSolutions, dimensionOfRightvestorInDataFile);

        /*
        while (true) {

            float sum = 0;

            // ==========================================
            // ALPHAS
            // ==========================================
            for (int i = 0; i < x; i++) {

                sum
                        += alphas
                                .get(i)
                                .get(indices[i]);

            }

            // ==========================================
            // BETAS
            // ==========================================
            for (int i = 0; i < x; i++) {

                sum
                        += betas
                                .get(i)
                                .get(indices[x + i]);
            }

            // ==========================================
            // CURRENT COMBINATION
            // =======================================
             ==
                    = System.out.println(
                            "indices = "
                            + Arrays.toString(indices)
                            + "   sum = "
                            + sum
                    );

            // ==========================================
            // NEXT COMBINATION
            // ==========================================
            int pos = totalLists - 1;

            while (pos >= 0) {

                int size;

                // alpha lists
                if (pos < x) {

                    size
                            = alphas
                                    .get(pos)
                                    .size();
                } // beta lists
                else {

                    size
                            = betas
                                    .get(pos - x)
                                    .size();
                }

                indices[pos]++;

                if (indices[pos] < size) {
                    break;
                }

                indices[pos] = 0;

                pos--;
            }

            // all combinations completed
            if (pos < 0) {
                break;
            }
        }
         */
        //    int i = 0;
        //    int j = 0;
        /*
        if (experimentNumber == 2) {
            parameter_2 = new ArrayList<>();
            parameter_3 = new ArrayList<>();
            parameter_4 = new ArrayList<>();
            parameter_5 = new ArrayList<>();
            int k = 0, l = 0, m = 0, o = 0;
            do {
                while (i < n - 5) {
                    j = i + 1;
                    while (j < n - 4) {
                        k = j + 1;
                        while (k < n - 3) {
                            l = k + 1;
                            while (l < n - 2) {
                                m = l + 1;
                                while (m < n - 1) {
                                    o = m + 1;
                                    while (o < n) {
                                        switch (whichPar) {
                                            case 1:
                                                determinerOfRightComponentOfFuzzyNumber[0] = matrixOfData[i][6];
                                                determinerOfRightComponentOfFuzzyNumber[1] = matrixOfData[j][6];
                                                determinerOfRightComponentOfFuzzyNumber[2] = matrixOfData[k][6];
                                                determinerOfRightComponentOfFuzzyNumber[3] = matrixOfData[l][6];
                                                determinerOfRightComponentOfFuzzyNumber[4] = matrixOfData[m][6];
                                                determinerOfRightComponentOfFuzzyNumber[5] = matrixOfData[o][6];
                                                break;
                                            case 2:
                                                determinerOfRightComponentOfFuzzyNumber[0] = matrixOfData[i][7];
                                                determinerOfRightComponentOfFuzzyNumber[1] = matrixOfData[j][7];
                                                determinerOfRightComponentOfFuzzyNumber[2] = matrixOfData[k][7];
                                                determinerOfRightComponentOfFuzzyNumber[3] = matrixOfData[l][7];
                                                determinerOfRightComponentOfFuzzyNumber[4] = matrixOfData[m][7];
                                                determinerOfRightComponentOfFuzzyNumber[5] = matrixOfData[o][7];
                                                break;
                                            case 3:
                                                determinerOfRightComponentOfFuzzyNumber[0] = matrixOfData[i][8];
                                                determinerOfRightComponentOfFuzzyNumber[1] = matrixOfData[j][8];
                                                determinerOfRightComponentOfFuzzyNumber[2] = matrixOfData[k][8];
                                                determinerOfRightComponentOfFuzzyNumber[3] = matrixOfData[l][8];
                                                determinerOfRightComponentOfFuzzyNumber[4] = matrixOfData[m][8];
                                                determinerOfRightComponentOfFuzzyNumber[5] = matrixOfData[o][8];
                                                break;
                                            default:
                                                break;
                                        }
                                        double[] solu = null;
                                        double[][] Adata1 = new double[numberOfUnknownParameters][numberOfUnknownParameters];
                                        int whichIndex = i;
                                        for (int v1 = 0; v1 < numberOfUnknownParameters; v1++) {
                                            switch (v1) {
                                                case 1:
                                                    whichIndex = j;
                                                    break;
                                                case 2:
                                                    whichIndex = k;
                                                    break;
                                                case 3:
                                                    whichIndex = l;
                                                    break;
                                                case 4:
                                                    whichIndex = m;
                                                    break;
                                                case 5:
                                                    whichIndex = o;
                                                    break;
                                                default:
                                                    break;
                                            }
                                            for (int v2 = 0; v2 < numberOfUnknownParameters; v2++) {
                                                Adata1[v1][v2] = matrixOfData[whichIndex][v2];
                                            }
                                        }
                                        double[] bdata1 = new double[numberOfUnknownParameters];
                                        for (int y = 0; y < numberOfUnknownParameters; y++) {
                                            bdata1[y] = determinerOfRightComponentOfFuzzyNumber[y];
                                        }
                                        if (!MatrixUtils.isSingular(Adata1)) {
                                            solu = solveTheSystem(Adata1, bdata1);
                                            result = new float[solu.length];
                                            for (int w = 0; w < solu.length; w++) {
                                                result[w] = (float) solu[w]; //we have a damn casting here that must be avoided later
                                            }
                                        }
                                        parameter_0.add(result[0]);
                                        parameter_1.add(result[1]);
                                        parameter_2.add(result[2]);
                                        parameter_3.add(result[3]);
                                        parameter_4.add(result[4]);
                                        parameter_5.add(result[5]);
                                        for (int parameter = 0; parameter < numberOfUnknownParameters; parameter++) {
                                            //    vectors.get(parameter).add(solu[parameter]);
                                            all_parameters[parameter][counter] = solu[parameter];
                                        }
                                        counter++;
                                        o++;
                                    }
                                    m++;
                                }
                                l++;
                            }
                            k++;
                        }
                        j++;
                    }
                    i++;
                }
            } while (i < n - 5
                    && j < n - 4
                    && k < n - 3
                    && l < n - 2
                    && m < n - 1
                    && o < n
                    && counter < number_of_all_mutual_equations);
            for (int parameter = 0; parameter < numberOfUnknownParameters; parameter++) {
                Arrays.sort(all_parameters[parameter]);
            }
            //later check here if ever we get duplicates and even so, whether it is worth using an ArrayList instead
            Collections.sort(parameter_0);
            //        Collections.sort(vectors.get(0));
            Collections.sort(parameter_1);
            Collections.sort(parameter_2);
            Collections.sort(parameter_3);
            Collections.sort(parameter_4);
            Collections.sort(parameter_5);
        }
         */
    }

    public void findGlobalMinimumOOptimized(int experimentNumber) {
        int N = matrixOfData.length;
        if (N == 0) {
            return;
        }

        int M = matrixOfData[0].length;
        int numX;
        numX = M - 3; // K parameters per group
        if (experimentNumber == 2) {
            numX++;
        }
        int h1Max = N / 4;
        int h2Start = (3 * N) / 4;
        int minWindowSize = N / 2;

        double globalMinO = Double.MAX_VALUE;
        int bestH1 = -1, bestH2 = -1, bestSolIdx = -1;

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
//            if (experimentNumber == 1) {
//                for (int j = 0; j < numX; j++) {
//                    currentGammas[j] = gammaSortedParameters.get(j).get(s);
//                }
//            }
            //   double[] E = new double[N];
//            double[] prefixSum = new double[N + 1];
//            prefixSum[0] = 0;

// 1. Calculate the Error vector and pair it with its original index
            double[][] E = new double[N][2];
            for (int i = 0; i < N; i++) {
                float sumAlphaX = 0, sumBetaX = 0, sumGammaX = 0;
                float[] row = matrixOfData[i];

                for (int j = 0; j < numX; j++) {
                    float xVal = row[j];
                    sumAlphaX += currentAlphas[j] * xVal;
                    sumBetaX += currentBetas[j] * xVal;
                    sumGammaX += currentGammas[j] * xVal;
                }

                // Store the error value
                if (experimentNumber == 1) {
                    E[i][0] = Math.abs(row[M - 3] - sumAlphaX)
                            + 0.5 * Math.abs(row[M - 2] - sumBetaX)
                            + 0.5 * Math.abs(row[M - 1] - sumGammaX);
                } else if (experimentNumber == 2) {
                    E[i][0] = Math.abs(row[M - 2] - sumAlphaX)
                            + 2 * 0.5 * Math.abs(row[M - 1] - sumBetaX);
                }
                // Store the original task/row index!
                E[i][1] = i;
            }

            // 2. SORT BY ERROR VALUE (Column 0), keeping original indices (Column 1) intact
            java.util.Arrays.sort(E, (a, b) -> Double.compare(a[0], b[1] == b[1] ? a[0] : b[0]));
            // Alternative standard way:
            java.util.Arrays.sort(E, java.util.Comparator.comparingDouble(a -> a[0]));

            // 3. Create Prefix Sums based on the SORTED error values
            double[] prefixSum = new double[N + 1];
            prefixSum[0] = 0;
            for (int i = 0; i < N; i++) {
                prefixSum[i + 1] = prefixSum[i] + E[i][0]; // Using sorted error value
            }

            double totalSumE = prefixSum[N];
            if (totalSumE < 1e-12) {
                continue;
            }

            // 4. Iterate through windows on the sorted positions
            for (int h1 = 0; h1 < h1Max; h1++) {
                for (int h2 = h2Start - 1; h2 < N; h2++) {
                    int currentWindowSize = h2 - h1 + 1;
                    if (currentWindowSize < minWindowSize) {
                        continue;
                    }

                    // Your exact formula: Sigma from h1 to h2 over total Sigma
                    double numerator = prefixSum[h2 + 1] - prefixSum[h1];
                    double currentO = numerator / totalSumE;

                    if (currentO < globalMinO) {
                        globalMinO = currentO;
                        bestSolIdx = s;

                        // Now you can track exactly which original tasks made up this best window!
                        // For any index 'k' between h1 and h2:
                        // int originalTaskIndex = (int) E[k][1];
                    }
                }
            }
        }

        // =========================================================================
        // MEMORY-COLLECTOR & SINGLE-SHOT DYNAMIC WRITE ACTION
        // =========================================================================
        if (bestSolIdx != -1) {
            int K = numX; // Dynamic number of parameters per group
            int totalParams = 3 * K;

            // Extract all dynamic winning parameters into one row array
            float[] currentResult = new float[totalParams];
            int pIdx = 0;

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
            if (allFileResults.size() == 10) {
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
                try (java.io.FileWriter fw = new java.io.FileWriter(file, false);
                        java.io.PrintWriter pw = new java.io.PrintWriter(fw)) {

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
                        pw.print(col == 0 ? String.format("%f", columnSums[col] / 10)
                                : String.format(",%f", columnSums[col] / 10));
                    }
                    pw.println();

                    // Row 14: Pure numeric Squared Deviations from unique baselines
                    for (int col = 0; col < totalParams; col++) {
                        double msd = columnSumSquaredDeviations[col] / 10;
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
        } else {
            System.out.println("No valid solution found matching window constraint.");
        }
    }

    private static void saveCleanParametersToCSV(int bestSolIdx) {
        String filename = "OptimizationResults.csv";

        // Create a file object to check if it already exists on your disk
        File file = new File(filename);
        boolean fileExists = file.exists();

        // Passing 'true' as the second argument enables APPEND MODE
        try (FileWriter fw = new FileWriter(file, true);
                PrintWriter pw = new PrintWriter(fw)) {

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

    public int[] setUpTheObjectiveComputation(boolean sortWithInsertionsort, float[] error, float fixed_parameter_0, float fixed_parameter_1, float fixed_beta_0, float fixed_beta_1, float fixed_gamma_0, float fixed_gamma_1) {

        //   int n = getNumberOfSamples();
        int[] indices_of_samples_sorted_by_error1 = new int[numberOfSamples];
        float rationalValue;
        if (sortWithInsertionsort) {
            indices_of_samples_sorted_by_error = insertionSort_indices(error);
        } else {
//indices_of_samples_sorted_by_error1 = fillIdenticalArray(indices_of_samples_sorted_by_error1);
            indices_of_samples_sorted_by_error1 = indexSort(error, true);
        }

        float numeratorOfObj;
        int h_1, h_2;
        for (int lb = 0; lb < 1; lb++) {
            if (sortWithInsertionsort) {
                h_1 = indices_of_samples_sorted_by_error[lb];
            } else {
                h_1 = indices_of_samples_sorted_by_error1[lb];
            }
            int ub = numberOfSamples - 1;
            numeratorOfObj = maintainedSumOfAllComputedErrorTerms;
            do {
                if (sortWithInsertionsort) {
                    h_2 = indices_of_samples_sorted_by_error[ub];
                } else {
                    h_2 = indices_of_samples_sorted_by_error1[ub];
                }
                rationalValue = numeratorOfObj / maintainedSumOfAllComputedErrorTerms;

                if (rationalValue < bestValueOfObjectiveFoundSoFar) {
                    bestValueOfObjectiveFoundSoFar = rationalValue;

                    //   System.out.println("bestValueOfObjectiveFoundSoFar = " + bestValueOfObjectiveFoundSoFar);
                    //   System.out.println("fixed_parameter_1 = " + fixed_parameter_1);
                    //    System.out.println();
                    if (fixed_parameter_0 != bestParam_0SoFar) {
                        bestParam_0SoFar = fixed_parameter_0;
                        //     System.out.println("bestParam_0SoFar = " + bestParam_0SoFar);
                        //   System.out.println();
                    }
                    //       if (fixed_parameter_1 == 3.0f)
                    //         System.out.println("bug");
                    if (fixed_parameter_1 != bestParam_1SoFar) {
                        bestParam_1SoFar = fixed_parameter_1;
                        //     System.out.println("bestParam_1SoFar = " + bestParam_1SoFar);
                        //      System.out.println();
                    }
                    if (h_1 != sampleWithBesth_1SoFar) {
                        //    System.out.println("sampleWithBesth_1SoFar was: " + sampleWithBesth_1SoFar);
                        sampleWithBesth_1SoFar = h_1;
                        //    System.out.println("now it got: " + sampleWithBesth_1SoFar);
                    }
                    if (h_2 != sampleWithBesth_2SoFar) {
                        //    System.out.println("sampleWithBesth_2SoFar was: " + sampleWithBesth_2SoFar);
                        sampleWithBesth_2SoFar = h_2;
                        //  System.out.print(" bestAlph_0SoFar now: " + bestAlph_0SoFar + ", ");
                        //    System.out.print("bestAlph_1SoFar now: " + bestAlph_1SoFar);
                        //    numberOfOutliers = indices_of_samples_sorted_by_error.length - ub - 1;
                        //    introducingOutliers(indices_of_samples_sorted_by_error);
                    }
                }
                ub--;
                numeratorOfObj -= error[ub];
            } while (ub > 3 * numberOfSamples / 4//       && (ub - lb + 1) >= n
                    );
        }
        if (sortWithInsertionsort) {
            return indices_of_samples_sorted_by_error;
        } else {
            return indices_of_samples_sorted_by_error1;
        }
    }

    public int[] setUpTheObjectiveComputation2(boolean sortWithInsertionsort, int numberOfUnknownParameters, float[] error, float[] fixed_parameters) {

        //   int n = getNumberOfSamples();
        int[] indices_of_samples_sorted_by_error1 = new int[numberOfSamples];
        float rationalValue;
        if (sortWithInsertionsort) {
            indices_of_samples_sorted_by_error = insertionSort_indices(error);
        } else {
//indices_of_samples_sorted_by_error1 = fillIdenticalArray(indices_of_samples_sorted_by_error1);
            indices_of_samples_sorted_by_error1 = indexSort(error, true);
        }
        bestParam_iSoFar = new float[numberOfUnknownParameters];
        float numeratorOfObj;
        int h_1, h_2;
        for (int lb = 0; lb < 1; lb++) {
            if (sortWithInsertionsort) {
                h_1 = indices_of_samples_sorted_by_error[lb];
            } else {
                h_1 = indices_of_samples_sorted_by_error1[lb];
            }
            int ub = numberOfSamples - 1;
            numeratorOfObj = maintainedSumOfAllComputedErrorTerms;
            do {
                if (sortWithInsertionsort) {
                    h_2 = indices_of_samples_sorted_by_error[ub];
                } else {
                    h_2 = indices_of_samples_sorted_by_error1[ub];
                }
                rationalValue = numeratorOfObj / maintainedSumOfAllComputedErrorTerms;

                if (rationalValue < bestValueOfObjectiveFoundSoFar) {
                    bestValueOfObjectiveFoundSoFar = rationalValue;

                    //   System.out.println("bestValueOfObjectiveFoundSoFar = " + bestValueOfObjectiveFoundSoFar);
                    //   System.out.println("fixed_parameter_1 = " + fixed_parameter_1);
                    //    System.out.println();
                    for (int q = 0; q < numberOfUnknownParameters; q++) {
                        if (fixed_parameters[q] != bestParam_iSoFar[q]) {
                            bestParam_iSoFar[q] = fixed_parameters[q];
                            //     System.out.println("bestParam_0SoFar = " + bestParam_0SoFar);
                            //   System.out.println();
                        }
                    }

                    if (h_1 != sampleWithBesth_1SoFar) {
                        //    System.out.println("sampleWithBesth_1SoFar was: " + sampleWithBesth_1SoFar);
                        sampleWithBesth_1SoFar = h_1;
                        //    System.out.println("now it got: " + sampleWithBesth_1SoFar);
                    }
                    if (h_2 != sampleWithBesth_2SoFar) {
                        //    System.out.println("sampleWithBesth_2SoFar was: " + sampleWithBesth_2SoFar);
                        sampleWithBesth_2SoFar = h_2;
                        //  System.out.print(" bestAlph_0SoFar now: " + bestAlph_0SoFar + ", ");
                        //    System.out.print("bestAlph_1SoFar now: " + bestAlph_1SoFar);
                        //    numberOfOutliers = indices_of_samples_sorted_by_error.length - ub - 1;
                        //    introducingOutliers(indices_of_samples_sorted_by_error);
                    }
                }
                ub--;
                numeratorOfObj -= error[ub];
            } while (ub > 3 * numberOfSamples / 4//       && (ub - lb + 1) >= n
                    );
        }
        if (sortWithInsertionsort) {
            return indices_of_samples_sorted_by_error;
        } else {
            return indices_of_samples_sorted_by_error1;
        }
    }

    public desiredParametersToReport findBestParameters(int experimentNumber, int numberOfUnknownParameters, boolean sortWithInsertionsort, int whichPar) {
        desiredParametersToReport desiredResult = new desiredParametersToReport();
        desiredParametersToReport2 b = new desiredParametersToReport2(numberOfUnknownParameters);

        //    int n = getNumberOfSamples();
        bestValueOfObjectiveFoundSoFar = 1.0f;
        sampleWithBesth_1SoFar = 0;
        sampleWithBesth_2SoFar = numberOfSamples - 1;
        int[] indices_of_samples_sorted_by_error1 = new int[numberOfSamples];
        if (sortWithInsertionsort) {
            fillIdenticalArray(indices_of_samples_sorted_by_error);
        }
        int iterator_over_alpha_0 = -1;
        int iterator_over_alpha_1;
        int iterator_over_beta_0 = -1;
        int iterator_over_beta_1;
        int iterator_over_gamma_0 = -1;
        int iterator_over_gamma_1;
        float fixed_parameter_0;
        float fixed_parameter_1;
        if (experimentNumber == 1) {
            switch (whichPar) {
                case 1:
                    tableOfAlpha_1sMultipliedInX_is = new float[alphaSortedParameters.get(0).size()][numberOfSamples];
                    float ithElementOfalpha_1WithoutDuplicates;
                    for (int i = 0; i < alphaSortedParameters.get(1).size(); i++) {
                        ithElementOfalpha_1WithoutDuplicates = alphaSortedParameters.get(1).get(i);
                        for (int j = 0; j < numberOfSamples; j++) {
                            //   tableOfParameter_1sMultipliedInX_is[i][j] = truncate(alpha_1WithoutDuplicates.get(i) * matrixOfData[j][1], 2);
                            tableOfAlpha_1sMultipliedInX_is[i][j] = ithElementOfalpha_1WithoutDuplicates * matrixOfData[j][1];
                        }
                    }
                    //    bestAlpha_0SoFar = parameter_0.get(0);
                    bestAlpha_0SoFar = alphaSortedParameters.get(0).get(0);
                    //  bestAlpha_1SoFar = parameter_1.get(0);
                    bestAlpha_1SoFar = alphaSortedParameters.get(1).get(0);
                    do {
//    System.out.println("iterator_over_parameter_0 = " + iterator_over_parameter_0);
                        for (int w = 0; w < numberOfSamples; w++) {
                            errorTerm[w] = matrixOfData[w][2];
                        }
                        maintainedSumOfAllComputedErrorTerms = 0;
                        iterator_over_alpha_0 += 1;
                        fixed_parameter_0 = alphaSortedParameters.get(0).get(iterator_over_alpha_0);
                        System.out.println("fixed_parameter_0 = " + fixed_parameter_0);
                        iterator_over_alpha_1 = -1;
                        addParameter_0ToTheErrorTerm(fixed_parameter_0);
                        do {
                            //    System.out.println("iterator_over_parameter_1 = " + iterator_over_parameter_1);
                            iterator_over_alpha_1 += 1;

                            fixed_parameter_1 = alphaSortedParameters.get(1).get(iterator_over_alpha_1);
                            //   System.out.print("\nfixed_parameter_1 " + fixed_parameter_1);
                            //    System.out.print("\n");
                            addParameter_1TermToTheErrorTerm(iterator_over_alpha_1, fixed_parameter_1, tableOfAlpha_1sMultipliedInX_is);
                            //    if (!solveOverLeftCenter && solveOverCenter && !solveOverRightCenter) {
                            indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_parameter_0, fixed_parameter_1, 0, 0, 0, 0);
                            //   }

                            substractParameter_1TermFromTheErrorTerm(iterator_over_alpha_1, fixed_parameter_1, tableOfAlpha_1sMultipliedInX_is);
                        } while (iterator_over_alpha_1 < alphaSortedParameters.get(1).size() - 1);
                    } while (iterator_over_alpha_0 < alphaSortedParameters.get(0).size() - 1);
                    //  desiredResult[0] = bestParam_0SoFar;
                    //     desiredResult[1] = bestParam_1SoFar;
                    desiredResult.setFirstParameter(bestParam_0SoFar);
                    desiredResult.setSecondParameter(bestParam_1SoFar);
                    //      sumOfAllbestAlpha_0sSoFar += bestParam_0SoFar;
                    //      sumOfAllbestAlpha_1sSoFar += bestParam_1SoFar;

// desiredResult[2] = bestAlpha_0SoFar;
                    break;
                case 2:
                    tableOfBeta_1sMultipliedInX_is = new float[betaSortedParameters.get(0).size()][numberOfSamples];
                    for (int i = 0; i < betaSortedParameters.get(1).size(); i++) {
                        for (int j = 0; j < numberOfSamples; j++) {
                            //   tableOfParameter_1sMultipliedInX_is[i][j] = truncate(alpha_1WithoutDuplicates.get(i) * matrixOfData[j][1], 2);
                            tableOfBeta_1sMultipliedInX_is[i][j] = betaSortedParameters.get(1).get(i) * matrixOfData[j][1];
                        }
                    }
                    bestBeta_0SoFar = betaSortedParameters.get(0).get(0);
                    bestBeta_1SoFar = betaSortedParameters.get(1).get(0);
                    do {
//    System.out.println("iterator_over_parameter_0 = " + iterator_over_parameter_0);
                        for (int w = 0; w < numberOfSamples; w++) {
                            errorTerm[w] = matrixOfData[w][3];
                        }
                        maintainedSumOfAllComputedErrorTerms = 0;

                        iterator_over_beta_0 += 1;
                        fixed_parameter_0 = betaSortedParameters.get(0).get(iterator_over_beta_0);
//   System.out.println("fixed_parameter_0 = " + fixed_parameter_0);
                        iterator_over_beta_1 = -1;
                        addParameter_0ToTheErrorTerm(fixed_parameter_0);
                        do {
                            //    System.out.println("iterator_over_parameter_1 = " + iterator_over_parameter_1);
                            iterator_over_beta_1 += 1;

                            fixed_parameter_1 = betaSortedParameters.get(1).get(iterator_over_beta_1);
                            //   System.out.print("\nfixed_parameter_1 " + fixed_parameter_1);
                            //    System.out.print("\n");
                            addParameter_1TermToTheErrorTerm(iterator_over_beta_1, fixed_parameter_1, tableOfBeta_1sMultipliedInX_is);
                            //    if (!solveOverLeftCenter && solveOverCenter && !solveOverRightCenter) {
                            indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_parameter_0, fixed_parameter_1, 0, 0, 0, 0);
                            //   }

                            substractParameter_1TermFromTheErrorTerm(iterator_over_beta_1, fixed_parameter_1, tableOfBeta_1sMultipliedInX_is);
                        } while (iterator_over_beta_1 < betaSortedParameters.get(1).size() - 1);
                    } while (iterator_over_beta_0 < betaSortedParameters.get(0).size() - 1);
                    desiredResult.setFirstParameter(bestParam_0SoFar);
                    desiredResult.setSecondParameter(bestParam_1SoFar);
                    //      sumOfAllbestBeta_0sSoFar += bestParam_0SoFar;
                    //     sumOfAllbestBeta_1sSoFar += bestParam_1SoFar;
                    // desiredResult[2] = bestAlpha_0SoFar;
                    break;
                case 3:
                    tableOfGamma_1sMultipliedInX_is = new float[gammaSortedParameters.get(0).size()][numberOfSamples];
                    for (int i = 0; i < gammaSortedParameters.get(1).size(); i++) {
                        for (int j = 0; j < numberOfSamples; j++) {
                            //   tableOfParameter_1sMultipliedInX_is[i][j] = truncate(alpha_1WithoutDuplicates.get(i) * matrixOfData[j][1], 2);
                            tableOfGamma_1sMultipliedInX_is[i][j] = gammaSortedParameters.get(1).get(i) * matrixOfData[j][1];
                        }
                    }
                    bestGamma_0SoFar = gammaSortedParameters.get(0).get(0);
                    bestGamma_1SoFar = gammaSortedParameters.get(1).get(0);
                    do {
//    System.out.println("iterator_over_parameter_0 = " + iterator_over_parameter_0);
                        for (int w = 0; w < numberOfSamples; w++) {
                            errorTerm[w] = matrixOfData[w][4];
                        }
                        maintainedSumOfAllComputedErrorTerms = 0;
                        iterator_over_gamma_0 += 1;
                        fixed_parameter_0 = gammaSortedParameters.get(0).get(iterator_over_gamma_0);
//   System.out.println("fixed_parameter_0 = " + fixed_parameter_0);
                        iterator_over_gamma_1 = -1;
                        addParameter_0ToTheErrorTerm(fixed_parameter_0);
                        do {
                            //    System.out.println("iterator_over_parameter_1 = " + iterator_over_parameter_1);
                            iterator_over_gamma_1 += 1;

                            fixed_parameter_1 = gammaSortedParameters.get(1).get(iterator_over_gamma_1);
                            //   System.out.print("\nfixed_parameter_1 " + fixed_parameter_1);
                            //    System.out.print("\n");
                            addParameter_1TermToTheErrorTerm(iterator_over_gamma_1, fixed_parameter_1, tableOfGamma_1sMultipliedInX_is);
                            //    if (!solveOverLeftCenter && solveOverCenter && !solveOverRightCenter) {
                            indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_parameter_0, fixed_parameter_1, 0, 0, 0, 0);
                            //   }

                            substractParameter_1TermFromTheErrorTerm(iterator_over_gamma_1, fixed_parameter_1, tableOfGamma_1sMultipliedInX_is);
                        } while (iterator_over_gamma_1 < gammaSortedParameters.get(1).size() - 1);
                    } while (iterator_over_gamma_0 < gammaSortedParameters.get(0).size() - 1);
                    desiredResult.setFirstParameter(bestParam_0SoFar);
                    desiredResult.setSecondParameter(bestParam_1SoFar);
                    //     sumOfAllbestGamma_0sSoFar += bestParam_0SoFar;
                    //    sumOfAllbestGamma_1sSoFar += bestParam_1SoFar;
                    // desiredResult[2] = bestAlpha_0SoFar;
                    break;
                default:
                    break;
            }
        }
        /* else if (experimentNumber == 2) {
            int iterator_over_alpha_2;
            int iterator_over_alpha_3;
            int iterator_over_alpha_4;
            int iterator_over_alpha_5;
            int iterator_over_beta_2 = -1;
            int iterator_over_beta_3 = -1;
            int iterator_over_beta_4 = -1;
            int iterator_over_beta_5 = -1;
            int iterator_over_gamma_2 = -1;
            int iterator_over_gamma_3 = -1;
            int iterator_over_gamma_4 = -1;
            int iterator_over_gamma_5 = -1;
            float fixed_parameter_2;
            float fixed_parameter_3;
            float fixed_parameter_4;
            float fixed_parameter_5;
            switch (whichPar) {
                case 1:
//                    tableOfAlpha_1sMultipliedInX_is = new float[alpha_1WithoutDuplicates.size()][n];
//                    float ithElementOfalpha_1WithoutDuplicates;
//                    for (int i = 0; i < alpha_1WithoutDuplicates.size(); i++) {
//                        ithElementOfalpha_1WithoutDuplicates = alpha_1WithoutDuplicates.get(i);
//                        for (int j = 0; j < n; j++) {
//                            tableOfAlpha_1sMultipliedInX_is[i][j] = ithElementOfalpha_1WithoutDuplicates * matrixOfData[j][1];
//                        }
//                    }
                    bestAlpha_0SoFar = parameter_0.get(0);
                    bestAlpha_1SoFar = parameter_1.get(0);
                    bestAlpha_2SoFar = alpha_2WithoutDuplicates.get(0);
                    bestAlpha_3SoFar = alpha_3WithoutDuplicates.get(0);
                    bestAlpha_4SoFar = alpha_4WithoutDuplicates.get(0);
                    bestAlpha_5SoFar = alpha_5WithoutDuplicates.get(0);
                    do {
//                        for (int w = 0; w < n; w++) {
//                            errorTerm[w] = matrixOfData[w][6];
//                        }
                        int iterator_over_columns = 0;
                        maintainedSumOfAllComputedErrorTerms = 0;
                        iterator_over_alpha_0 += 1;
                        fixed_parameter_0 = parameter_0.get(iterator_over_alpha_0);
                        System.out.println("fixed_parameter_0 = " + fixed_parameter_0);
                        iterator_over_alpha_1 = -1;
                        //    addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_0);
                        developTheSum(iterator_over_columns, fixed_parameter_0);
                        iterator_over_columns = 1;
                        do {
                            iterator_over_alpha_1 += 1;
                            fixed_parameter_1 = parameter_1.get(iterator_over_alpha_1);
                            //   addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_1);
                            developTheSum(iterator_over_columns, fixed_parameter_1);
                            iterator_over_columns = 2;
                            iterator_over_alpha_2 = -1;
                            do {
                                iterator_over_alpha_2 += 1;
                                System.out.println("iterator_over_alpha_2 = " + iterator_over_alpha_2);
                                fixed_parameter_2 = alpha_2WithoutDuplicates.get(iterator_over_alpha_2);
                                //    addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_2);
                                developTheSum(iterator_over_columns, fixed_parameter_2);
                                iterator_over_columns = 3;
                                iterator_over_alpha_3 = -1;
                                do {
                                    iterator_over_alpha_3 += 1;
                                    //    System.out.println("iterator_over_alpha_3 = " + iterator_over_alpha_3);
                                    fixed_parameter_3 = alpha_3WithoutDuplicates.get(iterator_over_alpha_3);
                                    //    addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_3);
                                    developTheSum(iterator_over_columns, fixed_parameter_3);
                                    iterator_over_columns = 4;
                                    iterator_over_alpha_4 = -1;
                                    do {
                                        iterator_over_alpha_4 += 1;
                                        //    System.out.println("iterator_over_alpha_4 = " + iterator_over_alpha_4);
                                        fixed_parameter_4 = alpha_4WithoutDuplicates.get(iterator_over_alpha_4);
                                        //    addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_4);
                                        developTheSum(iterator_over_columns, fixed_parameter_4);
                                        iterator_over_columns = 5;
                                        iterator_over_alpha_5 = -1;
                                        do {
                                            iterator_over_alpha_5 += 1;
                                            //    System.out.println("iterator_over_alpha_5 = " + iterator_over_alpha_5);
                                            fixed_parameter_5 = alpha_5WithoutDuplicates.get(iterator_over_alpha_5);
                                            developTheSum(iterator_over_columns, fixed_parameter_5);
                                            addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_5);
                                            indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation2(sortWithInsertionsort, errorTerm, fixed_parameter_0, fixed_parameter_1, fixed_parameter_2, fixed_parameter_3, fixed_parameter_4, fixed_parameter_5);
                                            //I have work here
                                            //   substractParameter_iTermFromTheErrorTerm(iterator_over_columns, fixed_parameter_5);
                                            substractFromTheSum(iterator_over_columns, fixed_parameter_5);
                                            //    iterator_over_columns = 0;
                                        } while (iterator_over_alpha_5 < sizeOfAlpha_5WithoutDuplicates - 1);
                                        substractFromTheSum(--iterator_over_columns, fixed_parameter_4);
                                    } while (iterator_over_alpha_4 < sizeOfAlpha_4WithoutDuplicates - 1);
                                    substractFromTheSum(--iterator_over_columns, fixed_parameter_3);
                                } while (iterator_over_alpha_3 < sizeOfAlpha_3WithoutDuplicates - 1);
                                substractFromTheSum(--iterator_over_columns, fixed_parameter_2);
                            } while (iterator_over_alpha_2 < sizeOfAlpha_2WithoutDuplicates - 1);
                            substractFromTheSum(--iterator_over_columns, fixed_parameter_1);
                        } while (iterator_over_alpha_1 < sizeOfAlpha_1WithoutDuplicates - 1);
                        substractFromTheSum(--iterator_over_columns, fixed_parameter_0);
                    } while (iterator_over_alpha_0 < sizeOfAlpha_0WithoutDuplicates - 1);
                    desiredResult.setFirstParameter(bestParam_0SoFar);
                    desiredResult.setSecondParameter(bestParam_1SoFar);
                    desiredResult.setThirdParameter(bestParam_2SoFar);
                    desiredResult.setFourthParameter(bestParam_3SoFar);
                    desiredResult.setFifthParameter(bestParam_4SoFar);
                    desiredResult.setSixthParameter(bestParam_5SoFar);
                    break;
                case 2:
                    tableOfBeta_1sMultipliedInX_is = new float[parameter_0.size()][n];
                    for (int i = 0; i < parameter_1.size(); i++) {
                        for (int j = 0; j < n; j++) {
                            tableOfBeta_1sMultipliedInX_is[i][j] = parameter_1.get(i) * matrixOfData[j][1];
                        }
                    }
                    bestBeta_0SoFar = parameter_0.get(0);
                    bestBeta_1SoFar = parameter_1.get(0);
                    do {
                        for (int w = 0; w < n; w++) {
                            errorTerm[w] = matrixOfData[w][3];
                        }
                        maintainedSumOfAllComputedErrorTerms = 0;

                        iterator_over_beta_0 += 1;
                        fixed_parameter_0 = parameter_0.get(iterator_over_beta_0);
                        iterator_over_beta_1 = -1;
                        addParameter_0ToTheErrorTerm(fixed_parameter_0);
                        do {
                            iterator_over_beta_1 += 1;
                            fixed_parameter_1 = parameter_1.get(iterator_over_beta_1);
                            addParameter_1TermToTheErrorTerm(iterator_over_beta_1, fixed_parameter_1, tableOfBeta_1sMultipliedInX_is);
                            indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_parameter_0, fixed_parameter_1, 0, 0, 0, 0);
                            substractParameter_1TermFromTheErrorTerm(iterator_over_beta_1, fixed_parameter_1, tableOfBeta_1sMultipliedInX_is);
                        } while (iterator_over_beta_1 < sizeOfBeta_1WithoutDuplicates - 1);
                    } while (iterator_over_beta_0 < sizeOfBeta_0WithoutDuplicates - 1);
                    desiredResult.setFirstParameter(bestParam_0SoFar);
                    desiredResult.setSecondParameter(bestParam_1SoFar);
                    break;
                case 3:
                    tableOfGamma_1sMultipliedInX_is = new float[parameter_0.size()][n];
                    for (int i = 0; i < parameter_1.size(); i++) {
                        for (int j = 0; j < n; j++) {
                            tableOfGamma_1sMultipliedInX_is[i][j] = parameter_1.get(i) * matrixOfData[j][1];
                        }
                    }
                    bestGamma_0SoFar = parameter_0.get(0);
                    bestGamma_1SoFar = parameter_1.get(0);
                    do {
                        for (int w = 0; w < n; w++) {
                            errorTerm[w] = matrixOfData[w][4];
                        }
                        maintainedSumOfAllComputedErrorTerms = 0;
                        iterator_over_gamma_0 += 1;
                        fixed_parameter_0 = parameter_0.get(iterator_over_gamma_0);
                        iterator_over_gamma_1 = -1;
                        addParameter_0ToTheErrorTerm(fixed_parameter_0);
                        do {
                            iterator_over_gamma_1 += 1;
                            fixed_parameter_1 = parameter_1.get(iterator_over_gamma_1);
                            addParameter_1TermToTheErrorTerm(iterator_over_gamma_1, fixed_parameter_1, tableOfGamma_1sMultipliedInX_is);
                            indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_parameter_0, fixed_parameter_1, 0, 0, 0, 0);
                            substractParameter_1TermFromTheErrorTerm(iterator_over_gamma_1, fixed_parameter_1, tableOfGamma_1sMultipliedInX_is);
                        } while (iterator_over_gamma_1 < sizeOfGamma_1WithoutDuplicates - 1);
                    } while (iterator_over_gamma_0 < sizeOfGamma_0WithoutDuplicates - 1);
                    desiredResult.setFirstParameter(bestParam_0SoFar);
                    desiredResult.setSecondParameter(bestParam_1SoFar);
                    break;
                default:
                    break;
            }
        }
         */
        int[] arrayOfOutliers;
        if (sortWithInsertionsort) {
            arrayOfOutliers = arrayOfOutlierSamples(indices_of_samples_sorted_by_error, sortWithInsertionsort, whichPar);
        } else {
            arrayOfOutliers = arrayOfOutlierSamples(indices_of_samples_sorted_by_error1, sortWithInsertionsort, whichPar);
        }

        System.out.println("bestValueOfObjectiveFoundSoFar: " + bestValueOfObjectiveFoundSoFar);
        System.out.println("sampleWithBesth_1SoFar: " + sampleWithBesth_1SoFar);
        System.out.println("sampleWithBesth_2SoFar: " + sampleWithBesth_2SoFar);
        System.out.println("bestParam_0: " + bestParam_0SoFar);
        System.out.println("bestParam_1: " + bestParam_1SoFar);
        //    System.out.println("number of outliers: " + arrayOfOutliers.length);
        //   System.out.println("outliers are:");
        //    Arrays.sort(arrayOfOutliers);
        //   printArray(arrayOfOutliers);
        desiredResult.setNumbOfOutliers(arrayOfOutliers.length);

        return desiredResult;
    }

    /*
    public desiredParametersToReport findBestParameters2(int experimentNumber, boolean sortWithInsertionsort, int numberOfUnknownParameters, float[][] all_parameters, int whichPar) {
        desiredParametersToReport desiredResult = new desiredParametersToReport();
        //    int n = getNumberOfSamples();
        bestValueOfObjectiveFoundSoFar = 1.0f;
        sampleWithBesth_1SoFar = 0;
        sampleWithBesth_2SoFar = n - 1;
        int[] indices_of_samples_sorted_by_error1 = new int[n];
        if (sortWithInsertionsort) {
            fillIdenticalArray(indices_of_samples_sorted_by_error);
        }
        int iterator_over_alpha_0 = -1;
        int iterator_over_alpha_1;
        int iterator_over_beta_0 = -1;
        int iterator_over_beta_1;
        int iterator_over_gamma_0 = -1;
        int iterator_over_gamma_1;
        float fixed_parameter_0;
        //   float fixed_parameter_1;
        float[] fixed_parameters = new float[numberOfUnknownParameters];
        int iterator_over_alpha_2;
        int iterator_over_alpha_3;
        int iterator_over_alpha_4;
        int iterator_over_alpha_5;
        int iterator_over_beta_2 = -1;
        int iterator_over_beta_3 = -1;
        int iterator_over_beta_4 = -1;
        int iterator_over_beta_5 = -1;
        int iterator_over_gamma_2 = -1;
        int iterator_over_gamma_3 = -1;
        int iterator_over_gamma_4 = -1;
        int iterator_over_gamma_5 = -1;
        //    float fixed_parameter_2;
        //    float fixed_parameter_3;
        //    float fixed_parameter_4;
        //    float fixed_parameter_5;
        switch (whichPar) {
            case 1:
//                    tableOfAlpha_1sMultipliedInX_is = new float[alpha_1WithoutDuplicates.size()][n];
//                    float ithElementOfalpha_1WithoutDuplicates;
//                    for (int i = 0; i < alpha_1WithoutDuplicates.size(); i++) {
//                        ithElementOfalpha_1WithoutDuplicates = alpha_1WithoutDuplicates.get(i);
//                        for (int j = 0; j < n; j++) {
//                            tableOfAlpha_1sMultipliedInX_is[i][j] = ithElementOfalpha_1WithoutDuplicates * matrixOfData[j][1];
//                        }
//                    }
//                bestAlpha_0SoFar = (float) all_parameters[0][0];
//                bestAlpha_1SoFar = (float) all_parameters[1][0];
//                bestAlpha_2SoFar = (float) all_parameters[2][0];
//                bestAlpha_3SoFar = (float) all_parameters[3][0];
//                bestAlpha_4SoFar = (float) all_parameters[4][0];
//                bestAlpha_5SoFar = (float) all_parameters[5][0];
                bestAlpha_iSoFar = new float[numberOfUnknownParameters];
                for (int t = 0; t < numberOfUnknownParameters; t++) {
                    bestAlpha_iSoFar[t] = (float) all_parameters[t][0];
                }
                do {
//                        for (int w = 0; w < n; w++) {
//                            errorTerm[w] = matrixOfData[w][6];
//                        }
                    int iterator_over_columns = 0;
                    maintainedSumOfAllComputedErrorTerms = 0;
                    iterator_over_alpha_0 += 1;
                    //    fixed_parameter_0 = alpha_0WithoutDuplicates.get(iterator_over_alpha_0);
                    //    fixed_parameter_0 = (float) all_parameters[0][iterator_over_alpha_0];
                    fixed_parameters[0] = (float) all_parameters[0][iterator_over_alpha_0];
                    System.out.println("fixed_parameter_0 = " + fixed_parameters[0]);
                    iterator_over_alpha_1 = -1;
                    //    addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_0);
                    developTheSum(iterator_over_columns, fixed_parameters[0]);
                    iterator_over_columns = 1;
                    do {
                        iterator_over_alpha_1 += 1;
                        //    fixed_parameter_1 = alpha_1WithoutDuplicates.get(iterator_over_alpha_1);

                        fixed_parameters[1] = (float) all_parameters[1][iterator_over_alpha_1];

                        //   addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_1);
                        developTheSum(iterator_over_columns, fixed_parameters[1]);

                        if (experimentNumber == 2) {
                            iterator_over_columns = 2;
                            iterator_over_alpha_2 = -1;
                            do {
                                iterator_over_alpha_2 += 1;
                                System.out.println("iterator_over_alpha_2 = " + iterator_over_alpha_2);
                                //    fixed_parameter_2 = alpha_2WithoutDuplicates.get(iterator_over_alpha_2);
                                //    addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_2);
                                fixed_parameters[2] = (float) all_parameters[2][iterator_over_alpha_2];

                                developTheSum(iterator_over_columns, fixed_parameters[2]);
                                iterator_over_columns = 3;
                                iterator_over_alpha_3 = -1;
                                do {
                                    iterator_over_alpha_3 += 1;
                                    //    System.out.println("iterator_over_alpha_3 = " + iterator_over_alpha_3);
                                    fixed_parameters[3] = (float) all_parameters[3][iterator_over_alpha_3];
                                    //    addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_3);
                                    developTheSum(iterator_over_columns, fixed_parameters[3]);
                                    iterator_over_columns = 4;
                                    iterator_over_alpha_4 = -1;
                                    do {
                                        iterator_over_alpha_4 += 1;
                                        //    System.out.println("iterator_over_alpha_4 = " + iterator_over_alpha_4);
                                        fixed_parameters[4] = (float) all_parameters[4][iterator_over_alpha_4];
                                        //    addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameter_4);
                                        developTheSum(iterator_over_columns, fixed_parameters[4]);
                                        iterator_over_columns = 5;
                                        iterator_over_alpha_5 = -1;
                                        do {
                                            iterator_over_alpha_5 += 1;
                                            //    System.out.println("iterator_over_alpha_5 = " + iterator_over_alpha_5);
                                            fixed_parameters[5] = (float) all_parameters[5][iterator_over_alpha_5];
                                            developTheSum(iterator_over_columns, fixed_parameters[5]);
                                            addParameter_iTermToTheErrorTerm(iterator_over_columns, fixed_parameters[5]);
                                            indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation2(sortWithInsertionsort, numberOfUnknownParameters, errorTerm, fixed_parameters);
                                            //I have work here
                                            //   substractParameter_iTermFromTheErrorTerm(iterator_over_columns, fixed_parameter_5);
                                            substractFromTheSum(iterator_over_columns, fixed_parameters[5]);
                                            //    iterator_over_columns = 0;
                                        } while (iterator_over_alpha_5 < sizeOfAlpha_5WithoutDuplicates - 1);
                                        substractFromTheSum(--iterator_over_columns, fixed_parameters[4]);
                                    } while (iterator_over_alpha_4 < sizeOfAlpha_4WithoutDuplicates - 1);
                                    substractFromTheSum(--iterator_over_columns, fixed_parameters[3]);
                                } while (iterator_over_alpha_3 < sizeOfAlpha_3WithoutDuplicates - 1);
                                substractFromTheSum(--iterator_over_columns, fixed_parameters[2]);
                            } while (iterator_over_alpha_2 < sizeOfAlpha_2WithoutDuplicates - 1);
                        }

                        substractFromTheSum(--iterator_over_columns, fixed_parameters[1]);
                    } while (iterator_over_alpha_1 < sizeOfAlpha_1WithoutDuplicates - 1);
                    substractFromTheSum(--iterator_over_columns, fixed_parameters[0]);
                } while (iterator_over_alpha_0 < sizeOfAlpha_0WithoutDuplicates - 1);
                desiredResult.setFirstParameter(bestParam_0SoFar);
                desiredResult.setSecondParameter(bestParam_1SoFar);
                desiredResult.setThirdParameter(bestParam_2SoFar);
                desiredResult.setFourthParameter(bestParam_3SoFar);
                desiredResult.setFifthParameter(bestParam_4SoFar);
                desiredResult.setSixthParameter(bestParam_5SoFar);
                break;
            case 2:
                tableOfBeta_1sMultipliedInX_is = new float[parameter_0.size()][n];
                for (int i = 0; i < parameter_1.size(); i++) {
                    for (int j = 0; j < n; j++) {
                        tableOfBeta_1sMultipliedInX_is[i][j] = parameter_1.get(i) * matrixOfData[j][1];
                    }
                }
                bestBeta_0SoFar = parameter_0.get(0);
                bestBeta_1SoFar = parameter_1.get(0);
                do {
                    for (int w = 0; w < n; w++) {
                        errorTerm[w] = matrixOfData[w][3];
                    }
                    maintainedSumOfAllComputedErrorTerms = 0;

                    iterator_over_beta_0 += 1;
                    fixed_parameter_0 = parameter_0.get(iterator_over_beta_0);
                    iterator_over_beta_1 = -1;
                    addParameter_0ToTheErrorTerm(fixed_parameter_0);
                    do {
                        iterator_over_beta_1 += 1;
                        fixed_parameter_1 = parameter_1.get(iterator_over_beta_1);
                        addParameter_1TermToTheErrorTerm(iterator_over_beta_1, fixed_parameter_1, tableOfBeta_1sMultipliedInX_is);
                        indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_parameter_0, fixed_parameter_1, 0, 0, 0, 0);
                        substractParameter_1TermFromTheErrorTerm(iterator_over_beta_1, fixed_parameter_1, tableOfBeta_1sMultipliedInX_is);
                    } while (iterator_over_beta_1 < sizeOfBeta_1WithoutDuplicates - 1);
                } while (iterator_over_beta_0 < sizeOfBeta_0WithoutDuplicates - 1);
                desiredResult.setFirstParameter(bestParam_0SoFar);
                desiredResult.setSecondParameter(bestParam_1SoFar);
                break;
            case 3:
                tableOfGamma_1sMultipliedInX_is = new float[parameter_1.size()][n];
                for (int i = 0; i < parameter_1.size(); i++) {
                    for (int j = 0; j < n; j++) {
                        tableOfGamma_1sMultipliedInX_is[i][j] = parameter_1.get(i) * matrixOfData[j][1];
                    }
                }
                bestGamma_0SoFar = parameter_0.get(0);
                bestGamma_1SoFar = parameter_1.get(0);
                do {
                    for (int w = 0; w < n; w++) {
                        errorTerm[w] = matrixOfData[w][4];
                    }
                    maintainedSumOfAllComputedErrorTerms = 0;
                    iterator_over_gamma_0 += 1;
                    fixed_parameter_0 = parameter_0.get(iterator_over_gamma_0);
                    iterator_over_gamma_1 = -1;
                    addParameter_0ToTheErrorTerm(fixed_parameter_0);
                    do {
                        iterator_over_gamma_1 += 1;
                        fixed_parameter_1 = parameter_1.get(iterator_over_gamma_1);
                        addParameter_1TermToTheErrorTerm(iterator_over_gamma_1, fixed_parameter_1, tableOfGamma_1sMultipliedInX_is);
                        indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_parameter_0, fixed_parameter_1, 0, 0, 0, 0);
                        substractParameter_1TermFromTheErrorTerm(iterator_over_gamma_1, fixed_parameter_1, tableOfGamma_1sMultipliedInX_is);
                    } while (iterator_over_gamma_1 < sizeOfGamma_1WithoutDuplicates - 1);
                } while (iterator_over_gamma_0 < sizeOfGamma_0WithoutDuplicates - 1);
                desiredResult.setFirstParameter(bestParam_0SoFar);
                desiredResult.setSecondParameter(bestParam_1SoFar);
                break;
            default:
                break;
        }

        int[] arrayOfOutliers;
        if (sortWithInsertionsort) {
            arrayOfOutliers = arrayOfOutlierSamples(indices_of_samples_sorted_by_error, sortWithInsertionsort, whichPar);
        } else {
            arrayOfOutliers = arrayOfOutlierSamples(indices_of_samples_sorted_by_error1, sortWithInsertionsort, whichPar);
        }

        System.out.println("bestValueOfObjectiveFoundSoFar: " + bestValueOfObjectiveFoundSoFar);
        System.out.println("sampleWithBesth_1SoFar: " + sampleWithBesth_1SoFar);
        System.out.println("sampleWithBesth_2SoFar: " + sampleWithBesth_2SoFar);
        System.out.println("bestParam_0: " + bestParam_0SoFar);
        System.out.println("bestParam_1: " + bestParam_1SoFar);
        //    System.out.println("number of outliers: " + arrayOfOutliers.length);
        //   System.out.println("outliers are:");
        //    Arrays.sort(arrayOfOutliers);
        //   printArray(arrayOfOutliers);
        desiredResult.setNumbOfOutliers(arrayOfOutliers.length);

        return desiredResult;
    }
     */
    public int[] arrayOfOutlierSamples(int[] indices_of_samples_sorted_by_error1, boolean sortWithInsertionsort, int whichPar
    ) {
        //    int n = getNumberOfSamples();
        float[] tableOf_column_yMinusParameter_0s;
        tableOf_column_yMinusParameter_0s = new float[numberOfSamples];
        float[] thisError = new float[numberOfSamples];
        for (int o = 0; o < numberOfSamples; o++) {
            tableOf_column_yMinusParameter_0s[o] = matrixOfData[o][whichPar + 1] - bestParam_0SoFar;
        }
        for (int o = 0; o < numberOfSamples; o++) {
            //   if (!solveOverLeftCenter && solveOverCenter && !solveOverRightCenter) {
            thisError[o] = Math.abs(tableOf_column_yMinusParameter_0s[o] - bestParam_1SoFar * matrixOfData[o][1]);
            //  } else {
            // thisError[o] = Math.abs(tableOf_column_yMinusParameter_0s[o] - bestAlph_1SoFar * matrixOfData[o][1]) + coefficientOfSecondErrorTerm * Math.abs(tableOf_column_lMinusBetta_0s[o] - bestBeta_1SoFar * matrixOfData[o][1]) + coefficientOfThirdErrorTerm * Math.abs(tableOf_column_rMinusGamma_0s[o] - bestGamma_1SoFar * matrixOfData[o][1]);
            //  }
        }
        int tempBound = 0;
        fillIdenticalArray(indices_of_samples_sorted_by_error);
        insertionSort_indices(thisError);
        for (int o = 0; o < numberOfSamples; o++) {
            if (indices_of_samples_sorted_by_error1[o] == sampleWithBesth_2SoFar) {
                tempBound = o;
                break;
            }
        }
        int[] arrayOfOutliers = new int[numberOfSamples - tempBound - 1];
        for (int y = 0; y < arrayOfOutliers.length; y++) {
            arrayOfOutliers[y] = indices_of_samples_sorted_by_error1[tempBound++];
        }
        return arrayOfOutliers;
    }

    public void addParameter_0ToTheErrorTerm(float fixed_something_0) {
        //    int n = getNumberOfSamples();
        for (int w = 0; w < numberOfSamples; w++) {
            errorTerm[w] -= fixed_something_0;
            maintainedSumOfAllComputedErrorTerms += errorTerm[w];
        }
    }

    public void substractParameter_0FromTheErrorTerm(int whichColumn, float fixed_something_0) {
        //    int n = getNumberOfSamples();
        for (int w = 0; w < numberOfSamples; w++) {
            errorTerm[w] += fixed_something_0;
        }
    }

    public float[] fillInAnArrayWithCoefficient(int whichColumn, float fixed_something_0) {
        //    int n = getNumberOfSamples();

        float[] tableOf_column_something_MinusParameter_0s = new float[numberOfSamples];
        for (int w = 0; w < numberOfSamples; w++) {
            tableOf_column_something_MinusParameter_0s[w] = matrixOfData[w][whichColumn] - fixed_something_0;
        }
        return tableOf_column_something_MinusParameter_0s;
    }

//    public void unfillInArrayWithCoefficientBeta(float[] tableOf_l_iMinusBeta_0s) {
//        int n = getNumberOfSamples();
//
//        for (int w = 0; w < n; w++) {
//            errorTerm[w] -= coefficientOfSecondErrorTerm * tableOf_l_iMinusBeta_0s[w];
//        }
//    }
//
//    public void unfillInArrayWithCoefficientGamma(float[] tableOf_column_rMinusGamma_0s) {
//        int n = getNumberOfSamples();
//
//        for (int w = 0; w < n; w++) {
//            errorTerm[w] -= coefficientOfThirdErrorTerm * tableOf_column_rMinusGamma_0s[w];
//        }
//    }
    public void addParameter_1TermToTheErrorTerm(int iterator_over_parameter_1, float fixed_something_1, float[][] tableOfAlpha_1sMultipliedInX_is) {
        //   int n = getNumberOfSamples();
        for (int i = 0; i < numberOfSamples; i++) {
            if (errorTerm[i] >= tableOfAlpha_1sMultipliedInX_is[iterator_over_parameter_1][i]) {
                errorTerm[i] += (-tableOfAlpha_1sMultipliedInX_is[iterator_over_parameter_1][i]);
                maintainedSumOfAllComputedErrorTerms += (-tableOfAlpha_1sMultipliedInX_is[iterator_over_parameter_1][i]);
            } else {
                float prevValue = errorTerm[i];
                errorTerm[i] = -errorTerm[i] + tableOfAlpha_1sMultipliedInX_is[iterator_over_parameter_1][i];
                maintainedSumOfAllComputedErrorTerms += (errorTerm[i] - prevValue);
                signOfThisCorrespondingErrorTermIsNegative[i] = true;
            }
        }
    }

    public void addParameter_iTermToTheErrorTerm(int iterator_over_columns, float fixed_something_i) {

        for (int i = 0; i < numberOfSamples; i++) {
            if (matrixOfData[i][6] > sumOfAllParamTimesX_jsSoFar[i]) {
                errorTerm[i] = matrixOfData[i][6] - sumOfAllParamTimesX_jsSoFar[i];
            } else {
                errorTerm[i] = -matrixOfData[i][6] + sumOfAllParamTimesX_jsSoFar[i];
            }
            maintainedSumOfAllComputedErrorTerms += errorTerm[i];
        }
    }

    public void developTheSum(int iterator_over_columns, float fixed_something_i) {
        for (int i = 0; i < numberOfSamples; i++) {
            sumOfAllParamTimesX_jsSoFar[i] += fixed_something_i * matrixOfData[i][iterator_over_columns];
        }
    }

    public void substractFromTheSum(int iterator_over_columns, float fixed_something_i) {
        for (int i = 0; i < numberOfSamples; i++) {
            sumOfAllParamTimesX_jsSoFar[i] -= fixed_something_i * matrixOfData[i][iterator_over_columns];
        }
    }

    public void substractParameter_1TermFromTheErrorTerm(int iterator_over_parameter_1, float fixed_something_1, float[][] tableOfAlpha_1sMultipliedInX_is) {
        float temp;
        for (int i = 0; i < numberOfSamples; i++) {
            temp = tableOfAlpha_1sMultipliedInX_is[iterator_over_parameter_1][i];
            if (!signOfThisCorrespondingErrorTermIsNegative[i]) {
                errorTerm[i] += temp;
                maintainedSumOfAllComputedErrorTerms += temp;
            } else {
                //    error[i] -= truncate(fixed_something_1 * matrixOfData[i][1], 2);
                float prevVal = errorTerm[i];
                errorTerm[i] = -errorTerm[i] + temp;
                maintainedSumOfAllComputedErrorTerms += (errorTerm[i] - prevVal);
                signOfThisCorrespondingErrorTermIsNegative[i] = false;
            }

        }
    }

    public int[] fillIdenticalArray(int[] a) {
        for (int r = 0; r < numberOfSamples; r++) {
            a[r] = r;
        }
        return a;
    }

    public int[] insertionSort_indices(float[] error) {
        for (int i = 1; i < numberOfSamples; i++) {
            int v = indices_of_samples_sorted_by_error[i];
            int j = i - 1;
            while (j >= 0 && error[indices_of_samples_sorted_by_error[j]] > error[v]) {
                indices_of_samples_sorted_by_error[j + 1] = indices_of_samples_sorted_by_error[j];
                j--;
            }
            indices_of_samples_sorted_by_error[j + 1] = v;
        }
        return indices_of_samples_sorted_by_error;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void insertionSortWithComparator(Integer[] a, Comparator c) {
        for (int i = 0; i < a.length - 1; i++) {
            Integer v = a[i];
            int j;
            for (j = i - 1; j >= 0; j--) {
                if (c.compare(a[j], v) <= 0) {
                    break;
                }
                a[j + 1] = a[j];
            }
            a[j + 1] = v;
        }
    }

//    public void solveAllPossibleMutualSystems(boolean sortWithInsertionsort) {
//
//        int whichParameter;
//
//        whichParameter = 1;
//        solveAllEquationsAndSetup(1, 2, whichParameter);
//
//    }
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

    public static List<float[]> establishTheSystemOfEquationsAndSolve(int experimentNumber, int numberOfUnknownParameters, int whichPar, int dimensionOfRightvestorInDataFile) {
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
                    && comb[pos] == numberOfSamples - numberOfUnknownParameters + pos) {

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

    static class ErrorSample {

        float error;

        int sampleIndex;

        ErrorSample(float error,
                int sampleIndex) {

            this.error = error;
            this.sampleIndex = sampleIndex;
        }
    }

}
