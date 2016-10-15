package net.bretti.modelcheck.api.ctl.atom;

import net.bretti.modelcheck.api.ctl.Formula;

public class True implements Formula {

    public static True True() {
        return new True();
    }

    @Override
    public Formula convertToCTLBase() {
        return this;
    }

    @Override
    public String toString() {
        return "true";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (getClass() == o.getClass());
    }

    @Override
    public int hashCode() {
        return 1;
    }

}
