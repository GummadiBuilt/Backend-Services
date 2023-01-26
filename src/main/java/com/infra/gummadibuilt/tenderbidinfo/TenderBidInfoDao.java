package com.infra.gummadibuilt.tenderbidinfo;

import com.infra.gummadibuilt.tenderbidinfo.model.TenderBidInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenderBidInfoDao extends JpaRepository<TenderBidInfo, Integer> {
}
