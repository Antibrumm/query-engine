package ch.mfrey.jpa.query.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class A {

    @Column
    private Boolean active;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String title;

    public Boolean getActive() {
        return active;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setActive(final Boolean active) {
        this.active = active;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
