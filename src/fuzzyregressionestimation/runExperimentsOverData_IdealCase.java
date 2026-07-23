/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyregressionestimation;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.Float.NEGATIVE_INFINITY;
import static java.lang.Float.POSITIVE_INFINITY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;

public class runExperimentsOverData_IdealCase {

    private final int numberOfSamples;
    private static boolean NanDetected;
    private static int numberOfOutliers;
    private static int[] indices_of_samples_sorted_by_error;
    private static float coefficientOfSecondErrorTerm;
    private static float coefficientOfThirdErrorTerm;
    private static ArrayList<Float> alpha_0;
    private static ArrayList<Float> alpha_1;
    private static ArrayList<Float> beta_0;
    private static ArrayList<Float> beta_1;
    private static ArrayList<Float> gamma_0;
    private static ArrayList<Float> gamma_1;
    private static ArrayList<Float> alpha_0WithoutDuplicates;
    private static ArrayList<Float> alpha_1WithoutDuplicates;
    private static ArrayList<Float> beta_0WithoutDuplicates;
    private static ArrayList<Float> beta_1WithoutDuplicates;
    private static ArrayList<Float> gamma_0WithoutDuplicates;
    private static ArrayList<Float> gamma_1WithoutDuplicates;
    private static int sizeOfAlpha_0WithoutDuplicates;
    private static int sizeOfAlpha_1WithoutDuplicates;
    private static int sizeOfBeta_0WithoutDuplicates;
    private static int sizeOfBeta_1WithoutDuplicates;
    private static int sizeOfGamma_0WithoutDuplicates;
    private static int sizeOfGamma_1WithoutDuplicates;
    private static float bestAlph_0SoFar;
    private static float bestAlph_1SoFar;
    private static float bestBeta_0SoFar;
    private static float bestBeta_1SoFar;
    private static float bestGamma_0SoFar;
    private static float bestGamma_1SoFar;
    private static int sampleWithBesth_1SoFar;
    private static int sampleWithBesth_2SoFar;
    private static float bestValueOfObjectiveFoundSoFar;
//    private static float[] error;
    //   private static float[] error2;
    private static float[] errorTerm;
    boolean[] signOfThisCorrespondingErrorTermIsNegative;
    boolean calledOnceOverBeta;
    float[][] matrixOfData;
    boolean calledOnceOverGamma;
    float[][] tableOfAlpha_1sMultipliedInX_is;
    float[][] tableOfBeta_1sMultipliedInX_is;
    float[][] tableOfGamma_1sMultipliedInX_is;
    static float maintainedSumOfAllComputedErrorTerms;
    boolean solveOverLeftCenter;
    boolean solveOverCenter;
    boolean solveOverRightCenter;

