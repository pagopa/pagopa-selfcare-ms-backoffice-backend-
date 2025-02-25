package it.pagopa.selfcare.pagopa.backoffice.service;

import com.azure.spring.cloud.feature.management.FeatureManager;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsQuicksightClient;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppError;
import it.pagopa.selfcare.pagopa.backoffice.exception.AppException;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.InstitutionDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.quicksightdashboard.QuicksightEmbedUrlResponse;
import it.pagopa.selfcare.pagopa.backoffice.util.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import static it.pagopa.selfcare.pagopa.backoffice.util.Constants.QUICKSIGHT_DASHBOARD_PRODUCT_ID;

@Slf4j
@Service
public class AwsQuicksightService {


    private final AwsQuicksightClient awsQuicksightClient;
    private final FeatureManager featureManager;
    private final ApiManagementService apiManagementService;

    @Autowired
    public AwsQuicksightService(AwsQuicksightClient awsQuicksightClient, FeatureManager featureManager, ApiManagementService apiManagementService) {
        this.awsQuicksightClient = awsQuicksightClient;
        this.featureManager = featureManager;
        this.apiManagementService = apiManagementService;
    }

    /**
     * Generated embed url for Aws quicksight dashboard
     *
     * @return dashboard's embed url
     */
    public QuicksightEmbedUrlResponse generateEmbedUrlForAnonymousUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = Utility.extractUserIdFromAuth(authentication);
        String institutionId = Utility.extractInstitutionIdFromAuth(authentication);

        QuicksightEmbedUrlResponse quicksightEmbedUrlResponse = new QuicksightEmbedUrlResponse();
        if (Boolean.FALSE.equals(this.featureManager.isEnabled("quicksightProductFreeTrial")) &&
                Boolean.FALSE.equals(this.featureManager.isEnabled("isOperator"))
        ) {
            InstitutionDetail institutionDetail = apiManagementService.getInstitutionFullDetail(institutionId);
            if (isNotSubscribedToDashboardProduct(institutionDetail)) {
                throw new AppException(AppError.FORBIDDEN);
            }
        }
        String embedUrl = this.awsQuicksightClient.generateEmbedUrlForAnonymousUser(institutionId);

        quicksightEmbedUrlResponse.setEmbedUrl(embedUrl);

        log.info(
                "Quicksight dashboard url requested by user {} for institution {}. Url: {}",
                userId,
                institutionId,
                quicksightEmbedUrlResponse.getEmbedUrl());
        return quicksightEmbedUrlResponse;
    }

    private static boolean isNotSubscribedToDashboardProduct(InstitutionDetail institutionDetail) {
        return institutionDetail.getOnboarding().parallelStream().noneMatch(el -> el.getProductId().equals(QUICKSIGHT_DASHBOARD_PRODUCT_ID) && el.getStatus().equals("ACTIVE"));
    }
}
