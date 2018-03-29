package ch.mfrey.jpa.query.test.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;

@Entity
public class ArrayOne {

    @Id
    private Long id;

    @OneToMany(mappedBy = "one")
    @OrderColumn()
    private ArrayMany[] manys;

    @Column
    private String title;

    public Long getId() {
        return id;
    }

    public ArrayMany[] getManys() {
        return manys;
    }

    public String getTitle() {
        return title;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setManys(final ArrayMany[] manys) {
        this.manys = manys;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
