package com.infra.gummadibuilt.userregistration;


import com.infra.gummadibuilt.common.ApproveReject;
import com.infra.gummadibuilt.userregistration.model.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRegistrationDao extends JpaRepository<UserRegistration, Integer> {

    List<UserRegistration> findAllByApproveRejectIn(List<ApproveReject> approveReject);

    Optional<UserRegistration> findByContactEmailAddressAndApproveReject(String contactEmailAddress, ApproveReject approveReject);

}
