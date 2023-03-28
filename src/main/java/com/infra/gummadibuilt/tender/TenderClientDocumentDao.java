package com.infra.gummadibuilt.tender;

import com.infra.gummadibuilt.tender.model.TenderClientDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TenderClientDocumentDao extends JpaRepository<TenderClientDocument, Integer> {

    @Modifying
    @Query("delete from TenderClientDocument where id=:id")
    void deleteClientDocument(@Param("id") int id);
}
