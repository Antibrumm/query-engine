package ch.mfrey.jpa.query.test.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Date {

    @Column
    private LocalDate date;

    @Column
    private LocalDateTime dateTime;

    @Id
    private Long id;

    public LocalDate getDate() {
        return date;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setDate(final LocalDate date) {
        this.date = date;
    }

    public void setDateTime(final LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setId(final Long id) {
        this.id = id;
    }

}
