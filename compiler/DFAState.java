import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayDeque;

public class DFAState {
	private HashSet<NFAState> nfaStateSet;
	private HashMap<String, DFAState> transitionMap;

	public DFAState(HashSet<NFAState> nfaStateSet) {
		this.nfaStateSet = nfaStateSet;
		transitionMap = new HashMap<String, DFAState>();
	}

	public DFAState() {
		this(new HashSet<NFAState>());
	}

	public HashSet<NFAState> getNFAStateSet() {
		return nfaStateSet;
	}

	public void addTransition(String character, DFAState dfaTransitionState) {
		transitionMap.put(character, dfaTransitionState);
	}

	public DFAState getTransition(String character) {
		return transitionMap.get(character);
	}

	private boolean addNFAState(NFAState nfaState) {
		return nfaStateSet.add(nfaState);
	}

	public DFAState closure(String character) {
		// Constants.print(nfaStateSet.toString());
		DFAState reachableStateSet = character.equals(Constants.EPSILON) ?
		new DFAState(nfaStateSet) : new DFAState();
		ArrayDeque<NFAState> uncheckedStateStack = new ArrayDeque<NFAState>();
		uncheckedStateStack.addAll(nfaStateSet);

		while (!uncheckedStateStack.isEmpty()) {
			NFAState startState = uncheckedStateStack.pop();
			HashSet<NFAState> transitionStateSet
			= startState.getTransitionSet(character);
			for (NFAState transitionState : transitionStateSet) {
				if (reachableStateSet.addNFAState(transitionState)) {
					uncheckedStateStack.push(transitionState);
				}
			}
		}
		return reachableStateSet;
	}

	public boolean isAcceptingDFAState() {
		for (NFAState nfaState : nfaStateSet) {
			if (nfaState.isAcceptingNFAState()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		HashSet<DFAState> checkedDFAStateSet = new HashSet<DFAState>();
		return toString(checkedDFAStateSet);
	}

	private String toString(HashSet<DFAState> checkedDFAStateSet) {
		String string = dfaStateToString();

		for (String character : transitionMap.keySet()) {
			string += " " + character + "->\t"
			+ transitionMap.get(character).dfaStateToString();
		}
		string += "\n";
		for (String character : transitionMap.keySet()) {
			DFAState dfaState = transitionMap.get(character);
			if (!checkedDFAStateSet.contains(dfaState)) {
				checkedDFAStateSet.add(dfaState);
				string += dfaState.toString(checkedDFAStateSet);
			}
		}
		return string;
	}

	private String dfaStateToString() {
		String string = "[";
		for (NFAState nfaState : nfaStateSet) {
			string += " " + nfaState.getID();
		}
		return string + "]\t";
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object == this) return true;
		if (object instanceof DFAState) {
			DFAState that = (DFAState)object;
			return nfaStateSet.equals(that.getNFAStateSet());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return nfaStateSet.hashCode();
	}
}