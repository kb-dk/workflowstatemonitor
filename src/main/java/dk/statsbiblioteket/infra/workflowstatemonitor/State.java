package dk.statsbiblioteket.infra.workflowstatemonitor;

import javax.xml.bind.annotation.XmlRootElement;
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
    private Entity entity;

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

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public String toString() {
        return "State{" + "id=" + id + ", component='" + component + '\''
                + ", state='" + state + '\'' + ", date=" + date + ", entity="
                + entity + '}';
    }
}
