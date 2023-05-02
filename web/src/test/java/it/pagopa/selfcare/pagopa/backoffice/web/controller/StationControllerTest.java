package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntities;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.DummyWrapperEntity;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.ChannelDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Station;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStatus;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.core.WrapperService;
import it.pagopa.selfcare.pagopa.backoffice.web.config.WebTestConfig;
import it.pagopa.selfcare.pagopa.backoffice.web.handler.RestExceptionsHandler;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.ChannelDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionStationDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.StationMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.StationDetailsDto;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.WrapperStationDetailsDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
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
import java.util.List;

import static it.pagopa.selfcare.pagopa.TestUtils.mockInstance;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = {StationController.class}, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ContextConfiguration(classes = {
        StationController.class,
        RestExceptionsHandler.class,
        WebTestConfig.class,
})
class StationControllerTest {
    private static final String BASE_URL = "/stations";
    @Autowired
    protected MockMvc mvc;
    private StationMapper mapper = Mappers.getMapper(StationMapper.class);
    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private ApiConfigService apiConfigServiceMock;

    @MockBean
    private WrapperService wrapperServiceMock;

    @Test
    void getStations() throws Exception {
        //given
        Integer limit = 1;
        Integer page = 1;
        String sort = "DESC";
        String creditorInstitutionCode = "creditorInstitutionCode";
        Stations stations = mockInstance(new Stations());
        Station station = mockInstance(new Station());
        PageInfo pageInfo = mockInstance(new PageInfo());
        stations.setStationsList(List.of(station));
        stations.setPageInfo(pageInfo);

        when(apiConfigServiceMock.getStations(anyInt(), anyInt(), any(), any(), any(), any()))
                .thenReturn(stations);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL)
                        .queryParam("limit", String.valueOf(limit))
                        .queryParam("page", String.valueOf(page))
                        .queryParam("sort", sort)
                        .queryParam("creditorInstitutionCode", creditorInstitutionCode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pageInfo", notNullValue()))
                .andExpect(jsonPath("$.stationsList", notNullValue()))
                .andExpect(jsonPath("$.stationsList", not(empty())))
                .andExpect(jsonPath("$.stationsList[0].stationCode", notNullValue()));
        //then
        verify(apiConfigServiceMock, times(1))
                .getStations(eq(limit), eq(page), eq(sort), eq(creditorInstitutionCode), isNull(), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void getStation() throws Exception {
        //given
        String stationId = "stationId";
        StationDetails station = mockInstance(new StationDetails());
        when(apiConfigServiceMock.getStation(anyString(), anyString()))
                .thenReturn(station);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/details/{stationId}", stationId)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationCode", notNullValue()))
                .andExpect(jsonPath("$.ip", notNullValue()))
                .andExpect(jsonPath("$.newPassword", notNullValue()))
                .andExpect(jsonPath("$.password", notNullValue()))
                .andExpect(jsonPath("$.port", notNullValue()))
                .andExpect(jsonPath("$.protocol", notNullValue()))
                .andExpect(jsonPath("$.redirectIp", notNullValue()))
                .andExpect(jsonPath("$.redirectPath", notNullValue()))
                .andExpect(jsonPath("$.redirectPort", notNullValue()))
                .andExpect(jsonPath("$.redirectQueryString", notNullValue()))
                .andExpect(jsonPath("$.redirectProtocol", notNullValue()))
                .andExpect(jsonPath("$.service", notNullValue()))
                .andExpect(jsonPath("$.pofService", notNullValue()))
                .andExpect(jsonPath("$.brokerCode", notNullValue()))
                .andExpect(jsonPath("$.protocol4Mod", notNullValue()))
                .andExpect(jsonPath("$.ip4Mod", notNullValue()))
                .andExpect(jsonPath("$.port4Mod", notNullValue()))
                .andExpect(jsonPath("$.service4Mod", notNullValue()))
                .andExpect(jsonPath("$.proxyEnabled", notNullValue()))
                .andExpect(jsonPath("$.proxyHost", notNullValue()))
                .andExpect(jsonPath("$.proxyPort", notNullValue()))
                .andExpect(jsonPath("$.proxyUsername", notNullValue()))
                .andExpect(jsonPath("$.proxyPassword", notNullValue()))
                .andExpect(jsonPath("$.threadNumber", notNullValue()))
                .andExpect(jsonPath("$.timeoutA", notNullValue()))
                .andExpect(jsonPath("$.timeoutB", notNullValue()))
                .andExpect(jsonPath("$.timeoutC", notNullValue()))
                .andExpect(jsonPath("$.flagOnline", notNullValue()))
                .andExpect(jsonPath("$.brokerObjId", notNullValue()))
                .andExpect(jsonPath("$.rtInstantaneousDispatch", notNullValue()))
                .andExpect(jsonPath("$.targetHost", notNullValue()))
                .andExpect(jsonPath("$.targetPort", notNullValue()))
                .andExpect(jsonPath("$.targetPath", notNullValue()))
                .andExpect(jsonPath("$.primitiveVersion", notNullValue()));
        //then
        verify(apiConfigServiceMock, times(1))
                .getStation(eq(stationId), anyString());
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void createStationTest(@Value("classpath:stubs/stationsDto.json") Resource dto) throws Exception {
        // Given
        StationDetailsDto stationDetailsDto = objectMapper.readValue(dto.getInputStream(), StationDetailsDto.class);
        StationDetails stationDetails = mapper.fromDto(stationDetailsDto);
        when(apiConfigServiceMock.createStation(any(), anyString())).thenReturn(stationDetails);

        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        wrapperEntity.setEntity(stationDetails);
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        wrapperEntities.setEntities(List.of(wrapperEntity));

        when(wrapperServiceMock.updateWrapperStationDetailsByOpt(any(), anyString(), anyString()))
                .thenReturn(wrapperEntities);

        // When
        mvc.perform(MockMvcRequestBuilders.
                        post(BASE_URL)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.modifiedBy", notNullValue()))
                .andExpect(jsonPath("$.entity", notNullValue()))
                .andExpect(jsonPath("$.entity.ip", notNullValue()))
                .andExpect(jsonPath("$.entity.port", notNullValue()))
                .andExpect(jsonPath("$.entity.protocol", notNullValue()));
        /*
                .andExpect(jsonPath("$.entity.stationCode", notNullValue()))
                .andExpect(jsonPath("$.ip", notNullValue()))
                .andExpect(jsonPath("$.newPassword", notNullValue()))
                .andExpect(jsonPath("$.password", notNullValue()))
                .andExpect(jsonPath("$.port", notNullValue()))
                .andExpect(jsonPath("$.protocol", notNullValue()))
                .andExpect(jsonPath("$.redirectIp", notNullValue()))
                .andExpect(jsonPath("$.redirectPath", notNullValue()))
                .andExpect(jsonPath("$.redirectPort", notNullValue()))
                .andExpect(jsonPath("$.redirectQueryString", notNullValue()))
                .andExpect(jsonPath("$.redirectProtocol", notNullValue()))
                .andExpect(jsonPath("$.service", notNullValue()))
                .andExpect(jsonPath("$.pofService", notNullValue()))
                .andExpect(jsonPath("$.brokerCode", notNullValue()))
                .andExpect(jsonPath("$.protocol4Mod", notNullValue()))
                .andExpect(jsonPath("$.ip4Mod", notNullValue()))
                .andExpect(jsonPath("$.port4Mod", notNullValue()))
                .andExpect(jsonPath("$.service4Mod", notNullValue()))
                .andExpect(jsonPath("$.proxyEnabled", notNullValue()))
                .andExpect(jsonPath("$.proxyHost", notNullValue()))
                .andExpect(jsonPath("$.proxyPort", notNullValue()))
                .andExpect(jsonPath("$.proxyUsername", notNullValue()))
                .andExpect(jsonPath("$.proxyPassword", notNullValue()))
                .andExpect(jsonPath("$.threadNumber", notNullValue()))
                .andExpect(jsonPath("$.timeoutA", notNullValue()))
                .andExpect(jsonPath("$.timeoutB", notNullValue()))
                .andExpect(jsonPath("$.timeoutC", notNullValue()))
                .andExpect(jsonPath("$.flagOnline", notNullValue()))
                .andExpect(jsonPath("$.brokerObjId", notNullValue()))
                .andExpect(jsonPath("$.rtInstantaneousDispatch", notNullValue()))
                .andExpect(jsonPath("$.targetHost", notNullValue()))
                .andExpect(jsonPath("$.targetPort", notNullValue()))
                .andExpect(jsonPath("$.targetPath", notNullValue()))
                .andExpect(jsonPath("$.primitiveVersion", notNullValue()));
         */

        //then
        verify(wrapperServiceMock, times(1))
                .updateWrapperStationDetailsByOpt(eq(stationDetails), eq(stationDetailsDto.getNote()), eq(stationDetailsDto.getStatus().name()));
    }

    @Test
    void getStationCode() throws Exception {
        //given
        String ecCode = "ecCode";
        String stationCode = "stationCode";

        when(apiConfigServiceMock.generateStationCode(anyString(), any()))
                .thenReturn(stationCode);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .get(BASE_URL + "/{ecCode}/generate", ecCode)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.stationCode", is(stationCode)));
        //then
        verify(apiConfigServiceMock, times(1))
                .generateStationCode(eq(ecCode), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void associateStationToCreditorInstitution() throws Exception {
        //given
        String ecCode = "ecCode";
        CreditorInstitutionStationDto station = mockInstance(new CreditorInstitutionStationDto());
        CreditorInstitutionStationEdit response = mockInstance(new CreditorInstitutionStationEdit());

        when(apiConfigServiceMock.createCreditorInstitutionStationRelation(anyString(), any(), anyString()))
                .thenReturn(response);
        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/{ecCode}/station", ecCode)
                        .content(objectMapper.writeValueAsBytes(station))
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.stationCode", is(station.getStationCode())));
        //then
        ArgumentCaptor<CreditorInstitutionStationEdit> stationArgumentCaptor = ArgumentCaptor.forClass(CreditorInstitutionStationEdit.class);
        verify(apiConfigServiceMock, times(1))
                .createCreditorInstitutionStationRelation(eq(ecCode), stationArgumentCaptor.capture(), anyString());
        CreditorInstitutionStationEdit captured = stationArgumentCaptor.getValue();
        assertNotNull(captured);
        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void createWrapperStationDetails(@Value("classpath:stubs/WrapperStationDto.json") Resource dto) throws Exception {
        //given
        String note = "note";
        WrapperStatus status = WrapperStatus.TO_CHECK;

        InputStream is = dto.getInputStream();
        WrapperStationDetailsDto wrapperStationDetailsDto = objectMapper.readValue(is, WrapperStationDetailsDto.class);
        StationDetails stationDetails = mapper.fromWrapperStationDetailsDto(wrapperStationDetailsDto);


        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        wrapperEntities.setEntities(List.of(wrapperEntity));

        when(wrapperServiceMock.createWrapperStationDetails(any(), anyString(), anyString()))
                .thenReturn(wrapperEntities);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .post(BASE_URL + "/create-wrapperStation")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.status", is(wrapperEntities.getStatus().name())))
                .andExpect(jsonPath("$.type", is(wrapperEntities.getType().name())))
                .andExpect(jsonPath("$.entities", notNullValue()));
        //then
        verify(wrapperServiceMock, times(1))
                .createWrapperStationDetails(eq(stationDetails), eq(note), eq(status.name()));
        verifyNoMoreInteractions(apiConfigServiceMock);
    }


    @Test
    void updateWrapperStationDetails(@Value("classpath:stubs/stationsDto.json") Resource dto) throws Exception {
        //given
        StationDetails stationDetails = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));

        InputStream is = dto.getInputStream();

        StationDetailsDto stationDetailsDto = objectMapper.readValue(is, StationDetailsDto.class);
        StationDetails fromStationDetailsDto = mapper.fromDto(stationDetailsDto);
        DummyWrapperEntity<StationDetails> wrapperEntityDto = new DummyWrapperEntity<>(fromStationDetailsDto);
        wrapperEntities.getEntities().add(wrapperEntityDto);
        String status = stationDetailsDto.getStatus().name();
        String note = stationDetailsDto.getNote();
        when(wrapperServiceMock.updateWrapperStationDetails(fromStationDetailsDto, note, status))
                .thenReturn(wrapperEntities);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/update-wrapperStation")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status", is(wrapperEntities.getStatus().name())))
                .andExpect(jsonPath("$.type", is(wrapperEntities.getType().name())))
                .andExpect(jsonPath("$.entities", notNullValue()));
        //then
        verify(wrapperServiceMock, times(1))
                .updateWrapperStationDetails(any(), anyString(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void updateWrapperChannelDetailsByOpt(@Value("classpath:stubs/stationsDto.json") Resource dto) throws Exception {
        //given
        String stationCode = "stationCode";
        StationDetails stationDetails = mockInstance(new StationDetails());
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));

        InputStream is = dto.getInputStream();

        StationDetailsDto stationDetailsDto = objectMapper.readValue(is, StationDetailsDto.class);
        StationDetails fromStationDetailsDto = mapper.fromDto(stationDetailsDto);
        DummyWrapperEntity<StationDetails> wrapperEntityDto = new DummyWrapperEntity<>(fromStationDetailsDto);
        wrapperEntities.getEntities().add(wrapperEntityDto);
        String status = stationDetailsDto.getStatus().name();
        String note = stationDetailsDto.getNote();
        when(wrapperServiceMock.updateWrapperStationDetailsByOpt(fromStationDetailsDto, note, status))
                .thenReturn(wrapperEntities);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/update-wrapperStationByOpt")
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.status", is(wrapperEntities.getStatus().name())))
                .andExpect(jsonPath("$.type", is(wrapperEntities.getType().name())))
                .andExpect(jsonPath("$.entities", notNullValue()));
        //then
        verify(wrapperServiceMock, times(1))
                .updateWrapperStationDetailsByOpt(any(), anyString(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }

    @Test
    void updateStation(@Value("classpath:stubs/stationsDto.json") Resource dto) throws Exception {
        //given
        String xRequestId = "1";
        String stationCode = "string";
        InputStream is = dto.getInputStream();
        StationDetailsDto stationDetailsDto = objectMapper.readValue(is, StationDetailsDto.class);
        StationDetails stationDetails = mapper.fromDto(stationDetailsDto);
        DummyWrapperEntity<StationDetails> wrapperEntity = mockInstance(new DummyWrapperEntity<>(stationDetails));
        DummyWrapperEntities<StationDetails> wrapperEntities = mockInstance(new DummyWrapperEntities<>(wrapperEntity));
        DummyWrapperEntity<StationDetails> wrapperEntityDto = new DummyWrapperEntity<>(stationDetails);
        wrapperEntities.getEntities().add(wrapperEntityDto);

        when(apiConfigServiceMock.updateStation(anyString(), any(), anyString()))
                .thenReturn(stationDetails);
        when(wrapperServiceMock.updateWrapperStationDetails(any(), anyString(), anyString()))
                .thenReturn(wrapperEntities);

        //when
        mvc.perform(MockMvcRequestBuilders
                        .put(BASE_URL + "/{stationcode}", stationCode)
                        .content(dto.getInputStream().readAllBytes())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-Request-Id", String.valueOf(xRequestId)))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.password", is(stationDetails.getPassword())))
                .andExpect(jsonPath("$.newPassword", notNullValue()))
                .andExpect(jsonPath("$.protocol", is(stationDetails.getProtocol().name())))
                .andExpect(jsonPath("$.ip", is(stationDetails.getIp())))
                .andExpect(jsonPath("$.port", notNullValue()))
                .andExpect(jsonPath("$.service", is(stationDetails.getService())))
                .andExpect(jsonPath("$.proxyEnabled", is(stationDetails.getProxyEnabled())))
                .andExpect(jsonPath("$.proxyHost", is(stationDetails.getProxyHost())))
                .andExpect(jsonPath("$.proxyPort", notNullValue()))
                .andExpect(jsonPath("$.proxyUsername", is(stationDetails.getProxyUsername())))
                .andExpect(jsonPath("$.targetHost", is(stationDetails.getTargetHost())))
                .andExpect(jsonPath("$.targetPort", notNullValue()))
                .andExpect(jsonPath("$.targetPath", is(stationDetails.getTargetPath())))
                .andExpect(jsonPath("$.threadNumber", notNullValue()))
                .andExpect(jsonPath("$.timeoutA", notNullValue()))
                .andExpect(jsonPath("$.timeoutB", notNullValue()))
                .andExpect(jsonPath("$.timeoutC", notNullValue()))
                .andExpect(jsonPath("$.redirectIp", is(stationDetails.getRedirectIp())))
                .andExpect(jsonPath("$.redirectPath", is(stationDetails.getRedirectPath())));

        //then
        verify(apiConfigServiceMock, times(1))
                .updateStation(anyString(), any(), anyString());

        verify(wrapperServiceMock, times(1))
                .updateWrapperStationDetails(any(), anyString(), anyString());

        verifyNoMoreInteractions(apiConfigServiceMock);
    }
}



