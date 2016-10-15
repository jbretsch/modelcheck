package net.bretti.modelcheck

import net.bretti.modelcheck.api.transitionsystem.KripkeStructure
import net.bretti.modelcheck.api.transitionsystem.State
import net.bretti.modelcheck.api.transitionsystem.exceptions.KripkeStructureInvalidException
import spock.lang.Specification

class KripkeStructureSpec extends Specification {
    def "test valid Kripke structure"() {
        when:
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addFinalState(p2)
        kripkeStructure.addTransition(p1, p2)

        then:
        kripkeStructure.isValid()

        and:
        kripkeStructure.validate()
    }

    def "test invalid Kripke structure (transitions not left total)"() {
        when:
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        kripkeStructure.addInitialState(p1)
        kripkeStructure.addState(p2)
        kripkeStructure.addTransition(p1, p2)

        then:
        !kripkeStructure.isValid()

        when:
        kripkeStructure.validate()

        then:
        KripkeStructureInvalidException e = thrown()
        e.getMessage() == "There is no transition starting from state s2."
    }

    def "test invalid Kripke structure (initial states empty)"() {
        when:
        KripkeStructure kripkeStructure = new KripkeStructure()
        State p1 = new State("s1", "p1")
        State p2 = new State("s2", "p2")
        kripkeStructure.addState(p1)
        kripkeStructure.addState(p2)
        kripkeStructure.addTransition(p1, p2)

        then:
        !kripkeStructure.isValid()

        when:
        kripkeStructure.validate()

        then:
        KripkeStructureInvalidException e = thrown()
        e.getMessage() == "Set of initial states is empty."
    }
}
