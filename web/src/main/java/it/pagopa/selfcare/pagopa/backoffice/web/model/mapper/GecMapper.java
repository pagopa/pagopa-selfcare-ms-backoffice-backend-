package it.pagopa.selfcare.pagopa.backoffice.web.model.mapper;

import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundle;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.gec.Bundles;
import it.pagopa.selfcare.pagopa.backoffice.web.model.gec.BundleResource;
import it.pagopa.selfcare.pagopa.backoffice.web.model.gec.BundlesResource;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GecMapper {

    public static BundleResource toResource1(Bundle bundle){
        if(bundle == null){
            return null;
        }
        BundleResource response = new BundleResource();

        response.setIdBundle(bundle.getIdBundle());
        response.setName(bundle.getName());
        response.setIdPsp(bundle.getIdPsp());
        response.setInsertedDate(bundle.getInsertedDate());
        response.setTouchpoint(bundle.getTouchpoint());
        response.setType(bundle.getType());
        response.setPaymentAmount(bundle.getPaymentAmount());
        response.setPaymentType(bundle.getPaymentType());
        response.setMaxPaymentAmount(bundle.getMaxPaymentAmount());
        response.setDescription(bundle.getDescription());
        response.setLastUpdatedDate(bundle.getLastUpdatedDate());
        response.setIdCiBundle(bundle.getIdCiBundle());
        response.setMinPaymentAmount(bundle.getMinPaymentAmount());
        response.setValidityDateFrom(bundle.getValidityDateFrom());
        response.setValidityDateTo(bundle.getValidityDateTo());

        List<String> list = new ArrayList<>();
        if (bundle.getTransferCategoryList() != null) {
            bundle.getTransferCategoryList().forEach(i -> list.add(i));
        }
        response.setTransferCategoryList(list);

        return response;
    }

    public static BundlesResource toResource(Bundles bundles){
        if(bundles == null){
            return null;
        }

        BundlesResource bundlesResource = new BundlesResource();

        bundlesResource.setBundles(bundles.getBundles().stream()
                .map(GecMapper::toResource1)
                .collect(Collectors.toList()));

        return bundlesResource;
    }
}
