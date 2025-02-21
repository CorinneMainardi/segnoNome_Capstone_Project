package it.epicode.segnoNome.modules.repositories;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.modules.entities.Event;
import it.epicode.segnoNome.modules.entities.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(AppUser user);
    List<Reservation> findByEvent(Event event);
    Optional<Reservation> findByUserAndEvent(AppUser user, Event event);
    boolean existsByUserAndEvent(AppUser user, Event event);
    void deleteByEvent(Event event);

}