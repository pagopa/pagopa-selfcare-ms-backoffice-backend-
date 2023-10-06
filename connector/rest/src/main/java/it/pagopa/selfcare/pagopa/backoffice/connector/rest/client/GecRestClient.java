package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.GecConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.NotNull;
import java.util.ArrayList;

@FeignClient(name = "${rest-client.gec.serviceCode}", url = "${rest-client.gec.base-url}", configuration = FeignConfig.class)
public interface GecRestClient extends GecConnector {

    @GetMapping(value = "${rest-client.gec.getBundlesByCI.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Bundles getBundlesByCI(@PathVariable(required = true) String cifiscalcode,
                           @RequestParam(required = false) Integer limit,
                           @RequestParam(required = false) Integer page);

    @GetMapping(value = "${rest-client.gec.getTouchpoints.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Touchpoints getTouchpoints(@RequestParam(required = false) Integer limit,
                               @RequestParam(required = false) Integer page);

    @GetMapping(value = "${rest-client.gec.getBundlesByPSP.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Bundles getBundlesByPSP(@PathVariable(required = true) String idpsp,
                            @RequestParam(required = false) ArrayList<BundleType> bundleType,
                            @RequestParam(required = false) String name,
                            @RequestParam(required = false) Integer limit,
                            @RequestParam(required = false) Integer page);

    @PostMapping(value = "${rest-client.gec.createPSPBundle.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    String createPSPBundle(@PathVariable(required = true) String idpsp,
                           @RequestBody @NotNull BundleCreate bundle);

    @GetMapping(value = "${rest-client.gec.getPaymenttypes.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    BundlePaymentTypes getPaymenttypes(@RequestParam(required = false) Integer limit,
                                       @RequestParam(required = false) Integer page);
}
