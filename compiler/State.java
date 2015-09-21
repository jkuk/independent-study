import java.util.HashMap;

public class State {
	private int id;
	private boolean accepting;
	private HashMap<String, State> transitionMap;

	public State(int id) {
		this.id = id;
		accepting = false;
		transitionMap = new HashMap<String, State>();
	}

	public int getID() {
		return id;
	}

	public void addTransition(String character, State state) {
		transitionMap.put(character, state);
	}

	public State getTransition(String character) {
		return transitionMap.get(character);
	}

	public boolean isAcceptingState() {
		return accepting;
	}

	public void makeAcceptingState() {
		accepting = true;
	}

	@Override
	public String toString() {
		return "placeholder";
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object == this) return true;
		if (object instanceof State) {
			State that = (State)object;
			return id == that.getID();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id;
	}
}