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