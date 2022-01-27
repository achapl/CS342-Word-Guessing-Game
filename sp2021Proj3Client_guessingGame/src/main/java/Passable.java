import java.io.Serializable;
import java.util.Vector;

public class Passable implements Serializable {
	public String function;
	public boolean fileFound;
	public Vector<String> categories;
	public Vector<Vector<String>> randWords;
	
	public Vector<Character> charsGuessed;
	
	public String successfulSubmit,
				   minorWinState,
				   majorWinState,
				   cloackedWord,
				   currCategory,
				   inputText,
				   currWord,
				   currHint;
	
	public int incorrectGuesses;
	public int incorrectWords;
	public int correctWords;
}
