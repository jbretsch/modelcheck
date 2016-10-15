package net.bretti.modelcheck.api.ctl.operator.quantor;

import net.bretti.modelcheck.api.ctl.Formula;

import java.util.Objects;

public class AX implements Formula {

    private final Formula operand;

    public static AX AX(Formula operand) {
        return new AX(operand);
    }

    public AX(Formula operand) {
        this.operand = operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AX not = (AX) o;
        return Objects.equals(operand, not.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }

    @Override
    public String toString() {
        return "AX " + operand;
    }

    public Formula getOperand() {
        return operand;
    }

    @Override
    public Formula convertToCTLBase() {
        return AX(operand.convertToCTLBase());
    }
}
