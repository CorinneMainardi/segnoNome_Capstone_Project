package it.epicode.segnoNome.modules.services;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.auth.repositories.AppUserRepository;
import it.epicode.segnoNome.modules.dto.ReservationRequest;
import it.epicode.segnoNome.modules.entities.Event;
import it.epicode.segnoNome.modules.entities.Reservation;
import it.epicode.segnoNome.modules.exceptions.AlreadyExistsException;
import it.epicode.segnoNome.modules.exceptions.InternalServerErrorException;
import it.epicode.segnoNome.modules.exceptions.ReservationException;
import it.epicode.segnoNome.modules.repositories.EventRepository;
import it.epicode.segnoNome.modules.repositories.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class ReservationSvc {
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private AppUserRepository userRepository;

    public List<Reservation> findAllReservation(){
        return reservationRepository.findAll();
    }


    public Reservation findReservationById(Long id){
        if(!reservationRepository.existsById(id)){
            throw new EntityNotFoundException("Reservation not found");
        }
        return reservationRepository.findById(id).get();
    }
    public List<Reservation> getUserReservations(Long userId) {
        AppUser user = userRepository.findById(userId).orElseThrow(() -> new ReservationException("User not found"));
        return reservationRepository.findByUser(user);
    }


    @Transactional
    public Reservation reserveSeat(@Valid ReservationRequest reservationRequest, @AuthenticationPrincipal AppUser appUser) {
        try {
            if (!appUser.getRoles().contains(Role.ROLE_USER)) {
                throw new AccessDeniedException("You do not have permission to reserve a seat.");
            }

            AppUser user = userRepository.findById(reservationRequest.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found with ID " + reservationRequest.getUserId()));
            Event event = eventRepository.findById(reservationRequest.getEventId())
                    .orElseThrow(() -> new EntityNotFoundException("Event not found with ID " + reservationRequest.getEventId()));

            if (event.getAvailableSeats() < reservationRequest.getSeatCount()) {
                throw new ReservationException("Not enough available seats for event ID " + event.getId());
            }

            Reservation reservation = new Reservation();
            reservation.setUser(user);
            reservation.setEvent(event);
            reservation.setSeatCount(reservationRequest.getSeatCount()); // Salva il numero di posti prenotati

            event.setAvailableSeats(event.getAvailableSeats() - reservationRequest.getSeatCount());
            eventRepository.save(event);

            return reservationRepository.save(reservation);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalServerErrorException("An error occurred while creating the reservation: " + ex.getMessage());
        }
    }



    @Transactional
    public Reservation deleteReservation(Long userId, Long eventId) {
        try {

            AppUser user = userRepository.findById(userId).orElseThrow(() -> new ReservationException("User not found"));

            Event event = eventRepository.findById(eventId).orElseThrow(() -> new ReservationException("Event not found"));

            Reservation reservation = reservationRepository.findByUserAndEvent(user, event)
                    .orElseThrow(() -> new ReservationException("Reservation not found for this user and event"));

            reservationRepository.delete(reservation);

            event.setAvailableSeats(event.getAvailableSeats() + 1);

            eventRepository.save(event);
            return reservation;
        } catch (Exception ex) {

            throw new InternalServerErrorException("An unexpected error occurred while deleting the reservation: " + ex.getMessage());
        }
    }

}
