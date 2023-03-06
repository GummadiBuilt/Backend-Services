package com.infra.gummadibuilt.payment;

import com.infra.gummadibuilt.payment.model.Payment;
import com.infra.gummadibuilt.tender.model.TenderInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, String> {

    List<Payment> findAllByTenderInfo(TenderInfo tenderInfo);
}
