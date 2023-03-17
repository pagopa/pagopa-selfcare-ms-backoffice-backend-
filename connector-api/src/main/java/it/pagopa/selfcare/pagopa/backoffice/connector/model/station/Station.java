package it.pagopa.selfcare.pagopa.backoffice.connector.model.station;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;


@Data
public class Station {
    @JsonProperty("station_code")
    @NotBlank
    protected String stationCode;

    @JsonProperty("enabled")
    @NotNull
    protected Boolean enabled;

    @JsonProperty("broker_description")
    protected String brokerDescription;

    @JsonProperty("version")
    @NotNull
    protected Long version = 2l;

    @JsonIgnore
    private StationStatus stationStatus = StationStatus.ACTIVE;
    @JsonIgnore
    private Integer associatedCreditorInstitutions = 0;
    @JsonIgnore
    private Instant activationDate = Instant.now();
    @JsonIgnore
    private Instant createdAt = Instant.now(); //FIXME when these fields will be available from apiConfig
    @JsonIgnore
    private Instant modifiedAt = Instant.now(); //FIXME remove instantiation after apiConfig has modified their entities

}
