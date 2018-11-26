package br.com.connekt.matching.web.rest;

import br.com.connekt.matching.MatchingJobApp;

import br.com.connekt.matching.domain.Customization;
import br.com.connekt.matching.repository.CustomizationRepository;
import br.com.connekt.matching.service.CustomizationService;
import br.com.connekt.matching.service.dto.CustomizationDTO;
import br.com.connekt.matching.service.mapper.CustomizationMapper;
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
 * Test class for the CustomizationResource REST controller.
 *
 * @see CustomizationResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MatchingJobApp.class)
public class CustomizationResourceIntTest {

    private static final String DEFAULT_CUSTOM_CSS = "AAAAAAAAAA";
    private static final String UPDATED_CUSTOM_CSS = "BBBBBBBBBB";

    private static final String DEFAULT_URL_LOGO = "AAAAAAAAAA";
    private static final String UPDATED_URL_LOGO = "BBBBBBBBBB";

    private static final Long DEFAULT_MATCHING_ID = 1L;
    private static final Long UPDATED_MATCHING_ID = 2L;

    @Autowired
    private CustomizationRepository customizationRepository;

    @Autowired
    private CustomizationMapper customizationMapper;

    @Autowired
    private CustomizationService customizationService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCustomizationMockMvc;

    private Customization customization;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CustomizationResource customizationResource = new CustomizationResource(customizationService);
        this.restCustomizationMockMvc = MockMvcBuilders.standaloneSetup(customizationResource)
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
    public static Customization createEntity(EntityManager em) {
        Customization customization = new Customization()
            .customCSS(DEFAULT_CUSTOM_CSS)
            .urlLogo(DEFAULT_URL_LOGO)
            .matchingId(DEFAULT_MATCHING_ID);
        return customization;
    }

    @Before
    public void initTest() {
        customization = createEntity(em);
    }

