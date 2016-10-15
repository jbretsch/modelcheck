package net.bretti.modelcheck

import net.bretti.modelcheck.api.ctl.Formula
import net.bretti.modelcheck.api.transitionsystem.KripkeStructure
import net.bretti.modelcheck.api.transitionsystem.State
import spock.lang.Specification

import static net.bretti.modelcheck.api.ctl.operator.quantor.AF.AF
import static net.bretti.modelcheck.api.ctl.operator.quantor.AG.AG
import static net.bretti.modelcheck.api.ctl.operator.quantor.AU.AU
import static net.bretti.modelcheck.api.ctl.operator.quantor.AX.AX
import static net.bretti.modelcheck.api.ctl.operator.bool.And.and
import static net.bretti.modelcheck.api.ctl.atom.Atom.atom
import static net.bretti.modelcheck.api.ctl.operator.quantor.EF.EF
import static net.bretti.modelcheck.api.ctl.operator.quantor.EG.EG
import static net.bretti.modelcheck.api.ctl.operator.quantor.EU.EU
import static net.bretti.modelcheck.api.ctl.operator.quantor.EX.EX
import static net.bretti.modelcheck.api.ctl.operator.bool.Not.not
import static net.bretti.modelcheck.api.ctl.operator.bool.Or.or

import static net.bretti.modelcheck.api.ctlchecker.CTLModelChecker.satisfies

class CTLModelCheckerSpec extends Specification {
    def "atomic predicate in initial state"() {
        when: "we have a simple Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addFinalState(p2)
        kripkeStructure.addTransition(p1, p2)

        then:
        satisfies(kripkeStructure, atom("p1"))
        !satisfies(kripkeStructure, atom("p2"))
    }

    def "conjunction in of atomic predicate in initial state"() {
        when: "we have a simple Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addFinalState(p2)
        kripkeStructure.addTransition(p1, p2)

        then:
        satisfies(kripkeStructure, or(atom("p1"), atom("p2")))
        !satisfies(kripkeStructure, and(atom("p2"), atom("p1")))
    }

    def "boolean expression of atomic predicates in initial state"() {
        when: "we have a simple Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addFinalState(p2)
        kripkeStructure.addTransition(p1, p2)

        then:
        !satisfies(kripkeStructure, not(or(atom("p1"), atom("p2"))))
        satisfies(kripkeStructure, not(and(atom("p2"), atom("p1"))))
    }

    def "AX in initial state"() {
        when: "we have a simple Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        State p3 = new State("s3", "p3")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addFinalState(p2)
        kripkeStructure.addFinalState(p3)
        kripkeStructure.addTransition(p1, p2)
        kripkeStructure.addTransition(p1, p3)

        then:
        !satisfies(kripkeStructure, AX(atom("p1")))
        !satisfies(kripkeStructure, AX(atom("p2")))
        !satisfies(kripkeStructure, AX(atom("p3")))
        satisfies(kripkeStructure, AX(or(atom("p2"), atom("p3"))))
        !satisfies(kripkeStructure, AX(and(atom("p2"), atom("p3"))))
    }

    def "EX in initial state"() {
        when: "we have a simple Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        State p3 = new State("s3", "p3")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addFinalState(p2)
        kripkeStructure.addFinalState(p3)
        kripkeStructure.addTransition(p1, p2)
        kripkeStructure.addTransition(p1, p3)

        then:
        !satisfies(kripkeStructure, EX(atom("p1")))
        satisfies(kripkeStructure, EX(atom("p2")))
        satisfies(kripkeStructure, EX(atom("p3")))
        satisfies(kripkeStructure, EX(or(atom("p2"), atom("p3"))))
        !satisfies(kripkeStructure, EX(and(atom("p2"), atom("p3"))))
    }


