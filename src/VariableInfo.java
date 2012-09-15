enum VarType {
	Integer,
	String,
	Boolean,
	Unknown,
}

//Stores information about a variable
public class VariableInfo {
	
	String name;
	VarType type;
	
	public VariableInfo() {
		name = "";
	}
}
