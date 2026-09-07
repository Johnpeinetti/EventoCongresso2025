package com.factory.congresso.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class DemographicDto {
    private String groupName;
    private long totalParticipants;
    private double demOpenRate;
    private double standVisitRate;
    private double symposiumAttendanceRate;
    private double vipSuiteRate;
}