import java.util.HashSet;
import java.util.HashMap;

public class DFA {
	private HashMap<
		HashSet<State>, HashMap<String, HashSet<State>>
	> transitionMap;

	public DFA() {
		transitionMap = new HashMap<
			HashSet<State>, HashMap<String, HashSet<State>>
		>();
	}

	public void put(
		HashSet<State> dfaState,
		String character,
		HashSet<State> dfaTransitionState
	) {
		if (transitionMap.get(dfaState) == null) {
			transitionMap.put(
				dfaState, new HashMap<String, HashSet<State>>()
			);
		}
		transitionMap.get(dfaState).put(character, dfaTransitionState);
	}

	@Override
	public String toString() {
		return "placeholder";
	}
}