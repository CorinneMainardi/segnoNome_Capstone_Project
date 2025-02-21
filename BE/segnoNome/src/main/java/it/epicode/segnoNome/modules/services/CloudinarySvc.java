package it.epicode.segnoNome.modules.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import it.epicode.segnoNome.modules.exceptions.UploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinarySvc {
    private final Cloudinary cloudinary;

    public Map uploader(MultipartFile file, String folder)  {

        try {
            Map result =
                    cloudinary
                            .uploader()
                            .upload(file.getBytes(),
                                    Cloudinary.asMap("folder", folder, "public_id", file.getOriginalFilename()));
            return result;
        } catch (IOException e) {
            throw new UploadException("upload file error " + file.getOriginalFilename());
        }

    }

    public String uploadImage(String base64Image) {
        try {
            Map uploadResult = cloudinary.uploader().upload(base64Image, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url"); //  Restituisce l'URL dell'immagine
        } catch (IOException e) {
            throw new RuntimeException("Error uploading image to Cloudinary", e);
        }
    }
}
