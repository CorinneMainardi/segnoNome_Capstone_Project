package it.epicode.segnoNome.auth.runners;


import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.auth.services.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;

@Component
public class AuthRunner implements ApplicationRunner {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //Optional<AppUser> adminUser = appUserService.findByUsername("admin2");
      // if (adminUser.isEmpty()) {
          // appUserService.registerUser("admin2", "adminpwd", "NomeAdmin", "CognomeAdmin", Set.of(Role.ROLE_ADMIN));
    //  }

        // Creazione dell'utente creator se non esiste
      // Optional<AppUser> creatorUser = appUserService.findByUsername("creator");
        //if (creatorUser.isEmpty()) {
           //appUserService.registerUser("creator2", "creatorpwd", "NomeCreator", "CognomeCreator", Set.of(Role.ROLE_CREATOR));
       // }

        // Creazione dell'utente user se non esiste
        //Optional<AppUser> normalUser = appUserService.findByUsername("user");
       // if (normalUser.isEmpty()) {
       //    appUserService.registerUser("user2", "userpwd", "NomeUser", "CognomeUser", Set.of(Role.ROLE_USER));
        //}
    }
    }


