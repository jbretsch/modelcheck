package net.bretti.modelcheck.api.ctl.operator.quantor;

import net.bretti.modelcheck.api.ctl.Formula;

import java.util.Objects;

import static net.bretti.modelcheck.api.ctl.atom.True.True;
import static net.bretti.modelcheck.api.ctl.operator.quantor.EU.EU;

public class EF implements Formula {

    private final Formula operand;

    public static EF EF(Formula operand) {
        return new EF(operand);
    }

    public EF(Formula operand) {
        this.operand = operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EF not = (EF) o;
        return Objects.equals(operand, not.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }

    @Override
    public String toString() {
        return "EF " + operand;
    }

    @Override
    public Formula convertToCTLBase() {
        return EU(True(), operand).convertToCTLBase();
    }
}