    def "EU in initial state"() {
        when: "we have a simple Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        State p3 = new State("s3", "p3")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addFinalState(p2)
        kripkeStructure.addFinalState(p3)
        kripkeStructure.addTransition(p1, p2)
        kripkeStructure.addTransition(p1, p3)

        then:
        satisfies(kripkeStructure, EU(atom("p1"), atom("p2")))
        satisfies(kripkeStructure, EU(atom("p1"), atom("p3")))
        !satisfies(kripkeStructure, EU(atom("p1"), atom("p4")))
        !satisfies(kripkeStructure, EU(atom("p2"), atom("p3")))
    }

    def "AU in initial state"() {
        when: "we have a simple Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        State p3 = new State("s3", "p3")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addFinalState(p2)
        kripkeStructure.addFinalState(p3)
        kripkeStructure.addTransition(p1, p2)
        kripkeStructure.addTransition(p1, p3)

        then:
        satisfies(kripkeStructure, AU(atom("p1"), or(atom("p2"), atom("p3"))))
        !satisfies(kripkeStructure, AU(atom("p1"), atom("p2")))
        !satisfies(kripkeStructure, AU(atom("p1"), atom("p3")))
        !satisfies(kripkeStructure, AU(atom("p1"), atom("p4")))
        !satisfies(kripkeStructure, AU(atom("p2"), atom("p3")))
    }

    def "EF in initial state"() {
        when: "we have a simple Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        State p3 = new State("s3", "p3")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addFinalState(p2)
        kripkeStructure.addFinalState(p3)
        kripkeStructure.addTransition(p1, p2)
        kripkeStructure.addTransition(p1, p3)

        then:
        satisfies(kripkeStructure, EF(atom("p1")))
        satisfies(kripkeStructure, EF(atom("p2")))
        satisfies(kripkeStructure, EF(atom("p3")))
        !satisfies(kripkeStructure, EF(atom("p4")))
        !satisfies(kripkeStructure, EF(and(atom("p2"), atom("p3"))))
        satisfies(kripkeStructure, EF(or(atom("p2"), atom("p3"))))
    }

    def "EU and AU in double square Kripke structure"() {
        when: "we have a Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State s0 = new State("s0", "a")
        State s1 = new State("s1")
        State s2 = new State("s2", "a")
        State s3 = new State("s3", "a")
        State s4 = new State("s4")
        State s5 = new State("s5", "a")
        State s6 = new State("s6", "b")
        kripkeStructure.addInitialState(s0)
        kripkeStructure.addState(s1, s2, s3, s4, s5)
        kripkeStructure.addFinalState(s6)
        kripkeStructure.addTransition(s0, s1)
        kripkeStructure.addTransition(s0, s2)
        kripkeStructure.addTransition(s1, s3)
        kripkeStructure.addTransition(s2, s3)
        kripkeStructure.addTransition(s3, s4)
        kripkeStructure.addTransition(s3, s5)
        kripkeStructure.addTransition(s4, s6)
        kripkeStructure.addTransition(s5, s6)

        and: "a formula"
        Formula stateFormula = EU(EU(atom("a"), atom("b")), AU(atom("a"), atom("b")))

        then:
        satisfies(kripkeStructure, stateFormula)
    }

    def "EF in Kripke structure"() {
        when: "we have a Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State s0 = new State("s0", "a")
        State s1 = new State("s1")
        State s2 = new State("s2", "b")
        State s3 = new State("s3", "c")
        State s4 = new State("s4", "d")
        kripkeStructure.addInitialState(s0)
        kripkeStructure.addState(s1, s3)
        kripkeStructure.addFinalState(s2, s4)
        kripkeStructure.addTransition(s0, s1)
        kripkeStructure.addTransition(s0, s3)
        kripkeStructure.addTransition(s1, s2)
        kripkeStructure.addTransition(s3, s4)

        then:
        satisfies(kripkeStructure, EF(atom("b")))
        !satisfies(kripkeStructure, EF(and(atom("b"), atom("d"))))
    }

