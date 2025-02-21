package it.epicode.segnoNome.auth.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "the field 'USERNAME' cannot be blank")
    private String username;
    @NotBlank (message = "the field 'PASSWORD' cannot be blank")
    private String password;
}
