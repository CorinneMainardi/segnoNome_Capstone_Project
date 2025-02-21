package it.epicode.segnoNome.modules.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DictionaryRequest {
    @NotBlank (message = "the field 'title' cannot be blank")
    private String title;
    @NotBlank(message = "the field 'description' cannot be blank")
    private String description;
    @NotBlank(message = "the field 'video' cannot be blank")
    private String dictionaryUrl;
}
