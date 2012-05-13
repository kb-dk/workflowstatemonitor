package dk.statsbiblioteket.infra.workflowstatemonitor;

import java.util.List;

/** Handling a set of timestamped, named states of named components for named entities. */
public interface StateManager {
    /**
     * Add a state for an entity. Will add the state to the list of registered states for that entity.
     *
     * @param entity The name of the entity.
     * @param state  The state.
     */
    public void addState(String entity, State state);

    /**
     * List all entities with registered states.
     *
     * @return A list of all entities.
     */
    public List<Entity> listEntities();

    /**
     * List all registered states for the given entity.
     *
     * @param entityName What to list states for.
     * @return A list of all registered states.
     */
    public List<State> listStates(String entityName);

    /**
     * List subset of registered states given by parameters.
     *
     * @param onlyLast If true, only list the newest registered state for each entity.
     * @param includes If not null, only list states with one of the given names.
     * @param excludes If not null, only list states without one of the given names.
     * @return A list of all registered states.
     */
    public List<State> listStates(boolean onlyLast, List<String> includes, List<String> excludes);
}
