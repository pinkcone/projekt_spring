package com.pollub.cookie.mapper;

import com.pollub.cookie.dto.DiscountCodeDTO;
import com.pollub.cookie.model.DiscountCode;
import com.pollub.cookie.model.DiscountType;
import org.springframework.stereotype.Service;

@Service
public class DiscountCodeMapper {
    public DiscountCode mapToEntity(DiscountCodeDTO discountCodeDTO) {
        DiscountCode discountCode = new DiscountCode();
        discountCode.setCode(discountCodeDTO.getCode());
        discountCode.setType(mapTypeStringToEnum(discountCodeDTO.getType()));
        discountCode.setValue(discountCodeDTO.getValue());
        discountCode.setExpirationDate(discountCodeDTO.getExpirationDate());
        return discountCode;
    }

    public DiscountCodeDTO mapToDTO(DiscountCode discountCode) {
        DiscountCodeDTO discountCodeDTO = new DiscountCodeDTO();
        discountCodeDTO.setId(discountCode.getId());
        discountCodeDTO.setCode(discountCode.getCode());
        discountCodeDTO.setType(discountCode.getType().name());
        discountCodeDTO.setValue(discountCode.getValue());
        discountCodeDTO.setExpirationDate(discountCode.getExpirationDate());
        return discountCodeDTO;
    }

    public DiscountType mapTypeStringToEnum(String typString) {
        try {
            return DiscountType.valueOf(typString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Nieprawidłowy typ rabatu: " + typString);
        }
    }
}
