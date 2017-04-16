import java.io.Serializable;

/**
 * Created by Adonay on 4/13/2017.
 */
public class Node implements Serializable{

    Node parentNode = null;
    Node leftNode = null;
    Node rightNode = null;
    char character;
    int frequency;

    public Node(char c, int freq){
        character = c;
        frequency = freq;
    }

    public Node(int freq){
        frequency = freq;
        character  = Character.MIN_VALUE;
    }


    public Node getParentNode() {
        return parentNode;
    }

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    public Node getLeftNode() {
        return leftNode;
    }

    public void setLeftNode(Node leftNode) {
        this.leftNode = leftNode;
    }

    public Node getRightNode() {
        return rightNode;
    }

    public void setRightNode(Node rightNode) {
        this.rightNode = rightNode;
    }


}
