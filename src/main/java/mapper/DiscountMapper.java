package mapper;

import dto.DiscountDTO;
import entity.Discount;

public class DiscountMapper {

    public static DiscountDTO toDTO(Discount discount) {
        if (discount == null) {
            return null;
        }
        DiscountDTO dto = new DiscountDTO();
        dto.setId(discount.getId());
        dto.setCode(discount.getCode());
        dto.setPercentage(discount.getPercentage());
        dto.setDescription(discount.getDescription());
        dto.setExpiresAt(discount.getExpiresAt());
        return dto;
    }

    public static Discount toEntity(DiscountDTO dto) {
        if (dto == null) {
            return null;
        }
        Discount discount = new Discount();
        discount.setId(dto.getId());
        discount.setCode(dto.getCode());
        discount.setPercentage(dto.getPercentage());
        discount.setDescription(dto.getDescription());
        discount.setExpiresAt(dto.getExpiresAt());
        return discount;
    }
}