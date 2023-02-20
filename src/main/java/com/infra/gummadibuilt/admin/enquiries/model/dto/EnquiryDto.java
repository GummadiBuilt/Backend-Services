package com.infra.gummadibuilt.admin.enquiries.model.dto;

import com.infra.gummadibuilt.admin.enquiries.model.Enquiry;
import com.infra.gummadibuilt.common.ChangeTracking;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EnquiryDto {

    private int id;

    @NotBlank
    @Size(max = 25)
    private String userName;

    @NotNull
    private int applicationRole;

    @NotBlank
    @Size(max = 10)
    private String mobileNumber;

    @NotBlank
    @Size(max = 50)
    private String emailAddress;

    @NotBlank
    @Size(max = 500)
    private String enquiryDescription;

    private ChangeTracking changeTracking;

    public static EnquiryDto valueOf(Enquiry enquiry){
        EnquiryDto response = new EnquiryDto();

        response.setId(enquiry.getId());
        response.setApplicationRole(enquiry.getApplicationRole().getId());
        response.setChangeTracking(enquiry.getChangeTracking());
        response.setEnquiryDescription(enquiry.getEnquiryDescription());
        response.setEmailAddress(enquiry.getEmailAddress());
        response.setMobileNumber(enquiry.getMobileNumber());
        response.setUserName(enquiry.getUserName());
        return  response;
    }
}
