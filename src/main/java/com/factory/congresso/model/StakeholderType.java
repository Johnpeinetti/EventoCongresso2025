package com.factory.congresso.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stakeholder_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StakeholderType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}