package com.factory.congresso.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "participant_touchpoints")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParticipantTouchpoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "participant_id", nullable = false)
    private Participant participant;

    @Column(nullable = false)
    private String touchpointCode;

    private Boolean boolValue;
    private Integer intValue;
    private Double floatValue;
    private LocalDate dateValue;
}