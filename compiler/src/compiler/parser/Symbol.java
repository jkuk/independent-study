package compiler.parser;

public abstract class Symbol {
	private String type;

	public Symbol(String type) {
		this.type = type.trim();
	}

	public String getType() {
		return type;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object == this) return true;
		if (object instanceof Symbol) {
			Symbol that = (Symbol)object;
			return type.equals(that.getType());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return type;
	}
}
