/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fuzzyregressionestimation;

/**
 *
 * @author HAMED FAHIMI
 */
public class tupleIDSaver {

    private int ID_1;
    private int ID_2;
    
    public tupleIDSaver(int ID_1, int ID_2 ) {
        
        this.ID_1 = ID_1;
        this.ID_2 = ID_2;
    }
    
    public int whatIsID_1() {
        return ID_1;
    }
    
    public int whatIsID_2() {
        return ID_2;
    }
    
    public void setID_1(int ID_1) {
        this.ID_1 = ID_1;
    }
    
    public void setID_2(int ID_2) {
        this.ID_2 = ID_2;
    }
}
