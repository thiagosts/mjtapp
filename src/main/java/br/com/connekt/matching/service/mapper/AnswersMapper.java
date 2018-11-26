package br.com.connekt.matching.service.mapper;

import br.com.connekt.matching.domain.*;
import br.com.connekt.matching.service.dto.AnswersDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Answers and its DTO AnswersDTO.
 */
@Mapper(componentModel = "spring", uses = {QuestionsMapper.class})
public interface AnswersMapper extends EntityMapper<AnswersDTO, Answers> {

    @Mapping(source = "questions.id", target = "questionsId")
    AnswersDTO toDto(Answers answers);

    @Mapping(source = "questionsId", target = "questions")
    Answers toEntity(AnswersDTO answersDTO);

    default Answers fromId(Long id) {
        if (id == null) {
            return null;
        }
        Answers answers = new Answers();
        answers.setId(id);
        return answers;
    }
}
