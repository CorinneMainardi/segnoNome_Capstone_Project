package it.epicode.segnoNome.auth.services;


import it.epicode.segnoNome.auth.dto.requests.UserImgDTO;
import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.auth.exceptions.InvalidCredentialsException;
import it.epicode.segnoNome.auth.repositories.AppUserRepository;
import it.epicode.segnoNome.auth.utils.JwtTokenUtil;
import it.epicode.segnoNome.modules.entities.Dictionary;
import it.epicode.segnoNome.modules.repositories.DictionaryRepository;
import it.epicode.segnoNome.modules.services.CloudinarySvc;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Validated
public class AppUserService {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private CloudinarySvc cloudinarySvc;

    /**
     * ✅ Carica e aggiorna l'immagine del profilo utente
     */
    @Transactional
    public AppUser uploadUserImage(Long userId, UserImgDTO imgRequest) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // ✅ Carica l'immagine su Cloudinary e ottieni l'URL
        String imageUrl = cloudinarySvc.uploadImage(imgRequest.getImg());

        // ✅ Salva l'URL nel database
        user.setImgUrl(imageUrl);
        return appUserRepository.save(user);
    }



    public AppUser registerUser(String username, String password, String firstName, String lastName, Set<Role> roles) {
        if (appUserRepository.existsByUsername(username)) {
            throw new EntityExistsException("Username già in uso");
        }

        AppUser appUser = new AppUser();
        appUser.setUsername(username);
        appUser.setPassword(passwordEncoder.encode(password));
        appUser.setFirstName(firstName);
        appUser.setLastName(lastName);
        appUser.setRoles(roles);

        return appUserRepository.save(appUser);
    }
    public List<AppUser> getAllUsers() {
        return appUserRepository.findAll();
    }

    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    public String authenticateUser(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("❌ Username o password errati", e);
        }
    }



    public AppUser loadUserByUsername(String username)  {
        AppUser appUser = appUserRepository.findByUsername(username)
            .orElseThrow(() -> new EntityNotFoundException("Utente non trovato con username: " + username));


        return appUser;
    }

    @Transactional
    public AppUser addFavoriteD(Long userId, Long dictionaryId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Dictionary dictionary = dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new EntityNotFoundException("Dictionary entry not found"));

        // ✅ Evita duplicati nei preferiti
        if (!user.getFavoritesD().contains(dictionary)) {
            user.getFavoritesD().add(dictionary);
            appUserRepository.save(user);
        }

        return user;
    }

    public List<Dictionary> getFavoritesD(Long userId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return new ArrayList<>(user.getFavoritesD());
    }
    @Transactional
    public AppUser removeFavoriteD(Long userId, Long dictionaryId) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Dictionary dictionary = dictionaryRepository.findById(dictionaryId)
                .orElseThrow(() -> new EntityNotFoundException("Dictionary entry not found"));


        if (user.getFavoritesD().contains(dictionary)) {
            user.getFavoritesD().remove(dictionary);
            appUserRepository.save(user);
        }

        return user;
    }

}
