enum TokenType {
	Unknown,
	EOL, //end of line \n
	Number,
	String, //"Hello world"
	Symbol, //+ - , | *
	Identifier,
}

public class Token {
	TokenType type;
	String value;
	int line;
	
	//Sets the value to nothing
	public Token() {
		value = "";
	}
	
	//Prints the token (for debug)
	public void print() {
		String output = "";
		
		switch (type) {
		case Unknown:
			output += "?(";
			break;
		case EOL:
			output += "EOL(";
			break;
		case Number:
			output += "NUM(";
			break;
		case String:
			output += "STR(";
			break;
		case Symbol:
			output += "SYM(";
			break;
		default:
			output += "ID(";
			break;
		}
		
		output += value + ", " + line + ")";
		
		System.out.print(output);
	}
}


