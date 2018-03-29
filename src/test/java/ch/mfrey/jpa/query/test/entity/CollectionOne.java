package ch.mfrey.jpa.query.test.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class CollectionOne {

    @Id
    private Long id;

    @OneToMany(mappedBy = "one")
    private Set<CollectionMany> manys;

    @Column
    private String title;

    public Long getId() {
        return id;
    }

    public Set<CollectionMany> getManys() {
        return manys;
    }

    public String getTitle() {
        return title;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setManys(final Set<CollectionMany> manys) {
        this.manys = manys;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

}
