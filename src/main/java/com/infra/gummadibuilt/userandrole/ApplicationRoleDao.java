package com.infra.gummadibuilt.userandrole;

import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRoleDao extends JpaRepository<ApplicationRole, Integer> {

    Optional<ApplicationRole> findByRoleNameIgnoreCase(String roleName);
}
