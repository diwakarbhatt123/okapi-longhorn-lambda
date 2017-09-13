package net.sf.okapi.applications.longhorn.lambdarequest;

/**
 * Created by diwakar on 30/08/17.
 */
public class ConversionRequest {

    private ConversionStep conversionStep;
    private String batchConfigS3Url;
    private String sourceFileS3Url;

    public ConversionStep getConversionStep() {
        return conversionStep;
    }

    public void setConversionStep(ConversionStep conversionStep) {
        this.conversionStep = conversionStep;
    }

    public String getBatchConfigS3Url() {
        return batchConfigS3Url;
    }

    public void setBatchConfigS3Url(String batchConfigS3Url) {
        this.batchConfigS3Url = batchConfigS3Url;
    }

    public String getSourceFileS3Url() {
        return sourceFileS3Url;
    }

    public void setSourceFileS3Url(String sourceFileS3Url) {
        this.sourceFileS3Url = sourceFileS3Url;
    }

    @Override
    public String toString() {
        return "ConversionRequest{" +
                "conversionStep='" + conversionStep + '\'' +
                ", batchConfigS3Url='" + batchConfigS3Url + '\'' +
                ", sourceFileS3Url='" + sourceFileS3Url + '\'' +
                '}';
    }
}
