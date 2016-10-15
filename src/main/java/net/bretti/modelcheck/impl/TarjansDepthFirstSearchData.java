package net.bretti.modelcheck.impl;

import net.bretti.modelcheck.api.transitionsystem.State;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Holds data used for performing <a href="https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm">
 * Tarjan's Depth First Search algorithm</a> to search for strongly connected components.
 */
public class TarjansDepthFirstSearchData {

    /**
     * Tarjan's current maximum depth first search index.
     */
    private int maxDfs = 0;

    /**
     * Contains the depth first search index of each already visited state.
     */
    private Map<State, Integer> dfs = new HashMap<>();

    /**
     * Contains the lowlink value of each already visited state.
     */
    private Map<State, Integer> lowlink = new HashMap<>();

    /**
     * Tarjan's depth first search stack.
     */
    private Stack<State> stack = new Stack<>();

    public void visit(State state) {
        stack.push(state);
        setDfs(state, maxDfs);
        setLowLink(state, maxDfs);
        maxDfs++;
    }

    public boolean isVisited(State state) {
        return dfs.containsKey(state);
    }

    public int getDfs(State state) {
        return dfs.getOrDefault(state, 0);
    }

    public boolean dfsEqualsLowLink(State state) {
        return getDfs(state) == getLowLink(state);
    }

    public void setLowLink(State state, int lowlink) {
        this.lowlink.put(state, lowlink);
    }

    public int getLowLink(State state) {
        return lowlink.getOrDefault(state, 0);
    }

    public boolean isOnStack(State state) {
        return stack.contains(state);
    }

    public State popFromStack() {
        return stack.pop();
    }

    public void removeFromStack(State state) {
        stack.remove(state);
    }

    public int getMaxDfs() {
        return maxDfs;
    }

    public String getStackAsString() {
        return stack.toString();
    }

    private void setDfs(State state, int dfs) {
        this.dfs.put(state, dfs);
    }
}
