package edu.yu.cs.com1320.project;

import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentPersistenceManager;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentStoreStage4Test {
     //variables to hold possible values for doc1
     private URI uri1;
     private String txt1;
 
     //variables to hold possible values for doc2
     private URI uri2;
     String txt2;

     private URI uri3;
     String txt3;

     private URI uri4;
     String txt4;

     private URI uri5;
     String txt5;

    @BeforeEach
    public void init() throws Exception {
        //init possible values for doc1
        this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txt1 = "Very short text doc";

        //init possible values for doc2
        this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txt2 = "A very averagely sized text document";

        //init possible values for doc3
        this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txt3 = "Very very very very very very very very long text document";

        //init possible values for doc4
        this.uri4 = new URI("http://edu.yu.cs/com1320/prject/doc4");
        this.txt4 = "A second very very very very very very long text document";

        this.uri5 = new URI("http://edu.yu.cs/com1320/prject/doc5");
        this.txt5 = "Random words to help with search tests";
    }
    //     @Test
    //     public void putDocumentNotEnoughDocStorage() throws IOException{
    //     DocumentStore store = new DocumentStoreImpl();
    //     store.setMaxDocumentCount(3);
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
    //     // Should not be enough room for a fourth document so should remove first doc references  
    //     store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT);
    //     assertNull(store.getDocument(this.uri1));
    //     assertNotNull(store.getDocument(this.uri2));
    //     assertNotNull(store.getDocument(this.uri3));
    //     assertNotNull(store.getDocument(this.uri4));
    // }
    // @Test
    // public void removeCommandInCommandSetFromStackDueToStorage() throws IOException {
    //     DocumentStore store = new DocumentStoreImpl();
    //     // assertThrows(IllegalStateException.class, () ->  store.undo(uri1));
    //     store.setMaxDocumentCount(3);
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
    //     // Create a commandSet with all 3 documents (all have the word very in them)
    //     store.deleteAll("very");
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
    //     // Should delete all references to doc1 including the commands in the commandStack
    //     store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()), this.uri4, DocumentStore.DocumentFormat.TXT); //Might not be right
    //     // Makes sure that stack no longer contains any commands performed on uri1
    //     assertThrows(IllegalStateException.class, () ->  store.undo(uri1));
    //     store.undo(uri2);
    //     assertNull(store.getDocument(uri2));
    //     store.undo(uri2);
    //     assertNotNull(store.getDocument(uri2));
    // }
    // @Test
    // public void putReplacingDocNoDocStorageProblem() throws IOException{
    //     DocumentStore store = new DocumentStoreImpl();
    //     store.setMaxDocumentCount(2);
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.undo(uri2);
    //     assertNotNull(store.getDocument(uri1));
    //     assertNotNull(store.getDocument(uri2));
    //     assertEquals(txt2, store.getDocument(uri2).getDocumentTxt());
        
        
    // }
    // @Test
    // public void heapAfterSearchCall() throws IOException{
    //     DocumentStore store = new DocumentStoreImpl();
    //     store.setMaxDocumentCount(3);
    //     // Purposely put these first to ensure that document 1's time is changed as a result of search call
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     assertEquals(1, store.search("averagely").size());
    //     assertEquals(1, store.search("long").size());
    //     store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
    //     assertNull(store.getDocument(uri1));
    //     assertNotNull(store.getDocument(uri2));
    //     assertNotNull(store.getDocument(uri3));
    //     assertNotNull(store.getDocument(uri4));

    // }
    // @Test //WHEN REPLACING DOC IN UNDO LAMBDA, MAKE SURE TO THIS.DOCUMENTHEAP.REMOVE() THE DOC CURRENTLY THERE AND THEN INSERT THE OLD DOCUMENT INTO THE HEAP
    // public void heapAfterSearchPrefixCall() throws IOException{
    //     DocumentStore store = new DocumentStoreImpl();
    //     store.setMaxDocumentCount(4);
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt5.getBytes()), this.uri5, DocumentStore.DocumentFormat.TXT);
    //     assertEquals(3, store.searchByPrefix("doc").size());
    //     store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
    //     assertNull(store.getDocument(uri5));
    //     assertNotNull(store.getDocument(uri1));
    //     assertNotNull(store.getDocument(uri2));
    //     assertNotNull(store.getDocument(uri3));
    //     assertNotNull(store.getDocument(uri4));
    // }
    // @Test
    // public void heapAfterGetDocumentCall()throws IOException{
    //     DocumentStore store = new DocumentStoreImpl();
    //     store.setMaxDocumentCount(3);
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     store.getDocument(uri2);
    //     store.getDocument(uri3);
    //     store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
    //     assertNull(store.getDocument(uri1));
    // }
    // @Test
    // public void removeGenericCommandFromStackDueToStorage() throws IOException {
    //     DocumentStore store = new DocumentStoreImpl();
    //     store.setMaxDocumentCount(3);
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt4.getBytes()),this.uri4, DocumentStore.DocumentFormat.TXT);
    //     assertThrows(IllegalStateException.class, () ->  store.undo(uri1));  
    // }
    // @Test
    // public void putDocumentNotEnoughByteStorage() throws IOException{
    //     DocumentStore store = new DocumentStoreImpl();
    //     store.setMaxDocumentBytes(80);
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.search("short");
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
    //     assertNull(store.getDocument(uri2));
    // }
    // @Test 
    // public void putReplacingDocNoByteStorageProblem() throws IOException{
    //     DocumentStore store = new DocumentStoreImpl();
    //     store.setMaxDocumentBytes(77);
    //     store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
    //     // Makes sure uri1 was not removed due to "insufficient storage"
    //     assertNotNull(store.getDocument(uri1));
    // }
    @Test
    public void testDocPersistenceManager() throws URISyntaxException{
        URI uri = new URI("https://Sam/Jake/Murder");
        String text = "Please Hashem- let this work";
        Document doc = new DocumentImpl(uri, text);
        BTreeImpl btree = new BTreeImpl<>();
        DocumentPersistenceManager dpm = new DocumentPersistenceManager(null);
        btree.setPersistenceManager(dpm);
        try {
            dpm.serialize(uri, doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //     Document document = null;
        // try {
        //     document = dpm.deserialize(uri);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
        // assertEquals(doc.getDocumentTxt(), document.getDocumentTxt());
        // System.out.println(doc.getWordMap());
        // System.out.println(document.getWordMap());
        // try {
        //     dpm.delete(uri);
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }

    }

}
// Bytes of doc1 = 19
// Bytes of doc2 = 36
// Bytes of doc3 = 58
// Bytes of doc4 = 57