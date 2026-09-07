package com.factory.congresso.controller;

import com.factory.congresso.dto.*;
import com.factory.congresso.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/funnel")
    public List<FunnelStageDto> getFunnel(
            @RequestParam(required = false) String stakeholder,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String channel) {
        return analyticsService.getFunnel(stakeholder, region, channel);
    }

    @GetMapping("/demographics")
    public List<DemographicDto> getDemographics(
            @RequestParam(defaultValue = "stakeholder") String dimension,
            @RequestParam(required = false) String stakeholder,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String channel) {
        return analyticsService.getDemographics(dimension, stakeholder, region, channel);
    }

    @GetMapping("/daily-trends")
    public List<DailyTrendDto> getDailyTrends(
            @RequestParam(required = false) String stakeholder,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String channel) {
        return analyticsService.getDailyTrends(stakeholder, region, channel);
    }
}