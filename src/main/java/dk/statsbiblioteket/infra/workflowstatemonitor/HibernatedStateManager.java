package dk.statsbiblioteket.infra.workflowstatemonitor;

import org.hibernate.Session;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
@Path("/")
public class HibernatedStateManager implements StateManager {
    @Override
    @POST
    @Path("states/{entityName}")
    public void addState(String entityName, State state) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();

            Entity entity = (Entity) session.createQuery("from Entity where name = '" + entityName + "'")
                    .uniqueResult();
            if (entity == null) {
                entity = new Entity();
                entity.setName(entityName);
            }
            entity.getStates().add(state);
            session.saveOrUpdate(entity);
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
    public List<String> listEntities() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<String> entityNames;
        try {
            session.beginTransaction();

            List<Entity> entities = session.createQuery("from Entity").list();
            entityNames = new ArrayList<String>();
            for (Entity entity : entities) {
                entityNames.add(entity.getName());
            }
            session.getTransaction().commit();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return entityNames;
    }

    @Override
    @GET
    @Path("states/")
    @Produces("text/xml")
    public List<State> listStates() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<State> states;
        try {
            session.beginTransaction();

            states = session.createQuery("from State").list();
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
    @Path("states/{entity}")
    @Produces("text/xml")
    public List<State> listStates(String... entity) {
        if (entity == null || entity.length == 0) {
            return Collections.emptyList();
        }

        StringBuilder entities = new StringBuilder();
        entities.append('\'').append(entity[0]).append('\'');
        for (int i = 1; i < entity.length; i++) {
            entities.append(",").append('\'').append(entity[i]).append('\'');
        }

        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<State> states;
        try {
            session.beginTransaction();

            states = session.createQuery(
                    "SELECT s FROM State s, IN (s.entities) AS e WHERE e.name IN (" + entities.toString() + ")").list();
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
    @Path("states/{entity}")
    @Produces("text/xml")
    public List<State> listStates(@QueryParam("onlyLast") boolean onlyLast,
                                  @QueryParam("includes") List<String> includes,
                                  @QueryParam("excludes") List<String> excludes) {
        StringBuilder query = new StringBuilder();

        if (onlyLast) {
            query.append("s.date = (SELECT MAX(s2.date) FROM State s2, IN (s2.entities) AS e2 WHERE e.name = e2.name)");
        }

        if (includes != null && includes.size() != 0) {
            if (query.length() > 0) {
                query.append(" AND ");
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

            states = session.createQuery("SELECT s FROM State s, IN (s.entities) AS e WHERE " + query.toString())
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
