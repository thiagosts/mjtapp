package br.com.connekt.matching.service;

import br.com.connekt.matching.service.dto.MatchingsJobDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Service Interface for managing MatchingsJob.
 */
public interface MatchingsJobService {

    /**
     * Save a matchingsJob.
     *
     * @param matchingsJobDTO the entity to save
     * @return the persisted entity
     */
    MatchingsJobDTO save(MatchingsJobDTO matchingsJobDTO);

    /**
     * Get all the matchingsJobs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<MatchingsJobDTO> findAll(Pageable pageable);


    /**
     * Get the "id" matchingsJob.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<MatchingsJobDTO> findOne(Long id);

    /**
     * Delete the "id" matchingsJob.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
}
