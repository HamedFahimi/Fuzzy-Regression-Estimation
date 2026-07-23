package fuzzyregressionestimation;

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

public class OptimizationAndOutlierPipeline {

    // Global data matrix: Rows = Samples/Tasks, Columns = Features/Variables
    private float[][] matrixOfData;
    private int numberOfSamples;

    public OptimizationAndOutlierPipeline(float[][] matrixOfData) {
        this.matrixOfData = matrixOfData;
        this.numberOfSamples = matrixOfData.length;
    }

    // =========================================================================
    // SECTION 1: SYSTEM SOLVER & COMBINATION GENERATOR
    // =========================================================================
    /**
     * Solves combinations of equations to extract parameter candidates.
     * Integrates:
     * - Incremental row updates (skips redundant writes)
     * - First-column static assignment (A[eq][0] = 1.0)
     * - Double precision + Apache QRDecomposition (stable for 12+ parameters)
     * - Vertical one-shot parameter deposition
     * - Native in-place sorting of final parameter tracks
     */
    public List<List<Float>> establishTheSystemOfEquationsAndSolve2(
            int experimentNumber, 
            int numberOfUnknownParameters, 
            int dimensionOfRightvestorInDataFile) {

        // 1. Vertical parameter containers
        List<List<Float>> sortedParameters = new ArrayList<>(numberOfUnknownParameters);
        for (int i = 0; i < numberOfUnknownParameters; i++) {
            sortedParameters.add(new ArrayList<>());
        }

        // 2. Combination tracker & caching array
        int[] comb = new int[numberOfUnknownParameters];
        int[] lastUsedRowIndex = new int[numberOfUnknownParameters];

        for (int i = 0; i < numberOfUnknownParameters; i++) {
            comb[i] = i;
            lastUsedRowIndex[i] = -1; // Force initial reload
        }

        // 3. System matrices (double precision)
        double[][] A = new double[numberOfUnknownParameters][numberOfUnknownParameters];
        double[] B = new double[numberOfUnknownParameters];

        // Set first column entries to 1.0 EXACTLY ONCE
        for (int eq = 0; eq < numberOfUnknownParameters; eq++) {
            A[eq][0] = 1.0;
        }

        while (true) {
            // Incremental Matrix Construction
            for (int eq = 0; eq < numberOfUnknownParameters; eq++) {
                int rowIndex = comb[eq];

                // Skip row build if unchanged from previous combination
                if (rowIndex == lastUsedRowIndex[eq]) {
                    continue;
                }

                float[] sourceRow = matrixOfData[rowIndex];

                // Skip column 0 (starts at var = 1)
                for (int var = 1; var < numberOfUnknownParameters; var++) {
                    A[eq][var] = (double) sourceRow[var];
                }

                // Populate Right-Hand Side vector
                B[eq] = (double) sourceRow[dimensionOfRightvestorInDataFile];

                lastUsedRowIndex[eq] = rowIndex;
            }

            // Solve using QR Decomposition
            try {
                RealMatrix matrixA = new Array2DRowRealMatrix(A, false); // false = no internal cloning
                QRDecomposition qr = new QRDecomposition(matrixA);
                DecompositionSolver solver = qr.getSolver();

                if (solver.isNonSingular()) {
                    RealVector vectorB = new ArrayRealVector(B, false);
                    RealVector solution = solver.solve(vectorB);

                    // Vertical Deposition directly into parameter tracks
                    for (int p = 0; p < numberOfUnknownParameters; p++) {
                        sortedParameters.get(p).add((float) solution.getEntry(p));
                    }
                }
            } catch (Exception e) {
                // Ignore singular matrices that bypass checks
            }

            // Lexicographical Combination Stepper
            int pos = numberOfUnknownParameters - 1;
            while (pos >= 0 && comb[pos] == numberOfSamples - numberOfUnknownParameters + pos) {
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

        // Native In-Place Sort of all parameter tracks
        for (int p = 0; p < numberOfUnknownParameters; p++) {
            Collections.sort(sortedParameters.get(p));
        }

        return sortedParameters;
    }

    // =========================================================================
    // SECTION 2: ERROR COMPUTATION & ORDER STATISTICS
    // =========================================================================
    /**
     * Calculates absolute errors for all data rows under a single candidate 
     * solution vector, returning a 2D array [ErrorValue, OriginalTaskID].
     */
    public double[][] computeErrorVector(float[] candidateSolution, int targetCol) {
        int N = matrixOfData.length;
        double[][] E = new double[N][2]; // [0] = Error, [1] = Original Row ID

        for (int i = 0; i < N; i++) {
            double predicted = candidateSolution[0]; // Intercept (Col 0 = 1.0)
            for (int j = 1; j < candidateSolution.length; j++) {
                predicted += candidateSolution[j] * matrixOfData[i][j];
            }
            double actual = matrixOfData[i][targetCol];
            
            E[i][0] = Math.abs(actual - predicted); // Absolute Error E_i
            E[i][1] = i;                            // Original Task Index
        }

        return E;
    }

    // =========================================================================
    // SECTION 3: WINDOW SCANNING & OUTLIER EXTRACTION
    // =========================================================================
    /**
     * Processes the raw error vector:
     * 1. Sorts errors smallest-to-largest (Order Statistics E_(k))
     * 2. Builds prefix sums for O(1) interval sum calculations
     * 3. Finds optimal h1, h2 window minimizing numerator/denominator ratio
     * 4. Extracts excluded indices as detected outliers
     */
    public OptimizationResult processSortedErrorsAndDetectOutliers(double[][] rawErrors) {
        int N = rawErrors.length;

        // 1. SORT ERRORS SMALLEST TO LARGEST
        Arrays.sort(rawErrors, Comparator.comparingDouble(a -> a[0]));

        // 2. BUILD PREFIX SUM ARRAY
        double[] prefixSum = new double[N + 1];
        for (int i = 0; i < N; i++) {
            prefixSum[i + 1] = prefixSum[i] + rawErrors[i][0];
        }

        // 3. OPTIMAL WINDOW SCANNING (h1 to h2)
        int bestH1 = 0;
        int bestH2 = N - 1;
        double minObjective = Double.MAX_VALUE;
        double totalSum = prefixSum[N];

        for (int h1 = 0; h1 < N; h1++) {
            for (int h2 = h1; h2 < N; h2++) {
                // Sum from sorted indices h1 to h2 via prefix sum
                double windowSum = prefixSum[h2 + 1] - prefixSum[h1];
                
                // Objective Function O = sum(E_h1..h2) / sum(E_1..N)
                double objective = (totalSum > 0) ? (windowSum / totalSum) : 0;

                if (objective < minObjective) {
                    minObjective = objective;
                    bestH1 = h1;
                    bestH2 = h2;
                }
            }
        }

        // 4. EXTRACT OUTLIERS
        List<Integer> outlierIndices = new ArrayList<>();
        for (int k = 0; k < N; k++) {
            if (k < bestH1 || k > bestH2) {
                int originalTaskID = (int) rawErrors[k][1];
                outlierIndices.add(originalTaskID);
            }
        }

        return new OptimizationResult(bestH1, bestH2, minObjective, outlierIndices, N);
    }

    // Container for optimization window metrics
    public static class OptimizationResult {
        public final int bestH1;
        public final int bestH2;
        public final double minObjective;
        public final List<Integer> outlierIndices;
        public final int totalTasks;
        public final int outlierCount;

        public OptimizationResult(int bestH1, int bestH2, double minObjective, List<Integer> outlierIndices, int totalTasks) {
            this.bestH1 = bestH1;
            this.bestH2 = bestH2;
            this.minObjective = minObjective;
            this.outlierIndices = outlierIndices;
            this.totalTasks = totalTasks;
            this.outlierCount = outlierIndices.size();
        }
    }

    // =========================================================================
    // SECTION 4: MAIN PIPELINE RUNNER
    // =========================================================================
    public void runFullAnalysisPipeline(int experimentNumber, int numberOfUnknownParameters, int targetCol) {
        System.out.println("--- Starting Pipeline Run for " + numberOfUnknownParameters + " Parameters ---");

        // Step 1: Solve combinations & obtain pre-sorted parameter arrays
        List<List<Float>> sortedParameters = establishTheSystemOfEquationsAndSolve2(
                experimentNumber, numberOfUnknownParameters, targetCol);

        System.out.println("Extracted " + sortedParameters.get(0).size() + " valid candidate solutions per parameter.");

        // Step 2: Pick a candidate solution vector (e.g., median parameters)
        float[] candidateSolution = new float[numberOfUnknownParameters];
        for (int p = 0; p < numberOfUnknownParameters; p++) {
            List<Float> track = sortedParameters.get(p);
            candidateSolution[p] = track.get(track.size() / 2); // Median candidate
        }

        // Step 3: Compute raw errors
        double[][] rawErrors = computeErrorVector(candidateSolution, targetCol);

        // Step 4: Perform sorted window search and outlier detection
        OptimizationResult result = processSortedErrorsAndDetectOutliers(rawErrors);

        // Step 5: Output Analysis Results
        System.out.println("\n--- Optimization & Outlier Results ---");
        System.out.println("Optimal Sorted Window Bounds: [" + result.bestH1 + " to " + result.bestH2 + "]");
        System.out.println("Clean Task Count: " + (result.bestH2 - result.bestH1 + 1));
        System.out.println("Outlier Count: " + result.outlierCount);
        System.out.println("Minimum Objective Score: " + result.minObjective);
        System.out.println("Outlier Task IDs: " + result.outlierIndices);
    }
}