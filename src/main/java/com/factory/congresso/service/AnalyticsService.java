package com.factory.congresso.service;

import com.factory.congresso.dto.*;
import com.factory.congresso.model.Participant;
import com.factory.congresso.model.ParticipantTouchpoint;
import com.factory.congresso.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final ParticipantRepository participantRepo;

    public List<FunnelStageDto> getFunnel(String stakeholder, String region, String channel) {
        List<Participant> participants = getFilteredParticipants(stakeholder, region, channel);
        long total = participants.size();
        if (total == 0) return Collections.emptyList();

        long demSent = countWithBoolTouchpoint(participants, "dem_inviata");
        long demOpen = countWithBoolTouchpoint(participants, "dem_aperta");
        long standVisit = countWithBoolTouchpoint(participants, "visita_stand");
        long vipAccess = countWithBoolTouchpoint(participants, "accesso_sala_vip");
        long symposium = countWithBoolTouchpoint(participants, "presenza_simposio");

        return List.of(
                new FunnelStageDto("Raggiunti (Totale)", total, 100.0),
                new FunnelStageDto("DEM Inviata", demSent, round((double) demSent / total * 100)),
                new FunnelStageDto("DEM Aperta", demOpen, round((double) demOpen / total * 100)),
                new FunnelStageDto("Visita Stand", standVisit, round((double) standVisit / total * 100)),
                new FunnelStageDto("Accesso Sala VIP", vipAccess, round((double) vipAccess / total * 100)),
                new FunnelStageDto("Presenza Simposio", symposium, round((double) symposium / total * 100))
        );
    }

    public List<DemographicDto> getDemographics(String dimension, String stakeholder, String region, String channel) {
        List<Participant> participants = getFilteredParticipants(stakeholder, region, channel);
        if (participants.isEmpty()) return Collections.emptyList();

        Map<String, List<Participant>> grouped = participants.stream().collect(Collectors.groupingBy(p -> {
            if ("region".equalsIgnoreCase(dimension)) return p.getRegion().getName();
            if ("channel".equalsIgnoreCase(dimension)) return p.getChannel().getName();
            return p.getStakeholderType().getName();
        }));

        List<DemographicDto> result = new ArrayList<>();
        for (Map.Entry<String, List<Participant>> entry : grouped.entrySet()) {
            List<Participant> group = entry.getValue();
            long gTotal = group.size();

            double demOpen = (double) countWithBoolTouchpoint(group, "dem_aperta") / gTotal * 100;
            double standVisit = (double) countWithBoolTouchpoint(group, "visita_stand") / gTotal * 100;
            double symposium = (double) countWithBoolTouchpoint(group, "presenza_simposio") / gTotal * 100;
            double vip = (double) countWithBoolTouchpoint(group, "accesso_sala_vip") / gTotal * 100;

            result.add(new DemographicDto(
                    entry.getKey(), gTotal, round(demOpen), round(standVisit), round(symposium), round(vip)
            ));
        }

        result.sort(Comparator.comparingLong(DemographicDto::getTotalParticipants).reversed());
        return result;
    }

    public List<DailyTrendDto> getDailyTrends(String stakeholder, String region, String channel) {
        List<Participant> participants = getFilteredParticipants(stakeholder, region, channel);
        if (participants.isEmpty()) return Collections.emptyList();

        Map<String, List<Participant>> visitorsByDate = new HashMap<>();

        for (Participant p : participants) {
            p.getTouchpoints().stream()
                    .filter(tp -> "giorno_visita".equals(tp.getTouchpointCode()) && tp.getDateValue() != null)
                    .forEach(tp -> {
                        String dateStr = tp.getDateValue().toString();
                        visitorsByDate.computeIfAbsent(dateStr, k -> new ArrayList<>()).add(p);
                    });
        }

        List<DailyTrendDto> trends = new ArrayList<>();
        for (Map.Entry<String, List<Participant>> entry : visitorsByDate.entrySet()) {
            List<Participant> visitors = entry.getValue();
            long count = visitors.size();

            double avgViews = visitors.stream()
                    .flatMap(v -> v.getTouchpoints().stream())
                    .filter(tp -> "visualizzazioni".equals(tp.getTouchpointCode()) && tp.getIntValue() != null)
                    .mapToInt(ParticipantTouchpoint::getIntValue).average().orElse(0.0);

            double avgScrolls = visitors.stream()
                    .flatMap(v -> v.getTouchpoints().stream())
                    .filter(tp -> "scroll".equals(tp.getTouchpointCode()) && tp.getIntValue() != null)
                    .mapToInt(ParticipantTouchpoint::getIntValue).average().orElse(0.0);

            trends.add(new DailyTrendDto(entry.getKey(), count, round(avgViews), round(avgScrolls)));
        }

        trends.sort(Comparator.comparing(DailyTrendDto::getDate));
        return trends;
    }

    private List<Participant> getFilteredParticipants(String stakeholder, String region, String channel) {
        Specification<Participant> spec = Specification.where(null);
        if (stakeholder != null && !stakeholder.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("stakeholderType").get("name"), stakeholder));
        }
        if (region != null && !region.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("region").get("name"), region));
        }
        if (channel != null && !channel.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("channel").get("name"), channel));
        }
        return participantRepo.findAll(spec);
    }

    private long countWithBoolTouchpoint(List<Participant> participants, String code) {
        return participants.stream()
                .filter(p -> p.getTouchpoints().stream()
                        .anyMatch(tp -> code.equals(tp.getTouchpointCode()) && Boolean.TRUE.equals(tp.getBoolValue())))
                .count();
    }

    private double round(double val) {
        return Math.round(val * 10.0) / 10.0;
    }
}