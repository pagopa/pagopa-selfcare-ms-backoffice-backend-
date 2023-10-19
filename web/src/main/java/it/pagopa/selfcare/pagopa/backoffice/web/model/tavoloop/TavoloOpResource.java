package it.pagopa.selfcare.pagopa.backoffice.web.model.tavoloop;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.Instant;
@Data
public class TavoloOpResource {

    private String id;

    private String taxCode;

    private String name;

    private String referent;

    private String email;

    private String telephone;

    private Instant modifiedAt;

    private String modifiedBy;

    private Instant createdAt;

    private String createdBy;
}
