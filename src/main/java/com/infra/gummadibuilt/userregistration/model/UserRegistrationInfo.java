package com.infra.gummadibuilt.userregistration.model;

import com.infra.gummadibuilt.common.ApproveReject;

import java.util.List;

/**
 * A Projection for the {@link UserRegistration} entity
 */
public interface UserRegistrationInfo {
    int getId();

    String getFirstName();

    String getLastName();

    String getEmail();

    ApproveReject getApproveReject();

    String getCompanyName();

    int getYearOfEstablishment();

    List<String> getTypeOfEstablishment();

    String getAddress();

    String getContactName();

    String getContactDesignation();

    String getContactPhoneNumber();

    String getContactEmailAddress();

    String getCoordinatorName();

    String getCoordinatorMobileNumber();
}