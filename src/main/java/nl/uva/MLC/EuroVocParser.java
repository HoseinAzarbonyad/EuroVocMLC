/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.MLC;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author mosi
 */
public abstract class EuroVocParser {
    
    
    public void fileReader(File mainFile) {
        File[] files = mainFile.listFiles();
        for(File file:files){
            if(file.isDirectory()){
                fileReader(file);
            }
            else{
                if(file.getName().startsWith("jrc")&& file.getName().endsWith(".xml"))
                                fileParser(file);
            }
        }
    }
    
    public void fileParser(File file){
               
                XPath xpath = null;
                Document doc = null;
                try {
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();   
//                    docFactory.setNamespaceAware(true);
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                    doc = docBuilder.parse(file);
                    
                    XPathFactory xpathfactory = XPathFactory.newInstance();
                    xpath = xpathfactory.newXPath();
                    
                } catch (ParserConfigurationException ex) {
                    Logger.getLogger(EuroVocParser.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SAXException ex) {
                    Logger.getLogger(EuroVocParser.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(EuroVocParser.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                    
                    try {
                        XPathExpression idExpr = xpath.compile("/TEI.2");
                        XPathExpression nExpr = xpath.compile("/TEI.2");
                        XPathExpression langExpr = xpath.compile("/TEI.2");
                        XPathExpression creationDateExpr = xpath.compile("//teiHeader");
                        XPathExpression titleExpr = xpath.compile("//title[2]");
                        XPathExpression urlExpr = xpath.compile("//xref");
                        XPathExpression noteExpr = xpath.compile("//note");
                        XPathExpression textExpr = xpath.compile("//classCode");
                           
                        String id = ((Node)idExpr.evaluate(doc, XPathConstants.NODE)).getAttributes().getNamedItem("id").getTextContent();
                        String n = ((Node)nExpr.evaluate(doc, XPathConstants.NODE)).getAttributes().getNamedItem("n").getTextContent();
                        String lang = ((Node)langExpr.evaluate(doc, XPathConstants.NODE)).getAttributes().getNamedItem("lang").getTextContent();
                        String creationDate = ((Node)creationDateExpr.evaluate(doc, XPathConstants.NODE)).getAttributes().getNamedItem("date.created").getTextContent();
                        String title = (String)titleExpr.evaluate(doc, XPathConstants.STRING);
                        String url = (String)urlExpr.evaluate(doc, XPathConstants.STRING);
                        String note = (String)noteExpr.evaluate(doc, XPathConstants.STRING);
                        NodeList nodes = (NodeList)textExpr.evaluate(doc, XPathConstants.NODESET);
                        ArrayList<String> classes = new ArrayList<>();
                        for (int i = 0; i < nodes.getLength(); i++) {
                            classes.add(nodes.item(i).getTextContent());
                        }
                        
                    } catch (XPathExpressionException ex) {
                        Logger.getLogger(EuroVocParser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
//                Object result = expr.evaluate(doc, XPathConstants.STRING);
//                s = (String) result;
                
//            Doc doc = new
                    
    }
    public abstract void doSomeAction(Doc doc); 
    
    public static void main(String[] args) {
        EuroVocParser evp = new EuroVocParser() {

            @Override
            public void doSomeAction(Doc doc) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
//        evp.fileReader(new File("/home/mosi/Desktop/MLC/data/EuroVac/en"));
            evp.fileParser(new File("/home/mosi/Desktop/MLC/data/EuroVac/en/1969/jrc31969L0169-en.xml"));
    }
}
