package com.infra.gummadibuilt.admin.client;

import com.infra.gummadibuilt.admin.client.dto.ClientUserInfoDto;
import com.infra.gummadibuilt.userandrole.ApplicationRoleDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientUserInfoService {

    private final ApplicationRoleDao applicationRoleDao;

    public ClientUserInfoService(ApplicationRoleDao applicationRoleDao) {
        this.applicationRoleDao = applicationRoleDao;
    }

    public List<ClientUserInfoDto> getAll() {
        Optional<ApplicationRole> applicationRole = applicationRoleDao.findByRoleNameIgnoreCase("Client");
        if (applicationRole.isPresent()) {
            return applicationRoleDao.getClientUsersInfo(applicationRole.get().getId());
        } else {
            throw new RuntimeException("Application doesn't have user role Client");
        }
    }

}
