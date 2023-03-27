package com.infra.gummadibuilt.common.file;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class AmazonFileService {

    private static final Logger logger = LoggerFactory.getLogger(AmazonFileService.class);

    private final AmazonConfiguration amazonConfiguration;

    public AmazonFileService(AmazonConfiguration amazonConfiguration) {
        this.amazonConfiguration = amazonConfiguration;
    }

    public S3Client buildConnection() {
        ProfileCredentialsProvider credentialsProvider = ProfileCredentialsProvider.create();
        Region region = Region.AP_SOUTH_1;
        return S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
    }


    public String uploadFile(String pathToUpload, Map<String, String> metaData, MultipartFile file) {

        String filePath = String.format("%s/", pathToUpload);
        logger.info(String.format("Uploading file to path %s", filePath));
        return putS3Object(filePath, metaData, this.buildConnection(), amazonConfiguration.getBucketName(), file);
    }

    public FileDownloadDto downloadFile(String fileLocation, String fileName) {

        String path = String.format("%s/%s", fileLocation, fileName);
        Map<String, String> response = getObjectBytes(this.buildConnection(), amazonConfiguration.getBucketName(), path);
        FileDownloadDto fileDownloadDto = new FileDownloadDto();
        fileDownloadDto.setEncodedResponse(response.get("data"));
        fileDownloadDto.setFileName(fileName);
        fileDownloadDto.setFileType(response.get("contentType"));

        return fileDownloadDto;
    }

    public void deleteFile(String fileLocation, String fileName) {
        String path = String.format("%s/%s", fileLocation, fileName);
        deleteBucketObjects(this.buildConnection(), amazonConfiguration.getBucketName(), path);
    }

    public static String putS3Object(String pathToUpload,
                                     Map<String, String> metaData,
                                     S3Client s3,
                                     String bucketName,
                                     MultipartFile file) {
        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(pathToUpload + file.getOriginalFilename())
                    .metadata(metaData)
                    .build();

            PutObjectResponse response = s3.putObject(putOb, RequestBody.fromBytes(file.getBytes()));
            return response.eTag();

        } catch (S3Exception e) {
            logger.error(String.format("Failed to upload file with error %s", e.awsErrorDetails().errorMessage()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "";
    }


    public static Map<String, String> getObjectBytes(S3Client s3, String bucketName, String path) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(path)
                    .bucket(bucketName)
                    .build();
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);
            String contentType = s3.getObject(objectRequest).response().contentType();
            byte[] data = objectBytes.asByteArray();
            logger.info(String.format("Successfully obtained bytes for file %s", path));

            Map<String, String> response = new HashMap<>();
            response.put("data", Base64.encodeBase64String(data));
            response.put("contentType", contentType);
            return response;

        } catch (S3Exception e) {
            logger.error(String.format("Failed to download file %s with error %s", path, e.awsErrorDetails().errorMessage()));
        }
        return new HashMap<>();
    }


    public static void deleteBucketObjects(S3Client s3, String bucketName, String objectName) {

        ArrayList<ObjectIdentifier> toDelete = new ArrayList<>();
        toDelete.add(ObjectIdentifier.builder()
                .key(objectName)
                .build());

        try {
            DeleteObjectsRequest dor = DeleteObjectsRequest.builder()
                    .bucket(bucketName)
                    .delete(Delete.builder()
                            .objects(toDelete).build())
                    .build();

            s3.deleteObjects(dor);

        } catch (S3Exception e) {
            logger.error(String.format("Failed to delete file %s with error %s", objectName, e.awsErrorDetails().errorMessage()));
        }

        logger.info(String.format("File %s delete successful", objectName));
    }


}
