package compiler.lexer;
import compiler.lexer.NFA;
import compiler.lexer.DFAState;

import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayDeque;

public class DFA {
	private DFAState firstState;

	public static DFA build(NFA nfa) {
		HashMap<DFAState, DFAState> dfaStateMap
		= new HashMap<DFAState, DFAState>();
		DFA dfa = new DFA(nfa, dfaStateMap);

		DFAState uncheckedDFAState = dfa.getFirstState();
		ArrayDeque<DFAState> uncheckedDFAStateStack
		= new ArrayDeque<DFAState>();
		uncheckedDFAStateStack.push(uncheckedDFAState);

		HashSet<DFAState> checkedDFAStateSet = new HashSet<DFAState>();

		while (!uncheckedDFAStateStack.isEmpty()) {
			uncheckedDFAState = uncheckedDFAStateStack.pop();
			for (Character character : nfa.getAlphabet()) {
				DFAState transitionState
				= uncheckedDFAState.closure(character + "");
				transitionState
				= transitionState.closure(LexerConstants.EPSILON);

				if (dfaStateMap.get(transitionState) == null) {
					dfaStateMap.put(transitionState, transitionState);
				}
				else {
					transitionState = dfaStateMap.get(transitionState);
				}

				if (!transitionState.getNFAStateSet().isEmpty()) {
					uncheckedDFAState.addTransition(
						character + "", transitionState
					);
					if (!checkedDFAStateSet.contains(transitionState)) {
						uncheckedDFAStateStack.push(transitionState);
					}
				}
				checkedDFAStateSet.add(uncheckedDFAState);
			}
		}
		return dfa;
	}

	private DFA(NFA nfa, HashMap<DFAState, DFAState> dfaStateMap) {
		HashSet<NFAState> nfaStateSet = new HashSet<NFAState>();
		nfaStateSet.add(nfa.getFirstState());
		firstState = new DFAState(nfaStateSet).closure(LexerConstants.EPSILON);
		dfaStateMap.put(firstState, firstState);
	}

	public DFAState getFirstState() {
		return firstState;
	}

	@Override
	public String toString() {
		return firstState.toString();
	}
}
