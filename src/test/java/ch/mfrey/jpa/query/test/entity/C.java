package ch.mfrey.jpa.query.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class C {

    @Column
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    private B b1;

    @ManyToOne(fetch = FetchType.LAZY)
    private B b2;

    @Id
    private Long id;

    @Column
    private String title;

    public Boolean getActive() {
        return active;
    }

    public B getB1() {
        return b1;
    }

    public B getB2() {
        return b2;
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

    public void setB1(final B b1) {
        this.b1 = b1;
    }

    public void setB2(final B b2) {
        this.b2 = b2;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
