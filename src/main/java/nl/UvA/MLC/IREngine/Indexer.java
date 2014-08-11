/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.UvA.MLC.IREngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import nl.UvA.MLC.EuroVoc.EuroVocDoc;
import nl.UvA.MLC.EuroVoc.EuroVocParser;
import static nl.UvA.MLC.IREngine.Utilities.getAnalyzer;
import static nl.UvA.MLC.Settings.Config.configFile;
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
 * @author mosi
 */
public class Indexer extends EuroVocParser{
    
    private IndexWriter writer;

    public Indexer(){
        try {
            Analyzer analyzer = getAnalyzer(configFile.getProperty("CORPUS_LANGUAGE"));
            IndexWriterConfig irc = new IndexWriterConfig(Version.LUCENE_CURRENT, analyzer);
            this.writer = new IndexWriter(new SimpleFSDirectory(new File(configFile.getProperty("INDEX_PATH"))), irc);
            fileReader(new File(configFile.getProperty("CORPUS_PATH")));
            this.writer.commit();
            this.writer.close();
            analyzer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void doSomeAction(EuroVocDoc EVdoc) {
        Document doc = new Document();
        doc.add(new Field("ID", EVdoc.getId(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("TITLE", EVdoc.getTitle(), Field.Store.YES, Field.Index.NO));
        doc.add(new Field("TEXT", EVdoc.getText(), Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS));
        String Classes = "";
        for(String s:EVdoc.getClasses()){
            Classes = s + " ";
        }
        doc.add(new Field("CLASSES", Classes.trim(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        try {
            this.writer.addDocument(doc);
        } catch (IOException ex) {
            Logger.getLogger(Indexer.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Document " + EVdoc.getId() + " has been indexed successfully...");
    }
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, SQLException {
        new Indexer();
    }
    
}
