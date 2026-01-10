/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package config; //

/**
 *
 * @author ajlan
 */


import com.stripe.Stripe;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

@Singleton
@Startup
public class StripeConfig {

    @PostConstruct
    public void init() {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            // Initialisation globale de Stripe
            Stripe.apiKey = prop.getProperty("stripe.key"); // Assure-toi que la clé s'appelle 'stripe.key' dans ton fichier
            
        } catch (IOException ex) {
            Logger.getLogger(StripeConfig.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}