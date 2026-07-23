package fuzzyregressionestimation;

import java.io.IOException;

public class runExperimentsOverDataTest {

 //   private final int n;
 //   private final boolean solveOverLeftCenter;
 //   private final boolean solveOverCenter;
 //   private final boolean solveOverRightCenter;

   /*
    public runExperimentsOverDataTest(int n, boolean solveOverLeftCenter, boolean solveOverCenter, boolean solveOverRightCenter) throws IOException {
        this.n = n;
   //     this.solveOverLeftCenter = solveOverLeftCenter;
  //      this.solveOverCenter = solveOverCenter;
    //    this.solveOverRightCenter = solveOverRightCenter;
        runExperimentsOverData X = new runExperimentsOverData(n, solveOverLeftCenter, solveOverCenter, solveOverRightCenter);
        int numberOfSamples = X.getNumberOfSamples();
        String s = "sampleData(n=" + numberOfSamples + ")-1.xls";
        //   String s = "exampleData(n=" + numberOfSamples + ")-2.xls";

        //   UpdateExcelFile B = new UpdateExcelFile();
        //    B.generateExcel(s);
        //     String dataName = "added" + s;
        String dataName = s;

        System.out.println("We are processing " + dataName);
        int NUMBER_OF_All_MUTUAL_EQUATIONS = X.choose2OverN();
        X.readDataFromExcel(dataName);
        boolean sortWithInsertionsort = !false;
        long startTime = System.currentTimeMillis();
        X.solveAllPossibleMutualSystems(NUMBER_OF_All_MUTUAL_EQUATIONS, sortWithInsertionsort);
        X.findBestParameters(NUMBER_OF_All_MUTUAL_EQUATIONS, sortWithInsertionsort);
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("Time for execution in minutes:" + (float) endTime / 60000);

    }
*/
    
    public static void main(String[] args) throws IOException {
        // PrintStream out = new PrintStream(new FileOutputStream("myoutput.txt"));
        //    System.setOut(out);
        int n = 30;
        boolean solveOverLeftCenter = false;
        boolean solveOverCenter = true;
        boolean solveOverRightCenter = false;
        runExperimentsOverData_IdealCase X = new runExperimentsOverData_IdealCase(n, solveOverLeftCenter, solveOverCenter, solveOverRightCenter);
        int numberOfSamples = X.getNumberOfSamples();
        String s = "sampleData(n=" + numberOfSamples + ")-1.xls";
        //   String s = "exampleData(n=" + numberOfSamples + ")-2.xls";

     //   UpdateExcelFile B = new UpdateExcelFile();
    //    B.generateExcel(s);
   //     String dataName = "added" + s;
             String dataName =  s;

        System.out.println("We are processing " + dataName);
        int NUMBER_OF_All_MUTUAL_EQUATIONS = X.choose2OverN();
        X.readDataFromExcel(dataName);
        boolean sortWithInsertionsort = !false;
        long startTime = System.currentTimeMillis();
        X.solveAllPossibleMutualSystems(NUMBER_OF_All_MUTUAL_EQUATIONS, sortWithInsertionsort);
        X.findBestParameters(NUMBER_OF_All_MUTUAL_EQUATIONS, sortWithInsertionsort);
        long endTime = System.currentTimeMillis() - startTime;
        System.out.println("Time for execution in minutes:" + (float) endTime / 60000);
    }
     
}
