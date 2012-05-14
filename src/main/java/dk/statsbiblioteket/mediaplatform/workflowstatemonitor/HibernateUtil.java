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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;

/**
 * Utility class for using hibernate.
 */
public final class HibernateUtil {
    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);

    private static SessionFactory sessionFactory;

    /** Utility class, must not be initialised. */
    private HibernateUtil(){}

    /**
     * Get the session factory for hibernate sessions.
     * @return The session factory.
     */
    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            // Create the SessionFactory from hibernate.cfg.xml
            String hibernateConfiguration = null;
            try {
                Context initCtx = new InitialContext();
                Context envCtx = (Context) initCtx.lookup("java:comp/env");

                hibernateConfiguration = (String)
                        envCtx.lookup("workflowstatemonitor/hibernate");
                log.info("Using hibernate configuration file '{}'", hibernateConfiguration);
            } catch (NamingException e) {
                log.error("Unable to lookup logback configuration from JNDI", e);
            }
            Configuration configuration;
            if (hibernateConfiguration == null) {
                configuration = new Configuration().configure();
            } else {
                configuration = new Configuration().configure(new File(hibernateConfiguration));
            }
            log.info("Connection to hibernate with configuration '{}'", configuration.getProperties());
            sessionFactory = configuration.buildSessionFactory(
                    new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry());
        }
        return sessionFactory;
    }

}