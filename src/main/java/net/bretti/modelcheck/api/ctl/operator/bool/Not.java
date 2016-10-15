package net.bretti.modelcheck.api.ctl.operator.bool;

import net.bretti.modelcheck.api.ctl.Formula;

import java.util.Objects;

public class Not implements Formula {

    private final Formula operand;

    public Not(Formula operand) {
        this.operand = operand;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Not not = (Not) o;
        return Objects.equals(operand, not.operand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operand);
    }

    public static Formula not(Formula op) {
        return new Not(op);
    }


    @Override
    public String toString() {
        return "NOT (" + operand + ")";
    }

    public Formula getOperand() {
        return operand;
    }

    @Override
    public Formula convertToCTLBase() {
        return not(operand.convertToCTLBase());
    }
}
