package it.epicode.segnoNome.modules.controllers;

import it.epicode.segnoNome.auth.services.AppUserService;
import it.epicode.segnoNome.auth.utils.JwtTokenUtil;
import it.epicode.segnoNome.modules.dto.DictionaryRequest;
import it.epicode.segnoNome.modules.dto.VideoClassRequest;
import it.epicode.segnoNome.modules.entities.Dictionary;
import it.epicode.segnoNome.modules.entities.VideoClass;
import it.epicode.segnoNome.modules.services.DictionarySvc;
import it.epicode.segnoNome.modules.services.UserRoleSvc;
import it.epicode.segnoNome.modules.services.VideoClassSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {
    @Autowired
    private DictionarySvc dictionarySvc;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private UserRoleSvc userRoleSvc;

    @GetMapping

    public ResponseEntity<List<Dictionary>> getAllDictionaryVideos(
            @AuthenticationPrincipal it.epicode.segnoNome.auth.entities.AppUser user) {
        String username = user.getUsername();
        userRoleSvc.allowedToallRoles(username);

        return ResponseEntity.ok(dictionarySvc.getAllDictionaryVideos());
    }

    @GetMapping("/{id}")

    public ResponseEntity<Dictionary> getDictionaryVideoById(@AuthenticationPrincipal  it.epicode.segnoNome.auth.entities.AppUser user, @PathVariable Long id) {

        String username = user.getUsername();

        userRoleSvc.allowedToallRoles(username);

        return ResponseEntity.ok(dictionarySvc.getDictionaryVideoById(id));
    }



    @PostMapping
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<Dictionary> createDictionaryVideo(
            @RequestBody DictionaryRequest newDictionaryVideo,
            @AuthenticationPrincipal it.epicode.segnoNome.auth.entities.AppUser user
    ) {

        if (user == null) {
            throw new RuntimeException("AuthenticationPrincipal è NULL! Il token non è stato elaborato correttamente.");
        }

        String username = user.getUsername();
        System.out.println("Username autenticato: " + username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(dictionarySvc.createDictionaryVideo(newDictionaryVideo, username));
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<Dictionary> updateDictionaryVideo(@RequestBody DictionaryRequest newDictionaryVideo, @PathVariable Long id, @AuthenticationPrincipal  it.epicode.segnoNome.auth.entities.AppUser user) {
        String username = user.getUsername();
        userRoleSvc.allowedToCreator(username);


        return ResponseEntity.ok(dictionarySvc.updateDictionaryVideo(id,newDictionaryVideo,username));
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<Dictionary> deleteDictionaryVideo(@PathVariable Long id, @AuthenticationPrincipal  it.epicode.segnoNome.auth.entities.AppUser user){
        String username = user.getUsername();

        userRoleSvc.allowedToCreator(username);

        return ResponseEntity.ok(dictionarySvc.deleteDictionaryVideo(id));
    }
}

