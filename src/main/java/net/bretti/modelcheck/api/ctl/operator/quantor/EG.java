package net.bretti.modelcheck.api.ctl.operator.quantor;

import net.bretti.modelcheck.api.ctl.Formula;

import java.util.Objects;

import static net.bretti.modelcheck.api.ctl.operator.bool.Not.not;
import static net.bretti.modelcheck.api.ctl.operator.quantor.AF.AF;

public class EG implements Formula {

    private final Formula operand;

    public static EG EG(Formula operand) {
        return new EG(operand);
    }

    public EG(Formula operand) {
        this.operand = operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EG not = (EG) o;
        return Objects.equals(operand, not.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }

    @Override
    public String toString() {
        return "EG " + operand;
    }

    @Override
    public Formula convertToCTLBase() {
        return not(AF(not(operand))).convertToCTLBase();
    }
}
