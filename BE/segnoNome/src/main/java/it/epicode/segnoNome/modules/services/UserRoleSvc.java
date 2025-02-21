package it.epicode.segnoNome.modules.services;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.auth.repositories.AppUserRepository;
import it.epicode.segnoNome.modules.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class UserRoleSvc {

    @Autowired
    AppUserRepository appUserRepository;


    public String allowedToallRoles(String username) { AppUser appUser= appUserRepository.findByUsername(username)
                .orElseThrow(() -> new it.epicode.segnoNome.modules.exceptions.UnauthorizedException("User not found"));

        if (appUser.getRoles().contains(Role.ROLE_USER) || appUser.getRoles().contains(Role.ROLE_ADMIN) || appUser.getRoles().contains(Role.ROLE_CREATOR)) {
            return appUser.getUsername();
        } else {
            throw new UnauthorizedException("Access denied: user does not have the necessary privileges");
        }
    }

    public String allowedToAmin(String username) {
        // Recupera l'utente AppUser dal database usando lo username
        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Controlla il ruolo dell'utente
        if (appUser.getRoles().contains(Role.ROLE_ADMIN)) {
            return appUser.getUsername();
        } else {
            throw new UnauthorizedException("Access denied: user does not have the necessary privileges");
        }
    }
    public AppUser allowedToCreator(String username) {
        if (username == null) {
            throw new RuntimeException("username è NULL in allowedToCreator!");
        }

        AppUser appUser = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        if (appUser.getRoles().contains(Role.ROLE_CREATOR)) {
            return appUser;
        } else {
            throw new UnauthorizedException("Access denied: user does not have the necessary privileges");
        }
    }

    public String boh(String username) {
        AppUser appUser= appUserRepository.findByUsername(username)
            .orElseThrow(() -> new it.epicode.segnoNome.modules.exceptions.UnauthorizedException("User not found"));
        System.out.println("-------------------------------------------------------------------- vaffanculo sono qui!!!" + appUser.getRoles());
        if ( appUser.getRoles().contains(Role.ROLE_ADMIN) || appUser.getRoles().contains(Role.ROLE_CREATOR)) {
            return appUser.getUsername();
        } else {

            throw new UnauthorizedException("Access denied: user does not have the necessary privileges");
        }
    }




}

