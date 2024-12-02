package com.pollub.cookie.service;

import com.pollub.cookie.dto.DiscountCodeDTO;
import jakarta.xml.bind.ValidationException;

import java.util.List;

public interface DiscountCodeService {

    DiscountCodeDTO createDiscountCode(DiscountCodeDTO discountCodeDTO) throws ValidationException;

    DiscountCodeDTO getDiscountCodeById(Long id);

    List<DiscountCodeDTO> getAllDiscountCodes();

    DiscountCodeDTO updateDiscountCode(Long id, DiscountCodeDTO discountCodeDTO);

    void deleteDiscountCode(Long id);

    DiscountCodeDTO getDiscountCodeByCode(String kod);
}
