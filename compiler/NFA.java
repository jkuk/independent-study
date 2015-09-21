import java.util.ArrayDeque;

public class NFA {
	private ArrayDeque<State> stateSequence;

	public NFA(State first, State last) {
		stateSequence = new ArrayDeque<State>();
		stateSequence.addLast(first);
		stateSequence.addLast(last);
	}

	public void addFirstState(State first) {
		stateSequence.addFirst(first);
	}

	public void addLastState(State last) {
		stateSequence.addLast(last);
	}

	public State getFirstState() {
		return stateSequence.getFirst();
	}

	public State getLastState() {
		return stateSequence.getLast();
	}

	public State pop() {
		return stateSequence.pop();
	}

	@Override
	public String toString() {
		return "placeholder";
	}
}