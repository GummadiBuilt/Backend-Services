package com.infra.gummadibuilt.tender;

import com.infra.gummadibuilt.tender.model.TenderClientDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderClientDocumentDao extends JpaRepository<TenderClientDocument, Integer> {
}
