package nl.UvA.MLC.IREngine.LuceneFacility;

import static nl.UvA.MLC.Settings.Config.configFile;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

/**
 *
 * @author  Mostafa Dehghani
 */
public class Utilities {
   
    private static String eol = System.getProperty("line.separator");
    
        public static CharArraySet Stoplistloader(String filePath) throws FileNotFoundException{
		Set<String> stopCollection = new HashSet<String>();
		Scanner fileScanner = new Scanner(new File(filePath));
		while(fileScanner.hasNextLine()){
			stopCollection.add(fileScanner.nextLine().trim());
		}    
		return new CharArraySet(Version.LUCENE_CURRENT,stopCollection,true);
	}


	public static Analyzer MyEnglishAnalizer(Boolean steming, Boolean stopwordRemooving) throws FileNotFoundException{
		final CharArraySet stopword;
		if(steming && stopwordRemooving) {
                        stopword = Stoplistloader(configFile.getProperty("ENGLISH_STOPWORDS_FILE_PATH"));
			return new AnalyzerWrapper() {
				@Override
				protected Analyzer getWrappedAnalyzer(String string) {
					return new StandardAnalyzer(Version.LUCENE_CURRENT,stopword);
				}
				@Override
				protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {
					return new Analyzer.TokenStreamComponents(tsc.getTokenizer(), new PorterStemFilter(tsc.getTokenizer()));
				}
			};
		}
		else if(!steming && stopwordRemooving) {
                        stopword = Stoplistloader(configFile.getProperty("ENGLISH_STOPWORDS_FILE_PATH"));
			return new StandardAnalyzer(Version.LUCENE_CURRENT,stopword);
		}
		else if(steming && !stopwordRemooving) {
			return new AnalyzerWrapper() {
				@Override
				protected Analyzer getWrappedAnalyzer(String string) {
					return new SimpleAnalyzer(Version.LUCENE_CURRENT);
				}
				@Override
				protected Analyzer.TokenStreamComponents wrapComponents(String fieldName, Analyzer.TokenStreamComponents tsc) {
					return new Analyzer.TokenStreamComponents(tsc.getTokenizer(), new PorterStemFilter(tsc.getTokenizer()));
				}
			};
		}
		return new SimpleAnalyzer(Version.LUCENE_CURRENT);
	}   
        
        public static Analyzer getAnalyzer(String Language, Boolean steming, Boolean stopwordRemooving) throws FileNotFoundException {
            
            Analyzer analyzer = new SimpleAnalyzer(Version.LUCENE_CURRENT);
            if(Language.equalsIgnoreCase("EN"))
            {
//                    analyzer = MyEnglishAnalizer(new Boolean(configFile.getProperty("IF_STEMMING")), new Boolean(configFile.getProperty("IF_STOPWORD_REMOVING"))); 
                    analyzer = MyEnglishAnalizer(steming, stopwordRemooving); 
            }
            return analyzer;
        
    }
    public static String readFileAsString(String filePath) throws IOException{
        return readFileAsString(new File(filePath));
    }
    
    public static String readFileAsString(File file) throws FileNotFoundException, IOException{
        BufferedInputStream f = null;
        String str = "";
            byte[] buffer = new byte[(int) file.length()];
            f = new BufferedInputStream(new FileInputStream(file));
            f.read(buffer);
            str = new String(buffer);
            f.close();
        return str;
    }
}
