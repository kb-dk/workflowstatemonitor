package dk.statsbiblioteket.mediaplatform.workflowstatemonitor;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for using hibernate.
 */
public final class HibernateUtil {
    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    private static final SessionFactory sessionFactory = buildSessionFactory();
    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration().configure();
            log.info("Connection to hibernate with configuration '{}'", configuration.getProperties());
            return configuration.buildSessionFactory(
                    new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry());
        } catch (Exception e) {
            log.error("Initial SessionFactory creation failed.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /** Utility class, must not be initialised. */
    private HibernateUtil(){}

    /**
     * Get the session factory for hibernate sessions.
     * @return The session factory.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}