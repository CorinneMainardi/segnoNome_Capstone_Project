package it.epicode.segnoNome.modules.controllers;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.auth.services.AppUserService;
import it.epicode.segnoNome.auth.utils.JwtTokenUtil;
import it.epicode.segnoNome.modules.dto.EventRequest;
import it.epicode.segnoNome.modules.entities.Event;
import it.epicode.segnoNome.modules.services.EventSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    @Autowired
    private EventSvc eventSvc;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AppUserService appUserService;


    //le 2 get non le gestiscvo con l'auth perché, a mio avviso, gli eventi devono essere visibili anche se non loggati
    @GetMapping
    public ResponseEntity<List<Event>>getAllEvents(){
        return ResponseEntity.ok(eventSvc.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event>getEventById(@PathVariable Long id){
        return ResponseEntity.ok(eventSvc.getEventById(id));
    }

    @PostMapping
    @PreAuthorize(" hasRole('ROLE_CREATOR')")
    public ResponseEntity<Event> createEvent(@RequestBody EventRequest eventRequest, @AuthenticationPrincipal AppUser appUser) {
        if (appUser == null) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        if (!appUser.getRoles().contains(Role.ROLE_CREATOR)) {
            throw new AccessDeniedException("You do not have permission to create an event.");
        }


        Event event = eventSvc.createEvent(eventRequest, appUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }



    @PutMapping("/{id}")
    @PreAuthorize(" hasRole('ROLE_ORGANISER')")
    public ResponseEntity<Event>updateEvent(@RequestBody EventRequest eventRequest, @PathVariable Long id, @AuthenticationPrincipal AppUser appUser){
        return ResponseEntity.ok(eventSvc.updateEvent(id, eventRequest));
    }
    @PreAuthorize(" hasRole('ROLE_ORGANISER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Event> deleteEvent(@PathVariable Long id, @AuthenticationPrincipal AppUser appUser){
        return ResponseEntity.ok(eventSvc.deleteEvent(id));
    }
}
