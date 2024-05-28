package ru.practicum.model;

import lombok.Data;

import javax.persistence.*;

@Table(name = "statistics")
@Entity
@Data
public class Statistic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "statistic_id")
    private Long id;
    @Column
    private String app;
    @Column
    private String uri;
    @Column
    private String ip;
    @Column(name = "created")
    private String timestamp;
}
