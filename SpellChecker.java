import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;

/**
 * This class takes a document and checks the spelling of each word
 * against a dictionary. This dictionary can optionally be passed in
 * as an argument so that the user can use a custom dictionary.
 * @author Chris
 *
 */
public class SpellChecker {

	private static final int normalTermination = 0;
	private static final int spellingErrorTermination = 1;
	private static final int IOErrorTermination = 2;
	private LinkedHashSet<String> d;
	private LinkedHashSet<Error> words;
	private Boolean errorOnLine;

	/**
	 * Checks a file against a dictionary.
	 * @param document The file to be checked.
	 * @param dictionary The dictionary to check against (/usr/share/dict/words by default).
	 */
	public void Checker(File document, LinkedHashSet<String> dictionary) {
		int exitCode = normalTermination;
		FileInputStream docu = null;
		BufferedReader read = null;
		this.d = dictionary;
		this.words = new LinkedHashSet<Error>();
		try {
			docu = new FileInputStream(document);
			InputStreamReader doc = new InputStreamReader(docu);
			read = new BufferedReader(doc);
			int lineNumber = 0;
			String line = read.readLine();
			this.errorOnLine = false;
			while(line != null) {
				
				this.splitLine(line, lineNumber);
				
				if(errorOnLine) {
					System.out.println("Line Number: " + lineNumber + " - " + line);
					for(Error error : words) {
						if(error.getLineNumber() == lineNumber) {
							System.out.println(error.getError());
						}
					}
					System.out.println();
				}
				
				lineNumber++;
				line = read.readLine();
				if(errorOnLine) {
					exitCode = spellingErrorTermination;
				}
				errorOnLine = false;
			}
		} catch (IOException e) {
			System.err.println("IO Error");
			System.err.println(e.getMessage());
			
			exitCode = IOErrorTermination;
		} finally {
			try {
				read.close();
				docu.close();
			} catch (IOException e) {
				System.err.println("Error when closing files");
				System.err.println(e.getMessage());
				
				exitCode = IOErrorTermination;
			}
		}
		System.exit(exitCode);
	}
	
	/**
	 * Takes a file to be used as a dictionary and turns that file into
	 * a LinkedHashSet to enable quick, easy and efficient access when
	 * checking words for spelling mistakes.
	 * @param dictionary The file to be turned into a LinkedHashSet.
	 * @return Returns a LinkedHashSet containing all the words of the dictionary.
	 */
	private LinkedHashSet<String> Dictionary(File dictionary) {
		FileInputStream dic = null;
		LinkedHashSet<String> set = new LinkedHashSet<String>();
		
		try {
			dic = new FileInputStream(dictionary);
			InputStreamReader dict = new InputStreamReader(dic);
			BufferedReader d = new BufferedReader(dict);
			String line = d.readLine();
			while(line != null) {
				set.add(line.toLowerCase());
				line = d.readLine();
			}
			d.close();
		} catch (IOException e) {
			System.err.println("IO Error, dictionary not found.");
			System.err.println(e.getMessage());
		} finally {
			try {
				dic.close();
			} catch (IOException e) {
				System.err.println("Error when closing files");
				System.err.println(e.getMessage());
			}
		}
		return set;
	}
	
	/**
	 * Takes a string and splits it up into the individual words.
	 * It also checks that string for spelling mistakes and adds
	 * any mistakes it finds to a LinkedHashSet to be printed out and manipulated later.
	 * @param line The string to be split up.
	 * @param lineNumber The number of the line, to be used to record spelling errors.
	 */
	private void splitLine(String line, int lineNumber) {
		String word = "";
		for(int j = 0; j < line.length(); j++) {
			char c = line.charAt(j);
			if((Character.isLetterOrDigit(c) || c == '\'') && c != ' ') {
				word += c;
			} else {
				if(!d.contains(word.toLowerCase()) && word != "") {
					words.add(new Error(word, lineNumber));
					errorOnLine = true;
				}
				word = "";
			}
		}
	}

	public static void main(String[] args) {
		if (args.length < 3 && args.length > 0) {
			File document = new File(args[0]);
			File dictionary = null;
			if (args.length == 2) {
				dictionary = new File(args[1]);
			} else {
				dictionary = new File("/usr/share/dict/words");
			}
			try {
				document.exists();
				dictionary.exists();
			} catch (SecurityException e) {
				System.err.println("Either document or dictionary cannot be opened.");
				System.err.println(e.getMessage());
			}
			SpellChecker checker = new SpellChecker();
			checker.Checker(document, checker.Dictionary(dictionary));
		} else {
			System.err.println("Usage: java SpellChecker <file to be checked> <dictionary *optional*>");
			System.exit(IOErrorTermination);
		}
	}
}
