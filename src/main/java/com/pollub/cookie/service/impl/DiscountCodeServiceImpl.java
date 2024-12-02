package com.pollub.cookie.service.impl;

import com.pollub.cookie.dto.DiscountCodeDTO;
import com.pollub.cookie.exception.DiscountCodeAlreadyExistsException;
import com.pollub.cookie.exception.ResourceNotFoundException;
import com.pollub.cookie.mapper.DiscountCodeMapper;
import com.pollub.cookie.model.DiscountCode;
import com.pollub.cookie.repository.DiscountCodeRepository;
import com.pollub.cookie.service.DiscountCodeService;
import jakarta.xml.bind.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class DiscountCodeServiceImpl implements DiscountCodeService {

    private final DiscountCodeRepository discountCodeRepository;
    private final DiscountCodeMapper discountCodeMapper;
    private static final Logger logger = LoggerFactory.getLogger(DiscountCodeServiceImpl.class);

    @Autowired
    public DiscountCodeServiceImpl(DiscountCodeRepository discountCodeRepository, DiscountCodeMapper discountCodeMapper) {
        this.discountCodeRepository = discountCodeRepository;
        this.discountCodeMapper = discountCodeMapper;
    }

    @Override
    @Transactional
    public DiscountCodeDTO createDiscountCode(DiscountCodeDTO discountCodeDTO) throws ValidationException {
        if (discountCodeRepository.findByCode(discountCodeDTO.getCode()).isPresent()) {
            logger.error("Kod rabatowy o kodzie '{}' już istnieje.", discountCodeDTO.getCode());
            throw new ValidationException("Kod rabatowy o kodzie '" + discountCodeDTO.getCode() + "' już istnieje.");
        }
        if (!validateDiscountCodeValue(discountCodeDTO.getType(), discountCodeDTO.getValue())) {
            logger.error("Nieprawidłowa wartość kodu rabatowego: typ '{}', wartość '{}'", discountCodeDTO.getType(), discountCodeDTO.getValue());
            throw new ValidationException("Wartość rabatu procentowego nie może być większa niż 100%");
        }
        DiscountCode discountCode = discountCodeMapper.mapToEntity(discountCodeDTO);
        DiscountCode savedDiscountCode = discountCodeRepository.save(discountCode);

        return discountCodeMapper.mapToDTO(savedDiscountCode);
    }


    @Override
    @Transactional(readOnly = true)
    public DiscountCodeDTO getDiscountCodeById(Long id) {
        DiscountCode discountCode = discountCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kod rabatowy nie znaleziony o ID: " + id));
        return discountCodeMapper.mapToDTO(discountCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountCodeDTO> getAllDiscountCodes() {
        List<DiscountCode> discountCodes = discountCodeRepository.findAll();
        return discountCodes.stream()
                .map(discountCodeMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DiscountCodeDTO updateDiscountCode(Long id, DiscountCodeDTO discountCodeDTO) {
        DiscountCode existingDiscountCode = discountCodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Kod rabatowy nie znaleziony o ID: " + id));

        if (!existingDiscountCode.getCode().equals(discountCodeDTO.getCode())) {
            if (discountCodeRepository.findByCode(discountCodeDTO.getCode()).isPresent()) {
                throw new DiscountCodeAlreadyExistsException("Kod rabatowy o wartości '" + discountCodeDTO.getCode() + "' już istnieje.");
            }
            existingDiscountCode.setCode(discountCodeDTO.getCode());
        }

        existingDiscountCode.setType(discountCodeMapper.mapTypeStringToEnum(discountCodeDTO.getType()));
        existingDiscountCode.setValue(discountCodeDTO.getValue());
        existingDiscountCode.setExpirationDate(discountCodeDTO.getExpirationDate());

        DiscountCode updatedDiscountCode = discountCodeRepository.save(existingDiscountCode);

        return discountCodeMapper.mapToDTO(updatedDiscountCode);
    }

    @Override
    @Transactional
    public void deleteDiscountCode(Long id) {
        if (!discountCodeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Kod rabatowy nie znaleziony o ID: " + id);
        }
        discountCodeRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountCodeDTO getDiscountCodeByCode(String code) {
        DiscountCode discountCode = discountCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Kod rabatowy nie znaleziony: " + code));

        if (discountCode.getExpirationDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Kod rabatowy wygasł.");
        }
        return discountCodeMapper.mapToDTO(discountCode);
    }
    private boolean validateDiscountCodeValue(String type, double value){
        return !Objects.equals(type, "PERCENTAGE") || !(value > 100);
    }
}

