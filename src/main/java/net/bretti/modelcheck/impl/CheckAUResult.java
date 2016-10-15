package net.bretti.modelcheck.impl;

import net.bretti.modelcheck.api.transitionsystem.State;

import java.util.ArrayList;

public class CheckAUResult {
    private SearchContinuation searchContinuation;
    private ArrayList<State> counterExample;

    public CheckAUResult(SearchContinuation searchContinuation, State lastStateInCounterExample) {
        this.searchContinuation = searchContinuation;
        this.counterExample = new ArrayList<>();
        this.counterExample.add(lastStateInCounterExample);
    }

    public SearchContinuation getSearchContinuation() {
        return searchContinuation;
    }

    public ArrayList<State> getCounterExample() {
        return counterExample;
    }

    public void prependCounterExampleWith(State state) {
        counterExample.add(0, state);
    }
}
