package com.infra.gummadibuilt.tender.model.dto;

import com.infra.gummadibuilt.tender.model.TenderClientDocument;
import lombok.Data;

@Data
public class TenderClientDocumentDto {

    private int id;

    private String fileName;

    private long fileSize;

    public static TenderClientDocumentDto valueOf(TenderClientDocument clientDocument) {
        TenderClientDocumentDto result = new TenderClientDocumentDto();
        result.setId(clientDocument.getId());
        result.setFileName(clientDocument.getFileName());
        result.setFileSize(clientDocument.getFileSize());
        return result;
    }
}
