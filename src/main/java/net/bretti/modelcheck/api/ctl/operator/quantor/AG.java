package net.bretti.modelcheck.api.ctl.operator.quantor;

import net.bretti.modelcheck.api.ctl.Formula;

import java.util.Objects;

import static net.bretti.modelcheck.api.ctl.operator.bool.Not.not;
import static net.bretti.modelcheck.api.ctl.operator.quantor.EF.EF;

public class AG implements Formula {

    private final Formula operand;

    public static AG AG(Formula operand) {
        return new AG(operand);
    }

    public AG(Formula operand) {
        this.operand = operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AG not = (AG) o;
        return Objects.equals(operand, not.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }

    @Override
    public String toString() {
        return "AG " + operand;
    }

    @Override
    public Formula convertToCTLBase() {
        return not(EF(not(operand))).convertToCTLBase();
    }
}
