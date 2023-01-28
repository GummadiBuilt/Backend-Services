package com.infra.gummadibuilt.tenderbidinfo;

import com.infra.gummadibuilt.tender.model.TenderInfo;
import com.infra.gummadibuilt.tenderbidinfo.model.TenderBidInfo;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TenderBidInfoDao extends JpaRepository<TenderBidInfo, Integer> {

    Optional<TenderBidInfo> findByTenderInfoAndApplicationUser(TenderInfo tenderInfo, ApplicationUser applicationUser);
    List<TenderBidInfo> findAllByTenderInfoAndApplicationUserIn(TenderInfo tenderInfo, List<ApplicationUser> applicationUser);
}
