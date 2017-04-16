import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.CookieHandler;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class HuffmanTest {
	static Scanner scanner = new Scanner(System.in);
	static int programMode = 0; // 1 is encode and 2 is decode
	String inputFilePath;
	String outputFilePath;
	StringBuilder builder = new StringBuilder();
	String inputFileContent;
	int treeHeight = 0;
	Node treeHeadNode = null;
	String codeMapFileName = "HuffmanCodeMap";
	String decodedString = "";
	int endProgram = 0;
	LinkedList<String> bitLinkedList = new LinkedList<String>();
	int numberOfBitsWritten;

	HashMap<Character, Integer> frequencyMap = new HashMap<Character, Integer>();
	LinkedList<Character> charactersFound = new LinkedList<>();

	HashMap<Character, String> huffmanCodeMap = new HashMap<Character, String>();

	public static void main(String args[]) {
		HuffmanTest compressor = new HuffmanTest();
		System.out.println("Welcome to the Huffman Compressor program!");

		while (compressor.endProgram != 1) {
			compressor.programMode = 0;
			while (compressor.programMode != 1 && compressor.programMode != 2) {
				System.out.println("Select which mode you would like to use:\n\t1 - Encode\n\t2 - Decode\n");
				compressor.programMode = scanner.nextInt();
			}

			scanner.nextLine();

			if (compressor.programMode == 1) {
				System.out.println("Encoding Started");
				compressor.encode();
				System.out.println("Encoding Finished");
			} else {
				System.out.println("Decoding Started");
				compressor.decode();
				System.out.println("Decoding Finished");
			}

			System.out.println(
					"Would you like to encoding and decoding again.\n\tEnter 1 to end program\n\tEnter any other number to continue using the program");
			compressor.endProgram = scanner.nextInt();
			scanner.nextLine();
		}

		/*
		 * Node a = new Node('a', 3); Node g = new Node('g', 5); Node h = new
		 * Node('h', 8); Node e = new Node('e', 10); Node b = new Node('b', 20);
		 * Node f = new Node('f', 20); Node d = new Node('d', 50); Node c = new
		 * Node('c', 100);
		 * 
		 * LinkedList<Node> lod = new LinkedList<Node>();
		 * 
		 * lod.add(a); lod.add(g); lod.add(h); lod.add(e); lod.add(b);
		 * lod.add(f); lod.add(d); lod.add(c);
		 * 
		 * Node headNode = compressor.huffmanTreeBuilder(lod);
		 * System.out.println("\nFinal total or frequency in head node is " +
		 * headNode.frequency + "\n");
		 * 
		 * Integer[] arr = new Integer[compressor.treeHeight];
		 * 
		 * compressor.huffmanCodeAssigner(headNode, arr, 0);
		 */

	}

	public void getInput(int mode) {
		String line = "";
		int integerBit;
		inputFileContent = "";
		inputFilePath = "";
		outputFilePath = "";	
		builder = new StringBuilder();

		System.out.println("Enter the path to input file");
		inputFilePath = scanner.nextLine();
		System.out.println("Enter the path to output file");
		outputFilePath = scanner.nextLine();

		try {
			FileReader fileReader = new FileReader(inputFilePath);

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			if (mode == 1) {
				while ((line = bufferedReader.readLine()) != null) {
					builder.append(line + "\n");
				}
				builder.deleteCharAt(builder.length() - 1);
				inputFileContent = builder.toString();
				bufferedReader.close();
			} else if(mode == 2) {
				FileInputStream fstream = new FileInputStream(inputFilePath);
				BitInputStream bitStream = new BitInputStream(fstream);

				while ((integerBit = bitStream.read()) != -1) {
					bitLinkedList.add(String.valueOf(integerBit));
				}				
				bitStream.close();
			}
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + inputFilePath + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + inputFilePath + "'");
		}

	}

	public void encode() {
		getInput(1);

		charactersFound = new LinkedList<>();
		frequencyMap = new HashMap<Character, Integer>();
		LinkedList<Node> huffmanList = new LinkedList<>();
		treeHeadNode = null;

		for (int i = 0; i < inputFileContent.length(); i++) {
			char c = inputFileContent.charAt(i);

			if (!charactersFound.contains(c)) {
				charactersFound.add(c);
			}

			Integer val = frequencyMap.get(new Character(c));
			if (val != null) {
				frequencyMap.put(c, new Integer(val + 1));
			} else {
				frequencyMap.put(c, 1);
			}
		}

		huffmanList = createHuffmanList(charactersFound, frequencyMap);
		treeHeadNode = huffmanTreeBuilder(huffmanList);
		Integer[] arr = new Integer[treeHeight];
		huffmanCodeAssigner(treeHeadNode, arr, 0);

		try {
			FileOutputStream fstream = new FileOutputStream(outputFilePath, false);
			BitOutputStream bitStream = new BitOutputStream(fstream);

			char fileCharacterArray[] = inputFileContent.toCharArray();

			for (int i = 0; i < fileCharacterArray.length; i++) {
				String codeInStringForm = huffmanCodeMap.get(fileCharacterArray[i]);
				System.out.println("Converting huffman code " + codeInStringForm + " for character " + fileCharacterArray[i] + "to bit format");
				for (int j = 0; j < codeInStringForm.length(); j++) {
					char oneCharacter = codeInStringForm.charAt(j);
					String oneLetter = String.valueOf(oneCharacter);
					int codeInIntForm = Integer.valueOf(oneLetter);
					System.out.println("Writing bit" + codeInIntForm);
					bitStream.write(codeInIntForm);
					numberOfBitsWritten++;
				}
			}
			bitStream.close();
			fstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(numberOfBitsWritten);
		saveCodeMap();

	}

	public void decode() {
		decodedString = "";

		loadCodeMap();

		getInput(2);
		HashMap<String, Character> swappedMap = swapMap(huffmanCodeMap);
		
		
		String bitsToDecode = "";
		
		while(bitLinkedList.size() != numberOfBitsWritten){
			bitLinkedList.remove(bitLinkedList.size()-1);
		}
		for(int i = 0 ; i < bitLinkedList.size(); i++){
			bitsToDecode+= bitLinkedList.get(i);
		}
		

		System.out.println(bitLinkedList.toString());
		
		decodeStringUsingMap(swappedMap, bitsToDecode, 0, 1);

		try {
			FileWriter fstream = new FileWriter(outputFilePath, false);
			BufferedWriter bWriter = new BufferedWriter(fstream);
			PrintWriter pWriter = new PrintWriter(bWriter);

			for (int counter = 0; counter < decodedString.toCharArray().length; counter++) {
				pWriter.print(decodedString.toCharArray()[counter]);
			}

			pWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(decodedString);
	}

	public Node huffmanTreeBuilder(LinkedList<Node> nodeList) {
		Node headNode = null;
		treeHeight = 0;

		if (nodeList.size() == 0) {
			return null;
		}

		if (nodeList.size() == 1) {
			return nodeList.get(0);
		}

		while (nodeList.size() > 2) {
			System.out.println("\n\nAdding nodes " + nodeList.get(0).frequency + " and " + nodeList.get(1).frequency);
			headNode = addNodes(nodeList.get(0), nodeList.get(1));
			treeHeight++;
			System.out.println("Node " + headNode.frequency + " created");

			System.out.println("Removing node " + nodeList.get(0).frequency);
			nodeList.removeFirst();

			System.out.println("Removing node " + nodeList.get(0).frequency);
			nodeList.removeFirst();

			for (int i = 0; i < nodeList.size(); i++) {
				System.out.println("Iterating throught list of size " + nodeList.size());
				System.out.println("Searching insertion place for node " + headNode.frequency);
				if (nodeList.get(i).frequency > headNode.frequency) {
					System.out.println("Insertion place for " + headNode.frequency + " found. Insert at index " + i
							+ " to replace " + nodeList.get(i).frequency);
					nodeList.add(nodeList.get(nodeList.size() - 1));

					for (int j = nodeList.size() - 2; j >= i; j--) {
						nodeList.set(j + 1, nodeList.get(j));
						System.out.println("Moving " + nodeList.get(j).frequency + " to the right at index " + (j + 1));
					}
					nodeList.set(i, headNode);
					System.out.println("Inserting node " + headNode.frequency + " at " + i);
					break;
				} else if (i + 1 == nodeList.size()) {
					System.out.println("Insertion place for " + headNode.frequency + " found. Insert next to index "
							+ (nodeList.size() - 1) + " (Node " + nodeList.getLast().frequency + ")");
					nodeList.add(headNode);
					System.out
							.println("Adding nodes " + nodeList.get(0).frequency + " and " + nodeList.get(1).frequency);
					break;
				}
				System.out.println("Insertion place for " + headNode.frequency + " not found");
			}

		}

		if (nodeList.size() == 2) {
			System.out.println("Adding " + nodeList.get(0).frequency + " and " + nodeList.get(1).frequency);
			headNode = addNodes(nodeList.get(0), nodeList.get(1));
			treeHeight++;
		}

		return headNode;
	}

	public Node addNodes(Node leftNode, Node rightNode) {
		Node sumNode = new Node(leftNode.frequency + rightNode.frequency);

		leftNode.setParentNode(sumNode);
		rightNode.setParentNode(sumNode);

		sumNode.setLeftNode(leftNode);
		sumNode.setRightNode(rightNode);

		return sumNode;
	}

	public LinkedList<Node> createHuffmanList(LinkedList<Character> charactersFound,
			HashMap<Character, Integer> frequencyMap) {
		LinkedList<Node> huffmanList = new LinkedList<>();

		for (int i = 0; i < charactersFound.size(); i++) {
			char c = charactersFound.get(i);
			Node node = new Node(c, frequencyMap.get(c));
			huffmanList.add(node);
		}

		Collections.sort(huffmanList, new Comparator<Node>() {
			@Override
			public int compare(Node node1, Node node2) {
				return node1.frequency - node2.frequency;
			}
		});

		return huffmanList;
	}

	public void huffmanCodeAssigner(Node node, Integer[] arr, int position) {
		if (!(node.leftNode == null)) {
			arr[position] = 0;
			huffmanCodeAssigner(node.leftNode, arr, position + 1);
		}

		if (!(node.rightNode == null)) {
			arr[position] = 1;
			huffmanCodeAssigner(node.rightNode, arr, position + 1);
		}

		if ((node.leftNode == null) && (node.rightNode == null)) {
			System.out.println("Mapping " + node.character + generateCode(arr, position));
			huffmanCodeMap.put(node.character, generateCode(arr, position));
		}
	}

	String generateCode(Integer[] arr, int size) {
		builder = new StringBuilder();
		for (int i = 0; i < size; ++i) {
			builder.append(String.valueOf(arr[i]));
		}
		return builder.toString();
	}

	public <Character, String> HashMap<String, Character> swapMap(Map<Character, String> map) {
		HashMap<String, Character> rev = new HashMap<String, Character>();
		for (Map.Entry<Character, String> entry : map.entrySet())
			rev.put(entry.getValue(), entry.getKey());
		return rev;
	}

	public void decodeStringUsingMap(HashMap<String, Character> map, String bits, int position, int end) {
		if (map.containsKey(bits.substring(position, end))) {
			decodedString += map.get(bits.substring(position, end));
			if (end == bits.length()) {
				return;
			}
			decodeStringUsingMap(map, bits, end, end + 1);
		} else {
			if (end == bits.length()) {
				return;
			}
			decodeStringUsingMap(map, bits, position, end + 1);
		}
	}

	/**
	*
	*/
	public void saveCodeMap() {

		FileOutputStream fileOutputStream;

		try {
			File file = new File(codeMapFileName);
			fileOutputStream = new FileOutputStream(file);
			ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(huffmanCodeMap);
			objectOutputStream.close();
			fileOutputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	*
	*/
	public void loadCodeMap() {

		FileInputStream fileInputStream;

		try {
			File file = new File(codeMapFileName);
			fileInputStream = new FileInputStream(file);
			ObjectInputStream is = new ObjectInputStream(fileInputStream);
			huffmanCodeMap = (HashMap<Character, String>) is.readObject();
			is.close();
			fileInputStream.close();
		} catch (Exception e) {
			huffmanCodeMap = new HashMap<Character, String>();
			saveCodeMap();
			e.printStackTrace();
		}
	}

}
