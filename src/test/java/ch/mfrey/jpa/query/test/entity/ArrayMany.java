package ch.mfrey.jpa.query.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ArrayMany {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ArrayOne one;

    @Column
    private String title;

    public Long getId() {
        return id;
    }

    public ArrayOne getOne() {
        return one;
    }

    public String getTitle() {
        return title;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setOne(final ArrayOne one) {
        this.one = one;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}
