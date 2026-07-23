package fuzzyregressionestimation;


public class MatrixUtils {

    // Tolerance for floating-point comparisons
    private static final double EPS = 1e-10;

    public static boolean isSingular(double[][] A) {
        int n = A.length;

        // Make a copy so original matrix is not modified
        double[][] M = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, M[i], 0, n);
        }

        for (int i = 0; i < n; i++) {

            // Find pivot
            int maxRow = i;
            for (int k = i + 1; k < n; k++) {
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
            for (int k = i + 1; k < n; k++) {
                double factor = M[k][i] / M[i][i];
                for (int j = i; j < n; j++) {
                    M[k][j] -= factor * M[i][j];
                }
            }
        }

        return false; // Not singular
    }
}
