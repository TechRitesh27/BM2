package com.Group18.hotel_automation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_types")
@Getter
@Setter
@NoArgsConstructor
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Double price;

    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "staff_type_id", nullable = false)
    private StaffType staffType;
}
