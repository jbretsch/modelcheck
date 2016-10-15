package net.bretti.modelcheck

import spock.lang.Specification

import static net.bretti.modelcheck.api.ctl.operator.quantor.AG.AG
import static net.bretti.modelcheck.api.ctl.operator.quantor.AU.AU
import static net.bretti.modelcheck.api.ctl.operator.bool.And.and
import static net.bretti.modelcheck.api.ctl.atom.Atom.atom
import static net.bretti.modelcheck.api.ctl.operator.quantor.EF.EF
import static net.bretti.modelcheck.api.ctl.operator.quantor.EU.EU
import static net.bretti.modelcheck.api.ctl.operator.bool.Not.not
import static net.bretti.modelcheck.api.ctl.operator.bool.Or.or

class StateFormulaSpec extends Specification {
    def "AND: simple equals"() {
        when:
        true

        then:
        and(atom("p1"), atom("p2")) == and(atom("p2"), atom("p1"))
        and(atom("p1"), atom("p2")) != and(atom("p1"), atom("p1"))
    }

    def "OR: simple equals"() {
        when:
        true

        then:
        or(atom("p1"), atom("p2")) == or(atom("p2"), atom("p1"))
        or(atom("p1"), atom("p2")) != or(atom("p1"), atom("p1"))
    }

    def "NOT: simple equals"() {
        when:
        true

        then:
        not(atom("p1")) == not(atom("p1"))
        not(atom("p1")) != not(atom("p2"))
    }

    def "test toString"() {
        when:
        true

        then:
        AG(EF(atom("p1"))).toString() == "AG EF p1"
        AU(EU(or(and(atom("p1"), atom("p2")), atom("p3")), atom("p4")), atom("p5")).toString() == "A(E(((p1 AND p2) OR p3) U p4) U p5)"
    }

    def "test convert to CTL base"() {
        when:
        true

        then:
        AG(or(atom("a"), atom("b"))).convertToCTLBase().toString() == "NOT (E(true U NOT ((a OR b))))"
    }
}
