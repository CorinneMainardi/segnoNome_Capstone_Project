package it.epicode.segnoNome.modules.controllers;

import it.epicode.segnoNome.auth.services.AppUserService;
import it.epicode.segnoNome.auth.utils.JwtTokenUtil;
import it.epicode.segnoNome.modules.dto.VideoClassRequest;
import it.epicode.segnoNome.modules.entities.VideoClass;
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
@RequestMapping("/api/videoClasses")
public class VideoClassController {

    @Autowired
    private VideoClassSvc videoClassSvc;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AppUserService appUserService;
    @Autowired
    private UserRoleSvc userRoleSvc;

   @GetMapping
    public ResponseEntity<List<VideoClass>> getAllVideoClasses(
            @AuthenticationPrincipal  it.epicode.segnoNome.auth.entities.AppUser user) {
        String username = user.getUsername();
        userRoleSvc.allowedToallRoles(username);

        return ResponseEntity.ok(videoClassSvc.getAllVideoClasses());
    }

    @GetMapping("/{id}")

    public ResponseEntity<VideoClass> getVideoClassById(@AuthenticationPrincipal  it.epicode.segnoNome.auth.entities.AppUser user, @PathVariable Long id) {

        String username = user.getUsername();

        userRoleSvc.allowedToallRoles(username);

        return ResponseEntity.ok(videoClassSvc.getVideoClassById(id));
    }



    @PostMapping()
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<VideoClass> createVideoClass(
            @RequestBody VideoClassRequest newVideoClass,
            @AuthenticationPrincipal it.epicode.segnoNome.auth.entities.AppUser user
    ) {

        if (user == null) {
            throw new RuntimeException("AuthenticationPrincipal è NULL! Il token non è stato elaborato correttamente.");
        }

       String username = user.getUsername();
        userRoleSvc.allowedToCreator(username);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(videoClassSvc.createVideoClass(newVideoClass,username)); //username
    }



    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<VideoClass> updateVideoClass(@RequestBody VideoClassRequest newVideoClass, @PathVariable Long id, @AuthenticationPrincipal  it.epicode.segnoNome.auth.entities.AppUser user) {
        String username = user.getUsername();
        userRoleSvc.allowedToCreator(username);


        return ResponseEntity.ok(videoClassSvc.updateVideoClass(id,newVideoClass,username));
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<VideoClass> deleteVideoClass(@PathVariable Long id, @AuthenticationPrincipal  it.epicode.segnoNome.auth.entities.AppUser user){
        String username = user.getUsername();

        userRoleSvc.allowedToCreator(username);

        return ResponseEntity.ok(videoClassSvc.deleteVideoClass(id));
    }
}














