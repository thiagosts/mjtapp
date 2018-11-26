package br.com.connekt.matching.service.impl;

import br.com.connekt.matching.service.MatchingsService;
import br.com.connekt.matching.domain.Matchings;
import br.com.connekt.matching.repository.MatchingsRepository;
import br.com.connekt.matching.service.dto.MatchingsDTO;
import br.com.connekt.matching.service.mapper.MatchingsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing Matchings.
 */
@Service
@Transactional
public class MatchingsServiceImpl implements MatchingsService {

    private final Logger log = LoggerFactory.getLogger(MatchingsServiceImpl.class);

    private final MatchingsRepository matchingsRepository;

    private final MatchingsMapper matchingsMapper;

    public MatchingsServiceImpl(MatchingsRepository matchingsRepository, MatchingsMapper matchingsMapper) {
        this.matchingsRepository = matchingsRepository;
        this.matchingsMapper = matchingsMapper;
    }

    /**
     * Save a matchings.
     *
     * @param matchingsDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public MatchingsDTO save(MatchingsDTO matchingsDTO) {
        log.debug("Request to save Matchings : {}", matchingsDTO);

        Matchings matchings = matchingsMapper.toEntity(matchingsDTO);
        matchings = matchingsRepository.save(matchings);
        return matchingsMapper.toDto(matchings);
    }

    /**
     * Get all the matchings.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MatchingsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Matchings");
        return matchingsRepository.findAll(pageable)
            .map(matchingsMapper::toDto);
    }


    /**
     * Get one matchings by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<MatchingsDTO> findOne(Long id) {
        log.debug("Request to get Matchings : {}", id);
        return matchingsRepository.findById(id)
            .map(matchingsMapper::toDto);
    }

    /**
     * Delete the matchings by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Matchings : {}", id);
        matchingsRepository.deleteById(id);
    }
}
