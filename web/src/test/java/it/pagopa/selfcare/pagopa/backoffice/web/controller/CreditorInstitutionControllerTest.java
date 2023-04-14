package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.InputStream;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {CreditorInstitutionController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        CreditorInstitutionController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
class CreditorInstitutionControllerTest {
    private static final String BASE_URL = "/creditor-institutions";
    @Autowired
    protected MockMvc mvc;
    private CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);
    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiConfigService apiConfigServiceMock;

    @Test
    void createCreditorInstitution(@Value("classpath:stubs/creditorInstitutionDetailsDto.json") Resource dto) throws Exception {
        //given
        InputStream resource = dto.getInputStream();

        CreditorInstitutionDto creditorInstitutionDto = objectMapper.readValue(resource, CreditorInstitutionDto.class);

        CreditorInstitutionDetails response = mapper.fromDto(creditorInstitutionDto);

        when(apiConfigServiceMock.createCreditorInstitution(any(), anyString()))
                .thenReturn(response);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.address.city", is(response.getAddress().getCity())))
                .andExpect(jsonPath("$.address.location", is(response.getAddress().getLocation())))
                .andExpect(jsonPath("$.address.countryCode", is(response.getAddress().getCountryCode())))
                .andExpect(jsonPath("$.address.zipCode", is(response.getAddress().getZipCode())))
                .andExpect(jsonPath("$.address.taxDomicile", is(response.getAddress().getTaxDomicile())))
                .andExpect(jsonPath("$.enabled", is(response.getEnabled())))
                .andExpect(jsonPath("$.pspPayment", is(response.getPspPayment())))
                .andExpect(jsonPath("$.creditorInstitutionCode", is(response.getCreditorInstitutionCode())))
                .andExpect(jsonPath("$.businessName", is(response.getBusinessName())))
                .andExpect(jsonPath("$.reportingFtp", is(response.getReportingFtp())));
        //then
        verify(apiConfigServiceMock, times(1))
                .createCreditorInstitution(eq(response), any());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getCreditorInstitutionDetails(@Value("classpath:stubs/creditorInstitutionDetails.json")Resource resource) throws Exception {
        //given
        String ecCode = "creditorInstitution1";
        CreditorInstitutionDetails creditorInstitutionDetails = objectMapper.readValue(resource.getInputStream(), CreditorInstitutionDetails.class);
        when(apiConfigServiceMock.getCreditorInstitutionDetails(anyString(), anyString()))
                .thenReturn(creditorInstitutionDetails);
        //when
        mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL+ "/" +ecCode)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.address.city", is(creditorInstitutionDetails.getAddress().getCity())))
                .andExpect(jsonPath("$.address.location", is(creditorInstitutionDetails.getAddress().getLocation())))
                .andExpect(jsonPath("$.address.countryCode", is(creditorInstitutionDetails.getAddress().getCountryCode())))
                .andExpect(jsonPath("$.address.zipCode", is(creditorInstitutionDetails.getAddress().getZipCode())))
                .andExpect(jsonPath("$.address.taxDomicile", is(creditorInstitutionDetails.getAddress().getTaxDomicile())))
                .andExpect(jsonPath("$.enabled", is(creditorInstitutionDetails.getEnabled())))
                .andExpect(jsonPath("$.pspPayment", is(creditorInstitutionDetails.getPspPayment())))
                .andExpect(jsonPath("$.creditorInstitutionCode", is(creditorInstitutionDetails.getCreditorInstitutionCode())))
                .andExpect(jsonPath("$.businessName", is(creditorInstitutionDetails.getBusinessName())))
                .andExpect(jsonPath("$.reportingFtp", is(creditorInstitutionDetails.getReportingFtp())));
        //then
        verify(apiConfigServiceMock, times(1))
                .getCreditorInstitutionDetails(eq(ecCode), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }
}