package com.infra.gummadibuilt.payment.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class PaymentDto {

    @Size(min = 3, max = 50)
    private String contactName;

    private long paymentAmount;

    private int roleId;

    @NotBlank
    @Size(max = 50)
    private String paymentDescription;

    @Size(max = 13)
    private String contactPhoneNumber;

    @Size(max = 50)
    private String contactEmailAddress;

    private Boolean notifyViaEmail;

    private Boolean notifyViaSms;

}
