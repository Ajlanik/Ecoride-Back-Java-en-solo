/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

/**
 *
 * @author ajlan
 */
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;

@ApplicationScoped
public class StripeService {

    // Créer une intention de paiement (appelé AVANT de sauvegarder la réservation)
    public PaymentIntent createPaymentIntent(BigDecimal amount, String currency) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount.multiply(new BigDecimal(100)).longValue()) // Stripe est en centimes
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .build()
                )
                .build();

        return PaymentIntent.create(params);
    }

    // Rembourser un paiement (appelé lors d'un REJECT ou CANCEL)
    public Refund refundPayment(String paymentIntentId) throws StripeException {
        if (paymentIntentId == null || paymentIntentId.isEmpty()) {
            return null;
        }
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(paymentIntentId)
                .build();

        return Refund.create(params);
    }
}
