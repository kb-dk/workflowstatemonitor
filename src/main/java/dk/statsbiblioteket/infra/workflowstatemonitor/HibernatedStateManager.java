package dk.statsbiblioteket.infra.workflowstatemonitor;

import org.hibernate.Session;

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
 *
 */
@Path("/")
public class HibernatedStateManager implements StateManager {
    @Override
    @POST
    @Path("states/{entityName}/")
    @Consumes("text/xml")
    public void addState(@PathParam("entityName") String entityName, State state) {
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
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
    }

    @Override
    @GET
    @Path("entities/")
    @Produces("text/xml")
    public List<Entity> listEntities() {
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
        return entities;
    }

    @Override
    @GET
    @Path("states/{entityName}/")
    @Produces("text/xml")
    public List<State> listStates(@PathParam("entityName") String entityName) {
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
        return states;
    }

    @Override
    @GET
    @Path("states/")
    @Produces("text/xml")
    public List<State> listStates(@QueryParam("onlyLast") boolean onlyLast,
                                  @QueryParam("includes") List<String> includes,
                                  @QueryParam("excludes") List<String> excludes) {
        StringBuilder query = new StringBuilder();

        if (onlyLast) {
            query.append("WHERE s.date = (SELECT MAX(s2.date) FROM State s2 WHERE s.entity.name = s2.entity.name)");
        }

        if (includes != null && includes.size() != 0) {
            if (query.length() > 0) {
                query.append(" AND ");
            } else {
                query.append("WHERE ");
            }
            query.append("s.state IN (\'").append(includes.get(0)).append('\'');
            for (int i = 1; i < includes.size(); i++) {
                query.append(",").append('\'').append(includes.get(1)).append('\'');
            }
            query.append(')');
        }

        if (excludes != null && excludes.size() != 0) {
            if (query.length() > 0) {
                query.append(" AND ");
            } else {
                query.append("WHERE ");
            }
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
        return states;
    }
}
