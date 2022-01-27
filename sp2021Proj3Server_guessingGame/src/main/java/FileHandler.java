import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

import javafx.util.Pair;

public class FileHandler extends BufferedReader {

		public FileHandler(String arg0) throws FileNotFoundException {
			super(new FileReader(arg0), 200);
		}
		
		//                 Category              Word,   Clue
		 public Vector<Pair<String, Vector<Pair<String, String>>>> getFileData() {
			Vector<Pair<String, Vector<Pair<String, String>>>> dataVect = new Vector<Pair<String, Vector<Pair<String, String>>>>();
			
			String line = "";
			String prevLine = getLine();
			getLine();
			while(line != null && line.compareTo("EOF") != 0) {
				String category = prevLine;
				dataVect.add(new Pair<String, Vector<Pair<String, String>>>(category, new Vector<Pair<String, String>>() ));
				line = getLine();
				while(line.compareTo("---") != 0 && line.compareTo("EOF") != 0) {
					if (line != null && line.indexOf('-') > 0) {
						int divider = line.indexOf('-');
						String word = line.substring(0, divider - 1);
						String clue = line.substring(divider + 2, line.length());
						
						dataVect.elementAt(dataVect.size()-1).getValue().add(new Pair(word, clue));
					}
					prevLine = line;
					line = getLine();
				}
			}
			// Print data vector for debugging
			/*
			for (int k = 0; k < dataVect.size(); k++) {
				System.out.println(dataVect.elementAt(k).getKey());
				for(int j = 0; j < dataVect.elementAt(k).getValue().size(); j++) {
					System.out.println("    " + dataVect.elementAt(k).getValue().elementAt(j).getKey());
					System.out.println("       " + dataVect.elementAt(k).getValue().elementAt(j).getValue());
				}
			}*/
			if (dataVect == null) {
				System.out.println("DATAVECT NULL");
			}
			return dataVect;
		}
		
		
		private String getLine() {
			try {
				String line = this.readLine();
				return line;
			} catch (IOException e) {
				return "EOF";
			}
		}
	}