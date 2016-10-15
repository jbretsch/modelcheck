package net.bretti.modelcheck.api.ctl.operator.bool;

import net.bretti.modelcheck.api.ctl.Formula;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class And implements Formula {

    private Set<Formula> operands = new HashSet<>();

    public And(Formula... operands) {
        Collections.addAll(this.operands, operands);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        And and = (And) o;
        return Objects.equals(operands, and.operands);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operands);
    }

    public static And and(Formula... operands) {
        return new And(operands);
    }

    @Override
    public String toString() {
        return "(" + operands.stream().map(Formula::toString).collect(Collectors.joining(" AND ")) + ")";
    }

    public Set<Formula> getOperands() {
        return new HashSet<>(operands);
    }

    @Override
    public Formula convertToCTLBase() {
        return and(operands.stream().map(Formula::convertToCTLBase).collect(Collectors.toList()).toArray(new Formula[]{}));
    }
}