    @Test
    @Transactional
    public void createCustomization() throws Exception {
        int databaseSizeBeforeCreate = customizationRepository.findAll().size();

        // Create the Customization
        CustomizationDTO customizationDTO = customizationMapper.toDto(customization);
        restCustomizationMockMvc.perform(post("/api/customizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(customizationDTO)))
            .andExpect(status().isCreated());

        // Validate the Customization in the database
        List<Customization> customizationList = customizationRepository.findAll();
        assertThat(customizationList).hasSize(databaseSizeBeforeCreate + 1);
        Customization testCustomization = customizationList.get(customizationList.size() - 1);
        assertThat(testCustomization.getCustomCSS()).isEqualTo(DEFAULT_CUSTOM_CSS);
        assertThat(testCustomization.getUrlLogo()).isEqualTo(DEFAULT_URL_LOGO);
        assertThat(testCustomization.getMatchingId()).isEqualTo(DEFAULT_MATCHING_ID);
    }

    @Test
    @Transactional
    public void createCustomizationWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = customizationRepository.findAll().size();

        // Create the Customization with an existing ID
        customization.setId(1L);
        CustomizationDTO customizationDTO = customizationMapper.toDto(customization);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomizationMockMvc.perform(post("/api/customizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(customizationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Customization in the database
        List<Customization> customizationList = customizationRepository.findAll();
        assertThat(customizationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCustomizations() throws Exception {
        // Initialize the database
        customizationRepository.saveAndFlush(customization);

        // Get all the customizationList
        restCustomizationMockMvc.perform(get("/api/customizations?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customization.getId().intValue())))
            .andExpect(jsonPath("$.[*].customCSS").value(hasItem(DEFAULT_CUSTOM_CSS.toString())))
            .andExpect(jsonPath("$.[*].urlLogo").value(hasItem(DEFAULT_URL_LOGO.toString())))
            .andExpect(jsonPath("$.[*].matchingId").value(hasItem(DEFAULT_MATCHING_ID.intValue())));
    }
    
    @Test
    @Transactional
    public void getCustomization() throws Exception {
        // Initialize the database
        customizationRepository.saveAndFlush(customization);

        // Get the customization
        restCustomizationMockMvc.perform(get("/api/customizations/{id}", customization.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(customization.getId().intValue()))
            .andExpect(jsonPath("$.customCSS").value(DEFAULT_CUSTOM_CSS.toString()))
            .andExpect(jsonPath("$.urlLogo").value(DEFAULT_URL_LOGO.toString()))
            .andExpect(jsonPath("$.matchingId").value(DEFAULT_MATCHING_ID.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingCustomization() throws Exception {
        // Get the customization
        restCustomizationMockMvc.perform(get("/api/customizations/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCustomization() throws Exception {
        // Initialize the database
        customizationRepository.saveAndFlush(customization);

        int databaseSizeBeforeUpdate = customizationRepository.findAll().size();

        // Update the customization
        Customization updatedCustomization = customizationRepository.findById(customization.getId()).get();
        // Disconnect from session so that the updates on updatedCustomization are not directly saved in db
        em.detach(updatedCustomization);
        updatedCustomization
            .customCSS(UPDATED_CUSTOM_CSS)
            .urlLogo(UPDATED_URL_LOGO)
            .matchingId(UPDATED_MATCHING_ID);
        CustomizationDTO customizationDTO = customizationMapper.toDto(updatedCustomization);

        restCustomizationMockMvc.perform(put("/api/customizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(customizationDTO)))
            .andExpect(status().isOk());

        // Validate the Customization in the database
        List<Customization> customizationList = customizationRepository.findAll();
        assertThat(customizationList).hasSize(databaseSizeBeforeUpdate);
        Customization testCustomization = customizationList.get(customizationList.size() - 1);
        assertThat(testCustomization.getCustomCSS()).isEqualTo(UPDATED_CUSTOM_CSS);
        assertThat(testCustomization.getUrlLogo()).isEqualTo(UPDATED_URL_LOGO);
        assertThat(testCustomization.getMatchingId()).isEqualTo(UPDATED_MATCHING_ID);
    }

    @Test
    @Transactional
    public void updateNonExistingCustomization() throws Exception {
        int databaseSizeBeforeUpdate = customizationRepository.findAll().size();

        // Create the Customization
        CustomizationDTO customizationDTO = customizationMapper.toDto(customization);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomizationMockMvc.perform(put("/api/customizations")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(customizationDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Customization in the database
        List<Customization> customizationList = customizationRepository.findAll();
        assertThat(customizationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCustomization() throws Exception {
        // Initialize the database
        customizationRepository.saveAndFlush(customization);

        int databaseSizeBeforeDelete = customizationRepository.findAll().size();

        // Get the customization
        restCustomizationMockMvc.perform(delete("/api/customizations/{id}", customization.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Customization> customizationList = customizationRepository.findAll();
        assertThat(customizationList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Customization.class);
        Customization customization1 = new Customization();
        customization1.setId(1L);
        Customization customization2 = new Customization();
        customization2.setId(customization1.getId());
        assertThat(customization1).isEqualTo(customization2);
        customization2.setId(2L);
        assertThat(customization1).isNotEqualTo(customization2);
        customization1.setId(null);
        assertThat(customization1).isNotEqualTo(customization2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CustomizationDTO.class);
        CustomizationDTO customizationDTO1 = new CustomizationDTO();
        customizationDTO1.setId(1L);
        CustomizationDTO customizationDTO2 = new CustomizationDTO();
        assertThat(customizationDTO1).isNotEqualTo(customizationDTO2);
        customizationDTO2.setId(customizationDTO1.getId());
        assertThat(customizationDTO1).isEqualTo(customizationDTO2);
        customizationDTO2.setId(2L);
        assertThat(customizationDTO1).isNotEqualTo(customizationDTO2);
        customizationDTO1.setId(null);
        assertThat(customizationDTO1).isNotEqualTo(customizationDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(customizationMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(customizationMapper.fromId(null)).isNull();
    }
}
