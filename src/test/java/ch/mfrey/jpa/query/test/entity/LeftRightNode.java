package ch.mfrey.jpa.query.test.entity;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class LeftRightNode {

    @OneToMany
    private List<LeftRightNode> leftChilds;

    @OneToMany
    private List<LeftRightNode> rightChilds;

    @Id
    private Long id;

    public List<LeftRightNode> getLeftChilds() {
        return leftChilds;
    }

    public void setLeftChilds(List<LeftRightNode> leftChilds) {
        this.leftChilds = leftChilds;
    }

    public List<LeftRightNode> getRightChilds() {
        return rightChilds;
    }

    public void setRightChilds(List<LeftRightNode> rightChilds) {
        this.rightChilds = rightChilds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
