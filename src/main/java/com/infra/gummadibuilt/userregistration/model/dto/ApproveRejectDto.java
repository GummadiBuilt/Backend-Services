package com.infra.gummadibuilt.userregistration.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApproveRejectDto {

    private List<Integer> requestId;

    private String actionTaken;

}
