package ru.practicum.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "locations")
@Data
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "location_id")
    private Long id;
    @Column
    private Float lat;
    @Column
    private Float lon;
}
