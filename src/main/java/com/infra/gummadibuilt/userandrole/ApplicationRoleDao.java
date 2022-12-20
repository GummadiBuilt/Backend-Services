package com.infra.gummadibuilt.userandrole;

import com.infra.gummadibuilt.admin.client.dto.ClientUserInfoDto;
import com.infra.gummadibuilt.admin.contractor.dto.ContractorUserInfoDto;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRoleDao extends JpaRepository<ApplicationRole, Integer> {

    Optional<ApplicationRole> findByRoleNameIgnoreCase(String roleName);

    @Query(value = "select au.id, au.company_name, au.contact_email_address, au.contact_first_name, " +
            "au.contact_last_name, ar.role_name," +
            "(select count(*) from tender_info  where workflow_step ='SAVE' and application_user_id=au.id) as save_step," +
            "(select count(*) from tender_info  where workflow_step ='PUBLISHED' and application_user_id=au.id) as publish_step," +
            "(select count(*) from tender_info  where workflow_step ='YET_TO_BE_PUBLISHED' and application_user_id=au.id) as yet_to_publish_step," +
            "(select count(*) from tender_info  where workflow_step ='UNDER_PROCESS' and application_user_id=au.id) as under_process_step," +
            "(select count(*) from tender_info  where workflow_step ='RECOMMENDED' and application_user_id=au.id) as recommended_step," +
            "(select count(*) from tender_info  where workflow_step ='SUSPENDED' and application_user_id=au.id) as suspended_step," +
            "count(ti.workflow_step) as total_tenders from application_user au " +
            "left join application_role ar on au.application_role_id = ar.id " +
            "left join tender_info ti on au.id = ti.application_user_id " +
            "where au.application_role_id = :roleId group by au.id,ar.role_name", nativeQuery = true)
    List<ClientUserInfoDto> getClientUsersInfo(int roleId);

    @Query(value = "select au.id, au.company_name, au.contact_email_address, au.contact_first_name," +
            "au.contact_last_name, ar.role_name," +
            "(select count(*) from tender_applicants ta  where ta.application_user_id = au.id) as applied_tenders " +
            "from application_user au left join application_role ar on au.application_role_id = ar.id " +
            "left join tender_info ti on au.id = ti.application_user_id where au.application_role_id = :roleId " +
            "group by au.id,ar.role_name",nativeQuery = true)
    List<ContractorUserInfoDto> getContractorUsersInfo(int roleId);
}
