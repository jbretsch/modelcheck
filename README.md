# modelcheck

[![Maven Central](https://img.shields.io/maven-central/v/net.bretti.modelcheck/modelcheck?color=brightgreen)](https://maven-badges.herokuapp.com/maven-central/net.bretti.modelcheck/modelcheck)
[![License](https://img.shields.io/github/license/jbretsch/modelcheck?color=brightgreen)](https://github.com/jbretsch/modelcheck/blob/master/LICENSE)

`modelcheck` is a Java library that allows you to check whether a given transition system (described as a
[Kripke structure](https://en.wikipedia.org/wiki/Kripke_structure_\(model_checking\))) satisfies a given
[computation tree logic (CTL)](https://en.wikipedia.org/wiki/Computation_tree_logic) formula. For this task `modelcheck`
uses an explicit labelling algorithm coupled with
[Tarjan's strongly connected components algorithm](https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm).

`modelcheck` can, as of now, not compete with state of the art explicit state exploration model checking tools. Its
main benefit lies in being an easily accessible _library_ anybody can integrate in their own software. If you search
for a powerful model checking tool for [Petri nets](https://en.wikipedia.org/wiki/Petri_net), I suggest taking a look at
[LoLA: A Low Level Petri Net Analyzer](https://theo.informatik.uni-rostock.de/theo-forschung/werkzeuge/) created by
[Professor Karsten Wolf](https://theo.informatik.uni-rostock.de/karsten-wolf/).

## Example

Suppose you have the following Kripke structure.

![Example Kripke structure](http://g.gravizo.com/svg?digraph%20G%20{%20rankdir=%22LR%22;%20node%20[shape=%22circle%22];%20secret_node%20[style=invisible,width=.05,fixedsize=true]%20s0[label=%22s0\na,b%22];%20s1[label=%22s1\nb%22];%20s2[label=%22s2\nb%22];%20s3[label=%22s3\nc%22];%20secret_node%20-%3E%20s0;%20s0%20-%3E%20s1;%20s0%20-%3E%20s2;%20s1%20-%3E%20s1;%20s1%20-%3E%20s2;%20s2%20-%3E%20s0;%20s2%20-%3E%20s3;%20s3%20-%3E%20s1;%20})

`s0` is the initial state. In state `s0` holds proposition `a`; in states `s0`, `s1`, and `s2` holds proposition `b`;
and in state `s3` holds proposition `c`.

Suppose you are interested in checking whether this Kripke structure satisfies the following CTL formulae:

1. AG EF a - Starting from every state that is reachable from any initial state, exists a path to a state that satisfies
proposition `a`.
2. A(b U c) - On all paths starting from all initial states, only states that satisfy proposition `b` are encountered
until a state is reached that satisfies proposition `c`.

Using the `modelcheck` library you could write the following Java program to check whether the described Kripke
structure satisfies the CTL formulae listed above.

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

The program's output would be

    true
    false

## Logging

If you configure your logging facility to output `DEBUG` messages for `net.bretti.modelcheck` you will get much more
detailed messages outlining the model checking process. This includes witness paths for the EU operator and counter
example paths for the AU operator. (All other CTL operators are transformed to EU or AU using tautologies.)

The above program would have logged:

    Starting to check whether the given Kripke structure satisifies AG EF a.
    Converted given formula to formula in our CTL base using tautologies:  NOT (E(true U NOT (E(true U a)))).
    Labelled: (s0, a) -> true
    EU: s0 satisfies a. So s0 also satisfies E(true U a).
    Labelled: (s0, E(true U a)) -> true
    EU: Found witness path for E(true U a) starting from s0: [s0].
    Labelled: (s0, NOT (E(true U a))) -> false
    Labelled: (s0, true) -> true
    EU: Initialize state label with true
    Labelled: (s0, E(true U NOT (E(true U a)))) -> true
    EU: Visiting s0. dfs=0; lowlink=0, maxDfs=1; stack=[s0]
    EU: Starting to check all successors of s0.
    EU: Starting to check s1 as successor of s0.
    EU: s1 was never visited. Starting checkEU(s1, E(true U NOT (E(true U a)))).
    Labelled: (s1, a) -> false
    Labelled: (s1, true) -> true
    EU: Initialize state label with true
    Labelled: (s1, E(true U a)) -> true
    EU: Visiting s1. dfs=0; lowlink=0, maxDfs=1; stack=[s1]
    EU: Starting to check all successors of s1.
    EU: Starting to check s1 as successor of s1.
    EU: s1 has already been visited.
    EU: Starting to check s2 as successor of s1.
    EU: s2 was never visited. Starting checkEU(s2, E(true U a)).
    Labelled: (s2, a) -> false
    Labelled: (s2, true) -> true
    EU: Initialize state label with true
    Labelled: (s2, E(true U a)) -> true
    EU: Visiting s2. dfs=1; lowlink=1, maxDfs=2; stack=[s1, s2]
    EU: Starting to check all successors of s2.
    EU: Starting to check s3 as successor of s2.
    EU: s3 was never visited. Starting checkEU(s3, E(true U a)).
    Labelled: (s3, a) -> false
    Labelled: (s3, true) -> true
    EU: Initialize state label with true
    Labelled: (s3, E(true U a)) -> true
    EU: Visiting s3. dfs=2; lowlink=2, maxDfs=3; stack=[s1, s2, s3]
    EU: Starting to check all successors of s3.
    EU: Starting to check s1 as successor of s3.
    EU: s1 has already been visited.
    EU: Starting to check s0 as successor of s2.
    EU: s0 was never visited. Starting checkEU(s0, E(true U a)).
    EU: Found witness path for E(true U a) starting from s1: [s1, s2, s0].
    Labelled: (s1, NOT (E(true U a))) -> false
    EU: Initialize state label with true
    Labelled: (s1, E(true U NOT (E(true U a)))) -> true
    EU: Visiting s1. dfs=1; lowlink=1, maxDfs=2; stack=[s0, s1]
    EU: Starting to check all successors of s1.
    EU: Starting to check s1 as successor of s1.
    EU: s1 has already been visited.
    EU: Starting to check s2 as successor of s1.
    EU: s2 was never visited. Starting checkEU(s2, E(true U NOT (E(true U a)))).
    Labelled: (s2, NOT (E(true U a))) -> false
    EU: Initialize state label with true
    Labelled: (s2, E(true U NOT (E(true U a)))) -> true
    EU: Visiting s2. dfs=2; lowlink=2, maxDfs=3; stack=[s0, s1, s2]
    EU: Starting to check all successors of s2.
    EU: Starting to check s3 as successor of s2.
    EU: s3 was never visited. Starting checkEU(s3, E(true U NOT (E(true U a)))).
    Labelled: (s3, NOT (E(true U a))) -> false
    EU: Initialize state label with true
    Labelled: (s3, E(true U NOT (E(true U a)))) -> true
    EU: Visiting s3. dfs=3; lowlink=3, maxDfs=4; stack=[s0, s1, s2, s3]
    EU: Starting to check all successors of s3.
    EU: Starting to check s1 as successor of s3.
    EU: s1 has already been visited.
    EU: Starting to check s0 as successor of s2.
    EU: s0 has already been visited.
    EU: Starting to check s2 as successor of s0.
    EU: s2 has already been visited.
    EU: Found s0 to be root of strongly connected component. But no path from any successor of s0 is witness for E(true U NOT (E(true U a))). So no path from s0 can be witness either. Labelling all states on stack [s0, s1, s2, s3] until s0 with false.
    Labelled: (s3, E(true U NOT (E(true U a)))) -> false
    Labelled: (s2, E(true U NOT (E(true U a)))) -> false
    Labelled: (s1, E(true U NOT (E(true U a)))) -> false
    Labelled: (s0, E(true U NOT (E(true U a)))) -> false
    EU: Found no witness path for E(true U NOT (E(true U a))) starting from s0.
    Labelled: (s0, NOT (E(true U NOT (E(true U a))))) -> true
    true
    Starting to check whether the given Kripke structure satisifies A(b U c).
    Converted given formula to formula in our CTL base using tautologies:  A(b U c).
    AU: Visiting s0. dfs=0; lowlink=0, maxDfs=1; stack=[s0]
    Labelled: (s0, c) -> false
    Labelled: (s0, b) -> true
    AU: Initialize state label with false
    Labelled: (s0, A(b U c)) -> false
    AU: Starting to check s1 as successor of s0.
    AU: s1 was never visited. Starting checkAU(s1, A(b U c)).
    AU: Visiting s1. dfs=1; lowlink=1, maxDfs=2; stack=[s0, s1]
    Labelled: (s1, c) -> false
    Labelled: (s1, b) -> true
    AU: Initialize state label with false
    Labelled: (s1, A(b U c)) -> false
    AU: Starting to check s1 as successor of s1.
    AU: s1 has already been visited.
    AU: s1 is on Tarjan's dfs stack. Aborting depth first search.
    AU: Found counter example for A(b U c) starting from s0: [s0, s1].
    false

`modelcheck` uses SLF4J to log messages.

