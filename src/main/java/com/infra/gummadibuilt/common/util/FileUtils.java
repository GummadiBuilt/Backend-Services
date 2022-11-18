package com.infra.gummadibuilt.common.util;

import com.infra.gummadibuilt.common.exception.InValidFileUploadedException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class FileUtils {

    private static final String[] FILE_TYPE_WHITELIST = {

            "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "csv", "txt", "jpeg", "jpg", "bmp", "tiff",
            "png", "svg", "pdf", "zip", "7z"
    };

    public static void checkFileValidOrNot(MultipartFile file) {
        String type = FilenameUtils.getExtension(file.getOriginalFilename());
        assert type != null;
        if (!Arrays.asList(FILE_TYPE_WHITELIST).contains(type.toLowerCase())) {
            throw new InValidFileUploadedException(
                    "File Type not supported: " + file.getOriginalFilename());
        }
    }
}
