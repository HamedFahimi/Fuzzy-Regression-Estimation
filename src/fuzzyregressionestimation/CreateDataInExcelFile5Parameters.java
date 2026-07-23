package fuzzyregressionestimation;

import static fuzzyregressionestimation.initializeRequiredVectors.truncate;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import jdistlib.NonCentralT;
import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;

public class CreateDataInExcelFile5Parameters {

    // private static float[] min_X_is;
    // private static float[] max_X_is;
    final static int numberOfKnownParameters = 5;
    final static int numberOfUnknownParameters = 6;
    private static float[] random_xs;
    private static float[] Alpha_is;
    private static float[] Beta_is;
    private static float[] Gamma_is;
//    private static float outlierFactor;
    private static float desiredStandardDeviation;
    private static float desiredMeanY;
    private static float desiredMeanL;
    private static float desiredMeanR;
    private static float df_y;
    //   private static float df_l;
    //   private static float df_r;
    private static float noc_y;
    //  private static float noc_l;
    //  private static float noc_r;
    private static NonCentralT nct_y;
//    private static NonCentralT nct_l;
//    private static NonCentralT nct_r;
    public static final String directoryAsAString = "C:\\Users\\Win10\\Documents\\NetBeansProjects\\fuzzyregressionestimation\\";

    public CreateDataInExcelFile5Parameters() {

        // min_X_is = new float[numberOfKnownParameters];
        // max_X_is = new float[numberOfKnownParameters];
        random_xs = new float[numberOfKnownParameters];
//        for (int r = 0; r < numberOfKnownParameters; r++) {
//            min_X_is[r] = 100;
//            max_X_is[r] = 200;
//        }

        Alpha_is = new float[numberOfUnknownParameters];
        Beta_is = new float[numberOfUnknownParameters];
        Gamma_is = new float[numberOfUnknownParameters];
        for (int r = 0; r < numberOfKnownParameters; r++) {
            Alpha_is[r] = 1f;
        }
        Beta_is[0] = 0.0f;
        Beta_is[1] = 0.01f;
        Beta_is[2] = 0.01f;
        Beta_is[3] = 0.01f;
        Beta_is[4] = 0.01f;
        Beta_is[5] = 1f;
//        for (int r = 0; r < numberOfKnownParameters; r++) {
//            Gamma_is[r] = Beta_is[r];
//        }
//        Alpha_is[0] = 10f;
//        Alpha_is[1] = 3f;
//        Beta_is[0] = -15f;
//        Beta_is[1] = 0.1f;
//        Gamma_is[0] = -40f;
//        Gamma_is[1] = 0.2f;
        //    outlierFactor = 0.9f;
        desiredStandardDeviation = 1.0f;
//        desiredMeanY = 0.0f;
//        desiredMeanL = 0.0f;
//        desiredMeanR = 0.0f;
        df_y = 10.0f;
        //    df_l = 10.0f;
        //    df_r = 10.0f;
        noc_y = 10.0f;
        //    noc_l = 10.0f;
        //    noc_r = 20.0f;
        nct_y = new NonCentralT(df_y, noc_y);
        //    nct_l = new NonCentralT(df_l, noc_l);
        //   nct_r = new NonCentralT(df_r, noc_r);
    }

    public static void main(String[] args) {
        int howManyInstanceToGenerate = 10;
        CreateDataInExcelFile5Parameters L = new CreateDataInExcelFile5Parameters();
        for (int q = 0; q < howManyInstanceToGenerate; q++) {
            //   L.generateData(q, 100, 90);
            L.generateDataOfExperiment2(q, 8, 7); //for debug purpose

        }
    }

