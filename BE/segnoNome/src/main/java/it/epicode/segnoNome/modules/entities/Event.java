package it.epicode.segnoNome.modules.entities;


import it.epicode.segnoNome.auth.entities.AppUser;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;
    private String description;

    @Column(name = "event_date")
    private LocalDate eventDate;

    private String location;
    private int availableSeats;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private AppUser creator;

}