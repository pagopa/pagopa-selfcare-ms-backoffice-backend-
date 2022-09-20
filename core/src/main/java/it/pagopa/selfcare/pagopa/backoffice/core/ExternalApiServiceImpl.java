package it.pagopa.selfcare.pagopa.backoffice.core;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;

@Slf4j
@Service
public class ExternalApiServiceImpl implements ExternalApiService{

    protected static final String AN_INSTITUTION_ID_IS_REQUIRED = "An institutionId is required";
    private final ExternalApiConnector externalApiConnector;
    

    public ExternalApiServiceImpl(ExternalApiConnector externalApiConnector) {
        this.externalApiConnector = externalApiConnector;
    }

    @Override
    public Institution getInstitution(String institutionId) {
        log.trace("getInstitution start");
        log.debug("getInstitution institutionId = {}", institutionId);
        Assert.hasText(institutionId, AN_INSTITUTION_ID_IS_REQUIRED);
        Institution institution = externalApiConnector.getInstitution(institutionId);
        log.debug("getInstitution result = {}", institution);
        log.trace("getInstitution end");
        return institution;
    }

    @Override
    public Collection<InstitutionInfo> getInstitutions(String productId) {
        return null;
    }
}
