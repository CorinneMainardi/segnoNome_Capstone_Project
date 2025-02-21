package it.epicode.segnoNome.modules.services;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.modules.dto.EventRequest;
import it.epicode.segnoNome.modules.entities.Event;
import it.epicode.segnoNome.modules.exceptions.InternalServerErrorException;
import it.epicode.segnoNome.modules.exceptions.ReservationException;
import it.epicode.segnoNome.modules.exceptions.UploadException;
import it.epicode.segnoNome.modules.repositories.EventRepository;
import it.epicode.segnoNome.modules.repositories.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class EventSvc {
    @Autowired
    EventRepository eventRepository;
    @Autowired
    ReservationRepository reservationRepository;

    public List<Event> getAllEvents(){
        return eventRepository.findAll();
    }

    public Event getEventById(Long id){
        if(!eventRepository.existsById(id)){
            throw new EntityNotFoundException("Event not found");
        }
        return eventRepository.findById(id).get();
    }
    public Event createEvent(@Valid EventRequest eventRequest, @AuthenticationPrincipal AppUser appUser) {
        try {

            if (!appUser.getRoles().contains(Role.ROLE_CREATOR)) {
                throw new AccessDeniedException("You do not have permission to create an event.");
            }
            if (eventRequest.getAvailableSeats() <= 0) {
                throw new ReservationException("Available seats must be greater than zero");
            }

            Event event = new Event();

            BeanUtils.copyProperties(eventRequest, event);

            event.setCreator(appUser);


            return eventRepository.save(event);
        } catch (Exception ex) {

            ex.printStackTrace();
            throw new InternalServerErrorException("An error occurred while creating the event: " + ex.getMessage());
        }
    }

    public Event updateEvent(Long id, @Valid EventRequest eventRequest) {
        try {
            Event event = eventRepository.findById(id).orElseThrow(() ->
                    new EntityNotFoundException("Event with id number " + id + " not found")
            );
            BeanUtils.copyProperties(eventRequest, event);

            return eventRepository.save(event);

        } catch (UploadException ex) {
            throw new IllegalArgumentException ("Failed to update event due to upload issue: " + ex.getMessage(), ex);

        } catch (Exception ex) {
            throw new InternalServerErrorException("An unexpected error occurred while updating the event: " + ex.getMessage());
        }
    }

    @Transactional
    public Event deleteEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        reservationRepository.deleteByEvent(event);

        eventRepository.delete(event);
        return event;
    }

}
