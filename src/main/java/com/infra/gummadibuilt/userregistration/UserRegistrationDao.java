package com.infra.gummadibuilt.userregistration;


import com.infra.gummadibuilt.userregistration.model.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegistrationDao extends JpaRepository<UserRegistration, Integer> {
}
