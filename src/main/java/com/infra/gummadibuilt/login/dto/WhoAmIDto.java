package com.infra.gummadibuilt.login.dto;

import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import com.infra.gummadibuilt.userandrole.model.ApplicationUser;
import lombok.Data;

import java.io.Serializable;

@Data
public class WhoAmIDto implements Serializable {

    private String firstName;

    private String lastName;

    private String emailAddress;

    private ApplicationRole applicationRole;

    private boolean credentialsExpired;

    public static WhoAmIDto valueOf(ApplicationUser applicationUser) {
        WhoAmIDto whoAmIDto = new WhoAmIDto();
        whoAmIDto.setApplicationRole(applicationUser.getApplicationRole());
        whoAmIDto.setFirstName(applicationUser.getFirstName());
        whoAmIDto.setLastName(applicationUser.getLastName());
        whoAmIDto.setEmailAddress(applicationUser.getEmail());
        whoAmIDto.setCredentialsExpired(applicationUser.isCredentialsExpired());

        return whoAmIDto;
    }
}
