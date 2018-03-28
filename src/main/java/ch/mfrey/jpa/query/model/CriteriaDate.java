package ch.mfrey.jpa.query.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class CriteriaDate extends AbstractCriteria<LocalDate> {

    public LocalDateTime getMinDateTime() {
        return getParameter() == null ? null : getParameter().atStartOfDay();
    }

    public LocalDateTime getMaxDateTime() {
        return getParameter() == null ? null : getParameter().atTime(LocalTime.MAX);
    }
}
