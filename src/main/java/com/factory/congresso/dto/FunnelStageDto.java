package com.factory.congresso.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class FunnelStageDto {
    private String stage;
    private long count;
    private double percentage;
}