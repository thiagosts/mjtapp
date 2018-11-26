package br.com.connekt.matching.web.rest;

import br.com.connekt.matching.MatchingJobApp;

import br.com.connekt.matching.domain.MatchingsJob;
import br.com.connekt.matching.repository.MatchingsJobRepository;
import br.com.connekt.matching.service.MatchingsJobService;
import br.com.connekt.matching.service.dto.MatchingsJobDTO;
import br.com.connekt.matching.service.mapper.MatchingsJobMapper;
import br.com.connekt.matching.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


import static br.com.connekt.matching.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the MatchingsJobResource REST controller.
 *
 * @see MatchingsJobResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MatchingJobApp.class)
public class MatchingsJobResourceIntTest {

    private static final Float DEFAULT_CUT_NOTE = 1F;
    private static final Float UPDATED_CUT_NOTE = 2F;

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;

    private static final String DEFAULT_REQUIRE = "AAAAAAAAAA";
    private static final String UPDATED_REQUIRE = "BBBBBBBBBB";

    private static final String DEFAULT_OPPORTUNITIES_ID = "AAAAAAAAAA";
    private static final String UPDATED_OPPORTUNITIES_ID = "BBBBBBBBBB";

    @Autowired
    private MatchingsJobRepository matchingsJobRepository;

    @Autowired
    private MatchingsJobMapper matchingsJobMapper;

    @Autowired
    private MatchingsJobService matchingsJobService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMatchingsJobMockMvc;

