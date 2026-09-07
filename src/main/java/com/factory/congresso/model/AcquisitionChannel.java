package com.factory.congresso.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "acquisition_channels")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AcquisitionChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}