//To-do
//Case insensitive?

class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Lexer lexer = new Lexer();
		//lexer.source = "bool b\nb=2+6==3/2\nbool c\nc=b\nbool d\nd=c==1\n";
		lexer.source = "PRINT 3\nIF 1 == 2 + 3 * 9 THEN\nPRINT 4\nELSE\nPRINT 2\nEND\nPRINT 1\nbool b\nb=1<=2\n";
		
		System.out.println("-Code-");
		System.out.println(lexer.source);
		
		lexer.Analyse();
		
		System.out.println("-Lexical Analysis-");
		
		for (Token t : lexer.tokens) {
			t.print();
			System.out.print(" ");
		
		}
		System.out.println("\n");
		
		System.out.println("-Parsing-");
		
		Parser parser = new Parser();
		parser.setTokens(lexer.tokens);
		parser.parseProgram();
		parser.displayOutput();
	}

}
