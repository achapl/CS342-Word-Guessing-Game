import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Vector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;

public class WordGame {
	
	ServerGUI parent;
	String file;
	FileHandler fh;
	boolean fileFound;
	//                Category              Word    Hint
	public Vector<Pair<String, Vector<Pair<String, String>>>> data;
	private Vector<String> categories;
	public Vector<Vector<String>> randWords;
	
	private Vector<Character> charsGuessed;
	
	private String minorWinState = "Ongoing",
				   majorWinState = "Ongoing",
				   currCategory,
				   currWord;
	
	int incorrectGuesses = 6;
	int incorrectWords = 0;
	int correctWords = 0;
	
	
	
	WordGame(String inputFile){
		randWords    = new Vector<Vector<String>>();
		charsGuessed = new Vector<Character>();
		charsGuessed.add('a');
		charsGuessed.add('e');
		charsGuessed.add('n');
		
		fileFound = getFileHandler(inputFile);
		if (getFileSuccess()) {
			data = fh.getFileData();
		}
		
		setRandCategories();
		setRandWords();
	}
	
	//--------------
	// File Handling
	//--------------
	public boolean getFileSuccess() {
		return fileFound;
	}
	
	// Return true if successful, else return false
	boolean getFileHandler(String inputFile) {
		try {
			fh = new FileHandler(inputFile);
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	
	
	//-----------------------------
	// Random Words Getting/Setting
	//-----------------------------
	
	// Set currCategory based on user choice
	public void setCurrCategory(String category) {
		this.currCategory = category;
	}
	
	// Initial setting of categories from constructor call
	private void setRandCategories() {
		categories = new Vector<String>();
		int numCategories = 3;
		Vector<Integer> indexes = getN_Rand(numCategories, data.size());
		for(int i = 0; i < numCategories; i++) {
			categories.add(data.elementAt(indexes.elementAt(i)).getKey());
		}
	}
	
	// Initial setting of categories from constructor call
	private void setRandWords() {
		
		//				    Category, Strings in category
		// randWords is type Vector<  Vector<String>   >, which is a vector that contains vectors of words for each category
		
		int numWords = 3;
		// For each category...
		for(int i = 0; i < categories.size(); i++) {
			randWords.add(new Vector<String>());
			
			// Get 'k' such that data[k] = category
			// 'k' is the index in 'data' of the current category
			int k = 0;
			while(data.elementAt(k).getKey() != categories.elementAt(i)) {
				k++;
			}
			Vector<Pair<String, String>> currWords = data.elementAt(k).getValue();			
			
			int totalWords = currWords.size();
			Vector<Integer> indexes = getN_Rand(numWords, totalWords);
			
			for(int l = 0; l < numWords; l++) {
				int randNum = indexes.elementAt(l);
				randWords.elementAt(i).add( currWords.elementAt(randNum).getKey() );
			}
		}
	}
	
	// Sets the current word to the 0'th elementh of the vector
	// corresponding to the currCategory
	public void setCurrWord() {
		int i = 0;
		while (categories.elementAt(i).compareTo(currCategory) != 0) {
			i++;
		}
		currWord = randWords.elementAt(i).elementAt(0);
	}
	
	public Vector<String> getCategories() { return categories; }
	
	public Vector<String> getWords(String category) {
		
		// Get 'i' such that data[i] = category
		int i = 0;
		while(categories.elementAt(i) != category) {
			i++;
		}
		
		return randWords.elementAt(i);
	}
	
	public String getCloakedCurrWord() {
		checkLost();
		
		String returnWord = "";		
		for(int i = 0; i < currWord.length(); i++) {
			if (charsGuessed.contains(currWord.charAt(i))) {
				returnWord = returnWord.concat(Character.toString(currWord.charAt(i)));
			} else {
				returnWord = returnWord.concat("-");
			}
		}
		updateWinState(returnWord);
		return returnWord;
	}
	
	private void checkLost() {
		if (incorrectGuesses == 0) {
			minorWinState = "Lost";
			incorrectWords++;
			
			// Remove current word from the randomWord pool so it can't be guessed again
			int i = getCategoryIndex(currCategory);
			randWords.elementAt(i).remove(0);
			if (incorrectWords == 3) {
				majorWinState = "Lost";
			}
		}
	}
	
	private void updateWinState(String returnWord) {
		if (returnWord.compareTo(currWord) == 0) {
			minorWinState = "Won";
			correctWords++;
			if (correctWords == 3) {
				majorWinState = "Won";
			}
		}
	}
	
	public String getHint(String category) {
		// Get 'i' such that data[i] = category
		int i = 0;
		while(data.elementAt(i).getKey().compareTo(category) != 0) {
			i++;
		}
		
		int j = 0;
		while(data.elementAt(i).getValue().elementAt(j).getKey().compareTo(currWord) != 0) {
			j++;
		}
		
		String clue = data.elementAt(i).getValue().elementAt(j).getValue();
		
		return clue;
	}
	
	public String getCurrCategory() {
		return currCategory;
	}
	
	public String getCurrWord() {
		return currWord;
	}
	
	public int getGuesses() {
		return incorrectGuesses;
	}	
	
	public String getMinorWinState() {
		return minorWinState;
	}
	
	public String getMajorWinState() {
		return majorWinState;
	}
	
	public int getCorrectWords() {
		return correctWords;
	}
	
	public int getIncorrectWords() {
		return incorrectWords;
	}
	
	public Vector<Vector<String>> getRandWords() {
		return randWords;
	}
	
	// Create a vector with n unique random integers from 1-max
	public Vector<Integer> getN_Rand(int n, int max) {
		
		Random rand = new Random();
		Vector<Integer> returnVect = new Vector<Integer>();
		
		while(returnVect.size() < n) {
			Integer a = rand.nextInt(max);
			if (!returnVect.contains(a)) {
				returnVect.add(a);
			}
		}
		return returnVect;
	}

	
	public String submitLetter(char c) {
		
		if (charsGuessed.contains(c)) {
			return "Already Guessed";
		}
		charsGuessed.add(c);
		if (currWord.contains((new Character(c)).toString())) {
			return "Correct Guess";
		} else {
			if (incorrectGuesses > 0) {
				incorrectGuesses--;
			}
			return "Incorrect Guess";
		}
	}
	
	// Get the index in 'data' of a given category, returns -1 if cannot be foun
	public int getCategoryIndex(String category) {
		int i = 0;
		while(data.elementAt(i).getKey().compareTo(category) != 0) {
			i++;
			// Item not found, prevent inf loop
			if (i == 10000) {
				return -1;
			}
		};
		return i;
	}
	
	public int getRandCategoryIndex(String category) {
		int i = 0;
		while(categories.elementAt(i).compareTo(category) != 0) {
			i++;
			// Item not found, prevent inf loop
			if (i == 10000) {
				return -1;
			}
		};
		return i;
	}
	
	
	public void reset() {
		incorrectGuesses = 6;
		currWord = null;
		currCategory = null;
		charsGuessed.clear();
		minorWinState = "Ongoing";
	}
}
