/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author ajlan
 */
@Stateless
@Path("users")
public class UserFacadeREST extends AbstractFacade<User> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public UserFacadeREST() {
        super(User.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(User entity) {
        try {
            // Hachage du mot de passe avant sauvegarde
            if (entity.getPassword() != null) {
                String hashed = BCrypt.hashpw(entity.getPassword(), BCrypt.gensalt());
                entity.setPassword(hashed);
            }
            super.create(entity);
        } catch (ConstraintViolationException e) {
            e.getConstraintViolations().forEach(violation -> {
                System.out.println(" ERREUR VALIDATION : "
                        + violation.getPropertyPath() + " " + violation.getMessage());
            });
            throw e;
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
//public void edit(@PathParam("id") Integer id, User entity) {
    public User edit(@PathParam("id") Integer id, User entity) {
        User existingUser = super.find(id);

        if (existingUser == null) {
//            return;
            return null;
        }

        // Mise à jour partielle (On ne touche qu'aux champs envoyés)
        if (entity.getFirstName() != null) {
            existingUser.setFirstName(entity.getFirstName());
        }
        if (entity.getLastName() != null) {
            existingUser.setLastName(entity.getLastName());
        }
        if (entity.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(entity.getPhoneNumber());
        }
        if (entity.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(entity.getDateOfBirth());
        }
        if (entity.getBio() != null) {
            existingUser.setBio(entity.getBio());
        }
        if (entity.getNationalId() != null) {
            existingUser.setNationalId(entity.getNationalId());
        }

        // Ajout : Mise à jour du mot de passe
        if (entity.getPassword() != null && !entity.getPassword().isEmpty()) {
            existingUser.setPassword(entity.getPassword());
        }
        //avatar profil
        if (entity.getAvatar() != null) {
            existingUser.setAvatar(entity.getAvatar());
        }

        super.edit(existingUser);
        return existingUser;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public User find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<User> findAll() {
        // Grâce au @JsonbTransient dans User.java, ceci ne fera pas de boucle infinie
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<User> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
