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
package dk.statsbiblioteket.medieplatform.workflowstatemonitor;

import java.util.Date;
import java.util.List;

/** Handling a set of timestamped, named states of named components for named entities. */
public interface StateManager {
    /**
     * Add a state for an entity. Will add the state to the list of registered states for that entity.
     *
     * @param entityName      The name of the entity.
     * @param state           The state.
     * @param preservedStates List of names of states, that should not be overridden. If the last state is one of these
     *                        states, the given state swill still be added, but another state with that name will then
     *                        also be added with the current timestamp. List may be null or empty, if no states should
     *                        be preserved.
     * @return                List of states added. May be just the given state, or both the given state and a preserved
     *                        readded state.
     */
    public List<State> addState(String entityName, State state, List<String> preservedStates);

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
     * @param onlyLast   If true, only list the newest registered state for the entity.
     * @param includes   If not null, only list states with one of the given names.
     * @param excludes   If not null, only list states without one of the given names.
     * @param startDate  If not null, only list states with timestamp after this date.
     * @param endDate    If not null, only list states with timestamp before this date.
     * @return A list of all registered states.
     */
    public List<State> listStates(String entityName, boolean onlyLast, List<String> includes, List<String> excludes,
                                  Date startDate, Date endDate);

    /**
     * List subset of registered states given by parameters.
     *
     * @param onlyLast  If true, only list the newest registered state for each entity.
     * @param includes  If not null, only list states with one of the given names.
     * @param excludes  If not null, only list states without one of the given names.
     * @param startDate If not null, only list states with timestamp after this date.
     * @param endDate   If not null, only list states with timestamp before this date.
     * @return A list of all registered states.
     */
    public List<State> listStates(boolean onlyLast, List<String> includes, List<String> excludes, Date startDate,
                                  Date endDate);
}
