package com.infra.gummadibuilt.admin.contractor;

import com.infra.gummadibuilt.admin.client.dto.ClientUserInfoDto;
import com.infra.gummadibuilt.admin.contractor.dto.ContractorUserInfoDto;
import com.infra.gummadibuilt.userandrole.ApplicationRoleDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContractorUserInfoService {

    private final ApplicationRoleDao applicationRoleDao;

    public ContractorUserInfoService(ApplicationRoleDao applicationRoleDao) {
        this.applicationRoleDao = applicationRoleDao;
    }

    public List<ContractorUserInfoDto> getAll() {
        Optional<ApplicationRole> applicationRole = applicationRoleDao.findByRoleNameIgnoreCase("Contractor");
        if (applicationRole.isPresent()) {
            return applicationRoleDao.getContractorUsersInfo(applicationRole.get().getId());
        } else {
            throw new RuntimeException("Application doesn't have user role Contractor");
        }
    }

}