    private MatchingsJob matchingsJob;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MatchingsJobResource matchingsJobResource = new MatchingsJobResource(matchingsJobService);
        this.restMatchingsJobMockMvc = MockMvcBuilders.standaloneSetup(matchingsJobResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MatchingsJob createEntity(EntityManager em) {
        MatchingsJob matchingsJob = new MatchingsJob()
            .cutNote(DEFAULT_CUT_NOTE)
            .order(DEFAULT_ORDER)
            .require(DEFAULT_REQUIRE)
            .opportunitiesId(DEFAULT_OPPORTUNITIES_ID);
        return matchingsJob;
    }

    @Before
    public void initTest() {
        matchingsJob = createEntity(em);
    }

    @Test
    @Transactional
    public void createMatchingsJob() throws Exception {
        int databaseSizeBeforeCreate = matchingsJobRepository.findAll().size();

        // Create the MatchingsJob
        MatchingsJobDTO matchingsJobDTO = matchingsJobMapper.toDto(matchingsJob);
        restMatchingsJobMockMvc.perform(post("/api/matchings-jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchingsJobDTO)))
            .andExpect(status().isCreated());

        // Validate the MatchingsJob in the database
        List<MatchingsJob> matchingsJobList = matchingsJobRepository.findAll();
        assertThat(matchingsJobList).hasSize(databaseSizeBeforeCreate + 1);
        MatchingsJob testMatchingsJob = matchingsJobList.get(matchingsJobList.size() - 1);
        assertThat(testMatchingsJob.getCutNote()).isEqualTo(DEFAULT_CUT_NOTE);
        assertThat(testMatchingsJob.getOrder()).isEqualTo(DEFAULT_ORDER);
        assertThat(testMatchingsJob.getRequire()).isEqualTo(DEFAULT_REQUIRE);
        assertThat(testMatchingsJob.getOpportunitiesId()).isEqualTo(DEFAULT_OPPORTUNITIES_ID);
    }

    @Test
    @Transactional
    public void createMatchingsJobWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = matchingsJobRepository.findAll().size();

        // Create the MatchingsJob with an existing ID
        matchingsJob.setId(1L);
        MatchingsJobDTO matchingsJobDTO = matchingsJobMapper.toDto(matchingsJob);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMatchingsJobMockMvc.perform(post("/api/matchings-jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchingsJobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MatchingsJob in the database
        List<MatchingsJob> matchingsJobList = matchingsJobRepository.findAll();
        assertThat(matchingsJobList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMatchingsJobs() throws Exception {
        // Initialize the database
        matchingsJobRepository.saveAndFlush(matchingsJob);

        // Get all the matchingsJobList
        restMatchingsJobMockMvc.perform(get("/api/matchings-jobs?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(matchingsJob.getId().intValue())))
            .andExpect(jsonPath("$.[*].cutNote").value(hasItem(DEFAULT_CUT_NOTE.doubleValue())))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)))
            .andExpect(jsonPath("$.[*].require").value(hasItem(DEFAULT_REQUIRE.toString())))
            .andExpect(jsonPath("$.[*].opportunitiesId").value(hasItem(DEFAULT_OPPORTUNITIES_ID.toString())));
    }
    
    @Test
    @Transactional
    public void getMatchingsJob() throws Exception {
        // Initialize the database
        matchingsJobRepository.saveAndFlush(matchingsJob);

        // Get the matchingsJob
        restMatchingsJobMockMvc.perform(get("/api/matchings-jobs/{id}", matchingsJob.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(matchingsJob.getId().intValue()))
            .andExpect(jsonPath("$.cutNote").value(DEFAULT_CUT_NOTE.doubleValue()))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER))
            .andExpect(jsonPath("$.require").value(DEFAULT_REQUIRE.toString()))
            .andExpect(jsonPath("$.opportunitiesId").value(DEFAULT_OPPORTUNITIES_ID.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingMatchingsJob() throws Exception {
        // Get the matchingsJob
        restMatchingsJobMockMvc.perform(get("/api/matchings-jobs/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMatchingsJob() throws Exception {
        // Initialize the database
        matchingsJobRepository.saveAndFlush(matchingsJob);

        int databaseSizeBeforeUpdate = matchingsJobRepository.findAll().size();

        // Update the matchingsJob
        MatchingsJob updatedMatchingsJob = matchingsJobRepository.findById(matchingsJob.getId()).get();
        // Disconnect from session so that the updates on updatedMatchingsJob are not directly saved in db
        em.detach(updatedMatchingsJob);
        updatedMatchingsJob
            .cutNote(UPDATED_CUT_NOTE)
            .order(UPDATED_ORDER)
            .require(UPDATED_REQUIRE)
            .opportunitiesId(UPDATED_OPPORTUNITIES_ID);
        MatchingsJobDTO matchingsJobDTO = matchingsJobMapper.toDto(updatedMatchingsJob);

        restMatchingsJobMockMvc.perform(put("/api/matchings-jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchingsJobDTO)))
            .andExpect(status().isOk());

        // Validate the MatchingsJob in the database
        List<MatchingsJob> matchingsJobList = matchingsJobRepository.findAll();
        assertThat(matchingsJobList).hasSize(databaseSizeBeforeUpdate);
        MatchingsJob testMatchingsJob = matchingsJobList.get(matchingsJobList.size() - 1);
        assertThat(testMatchingsJob.getCutNote()).isEqualTo(UPDATED_CUT_NOTE);
        assertThat(testMatchingsJob.getOrder()).isEqualTo(UPDATED_ORDER);
        assertThat(testMatchingsJob.getRequire()).isEqualTo(UPDATED_REQUIRE);
        assertThat(testMatchingsJob.getOpportunitiesId()).isEqualTo(UPDATED_OPPORTUNITIES_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingMatchingsJob() throws Exception {
        int databaseSizeBeforeUpdate = matchingsJobRepository.findAll().size();

        // Create the MatchingsJob
        MatchingsJobDTO matchingsJobDTO = matchingsJobMapper.toDto(matchingsJob);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMatchingsJobMockMvc.perform(put("/api/matchings-jobs")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(matchingsJobDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MatchingsJob in the database
        List<MatchingsJob> matchingsJobList = matchingsJobRepository.findAll();
        assertThat(matchingsJobList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteMatchingsJob() throws Exception {
        // Initialize the database
        matchingsJobRepository.saveAndFlush(matchingsJob);

        int databaseSizeBeforeDelete = matchingsJobRepository.findAll().size();

        // Get the matchingsJob
        restMatchingsJobMockMvc.perform(delete("/api/matchings-jobs/{id}", matchingsJob.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<MatchingsJob> matchingsJobList = matchingsJobRepository.findAll();
        assertThat(matchingsJobList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MatchingsJob.class);
        MatchingsJob matchingsJob1 = new MatchingsJob();
        matchingsJob1.setId(1L);
        MatchingsJob matchingsJob2 = new MatchingsJob();
        matchingsJob2.setId(matchingsJob1.getId());
        assertThat(matchingsJob1).isEqualTo(matchingsJob2);
        matchingsJob2.setId(2L);
        assertThat(matchingsJob1).isNotEqualTo(matchingsJob2);
        matchingsJob1.setId(null);
        assertThat(matchingsJob1).isNotEqualTo(matchingsJob2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MatchingsJobDTO.class);
        MatchingsJobDTO matchingsJobDTO1 = new MatchingsJobDTO();
        matchingsJobDTO1.setId(1L);
        MatchingsJobDTO matchingsJobDTO2 = new MatchingsJobDTO();
        assertThat(matchingsJobDTO1).isNotEqualTo(matchingsJobDTO2);
        matchingsJobDTO2.setId(matchingsJobDTO1.getId());
        assertThat(matchingsJobDTO1).isEqualTo(matchingsJobDTO2);
        matchingsJobDTO2.setId(2L);
        assertThat(matchingsJobDTO1).isNotEqualTo(matchingsJobDTO2);
        matchingsJobDTO1.setId(null);
        assertThat(matchingsJobDTO1).isNotEqualTo(matchingsJobDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(matchingsJobMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(matchingsJobMapper.fromId(null)).isNull();
    }
}
