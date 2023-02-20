package com.infra.gummadibuilt.admin.enquiries;

import com.infra.gummadibuilt.admin.enquiries.model.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnquiryDao extends JpaRepository<Enquiry, Integer> {

}