    def "AF in Kripke structure"() {
        when: "we have a Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State s0 = new State("s0", "a")
        State s1 = new State("s1")
        State s2 = new State("s2", "b")
        State s3 = new State("s3", "c")
        State s4 = new State("s4", "d", "e")
        kripkeStructure.addInitialState(s0)
        kripkeStructure.addState(s1, s3)
        kripkeStructure.addFinalState(s2, s4)
        kripkeStructure.addTransition(s0, s1)
        kripkeStructure.addTransition(s0, s3)
        kripkeStructure.addTransition(s1, s2)
        kripkeStructure.addTransition(s3, s4)

        then:
        satisfies(kripkeStructure, AF(or(atom("b"), atom("d"))))
        !satisfies(kripkeStructure, AF(or(atom("c"), atom("e"))))
    }

    def "AG and EG in Kripke structure"() {
        when: "we have a Kripke structure"
        KripkeStructure kripkeStructure = new KripkeStructure()
        State s0 = new State("s0", "a")
        State s1 = new State("s1", "a")
        State s2 = new State("s2", "b")
        State s3 = new State("s3", "a")
        State s4 = new State("s4", "c", "b")
        kripkeStructure.addInitialState(s0)
        kripkeStructure.addState(s1, s3)
        kripkeStructure.addFinalState(s2, s4)
        kripkeStructure.addTransition(s0, s1)
        kripkeStructure.addTransition(s0, s3)
        kripkeStructure.addTransition(s1, s2)
        kripkeStructure.addTransition(s3, s4)

        then:
        satisfies(kripkeStructure, AG(or(atom("a"), atom("b"))))
        !satisfies(kripkeStructure, AG(or(atom("a"), atom("c"))))
        satisfies(kripkeStructure, EG(or(atom("a"), atom("c"))))
        !satisfies(kripkeStructure, EG(or(atom("a"), atom("d"))))
    }

    def "Check (AFAG a) and (AFEG a) for Kripke structure that satisfies (FG a)"() {
        when: "we have a Kripke structure"
        State s0 = new State("s0", "a")
        State s1 = new State("s1")
        State s2 = new State("s2", "a")
        KripkeStructure kripkeStructure = new KripkeStructure()
        kripkeStructure.addInitialState(s0)
        kripkeStructure.addState(s1)
        kripkeStructure.addFinalState(s2)
        kripkeStructure.addTransition(s0, s0)
        kripkeStructure.addTransition(s0, s1)
        kripkeStructure.addTransition(s1, s2)

        then:
        !satisfies(kripkeStructure, AF(AG(atom("a"))))
        satisfies(kripkeStructure, AF(EG(atom("a"))))
    }

    def "Check (AFAG a) and (AFEG a) for Kripke structure that does not satisfy (FG a)"() {
        when: "we have a Kripke structure"
        State s0 = new State("s0", "a")
        State s1 = new State("s1")
        State s2 = new State("s2", "a")
        State s3 = new State("s3")
        KripkeStructure kripkeStructure = new KripkeStructure()
        kripkeStructure.addInitialState(s0)
        kripkeStructure.addState(s1)
        kripkeStructure.addFinalState(s2, s3)
        kripkeStructure.addTransition(s0, s0)
        kripkeStructure.addTransition(s0, s1)
        kripkeStructure.addTransition(s1, s2)
        kripkeStructure.addTransition(s1, s3)

        then:
        !satisfies(kripkeStructure, AF(AG(atom("a"))))
        satisfies(kripkeStructure, AF(EG(atom("a"))))
    }

    def "AGEF a (reset property) in simple looping Kripke structure"() {
        when: "we have simple looping Kripke structure"
        State s0 = new State("s0", "a")
        State s1 = new State("s1")
        State s2 = new State("s2")

        KripkeStructure kripkeStructure = new KripkeStructure()
        kripkeStructure.addInitialState(s0)
        kripkeStructure.addState(s1, s2)
        kripkeStructure.addTransition(s0, s1)
        kripkeStructure.addTransition(s0, s2)
        kripkeStructure.addTransition(s1, s1)
        kripkeStructure.addTransition(s1, s2)
        kripkeStructure.addTransition(s2, s0)

        then: "reset property is fulfilled"
        satisfies(kripkeStructure, AG(EF(atom("a"))))
    }

}
