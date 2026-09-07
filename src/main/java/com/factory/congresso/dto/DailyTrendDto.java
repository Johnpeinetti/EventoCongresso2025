package com.factory.congresso.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class DailyTrendDto {
    private String date;
    private long standVisitors;
    private double avgViews;
    private double avgScrolls;
}