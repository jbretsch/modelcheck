package example.packagename;

import net.bretti.modelcheck.api.ctl.Formula;
import net.bretti.modelcheck.api.ctlchecker.CTLModelChecker;
import net.bretti.modelcheck.api.transitionsystem.KripkeStructure;
import net.bretti.modelcheck.api.transitionsystem.State;

import static net.bretti.modelcheck.api.ctl.atom.Atom.atom;
import static net.bretti.modelcheck.api.ctl.operator.quantor.AG.AG;
import static net.bretti.modelcheck.api.ctl.operator.quantor.AU.AU;
import static net.bretti.modelcheck.api.ctl.operator.quantor.EF.EF;

public class ExampleMain {
    public static void main(String[] args) {

        State s0 = new State("s0", "a", "b");
        State s1 = new State("s1", "b");
        State s2 = new State("s2", "b");
        State s3 = new State("s3", "c");

        KripkeStructure kripkeStructure = new KripkeStructure();
        kripkeStructure.addInitialState(s0);
        kripkeStructure.addState(s1, s2, s3);
        kripkeStructure.addTransition(s0, s1);
        kripkeStructure.addTransition(s0, s2);
        kripkeStructure.addTransition(s1, s1);
        kripkeStructure.addTransition(s1, s2);
        kripkeStructure.addTransition(s2, s0);
        kripkeStructure.addTransition(s2, s3);
        kripkeStructure.addTransition(s3, s1);

        Formula formula1 = AG(EF(atom("a")));
        boolean f1Satisfied = CTLModelChecker.satisfies(kripkeStructure, formula1);
        System.out.println(f1Satisfied); // Outputs 'true'.

        Formula formula2 = AU(atom("b"), atom("c"));
        boolean f2Satisfied = CTLModelChecker.satisfies(kripkeStructure, formula2);
        System.out.println(f2Satisfied); // Outputs 'false'.
    }
}
