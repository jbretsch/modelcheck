package net.bretti.modelcheck.api.ctl.atom;

import net.bretti.modelcheck.api.ctl.Formula;

import java.util.Objects;

public class Atom implements Formula {
    private final String atomicPredicate;

    public Atom(String atomicPredicate) {
        this.atomicPredicate = atomicPredicate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atom that = (Atom) o;
        return Objects.equals(atomicPredicate, that.atomicPredicate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(atomicPredicate);
    }

    public static Atom atom(String atomicPredicate) {
        return new Atom(atomicPredicate);
    }

    @Override
    public String toString() {
        return atomicPredicate;
    }

    @Override
    public Formula convertToCTLBase() {
        return this;
    }
}
