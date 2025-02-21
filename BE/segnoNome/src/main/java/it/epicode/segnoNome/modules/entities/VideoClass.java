package it.epicode.segnoNome.modules.entities;

import it.epicode.segnoNome.auth.entities.AppUser;
import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "videoclasses")
public class VideoClass {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String title;
    private String description;
    private String videoClassUrl;
    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private AppUser creator;
}
