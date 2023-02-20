package com.infra.gummadibuilt.admin.enquiries;

import com.infra.gummadibuilt.admin.enquiries.model.Enquiry;
import com.infra.gummadibuilt.admin.enquiries.model.dto.EnquiryDto;
import com.infra.gummadibuilt.common.ChangeTracking;
import com.infra.gummadibuilt.common.LoggedInUser;
import com.infra.gummadibuilt.common.mail.MailService;
import com.infra.gummadibuilt.common.util.SaveEntityConstraintHelper;
import com.infra.gummadibuilt.userandrole.ApplicationRoleDao;
import com.infra.gummadibuilt.userandrole.ApplicationUserDao;
import com.infra.gummadibuilt.userandrole.model.ApplicationRole;
import com.infra.gummadibuilt.userregistration.UserRegistrationService;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.infra.gummadibuilt.common.util.CommonModuleUtils.ROLE_NOT_FOUND;
import static com.infra.gummadibuilt.common.util.CommonModuleUtils.getById;

@Service
public class EnquiryService {

    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);

    private final EnquiryDao enquiryDao;
    private final ApplicationRoleDao applicationRoleDao;
    private final UserRegistrationService userRegistrationService;

    private final MailService mailService;

    public EnquiryService(EnquiryDao enquiryDao,
                          ApplicationRoleDao applicationRoleDao,
                          UserRegistrationService userRegistrationService,
                          MailService mailService) {
        this.enquiryDao = enquiryDao;
        this.applicationRoleDao = applicationRoleDao;
        this.userRegistrationService = userRegistrationService;
        this.mailService = mailService;
    }

    public void enquireAboutAService(EnquiryDto enquiryDto) throws MessagingException, TemplateException, IOException {

        LoggedInUser loggedInUser = new LoggedInUser(
                enquiryDto.getUserName(),
                enquiryDto.getUserName(),
                enquiryDto.getEmailAddress(),
                "NEW-USER"
        );

        ApplicationRole applicationRole = getById(applicationRoleDao, enquiryDto.getApplicationRole(), ROLE_NOT_FOUND);

        Enquiry enquiry = new Enquiry();

        enquiry.setApplicationRole(applicationRole);
        enquiry.setUserName(enquiryDto.getUserName());
        enquiry.setEmailAddress(enquiryDto.getEmailAddress());
        enquiry.setMobileNumber(enquiryDto.getMobileNumber());
        enquiry.setEnquiryDescription(enquiryDto.getEnquiryDescription());
        enquiry.setChangeTracking(new ChangeTracking(loggedInUser.toString()));

        SaveEntityConstraintHelper.save(enquiryDao, enquiry, null);

        Map<String, Object> model = new HashMap<>();
        model.put("userName", enquiryDto.getUserName());
        model.put("emailAddress", enquiryDto.getEmailAddress());
        model.put("mobileNumber", enquiryDto.getMobileNumber());
        model.put("description", enquiryDto.getEnquiryDescription());
        model.put("appRole", applicationRole.getRoleName());

        String[] adminUsers = userRegistrationService.getAdminUserEmailAddress();
        mailService.sendMail(adminUsers, null, "enquiry.ftl", model);
    }

    public List<EnquiryDto> getListOfQueries() {
        List<Enquiry> enquiry = enquiryDao.findAll();
        return enquiry.stream().map(EnquiryDto::valueOf).collect(Collectors.toList());
    }
}
