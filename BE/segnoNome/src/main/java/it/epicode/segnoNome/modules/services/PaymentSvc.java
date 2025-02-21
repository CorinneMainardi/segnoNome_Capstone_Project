package it.epicode.segnoNome.modules.services;

import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import it.epicode.segnoNome.auth.entities.AppUser;
import it.epicode.segnoNome.auth.repositories.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentSvc {


    @Autowired
    private AppUserRepository appUserRepository;

    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;
//createpayment: Riceve l'importo (total) e il token JWT dell'utente (jwtToken).

    public String createPayment(Double total, String jwtToken) throws PayPalRESTException {
        System.out.println("🔵 Creazione pagamento per utente autenticato...");
        System.out.println("🔵 Token JWT ricevuto: " + jwtToken);
//Crea un'istanza di APIContext, usando le credenziali di PayPal.
        APIContext apiContext = new APIContext(clientId, clientSecret, mode);
        //imposto l'importo
        Amount amount = new Amount();
        //imposto la valuta
        amount.setCurrency("EUR");
        //formatto a 2 decimali
        amount.setTotal(String.format(java.util.Locale.US, "%.2f", total));

        //imposto la transizione con importo e descrizione
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription("Pagamento per il corso di segni");
        //aggiungo la transazione alla lista perché paypal può gestire più transazioni contemporaneamnente
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        //creo un payer e imposto il metodo di pagamento
        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");
        // creo un pagamento e imposto:l'intento il payer e le trasazioni
        Payment payment = new Payment();
        payment.setIntent("sale");
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        // aggiungo il token JWT nell'URL di ritorno
        RedirectUrls redirectUrls = new RedirectUrls();
        //imposto l'url in caso di pagamento fallito
        redirectUrls.setCancelUrl("http://localhost:4200/payment-failed");
        //imposto l'url in caso di pagamento andato a buon fine. cambia ogni volta che scadengrok
        redirectUrls.setReturnUrl("https://117d-151-40-4-202.ngrok-free.app/api/payments/execute-payment?jwtToken=" + jwtToken);



        payment.setRedirectUrls(redirectUrls);
        //invio il pagamento a paypal che mi restituisce l'oggetto createdPayment
        Payment createdPayment = payment.create(apiContext);
        // link di approvazione dove l'utente deve completare il pagamento
        String approvalUrl = createdPayment.getLinks().stream()
                .filter(link -> link.getRel().equalsIgnoreCase("approval_url"))
                .findFirst()
                .get()
                .getHref();

        System.out.println("✅ Payment created. Approval URL: " + approvalUrl);
        //restiusce url così il fe sa dove deve andare
        return approvalUrl;
    }

    //riceve l'id payment e payer
    public boolean executePayment(String paymentId, String payerId) throws PayPalRESTException {
        APIContext apiContext = new APIContext(clientId, clientSecret, mode);
    //crea un payment
        Payment payment = new Payment();
        payment.setId(paymentId);
    //creo un paymentExcution per completare il pagamento
        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        try {
            //eseguo il pagamento su paypal
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            System.out.println("✅ Payment executed successfully! ID: " + executedPayment.getId());

            //  Verifico se lo stato del pagamento sia "approved"
            if ("approved".equalsIgnoreCase(executedPayment.getState())) {
                System.out.println("✅ Il pagamento è stato approvato!");
                return true;
            } else {
                System.err.println("❌ Il pagamento NON è stato approvato! Stato: " + executedPayment.getState());
                return false;
            }
        } catch (PayPalRESTException e) {
            System.err.println("❌ Errore durante l'esecuzione del pagamento: " + e.getMessage());
            return false;
        }
    }


    public void setUserHasPaid(AppUser user) {
        if (user == null) {
            System.err.println("❌ Errore: utente non autenticato!");
            return;
        }

        // Recupera l'utente dal database
        System.out.println("🔍 Tentativo di recupero utente con ID: " + user.getId());
        AppUser userFromDb = appUserRepository.findById(user.getId()).orElse(null);

        if (userFromDb == null) {
            System.err.println("❌ Errore: utente non trovato nel database con ID: " + user.getId());
            return;
        }

        System.out.println("✅ Utente trovato nel database: " + userFromDb.getUsername());
        System.out.println("🔹 Stato attuale hasPaid: " + userFromDb.isHasPaid());

        // Aggiorna lo stato di pagamento
        userFromDb.setHasPaid(true);

        // Salvataggio nel database
        appUserRepository.save(userFromDb);
        System.out.println("✅ Pagamento registrato per: " + userFromDb.getUsername());
        System.out.println("🔹 Nuovo stato hasPaid: " + userFromDb.isHasPaid());

        // Verifica salvataggio
        AppUser checkUser = appUserRepository.findById(user.getId()).orElse(null);
        if (checkUser != null) {
            System.out.println("🔍 Verifica dopo il salvataggio -> hasPaid: " + checkUser.isHasPaid());
        } else {
            System.err.println("❌ Errore: impossibile verificare il salvataggio dell'utente.");
        }
    }



}