package it.pagopa.selfcare.pagopa.backoffice.service;

import it.pagopa.selfcare.pagopa.backoffice.client.ApiConfigClient;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.CreateStationMaintenance;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListResource;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceListState;
import it.pagopa.selfcare.pagopa.backoffice.model.stationmaintenance.StationMaintenanceResource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class StationMaintenanceService {

    private final ApiConfigClient apiConfigClient;

    @Autowired
    public StationMaintenanceService(ApiConfigClient apiConfigClient) {
        this.apiConfigClient = apiConfigClient;
    }

    public StationMaintenanceListResource getStationMaintenances(
            String brokerCode,
            String stationCode,
            StationMaintenanceListState state,
            Integer year,
            Integer limit,
            Integer page
    ) {
        OffsetDateTime startDateTimeBefore = null;
        OffsetDateTime startDateTimeAfter = null;
        OffsetDateTime endDateTimeBefore = null;
        OffsetDateTime endDateTimeAfter = null;

        if (state != null) {
            if (state.equals(StationMaintenanceListState.FINISHED)) {
                endDateTimeBefore = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
            if (state.equals(StationMaintenanceListState.SCHEDULED_AND_IN_PROGRESS)) {
                endDateTimeAfter = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
            if (state.equals(StationMaintenanceListState.SCHEDULED)) {
                startDateTimeAfter = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
            if (state.equals(StationMaintenanceListState.IN_PROGRESS)) {
                startDateTimeBefore = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
                endDateTimeAfter = OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES);
            }
        }

        if (year != null
        ) {
            startDateTimeBefore = startDateTimeBefore != null ? startDateTimeBefore.withYear(year) : getEndOfYear(year);
            startDateTimeAfter = startDateTimeAfter != null ? startDateTimeAfter.withYear(year) : getStartOfYear(year);
        }

        return this.apiConfigClient.getStationMaintenances(
                brokerCode,
                stationCode,
                startDateTimeBefore,
                startDateTimeAfter,
                endDateTimeBefore,
                endDateTimeAfter,
                limit,
                page
        );
    }

    public StationMaintenanceResource createStationMaintenance(String brokerCode,
                                                               CreateStationMaintenance createStationMaintenance) {
        return this.apiConfigClient.createStationMaintenance(brokerCode, createStationMaintenance);
    }

    private OffsetDateTime getStartOfYear(int year) {
        return OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES).withYear(year).withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0);
    }

    private OffsetDateTime getEndOfYear(int year) {
        return OffsetDateTime.now().truncatedTo(ChronoUnit.MINUTES).withYear(year).withMonth(12).withDayOfMonth(31).withHour(23).withMinute(59);
    }
}
