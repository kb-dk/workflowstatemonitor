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

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.Collections;
import java.util.List;

/**
 * A state manager backed by a hibernated database.
 * This class is annotated to be exposed as a REST webservice.
 */
@Path("/")
public class HibernatedStateManager implements StateManager {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    @POST
    @Path("states/{entityName}/")
    @Consumes("text/xml")
    public void addState(@PathParam("entityName") String entityName, State state) {
        try {
            log.trace("Enter addState(entityName='{}',state='{}')", entityName, state);
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            try {
                session.beginTransaction();

                Entity entity = (Entity) session.createQuery("from Entity where name = '" + entityName + "'")
                        .uniqueResult();
                if (entity == null) {
                    entity = new Entity();
                    entity.setName(entityName);
                    session.save(entity);
                }
                state.setEntity(entity);
                session.save(state);
                session.getTransaction().commit();
                log.debug("Added state '{}'", state);
            } finally {
                if (session.isOpen()) {
                    session.close();
                }
            }
            log.trace("Exit addState(entityName='{}',state='{}')", entityName, state);
        } catch (RuntimeException e) {
            log.error("Failed addState(entityName='{}',state='{}'): '{}'", new Object[]{entityName, state, e});
            throw e;
        }
    }

    @Override
    @GET
    @Path("entities/")
    @Produces("text/xml")
    public List<Entity> listEntities() {
        try {
            log.trace("Enter listEntities()");
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            List<Entity> entities;
            try {
                session.beginTransaction();

                entities = session.createQuery("from Entity").list();
                session.getTransaction().commit();
            } finally {
                if (session.isOpen()) {
                    session.close();
                }
            }
            log.trace("Exit listEntities()->entities='{}'", entities.toString());
            return entities;
        } catch (RuntimeException e) {
            log.error("Failed listEntities(): '{}'", e);
            throw e;
        }
    }

    @Override
    @GET
    @Path("states/{entityName}/")
    @Produces("text/xml")
    public List<State> listStates(@PathParam("entityName") String entityName) {
        try {
            log.trace("Enter listStates(entityName='{}')", entityName);
            if (entityName == null) {
                return Collections.emptyList();
            }

            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            List<State> states;
            try {
                session.beginTransaction();

                states = session.createQuery("FROM State s WHERE s.entity.name='" + entityName + "'")
                        .list();
                session.getTransaction().commit();
            } finally {
                if (session.isOpen()) {
                    session.close();
                }
            }
            log.trace("Exit listStates(entityName='{}')->states='{}'", entityName, states);
            return states;
        } catch (RuntimeException e) {
            log.error("Failed listStates(entityName='{}'): '{}'", entityName, e);
            throw e;
        }
    }

    @Override
    @GET
    @Path("states/")
    @Produces("text/xml")
    public List<State> listStates(@QueryParam("onlyLast") boolean onlyLast,
                                  @QueryParam("includes") List<String> includes,
                                  @QueryParam("excludes") List<String> excludes) {
        try {
            log.trace("Enter listStates(onlyLast='{}', includes='{}', excludes='{}')",
                      new Object[]{onlyLast, includes, excludes});
            StringBuilder query = new StringBuilder();

            if (onlyLast) {
                initNextClause(query);
                query.append("s.date = (SELECT MAX(s2.date) FROM State s2 WHERE s.entity.id = s2.entity.id)");
            }

            if (includes != null && includes.size() != 0) {
                initNextClause(query);
                query.append("s.state IN (\'").append(includes.get(0)).append('\'');
                for (int i = 1; i < includes.size(); i++) {
                    query.append(",").append('\'').append(includes.get(1)).append('\'');
                }
                query.append(')');
            }

            if (excludes != null && excludes.size() != 0) {
                initNextClause(query);
                query.append("NOT s.state IN (\'").append(excludes.get(0)).append('\'');
                for (int i = 1; i < excludes.size(); i++) {
                    query.append(",").append('\'').append(excludes.get(1)).append('\'');
                }
                query.append(')');
            }

            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            List<State> states;
            try {
                session.beginTransaction();

                states = session.createQuery("SELECT s FROM State s " + query.toString())
                        .list();
                session.getTransaction().commit();
            } finally {
                if (session.isOpen()) {
                    session.close();
                }
            }
            log.trace("Exit listStates(onlyLast='{}', includes='{}', excludes='{}') -> states='{}'",
                      new Object[]{onlyLast, includes, excludes, states});
            return states;
        } catch (RuntimeException e) {
            log.error("Failed listStates(onlyLast='{}', includes='{}', excludes='{}'): '{}'",
                      new Object[]{onlyLast, includes, excludes, e});
            throw e;
        }
    }

    private void initNextClause(StringBuilder query) {
        if (query.length() > 0) {
            query.append(" AND ");
        } else {
            query.append("WHERE ");
        }
    }
}
