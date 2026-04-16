package com.Group18.hotel_automation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StaffType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // HOUSEKEEPING, MAINTENANCE, FRONT_DESK, KITCHEN, TRANSPORT
}
