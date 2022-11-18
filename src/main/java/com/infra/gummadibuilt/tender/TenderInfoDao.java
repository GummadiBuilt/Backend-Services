package com.infra.gummadibuilt.tender;

import com.infra.gummadibuilt.tender.model.TenderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;

public interface TenderInfoDao extends JpaRepository<TenderInfo, String> {
    @Query(value = "SELECT nextval('tender_id_seq')", nativeQuery = true)
    BigDecimal getNextVal();
}
