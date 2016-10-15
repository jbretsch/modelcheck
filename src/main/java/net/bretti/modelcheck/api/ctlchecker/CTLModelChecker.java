package net.bretti.modelcheck.api.ctlchecker;

import net.bretti.modelcheck.api.ctl.Formula;
import net.bretti.modelcheck.api.ctl.atom.Atom;
import net.bretti.modelcheck.api.ctl.atom.False;
import net.bretti.modelcheck.api.ctl.atom.True;
import net.bretti.modelcheck.api.ctl.operator.bool.And;
import net.bretti.modelcheck.api.ctl.operator.bool.Not;
import net.bretti.modelcheck.api.ctl.operator.bool.Or;
import net.bretti.modelcheck.api.ctl.operator.quantor.AU;
import net.bretti.modelcheck.api.ctl.operator.quantor.AX;
import net.bretti.modelcheck.api.ctl.operator.quantor.EU;
import net.bretti.modelcheck.api.ctl.operator.quantor.EX;
import net.bretti.modelcheck.api.transitionsystem.KripkeStructure;
import net.bretti.modelcheck.api.transitionsystem.State;
import net.bretti.modelcheck.impl.CheckAUResult;
import net.bretti.modelcheck.impl.CheckEUResult;
import net.bretti.modelcheck.impl.TarjansDepthFirstSearchData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.bretti.modelcheck.impl.SearchContinuation.ABORT;
import static net.bretti.modelcheck.impl.SearchContinuation.CONTINUE;

public class CTLModelChecker {

    private static final Logger logger = LoggerFactory.getLogger(CTLModelChecker.class);

    private final KripkeStructure kripkeStructure;

    private Map<State, Map<Formula, Boolean>> labels = new HashMap<>();

    public CTLModelChecker(KripkeStructure kripkeStructure) {
        this.kripkeStructure = kripkeStructure;
    }

    public static boolean satisfies(KripkeStructure kripkeStructure, Formula formula) {
        return new CTLModelChecker(kripkeStructure).satisfies(formula);
    }

    public boolean satisfies(Formula formula) {
        kripkeStructure.validate();
        logger.debug("Starting to check whether the given Kripke structure satisifies {}.", formula);
        Formula formulaBase = formula.convertToCTLBase();
        logger.debug("Converted given formula to formula in our CTL base using tautologies:  {}.", formulaBase);
        return kripkeStructure.getInitialStates().stream().allMatch(initialState -> satisfies(initialState, formulaBase));
    }

    private boolean addLabel(State state, Formula formula, boolean value) {
        Map<Formula, Boolean> labelsForState = labels.computeIfAbsent(state, k -> new HashMap<>());

        labelsForState.put(formula, value);
        logger.debug("Labelled: (" + state + ", " + formula + ") -> " + value);
        return value;
    }

    private boolean satisfies(State state, Formula formula) {
        return getLabel(state, formula).orElseGet(() -> computeLabel(state, formula));
    }

    private Optional<Boolean> getLabel(State state, Formula formula) {
        return Optional.ofNullable(labels.getOrDefault(state, new HashMap<>()).get(formula));
    }

    private boolean computeLabel(State state, Formula formula) {
        if (formula instanceof True) {
            return addLabel(state, formula, true);
        }

        if (formula instanceof False) {
            return addLabel(state, formula, false);
        }

        if (formula instanceof Atom) {
            return addLabel(state, formula, state.satisfies((Atom) formula));
        }

        if (formula instanceof Or) {
            for (Formula subFormula: ((Or) formula).getOperands()) {
                if (satisfies(state, subFormula)) {
                    return addLabel(state, formula, true);
                }
            }

            return addLabel(state, formula, false);
        }

        if (formula instanceof And) {
            for (Formula subFormula: ((And) formula).getOperands()) {
                if (!satisfies(state, subFormula)) {
                    return addLabel(state, formula, false);
                }
            }

            return addLabel(state, formula, true);
        }

        if (formula instanceof Not) {
            return addLabel(state, formula, !satisfies(state, ((Not) formula).getOperand()));
        }

        if (formula instanceof AX) {
            Formula subFormula = ((AX) formula).getOperand();
            for (State successorState: kripkeStructure.getAllSuccessorStates(state)) {
                if (!satisfies(successorState, subFormula)) {
                    return addLabel(state, formula, false);
                }
            }

            return addLabel(state, formula, true);
        }

        if (formula instanceof EX) {
            Formula subFormula = ((EX) formula).getOperand();
            for (State successorState: kripkeStructure.getAllSuccessorStates(state)) {
                if (satisfies(successorState, subFormula)) {
                    return addLabel(state, formula, true);
                }
            }

            return addLabel(state, formula, false);
        }

        if (formula instanceof EU) {
            CheckEUResult checkEUResult = checkEU(state, (EU) formula);
            boolean isFormulaSatisfied = getLabel(state, formula).get();
            if (isFormulaSatisfied) {
                logger.debug("EU: Found witness path for {} starting from {}: {}.", formula,
                        state, checkEUResult.getWitnessPath());
            } else {
                logger.debug("EU: Found no witness path for {} starting from {}.", formula, state);
            }
            return isFormulaSatisfied;
        }

        if (formula instanceof AU) {
            CheckAUResult checkAUResult = checkAU(state, (AU) formula);
            boolean isFormulaSatisfied = getLabel(state, formula).get();
            if (!isFormulaSatisfied) {
                logger.debug("AU: Found counter example for {} starting from {}: {}.", formula,
                        state, checkAUResult.getCounterExample());
            } else {
                logger.debug("AU: Found no counter example for {} starting from {}.", formula, state);
            }
            return isFormulaSatisfied;
        }

        throw new IllegalArgumentException(formula.toString());
    }

