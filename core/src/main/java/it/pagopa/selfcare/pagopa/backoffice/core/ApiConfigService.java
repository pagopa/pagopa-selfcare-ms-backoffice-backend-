package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.BrokerDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutions;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.IbansDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.CreditorInstitutionStationEdit;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.StationDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.station.Stations;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperChannels;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.wrapper.WrapperStations;
import org.springframework.core.io.Resource;

public interface ApiConfigService {

    Channels getChannels(Integer limit, Integer page, String code, String sort, String xRequestId);

    ChannelDetails createChannel(ChannelDetails channelDetails, String xRequestId);

    ChannelDetails updateChannel(ChannelDetails channelDetails, String channelCode, String xRequestId);

    PspChannels getPspChannels(String pspCode, String xRequestId);

    ChannelDetails getChannelDetails(String channelCode, String xRequestId);

    PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode, String xRequestId);

    PaymentTypes getPaymentTypes(String xRequestId);

    void deleteChannelPaymentType(String channelCode, String paymentTypeCode, String xRequestId);

    PspChannelPaymentTypes getChannelPaymentTypes(String channelCode, String xRequestId);

    void deletePaymentServiceProvidersChannels(String pspCode, String channelCode, String xRequestId);

    PspChannelPaymentTypes updatePaymentServiceProvidersChannels(String pspCode, String channelCode, PspChannelPaymentTypes pspChannelPaymentTypes, String xRequestId);

    void deleteChannel(String channelCode, String xRequestId);

    PaymentServiceProviders getPspBrokerPsp(Integer limit, Integer page, String brokerPspCode, String xRequestId);

    Resource getChannelsCSV(String uuid);

    ChannelPspList getChannelPaymentServiceProviders(Integer limit, Integer page, String channelCode, String xRequestId);

    BrokerPspDetails createBrokerPsp(BrokerPspDetails brokerPspDetails, String xRequestId);

    BrokerDetails createBroker(BrokerDetails request, String xRequestId);

    PaymentServiceProviderDetails createPaymentServiceProvider(PaymentServiceProviderDetails paymentServiceProviderDetails, String xRequestId);

    Stations getStations(Integer limit, Integer page, String sort, String brokerCode, String ecCode, String stationCode, String xRequestId);

    StationDetails getStation(String stationCode, String xRequestId);

    StationDetails createStation(StationDetails stationDetails, String xRequestId);

    String generateChannelCode(String pspCode, String xRequestId);

    String generateStationCode(String ecCode, String xRequestId);

    PaymentServiceProviderDetails getPSPDetails(String pspCode, String xRequestId);

    CreditorInstitutionStationEdit createCreditorInstitutionStationRelation(String ecCode, CreditorInstitutionStationEdit station, String xRequestId);

    CreditorInstitutionDetails createCreditorInstitution(CreditorInstitutionDetails dto, String xRequestId);

    CreditorInstitutionDetails getCreditorInstitutionDetails(String ecCode, String xRequestId);

    CreditorInstitutionDetails updateCreditorInstitutionDetails(String creditorInstitutionCode,
                                                                CreditorInstitutionDetails request,
                                                                String xRequestId);
    StationDetails updateStation(String stationCode, StationDetails stationDetails, String xRequestId);


    WrapperStations mergeAndSortWrapperStations(WrapperStations wrapperStationsApiConfig,WrapperStations wrapperStationsMongo, String sorting);

    WrapperChannels mergeAndSortWrapperChannels(WrapperChannels wrapperChannelsApiConfig, WrapperChannels wrapperChannelsMongo, String sorting);

    WfespPluginConfs getWfespPlugins(String xRequestId);

    CreditorInstitutions getCreditorInstitutionsByStation(String stationcode, Integer limit, Integer page, String xRequestId);

    void deleteCreditorInstitutionStationRelationship(String ecCode, String stationcode, String xRequestId);

    IbansDetails getCreditorInstitutionIbans(String ecCode, String xRequestId);

    IbanCreate createCreditorInstitutionIbans(String ecCode, IbanCreate ibanCreate, String xRequestId);

}
