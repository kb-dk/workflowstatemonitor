package dk.statsbiblioteket.infra.workflowstatemonitor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Bean representing a named entity.
 */
@XmlRootElement
public class Entity {
    private int id;
    private String name;

    public Entity() {
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Entity{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
