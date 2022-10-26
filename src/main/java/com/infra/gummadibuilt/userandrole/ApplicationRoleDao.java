package com.infra.gummadibuilt.userandrole;

import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRoleDao extends JpaRepository<ApplicationRole, Integer> {
}
