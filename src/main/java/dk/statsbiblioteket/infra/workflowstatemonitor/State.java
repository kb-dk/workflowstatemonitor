package dk.statsbiblioteket.infra.workflowstatemonitor;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;
import java.util.Set;

/**
 * Bean representing a single state in a workflow
 */
@XmlRootElement
public class State {
    private int id;
    private String component;
    private String state;
    private Date date;
    private Set<Entity> entities;

    public State() {
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Set<Entity> getEntities() {
        return entities;
    }

    public void setEntities(Set<Entity> entities) {
        this.entities = entities;
    }

    @Override
    public String toString() {
        return "State{" + "id=" + id + ", component='" + component + '\''
                + ", state='" + state + '\'' + ", date=" + date + ", entities="
                + entities + '}';
    }
}
