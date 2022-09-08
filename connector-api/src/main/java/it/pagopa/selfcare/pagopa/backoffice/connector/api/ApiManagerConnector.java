package it.pagopa.selfcare.pagopa.backoffice.connector.api;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.InstitutionApiKeys;

public interface ApiManagerConnector {

    void createInstitution(String userId, CreateInstitutionApiKeyDto dto);
    
    InstitutionApiKeys createInstitutionSubscription(String institutionId, String institutionName);

    InstitutionApiKeys getInstitutionApiKeys(String userId);
    
}
