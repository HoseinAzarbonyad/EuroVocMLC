/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.UvA.MLC.IREngine.LuceneFacility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.lucene.index.AtomicReader;
import org.apache.lucene.index.CompositeReader;
import org.apache.lucene.index.DocsEnum;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.FieldInfos;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.SlowCompositeReaderWrapper;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.BytesRef;
import static nl.UvA.MLC.Settings.Config.configFile;

/**
 *
 * @author mosi
 * 
 * <code>IndexInfo</code> is a class which provide whit methods that extract information about the terms and document from a lucene index
 * Note: this class is compatible with Lucene 4.9 index
 */
public class IndexInfo {

    static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(IndexInfo.class.getName());
    private IndexReader ireader = null;
    private String IndexPath = null;
    private HashSet<Term> allTerms;
    private Long numOfAllTerms = -1L;
    private Long numOfAllUniqTerms = -1L;
    private Double AvgDocLength = 0D;
    private TermStats[] topTermsDF = null;
    private TermStats[] topTermsTF = null;
    private List<String> fieldNames = null;
    private HashMap<String, Long> termCounts = null;

    /**
     * Constructor  
     * setting the path of index directory
     * @param IndexPath path of the index directory
     */
    public IndexInfo(String IndexPath) {
        allTerms = new HashSet<Term>();
        fieldNames = new ArrayList<String>();
        numOfAllTerms = -1L;
        AvgDocLength = -1D;

        try {
            ireader = IndexReader.open(new SimpleFSDirectory(new File(configFile.getProperty("DOC_INDEX_PATH"))));
        } catch (IOException ex) {
            log.error(ex);
        }
        Collections.sort(fieldNames);
    }
    
    public static void main(String[] args) {
        IndexInfo ii = new IndexInfo(configFile.getProperty("DOC_INDEX_PATH"));
        TermStats[] tsArray = ii.getTopTerms_TF("TEXT",50);
        for(TermStats ts: tsArray){
            System.out.println(ts.getTermText() + " " + ts.totalTermFreq + " " + ts.docFreq);
        }
    }

    
    
    //Terms Info
    
    /**
     * 
     * <code>getTotalTF_OverAllFields</code> extracts term frequency of the given Lucene term  over all index fields. 
     * NOTE: the given term should be analyzed (e.g. stemming) before.
     * 
     * @param text 
     * @return term frequency of given term over all index field
     */
    public Long getTotalTF_OverAllFields(BytesRef text) {
        Long totalTF = 0L;
        try {
            Fields fields = MultiFields.getFields(this.ireader);
            Iterator<String> fieldIterator = fields.iterator();
            while (fieldIterator.hasNext()) {
                String fieldName = fieldIterator.next();
                //
                Term term = new Term(fieldName, text);
                totalTF += this.ireader.totalTermFreq(term);
                /*    
                final Terms terms = MultiFields.getTerms(ireader,field);
                if (terms != null) {
                    final TermsEnum termsEnum = terms.iterator(null);
                    if (termsEnum.seekExact(text)) {
                        return termsEnum.totalTermFreq();
                    }
                }*/
             }
        } catch (IOException ex) {
            log.error(ex);
        }
        return totalTF;
    }
    
    /**
     * 
     * <code>etTotalTF_PerField</code> extracts term frequency of the given Lucene term for the given index field.
     * NOTE: the given term should be analyzed (e.g. stemming) before.
     * 
     * @param field
     * @param text
     * @return term frequency of given term over the given field
     */
    private Long getTotalTF_PerField(String field, BytesRef text){
        Long TF = 0L;
            Term term = new Term(field, text);
            try {
                TF = this.ireader.totalTermFreq(term);
            } catch (IOException ex) {
                log.error(ex);
            }
            return TF;
    }
    
    /**
     * 
     * <code>getTF</code> extract the term frequency of the given term in the given field of a specific document
     * 
     * @param text
     * @param docId
     * @param field
     * @return term frequency of the given term in the given field of a specific document
     */
    
