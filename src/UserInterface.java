
public class UserInterface {

	public static void printLogo() {
		System.out.println("  __________");
		System.out.println(" /  ______  \\");
		System.out.println("/  /______\\  \\");
		System.out.println("\\   _________/");
		System.out.println(" \\  \\________");
		System.out.println("  \\_________/");
	}
	
	public static void printIntroduction() {
		System.out.println("Welcome to the Esper Programming Language");
	}
	
	public static void printMenu() {
		System.out.println("+ Menu");
		System.out.println("1. Enter code");
		System.out.println("2. Use file");
		System.out.println("3. Use default");
		System.out.println("4. Help");
	}
	
	public static void printHelpMenu() {
		System.out.println("+ Menu > Help");
		System.out.println("1. Output");
		System.out.println("2. Input");
	}
	
	public static void printOutputExample() {
		System.out.println("+ Menu > Help > Output");
		System.out.println("PRINT \"Hello World\"");
		System.out.println("PRINT 3");
	}
	
	public static void printInputExample() {
		System.out.println("+ Menu > Help > Input");
		System.out.println("INT a");
		System.out.println("INPUT a");
		System.out.println("STRING b");
		System.out.println("INPUT b");
	}
}
