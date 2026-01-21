package com.Group18.hotel_automation.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_types")
@Getter @Setter @NoArgsConstructor
public class ServiceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;   // FOOD, LAUNDRY, CLEANING...

    @Column(nullable = false)
    private Double price;  // default charge

    private Boolean active = true;

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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
