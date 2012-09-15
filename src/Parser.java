import java.util.ArrayList;

//Assign tokens a line number for better error detection

public class Parser {
	
	//Tokens to parse (output from the lexical analyser)
	private ArrayList<Token> tokens;
	
	
	private ArrayList<VariableInfo> variables; //A list of all the variables created to keep track
	private int tokenIndex; //Index of the current token
	private ArrayList<String> keywords;
	private ArrayList<String> variableTypes;
	private Root root;
	private int lineCount;
	
	public Parser() {
		
		root = new Root();
		variables = new ArrayList<VariableInfo>();
		tokens = new ArrayList<Token>();
		setKeywords();
		setVariables();
	}
	
	//Set the tokens to parse
	public void setTokens(ArrayList<Token> lexOut) {
		
		tokens = lexOut;
		
		//Get line count (line number of the final token)
		if (tokens.size() > 0)
			lineCount = tokens.get(tokens.size()-1).line;
	}
	
	//Gets the current token that is being parsed
	private Token getCurrentToken() {
		try {
			return tokens.get(tokenIndex);
		}
		catch (Exception e) {
			System.out.println("Error - out of tokens");
			return null;
		}
		
	}
	
	private void setVariables() {

		variableTypes = new ArrayList<String>();
		
		variableTypes.add("INT");
		variableTypes.add("STRING");
		variableTypes.add("BOOL");
	}
	
	private void setKeywords() {

		keywords = new ArrayList<String>();
		
		keywords.add("PRINT");
		keywords.add("INPUT");
	}
	
