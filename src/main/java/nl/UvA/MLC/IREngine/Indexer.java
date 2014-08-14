package nl.UvA.MLC.IREngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.xml.parsers.ParserConfigurationException;
import nl.UvA.MLC.EuroVoc.EuroVocDoc;
import nl.UvA.MLC.EuroVoc.EuroVocParser;
import static nl.UvA.MLC.IREngine.LuceneFacility.Utilities.getAnalyzer;
import static nl.UvA.MLC.Settings.Config.configFile;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.xml.sax.SAXException;


/**
 *
 * @author  Mostafa Dehghani
 */

public class Indexer extends EuroVocParser{
    
    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Indexer.class.getName());
    private IndexWriter writer;
    private final Boolean stemming = Boolean.valueOf(configFile.getProperty("IF_STEMMING"));
    private final Boolean commonWordsRemoving = Boolean.valueOf(configFile.getProperty("IF_STOPWORD_REMOVING"));
    

    public Indexer(){
        log.info("\n\n -----------------------INDEXING--------------------------\n");
        try {
                File tmpIndex = new File(configFile.getProperty("DOC_TMP_INDEX_PATH"));
                if(tmpIndex.exists()){
                    FileUtils.deleteDirectory(tmpIndex);
                    log.info("Deletting the existing tmp_index directory on: " + configFile.getProperty("DOC_TMP_INDEX_PATH"));
                    FileUtils.forceMkdir(new File(configFile.getProperty("DOC_TMP_INDEX_PATH")));
                    log.info("Making tmp_index directory on: " + configFile.getProperty("DOC_TMP_INDEX_PATH"));
                }
                
                File Index = new File(configFile.getProperty("DOC_INDEX_PATH"));
                if(tmpIndex.exists()){
                    FileUtils.deleteDirectory(Index);
                    log.info("Deletting the existing index directory on: " + configFile.getProperty("DOC_INDEX_PATH"));
                    FileUtils.forceMkdir(new File(configFile.getProperty("DOC_INDEX_PATH")));
                    log.info("Making index directory on: " + configFile.getProperty("DOC_INDEX_PATH"));
                }
                
                Analyzer analyzer = getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"),stemming, false);
                IndexWriterConfig irc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
                this.writer = new IndexWriter(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))), irc);
                fileReader(new File(configFile.getProperty("CORPUS_PATH")));
                this.writer.commit();
                this.writer.close();
                analyzer.close();
            if(!commonWordsRemoving){
                File index = new File(configFile.getProperty("DOC_TMP_INDEX_PATH"));
                File newIndex = new File(configFile.getProperty("DOC_INDEX_PATH"));
                index.renameTo(newIndex);
            }
//            else{
//                IndexReader ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("DOC_TMP_INDEX_PATH"))));
                    
                
//                Analyzer analyzer = getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"),stemming, commonWordsRemoving);
//                IndexWriterConfig irc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
//                this.writer = new IndexWriter(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))), irc);
//                fileReader(new File(configFile.getProperty("CORPUS_PATH")));
//                this.writer.commit();
//                this.writer.close();
//                analyzer.close();
//                Analyzer analyzer = getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"),stemming, commonWordsRemoving);
//                IndexWriterConfig irc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
//                this.writer = new IndexWriter(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))), irc);
//                fileReader(new File(configFile.getProperty("CORPUS_PATH")));
//                this.writer.commit();
//                this.writer.close();
//                analyzer.close();\
//    
//                
//            }
        } catch (FileNotFoundException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }
    }
    @Override
    public void doSomeAction(EuroVocDoc EVdoc) {
        Document doc = new Document();
        doc.add(new Field("ID", EVdoc.getId(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("TITLE", EVdoc.getTitle(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        doc.add(new Field("TEXT", EVdoc.getText(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        String Classes = "";
        for(String s:EVdoc.getClasses()){
            Classes = s + " ";
        }
        doc.add(new Field("CLASSES", Classes.trim(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        try {
            this.writer.addDocument(doc);
        } catch (IOException ex) {
            log.error(ex);
        }
        log.info("Document " + EVdoc.getId() + " has been indexed successfully...");
    }
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, SQLException {
        new Indexer();
    }
    
}
