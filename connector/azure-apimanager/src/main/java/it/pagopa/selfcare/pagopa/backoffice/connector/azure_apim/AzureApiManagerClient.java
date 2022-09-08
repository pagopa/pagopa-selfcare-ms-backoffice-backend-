package it.pagopa.selfcare.pagopa.backoffice.connector.azure_apim;

import com.azure.core.credential.TokenCredential;
import com.azure.core.http.rest.Response;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.core.util.Context;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.apimanagement.ApiManagementManager;
import com.azure.resourcemanager.apimanagement.models.*;
import it.pagopa.selfcare.pagopa.backoffice.connector.api.ApiManagerConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.CreateInstitutionApiKeyDto;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.InstitutionApiKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AzureApiManagerClient implements ApiManagerConnector {

    private final ApiManagementManager manager;
    private final String serviceName;
    private final String resourceGroupName;


    public AzureApiManagerClient(@Value("${azure.resource-manager.api-manager.service-name}") String serviceName,
                                 @Value("${azure.resource-manager.api-manager.resource-group}") String resourceGroupName,
                                 @Value("${azure.resource-manager.api-manager.subscription-id}") String subscriptionId,
                                 @Value("${azure.resource-manager.api-manager.tenant-id}") String tenantId
                                 ) {
        this.serviceName = serviceName;
        this.resourceGroupName = resourceGroupName;

        AzureProfile profile = new AzureProfile(tenantId, subscriptionId, AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();
        this.manager = ApiManagementManager
                .authenticate(credential, profile);

    }


    @Override
    public void createInstitution(String userId, CreateInstitutionApiKeyDto dto) {
        UserContract userContract = manager
                .users()
                .define(userId)
                .withExistingService(resourceGroupName, serviceName)
                .withEmail(dto.getEmail())
                .withFirstName(dto.getFiscalCode())
                .withLastName(dto.getDescription())
                .withConfirmation(Confirmation.SIGNUP)
                .create();
    }

    @Override
    public InstitutionApiKeys createInstitutionSubscription(String institutionId, String institutionName) {
        SubscriptionContract contract = manager.subscriptions().createOrUpdate(resourceGroupName,
                serviceName,
                institutionId,
                new SubscriptionCreateParameters()
                        .withOwnerId(String.format("/users/%s", institutionId))
                        .withDisplayName(institutionName)
                        .withScope("/apis")
        );

        InstitutionApiKeys apiKeys = getApiKeys(institutionId);
        return apiKeys;
    }

    @Override
    public InstitutionApiKeys getUserSubscription(String institutionId) {
        log.trace("getUser start");
        log.debug("getUser serviceName = {}, resourceGroup = {}, institutionId = {}", serviceName, resourceGroupName, institutionId);
        InstitutionApiKeys subscription = getApiKeys(institutionId);
        log.debug("getUser result = {}", subscription);
        return subscription;
    }

    @Override
    public void regeneratePrimaryKey(String institutionId) {
        manager.subscriptions().regeneratePrimaryKey(resourceGroupName, serviceName, institutionId);
    }

    @Override
    public void regenerateSecondaryKey(String institutionId) {
        manager.subscriptions().regenerateSecondaryKey(resourceGroupName, serviceName, institutionId);
    }


    private InstitutionApiKeys getApiKeys(String institutionId) {
        InstitutionApiKeys apiKeys = null;
        Response<SubscriptionKeysContract> subscriptionKeysContractResponse = manager.subscriptions().listSecretsWithResponse(resourceGroupName, serviceName, institutionId, Context.NONE);
        if (subscriptionKeysContractResponse.getValue() != null) {
            apiKeys = new InstitutionApiKeys();
            apiKeys.setPrimaryKey(subscriptionKeysContractResponse.getValue().primaryKey());
            apiKeys.setSecondaryKey(subscriptionKeysContractResponse.getValue().secondaryKey());
        }
        return apiKeys;
    }
}
