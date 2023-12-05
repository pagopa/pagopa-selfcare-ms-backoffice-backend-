package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions.BrokerEcDto;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.*;
import it.pagopa.selfcare.pagopa.backoffice.service.BrokerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping(value = "/brokers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Creditor institution's Brokers")
public class BrokerController {

    @Autowired
    private BrokerService brokerService;


    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a Broker", security = {@SecurityRequirement(name = "JWT")})
    public BrokerResource createBroker(@RequestBody BrokerDto brokerDto) {
        return brokerService.createBroker(brokerDto);
    }

    @GetMapping(value = "", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of creditor brokers", security = {@SecurityRequirement(name = "JWT")})
    public BrokersResource getBrokersEC(@Parameter(description = "") @RequestParam(required = false, defaultValue = "50") Integer limit,
                                        @Parameter(description = "Page number. Page value starts from 0") @RequestParam Integer page,
                                        @RequestParam(required = false) String code,
                                        @RequestParam(required = false) String name,
                                        @Parameter(description = "order by name or code, default = CODE") @RequestParam(required = false, defaultValue = "CODE") String orderby,
                                        @Parameter() @RequestParam(required = false, defaultValue = "DESC") String ordering) {
        return brokerService.getBrokersEC(limit, page, code, name, orderby, ordering);
    }


    @PutMapping(value = "/{broker-code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update an existing EC broker", security = {@SecurityRequirement(name = "JWT")})
    public BrokerDetailsResource updateBroker(@RequestBody @Valid BrokerEcDto dto,
                                              @Parameter(description = "Broker code") @PathVariable("broker-code") String brokerCode) {

        return brokerService.updateBrokerForCI(dto, brokerCode);
    }

    @GetMapping(value = "/{broker-code}/stations", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get paginated list of stations given brokerid code", security = {@SecurityRequirement(name = "JWT")})
    public StationDetailsResourceList getStationsDetailsListByBroker(@PathVariable("broker-code") String brokerCode,
                                                                     @RequestParam(required = false) String stationId,
                                                                     @RequestParam(required = false, defaultValue = "10") Integer limit,
                                                                     @RequestParam(required = false, defaultValue = "0") Integer page) {
        return brokerService.getStationsDetailsListByBroker(brokerCode, stationId, limit, page);
    }
}
