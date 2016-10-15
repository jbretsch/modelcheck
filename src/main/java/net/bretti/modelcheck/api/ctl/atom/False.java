package net.bretti.modelcheck.api.ctl.atom;

import net.bretti.modelcheck.api.ctl.Formula;

public class False implements Formula {

    public static False False() {
        return new False();
    }

    @Override
    public Formula convertToCTLBase() {
        return this;
    }

    @Override
    public String toString() {
        return "false";
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (getClass() == o.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }

}
