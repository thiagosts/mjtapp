package br.com.connekt.matching.service;

import br.com.connekt.matching.service.dto.CustomizationDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Customization.
 */
public interface CustomizationService {

    /**
     * Save a customization.
     *
     * @param customizationDTO the entity to save
     * @return the persisted entity
     */
    CustomizationDTO save(CustomizationDTO customizationDTO);

    /**
     * Get all the customizations.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<CustomizationDTO> findAll(Pageable pageable);


    /**
     * Get the "id" customization.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<CustomizationDTO> findOne(Long id);

    /**
     * Delete the "id" customization.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
