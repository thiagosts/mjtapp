package br.com.connekt.matching.service;

import br.com.connekt.matching.service.dto.AnswersDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing Answers.
 */
public interface AnswersService {

    /**
     * Save a answers.
     *
     * @param answersDTO the entity to save
     * @return the persisted entity
     */
    AnswersDTO save(AnswersDTO answersDTO);

    /**
     * Get all the answers.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<AnswersDTO> findAll(Pageable pageable);


    /**
     * Get the "id" answers.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<AnswersDTO> findOne(Long id);

    /**
     * Delete the "id" answers.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
