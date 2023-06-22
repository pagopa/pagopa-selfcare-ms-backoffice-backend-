package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.IbansResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {IbanController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        IbanController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
public class IbanControllerTest {

    private static final String BASE_URL = "/creditorinstitutions/ibans";

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiConfigService apiConfigServiceMock;

    @Test
    void getCreditorInstitutionIbans() throws Exception {

        String ecCode = "ecCode";
        IbansDetails ibansDetails = mockInstance(new IbansDetails());
        IbanDetails ibanDetails = mockInstance(new IbanDetails());
        ibansDetails.setIbanList(List.of(ibanDetails));

        when(apiConfigServiceMock.getCreditorInstitutionIbans(anyString(), anyString()))
                .thenReturn(ibansDetails);

        mvc.perform(MockMvcRequestBuilders
                .get(BASE_URL)
                .content(ecCode)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON));


        verify(apiConfigServiceMock, times(1))
                .getCreditorInstitutionIbans(anyString(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }
}
