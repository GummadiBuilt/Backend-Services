package com.infra.gummadibuilt.userandrole;

import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<ApplicationUser, Integer> {

    ApplicationUser findByEmail(String email);

}
