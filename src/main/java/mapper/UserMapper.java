package mapper;

import dto.UserDTO;
import entity.User;

/**
 * Classe utilitaire pour convertir les entités User en DTO et inversement.
 */
public class UserMapper {

    /**
     * Convertit une entité User vers un UserDTO.
     */
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBio(user.getBio());
        dto.setNationalId(user.getNationalId());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setAvatar(user.getAvatar());
        dto.setCredits(user.getCredits());
        dto.setRating(user.getRating());
        return dto;
    }

    /**
     * Convertit un UserDTO vers une entité User.
     * Note : Le mot de passe et les rôles ne sont pas gérés ici car ils 
     * nécessitent un traitement spécifique lors de la création/modification.
     */
    public static User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setBio(dto.getBio());
        user.setNationalId(dto.getNationalId());
        user.setDateOfBirth(dto.getDateOfBirth());
        user.setAvatar(dto.getAvatar());
        user.setCredits(dto.getCredits());
        user.setRating(dto.getRating());
        return user;
    }
}