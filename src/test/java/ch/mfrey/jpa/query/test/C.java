package ch.mfrey.jpa.query.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class C {

    @Id
    private Long id;

    @Column
    private String title;

    @Column
    private Boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    private B b1;

    @ManyToOne(fetch = FetchType.LAZY)
    private B b2;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public B getB1() {
        return b1;
    }

    public void setB1(B b1) {
        this.b1 = b1;
    }

    public B getB2() {
        return b2;
    }

    public void setB2(B b2) {
        this.b2 = b2;
    }
}
