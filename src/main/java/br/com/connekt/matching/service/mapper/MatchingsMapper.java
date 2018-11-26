package br.com.connekt.matching.service.mapper;

import br.com.connekt.matching.domain.*;
import br.com.connekt.matching.service.dto.MatchingsDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Matchings and its DTO MatchingsDTO.
 */
@Mapper(componentModel = "spring", uses = {MatchingsJobMapper.class, CustomizationMapper.class})
public interface MatchingsMapper extends EntityMapper<MatchingsDTO, Matchings> {

    @Mapping(source = "matchingsJob.id", target = "matchingsJobId")
    @Mapping(source = "customization.id", target = "customizationId")
    MatchingsDTO toDto(Matchings matchings);

    @Mapping(source = "matchingsJobId", target = "matchingsJob")
    @Mapping(source = "customizationId", target = "customization")
    @Mapping(target = "questions", ignore = true)
    Matchings toEntity(MatchingsDTO matchingsDTO);

    default Matchings fromId(Long id) {
        if (id == null) {
            return null;
        }
        Matchings matchings = new Matchings();
        matchings.setId(id);
        return matchings;
    }
}
