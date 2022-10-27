package com.infra.gummadibuilt.userregistration;


import com.infra.gummadibuilt.common.ApproveReject;
import com.infra.gummadibuilt.userregistration.model.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRegistrationDao extends JpaRepository<UserRegistration, Integer> {

    List<UserRegistration> findAllByApproveReject(ApproveReject approveReject);

    Optional<UserRegistration> findByEmail(String email);

}
