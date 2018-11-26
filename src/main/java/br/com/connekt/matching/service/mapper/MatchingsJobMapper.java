package br.com.connekt.matching.service.mapper;

import br.com.connekt.matching.domain.*;
import br.com.connekt.matching.service.dto.MatchingsJobDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity MatchingsJob and its DTO MatchingsJobDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface MatchingsJobMapper extends EntityMapper<MatchingsJobDTO, MatchingsJob> {


    @Mapping(target = "matchings", ignore = true)
    MatchingsJob toEntity(MatchingsJobDTO matchingsJobDTO);

    default MatchingsJob fromId(Long id) {
        if (id == null) {
            return null;
        }
        MatchingsJob matchingsJob = new MatchingsJob();
        matchingsJob.setId(id);
        return matchingsJob;
    }
}
