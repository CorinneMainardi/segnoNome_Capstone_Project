package it.epicode.segnoNome.modules.services;

import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.enums.Role;
import it.epicode.segnoNome.auth.repositories.AppUserRepository;
import it.epicode.segnoNome.modules.dto.VideoClassRequest;
import it.epicode.segnoNome.modules.entities.VideoClass;
import it.epicode.segnoNome.modules.exceptions.InternalServerErrorException;
import it.epicode.segnoNome.modules.exceptions.UploadException;
import it.epicode.segnoNome.modules.repositories.VideoClassRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VideoClassSvc {
    @Autowired
    AppUserRepository appUserRepository;
    @Autowired
    VideoClassRepository videoClassRepository;
    @Autowired
    UserRoleSvc userRoleSvc;

    public List<VideoClass> getAllVideoClasses(){
        return videoClassRepository.findAll();
    }

    public VideoClass getVideoClassById(Long id){
        if(!videoClassRepository.existsById(id)){
            throw new EntityNotFoundException("Video not found");
        }
        return videoClassRepository.findById(id).get();
    }
@Transactional
    public VideoClass createVideoClass(@Valid VideoClassRequest videoClassRequest,  String username) {
        try {



            VideoClass videoClass = new VideoClass();
            BeanUtils.copyProperties(videoClassRequest, videoClass);
            AppUser appUser = appUserRepository.findByUsername(username).get();
            videoClass.setCreator(appUser);

            return videoClassRepository.save(videoClass);  // Salva la video class nel repository

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InternalServerErrorException("An error occurred while creating the video class: " + ex.getMessage());
        }
    }


    @Transactional
    public VideoClass updateVideoClass(Long id, @Valid VideoClassRequest videoClassRequest, String username) {
        try {

            AppUser appUser = userRoleSvc.allowedToCreator(username);
            VideoClass videoClass = videoClassRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Video class with id " + id + " not found"));

            BeanUtils.copyProperties(videoClassRequest, videoClass, "id", "creator");

            return videoClassRepository.save(videoClass);

        } catch (Exception ex) {
            throw new InternalServerErrorException("An error occurred while updating the video class: " + ex.getMessage());
        }
    }




    @Transactional
    public VideoClass deleteVideoClass(Long videoClassId) {
        VideoClass videoClass= videoClassRepository.findById(videoClassId)
                .orElseThrow(() -> new EntityNotFoundException("Video class not found"));
        videoClassRepository.delete(videoClass);
        return videoClass;
    }

}
