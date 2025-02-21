package it.epicode.segnoNome.modules.repositories;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.modules.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCreator(AppUser creator);
    List<Event> findByAvailableSeatsGreaterThan(int availableSeats);
}
