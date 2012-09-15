//A node of the parser tree
public class Node {
	public String value; //Value of the node
	public String[] attributes = new String[3]; //Each node has 3 attributes
	public Node left, right; //The two child nodes
	
	//Set the the variables by null
	public Node() {
		left = null;
		right = null;
		attributes[0] = "";
		attributes[1] = "";
		attributes[2] = "";
		value = "";
	}
	
	//Deletes a node and all its children
	public static void DeleteTree(Node root) {
		if (root.left != null)
			DeleteTree(root.left);
		
		if (root.right != null)
			DeleteTree(root.right);
		
		root = null;
	}
	
	public void nullifyEmptyChildren() {
		if (left != null)	{
			left.nullifyEmptyChildren();
			if (left.isEmpty())
				left = null;
		}
		
		if (right != null) {
			right.nullifyEmptyChildren();
			if (right.isEmpty())
				right = null;
		}
	}
	
	//If the node has any data
	public Boolean isEmpty() {
		
		return left == null && right == null && attributes[0].equals("") && attributes[1].equals("") && attributes[2].equals("") && value.equals("");
	}
	
	public Node getClone() {
		Node clone = new Node();
		clone.left = left;
		clone.right = right;
		clone.value = value;
		clone.attributes = attributes;
		
		return clone;
	}
}

