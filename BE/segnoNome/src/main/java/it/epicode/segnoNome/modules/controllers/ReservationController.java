package it.epicode.segnoNome.modules.controllers;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.modules.dto.ReservationRequest;
import it.epicode.segnoNome.modules.entities.Reservation;
import it.epicode.segnoNome.modules.services.ReservationSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    @Autowired
    private ReservationSvc reservationSvc;

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping
    public ResponseEntity<List<Reservation>> finAllReservation(){
        return ResponseEntity.ok(reservationSvc.findAllReservation());
    }
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Reservation>findReservationById(@PathVariable Long id){
        return ResponseEntity.ok(reservationSvc.findReservationById(id));
    }
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<Reservation>> getUserReservations(@PathVariable Long userId) {
        List<Reservation> reservations = reservationSvc.getUserReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping
    @PreAuthorize(" hasRole('ROLE_USER')")
    public ResponseEntity<Reservation> reserveSeat(@RequestBody ReservationRequest reservationRequest, @AuthenticationPrincipal AppUser appUser) {
        if (appUser == null) {
            throw new RuntimeException("User is not authenticated");
        }

        if (!appUser.getRoles().contains(Role.ROLE_USER)) {
            throw new RuntimeException("Only users can reserve seats");
        }

        Reservation reservation = reservationSvc.reserveSeat(reservationRequest, appUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Reservation> deleteReservation(@PathVariable Long userId, Long eventId, @AuthenticationPrincipal AppUser appUser){
        return ResponseEntity.ok( reservationSvc.deleteReservation(userId, eventId));
    }
}
