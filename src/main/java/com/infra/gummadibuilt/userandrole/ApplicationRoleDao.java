package com.infra.gummadibuilt.userandrole;

import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationRoleDao extends JpaRepository<ApplicationRole, Integer> {

    Optional<ApplicationRole> findByRoleNameIgnoreCase(String roleName);
}
