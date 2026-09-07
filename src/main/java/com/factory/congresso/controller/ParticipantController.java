package com.factory.congresso.controller;

import com.factory.congresso.dto.PageResponse;
import com.factory.congresso.dto.ParticipantDto;
import com.factory.congresso.model.Participant;
import com.factory.congresso.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantRepository participantRepo;

    @GetMapping
    public PageResponse<ParticipantDto> getParticipants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<Participant> pageData = participantRepo.findAll(PageRequest.of(page, size));

        var items = pageData.getContent().stream().map(p -> {
            Map<String, Object> tpMap = new HashMap<>();
            if (p.getTouchpoints() != null) {
                p.getTouchpoints().forEach(tp -> {
                    Object val = tp.getBoolValue() != null ? tp.getBoolValue() :
                                 tp.getIntValue() != null ? tp.getIntValue() :
                                 tp.getFloatValue() != null ? tp.getFloatValue() :
                                 tp.getDateValue();
                    tpMap.put(tp.getTouchpointCode(), val);
                });
            }

            return ParticipantDto.builder()
                    .id(p.getId())
                    .originalId(p.getOriginalId())
                    .fullName(p.getFullName())
                    .email(p.getEmail())
                    .stakeholderType(p.getStakeholderType().getName())
                    .region(p.getRegion().getName())
                    .channel(p.getChannel().getName())
                    .inDemDb(p.getInDemDb())
                    .touchpoints(tpMap)
                    .build();
        }).collect(Collectors.toList());

        return new PageResponse<>(
                pageData.getTotalElements(),
                page,
                size,
                pageData.getTotalPages(),
                items
        );
    }
}