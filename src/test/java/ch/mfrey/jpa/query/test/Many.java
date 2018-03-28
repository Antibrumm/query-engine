package ch.mfrey.jpa.query.test;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Many {

    @Id
    private Long id;

    @Column
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    private One one;

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

    public One getOne() {
        return one;
    }

    public void setOne(One one) {
        this.one = one;
    }
}
