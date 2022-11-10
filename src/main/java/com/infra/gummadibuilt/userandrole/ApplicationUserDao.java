package com.infra.gummadibuilt.userandrole;

import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationUserDao extends JpaRepository<ApplicationUser, Integer> {
    Optional<ApplicationUser> findByContactEmailAddress(String contactEmailAddress);

    List<ApplicationUser> findAllByApplicationRole(ApplicationRole applicationRole);
}
