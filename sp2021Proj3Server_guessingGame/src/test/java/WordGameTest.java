import static org.junit.jupiter.api.Assertions.*;

import java.util.Vector;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class WordGameTest {
	WordGame w1;
	@BeforeEach
	void beforeEach() {
		w1 = new WordGame("src/main/resources/input1.txt");
	}
	
	@Test
	void testConstructor() {
		WordGame w2 = new WordGame("src/main/resources/input1.txt");
		assertEquals(w2.getClass(), WordGame.class, "Constructor failed!");
	}
	
	@Test
	void testConstructorFailure() {
		boolean failedPathWorks = true;
		try {
			WordGame w2 = new WordGame("FailedPath");
		} catch(Exception e) {
			failedPathWorks = false;
		}
		assertEquals(failedPathWorks, false, "Constructor failed!");
	}
	
	@Test
	void testGetFileSuccess() {
		assertEquals(w1.getFileSuccess(), true, "getFileSuccess failed for valid input");
	}
	
	@Test
	void testGetCategories() {
		Vector<String> categories = new Vector<String>();
		Vector<String> correctCategories = new Vector<String>();
		correctCategories.add("Colors");
		correctCategories.add("States");
		correctCategories.add("Shapes");
		
		categories = w1.getCategories();
		
		boolean likeVects = true;
		for(int i = 0; i < 3; i++) {
			if (!correctCategories.contains(categories.elementAt(i))) {
				likeVects = false;
			}
		}
		assertEquals(likeVects, true, "getCategores failed, wrong categories produced");
	}
	
	@Test
	void testRandCategories() {
		Vector<String> categories = w1.getCategories();
		// Create a bunch of WordGames to see if they have different arrangement of random categories
		boolean differentCategories = false;
		for(int i = 0; i < 10000; i++) {
			WordGame w2 = new WordGame("src/main/resources/input1.txt");
			if (w2.getCategories() != categories) {
				differentCategories = true;
				break;
			}
		}
		assertEquals(differentCategories, true,"setRandomCategories failed, Categories not randomized");
	}

	@Test
	void testSetCurrWord() {
		w1.setCurrCategory("States");
		w1.setCurrWord();
		int i = w1.getRandCategoryIndex("States");
		Vector<Vector<String>> randWords = w1.getRandWords();
		
		assertEquals(w1.getCurrWord(), randWords.elementAt(i).elementAt(0), "setCurrWord failed, did not properly reset current word");
	}
	
	@Test
	void testGetCloakedWord() {
		w1.setCurrCategory("States");
		w1.setCurrWord();
		String currWord = w1.getCurrWord();
		currWord = currWord.replace(currWord.charAt(0), '-');
		for(int i = 1; i < currWord.length(); i++) {
			w1.submitLetter(currWord.charAt(i));
		}
		
		assertEquals(w1.getCloakedCurrWord(), currWord, "getCloakedWord failed, did not cloak word for every char guessed except first letter in word");
		
	}
	
	@Test
	void testCheckLost() {
		Vector<String> categories = w1.getCategories();
		// Algorithm to incorrectly guess all letters
		for(int i = 0; i < 3; i++) {
			w1.setCurrCategory(categories.elementAt(i));
			w1.setCurrWord();
			for(int j = 0; j < w1.getCurrWord().length(); j++) {
				if (j-1 > 0) {
					if (w1.getCurrWord().substring(0, j-1).contains(String.valueOf(w1.getCurrWord().charAt(j)))) {
						w1.submitLetter((char) (w1.getCurrWord().charAt(j)+1));
					}
				}
			}
		}
		assertEquals(w1.getMajorWinState(), "Ongoing", "CheckLost failed");
	}

	@Test
	void testUpdateWinState() {
		Vector<String> categories = w1.getCategories();
		// Algorithm to incorrectly guess all letters
		for(int i = 0; i < 3; i++) {
			w1.setCurrCategory(categories.elementAt(i));
			w1.setCurrWord();
			for(int j = 0; j < w1.getCurrWord().length(); j++) {
				if (j-1 > 0) {
					if (w1.getCurrWord().substring(0, j-1).contains(String.valueOf(w1.getCurrWord().charAt(j)))) {
						w1.submitLetter((char) (w1.getCurrWord().charAt(j)));
					}
				}
			}
		}
		assertEquals(w1.getMajorWinState(), "Ongoing", "updateWinState failed");
	}
	
	@Test
	void testgetN_Rand() {
		int max = 10;
		Vector<Integer> randVect = w1.getN_Rand(4, max);
		
		boolean vectorValid = true;
		for(int i = 0; i < randVect.size(); i++) {
			// Check elements are within propor range
			if (randVect.elementAt(i) < 0 || randVect.elementAt(i) > 10) {
				vectorValid = false;
				break;
			}
			// Check for duplicates
			for(int j = 0; j < i; j++) {
				if (randVect.elementAt(j) == randVect.elementAt(i)) {
					vectorValid = false;
					break;
				}
			}
		}
		
		assertEquals(vectorValid, true, "getN_Rand failed");
	}
}
