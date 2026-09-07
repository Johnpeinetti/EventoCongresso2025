package com.factory.congresso.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "touchpoint_catalog")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TouchpointCatalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codeName;
    private String sheetHeader;
    private String dataType;
    private String journeyPhase;
    
    @Column(length = 1000)
    private String description;
}