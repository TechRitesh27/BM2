package com.Group18.hotel_automation.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Double basePrice;

    @Column(nullable = false)
    private Integer capacity;

    private String bedType;
    private Integer roomSize;
    private String amenities;
    private String imageUrl;
    private Integer priority;

    @Column(nullable = false)
    private Boolean active = true;
}
