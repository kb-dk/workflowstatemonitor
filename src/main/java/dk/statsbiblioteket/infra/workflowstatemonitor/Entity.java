package dk.statsbiblioteket.infra.workflowstatemonitor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Bean representing a named entity.
 */
public class Entity {
    private int id;
    private String name;
    private Set<State> states = new HashSet<State>();

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

    public Set<State> getStates() {
        return states;
    }

    public void setStates(Set<State> states) {
        this.states = states;
    }

    @Override
    public String toString() {
        return "Entity{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