    public String generateDataOfExperiment2(int dataFileNumber, int N, int N_1) {
        Random random = new Random(); // creating Random object
        String filenameDir = null;

        try {
            filenameDir = directoryAsAString + "generatedData" + dataFileNumber + ".xls";
            try (
                    HSSFWorkbook workbook = new HSSFWorkbook()) {
                HSSFSheet sheet = workbook.createSheet("Data");
                double random_y;
                double random_l;
                double random_r;
                for (int i = 0; i < N; i++) {
                    int columnCounter = 0;
                    //   random_x = min_X + random.nextFloat() * (max_X - min_X);//5 random x should be generated
                    //  for (int g = 0; g < numberOfKnownParameters; g++) {
                    //   random_xs[g] = min_X_is[g] + random.nextFloat() * (max_X_is[g] - min_X_is[g]);
                    //for must be gotten rif of
                    //random_x0 = 
                    //  random_xs[0] = min_X_is[0] + random.nextFloat() * (max_X_is[0] - min_X_is[0]);
                    //  random_xs[1] = min_X_is[1] + random.nextFloat() * (max_X_is[1] - min_X_is[1]);
                    //     random_xs[1] = Math.abs(100 + random.nextFloat() - 100 + random.nextFloat());
                    //    random_xs[2] = Math.abs(100 + random.nextFloat() - 100 + random.nextFloat());

                    // }
                    random_xs[0] = 10 + random.nextFloat() * (20 - 10);
                    float w1 = 100 + random.nextFloat() * (101 - 100);
                    float w2 = 100 + random.nextFloat() * (101 - 100);
                    random_xs[1] = Math.abs(w2 - w1);
                    random_xs[2] = 30 + 1 * (float) random.nextGaussian();
                    ChiSquaredDistribution chi = new ChiSquaredDistribution(20);
                    random_xs[3] = (float) chi.sample();
                    BetaDistribution dist = new BetaDistribution(3, 1);
                    random_xs[4] = (float) dist.sample();
                    HSSFRow row = sheet.createRow((short) i);
                    for (int g = 0; g < numberOfKnownParameters; g++) {
                        row.createCell(columnCounter++).setCellValue(truncate((float) random_xs[g], 2));
                    }
                    if (i < N_1) {
                        random_y = makeRandomComponentY(random, numberOfKnownParameters, Alpha_is, random_xs, desiredMeanY);//alpha_0 to 5
                        random_l = makeRandomComponentL(random, numberOfKnownParameters, Beta_is, random_xs, desiredMeanL);//..
                    } else {
                        //fix method name
                        //..
                        random_y = makeRandomComponentYOutlier(numberOfKnownParameters, Alpha_is, random_xs, df_y, noc_y
                        //add df = 10, noc = 50 and erase desiredMeanOutlierL
                        );
                        //df=10, noc=10
                        random_l = makeRandomComponentLOutlier(numberOfKnownParameters, Beta_is, random_xs);
                        //df=10, noc=20
                    }
                    random_r = random_l;
                    row.createCell(columnCounter++).setCellValue(truncate((float) random_y, 2));
                    row.createCell(columnCounter++).setCellValue(truncate((float) random_l, 2));
                    row.createCell(columnCounter++).setCellValue(truncate((float) random_r, 2));
                }
                //    System.out.println("Writing to: " + filenameDir);

                try (FileOutputStream fileOut = new FileOutputStream(filenameDir)) {
                    workbook.write(fileOut);
                }
            }
            //        System.out.println("Excel file has been generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();

        }
        String filename = "generatedData" + dataFileNumber + ".xls";
        return filename;
    }

    public static double makeRandomComponentY(Random random, int numberOfKnownParameters, float[] unknownParam, float[] knownParam, float desiredMeanOrOutlier) {
        float sum = 0;
        sum += unknownParam[0];
        for (int i = 1; i <= numberOfKnownParameters; i++) {
            sum += unknownParam[i] * knownParam[i - 1];
        }
        return sum + random.nextGaussian();
    }

    public static double makeRandomComponentL(Random random, int numberOfKnownParameters, float[] unknownParam, float[] knownParam, float desiredMeanOrOutlier) {
        float sum = 0;
        sum += unknownParam[0];
        for (int i = 1; i <= numberOfKnownParameters; i++) {
            sum += unknownParam[i] * knownParam[i - 1];
        }
        return sum + Math.abs(0.1 * (float) random.nextGaussian());
    }

    public static double makeRandomComponentYOutlier(int numberOfKnownParameters, float[] unknownParam, float[] knownParam, float df_y, float noc_y
    ) //6 parameters
    {
        float sum = 0;
        sum += unknownParam[0];
        for (int i = 1; i <= numberOfKnownParameters; i++) {
            sum += unknownParam[i] * knownParam[i - 1];
        }
        return sum + (nct_y.random());
        //        return param_0 + param_1 * random_x1 + param_2 * random_x2 +...(nct_y.random());

        //t.(df , noc)
    }

    public static double makeRandomComponentLOutlier(int numberOfKnownParameters, float[] unknownParam, float[] knownParam) {
        Random random = new Random();
        double sum = 0;
        sum += unknownParam[0];
        for (int i = 1; i <= numberOfKnownParameters; i++) {
            sum += unknownParam[i] * knownParam[i - 1];
        }
        return sum + Math.abs(0.1 * (float) random.nextGaussian());
        //t.(df , noc)
    }

    public static double makeRandomComponentROutlier(int numberOfKnownParameters, float[] unknownParam, float[] knownParam) {
        Random random = new Random();
        double sum = 0;
        sum += unknownParam[0];
        for (int i = 1; i <= numberOfKnownParameters; i++) {
            sum += unknownParam[i] * knownParam[i - 1];
        }
        return sum + 0.1 * (float) random.nextGaussian();
        //t.(df , noc)
    }
}
