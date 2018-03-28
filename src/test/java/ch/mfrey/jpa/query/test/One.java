package ch.mfrey.jpa.query.test;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class One {

    @Id
    private Long id;

    @Column
    private String title;

    @OneToMany(mappedBy = "one")
    private Set<Many> manys;

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

    public Set<Many> getManys() {
        return manys;
    }

    public void setManys(Set<Many> manys) {
        this.manys = manys;
    }
}
