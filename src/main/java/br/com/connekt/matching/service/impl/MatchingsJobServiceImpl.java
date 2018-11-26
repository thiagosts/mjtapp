package br.com.connekt.matching.service.impl;

import br.com.connekt.matching.service.MatchingsJobService;
import br.com.connekt.matching.domain.MatchingsJob;
import br.com.connekt.matching.repository.MatchingsJobRepository;
import br.com.connekt.matching.service.dto.MatchingsJobDTO;
import br.com.connekt.matching.service.mapper.MatchingsJobMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing MatchingsJob.
 */
@Service
@Transactional
public class MatchingsJobServiceImpl implements MatchingsJobService {

    private final Logger log = LoggerFactory.getLogger(MatchingsJobServiceImpl.class);

    private final MatchingsJobRepository matchingsJobRepository;

    private final MatchingsJobMapper matchingsJobMapper;

    public MatchingsJobServiceImpl(MatchingsJobRepository matchingsJobRepository, MatchingsJobMapper matchingsJobMapper) {
        this.matchingsJobRepository = matchingsJobRepository;
        this.matchingsJobMapper = matchingsJobMapper;
    }

    /**
     * Save a matchingsJob.
     *
     * @param matchingsJobDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public MatchingsJobDTO save(MatchingsJobDTO matchingsJobDTO) {
        log.debug("Request to save MatchingsJob : {}", matchingsJobDTO);

        MatchingsJob matchingsJob = matchingsJobMapper.toEntity(matchingsJobDTO);
        matchingsJob = matchingsJobRepository.save(matchingsJob);
        return matchingsJobMapper.toDto(matchingsJob);
    }

    /**
     * Get all the matchingsJobs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MatchingsJobDTO> findAll(Pageable pageable) {
        log.debug("Request to get all MatchingsJobs");
        return matchingsJobRepository.findAll(pageable)
            .map(matchingsJobMapper::toDto);
    }


    /**
     * Get one matchingsJob by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<MatchingsJobDTO> findOne(Long id) {
        log.debug("Request to get MatchingsJob : {}", id);
        return matchingsJobRepository.findById(id)
            .map(matchingsJobMapper::toDto);
    }

    /**
     * Delete the matchingsJob by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete MatchingsJob : {}", id);
        matchingsJobRepository.deleteById(id);
    }
}