	//Checks the variable list to see if a variable already exists
	public Boolean variableExists(String name) {
		
		for (VariableInfo var : variables) {
			if (var.name.equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	//Gets the type of the given variable name
	private VarType getVariableType(String name) {
		
		for (VariableInfo var : variables) {
			if (var.name.equals(name)) {
				return var.type;
			}
		}
		
		return VarType.Unknown;
	}
	
	//Checks if the name for a new variable is invalid
	private Boolean isNewVariableNameInvalid(String name) {
		
		//If the variable name is a keyword, it is invalid
		if (keywords.contains(name))
			return true;
		
		//If the variable already exists, it is invalid
		return (variableExists(name));
	}
	
	//Checks if we have unexpectedly run out of tokens
	private void checkIfOutOfTokens() {
		
		if (tokenIndex >= tokens.size())
			System.out.println("Error - program ended unexpectedly");
	}
	
	//Parse a facor in an expression
	//EBNF: <factor> ::= <IDENTIFIER> | <CONSTANT> | ( ‘(’, <expression>, ‘)’ ) | ( '-', <factor> );
	//We need to have the node where the factor is to be held
	//A the type so we know whether we are expecting an int or a string
	private Node parseFactor(Node node, VarType type) {
		
		//Check we have any tokens left
		checkIfOutOfTokens();

		//First check for a parenthesised expression
		//( ‘(’, <expression>, ‘)’ )
		if (getCurrentToken().value.equals("(")) {
			tokenIndex++;
			
			//Now get expression
			node = parseExpression(node, type);
			
			//Get the closing parenthesis
			if (!getCurrentToken().value.equals(")"))
				System.out.println("Expected closing bracket");
			
			tokenIndex++;
			return node;
		} 
		
		//If it's not a parenthesised expression
		//Check for a negative number
		//( '-', <factor> )
		if (type.equals(VarType.Integer)) {
			if (getCurrentToken().value.equals("-")) {
				tokenIndex++;
				node = parseFactor(node, type);
				
				//Apply the negative sign to the parsed factor
				node.value = "-" + node.value;
				return node;
			}
		}
		
		//It will be an <IDENTIFIER> or <CONSTANT>
		node = new Node();
		
		//Store the value in the node
		node.value = getCurrentToken().value;
		
		//Semantic analysis
		//For a variable, check if it exists
		if (getCurrentToken().type.equals(TokenType.Identifier) && !variableExists(getCurrentToken().value))
			System.out.println("Undeclared var");
	
		//Add some extra info to help code gen
		if (getCurrentToken().type.equals(TokenType.Identifier))
			node.attributes[0] = "VARIABLE";
		else
			node.attributes[1] = "VALUE";
		
		tokenIndex++;
		
		return node;
	}
	
	//To parse a term in the epression
	//In EBNF:
	//      <term> ::= <factor>, { ‘*’|’/’, <factor> };
	private Node parseTerm(Node node) {
		
		checkIfOutOfTokens();
		
		//Parse the first factor
		node = parseFactor(node, VarType.Integer);
		
		//Then the {...}
		//For each * or / parse another factor
		while (getCurrentToken().value.equals("*") || getCurrentToken().value.equals("/")) {
			
			Node clonedNode = node.getClone();
			node.left = clonedNode;
			node.value = getCurrentToken().value;
			tokenIndex++;
			node.right = parseFactor(node.right, VarType.Integer);
		}
		
		return node;
	}
	
	//Checks if a given string is a conditional operator
	private Boolean isConditionalOperator(String str) {
		
		return str.equals("==") || str.equals(">=") || str.equals("<=") || str.equals("!=");
	}
	
	//EBNF: <condition> ::= <expression> | <expression> '==' <expression>
	private Node parseCondition(Node node) {
		
		node = new Node();
		node.value = "CONDITION";
		
		node.left = parseExpression(node, VarType.Integer);

		//Check for anything that isn't a variable or value
		//If we don't find either, the condition has been parsed
		if (!getCurrentToken().type.equals(TokenType.Identifier) && !getCurrentToken().type.equals(TokenType.Number) && 
			!isConditionalOperator(getCurrentToken().value))
			return node;
		
		//Get the equality comparators
		node.attributes[0] = getCurrentToken().value;
		
		tokenIndex++;
		
		node.right = parseExpression(node, VarType.Integer);
		
		return node;
	}

	//To parse the expression
	//In EBNF:
    //           <expression> ::= <term>, { ‘+’|’-’, <term> };
	private Node parseExpression(Node node, VarType type) {

		checkIfOutOfTokens();
		
	    // A numeric expression may be made up of terms
	    // but a string expression is made up of string factors only :
	    //      we can't multiply or divide strings
		if (type.equals(VarType.Integer))
			node = parseTerm(node);
		else if (type.equals(VarType.Boolean))
  			node = parseCondition(node);
		else
			node = parseFactor(node, VarType.String);
		
		//Code is similar to parseTerm function
		while (getCurrentToken().value.equals("+") || (type == VarType.Integer && getCurrentToken().value.equals("-"))) {
			
			if (node == null)
				node = new Node();
			
			Node clonedNode = node.getClone();
			node.left = clonedNode;
			node.value = getCurrentToken().value;
			tokenIndex++;
			
			if (type == VarType.Integer)
				node.right = parseTerm(node.right);
			else
				node.right = parseFactor(node.right, VarType.String);
		}
		
		return node;
	}
	
	//Gets the type of the variable from a token's value
	private VarType getVariableTypeFromValue(String tokenValue) {
		
		if (tokenValue.equalsIgnoreCase("STRING"))
			return VarType.String;
		else if (tokenValue.equalsIgnoreCase("INT"))
			return VarType.Integer;
		else if (tokenValue.equalsIgnoreCase("BOOL"))
			return VarType.Boolean;
		
		return VarType.Unknown;
	}
	

	//To declare a variable
	//In EBNF:
	//	         <DECLARE> ::= ( 'INT' | 'STRING' | 'BOOL'), <IDENTIFIER>;
	private Node declareVariable(Node node) {
		
		node = new Node();
		node.value = "DECLARE";
		node.attributes[1] = getCurrentToken().value;
		tokenIndex++;
		checkIfOutOfTokens();
		node.attributes[0] = getCurrentToken().value;
		
		//Semantic part
		//Check of valid variable name
		if (!getCurrentToken().type.equals(TokenType.Identifier) || isNewVariableNameInvalid(getCurrentToken().value))
			System.out.println("Invalid variable name used in declaration");
		
		//Add the variable to list for future reference
		VariableInfo variableInfo = new VariableInfo();
		variableInfo.name = getCurrentToken().value;
		
		variableInfo.type = this.getVariableTypeFromValue(node.attributes[1]);		

		variables.add(variableInfo);
		
		tokenIndex++;
		
		return node;
	}
	
	// To assign expression to a varibale
	// In EBNF:
	//	       <ASSIGN> ::= <IDENTIFIER>, '=', <EXPRESSION>;
	private Node assignExpression(Node node) {
		
		node.value = "ASSIGN";
		
		node.left = new Node();
		node.left.value = getCurrentToken().value;
		
		//Skip through = symbol
		tokenIndex += 2;
		
		node.right = parseExpression(node.right, getVariableType(node.left.value));
		
		//Semantic part
		if (!variableExists(node.left.value))
			System.out.println("Undeclared var name used in assignment");

	    // Set node's attribute to the type of variable to aid in code generation
		if (getVariableType(node.left.value).equals(VarType.Integer))
			node.attributes[0] = "INT";
		else if (getVariableType(node.left.value).equals(VarType.Boolean))
			node.attributes[0] = "BOOL";
		else
			node.attributes[0] = "STRING";
		
	    // Note: we do not need to do NextToken as usual; this is because
	    // we are already a token ahead of the expression after ParseExpression is called
		
		return node;
	}
	
	// <PRINT> ::= 'PRINT', ( <IDENTIFIER> | <CONSTANT> );
	private Node parsePrintFunction(Node node) {
		
		node = new Node();
		node.value = "PRINT";
		tokenIndex++;
		checkIfOutOfTokens();
		node.attributes[0] = getCurrentToken().value;
		
		//Semantic part
		//Check for validity - argument should be variable or constant
		if (!getCurrentToken().type.equals(TokenType.Identifier) && !getCurrentToken().type.equals(TokenType.String)
			&& !getCurrentToken().type.equals(TokenType.Number))
			System.out.println("Invalid argument for PRINT statement");
		//And in case of variable, it must exist
		if (getCurrentToken().type.equals(TokenType.Identifier) && !variableExists(getCurrentToken().value))
			System.out.println("Undeclared variable name used as argument in PRINT");
		
		//Store some attributes to aid in code gen
		if (getCurrentToken().type.equals(TokenType.Number) || 
			getVariableType(getCurrentToken().value).equals(VarType.Integer))
			node.attributes[1] = "INT";
		else
			node.attributes[1] = "STRING";
		
		if (getCurrentToken().type.equals(TokenType.Identifier))
			node.attributes[2] = "VARIABLE";
		else
			node.attributes[2] = "VALUE";
		
		tokenIndex++;
		
		return node;
	}

	// <INPUT> ::= 'INPUT', <IDENTIFIER>
	private Node parseInputFunction(Node node) {
		
		node = new Node();
		node.value = "INPUT";
		tokenIndex++;
		checkIfOutOfTokens();
		node.attributes[0] = getCurrentToken().value;
		
		//Semantic part
		if (!getCurrentToken().type.equals(TokenType.Identifier))
			System.out.println("Invalid argument for input statement");
		
		if (!variableExists(getCurrentToken().value))
			System.out.println("Undeclared variable name used as argument in INPUT");
		
		if (getVariableType(getCurrentToken().value).equals(VarType.Integer))
			node.attributes[1] = "INT";
		else
			node.attributes[1] = "STRING";
		
		tokenIndex++;
		
		return node;
	}
	
	//Currently only basic if statements are supported
	//EBNF: <IFSTATEMENT> :== 'IF', <condition>, 'THEN', <statement>, 'END'
	private Node parseIfStatement(Node node) {
		node = new Node();
		node.value = "IF-THEN";
		
		node.left = new Node();
		node.left.value = "IF";
		
		tokenIndex++;
		
		//The first node is the condition
		node.left.left = parseCondition(node.left.left);
		
		//Skip the 'THEN' and EOL
		tokenIndex+= 2;
		
		node.left.right = new Node();
		node.left.right.value = "THEN";
		
		node.left.right.left = parseStatement(node.left.right.left);
		
		tokenIndex++;
		
		//Not implemented
		//Else might be here, in which case assign the right node to the statements
		if (getCurrentToken().value.equals("ELSE")) {
			//works
			
		}
		//node.left.right.right = new Node();
		//node.left.right.right.value = "ELSE";
		
		return node;
	}
	
	//Parse a new statement
	private Node parseStatement(Node node) {
		
		//Call appropriate parse function according to first token in the statement
	
		if (variableTypes.contains(getCurrentToken().value))
			node = declareVariable(node);
		else if (getCurrentToken().value.equals("INPUT"))
			node = parseInputFunction(node);
		else if (getCurrentToken().value.equals("PRINT"))
			node = parsePrintFunction(node);
		else if (getCurrentToken().type.equals(TokenType.Identifier) && tokens.get(tokenIndex+1).value.equals("="))
			node = assignExpression(node);
		else if (getCurrentToken().value.equals("IF"))
			node = parseIfStatement(node);
		else
			System.out.println("INVALID STATEMENT on line " + getCurrentToken().line + ": " + getCurrentToken().value);
		
		return node;
	}
	
	public void parseProgram() {
		
		//Last token must be EOL so add one in case
		Token eol = new Token();
		eol.line = tokens.get(tokens.size()-1).line + 1;
		eol.type = TokenType.EOL;
		eol.value = "\n";
		tokens.add(eol);
		
		//Start at the first token
		tokenIndex = 0;
		
		for (int line = 0; line < lineCount; line++) {

			Node currentNode = new Node();
			
			if (!getCurrentToken().type.equals(TokenType.EOL)) {
				
				if (line == lineCount - 1) {
					currentNode = parseStatement(currentNode);
					root.insertFinal(currentNode);
				}
				else {
					root.insert("STATEMENTS", parseStatement(currentNode));
				}
			}
			
			tokenIndex++;
		}
		
		root.nullifyEmptyChildren();
	}
	
	private void displayTree(Node node, String inner) {
		
		if (node == null)
			return;
		
		System.out.print(inner + ">" + node.value);
		
		String attrib = "";
		
		for (String attribute : node.attributes)
			if (attribute != "")
				attrib += " " + attribute;
				
		if (attrib != "")
			System.out.print("  (Attributes: " + attrib + ")");
		
		System.out.print("\n");
		
		displayTree(node.left, inner + "-");
		displayTree(node.right, inner + "-");
	}
	
	public void displayOutput() {
		
		displayTree(root, "");
	}
}
