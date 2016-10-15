package net.bretti.modelcheck.api.ctl.operator.quantor;

import net.bretti.modelcheck.api.ctl.Formula;

import java.util.Objects;

import static net.bretti.modelcheck.api.ctl.atom.True.True;
import static net.bretti.modelcheck.api.ctl.operator.quantor.AU.AU;

public class AF implements Formula {

    private final Formula operand;

    public static AF AF(Formula operand) {
        return new AF(operand);
    }

    public AF(Formula operand) {
        this.operand = operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AF not = (AF) o;
        return Objects.equals(operand, not.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }

    @Override
    public String toString() {
        return "AF " + operand;
    }

    @Override
    public Formula convertToCTLBase() {
        return AU(True(), operand).convertToCTLBase();
    }
}
