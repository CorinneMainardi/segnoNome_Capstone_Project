package it.epicode.segnoNome.auth.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {

    private final Map<String, String> captchaStore = new ConcurrentHashMap<>();

    // API per generare un Captcha
    @GetMapping("/generate")
    public ResponseEntity<Map<String, String>> generateCaptcha() {
        String captchaCode = UUID.randomUUID().toString().substring(0, 6); // Genera un codice casuale di 6 caratteri
        String captchaId = UUID.randomUUID().toString(); // ID univoco per il captcha
        captchaStore.put(captchaId, captchaCode);

        Map<String, String> response = new HashMap<>();
        response.put("captchaId", captchaId);
        response.put("captchaCode", captchaCode); // (Solo per testing, in produzione dovresti inviare un'immagine)

        return ResponseEntity.ok(response);
    }

    // API per validare il Captcha inserito dall'utente
    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateCaptcha(@RequestBody Map<String, String> request) {
        String captchaId = request.get("captchaId");
        String userInput = request.get("captcha");

        if (captchaStore.containsKey(captchaId) && captchaStore.get(captchaId).equals(userInput)) {
            captchaStore.remove(captchaId); // Cancella il captcha usato
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.ok(false);
    }
}
