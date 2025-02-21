package it.epicode.segnoNome.auth.dto.requests;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserImgDTO {
    @NotNull(message = "The field 'img' cannot be null")
    private String img;
}