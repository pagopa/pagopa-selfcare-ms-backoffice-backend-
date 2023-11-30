package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorinstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CreditorInstitutionAddressResource {

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.location}")
    private String location;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.city}")
    private String city;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.zipCode}")
    private String zipCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.countryCode}")
    private String countryCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.address.taxDomicile}")
    private String taxDomicile;
}
