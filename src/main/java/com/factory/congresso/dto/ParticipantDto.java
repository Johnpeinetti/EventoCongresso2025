package com.factory.congresso.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data @Builder
public class ParticipantDto {
    private Long id;
    private Integer originalId;
    private String fullName;
    private String email;
    private String stakeholderType;
    private String region;
    private String channel;
    private Boolean inDemDb;
    private Map<String, Object> touchpoints;
}