package it.pagopa.selfcare.pagopa.backoffice.web.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import it.pagopa.selfcare.pagopa.backoffice.connector.logging.LogUtils;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.broker.Brokers;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.BrokerPspDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.channel.PaymentServiceProviderDetails;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution.CreditorInstitutionDetails;
import it.pagopa.selfcare.pagopa.backoffice.core.ApiConfigService;
import it.pagopa.selfcare.pagopa.backoffice.web.model.Problem;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.BrokerOrPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.BrokerPspDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.channels.PaymentServiceProviderDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions.CreditorInstitutionDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.BrokerMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.ChannelMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.mapper.CreditorInstitutionMapper;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokerAndEcDetailsResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.stations.BrokersResource;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/utils", produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "Utils")
public class UtilsController {

    private final ApiConfigService apiConfigService;

    CreditorInstitutionMapper mapper = Mappers.getMapper(CreditorInstitutionMapper.class);


    @Autowired
    public UtilsController(ApiConfigService apiConfigService) {
        this.apiConfigService = apiConfigService;
    }

    @GetMapping(value = "/broker-or-psp-details", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getBrokerPsp}")
    public BrokerOrPspDetailsResource getBrokerOrPspDetails(@ApiParam("swagger.request.brokerpspcode")
                                                 @RequestParam(required = false, name = "brokerpspcode") String brokerPspCode) {
        log.trace("getBrokerOrPspDetails start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getBrokerOrPspDetails brokerPspCode = {} , xRequestId:  {}", brokerPspCode, xRequestId);

        BrokerPspDetails brokerPspDetails = apiConfigService.getBrokerPsp(brokerPspCode, xRequestId);
        BrokerPspDetailsResource brokerPspDetailsResource = ChannelMapper.toResource(brokerPspDetails);

        PaymentServiceProviderDetails paymentServiceProviderDetails = apiConfigService.getPSPDetails(brokerPspCode, xRequestId);
        PaymentServiceProviderDetailsResource paymentServiceProviderDetailsResource = ChannelMapper.toResource(paymentServiceProviderDetails);

        BrokerOrPspDetailsResource resource = new BrokerOrPspDetailsResource();
        resource.setBrokerPspDetailsResource(brokerPspDetailsResource);
        resource.setPaymentServiceProviderDetailsResource(paymentServiceProviderDetailsResource);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getBrokerOrPspDetails result = {}", resource);
        log.trace("getBrokerOrPspDetails end");

        return resource;
    }

    @GetMapping(value = "/ec-brokers/{code}/details", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "", notes = "${swagger.api.channels.getBrokerPsp}")
    public BrokerAndEcDetailsResource getBrokerAndEcDetails(@ApiParam("swagger.request.brokerpspcode")
                                                            @RequestParam(required = false, name = "brokercode") String brokercode) throws Exception {
        log.trace("getBrokerOrEcDetails start");
        String xRequestId = UUID.randomUUID().toString();
        log.debug("getBrokerOrEcDetails brokerPspCode = {} , xRequestId:  {}", brokercode, xRequestId);

        BrokersResource brokersResource = null;
        CreditorInstitutionDetailsResource creditorInstitutionDetailsResource = null;

        try {
            Brokers brokers = apiConfigService.getBrokersEC(1, 0, brokercode, null, null, "ASC", xRequestId);
            brokersResource = BrokerMapper.toResource(brokers);
        }catch (Exception e){
            log.trace("getBrokerOrEcDetails - Not BrokerEC found");
        }

        try {
            CreditorInstitutionDetails creditorInstitutionDetails = apiConfigService.getCreditorInstitutionDetails(brokercode, xRequestId);
             creditorInstitutionDetailsResource = mapper.toResource(creditorInstitutionDetails);
        }catch (Exception e){
            log.trace("getBrokerOrEcDetails - Not CreditorInstitution found");
        }

        if(brokersResource == null && creditorInstitutionDetailsResource == null){
            throw new Exception("Nessun dato trovato per il broker o per il creditorInstitution");
        }
        BrokerAndEcDetailsResource resource = new BrokerAndEcDetailsResource();
        resource.setBrokerDetailsResource(brokersResource);
        resource.setCreditorInstitutionDetailsResource(creditorInstitutionDetailsResource);

        log.debug(LogUtils.CONFIDENTIAL_MARKER, "getBrokerOrEcDetails result = {}", resource);
        log.trace("getBrokerOrEcDetails end");

        return resource;
    }
}
