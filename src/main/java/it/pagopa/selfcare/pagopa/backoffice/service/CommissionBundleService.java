package it.pagopa.selfcare.pagopa.backoffice.service;

import feign.FeignException;
import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigSelfcareIntegrationClient;
import it.pagopa.selfcare.pagopa.backoffice.client.AwsSesClient;
import it.pagopa.selfcare.pagopa.backoffice.client.GecClient;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlePaymentTypes;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundleResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.BundlesResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundle;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundleFee;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CIBundleStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.CISubscriptionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleCISubscriptionsDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleCISubscriptionsResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.PublicBundleSubscriptionStatus;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.Touchpoints;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreateResponse;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleCreditorInstitutionResource;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundlePaymentTypesDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.BundleType;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleAttribute;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.CiBundleDetails;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PspCiBundleAttribute;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequest;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.PublicBundleRequests;
import it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle.client.TouchpointsDTO;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.email.EmailMessageDetail;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.SelfcareProductUser;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.CreditorInstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.util.LegacyPspCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CommissionBundleService {

    private static final String BUNDLE_DELETE_SUBSCRIPTION_SUBJECT = "Conferma rimozione da pacchetto";
    private static final String BUNDLE_DELETE_SUBSCRIPTION_BODY = "Ciao %n%n%n sei stato rimosso dal pacchetto %s.%n%n%n Se riscontri dei problemi, puoi richiedere maggiori dettagli utilizzando il canale di assistenza ( https://selfcare.pagopa.it/assistenza ).%n%n%nA presto,%n%nBack-office pagoPa";

    private final GecClient gecClient;

    private final ModelMapper modelMapper;

    private final TaxonomyService taxonomyService;

    private final LegacyPspCodeUtil legacyPspCodeUtil;

    private final ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient;

    private final AwsSesClient awsSesClient;

    @Autowired
    public CommissionBundleService(
            GecClient gecClient,
            ModelMapper modelMapper,
            TaxonomyService taxonomyService,
            LegacyPspCodeUtil legacyPspCodeUtil,
            ApiConfigSelfcareIntegrationClient apiConfigSelfcareIntegrationClient,
            AwsSesClient awsSesClient
    ) {
        this.gecClient = gecClient;
        this.modelMapper = modelMapper;
        this.taxonomyService = taxonomyService;
        this.legacyPspCodeUtil = legacyPspCodeUtil;
        this.apiConfigSelfcareIntegrationClient = apiConfigSelfcareIntegrationClient;
        this.awsSesClient = awsSesClient;
    }

    public BundlePaymentTypes getBundlesPaymentTypes(Integer limit, Integer page) {
        BundlePaymentTypesDTO dto = gecClient.getPaymenttypes(limit, page);
        return modelMapper.map(dto, BundlePaymentTypes.class);
    }

    public Touchpoints getTouchpoints(Integer limit, Integer page) {
        TouchpointsDTO dto = gecClient.getTouchpoints(limit, page);
        return modelMapper.map(dto, Touchpoints.class);
    }

    public BundlesResource getBundlesByPSP(
            String pspTaxCode,
            List<BundleType> bundleType,
            String name, Integer limit,
            Integer page
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        Bundles bundles = this.gecClient.getBundlesByPSP(pspCode, bundleType, name, limit, page);
        return getBundlesResource(bundles);
    }

    private BundlesResource getBundlesResource(Bundles bundles) {
        List<BundleResource> bundleResources = bundles.getBundles() != null ?
                bundles.getBundles().stream().map(bundle -> {
                    BundleResource bundleResource = new BundleResource();
                    BeanUtils.copyProperties(bundle, bundleResource);
                    bundleResource.setTransferCategoryList(
                            this.taxonomyService.getTaxonomiesByCodes(bundle.getTransferCategoryList())
                    );
                    return bundleResource;
                }).toList() :
                new ArrayList<>();
        return BundlesResource.builder().bundles(bundleResources).pageInfo(bundles.getPageInfo()).build();
    }

    public BundleCreateResponse createPSPBundle(String pspTaxCode, BundleRequest bundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        return this.gecClient.createPSPBundle(pspCode, bundle);
    }

    public BundleResource getBundleDetailByPSP(String pspTaxCode, String idBundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        Bundle bundle = this.gecClient.getBundleDetailByPSP(pspCode, idBundle);
        BundleResource bundleResource = new BundleResource();
        BeanUtils.copyProperties(bundle, bundleResource);
        bundleResource.setTransferCategoryList(this.taxonomyService.getTaxonomiesByCodes(bundle.getTransferCategoryList()));
        return bundleResource;
    }

    public void updatePSPBundle(String pspTaxCode, String idBundle, BundleRequest bundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        this.gecClient.updatePSPBundle(pspCode, idBundle, bundle);
    }

    public void deletePSPBundle(String pspTaxCode, String idBundle) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        this.gecClient.deletePSPBundle(pspCode, idBundle);
    }

    /**
     * Retrieve the PSP code with the psp tax code and accept the list of EC subscription requests to a public bundle
     * by invoking the {@link GecClient}
     *
     * @param pspTaxCode          the tax code of the PSP that owns the public bundle
     * @param bundleRequestIdList the list of bundle request id to be accepted
     */
    public void acceptPublicBundleSubscriptionsByPSP(String pspTaxCode, List<String> bundleRequestIdList) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        for (String requestId : bundleRequestIdList) {
            this.gecClient.acceptPublicBundleSubscriptionsByPSP(pspCode, requestId);
        }
    }

    /**
     * Retrieve creditor institution paged bundle list, using optionally a filter by creditor institution tax code,
     * using a dedicate API. the result contains an expanded version of the bundle, using the taxonomy detail extracted
     * from the repository instance
     *
     * @param ciTaxCode optional parameter used for filter by creditor institution tax code
     * @param limit     page limit parameter
     * @param page      page number parameter
     * @return paged list of bundle resources, expanded with taxonomy data
     */
    public BundlesResource getCIBundles(BundleType bundleType, String ciTaxCode, String name, Integer limit, Integer page) {
        if (bundleType.equals(BundleType.GLOBAL)) {
            Bundles bundles = this.gecClient.getBundles(Collections.singletonList(bundleType), name, limit, page);
            return getBundlesResource(bundles);
        } else if (bundleType.equals(BundleType.PUBLIC)) {
            StopWatch stopWatch = StopWatch.createStarted();
            Bundles bundles = gecClient.getBundles(Collections.singletonList(bundleType), name, limit, page);

            List<BundleResource> bundleResources = bundles.getBundles().stream()
                    .map(bundle -> {
                        BundleResource bundleResource = this.modelMapper.map(bundle, BundleResource.class);
                        bundleResource.setCiBundleStatus(getPublicBundleStatus(ciTaxCode, bundle));
                        bundleResource.setTransferCategoryList(
                                this.taxonomyService.getTaxonomiesByCodes(bundle.getTransferCategoryList())
                        );
                        return bundleResource;
                    })
                    .toList();
            stopWatch.stop();
            log.info("GET PUBLIC BUNDLE TERMINATED IN {}", stopWatch);
            return BundlesResource.builder().bundles(bundleResources).pageInfo(bundles.getPageInfo()).build();
        }
        return BundlesResource.builder().bundles(Collections.emptyList()).pageInfo(new PageInfo()).build();
    }

    private CIBundleStatus getPublicBundleStatus(String ciTaxCode, Bundle bundle) {
        try {
            CIBundle ciBundle = this.gecClient.getCIBundle(ciTaxCode, bundle.getId());
            LocalDate today = LocalDate.now();
            LocalDate validityDateTo = ciBundle.getValidityDateTo();
            if (validityDateTo == null || validityDateTo.isAfter(today)) {
                return CIBundleStatus.ENABLED;
            }
            return CIBundleStatus.ON_REMOVAL;
        } catch (FeignException.NotFound ignore) {
            try {
                this.gecClient.getCIPublicBundleRequest(ciTaxCode, null, bundle.getId(), 1, 0);
                return CIBundleStatus.REQUESTED;
            } catch (FeignException.NotFound ignored) {
                return CIBundleStatus.AVAILABLE;
            }
        }
    }

    /**
     * Reject a subscription requests to a public bundle
     *
     * @param pspTaxCode      the tax code of the PSP that owns the public bundle
     * @param bundleRequestId the request id to be rejected
     */
    public void rejectPublicBundleSubscriptionByPSP(String pspTaxCode, String bundleRequestId) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        this.gecClient.rejectPublicBundleSubscriptionByPSP(pspCode, bundleRequestId);
    }

    /**
     * Retrieve a paginated list of creditor institution's info that have requested a subscription ({@link PublicBundleSubscriptionStatus#WAITING})
     * or are subscribed ({@link PublicBundleSubscriptionStatus#ACCEPTED}) to a public bundle of a PSP
     *
     * @param idBundle   the id of the public bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param status     the status of the subscription
     * @param ciTaxCode  the creditor institution's tax code
     * @param limit      the size of the page
     * @param page       the page number
     * @return a paginated list of creditor institution's info
     */
    public PublicBundleCISubscriptionsResource getPublicBundleCISubscriptions(
            String idBundle,
            String pspTaxCode,
            PublicBundleSubscriptionStatus status,
            String ciTaxCode,
            Integer limit,
            Integer page
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        PageInfo pageInfo;
        List<CISubscriptionInfo> ciSubscriptionInfoList;

        if (status.equals(PublicBundleSubscriptionStatus.ACCEPTED)) {
            BundleCreditorInstitutionResource acceptedSubscription = this.gecClient
                    .getPublicBundleSubscriptionByPSP(pspCode, idBundle, ciTaxCode, limit, page);

            List<CreditorInstitutionInfo> ciInfoList = getCIInfo(acceptedSubscription);
            ciSubscriptionInfoList = buildCISubscriptionInfoList(ciInfoList, acceptedSubscription);
            pageInfo = acceptedSubscription.getPageInfo();
        } else {
            PublicBundleRequests subscriptionRequest = this.gecClient
                    .getPublicBundleSubscriptionRequestByPSP(pspCode, ciTaxCode, idBundle, limit, page);

            List<CreditorInstitutionInfo> ciInfoList = getCIInfo(subscriptionRequest);
            ciSubscriptionInfoList = buildCISubscriptionInfoList(ciInfoList);
            pageInfo = subscriptionRequest.getPageInfo();
        }

        return PublicBundleCISubscriptionsResource.builder()
                .ciSubscriptionInfoList(ciSubscriptionInfoList)
                .pageInfo(pageInfo)
                .build();
    }

    /**
     * Retrieve the detail of a creditor institution's subscription to a public bundle, included the specified taxonomies
     *
     * @param idBundle   the id of the public bundle
     * @param pspTaxCode the payment service provider's tax code
     * @param ciTaxCode  the creditor institution's tax code
     * @param status     the status of the subscription
     * @return the detail of a creditor institution's subscription
     */
    public PublicBundleCISubscriptionsDetail getPublicBundleCISubscriptionsDetail(
            String idBundle,
            String pspTaxCode,
            String ciTaxCode,
            PublicBundleSubscriptionStatus status
    ) {
        String pspCode = this.legacyPspCodeUtil.retrievePspCode(pspTaxCode, false);
        List<CIBundleFee> ciBundleFeeList = Collections.emptyList();
        String bundleRequestId = null;
        String idCIBundle = null;

        if (status.equals(PublicBundleSubscriptionStatus.ACCEPTED)) {
            CiBundleDetails ciBundleDetails = this.gecClient
                    .getPublicBundleSubscriptionDetailByPSP(pspCode, ciTaxCode, idBundle);

            List<Taxonomy> taxonomies = getTaxonomiesByCIBundleDetails(ciBundleDetails);

            ciBundleFeeList = getBundleFeeList(ciBundleDetails, taxonomies);
            idCIBundle = ciBundleDetails.getIdCIBundle();
        } else {
            PublicBundleRequests subscriptionRequest = this.gecClient
                    .getPublicBundleSubscriptionRequestByPSP(pspCode, ciTaxCode, idBundle, 1, 0);

            if (!subscriptionRequest.getRequestsList().isEmpty()) {
                PublicBundleRequest publicBundleRequest = subscriptionRequest.getRequestsList().get(0);
                List<Taxonomy> taxonomies = getTaxonomiesByPSPBundleRequest(publicBundleRequest);

                ciBundleFeeList = getCiBundleFeeList(publicBundleRequest, taxonomies);
                bundleRequestId = publicBundleRequest.getId();
            }
        }

        return PublicBundleCISubscriptionsDetail.builder()
                .ciBundleFeeList(ciBundleFeeList)
                .bundleRequestId(bundleRequestId)
                .idCIBundle(idCIBundle)
                .build();
    }

    /**
     * Delete the creditor institution's subscription to the specified bundle
     *
     * @param ciBundleId subscription's id of a creditor institution to a bundle
     * @param ciTaxCode  creditor institution's tax code
     * @param bundleName bundle's name
     */
    public void deleteCIBundleSubscription(String ciBundleId, String ciTaxCode, String bundleName) {
        this.gecClient.deleteCIBundle(ciTaxCode, ciBundleId);

        EmailMessageDetail messageDetail = EmailMessageDetail.builder()
                .institutionTaxCode(ciTaxCode)
                .subject(BUNDLE_DELETE_SUBSCRIPTION_SUBJECT)
                .textBody(String.format(BUNDLE_DELETE_SUBSCRIPTION_BODY, bundleName))
                .htmlBodyFileName("deleteBundleSubscriptionEmail.html")
                .htmlBodyContext(buildEmailHtmlBodyContext(bundleName))
                .destinationUserType(SelfcareProductUser.ADMIN)
                .build();

        awsSesClient.sendEmail(messageDetail);
    }

    private Context buildEmailHtmlBodyContext(String bundleName) {
        // Thymeleaf Context
        Context context = new Context();

        // Properties to show up in Template after stored in Context
        Map<String, Object> properties = new HashMap<>();
        properties.put("bundleName", bundleName);

        context.setVariables(properties);
        return context;
    }

    private List<CISubscriptionInfo> buildCISubscriptionInfoList(List<CreditorInstitutionInfo> ciInfoList, BundleCreditorInstitutionResource acceptedSubscription) {
        LocalDate today = LocalDate.now();

        return ciInfoList.parallelStream()
                .map(ciInfo -> {
                    CISubscriptionInfo subscriptionInfo = this.modelMapper.map(ciInfo, CISubscriptionInfo.class);
                    LocalDate validityDateTo = acceptedSubscription.getCiBundleDetails().stream()
                            .filter(s -> s.getCiTaxCode().equals(ciInfo.getCiTaxCode()))
                            .findFirst()
                            .orElse(new CiBundleDetails())
                            .getValidityDateTo();

                    subscriptionInfo.setOnRemoval(validityDateTo != null && (validityDateTo.isBefore(today) || validityDateTo.isEqual(today)));

                    return subscriptionInfo;
                })
                .toList();
    }

    private List<CISubscriptionInfo> buildCISubscriptionInfoList(List<CreditorInstitutionInfo> ciInfoList) {
        return ciInfoList.parallelStream()
                .map(ciInfo -> this.modelMapper.map(ciInfo, CISubscriptionInfo.class))
                .toList();
    }

    private List<CIBundleFee> getCiBundleFeeList(PublicBundleRequest publicBundleRequest, List<Taxonomy> taxonomies) {
        return publicBundleRequest.getCiBundleAttributes().parallelStream()
                .map(attribute -> buildCIBundleFee(
                        attribute.getMaxPaymentAmount(),
                        attribute.getTransferCategory(),
                        taxonomies)
                )
                .toList();
    }

    private List<CIBundleFee> getBundleFeeList(CiBundleDetails ciBundleDetails, List<Taxonomy> taxonomies) {
        return ciBundleDetails.getAttributes().parallelStream()
                .map(attribute -> buildCIBundleFee(
                        attribute.getMaxPaymentAmount(),
                        attribute.getTransferCategory(),
                        taxonomies)
                )
                .toList();
    }

    private List<CreditorInstitutionInfo> getCIInfo(BundleCreditorInstitutionResource acceptedSubscription) {
        List<String> taxCodeList = acceptedSubscription.getCiBundleDetails().parallelStream()
                .map(CiBundleDetails::getCiTaxCode)
                .toList();
        return this.apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(taxCodeList);
    }

    private List<CreditorInstitutionInfo> getCIInfo(PublicBundleRequests subscriptionRequest) {
        List<String> taxCodeList = subscriptionRequest.getRequestsList().parallelStream()
                .map(PublicBundleRequest::getCiFiscalCode)
                .toList();
        return this.apiConfigSelfcareIntegrationClient.getCreditorInstitutionInfo(taxCodeList);
    }

    private List<Taxonomy> getTaxonomiesByPSPBundleRequest(PublicBundleRequest publicBundleRequest) {
        List<String> transferCategoryList = publicBundleRequest
                .getCiBundleAttributes().parallelStream()
                .map(PspCiBundleAttribute::getTransferCategory)
                .toList();

        return this.taxonomyService.getTaxonomiesByCodes(transferCategoryList);
    }

    private List<Taxonomy> getTaxonomiesByCIBundleDetails(CiBundleDetails ciBundleDetails) {
        List<String> transferCategoryList = ciBundleDetails.getAttributes().parallelStream()
                .map(CiBundleAttribute::getTransferCategory)
                .toList();

        return this.taxonomyService.getTaxonomiesByCodes(transferCategoryList);
    }

    private CIBundleFee buildCIBundleFee(Long paymentAmount, String transferCategory, List<Taxonomy> taxonomies) {
        return CIBundleFee.builder()
                .paymentAmount(paymentAmount)
                .specificBuiltInData(transferCategory)
                .serviceType(taxonomies.stream()
                        .filter(taxonomy -> taxonomy.getSpecificBuiltInData().equals(transferCategory))
                        .findFirst()
                        .orElse(new Taxonomy())
                        .getServiceType())
                .build();
    }
}
