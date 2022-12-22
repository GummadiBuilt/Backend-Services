package com.infra.gummadibuilt.userandrole;

import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import com.infra.gummadibuilt.userandrole.model.dto.UserDetailsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationUserDao extends JpaRepository<ApplicationUser, String> {

    Optional<ApplicationUser> findByContactEmailAddress(String contactEmailAddress);

    List<ApplicationUser> findAllByApplicationRole(ApplicationRole applicationRole);
}
