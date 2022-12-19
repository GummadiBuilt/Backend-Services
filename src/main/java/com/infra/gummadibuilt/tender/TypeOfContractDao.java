package com.infra.gummadibuilt.tender;

import com.infra.gummadibuilt.tender.model.TypeOfContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeOfContractDao extends JpaRepository<TypeOfContract, Integer> {

}