    public Integer getTF(String field, BytesRef text, Integer docId) {
        Integer TF = 0;
        try {
            Terms terms = ireader.getTermVector(docId, field);
            if (terms != null && terms.size() > 0) {
                TermsEnum termsEnum = terms.iterator(null); // access the terms for this field
                termsEnum.seekExact(text);
                DocsEnum docsEnum = termsEnum.docs(null, null); // enumerate through documents, in this case only one
                int docIdEnum;
                while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                    TF = docsEnum.freq();
                }
            }

        } catch (IOException ex) {
            log.error(ex);
        }
        return TF;
    }
    

    /**
     * 
     * <code>getDF</code> extracts the document frequency of the given term in the given field
     * 
     * @param field
     * @param text
     * @return document frequency of the given term in the given field
     */
    public Integer getDF(String field, BytesRef text){
        Integer DF = 0;
        Term term = new Term(field, text);
        try {
            DF = ireader.docFreq(term);
        } catch (IOException ex) {
            log.error(ex);
        }
        return DF;
    }
    
    /**
     * 
     * <code>getDocumentLength</code> extracts the length of the given field the given document
     * 
     * @param docId
     * @param field
     * @return length of the given field the given document
     */
    public Long getDocumentLength(int docId, String field){
        Long dLenght = 0L;
        try {
            Terms terms = ireader.getTermVector(docId, field);
            if (terms != null && terms.size() > 0) {
                TermsEnum termsEnum = terms.iterator(null); // access the terms for this field
                BytesRef t = null;
                while ((t = termsEnum.next()) != null) {// explore the terms for this field
                    DocsEnum docsEnum = termsEnum.docs(null, null); // enumerate through documents, in this case only one
                    int docIdEnum;
                    while ((docIdEnum = docsEnum.nextDoc()) != DocIdSetIterator.NO_MORE_DOCS) {
                        dLenght += docsEnum.freq();
                    }
                }
            }

        } catch (IOException ex) {
            log.error(ex);
        }
        return dLenght;
    }
    
    /**
     *
     * <code>getNumberofUniqTermsInDocument</code> gets number of unique terms in the given field of the given document
     * 
     * @param docId
     * @param field
     * @return
     */
    public Long getNumberofUniqTermsInDocument(int docId, String field){
        Long udl = 0L;
        try {
            Terms terms = ireader.getTermVector(docId, field);
             udl= terms.size();
        } catch (IOException ex) {
            log.error(ex);
        }
        return udl;
    }



    /**
     *<code>getNumOfAllTerms</code> gets number of all terms in the given field of index (summation of all document's length)
     * 
     * @param field
     * @return number of all terms in the given field of index
     */
    public Long getNumOfAllTerms(String field){
        if (numOfAllTerms == -1) {
            try {
                numOfAllTerms = ireader.getSumTotalTermFreq(field);
            } catch (IOException ex) {
               log.error(ex);
            }
        }
        return numOfAllTerms;
    }
    
    /**
     *
     * <code>getAvgDocLength</code> calculates average length of the given fields over all documents
     *
     * @param field
     * @return average length of the given fields over all documents
     */
    public Double getAvgDocLength(String field){
        if (AvgDocLength == -1) {
            Long sum = 0L;
            sum += this.getNumOfAllTerms(field);
            double avgDocLength = sum / (double) ireader.numDocs();
            AvgDocLength = avgDocLength;
        }
        return AvgDocLength;
    }    
    /**
     *<code>getNumOfAllUniqueTerms_PerField</code> gets number of unique terms in the given field of index
     * 
     * @param field
     * @return number of unique terms in the given field of index
     */
    public Long getNumOfAllUniqueTerms_PerField(String field){
        if (termCounts == null) {
            countTerms();
        }
        return termCounts.get(field);
    }
    
    /**
     *<code>getNumOfAllUniqueTerms_OverAllFields</code> gets number of unique terms over all fields of index
     * 
     * @return number of unique terms over all fields of index
     */
    public Long getNumOfAllUniqueTerms_OverAllFields(){
        if (numOfAllUniqTerms == -1) {
            countTerms();
        }
        return numOfAllUniqTerms;
    }
   
    private void countTerms(){
        try {
            termCounts = new HashMap<String, Long>();
            numOfAllUniqTerms = 0L;
            Fields fields = MultiFields.getFields(ireader);
            // if there are no postings, throw an exception
            if (fields == null) {
                log.warn("There are no postings in the index reader.");
            }
            Iterator<String> fe = fields.iterator();
            TermsEnum te = null;
            while (fe.hasNext()) {
                String fld = fe.next();
                Long termCount = 0L;
                Terms terms = fields.terms(fld);
                if (terms != null) { // count terms
                    te = terms.iterator(te);
                    while (te.next() != null) {
                        termCount++;
                        numOfAllUniqTerms++;
                    }
                }
                termCounts.put(fld, termCount);
            }
        } catch (IOException ex) {
            log.error(ex);
        }
    }
    
    /**
     *
     *<code>getTopTerms_DF</code> extracts top k most frequent terms in the given field of index in terms of Document Frequency 
     * 
     * @param field
     * @param numTerms Threshold for number of terms in the output list
     * @return top k most frequent terms in the given field of index in terms of Document Frequency
     */
    public TermStats[] getTopTerms_DF(String field, Integer numTerms){
        if (topTermsDF == null) {
            HighFreqTerms HIT = new HighFreqTerms(ireader);
            topTermsDF = HIT.getHighDFTerms(numTerms, field);
      }
      return topTermsDF;
    }
    
     /**
     *
     *<code>getTopTerms_DF</code> extracts top k most frequent terms in the given field of index in terms of Term Frequency 
     * 
     * @param field
     * @param numTerms Threshold for number of terms in the output list
     * @return top k most frequent terms in the given field of index in terms of Term Frequency
     */
    public TermStats[] getTopTerms_TF(String field, Integer numTerms){
        if (topTermsTF == null) {
            HighFreqTerms HIT = new HighFreqTerms(ireader);
            topTermsTF = HIT.getHighTFTerms(numTerms, field);
      }
      return topTermsTF;
    }
    
    /**
     *<code>getFieldNames</code> gets list of the names of all index fields.
     * @return list of the names of all index fields
     */
    public List<String> getFieldNames() {
        if(fieldNames == null){
            fieldNames.addAll(this.fieldNames(ireader, false));
        }
        return fieldNames;
     }
    
    private Collection<String> fieldNames(IndexReader r, boolean indexedOnly){
        AtomicReader reader = null;
        if (r instanceof CompositeReader) {
            try {
                reader = SlowCompositeReaderWrapper.wrap(r);
            } catch (IOException ex) {
                log.error(ex);
            }
        } else {
            reader = (AtomicReader) r;
        }
        Set<String> res = new HashSet<String>();
        FieldInfos infos = reader.getFieldInfos();
        for (FieldInfo info : infos) {
            if (indexedOnly && info.isIndexed()) {
                res.add(info.name);
                continue;
            }
            res.add(info.name);
        }
        return res;
    }
}

