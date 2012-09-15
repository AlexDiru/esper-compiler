
public class Root extends Node {

	public Node currentNode;
	
	public Root() {
		
		super();
		currentNode = this;
	}
	
	public void insert(String val, Node leftNode) {
		
		currentNode.value = val;
		currentNode.left = new Node();
		currentNode.right = new Node();
		currentNode.left = leftNode;
		currentNode = currentNode.right;
		
	}
	
	public void insertFinal(Node node) {
		
		currentNode.value = "STATEMENTS";
		currentNode.left = node;
	}
	
}
