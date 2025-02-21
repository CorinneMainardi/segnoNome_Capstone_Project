package it.epicode.segnoNome.modules.services;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.auth.repositories.AppUserRepository;
import it.epicode.segnoNome.modules.dto.LessonInterestRequest;
import it.epicode.segnoNome.modules.entities.LessonInterest;
import it.epicode.segnoNome.modules.enums.LessonType;
import it.epicode.segnoNome.modules.exceptions.InternalServerErrorException;
import it.epicode.segnoNome.modules.exceptions.UnauthorizedException;
import it.epicode.segnoNome.modules.repositories.LessonInterestRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LessonInterestSvc {

    @Autowired
    private LessonInterestRepository lessonInterestRepository;
    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private UserRoleSvc userRoleSvc;


    public List<LessonInterest> getAllRequests(String username) {

        return lessonInterestRepository.findAll();
    }


    public List<LessonInterest> getHandledRequests(String username) {

        return lessonInterestRepository.findByHandledTrue();
    }


    public List<LessonInterest> getPendingRequests(String username) {

        return lessonInterestRepository.findByHandledFalse();
    }


    @Transactional
    public LessonInterest updateInterestStatus(Long id, boolean contacted, boolean interested, boolean toBeRecontacted, String note, boolean handled, String username) {


        LessonInterest lessonInterest = lessonInterestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        lessonInterest.setContacted(contacted);
        lessonInterest.setInterested(interested);
        lessonInterest.setToBeRecontacted(toBeRecontacted);
        lessonInterest.setHandled(handled);

        if (note != null) {
            lessonInterest.setNote(note);
        }

        return lessonInterestRepository.save(lessonInterest);
    }



    @Transactional
    public LessonInterest createInterestRequest(@Valid LessonInterestRequest lessonInterestRequest, String username) {
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


        if (!user.getRoles().contains(Role.ROLE_USER)) {
            throw new UnauthorizedException("Solo gli utenti con ruolo USER possono creare una richiesta di interesse.");
        }

        LessonInterest lessonInterest = new LessonInterest();
        BeanUtils.copyProperties(lessonInterestRequest, lessonInterest);
        lessonInterest.setUser(user); // Associa la richiesta all'utente

        // Controllo per la città se la lezione è IN_PERSON
        if (lessonInterestRequest.getLessonType() == LessonType.IN_PERSON) {
            if (lessonInterestRequest.getCity() == null || lessonInterestRequest.getCity().isBlank()) {
                throw new IllegalArgumentException("La città è obbligatoria per le lezioni in presenza!");
            }
            lessonInterest.setCity(lessonInterestRequest.getCity());
        }

        return lessonInterestRepository.save(lessonInterest);
    }






    @Transactional
    public void deleteInterestRequest(Long id, String username) {


        LessonInterest lessonInterest = lessonInterestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        lessonInterestRepository.delete(lessonInterest);
    }
}
