import java.util.ArrayList;

//Creation aided by Bibek Dahal's C++ compiler tutorial available at http://learntocompile.blogspot.co.uk

public class Lexer {
	
	public ArrayList<Token> tokens;
	public String source; //The code being parsed
	private int sourceIndex;
	private int currentLine;
	
	public Lexer() {
		tokens = new ArrayList<Token>();
		source = "";
		sourceIndex = 0;
		currentLine = 1;
	}
	
	//Sets the code to be analysed
	public void setCode(String code) {
		source = code;
	}
	
	//Converts the code into tokens
	public void Analyse() {
		//Have to add another token to the source as it doesn't analyse the last token
		source += " 3";
		
		//Keep trying to read tokens until an exception
		try {
			//Loop until exception (when a non-existant token is accessed)
			while (1 == 1)
				tokens.add(readNextToken());
		}
		catch (Exception ex) 
		{
		}
	}
	
	//Gets the current char of the source
	private char getSourceChar() {
		
		return source.charAt(sourceIndex);
	}
	
	//Gets next char of the source
	private char getNextSourceChar() {
		if (sourceIndex+1 < source.length())
			return source.charAt(sourceIndex+1);
		else {
			System.out.println("Error - no more characters detected");
			return '\0';
		}
	}
	
	//Skips spaces tabs and comments
	@SuppressWarnings("deprecation")
	private void skipWhiteSpace() {
		
		//Skip spaces until a new line character
		while (Character.isSpace(getSourceChar()))
		{
			if (getSourceChar() == '\n')
			{
				currentLine++;
				break;
			}
			sourceIndex++;
		}
		
		//Skip the rest of the line if a comment is detected
		if (getSourceChar() == '/' && getNextSourceChar() == '/') {
			while (getSourceChar() != '\n')
				sourceIndex++;
		}
	}
	
	//Get the number type of token
	private Token getNumberToken() {
		Token token = new Token();
		
		do {
			token.value += getSourceChar();
			sourceIndex++;
		} while (Character.isDigit(getSourceChar()));
		
		token.type = TokenType.Number;
		
		return token;
	}
	
	//Get the string type of token
	private Token getStringToken() {
		
		sourceIndex++; //Skip past the first quote mark
		
		Token token = new Token();
		
		do {
			if (getSourceChar() == '\n') //Can't end a line in the middle of a string
				System.out.println("Error - exptected ending quotation mark");
			
			token.value += getSourceChar();
			sourceIndex++;
		} while (getSourceChar() != '\"'); //Scan until the end quote mark
		
		sourceIndex++;
		token.type = TokenType.String;
		
		return token;
	}
	
	//Get the symbol type of token
	private Token getSymbolToken() {
		
		Token token = new Token();
		
		token.value += getSourceChar();
		
		//In case of <=, >=, == or != we have two characters as token
		if ((getSourceChar() == '<' || getSourceChar() == '>' || getSourceChar() == '!' || getSourceChar() == '=') && getNextSourceChar() == '=') {
			sourceIndex++;
			token.value += getSourceChar();
		}
		
		sourceIndex++;
		token.type = TokenType.Symbol;
		
		return token;
	}
	
	//Gets the identifier type of token
	private Token getIdentifierToken() {
		
		Token token = new Token();
		
		do {
			token.value += Character.toUpperCase(getSourceChar());
			sourceIndex++;
		} while (Character.isLetterOrDigit(getSourceChar()) || getSourceChar() == '_');
		
		token.type = TokenType.Identifier;
		return token;
	}
	
	//Whether the input character is a punctuation
	private static Boolean isPunctuation(char c) {
		return !(Character.isLetterOrDigit(c) || c == ' ');
	}
	
	private Token readNextToken() {
		
		//Start by skipping redundant data
		skipWhiteSpace();
		
		Token nextToken = null;
		
		//If the first character is EOL
		if (getSourceChar() == '\n') {
			nextToken = new Token();
			nextToken.type = TokenType.EOL;
			nextToken.line = currentLine - 1;
			sourceIndex++;
			return nextToken;
		}
		else if (Character.isAlphabetic(getSourceChar())) {
			nextToken = getIdentifierToken();
		}
		else if (Character.isDigit(getSourceChar())) {
			nextToken = getNumberToken();
		}
		else if (getSourceChar() == '\"') {
			nextToken = getStringToken();
		}
		else if (isPunctuation(getSourceChar())) {
			nextToken = getSymbolToken();
		}
		else {
			System.out.println("Error - undefined token");
		}
		
		nextToken.line = currentLine;
		return nextToken;
	}
}
