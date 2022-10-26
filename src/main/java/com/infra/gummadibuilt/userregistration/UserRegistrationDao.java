package com.infra.gummadibuilt.userregistration;


import com.infra.gummadibuilt.common.ApproveReject;
import com.infra.gummadibuilt.userregistration.model.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRegistrationDao extends JpaRepository<UserRegistration, Integer> {

    List<UserRegistration> findAllByApproveReject(ApproveReject approveReject);

}
