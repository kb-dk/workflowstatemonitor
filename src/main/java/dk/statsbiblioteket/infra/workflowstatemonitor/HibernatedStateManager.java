package dk.statsbiblioteket.infra.workflowstatemonitor;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class HibernatedStateManager implements StateManager {
    @Override
    public void addState(String entityName, State state) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        try {
            session.beginTransaction();

            Entity entity = (Entity) session
                    .createQuery("from Entity where name = '" + entityName + "'")
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
    public List<String> listEntities() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<String> entityNames;
        try {
            session.beginTransaction();

            List<Entity> entities = session
                    .createQuery("from Entity")
                    .list();
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
    public List<State> listStates() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        List<State> states;
        try {
            session.beginTransaction();

            states = session
                    .createQuery("from State")
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

            states = session
                    .createQuery("SELECT s FROM State s, IN (s.entities) AS e WHERE e.name IN ("
                                         + entities.toString() + ")")
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
    public List<State> listStates(boolean onlyLast, List<String> includes,
                                  List<String> excludes) {
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

            states = session
                    .createQuery("SELECT s FROM State s, IN (s.entities) AS e WHERE "
                                         + query.toString())
                    .list();
            session.getTransaction().commit();
        } finally {
            if (session.isOpen()) {
                session.close();
            }
        }
        return states;    }
}
