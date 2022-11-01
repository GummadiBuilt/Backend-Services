package com.infra.gummadibuilt.userandrole.model.dto;

import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import lombok.Data;
import org.checkerframework.checker.units.qual.A;

@Data
public class ApplicationRoleDto {

    private int id;

    private String roleName;

    private String roleDescription;

    private boolean displayToAll;

    private ChangeTracking changeTracking;

    public static ApplicationRoleDto valueOf(ApplicationRole applicationRole){
        ApplicationRoleDto result = new ApplicationRoleDto();
        result.setId(applicationRole.getId());
        result.setRoleName(applicationRole.getRoleName());
        result.setRoleDescription(applicationRole.getRoleDescription());
        result.setDisplayToAll(applicationRole.isDisplayToAll());
        result.setChangeTracking(applicationRole.getChangeTracking());
        return result;
    }
}
