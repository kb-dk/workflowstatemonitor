package dk.statsbiblioteket.infra.workflowstatemonitor;

import org.hibernate.Session;

import java.util.Date;
import java.util.List;

public class StateRegistrar {

    public static void main(String[] args) {
        StateRegistrar registrar = new StateRegistrar();

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
    }
}