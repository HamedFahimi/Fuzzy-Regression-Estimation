package fuzzyregressionestimation;

public class desiredParametersToReport {

    private float firstParameter;
    private float secondParameter;
    private int numbOfOutliers;

    public float getFirstParameter() {
        return firstParameter;
    }

    public float getSecondParameter() {
        return secondParameter;
    }

    public int getNumbOfOutliers() {
        return numbOfOutliers;
    }

    public void setNumbOfOutliers(int numbOfOutliers) {
        this.numbOfOutliers = numbOfOutliers;
    }

    public void setFirstParameter(float firstParameter) {
        this.firstParameter = firstParameter;
    }

    public void setSecondParameter(float secondParameter) {
        this.secondParameter = secondParameter;
    }
}
