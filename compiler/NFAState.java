import java.util.HashMap;
import java.util.HashSet;

public class NFAState {
	private int id;
	private boolean accepting;
	private HashMap<String, HashSet<NFAState>> transitionSetMap;

	public NFAState(int id) {
		this.id = id;
		accepting = false;
		transitionSetMap = new HashMap<String, HashSet<NFAState>>();
	}

	public int getID() {
		return id;
	}

	public void addTransition(String character, NFAState nfaState) {
		if (transitionSetMap.get(character) == null) {
			transitionSetMap.put(character, new HashSet<NFAState>());
		}
		transitionSetMap.get(character).add(nfaState);
	}

	public HashSet<NFAState> getTransitionSet(String character) {
		HashSet<NFAState> transitionSet = transitionSetMap.get(character);
		return transitionSet == null ? new HashSet<NFAState>() : transitionSet;
	}

	public boolean isAcceptingNFAState() {
		return accepting;
	}

	public void makeAcceptingNFAState() {
		accepting = true;
	}

	@Override
	public String toString() {
		HashSet<NFAState> checkedNFAStateSet = new HashSet<NFAState>();
		return toString(checkedNFAStateSet);
	}

	private String toString(HashSet<NFAState> checkedNFAStateSet) {
		String string = "[" + id + "]\t";
		for (String character : transitionSetMap.keySet()) {
			string += " " + character + "->\t";
			for (NFAState nfaState : transitionSetMap.get(character)) {
				string += " " + nfaState.getID();
			}
		}
		string += "\n";
		for (String character : transitionSetMap.keySet()) {
			for (NFAState nfaState : transitionSetMap.get(character)) {
				if (!checkedNFAStateSet.contains(nfaState)) {
					checkedNFAStateSet.add(nfaState);
					string += nfaState.toString(checkedNFAStateSet);
				}
			}
		}
		return string;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object == this) return true;
		if (object instanceof NFAState) {
			NFAState that = (NFAState)object;
			return id == that.getID();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id;
	}
}