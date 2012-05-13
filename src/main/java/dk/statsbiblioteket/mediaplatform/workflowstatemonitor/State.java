/*
 * #%L
 * Workflow state monitor
 * %%
 * Copyright (C) 2012 The State and University Library, Denmark
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package dk.statsbiblioteket.mediaplatform.workflowstatemonitor;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Bean representing a single state in a workflow.
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
