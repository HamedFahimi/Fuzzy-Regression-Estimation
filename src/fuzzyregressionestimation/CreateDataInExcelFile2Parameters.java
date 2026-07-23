package fuzzyregressionestimation;

import static fuzzyregressionestimation.initializeRequiredVectors.truncate;
//import static fuzzyregressionestimation.runExperimentsOverNumerousData2Parameters.directoryAsAString;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import jdistlib.NonCentralT;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class CreateDataInExcelFile2Parameters {

    private static int min_X; //in the new code it will be x_1 to x_4
    private static int max_X;
    private static double random_x;
    private static float Alpha_0;//similar 0 to 5
    private static float Alpha_1;//...
    private static float Beta_0;
    private static float Beta_1;
    private static float Gamma_0;
    private static float Gamma_1;
    private static float outlierFactor;
    private static float desiredStandardDeviation;
    private static float desiredMeanY;
    private static float desiredMeanL;
    private static float desiredMeanR;
    private static float df_y;
    private static float df_l;
    private static float df_r;
    private static float noc_y;
    private static float noc_l;
    private static float noc_r;
    private static Random random = new Random();
    private static NonCentralT nct_y;
    private static NonCentralT nct_l;
    private static NonCentralT nct_r;

    public CreateDataInExcelFile2Parameters() {

        min_X = 100; //in the new code it will be x_1 to x_4
        max_X = 200;
        Alpha_0 = 10f;//similar 0 to 5
        Alpha_1 = 3f;//...
        Beta_0 = -15f;
        Beta_1 = 0.1f;
        Gamma_0 = -40f;
        Gamma_1 = 0.2f;
        outlierFactor = 0.9f;
        desiredStandardDeviation = 1.0f;
        desiredMeanY = 0.0f;
        desiredMeanL = 0.0f;
        desiredMeanR = 0.0f;
        df_y = 5.0f;
        df_l = 10.0f;
        df_r = 10.0f;
        noc_y = 50.0f;
        noc_l = 10.0f;
        noc_r = 20.0f;
        random = new Random();
        nct_y = new NonCentralT(df_y, noc_y);
        nct_l = new NonCentralT(df_l, noc_l);
        nct_r = new NonCentralT(df_r, noc_r);
    }

    public String generateDataOfExperiment1(int dataFileNumber, int N) {
        try {
           String filenameDir = "C:\\Users\\Win10\\Documents\\NetBeansProjects\\fuzzyregressionestimation\\"
                   + "generatedData" + dataFileNumber + ".xls";
            try (
                HSSFWorkbook workbook = new HSSFWorkbook()) {
                HSSFSheet sheet = workbook.createSheet("Data");
                double random_y;
                double random_l;
                double random_r;
                for (int i = 0; i < N; i++) {
                    random_x = min_X + random.nextFloat() * (max_X - min_X);//5 random x should be generated
                    HSSFRow row = sheet.createRow((short) i);
                    row.createCell(0).setCellValue(truncate((float) random_x, 2));
                    if (i < outlierFactor * N) {
                        random_y = makeRandomComponent(Alpha_0, Alpha_1, desiredMeanY);//alpha_0 to 5
                        random_l = makeRandomComponent(Beta_0, Beta_1, desiredMeanL);//..
                        random_r = makeRandomComponent(Gamma_0, Gamma_1, desiredMeanR);//..
                    } else {
                        //fix method name
                        //..
                        random_y = makeRandomComponentYOutlier(Alpha_0, Alpha_1, df_y, noc_y
                        //add df = 10, noc = 50 and erase desiredMeanOutlierL
                        );

                        //df=10, noc=10
                        random_l = makeRandomComponentLOutlier(Beta_0, Beta_1, df_l, noc_l);

                        //df=10, noc=20
                        random_r = makeRandomComponentROutlier(Gamma_0, Gamma_1, df_r, noc_r);
                    }
                    row.createCell(1).setCellValue(truncate((float) random_y, 2));
                    row.createCell(2).setCellValue(truncate((float) random_l, 2));
                    row.createCell(3).setCellValue(truncate((float) random_r, 2));
                }
                try (FileOutputStream fileOut = new FileOutputStream(filenameDir)) {
                    workbook.write(fileOut);
                }
            }
        } catch (IOException e) {
        }
        String filename = "generatedData" + dataFileNumber + ".xls";
        return filename;
    }

    public static double makeRandomComponent(double param_0, double param_1, double desiredMeanOrOutlier) {
        return param_0 + param_1 * random_x + (random.nextGaussian() * desiredStandardDeviation + desiredMeanOrOutlier);
    }

    public static double makeRandomComponentYOutlier(double param_0, double param_1, double df_y, double noc_y
    ) //6 parameters
    {
        return param_0 + param_1 * random_x + (nct_y.random());
        //        return param_0 + param_1 * random_x1 + param_2 * random_x2 +...(nct_y.random());

        //t.(df , noc)
    }

    public static double makeRandomComponentLOutlier(double param_0, double param_1, double df_l, double noc_l
    ) {
        return param_0 + param_1 * random_x + (nct_l.random());
        //t.(df , noc)
    }

    public static double makeRandomComponentROutlier(double param_0, double param_1, double df_r, double noc_r
    ) {
        return param_0 + param_1 * random_x + (nct_r.random());
        //t.(df , noc)
    }
}