    private CheckEUResult checkEU(State state, EU formula) {
        TarjansDepthFirstSearchData dfsData = new TarjansDepthFirstSearchData();
        return checkEU(state, formula, dfsData);
    }

    private CheckEUResult checkEU(State state, EU formula, TarjansDepthFirstSearchData dfsData) {
        Formula op1 = formula.getOperand1();
        Formula op2 = formula.getOperand2();

        Optional<Boolean> label = getLabel(state, formula);
        if (label.isPresent()) {
            if (label.get()) {
                return new CheckEUResult(ABORT, state);
            } else {
                return new CheckEUResult(CONTINUE, null);
            }
        }

        if (satisfies(state, op2)) {
            logger.debug("EU: {} satisfies {}. So {} also satisfies {}.", state, op2, state, formula);
            addLabel(state, formula, true);
            return new CheckEUResult(ABORT, state);
        }

        if (!satisfies(state, op1)) {
            logger.debug("EU: {} does not satisfy {}. So {} does not satisfy {} either.", state, op1, state, formula);
            addLabel(state, formula, false);
            return new CheckEUResult(CONTINUE, null);
        }

        logger.debug("EU: Initialize state label with true");
        addLabel(state, formula, true);
        dfsData.visit(state);
        logger.debug("EU: Visiting {}. dfs={}; lowlink={}, maxDfs={}; stack={}", state, dfsData.getDfs(state),
                dfsData.getDfs(state), dfsData.getMaxDfs(), dfsData.getStackAsString());

        logger.debug("EU: Starting to check all successors of {}.", state);
        for (State successorState: kripkeStructure.getAllSuccessorStates(state)) {
            logger.debug("EU: Starting to check {} as successor of {}.", successorState, state);
            if (!dfsData.isVisited(successorState)) {
                logger.debug("EU: {} was never visited. Starting checkEU({}, {}).", successorState, successorState, formula);
                CheckEUResult checkEUResult = checkEU(successorState, formula, dfsData);
                if (checkEUResult.getSearchContinuation() == ABORT) {
                    checkEUResult.prependWitnessPathWith(state);
                    return checkEUResult;
                }
                dfsData.setLowLink(state, Math.min(dfsData.getLowLink(state), dfsData.getLowLink(successorState)));
            } else {
                logger.debug("EU: {} has already been visited.", successorState);
                if (dfsData.isOnStack(successorState)) {
                    dfsData.setLowLink(state, Math.min(dfsData.getLowLink(state), dfsData.getDfs(successorState)));
                }
            }
        }

        if (dfsData.dfsEqualsLowLink(state)) {
            logger.debug("EU: Found {} to be root of strongly connected component. But no path from any successor of " +
                    "{} is witness for {}. So no path from {} can be witness either. Labelling all states on stack {}" +
                    " until {} with false.", state, state, formula, state, dfsData.getStackAsString(),
                    state);
            State stateFromStack;
            do {
                stateFromStack = dfsData.popFromStack();
                addLabel(stateFromStack, formula, false);
            } while (!state.equals(stateFromStack));
        }

        return new CheckEUResult(CONTINUE, null);
    }

    private CheckAUResult checkAU(State state, AU formula) {
        TarjansDepthFirstSearchData dfsData = new TarjansDepthFirstSearchData();
        return checkAU(state, formula, dfsData);
    }

    private CheckAUResult checkAU(State state, AU formula, TarjansDepthFirstSearchData dfsData) {
        Formula op1 = formula.getOperand1();
        Formula op2 = formula.getOperand2();

        dfsData.visit(state);
        logger.debug("AU: Visiting {}. dfs={}; lowlink={}, maxDfs={}; stack={}", state, dfsData.getDfs(state),
                dfsData.getDfs(state), dfsData.getMaxDfs(), dfsData.getStackAsString());

        Optional<Boolean> label = getLabel(state, formula);
        if (label.isPresent()) {
            if (label.get()) {
                return new CheckAUResult(CONTINUE, null);
            } else {
                return new CheckAUResult(ABORT, state);
            }
        }

        if (satisfies(state, op2)) {
            logger.debug("AU: {} satisfies {}. So {} also satisfies {}.", state, op2, state, formula);
            addLabel(state, formula, true);
            return new CheckAUResult(CONTINUE, null);
        }

        if (!satisfies(state, op1)) {
            logger.debug("AU: {} does not satisfy {}. So {} does not satisfy {} either.", state, op1, state, formula);
            addLabel(state, formula, false);
            return new CheckAUResult(ABORT, state);
        }

        logger.debug("AU: Initialize state label with false");
        addLabel(state, formula, false);
        for (State successorState : kripkeStructure.getAllSuccessorStates(state)) {
            logger.debug("AU: Starting to check {} as successor of {}.", successorState, state);
            if (!dfsData.isVisited(successorState)) {
                logger.debug("AU: {} was never visited. Starting checkAU({}, {}).", successorState, successorState, formula);
                CheckAUResult checkAUResult = checkAU(successorState, formula, dfsData);
                if (checkAUResult.getSearchContinuation() == ABORT) {
                    checkAUResult.prependCounterExampleWith(state);
                    return checkAUResult;
                }
            } else {
                logger.debug("AU: {} has already been visited.", successorState);
                if (dfsData.isOnStack(successorState)) {
                    logger.debug("AU: {} is on Tarjan's dfs stack. Aborting depth first search.", successorState);
                    return new CheckAUResult(ABORT, successorState);
                }
            }
        }

        dfsData.removeFromStack(state);
        addLabel(state, formula, true);
        return new CheckAUResult(CONTINUE, null);
    }

}
