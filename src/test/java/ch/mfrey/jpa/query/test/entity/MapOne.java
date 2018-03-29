package ch.mfrey.jpa.query.test.entity;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class MapOne {

    @Id
    private Long id;

    @OneToMany(mappedBy = "one")
    private Map<String, MapMany> manys;

    @Column
    private String title;

    public Long getId() {
        return id;
    }

    public Map<String, MapMany> getManys() {
        return manys;
    }

    public String getTitle() {
        return title;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setManys(final Map<String, MapMany> manys) {
        this.manys = manys;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
