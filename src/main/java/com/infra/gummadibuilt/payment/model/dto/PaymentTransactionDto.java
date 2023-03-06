package com.infra.gummadibuilt.payment.model.dto;

import com.infra.gummadibuilt.payment.model.Payment;
import com.infra.gummadibuilt.userandrole.model.dto.ApplicationRoleDto;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PaymentTransactionDto {

    private String referenceId;

    @NotNull
    private long paymentAmount;

    private ApplicationRoleDto applicationRole;

    @NotBlank
    private String shortUrl;

    @NotBlank
    private String paymentId;

    @NotNull
    private long expireBy;

    @NotBlank
    @Size(max = 50)
    private String paymentDescription;

    @NotBlank
    private String contactName;

    private String contactPhoneNumber;

    private String contactEmailAddress;

    private boolean notifyViaSms;

    private boolean notifyViaEmail;

    public static PaymentTransactionDto valueOf(Payment payment) {
        PaymentTransactionDto result = new PaymentTransactionDto();
        result.setReferenceId(payment.getReferenceId());
        result.setPaymentAmount(payment.getPaymentAmount());
        result.setShortUrl(payment.getShortUrl());
        result.setPaymentId(payment.getPaymentId());
        result.setExpireBy(payment.getExpireBy());
        result.setPaymentDescription(payment.getPaymentDescription());
        result.setContactName(payment.getContactName());
        result.setContactEmailAddress(payment.getContactEmailAddress());
        result.setNotifyViaEmail(payment.isNotifyViaEmail());
        result.setNotifyViaSms(payment.isNotifyViaSms());
        result.setApplicationRole(ApplicationRoleDto.valueOf(payment.getApplicationRole()));

        return result;
    }

}
