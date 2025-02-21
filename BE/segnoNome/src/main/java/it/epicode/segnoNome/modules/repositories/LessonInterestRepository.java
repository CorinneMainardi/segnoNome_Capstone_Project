package it.epicode.segnoNome.modules.repositories;

import it.epicode.segnoNome.modules.entities.LessonInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonInterestRepository extends JpaRepository<LessonInterest, Long> {

    List<LessonInterest> findByHandledTrue();  // Ottiene solo le richieste gestite

    List<LessonInterest> findByHandledFalse();
}