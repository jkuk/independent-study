package lexer;

import java.util.ArrayDeque;
import java.util.HashSet;

public class NFA {
	private static int idCounter;
	private static HashSet<Character> alphabet;

	private NFAState firstState;
	private NFAState lastState;

	public static NFA build(String postfixExpression) {
		idCounter = 0;
		alphabet = new HashSet<Character>();

		ArrayDeque<NFA> nfaStack = new ArrayDeque<NFA>();
		Character character = null;
		Character previousCharacter = null;
		for (int i = 0; i < postfixExpression.length(); i++) {
			character = postfixExpression.charAt(i);
			Operator input = new Operator(character);
			if (character == LexerConstants.ESCAPE_CHARACTER) {
				character = postfixExpression.charAt(++i);
				nfaStack.push(generateCharacterNFA(character));
			}
			else if (input.isOperator()) {
				nfaStack.push(generateOperatorNFA(character, nfaStack));
			}
			else {
				nfaStack.push(generateCharacterNFA(character));
			}
			previousCharacter = character;
		}
		nfaStack.peek().getLastState().makeAcceptingNFAState();
		return nfaStack.pop();
	}

	private NFA(NFAState firstState, NFAState lastState) {
		this.firstState = firstState;
		this.lastState = lastState;
	}

	public void setFirstState(NFAState firstState) {
		this.firstState = firstState;
	}
	
	public void setLastState(NFAState lastState) {
		this.lastState = lastState;
	}

	public NFAState getFirstState() {
		return firstState;
	}

	public NFAState getLastState() {
		return lastState;
	}

	public HashSet<Character> getAlphabet() {
		return alphabet;
	}

	private static NFA generateCharacterNFA(Character character) {
		alphabet.add(character);

		NFAState firstState = new NFAState(idCounter++);
		NFAState lastState = new NFAState(idCounter++);

		firstState.addTransition(character + "", lastState);

		NFA nfa = new NFA(firstState, lastState);
		return nfa;
	}

	private static NFA generateOperatorNFA(
		Character operator, ArrayDeque<NFA> nfaStack
	) {
		if (operator == LexerConstants.KLEENE) {
			return kleene(nfaStack.pop());
		}
		else if (operator == LexerConstants.CONCATENATE) {
			return concatenate(nfaStack.pop(), nfaStack.pop());
		}
		else {
			return alternate(nfaStack.pop(), nfaStack.pop());
		}
	}

	private static NFA kleene(NFA a) {
		NFAState firstState = new NFAState(idCounter++);
		NFAState lastState = new NFAState(idCounter++);

		firstState.addTransition(LexerConstants.EPSILON, a.getFirstState());
		firstState.addTransition(LexerConstants.EPSILON, lastState);
		a.getLastState().addTransition(LexerConstants.EPSILON, a.getFirstState());
		a.getLastState().addTransition(LexerConstants.EPSILON, lastState);

		return new NFA(firstState, lastState);
	}

	private static NFA concatenate(NFA b, NFA a) {
		a.getLastState().addTransition(LexerConstants.EPSILON, b.getFirstState());

		return new NFA(a.getFirstState(), b.getLastState());
	}

	private static NFA alternate(NFA b, NFA a) {
		NFAState firstState = new NFAState(idCounter++);
		NFAState lastState = new NFAState(idCounter++);

		firstState.addTransition(LexerConstants.EPSILON, a.getFirstState());
		firstState.addTransition(LexerConstants.EPSILON, b.getFirstState());
		a.getLastState().addTransition(LexerConstants.EPSILON, lastState);
		b.getLastState().addTransition(LexerConstants.EPSILON, lastState);

		return new NFA(firstState, lastState);
	}

	@Override
	public String toString() {
		return firstState.toString();
	}
}