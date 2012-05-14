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

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestHibernatedStateManager extends TestCase {
    @Before
    public void setUp() throws Exception {
        fillTestDB();
    }

    @After
    public void tearDown() throws Exception {
        clearTestDB();
    }

    @Test
    public void testAddState() throws Exception {
        // Clean database
        clearTestDB();

        //Insert an element
        State state = new State();
        state.setComponent("comp");
        state.setMessage("message");
        state.setDate(new Date(1000));
        state.setStateName("stat");
        HibernatedStateManager hibernatedStateManager = new HibernatedStateManager();
        hibernatedStateManager.addState("test", state);

        //Check element is inserted as expected
        List<Entity> entities = hibernatedStateManager.listEntities();
        List<State> states = hibernatedStateManager.listStates(false, null, null);
        assertEquals(1, entities.size());
        assert(contains(entities, "test"));
        assertEquals(1, states.size());
        assertEquals("stat", states.get(0).getStateName());
        assertEquals("message", states.get(0).getMessage());
        assertEquals("comp", states.get(0).getComponent());
        assertEquals(new Date(1000), states.get(0).getDate());
    }

    @Test
    public void testListEntities() {
        // List entities
        List<Entity> list = new HibernatedStateManager().listEntities();

        // Check them
        assertEquals(2, list.size());
        assertTrue(contains(list, "file1"));
        assertTrue(contains(list, "file2"));
    }

    private boolean contains(List<Entity> entities, String name) {
        for (Entity entity : entities) {
            if (entity.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testListStates() {
        HibernatedStateManager hibernatedStateManager = new HibernatedStateManager();

        // List all states
        List<State> states = hibernatedStateManager.listStates(false, null, null);

        // Check them
        assertEquals(7, states.size());
        assertTrue(contains(states, "comp1", new Date(0), "state1", "file1"));
        assertTrue(contains(states, "comp1", new Date(1000), "state2", "file1"));
        assertTrue(contains(states, "comp2", new Date(2000), "state1", "file1"));
        assertTrue(contains(states, "comp2", new Date(3000), "state2", "file1"));
        assertTrue(contains(states, "comp1", new Date(4000), "state1", "file2"));
        assertTrue(contains(states, "comp1", new Date(5000), "state2", "file2"));
        assertTrue(contains(states, "comp2", new Date(6000), "state1", "file2"));

        // List states for file1
        states = hibernatedStateManager.listStates("file1");

        // Check them
        assertEquals(4, states.size());
        assertTrue(contains(states, "comp1", new Date(0), "state1", "file1"));
        assertTrue(contains(states, "comp1", new Date(1000), "state2", "file1"));
        assertTrue(contains(states, "comp2", new Date(2000), "state1", "file1"));
        assertTrue(contains(states, "comp2", new Date(3000), "state2", "file1"));
        assertFalse(contains(states, "comp1", new Date(4000), "state1", "file2"));
        assertFalse(contains(states, "comp1", new Date(5000), "state2", "file2"));
        assertFalse(contains(states, "comp2", new Date(6000), "state1", "file2"));

        // List all state1 states
        states = hibernatedStateManager.listStates(false, Arrays.asList(new String[]{"state1"}), null);

        // Check them
        assertEquals(4, states.size());
        assertTrue(contains(states, "comp1", new Date(0), "state1", "file1"));
        assertFalse(contains(states, "comp1", new Date(1000), "state2", "file1"));
        assertTrue(contains(states, "comp2", new Date(2000), "state1", "file1"));
        assertFalse(contains(states, "comp2", new Date(3000), "state2", "file1"));
        assertTrue(contains(states, "comp1", new Date(4000), "state1", "file2"));
        assertFalse(contains(states, "comp1", new Date(5000), "state2", "file2"));
        assertTrue(contains(states, "comp2", new Date(6000), "state1", "file2"));

        // List all NOT state2 states
        states = hibernatedStateManager.listStates(false, null, Arrays.asList(new String[]{"state2"}));

        // Check them
        assertEquals(4, states.size());
        assertTrue(contains(states, "comp1", new Date(0), "state1", "file1"));
        assertFalse(contains(states, "comp1", new Date(1000), "state2", "file1"));
        assertTrue(contains(states, "comp2", new Date(2000), "state1", "file1"));
        assertFalse(contains(states, "comp2", new Date(3000), "state2", "file1"));
        assertTrue(contains(states, "comp1", new Date(4000), "state1", "file2"));
        assertFalse(contains(states, "comp1", new Date(5000), "state2", "file2"));
        assertTrue(contains(states, "comp2", new Date(6000), "state1", "file2"));

        // List all state1 and state2 states and NOT state1 (i.e. state2)
        states = hibernatedStateManager.listStates(false, Arrays.asList(new String[]{"state1", "state2"}),
                                                             Arrays.asList(new String[]{"state1"}));

        //Check them
        assertEquals(3, states.size());
        assertFalse(contains(states, "comp1", new Date(0), "state1", "file1"));
        assertTrue(contains(states, "comp1", new Date(1000), "state2", "file1"));
        assertFalse(contains(states, "comp2", new Date(2000), "state1", "file1"));
        assertTrue(contains(states, "comp2", new Date(3000), "state2", "file1"));
        assertFalse(contains(states, "comp1", new Date(4000), "state1", "file2"));
        assertTrue(contains(states, "comp1", new Date(5000), "state2", "file2"));
        assertFalse(contains(states, "comp2", new Date(6000), "state1", "file2"));

        states = hibernatedStateManager.listStates(true, null, null);

        //Check them
        assertEquals(2, states.size());
        assertFalse(contains(states, "comp1", new Date(0), "state1", "file1"));
        assertFalse(contains(states, "comp1", new Date(1000), "state2", "file1"));
        assertFalse(contains(states, "comp2", new Date(2000), "state1", "file1"));
        assertTrue(contains(states, "comp2", new Date(3000), "state2", "file1"));
        assertFalse(contains(states, "comp1", new Date(4000), "state1", "file2"));
        assertFalse(contains(states, "comp1", new Date(5000), "state2", "file2"));
        assertTrue(contains(states, "comp2", new Date(6000), "state1", "file2"));
    }

    private boolean contains(List<State> states, String component, Date date, String state1, String file) {
        for (State state : states) {
            if (state.getComponent().equals(component) && state.getDate().getTime() == date.getTime()
                    && state.getStateName().equals(state1) && state.getEntity().getName()
                    .equals(file)) {
                return true;
            }
        }
        return false;
    }

    private void fillTestDB() {
        HibernatedStateManager hibernatedStateManager = new HibernatedStateManager();
        State state1 = new State();
        state1.setComponent("comp1");
        state1.setDate(new Date(0));
        state1.setStateName("state1");
        hibernatedStateManager.addState("file1", state1);
        State state2 = new State();
        state2.setComponent("comp1");
        state2.setDate(new Date(1000));
        state2.setStateName("state2");
        hibernatedStateManager.addState("file1", state2);
        State state3 = new State();
        state3.setComponent("comp2");
        state3.setDate(new Date(2000));
        state3.setStateName("state1");
        hibernatedStateManager.addState("file1", state3);
        State state4 = new State();
        state4.setComponent("comp2");
        state4.setDate(new Date(3000));
        state4.setStateName("state2");
        hibernatedStateManager.addState("file1", state4);
        State state5 = new State();
        state5.setComponent("comp1");
        state5.setDate(new Date(4000));
        state5.setStateName("state1");
        hibernatedStateManager.addState("file2", state5);
        State state6 = new State();
        state6.setComponent("comp1");
        state6.setDate(new Date(5000));
        state6.setStateName("state2");
        hibernatedStateManager.addState("file2", state6);
        State state7 = new State();
        state7.setComponent("comp2");
        state7.setDate(new Date(6000));
        state7.setStateName("state1");
        hibernatedStateManager.addState("file2", state7);
    }

    public void clearTestDB() throws Exception {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:hsqldb:mem:testdb", "SA", "");
            try {
                Statement stmt = connection.createStatement();
                try {
                    stmt.execute("TRUNCATE SCHEMA PUBLIC RESTART IDENTITY AND COMMIT NO CHECK");
                    connection.commit();
                } finally {
                    stmt.close();
                }
            } catch (SQLException e) {
                connection.rollback();
                throw new Exception(e);
            }
        } catch (SQLException e) {
            throw new Exception(e);
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    /*    public static void main(String[] args) {
        TestHibernatedStateManager registrar = new TestHibernatedStateManager();

        if (args[0].equals("store")) {
            registrar.registerState(args[1], "component", "statename");
        } else if (args[0].equals("listAll")) {
            List<State> states = registrar.listStates();
            for (State theState : states) {
                System.out.println(
                        "Name: " + theState.getName() + " Time: " + theState
                                .getDate());
            }
        } else if (args[0].equals("list")) {
            List<State> states = registrar.listStates(args[1]);
            for (State theState : states) {
                System.out.println(
                        "Name: " + theState.getName() + " Time: " + theState
                                .getDate());
            }
        }

        HibernateUtil.getSessionFactory().close();
    }

    private void registerState(String name, String component,
                               String stateName) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        State state = new State();
        state.setName(name);
        state.setComponent(component);
        state.setState(stateName);
        state.setDate(new Date());

        session.save(state);
        session.getTransaction().commit();
    }

    private List<State> listStates() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from State").list();
        session.getTransaction().commit();
        return result;
    }

    private List<State> listStates(String name) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session
                .createQuery("from State where name = '" + name + "'").list();
        session.getTransaction().commit();
        return result;
    }*/
}