    public runExperimentsOverData_IdealCase(int numberOfSamples, boolean solveOverLeftCenter, boolean solveOverCenter, boolean solveOverRightCenter) {
        this.numberOfSamples = numberOfSamples;
        this.solveOverLeftCenter = solveOverLeftCenter;
        this.solveOverCenter = solveOverCenter;
        this.solveOverRightCenter = solveOverRightCenter;
        matrixOfData = new float[numberOfSamples][5];
        NanDetected = false;
        numberOfOutliers = 0;
        coefficientOfSecondErrorTerm = 0.5f;
        coefficientOfThirdErrorTerm = 0.5f;
        alpha_0 = new ArrayList<>();
        alpha_1 = new ArrayList<>();
        beta_0 = new ArrayList<>();
        beta_1 = new ArrayList<>();
        gamma_0 = new ArrayList<>();
        gamma_1 = new ArrayList<>();
        alpha_0WithoutDuplicates = new ArrayList<>();
        alpha_1WithoutDuplicates = new ArrayList<>();
        beta_0WithoutDuplicates = new ArrayList<>();
        beta_1WithoutDuplicates = new ArrayList<>();
        gamma_0WithoutDuplicates = new ArrayList<>();
        gamma_1WithoutDuplicates = new ArrayList<>();
        sizeOfAlpha_0WithoutDuplicates = 0;
        sizeOfAlpha_1WithoutDuplicates = 0;
        sizeOfBeta_0WithoutDuplicates = 0;
        sizeOfBeta_1WithoutDuplicates = 0;
        sizeOfGamma_0WithoutDuplicates = 0;
        sizeOfGamma_1WithoutDuplicates = 0;
      //  maintainedSumOfAllComputedErrorTerms = 0;
        //   error = new float[numberOfSamples];
        //   error2 = new float[numberOfSamples];
        errorTerm = new float[numberOfSamples];
        //    calledOnce = false;
        signOfThisCorrespondingErrorTermIsNegative = new boolean[numberOfSamples];
        calledOnceOverBeta = false;
        calledOnceOverGamma = false;
        indices_of_samples_sorted_by_error = new int[numberOfSamples];
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public int choose2OverN() {
        int m = getNumberOfSamples();
        return m * (m - 1) / 2;
    }

    public void readDataFromExcel(String dataName) throws FileNotFoundException, IOException {
        int n = getNumberOfSamples();
        int i;
        for (i = 0; i < n; i++) {
            matrixOfData[i][0] = 1.0f;
        }
        i = 0;
        int j = 0;
        int k = 0;
        FileInputStream fis = new FileInputStream(new File("C:\\Users\\Win10\\Documents\\NetBeansProjects\\fuzzyregressionestimation\\" + dataName));
        HSSFWorkbook wb = new HSSFWorkbook(fis);
        HSSFSheet sheet = wb.getSheetAt(0);
        FormulaEvaluator formulaEvaluator = wb.getCreationHelper().createFormulaEvaluator();
        for (Row row : sheet) {
            k = 1;
            for (Cell cell : row) {
                switch (k) {
                    case 1:
                        matrixOfData[i][k] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 2:
                        matrixOfData[i][k] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 3:
                        matrixOfData[i][k] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    case 4:
                        matrixOfData[i][k] = truncate((float) cell.getNumericCellValue(), 2);
                        break;
                    default:
                        break;
                }
                k++;
            }
            i++;
        }
    }

    public static float[] solveSimultaneousEquations(float a, float b, float e, float c, float d, float f) throws ArithmeticException {
        float det = (a * d - b * c);
        if (det == 0.0 || ((d * e - b * f == 0.0 || a * f - c * e == 0.0) && det == 0.0)) {
            NanDetected = true;
        }
        float x = (d * e - b * f) / det;
        float y = (a * f - c * e) / det;
        float[] result = new float[2];
        result[0] = truncate(x, 2);
        result[1] = truncate(y, 2);
        return result;
    }

    public void solveAllEquationsAndSetup(int number_of_all_mutual_equations, ArrayList<Float> par1, ArrayList<Float> par2, int whichPar) {
        int n = getNumberOfSamples();
        int nMinusOne = n - 1;
        float[] result;
        int counter = 0;
        int i = 0;
        int j;
        float determinerOfRightComponentOfFuzzyNumber1 = 0.0f;
        float determinerOfRightComponentOfFuzzyNumber2 = 0.0f;
        do {
            while (i < nMinusOne) {
                j = i + 1;
                while (j < n) {
                    if (whichPar == 1) {
                        determinerOfRightComponentOfFuzzyNumber1 = matrixOfData[i][2];
                        determinerOfRightComponentOfFuzzyNumber2 = matrixOfData[j][2];
                    }
                    if (whichPar == 2) {
                        determinerOfRightComponentOfFuzzyNumber1 = matrixOfData[i][3];
                        determinerOfRightComponentOfFuzzyNumber2 = matrixOfData[j][3];
                    }
                    if (whichPar == 3) {
                        determinerOfRightComponentOfFuzzyNumber1 = matrixOfData[i][4];
                        determinerOfRightComponentOfFuzzyNumber2 = matrixOfData[j][4];
                    }
                    result = solveSimultaneousEquations(matrixOfData[i][0], matrixOfData[i][1], determinerOfRightComponentOfFuzzyNumber1, matrixOfData[j][0], matrixOfData[j][1], determinerOfRightComponentOfFuzzyNumber2);
                    if (!NanDetected
                            && result[0] != NEGATIVE_INFINITY && result[0] != POSITIVE_INFINITY
                            && result[1] != NEGATIVE_INFINITY && result[1] != POSITIVE_INFINITY) {
                        par1.add(result[0]);
                        par2.add(result[1]);
                        counter++;
                    }
                    if (NanDetected) {
                        NanDetected = false;
                    }
                    j++;
                }
                i++;
            }
        } while (i < nMinusOne && counter < number_of_all_mutual_equations);
        Collections.sort(par1);
        Collections.sort(par2);
        if (whichPar == 1) {
            LinkedHashSet<Float> hashSet0 = new LinkedHashSet<>(par1);
            alpha_0WithoutDuplicates = new ArrayList<>(hashSet0);
            LinkedHashSet<Float> hashSet1 = new LinkedHashSet<>(par2);
            alpha_1WithoutDuplicates = new ArrayList<>(hashSet1);
            sizeOfAlpha_0WithoutDuplicates = alpha_0WithoutDuplicates.size();
            sizeOfAlpha_1WithoutDuplicates = alpha_1WithoutDuplicates.size();
            System.out.println("Alpha_0 in [" + alpha_0WithoutDuplicates.get(0) + ", " + alpha_0WithoutDuplicates.get(sizeOfAlpha_0WithoutDuplicates - 1) + "]");
            System.out.println("Alpha_1 in [" + alpha_1WithoutDuplicates.get(0) + ", " + alpha_1WithoutDuplicates.get(sizeOfAlpha_1WithoutDuplicates - 1) + "]");
        }
        if (whichPar == 2) {
            LinkedHashSet<Float> hashSet0 = new LinkedHashSet<>(par1);
            beta_0WithoutDuplicates = new ArrayList<>(hashSet0);
            LinkedHashSet<Float> hashSet1 = new LinkedHashSet<>(par2);
            beta_1WithoutDuplicates = new ArrayList<>(hashSet1);
            sizeOfBeta_0WithoutDuplicates = beta_0WithoutDuplicates.size();
            sizeOfBeta_1WithoutDuplicates = beta_1WithoutDuplicates.size();
            System.out.println("Beta_0 in [" + beta_0WithoutDuplicates.get(0) + ", " + beta_0WithoutDuplicates.get(sizeOfBeta_0WithoutDuplicates - 1) + "]");
            System.out.println("Beta_1 in [" + beta_1WithoutDuplicates.get(0) + ", " + beta_1WithoutDuplicates.get(sizeOfBeta_1WithoutDuplicates - 1) + "]");
        }
        if (whichPar == 3) {
            LinkedHashSet<Float> hashSet0 = new LinkedHashSet<>(par1);
            gamma_0WithoutDuplicates = new ArrayList<>(hashSet0);
            LinkedHashSet<Float> hashSet1 = new LinkedHashSet<>(par2);
            gamma_1WithoutDuplicates = new ArrayList<>(hashSet1);
            sizeOfGamma_0WithoutDuplicates = gamma_0WithoutDuplicates.size();
            sizeOfGamma_1WithoutDuplicates = gamma_1WithoutDuplicates.size();
            System.out.println("Gamma_0 in [" + gamma_0WithoutDuplicates.get(0) + ", " + gamma_0WithoutDuplicates.get(sizeOfGamma_0WithoutDuplicates - 1) + "]");
            System.out.println("Gamma_1 in [" + gamma_1WithoutDuplicates.get(0) + ", " + gamma_1WithoutDuplicates.get(sizeOfGamma_1WithoutDuplicates - 1) + "]");
        }
    }

    public void solveAllPossibleMutualSystems(int number_of_all_mutual_equations, boolean sortWithInsertionsort) {

        int whichParameter;
        if (!solveOverLeftCenter && solveOverCenter && !solveOverRightCenter) {
            whichParameter = 1;
            solveAllEquationsAndSetup(number_of_all_mutual_equations, alpha_0, alpha_1, whichParameter);
        } else if (solveOverLeftCenter && !solveOverCenter && !solveOverRightCenter) {
            whichParameter = 2;
            solveAllEquationsAndSetup(number_of_all_mutual_equations, beta_0, beta_1, whichParameter);
        } else if (!solveOverLeftCenter && !solveOverCenter && solveOverRightCenter) {
            whichParameter = 3;
            solveAllEquationsAndSetup(number_of_all_mutual_equations, gamma_0, gamma_1, whichParameter);
        } else if (solveOverLeftCenter && solveOverCenter && solveOverRightCenter) {
            solveAllEquationsAndSetup(number_of_all_mutual_equations, alpha_0, alpha_1, 1);
            solveAllEquationsAndSetup(number_of_all_mutual_equations, beta_0, beta_1, 2);
            solveAllEquationsAndSetup(number_of_all_mutual_equations, gamma_0, gamma_1, 3);
        }
    }

    public static int numberOfOutliers() {
        return numberOfOutliers;
    }

    /*
    public static float sumOfAllArrayElements(float arr[]) {
        float total = 0.0f;
        for (int i = 0; i < arr.length; i++) {
            total += arr[i];
        }
        return total;
    }
*/
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

    static float truncate(float number, int precision) {

        double prec = Math.pow(10, precision);
        int integerPart = (int) number;
        float fractionalPart = number - integerPart;
        fractionalPart *= prec;
        int fractPart = (int) fractionalPart;
        fractionalPart = (float) (integerPart) + (fractPart) / (float) prec;
        return (float) fractionalPart;
    }

    static void printArray(int arr[]) {
        int n = arr.length;
        for (int i = 0; i < n; ++i) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }

    static void printArray(float arr[]) {
        int n = arr.length;
        for (int i = 0; i < n; ++i) {
            System.out.print("\ni: " + i + "->" + arr[i] + " ");
        }
        System.out.println();
    }

    public int[] setUpTheObjectiveComputation(boolean sortWithInsertionsort, float[] error, float fixed_alpha_0, float fixed_alpha_1, float fixed_beta_0, float fixed_beta_1, float fixed_gamma_0, float fixed_gamma_1) {

        int n = getNumberOfSamples();
        int[] indices_of_samples_sorted_by_error1 = new int[n];
        float rationalValue;
        if (sortWithInsertionsort) {
            indices_of_samples_sorted_by_error = insertionSort_indices(error);
        } else {
//indices_of_samples_sorted_by_error1 = fillIdenticalArray(indices_of_samples_sorted_by_error1);
            indices_of_samples_sorted_by_error1 = indexSort(error, true);
        }
        //float denominatorOfObj = truncate(sumOfAllArrayElements(error), 2);
      //  float denominatorOfObj = sumOfAllArrayElements(error);
//denominatorOfObj = maintainedSumOfAllComputedErrorTerms;
        // System.out.println("\ndenominatorOfObj = " + denominatorOfObj);
        //  System.out.println();
        float numeratorOfObj;
        int h_1, h_2;
        for (int lb = 0; lb < 1; lb++) {
            if (sortWithInsertionsort) {
                h_1 = indices_of_samples_sorted_by_error[lb];
            } else {
                h_1 = indices_of_samples_sorted_by_error1[lb];
            }
            int ub = n - 1;
            numeratorOfObj = maintainedSumOfAllComputedErrorTerms;
            do {
                if (sortWithInsertionsort) {
                    h_2 = indices_of_samples_sorted_by_error[ub];
                } else {
                    h_2 = indices_of_samples_sorted_by_error1[ub];
                }
                //     rationalValue = truncate(numeratorOfObj / denominatorOfObj, 6);
                rationalValue = numeratorOfObj / maintainedSumOfAllComputedErrorTerms;

                if (rationalValue < bestValueOfObjectiveFoundSoFar) {
                    bestValueOfObjectiveFoundSoFar = rationalValue;

                    //       System.out.println("bestValueOfObjectiveFoundSoFar = " + bestValueOfObjectiveFoundSoFar);
                    //     System.out.println("fixed_alpha_1 = " + fixed_alpha_1);
                    //        System.out.println();
                    if (fixed_alpha_0 != bestAlph_0SoFar) {
                        bestAlph_0SoFar = fixed_alpha_0;
                    }
                    if (fixed_alpha_1 != bestAlph_1SoFar) {
                        bestAlph_1SoFar = fixed_alpha_1;
                    }
                    if (fixed_beta_0 != bestBeta_0SoFar) {
                        bestBeta_0SoFar = fixed_beta_0;
                    }
                    if (fixed_beta_1 != bestBeta_1SoFar) {
                        bestBeta_1SoFar = fixed_beta_1;
                    }
                    if (fixed_gamma_0 != bestGamma_0SoFar) {
                        bestGamma_0SoFar = fixed_gamma_0;
                    }
                    if (fixed_gamma_1 != bestGamma_1SoFar) {
                        bestGamma_1SoFar = fixed_gamma_1;
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
            } while (ub > 3 * n / 4//       && (ub - lb + 1) >= n
                    );
        }
        if (sortWithInsertionsort) {
            return indices_of_samples_sorted_by_error;
        } else {
            return indices_of_samples_sorted_by_error1;
        }
    }

    public void findBestParameters(int number_of_all_mutual_equations, boolean sortWithInsertionsort) {
        int n = getNumberOfSamples();
        int iterator_over_beta_0;
        float fixed_beta_0;
        int iterator_over_beta_1;
        float fixed_beta_1;
        int iterator_over_gamma_0 = -1;
        float fixed_gamma_0;
        int iterator_over_gamma_1;
        float fixed_gamma_1;
        iterator_over_beta_0 = -1;
        bestValueOfObjectiveFoundSoFar = 1.0f;
        sampleWithBesth_1SoFar = 0;
        sampleWithBesth_2SoFar = n - 1;
        int[] indices_of_samples_sorted_by_error1 = new int[n];
        if (sortWithInsertionsort) {
            fillIdenticalArray(indices_of_samples_sorted_by_error);
        }
        float[] tableOf_column_rMinusGamma_0s;
        float[] tableOf_column_lMinusBetta_0s;
        int iterator_over_alpha_0 = -1;
        float fixed_alpha_0;
        int iterator_over_alpha_1;
        float fixed_alpha_1;
        tableOfAlpha_1sMultipliedInX_is = new float[alpha_1WithoutDuplicates.size()][n];
        for (int i = 0; i < alpha_1WithoutDuplicates.size(); i++) {
            for (int j = 0; j < n; j++) {
                //   tableOfAlpha_1sMultipliedInX_is[i][j] = truncate(alpha_1WithoutDuplicates.get(i) * matrixOfData[j][1], 2);
                tableOfAlpha_1sMultipliedInX_is[i][j] = alpha_1WithoutDuplicates.get(i) * matrixOfData[j][1];
            }
        }
        if (solveOverLeftCenter) {
            tableOfBeta_1sMultipliedInX_is = new float[beta_1WithoutDuplicates.size()][n];
            for (int i = 0; i < beta_1WithoutDuplicates.size(); i++) {
                for (int j = 0; j < n; j++) {
                    //  tableOfBeta_1sMultipliedInX_is[i][j] = truncate(beta_1WithoutDuplicates.get(i) * matrixOfData[j][1], 2);
                    tableOfBeta_1sMultipliedInX_is[i][j] = beta_1WithoutDuplicates.get(i) * matrixOfData[j][1];
                }
            }
        }
        if (solveOverRightCenter) {
            tableOfGamma_1sMultipliedInX_is = new float[gamma_1WithoutDuplicates.size()][n];
            for (int i = 0; i < gamma_1WithoutDuplicates.size(); i++) {
                for (int j = 0; j < n; j++) {
                    //  tableOfGamma_1sMultipliedInX_is[i][j] = truncate(gamma_1WithoutDuplicates.get(i) * matrixOfData[j][1], 2);
                    tableOfGamma_1sMultipliedInX_is[i][j] = gamma_1WithoutDuplicates.get(i) * matrixOfData[j][1];
                }
            }
        }
        bestAlph_0SoFar = alpha_0WithoutDuplicates.get(0);
        bestAlph_1SoFar = alpha_1WithoutDuplicates.get(0);
        if (solveOverLeftCenter) {
            bestBeta_0SoFar = beta_0WithoutDuplicates.get(0);
            bestBeta_1SoFar = beta_1WithoutDuplicates.get(0);
        }
        if (solveOverRightCenter) {
            bestGamma_0SoFar = gamma_0WithoutDuplicates.get(0);
            bestGamma_1SoFar = gamma_1WithoutDuplicates.get(0);
        }
        do {
//    System.out.println("iterator_over_alpha_0 = " + iterator_over_alpha_0);
            for (int w = 0; w < n; w++) {
                errorTerm[w] = matrixOfData[w][2];
            }
                    maintainedSumOfAllComputedErrorTerms = 0;

            iterator_over_alpha_0 += 1;
            fixed_alpha_0 = alpha_0WithoutDuplicates.get(iterator_over_alpha_0);
            System.out.println("fixed_alpha_0 = " + fixed_alpha_0);
            iterator_over_alpha_1 = -1;
            addAlpha_0ToTheErrorTerm(fixed_alpha_0);
            do {
                //    System.out.println("iterator_over_alpha_1 = " + iterator_over_alpha_1);
                iterator_over_alpha_1 += 1;
                fixed_alpha_1 = alpha_1WithoutDuplicates.get(iterator_over_alpha_1);
                //   System.out.print("\nfixed_alpha_1 " + fixed_alpha_1);
                //    System.out.print("\n"); 
                addAlpha_1TermToTheErrorTerm(iterator_over_alpha_1, fixed_alpha_1);
                if (!solveOverLeftCenter && solveOverCenter && !solveOverRightCenter) {
                    indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_alpha_0, fixed_alpha_1, 0, 0, 0, 0);
                }

            /*
              //  Beginning of what I mean by ideal case
                if (solveOverLeftCenter) {
                    iterator_over_beta_0 = -1;
                    do {
                        iterator_over_beta_0 += 1;
                        fixed_beta_0 = beta_0WithoutDuplicates.get(iterator_over_beta_0);
                        iterator_over_beta_1 = -1;
                        tableOf_column_lMinusBetta_0s = fillInAnArrayWithCoefficient(3, fixed_beta_0);
                        calledOnceOverBeta = false;
                        do {
                            //     System.out.println("iterator_over_beta_1 = " + iterator_over_beta_1);
                            iterator_over_beta_1 += 1;
                            fixed_beta_1 = beta_1WithoutDuplicates.get(iterator_over_beta_1);
                            addBeta_1TermToTheErrorTerm(iterator_over_beta_1, tableOf_column_lMinusBetta_0s, fixed_beta_1);
                            if (solveOverLeftCenter && solveOverCenter && !solveOverRightCenter) {
                                indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_alpha_0, fixed_alpha_1, fixed_beta_0, fixed_beta_1, 0, 0);
                            }
                            if (solveOverRightCenter) {
                                iterator_over_gamma_0 = -1;
                                do {
                                    //   System.out.println("iterator_over_gamma_0 = " + iterator_over_gamma_0);
                                    iterator_over_gamma_0 += 1;
                                    fixed_gamma_0 = gamma_0WithoutDuplicates.get(iterator_over_gamma_0);
                                    tableOf_column_rMinusGamma_0s = fillInAnArrayWithCoefficient(4, fixed_gamma_0);
                                    calledOnceOverGamma = false;
                                    iterator_over_gamma_1 = -1;
                                    do {
                                        //  System.out.println("iterator_over_gamma_1 = " + iterator_over_gamma_1);
                                        iterator_over_gamma_1 += 1;
                                        fixed_gamma_1 = gamma_1WithoutDuplicates.get(iterator_over_gamma_1);
                                        addGamma_1TermToTheErrorTerm(iterator_over_gamma_1, tableOf_column_rMinusGamma_0s, fixed_gamma_1);
                                        if (solveOverLeftCenter && solveOverCenter && solveOverRightCenter) {
                                            indices_of_samples_sorted_by_error1 = setUpTheObjectiveComputation(sortWithInsertionsort, errorTerm, fixed_alpha_0, fixed_alpha_1, fixed_beta_0, fixed_beta_1, fixed_gamma_0, fixed_gamma_1);
                                        }
                                        substractGamma_1TermFromTheErrorTerm(iterator_over_gamma_1, tableOf_column_rMinusGamma_0s, fixed_gamma_1);
                                        calledOnceOverGamma = true;
                                    } while (iterator_over_gamma_1 < sizeOfGamma_1WithoutDuplicates - 1);
                                    unfillInArrayWithCoefficientGamma(tableOf_column_rMinusGamma_0s);
                                } while (iterator_over_gamma_0 < sizeOfGamma_0WithoutDuplicates - 1);
                            }
                            substractBeta_1TermFromTheErrorTerm(iterator_over_beta_1, tableOf_column_lMinusBetta_0s, fixed_beta_1);
                            calledOnceOverBeta = true;
                        } while (iterator_over_beta_1 < sizeOfBeta_1WithoutDuplicates - 1);
                        unfillInArrayWithCoefficientBeta(tableOf_column_lMinusBetta_0s);
                    } while (iterator_over_beta_0 < sizeOfBeta_0WithoutDuplicates - 1);
                }
                              //  End of what I mean by ideal case               
                */
                substractAlpha_1TermFromTheErrorTerm(iterator_over_alpha_1, fixed_alpha_1);
            } while (iterator_over_alpha_1 < sizeOfAlpha_1WithoutDuplicates - 1);
            //     substractAlpha_0FromTheErrorTerm(2, fixed_alpha_0);
        } while (iterator_over_alpha_0 < sizeOfAlpha_0WithoutDuplicates - 1);
        int[] arrayOfOutliers;
        if (sortWithInsertionsort) {
            arrayOfOutliers = arrayOfOutlierSamples(indices_of_samples_sorted_by_error, sortWithInsertionsort);
        } else {
            arrayOfOutliers = arrayOfOutlierSamples(indices_of_samples_sorted_by_error1, sortWithInsertionsort);
        }
        System.out.println("bestValueOfObjectiveFoundSoFar: " + bestValueOfObjectiveFoundSoFar);
        System.out.println("sampleWithBesth_1SoFar: " + sampleWithBesth_1SoFar);
        System.out.println("sampleWithBesth_2SoFar: " + sampleWithBesth_2SoFar);
        System.out.println("bestAlph_0: " + bestAlph_0SoFar);
        System.out.println("bestAlph_1: " + bestAlph_1SoFar);
        System.out.println("bestBeta_0: " + bestBeta_0SoFar);
        System.out.println("bestBeta_1: " + bestBeta_1SoFar);
        System.out.println("bestGamma_0: " + bestGamma_0SoFar);
        System.out.println("bestGamma_1: " + bestGamma_1SoFar);
        System.out.println("number of outliers: " + arrayOfOutliers.length);
        System.out.println("outliers are:");
        Arrays.sort(arrayOfOutliers);
        printArray(arrayOfOutliers);
    }

    public int[] arrayOfOutlierSamples(int[] indices_of_samples_sorted_by_error1, boolean sortWithInsertionsort
    ) {
        int n = getNumberOfSamples();

        float[] tableOf_column_yMinusAlpha_0s;
        tableOf_column_yMinusAlpha_0s = new float[n];
        float[] tableOf_column_lMinusBetta_0s;
        tableOf_column_lMinusBetta_0s = new float[n];
        float[] tableOf_column_rMinusGamma_0s;
        tableOf_column_rMinusGamma_0s = new float[n];
        float[] thisError = new float[n];
        for (int o = 0; o < n; o++) {
            tableOf_column_yMinusAlpha_0s[o] = matrixOfData[o][2] - bestAlph_0SoFar;
            if (solveOverLeftCenter) {
                tableOf_column_lMinusBetta_0s[o] = matrixOfData[o][3] - bestBeta_0SoFar;
            }
            if (solveOverRightCenter) {
                tableOf_column_rMinusGamma_0s[o] = matrixOfData[o][4] - bestGamma_0SoFar;
            }
        }
        for (int o = 0; o < n; o++) {
            if (!solveOverLeftCenter && solveOverCenter && !solveOverRightCenter) {
                thisError[o] = Math.abs(tableOf_column_yMinusAlpha_0s[o] - bestAlph_1SoFar * matrixOfData[o][1]);
            } else {
                thisError[o] = Math.abs(tableOf_column_yMinusAlpha_0s[o] - bestAlph_1SoFar * matrixOfData[o][1]) + coefficientOfSecondErrorTerm * Math.abs(tableOf_column_lMinusBetta_0s[o] - bestBeta_1SoFar * matrixOfData[o][1]) + coefficientOfThirdErrorTerm * Math.abs(tableOf_column_rMinusGamma_0s[o] - bestGamma_1SoFar * matrixOfData[o][1]);
            }
        }
        //   System.out.print("oo");
        int tempBound = 0;
        fillIdenticalArray(indices_of_samples_sorted_by_error);
        insertionSort_indices(thisError);
        for (int o = 0; o < n; o++) {
            if (indices_of_samples_sorted_by_error1[o] == sampleWithBesth_2SoFar) {
                tempBound = o;
                break;
            }
        }
        int[] arrayOfOutliers = new int[n - tempBound - 1];
        for (int y = 0; y < arrayOfOutliers.length; y++) {
            arrayOfOutliers[y] = indices_of_samples_sorted_by_error1[tempBound++];
        }
        return arrayOfOutliers;
    }

    public void addAlpha_0ToTheErrorTerm(float fixed_something_0) {
        int n = getNumberOfSamples();

        //     float[] tableOf_column_something_MinusAlpha_0s = new float[n];
        for (int w = 0; w < n; w++) {
            //   tableOf_column_something_MinusAlpha_0s[w] = matrixOfData[w][whichColumn] - fixed_something_0;
            //     error2[w] -= fixed_something_0;
            errorTerm[w] -= fixed_something_0;
            maintainedSumOfAllComputedErrorTerms += errorTerm[w];
            //   if (tableOf_column_something_MinusAlpha_0s[w] != error2[w]) {
            //    System.out.print("hj");
            // }
        }
    }

    public void substractAlpha_0FromTheErrorTerm(int whichColumn, float fixed_something_0) {
        int n = getNumberOfSamples();

        //     float[] tableOf_column_something_MinusAlpha_0s = new float[n];
        for (int w = 0; w < n; w++) {
            //   tableOf_column_something_MinusAlpha_0s[w] = matrixOfData[w][whichColumn] - fixed_something_0;
            //     error2[w] -= fixed_something_0;
            errorTerm[w] += fixed_something_0;
            //   if (tableOf_column_something_MinusAlpha_0s[w] != error2[w]) {
            //    System.out.print("hj");
            // }
        }
    }

    public float[] fillInAnArrayWithCoefficient(int whichColumn, float fixed_something_0) {
        int n = getNumberOfSamples();

        float[] tableOf_column_something_MinusAlpha_0s = new float[n];
        for (int w = 0; w < n; w++) {
            tableOf_column_something_MinusAlpha_0s[w] = matrixOfData[w][whichColumn] - fixed_something_0;
        }
        return tableOf_column_something_MinusAlpha_0s;
    }

    public void unfillInArrayWithCoefficientBeta(float[] tableOf_l_iMinusBeta_0s) {
        int n = getNumberOfSamples();

        for (int w = 0; w < n; w++) {
            errorTerm[w] -= coefficientOfSecondErrorTerm * tableOf_l_iMinusBeta_0s[w];
        }
    }

    public void unfillInArrayWithCoefficientGamma(float[] tableOf_column_rMinusGamma_0s) {
        int n = getNumberOfSamples();

        for (int w = 0; w < n; w++) {
            errorTerm[w] -= coefficientOfThirdErrorTerm * tableOf_column_rMinusGamma_0s[w];
        }
    }

//    public void developErrorArray1(int whichColumn, float fixed_something_0) {
//        int n = getNumberOfSamples();
//        for (int w = 0; w < n; w++) {
//            error[w] = matrixOfData[w][whichColumn] - fixed_something_0;
//        }
//    }
//    public void developErrorArray2(float fixed_something_0) {
//        int n = getNumberOfSamples();
//        for (int i = 0; i < n; i++) {
//            error[i] = Math.abs(error[i] - fixed_something_0 * matrixOfData[i][1]);
//        }
//    }
    public void addAlpha_1TermToTheErrorTerm(int iterator_over_alpha_1, float fixed_something_1) {
        int n = getNumberOfSamples();
        //     float temp;
        //    float temp2;

        //   if (!calledOnce) {
        for (int i = 0; i < n; i++) {
            //    temp = truncate(fixed_something_1 * matrixOfData[i][1], 2);
            //  temp = fixed_something_1 * matrixOfData[i][1];
//temp = tableOfAlpha_1sMultipliedInX_is[iterator_over_alpha_1][i];
//if (temp != temp2)
            // System.out.print("ghj");
            if (errorTerm[i] >= tableOfAlpha_1sMultipliedInX_is[iterator_over_alpha_1][i]) {
                errorTerm[i] += (-tableOfAlpha_1sMultipliedInX_is[iterator_over_alpha_1][i]);
                maintainedSumOfAllComputedErrorTerms += (-tableOfAlpha_1sMultipliedInX_is[iterator_over_alpha_1][i]);
            } else {
                float prevValue = errorTerm[i];
                errorTerm[i] = -errorTerm[i] + tableOfAlpha_1sMultipliedInX_is[iterator_over_alpha_1][i];
                maintainedSumOfAllComputedErrorTerms += (errorTerm[i] - prevValue);
                signOfThisCorrespondingErrorTermIsNegative[i] = true;
            }
        }
        //   }
        /*   else {
            for (int i = 0; i < n; i++) {
                temp = fixed_something_1 * matrixOfData[i][1];
                if (errorTerm[i] >= temp) {
                    errorTerm[i] += truncate(-fixed_something_1 * matrixOfData[i][1], 2);
                } else {
                    errorTerm[i] = truncate(fixed_something_1 * matrixOfData[i][1], 2) - errorTerm[i];
                    signOfThisCorrespondingErrorTermIsNegative[i] = true;
                }
            }
        }
         */
        //   calledOnce = true;
    }

    public void addBeta_1TermToTheErrorTerm(int iterator_over_beta_1, float[] tableOf_l_iMinusBeta_0s, float fixed_something_1) {
        int n = getNumberOfSamples();
        float temp;
        float temp2;
        float temp3;

        // if (!calledOnce) {
        for (int i = 0; i < n; i++) {
            //    temp = truncate(fixed_something_1 * matrixOfData[i][1], 2);
            //temp = fixed_something_1 * matrixOfData[i][1];
            temp = tableOfBeta_1sMultipliedInX_is[iterator_over_beta_1][i];

            if (tableOf_l_iMinusBeta_0s[i] >= temp) {
                if (!calledOnceOverBeta) {
                    temp2 = coefficientOfSecondErrorTerm * (tableOf_l_iMinusBeta_0s[i] - temp);
                } else {
                    temp2 = coefficientOfSecondErrorTerm * (-temp);
                }
                errorTerm[i] += temp2;
            } else {
                if (!calledOnceOverBeta) {
                    temp2 = coefficientOfSecondErrorTerm * (-tableOf_l_iMinusBeta_0s[i] + temp);
                } else {
                    temp2 = coefficientOfSecondErrorTerm * (temp);
                }
                errorTerm[i] += temp2;
                //   signOfThisCorrespondingErrorTermIsNegative[i] = true;
            }
        }
    }

    public void addGamma_1TermToTheErrorTerm(int iterator_over_gamma_1, float[] tableOf_column_rMinusGamma_0s, float fixed_something_1) {
        int n = getNumberOfSamples();
        float temp;
        float temp2;

        // if (!calledOnce) {
        for (int i = 0; i < n; i++) {
            //    temp = truncate(fixed_something_1 * matrixOfData[i][1], 2);
            //   temp = fixed_something_1 * matrixOfData[i][1];
            temp = tableOfGamma_1sMultipliedInX_is[iterator_over_gamma_1][i];

            if (tableOf_column_rMinusGamma_0s[i] >= temp) {
                if (!calledOnceOverGamma) {
                    temp2 = coefficientOfThirdErrorTerm * (tableOf_column_rMinusGamma_0s[i] - temp);
                } else {
                    temp2 = coefficientOfThirdErrorTerm * (-temp);
                }
                errorTerm[i] += temp2;
            } else {
                if (!calledOnceOverGamma) {
                    temp2 = coefficientOfThirdErrorTerm * (-tableOf_column_rMinusGamma_0s[i] + temp);
                } else {
                    temp2 = coefficientOfThirdErrorTerm * (temp);
                }
                errorTerm[i] += temp2;
                //   signOfThisCorrespondingErrorTermIsNegative[i] = true;
            }
            maintainedSumOfAllComputedErrorTerms += errorTerm[i];
        }
    }

    public void substractBeta_1TermFromTheErrorTerm(int iterator_over_beta_1, float[] tableOf_l_iMinusBeta_0s, float fixed_something_1) {
        int n = getNumberOfSamples();
        float temp;
        //   if (!calledOnce) {
        for (int i = 0; i < n; i++) {
            //    temp = truncate(fixed_something_1 * matrixOfData[i][1], 2);
            // temp = fixed_something_1 * matrixOfData[i][1];
            temp = tableOfBeta_1sMultipliedInX_is[iterator_over_beta_1][i];
            if (tableOf_l_iMinusBeta_0s[i] >= temp) {
                errorTerm[i] -= coefficientOfSecondErrorTerm * (-temp);
            } else {
                errorTerm[i] -= coefficientOfSecondErrorTerm * (temp);
                //   signOfThisCorrespondingErrorTermIsNegative[i] = true;
            }
        }
    }

    public void substractGamma_1TermFromTheErrorTerm(int iterator_over_gamma_1, float[] tableOf_column_rMinusGamma_0s, float fixed_something_1) {
        int n = getNumberOfSamples();
        float temp;
        //   if (!calledOnce) {
        for (int i = 0; i < n; i++) {
            //    temp = truncate(fixed_something_1 * matrixOfData[i][1], 2);
            //   temp = fixed_something_1 * matrixOfData[i][1];
            temp = tableOfGamma_1sMultipliedInX_is[iterator_over_gamma_1][i];
            if (tableOf_column_rMinusGamma_0s[i] >= temp) {
                errorTerm[i] -= coefficientOfThirdErrorTerm * (-temp);
            } else {
                errorTerm[i] -= coefficientOfThirdErrorTerm * (temp);
                //   signOfThisCorrespondingErrorTermIsNegative[i] = true;
            }
            maintainedSumOfAllComputedErrorTerms -= errorTerm[i];

        }
    }

    public void substractAlpha_1TermFromTheErrorTerm(int iterator_over_alpha_1, float fixed_something_1) {
        int n = getNumberOfSamples();
        float temp;

        // if (calledOnce) {
        /*
        for (int i = 0; i < n; i++) {
            temp = truncate(fixed_something_1 * matrixOfData[i][1], 2);
            //   if (tableOf_column_something_MinusAlpha_0s[i] != errorTerm[i])
            //     System.out.println("dhfhu");

         //   if (signOfThisCorrespondingErrorTermIsNegative[i])
             //   System.out.println("hhuh");
           if (tableOf_column_something_MinusAlpha_0s[i] >= temp) {
              //  error[i] -= truncate(-fixed_something_1 * matrixOfData[i][1], 2);
                errorTerm[i] += temp;
            } else {
            //    error[i] -= truncate(fixed_something_1 * matrixOfData[i][1], 2);
                              
            
            errorTerm[i] = -errorTerm[i];
            errorTerm[i] +=   temp;

            }
         */
        for (int i = 0; i < n; i++) {
            //  temp = truncate(fixed_something_1 * matrixOfData[i][1], 2);
            // temp = fixed_something_1 * matrixOfData[i][1];
            temp = tableOfAlpha_1sMultipliedInX_is[iterator_over_alpha_1][i];
            if (!signOfThisCorrespondingErrorTermIsNegative[i]) {
                //  error[i] -= truncate(-fixed_something_1 * matrixOfData[i][1], 2);
                errorTerm[i] += temp;
                maintainedSumOfAllComputedErrorTerms += temp;
            } else {
                //    error[i] -= truncate(fixed_something_1 * matrixOfData[i][1], 2);
          float prevVal = errorTerm[i];
                errorTerm[i] = -errorTerm[i] + temp;
                //  errorTerm[i] += temp;
              maintainedSumOfAllComputedErrorTerms += (errorTerm[i] - prevVal);
                signOfThisCorrespondingErrorTermIsNegative[i] = false;
            }

//            if (Math.abs(error[i] - errorTerm[i]) >= 1.0f) {
//                System.out.println("gg");
//            }
        }
//        } else {
//            for (int i = 0; i < n; i++) {
//                error[i] = 0;
//            }
//        }
        //   return khar;
    }

    public int[] fillIdenticalArray(int[] a) {
        int n = getNumberOfSamples();

        for (int r = 0; r < n; r++) {
            a[r] = r;
        }
        return a;
    }

    public int[] insertionSort_indices(float[] error) {
        int n = getNumberOfSamples();

        for (int i = 1; i < n; i++) {
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
}
