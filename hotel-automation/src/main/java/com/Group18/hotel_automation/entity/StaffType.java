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
    private String name;
    // HOUSEKEEPING, MAINTENANCE, FRONT_DESK, KITCHEN, TRANSPORT


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
