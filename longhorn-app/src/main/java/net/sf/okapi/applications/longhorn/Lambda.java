package net.sf.okapi.applications.longhorn;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.okapi.applications.longhorn.lambdarequest.ConversionRequest;
import net.sf.okapi.applications.longhorn.lib.ProjectUtils;
import net.sf.okapi.applications.longhorn.lib.WorkspaceUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Properties;

/**
 * Created by diwakar on 29/08/17.
 */
public class Lambda implements RequestHandler<String, String> {

    private static final String BUCKET_NAME = "cw-okapi-longhorn";

    @Override
    public String handleRequest(String input, Context context) {
        try {
            AmazonS3 amazonS3 = instantiateAmazonS3();
            ConversionRequest conversionRequest = new ObjectMapper().readValue(input, ConversionRequest.class);
            String response = null;
            switch (conversionRequest.getConversionStep()) {
                case EXTRACTION_STEP:
                    response = executeMergingStep(conversionRequest.getSourceFileS3Url(), conversionRequest.getBatchConfigS3Url(), amazonS3);
                    break;
                case MERGING_STEP:
                    break;
            }
            uploadWorkingDirectoryOnS3(amazonS3, new File(WorkspaceUtils.getWorkingDirectory()));
            return response;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return ProjectUtils.createNewProject();
    }

    public static void main(String[] args) {
        Lambda lambda = new Lambda();
        String input = "{\"conversionStep\":\"EXTRACTION_STEP\",\"batchConfigS3Url\":null,\"sourceFileS3Url\":null}";
        lambda.handleRequest(input, null);
    }

    private AmazonS3 instantiateAmazonS3() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("awsConfig.properties"));
        AWSCredentials awsCredentials = new BasicAWSCredentials(properties.getProperty("awsAccessKey"), properties.getProperty("awsSecretKey"));
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        return AmazonS3ClientBuilder.standard().withCredentials(awsCredentialsProvider).build();
    }

    private String executeMergingStep(String sourceFileS3Url, String batchConfigS3FileUrl, AmazonS3 amazonS3) throws IOException {
        File sourceFile = getFileFromS3(amazonS3, "AppBody-Sample-English.out.docx");
        File batchConfigFile = getFileFromS3(amazonS3, "rainbowExport.bconf");
        String projectId = ProjectUtils.createNewProject();
        ProjectUtils.addBatchConfig(projectId,batchConfigFile,new HashMap<String, String>());
        ProjectUtils.addInputFile(projectId,sourceFile,sourceFile.getName());
        ProjectUtils.executeProject(projectId);
        return projectId;
    }

    private void uploadWorkingDirectoryOnS3(AmazonS3 amazonS3Endpoint, File directory) throws IOException {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isFile()) {
                    amazonS3Endpoint.putObject(BUCKET_NAME, file.getAbsolutePath(), file);
                } else if (file.isDirectory()) {
                    if (file.listFiles().length == 0) {
                        amazonS3Endpoint.putObject(BUCKET_NAME, file.getAbsolutePath(), "");
                    } else {
                        uploadWorkingDirectoryOnS3(amazonS3Endpoint, file);
                    }
                }
            }
        }
    }

    private File getFileFromS3(AmazonS3 amazonS3, String fileUrl) throws IOException {
        S3Object s3Object = amazonS3.getObject(BUCKET_NAME, fileUrl);
        InputStream reader = new BufferedInputStream(
                s3Object.getObjectContent());
        File file = new File(fileUrl);
        OutputStream writer = new BufferedOutputStream(new FileOutputStream(file));

        int read = -1;

        while ((read = reader.read()) != -1) {
            writer.write(read);
        }
        writer.flush();
        writer.close();
        reader.close();
        return file;
    }
}
