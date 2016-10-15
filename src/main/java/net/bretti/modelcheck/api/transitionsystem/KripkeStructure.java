package net.bretti.modelcheck.api.transitionsystem;

import net.bretti.modelcheck.api.transitionsystem.exceptions.KripkeStructureInvalidException;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KripkeStructure {

    private Set<State> states = new HashSet<>();
    private Set<State> initialStates = new HashSet<>();
    private Map<State, Set<State>> transitions = new HashMap<>();

    public void addState(State... states) {
        Collections.addAll(this.states, states);
    }

    public void addInitialState(State... states) {
        addState(states);
        Collections.addAll(this.initialStates, states);
    }

    public void addFinalState(State... states) {
        addState(states);
        for (State state : states) {
            addTransition(state, state);
        }
    }

    public void addTransition(State src, State dst) {
        transitions.merge(src, Collections.singleton(dst), (oldValue, newValue) -> {
            HashSet<State> mergeValue = new HashSet<>(oldValue);
            mergeValue.addAll(newValue);
            return mergeValue;
        });
    }

    public boolean isValid() {
        try {
            validate();
        } catch (RuntimeException e) {
            return false;
        }
        return true;
    }

    public void validate() {
        if (states.isEmpty()) {
            throw new KripkeStructureInvalidException("Set of states is empty.");
        }

        if (initialStates.isEmpty()) {
            throw new KripkeStructureInvalidException("Set of initial states is empty.");
        }

        validateTransitionsAreLeftTotal();
    }

    public Set<State> getInitialStates() {
        return new HashSet<>(initialStates);
    }

    public Set<State> getAllSuccessorStates(State state) {
        return new HashSet<>(transitions.get(state));
    }

    private void validateTransitionsAreLeftTotal() {
        states.forEach(state -> {
            if (transitions.getOrDefault(state, new HashSet<>()).isEmpty()) {
                throw new KripkeStructureInvalidException(
                        String.format("There is no transition starting from state %s.", state));
            }
        });
    }

}
