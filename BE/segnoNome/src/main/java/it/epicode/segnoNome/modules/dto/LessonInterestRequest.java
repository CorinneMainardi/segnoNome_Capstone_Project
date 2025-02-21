package it.epicode.segnoNome.modules.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.epicode.segnoNome.modules.enums.LessonType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)  // Evita valori nulli nel JSON
public class LessonInterestRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LessonType lessonType;
    private String preferredDays;
    private String preferredTimes;
    private String city;  // Solo per lezioni in presenza

}