package com.infra.gummadibuilt.common.file;

import lombok.Data;

@Data
public class FileDownloadDto {

    private String fileName;

    private String fileType;

    private String encodedResponse;
}
