package dk.statsbiblioteket.mediaplatform.workflowstatemonitor;

import javax.xml.bind.annotation.XmlRootElement;

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
