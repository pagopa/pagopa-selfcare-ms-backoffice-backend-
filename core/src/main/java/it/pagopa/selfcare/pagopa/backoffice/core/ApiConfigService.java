package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.*;

public interface ApiConfigService {

    Channels getChannels(Integer limit, Integer page, String code, String sort, String xRequestId);

    ChannelDetails createChannel(ChannelDetails channelDetails, String xRequestId);
    PspChannels getPspChannels(String pspCode, String xRequestId);
    ChannelDetails getChannelDetails(String channelCode, String xRequestId);
    PspChannelPaymentTypes createChannelPaymentType(PspChannelPaymentTypes pspChannelPaymentTypes, String channelCode, String xRequestId);
    PaymentTypes getPaymentTypes(String xRequestId);
}
