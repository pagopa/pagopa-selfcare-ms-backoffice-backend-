package it.pagopa.selfcare.pagopa.backoffice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Iban;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.IbanCreate;
import it.pagopa.selfcare.pagopa.backoffice.model.iban.Ibans;
import it.pagopa.selfcare.pagopa.backoffice.service.IbanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(value = "/creditor-institutions/{ci-code}/ibans", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Ibans")
public class IbanController {

    @Autowired
    private IbanService ibanService;

    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all IBANs related to creditor institution, filtering by specific label", security = {@SecurityRequirement(name = "JWT")})
    public Ibans getCreditorInstitutionIbans(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                             @Parameter(description = "Label to be used as search filter for associated IBANs") @RequestParam(required = false) String labelName) {

        return ibanService.getIban(ciCode, labelName);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an IBAN owned by creditor institution", security = {@SecurityRequirement(name = "JWT")})
    public Iban createCreditorInstitutionIbans(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                               @RequestBody @NotNull IbanCreate requestDto) {

        return ibanService.createIban(ciCode, requestDto);
    }

    @PutMapping(value = "/{iban-value}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update a specific IBAN owned by creditor institution", security = {@SecurityRequirement(name = "JWT")})
    public Iban updateCreditorInstitutionIbans(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                               @Parameter(description = "IBAN identification value") @PathVariable("iban-value") String ibanValue,
                                               @RequestBody @NotNull IbanCreate requestDto) {

        return ibanService.updateIban(ciCode, ibanValue, requestDto);
    }

    @DeleteMapping(value = "/{iban-value}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete a specific IBAN owned by creditor institution", security = {@SecurityRequirement(name = "JWT")})
    public void deleteCreditorInstitutionIbans(@Parameter(description = "Creditor institution code") @PathVariable("ci-code") String ciCode,
                                               @Parameter(description = "IBAN identification value") @PathVariable("iban-value") String ibanValue) {

        ibanService.deleteIban(ciCode, ibanValue);
    }


    @GetMapping(value = "/export", produces = "text/csv")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Export all IBANs of all creditor institutions handled by a broker EC to CSV", security = {@SecurityRequirement(name = "JWT")})
    public ResponseEntity<Resource> exportIbansToCsv(@Parameter(description = "Broker code") @RequestParam("broker_code") String brokerCode) {

        byte[] file = ibanService.exportIbansToCsv(brokerCode);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=iban-export.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new ByteArrayResource(file));
    }

}
