package it.epicode.segnoNome.modules.controllers;

import com.paypal.base.rest.PayPalRESTException;
import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.repositories.AppUserRepository;
import it.epicode.segnoNome.auth.utils.JwtTokenUtil;
import it.epicode.segnoNome.modules.services.PaymentSvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;





@RestController
@RequestMapping("/api/payments")
public class PaymentController {
        @Autowired
        private PaymentSvc paymentSvc;

        @Autowired
        private AppUserRepository appUserRepository;
    @Autowired
    private JwtTokenUtil jwtUtils; // 🔹 Usa una classe per gestire i JWT

    //riceve il token come parametro
    private AppUser authenticateUserFromToken(String token) {
        System.out.println("📌 Token ricevuto per autenticazione: " + token);
     //verifico il token
        if (token == null || token.isEmpty()) {
            //se il token è null, stampo errore e restituisco null
            System.err.println("❌ Errore: token JWT mancante!");
            return null;
        }
        //Estraggo  l'username dal token
        String username = jwtUtils.getUsernameFromToken(token);
        if (username == null) {
            //se username è null, stampo errore e restituisco null
            System.err.println("❌ Errore: impossibile estrarre username dal token!");
            return null;
        }
        //Cerco l'utente nel database tramite il suo username.
        return appUserRepository.findByUsername(username).orElse(null);
    }

    @PostMapping("/create-payment")
    public ResponseEntity<String> createPayment(
            @AuthenticationPrincipal AppUser user,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

       //Controllo se l'utente è autenticato.
        if (user == null) {
            System.err.println("❌ Errore: utente non autenticato!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Errore: Devi essere autenticato per effettuare un pagamento.");
        }
        // controllo se l'header Authorization contiene un JWT valido.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("❌ Errore: header Authorization mancante o malformato!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Errore: Token JWT mancante o malformato.");
        }

        // Estraggo il token senza "Bearer "
        String jwtToken = authHeader.replace("Bearer ", "").trim();
        System.out.println("📌 Token JWT ricevuto nel pagamento: " + jwtToken);

        try {
            System.out.println("✅ Creazione pagamento per utente: " + user.getUsername());
            //richiamo il metodo per creare un pagamento
            String paymentUrl = paymentSvc.createPayment(50.00, jwtToken);
            //se va a buon fine, restituisco la risposta ok
            return ResponseEntity.ok(paymentUrl);
        } catch (PayPalRESTException e) {
            System.err.println("❌ Errore nel pagamento: " + e.getMessage());
            //diversamente restituisco una badrequest
            return ResponseEntity.badRequest().body("Errore nel pagamento");
        }
    }



    @GetMapping("/execute-payment")
    public ResponseEntity<Void> executePayment(
            @RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId,
            @RequestParam(name = "jwtToken", required = false) String jwtToken,
            @RequestParam(name = "paypalToken", required = false) String paypalToken) {

        System.out.println("🔍 Token JWT ricevuto: " + jwtToken);
        System.out.println("🔍 Token PayPal ricevuto: " + paypalToken);

        //controllo che jwt non sia vuoto
        if (jwtToken == null || jwtToken.isEmpty()) {
            System.err.println("❌ Errore: il token JWT è mancante!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //faccio autenticare l'user dal token jwt
        AppUser user = authenticateUserFromToken(jwtToken);
        if (user == null) {
            System.err.println("❌ Errore: token JWT non valido!");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            //verifico il pagamento
            boolean paymentExecuted = paymentSvc.executePayment(paymentId, payerId);

            //Controllo se il pagamento va a buon fine, se sì:
            if (paymentExecuted) {
                user.setHasPaid(true);
                //aggiorno lo stato dell'utente nel database
                appUserRepository.save(user);
                System.out.println("✅ Pagamento confermato per utente: " + user.getUsername());
                //reindirizzo alla pagina di conferma.
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header("Location", "http://localhost:4200/videoclasses?paymentSuccess=true")
                        .build();
            } else {
                //diversamente invio una bad request
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            System.err.println("❌ Errore durante l'esecuzione del pagamento: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

  // Verifico se un utente ha già pagato.
    @GetMapping("/hasPaid")
        public ResponseEntity<Boolean> hasUserPaid(@AuthenticationPrincipal AppUser user) {
            if (user == null) {
                System.err.println("❌ Errore: utente non autenticato!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            System.out.println("✅ User " + user.getUsername() + " hasPaid status: " + user.isHasPaid());
            return ResponseEntity.ok(user.isHasPaid());
        }
    }
