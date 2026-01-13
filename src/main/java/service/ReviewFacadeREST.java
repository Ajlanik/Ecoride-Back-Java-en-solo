package service;

import dto.ReviewDTO;
import entity.Review;
import entity.User;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import mapper.ReviewMapper;

@Stateless
@Path("reviews")
public class ReviewFacadeREST extends AbstractFacade<Review> {

    @PersistenceContext(unitName = "my_persistence_unit")
    private EntityManager em;

    public ReviewFacadeREST() {
        super(Review.class);
    }

    @POST
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public ReviewDTO createAndReturn(ReviewDTO dto) { // 👈 On accepte le DTO

        // Conversion DTO -> Entity (Le Mapper gère les IDs)
        Review entity = ReviewMapper.toEntity(dto);

        // Gestion de la date si absente
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(new Date());
        }

        // Persistance
        super.create(entity);

        updateUserRating(entity.getTargetId());

        // Retourne le DTO (avec l'ID)
        return ReviewMapper.toDTO(entity);
    }

    private void updateUserRating(User targetUser) {
        if (targetUser == null) {
            return;
        }

        try {
            // Calculer la moyenne via SQL (c'est très rapide)
            Double average = em.createQuery("SELECT AVG(r.rating) FROM Review r WHERE r.targetId = :user", Double.class)
                    .setParameter("user", targetUser)
                    .getSingleResult();

            // Gérer le cas null (pas d'avis, peu probable ici car on vient d'en ajouter un)
            if (average == null) {
                average = 5.0;
            }

            // Arrondir à 1 décimale
            double roundedRating = Math.round(average * 10.0) / 10.0;

            // Mettre à jour l'utilisateur

            User userToUpdate = em.find(User.class, targetUser.getId());
            userToUpdate.setRating(roundedRating);

            // Sauvegarder
            em.merge(userToUpdate);

            System.out.println("Note mise à jour pour " + userToUpdate.getEmail() + " : " + roundedRating);

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du rating : " + e.getMessage());
            e.printStackTrace();
        }
    }


    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Produces(MediaType.APPLICATION_JSON)
    public ReviewDTO edit(@PathParam("id") Integer id, Review entity) {
        super.edit(entity);
        return ReviewMapper.toDTO(super.find(id));
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public ReviewDTO findDTO(@PathParam("id") Integer id) {
        return ReviewMapper.toDTO(super.find(id));
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<ReviewDTO> findAllDTO() {
        return super.findAll().stream()
                .map(ReviewMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
