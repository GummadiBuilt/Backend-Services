package com.infra.gummadibuilt.tender;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UploadObject {

    public static void main(String[] args) {
        final String usage = "\n" +
                "Usage:\n" +
                "  <bucketName> <objectKey> <objectPath> \n\n" +
                "Where:\n" +
                "  bucketName - The Amazon S3 bucket to upload an object into.\n" +
                "  objectKey - The object to upload (for example, book.pdf).\n" +
                "  objectPath - The path where the file is located (for example, C:/AWS/book2.pdf). \n\n";

        String bucketName = "gummadi-built";
        String objectKey = "Contracts.xlsx";
        String objectPath = "E:\\Gummadi\\Docs\\Type of Works & Contracts.xlsx";
        System.out.println("Putting object " + objectKey + " into bucket " + bucketName);
        System.out.println("  in bucket: " + bucketName);

        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.AP_SOUTH_1;
        S3Client s3 = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();

        String result = putS3Object(s3, bucketName, objectKey, objectPath);
        System.out.println("Tag information: " + result);
        s3.close();
    }

    // snippet-start:[s3.java2.s3_object_upload.main]
    public static String putS3Object(S3Client s3, String bucketName, String objectKey, String objectPath) {

        try {
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal", "test");
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .metadata(metadata)
                    .build();

            PutObjectResponse response = s3.putObject(putOb, RequestBody.fromBytes(getObjectFile(objectPath)));
            return response.eTag();

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        return "";
    }

    // Return a byte array.
    private static byte[] getObjectFile(String filePath) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {
            File file = new File(filePath);
            bytesArray = new byte[(int) file.length()];
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return bytesArray;
    }
}