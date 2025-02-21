package it.epicode.segnoNome.modules.controllers;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.modules.dto.LessonInterestRequest;
import it.epicode.segnoNome.modules.entities.LessonInterest;
import it.epicode.segnoNome.modules.services.LessonInterestSvc;
import it.epicode.segnoNome.modules.services.UserRoleSvc;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lesson-interest")
public class LessonInterestController {

    @Autowired
    private LessonInterestSvc lessonInterestSvc;
    @Autowired
    private UserRoleSvc userRoleSvc;
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    @GetMapping("/all")
    public List<LessonInterest> getAllRequests(@AuthenticationPrincipal AppUser user) {
       String username = user.getUsername();
        userRoleSvc.boh(username);
        return lessonInterestSvc.getAllRequests(user.getUsername());
    }


    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    @GetMapping("/handled")
    public List<LessonInterest> getHandledRequests(@AuthenticationPrincipal AppUser user) {
        String username = user.getUsername();
        userRoleSvc.boh(username);
        return lessonInterestSvc.getHandledRequests(user.getUsername());
    }


    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    @GetMapping("/pending")
    public List<LessonInterest> getPendingRequests(@AuthenticationPrincipal AppUser user) {
        String username = user.getUsername();
        userRoleSvc.boh(username);
        return lessonInterestSvc.getPendingRequests(user.getUsername());
    }


    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create")
    public LessonInterest createInterest(@AuthenticationPrincipal AppUser user,
                                         @Valid @RequestBody LessonInterestRequest request) {
        return lessonInterestSvc.createInterestRequest(request, user.getUsername());
    }

    @PreAuthorize("hasRole('CREATOR')")
    @PutMapping("/{id}/update-status")
    public LessonInterest updateStatus(@AuthenticationPrincipal AppUser user,
                                       @PathVariable Long id,
                                       @RequestParam boolean contacted,
                                       @RequestParam boolean interested,
                                       @RequestParam boolean toBeRecontacted,
                                       @RequestParam(required = false) String note,
                                       @RequestParam boolean handled) {
        return lessonInterestSvc.updateInterestStatus(id, contacted, interested, toBeRecontacted, note, handled, user.getUsername());
    }



    @PreAuthorize("hasRole('CREATOR')")
    @DeleteMapping("/{id}")
    public void deleteRequest(@AuthenticationPrincipal AppUser user, @PathVariable Long id) {
        lessonInterestSvc.deleteInterestRequest(id, user.getUsername());
    }
}