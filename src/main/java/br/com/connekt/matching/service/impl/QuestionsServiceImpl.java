package br.com.connekt.matching.service.impl;

import br.com.connekt.matching.service.QuestionsService;
import br.com.connekt.matching.domain.Questions;
import br.com.connekt.matching.repository.QuestionsRepository;
import br.com.connekt.matching.service.dto.QuestionsDTO;
import br.com.connekt.matching.service.mapper.QuestionsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing Questions.
 */
@Service
@Transactional
public class QuestionsServiceImpl implements QuestionsService {

    private final Logger log = LoggerFactory.getLogger(QuestionsServiceImpl.class);

    private final QuestionsRepository questionsRepository;

    private final QuestionsMapper questionsMapper;

    public QuestionsServiceImpl(QuestionsRepository questionsRepository, QuestionsMapper questionsMapper) {
        this.questionsRepository = questionsRepository;
        this.questionsMapper = questionsMapper;
    }

    /**
     * Save a questions.
     *
     * @param questionsDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public QuestionsDTO save(QuestionsDTO questionsDTO) {
        log.debug("Request to save Questions : {}", questionsDTO);

        Questions questions = questionsMapper.toEntity(questionsDTO);
        questions = questionsRepository.save(questions);
        return questionsMapper.toDto(questions);
    }

    /**
     * Get all the questions.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<QuestionsDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Questions");
        return questionsRepository.findAll(pageable)
            .map(questionsMapper::toDto);
    }


    /**
     * Get one questions by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<QuestionsDTO> findOne(Long id) {
        log.debug("Request to get Questions : {}", id);
        return questionsRepository.findById(id)
            .map(questionsMapper::toDto);
    }

    /**
     * Delete the questions by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Questions : {}", id);
        questionsRepository.deleteById(id);
    }
}
