package it.epicode.segnoNome.modules.services;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.repositories.AppUserRepository;
import it.epicode.segnoNome.modules.dto.DictionaryRequest;
import it.epicode.segnoNome.modules.dto.VideoClassRequest;
import it.epicode.segnoNome.modules.entities.Dictionary;
import it.epicode.segnoNome.modules.entities.VideoClass;
import it.epicode.segnoNome.modules.exceptions.InternalServerErrorException;
import it.epicode.segnoNome.modules.repositories.DictionaryRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DictionarySvc {
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    DictionaryRepository dictionaryRepository;
    @Autowired
    UserRoleSvc userRoleSvc;



    public List<Dictionary> getAllDictionaryVideos(){
        return dictionaryRepository.findAll();
    }

    public Dictionary getDictionaryVideoById(Long id){
        if(!dictionaryRepository.existsById(id)){
            throw new EntityNotFoundException("Video not found");
        }
        return dictionaryRepository.findById(id).get();
    }



    @Transactional
    public Dictionary createDictionaryVideo(@Valid DictionaryRequest dictionaryRequest, String username) {
        try {
            Dictionary  dictionary= new Dictionary();
            BeanUtils.copyProperties(dictionaryRequest, dictionary);
            AppUser appUser = appUserRepository.findByUsername(username).get();
            dictionary.setCreator(appUser);

            return dictionaryRepository.save(dictionary);  // Salva la video class nel repository

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalServerErrorException("An error occurred while creating the video : " + ex.getMessage());
        }
    }


    @Transactional
    public Dictionary updateDictionaryVideo(Long id, @Valid DictionaryRequest dictionaryRequest, String username) {
        try {

            AppUser appUser = userRoleSvc.allowedToCreator(username);
           Dictionary dictionary = dictionaryRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Video class with id " + id + " not found"));

            BeanUtils.copyProperties(dictionaryRequest, dictionary, "id", "creator");

            return dictionaryRepository.save(dictionary);

        } catch (Exception ex) {
            throw new InternalServerErrorException("An error occurred while updating the video : " + ex.getMessage());
        }
    }




    @Transactional
    public Dictionary deleteDictionaryVideo(Long DictionaryVideoId) {
        Dictionary dictionary= dictionaryRepository.findById(DictionaryVideoId)
                .orElseThrow(() -> new EntityNotFoundException("Video  not found"));
        dictionaryRepository.delete(dictionary);
        return dictionary;
    }

}