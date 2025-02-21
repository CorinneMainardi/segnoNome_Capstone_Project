package it.epicode.segnoNome.modules.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.modules.enums.LessonType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


@Entity
@Table(name = "lesson_interests")
@Data
public class LessonInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;  // Utente che fa la richiesta

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType lessonType;

    @Column(nullable = false)
    private String preferredDays;

    @Column(nullable = false)
    private String preferredTimes;

    private String city;  // Solo se lezioni sono in presenza

    private boolean contacted = false;
    private boolean interested = false;
    private boolean toBeRecontacted = false;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(columnDefinition = "TEXT")
    private String note;
    private boolean handled= false;
}
