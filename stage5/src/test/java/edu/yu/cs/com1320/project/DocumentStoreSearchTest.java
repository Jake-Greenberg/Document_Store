package edu.yu.cs.com1320.project;

import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
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

public class DocumentStoreSearchTest {

        //variables to hold possible values for doc1
        private URI uri1;
        private String txt1;
    
        //variables to hold possible values for doc2
        private URI uri2;
        String txt2;

        private URI uri3;
        String txt3;
    
        @BeforeEach
        public void init() throws Exception {
            //init possible values for doc1
            this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
            this.txt1 = "Apple Apple Pizza Fish Pie Pizza Apple";
    
            //init possible values for doc2
            this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
            this.txt2 = "Pizza Pizza Pizza Pizza Pizza";

            //init possible values for doc3
            this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
            this.txt3 = "Penguin Park Piccalo Pants Pain Possum";
        }
        @Test
            public void wordCountAndGetWordsTest() throws URISyntaxException {
            DocumentImpl txtDoc = new DocumentImpl(new URI("placeholder"), " The!se ARE? sOme   W@o%$rds with^ s**ymbols (m)ixed [in]. Hope    this test test passes!");
            assertEquals(0, txtDoc.wordCount("bundle"));
            assertEquals(1, txtDoc.wordCount("these"));
            assertEquals(1, txtDoc.wordCount("WORDS"));
            assertEquals(1, txtDoc.wordCount("S-Y-M-B-O-??-LS"));
            assertEquals(1, txtDoc.wordCount("p@A$$sse$s"));
            assertEquals(2, txtDoc.wordCount("tEst"));
            Set<String> words = txtDoc.getWords();
            assertEquals(12, words.size());
            assertTrue(words.contains("some"));

            DocumentImpl binaryDoc = new DocumentImpl(new URI("0110"), new byte[] {0,1,1,0});
            assertEquals(0, binaryDoc.wordCount("anythingYouPutHereShouldBeZero"));
            Set<String> words2 = binaryDoc.getWords();
            assertEquals(0, words2.size());
 }
    
        @Test
        public void basicSearchAndOrganizationTest() throws IOException {
            DocumentStore store = new DocumentStoreImpl();
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
            assertEquals(1, store.search("PiE").size());
            assertEquals(3, store.searchByPrefix("p").size());
            assertEquals(0, store.searchByPrefix("x").size());
            assertEquals(3, store.searchByPrefix("pi").size());
            assertEquals(5, store.search("PiZzA").get(0).wordCount("pizza"));
            assertEquals(6, store.searchByPrefix("p").get(0).getWords().size());
        }
        @Test
        public void basicSearchDeleteTest() throws IOException {
            DocumentStore store = new DocumentStoreImpl();
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
            assertEquals(1, store.search("PiE").size());
            assertEquals(3, store.searchByPrefix("p").size());
            assertEquals(1, store.search("possum").size());
            store.deleteDocument(this.uri3);
            DocumentImpl doc1 = new DocumentImpl(this.uri1, this.txt1);
            DocumentImpl doc2 = new DocumentImpl(this.uri2, this.txt2);
            DocumentImpl doc3 = new DocumentImpl(this.uri3, this.txt3);
            for (char c = 'a'; c<='z'; c++) {
                List<Document> list = store.searchByPrefix(Character.toString(c));
                if (list.size()!=0) {
                    assertNotEquals(doc3, list.get(0));
                    if ((!list.get(0).equals(doc1))&&(!list.get(0).equals(doc2))) {
                        fail();
                    }
                }
            }
            for (char c = '0'; c<='9'; c++) {
                List<Document> list = store.searchByPrefix(Character.toString(c));
                if (list.size()!=0) {
                    assertNotEquals(doc3, list.get(0));
                    if ((!list.get(0).equals(doc1))&&(!list.get(0).equals(doc2))) {
                        fail();
                    }
                }
            }
            assertEquals(0, store.search("possum").size());
            assertEquals(2, store.search("pizza").size());
            store.deleteDocument(this.uri2);
            assertEquals(1, store.search("pizza").size());
        }
        @Test
        public void basicPutOverwriteTest() throws IOException {
            DocumentStore store = new DocumentStoreImpl();
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            assertEquals(2, store.search("pizza").size());
            store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            System.out.println(store.getDocument(uri1).getDocumentTxt());
            assertEquals(1, store.search("pizza").size());
        }
        @Test
        public void testDeleteAndDeleteAll() throws IOException {
            DocumentStore store = new DocumentStoreImpl();
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
            assertEquals(2, store.search("pizza").size());
            store.deleteAll("PiZZa");
            assertEquals(0, store.search("pizza").size());
            assertNull(store.getDocument(this.uri1));
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            assertEquals(2, store.search("pizza").size());
            assertNotNull(store.getDocument(this.uri1));
            assertNotNull(store.getDocument(this.uri2));
            assertNotNull(store.getDocument(this.uri3));
            store.deleteAllWithPrefix("p");
            assertNull(store.getDocument(this.uri1));
            assertNull(store.getDocument(this.uri2));
            assertNull(store.getDocument(this.uri3));
        }
        @Test
        public void testUndoNoArgs() throws IOException {
            DocumentStore store = new DocumentStoreImpl();
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
            store.undo();
            assertEquals(null, store.getDocument(this.uri3));
            assertEquals(0, store.search("penguin").size());
            store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
            store.deleteAll("pizza");
            assertEquals(0, store.search("pizza").size());
            assertNull(store.getDocument(this.uri1));
            store.undo();
            assertEquals(2, store.search("pizza").size());
        }
        @Test
        public void testUndoWithArgs() throws IOException {
            DocumentStore store = new DocumentStoreImpl();
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
            assertEquals(1, store.search("apple").size());
            assertEquals(1, store.searchByPrefix("a").size());
            store.undo(this.uri1);
            assertEquals(0, store.search("apple").size());
            assertEquals(0, store.searchByPrefix("a").size());
        }
        @Test
        public void testUndoCommandSet() throws IOException {
            DocumentStore store = new DocumentStoreImpl();
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            assertEquals(2, store.deleteAll("pizza").size());
            store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()),this.uri3, DocumentStore.DocumentFormat.TXT);
            assertNotNull(store.getDocument(this.uri3));
            assertEquals(0, store.search("pizza").size());
            store.undo(uri1);
            assertEquals(1, store.search("pizza").size());
            assertEquals(4, store.search("pizza").get(0).getWords().size());
            store.undo(uri2);
            assertEquals(2, store.search("pizza").size());
            assertEquals(1, store.search("pizza").get(0).getWords().size());
            store.undo();
            assertNull(store.getDocument(this.uri3));
            assertEquals(0, store.search("penguin").size());
        }
        @Test
        public void testUndoCommandSet2() throws IOException {
            DocumentStore store = new DocumentStoreImpl();
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            store.deleteAll("pizza");
            assertEquals(0, store.search("pizza").size());
            store.undo(uri2);
            assertEquals(1, store.search("pizza").size());
            store.undo(uri2);
            assertEquals(0, store.search("pizza").size());
            boolean test = false;
            try {
                store.undo(uri2);
            } catch (IllegalStateException e) {
                test = true;
            }
            assertTrue(test);
            assertEquals(0, store.search("pizza").size());
            store.undo(uri1);
            assertEquals(1, store.searchByPrefix("app").size());
            assertEquals(1, store.search("pizza").size());
        }
    @Test
        public void removeCommandSet() throws IOException {
            DocumentStore store = new DocumentStoreImpl();
            store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()),this.uri1, DocumentStore.DocumentFormat.TXT);
            store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()),this.uri2, DocumentStore.DocumentFormat.TXT);
            store.deleteAll("pizza");
            assertEquals(0, store.search("pizza").size());
            store.undo(uri2);
            assertEquals(1, store.search("pizza").size());
            store.undo(uri1);
            assertEquals(2, store.search("pizza").size());
            store.undo();
            assertNull(store.getDocument(uri2));
            assertNotNull(store.getDocument(uri1));
            assertEquals(1, store.search("pizza").size());
        }
        @Test
public void simpleTrieTest() {
  Trie trie = new TrieImpl<Integer>();
  trie.put("APPLE123", 1);
  trie.put("APPLE123", 2);
  trie.put("APPLE123", 3);
  trie.put("WORD87", 8);
  trie.put("WORD87", 7);

  List<Integer> apple123List = trie.getAllSorted("apple123", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});//this comparator will order integers from lowest to highest
  List<Integer> word87List = trie.getAllSorted("word87", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});

  assertEquals(3, apple123List.size());
  assertEquals(2, word87List.size());
  assertEquals(1, apple123List.get(0));
  assertEquals(2, apple123List.get(1));
  assertEquals(3, apple123List.get(2));
  assertEquals(7, word87List.get(0));
  assertEquals(8, word87List.get(1));

  trie.put("app", 12);
  trie.put("app", 5);
  trie.put("ap", 4);

  List<Integer> apList = trie.getAllWithPrefixSorted("AP", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});
  List<Integer> appList = trie.getAllWithPrefixSorted("APP", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});

  assertEquals(6, apList.size());
  assertEquals(5, appList.size());
  assertEquals(12, apList.get(5));
  assertEquals(12, appList.get(4));

  Set<Integer> deletedAppPrefix = trie.deleteAllWithPrefix("aPp");
  assertEquals(5, deletedAppPrefix.size());
  assertTrue(deletedAppPrefix.contains(3));
  assertTrue(deletedAppPrefix.contains(5));

  apList = trie.getAllWithPrefixSorted("AP", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});
  appList = trie.getAllWithPrefixSorted("APP", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});

  assertEquals(1, apList.size());
  assertEquals(0, appList.size());

  trie.put("deleteAll", 100);
  trie.put("deleteAll", 200);
  trie.put("deleteAll", 300);

  List<Integer> deleteList = trie.getAllSorted("DELETEALL", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});

  assertEquals(3, deleteList.size());
  Set<Integer> thingsActuallyDeleted = trie.deleteAll("DELETEall");
  assertEquals(3, thingsActuallyDeleted.size());
  assertTrue(thingsActuallyDeleted.contains(100));

  deleteList = trie.getAllSorted("DELETEALL", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});

  assertEquals(0, deleteList.size());

  trie.put("deleteSome", 100);
  trie.put("deleteSome", 200);
  trie.put("deleteSome", 300);

  List<Integer> deleteList2 = trie.getAllSorted("DELETESOME", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});

  assertEquals(3, deleteList2.size());
  Integer twoHundred = (Integer) trie.delete("deleteSome", 200);
  Integer nullInt = (Integer) trie.delete("deleteSome", 500);
  assertEquals(200, twoHundred);
  assertNull(nullInt);

  deleteList2 = trie.getAllSorted("DELETESOME", (int1, int2) -> {
   if ((int) int1 < (int) int2) {
    return -1;
   } else if ((int) int2 < (int) int1) {
    return 1;
   }
   return 0;});

  assertEquals(2, deleteList2.size());
  assertFalse(deleteList2.contains(200));
 }
 @Test
public void complicatedTrieTest() {
    Trie trie = new TrieImpl<Integer>();
    trie.put("APPLE123", 1);
    trie.put("APPLE123", 2);
    trie.put("APPLE123", 3);
    trie.put("APPle87", 8);
    trie.put("aPpLe87", 7);
    List<Integer> appleList = trie.getAllSorted("apple123", (int1, int2) -> {
        if ((int) int1 < (int) int2) {
            return -1;
        } else if ((int) int2 < (int) int1) {
            return 1;
        }
        return 0;});
    appleList.addAll(trie.getAllSorted("apple87", (int1, int2) -> {
        if ((int) int1 < (int) int2) {
            return -1;
        } else if ((int) int2 < (int) int1) {
            return 1;
        }
        return 0;}));
    assertEquals(5, appleList.size());
    List<Integer> testSet = List.copyOf(appleList);
    Set<Integer> deleteSet = trie.deleteAllWithPrefix("app");
    assertEquals(5, deleteSet.size());
    assertEquals(deleteSet.size(), testSet.size());
    if (!deleteSet.containsAll(testSet)){
        fail();
    }
    System.out.println("you passed complicatedTrieTest, congratulations!!!");
}

//variables to hold possible values for doc1
    private URI uriA;
    private String txtA;

    //variables to hold possible values for doc2
    private URI uriB;
    String txtB;

    private URI uriC;
    String txtC;
    @Test
    public void complicatedDocumentStoreTest() throws IOException, URISyntaxException {
        //init possible values for doc1
        this.uriA = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txtA = "Apple Apple AppleProducts applesAreGood Apps APCalculus Apricots";

        //init possible values for doc2
        this.uriB = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txtB = "Apple Apple Apple Apple Apple";

        //init possible values for doc3
        this.uriC = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txtC = "APenguin APark APiccalo APants APain APossum";
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txtA.getBytes()), this.uriA, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txtB.getBytes()), this.uriB, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txtC.getBytes()), this.uriC, DocumentStore.DocumentFormat.TXT);
        List<Document> appleList = new ArrayList<>();
        appleList.addAll(store.searchByPrefix("ap"));
        assertEquals(3, appleList.size());
        List<URI> testSet = new ArrayList<>();
        for(Document doc :appleList){
            testSet.add(doc.getKey());
        }
        Set<URI> deleteSet = store.deleteAllWithPrefix("ap");
        assertEquals(3, deleteSet.size());
        assertEquals(deleteSet.size(), testSet.size());
        if (!deleteSet.containsAll(testSet)){
            fail();
        }
        System.out.println("you passed complicatedDocumentStoreTest, congratulations!!!");
    }
    @Test
    public void reallyComplicatedDocumentStoreUndoTest() throws IOException, URISyntaxException {
        //init possible values for doc1
        this.uriA = new URI("http://edu.yu.cs/com1320/project/doc1");
        this.txtA = "Apple Apple AppleProducts applesAreGood Apps APCalculus Apricots";

        //init possible values for doc2
        this.uriB = new URI("http://edu.yu.cs/com1320/project/doc2");
        this.txtB = "Apple Apple Apple Apple Apple";

        //init possible values for doc3
        this.uriC = new URI("http://edu.yu.cs/com1320/project/doc3");
        this.txtC = "APenguin APark APiccalo APants APain APossum";
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(this.txtA.getBytes()), this.uriA, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txtB.getBytes()), this.uriB, DocumentStore.DocumentFormat.TXT);
        store.putDocument(new ByteArrayInputStream(this.txtC.getBytes()), this.uriC, DocumentStore.DocumentFormat.TXT);
        List<Document> appleList = new ArrayList<>();
        appleList.addAll(store.searchByPrefix("ap"));
        assertEquals(3, appleList.size());
        store.undo(this.uriB);
        appleList = store.searchByPrefix("ap");
        assertEquals(2, appleList.size());
        List<URI> testSet = new ArrayList<>();
        for(Document doc :appleList){
            testSet.add(doc.getKey());
        }
        store.putDocument(new ByteArrayInputStream(this.txtB.getBytes()), this.uriB, DocumentStore.DocumentFormat.TXT);
        appleList = store.searchByPrefix("ap");
        assertEquals(3, appleList.size());
        Set<URI> deleteSet = store.deleteAllWithPrefix("ap");
        assertEquals(3, deleteSet.size());
        store.undo(this.uriA);
        store.undo(this.uriC);
        assertEquals(2, store.searchByPrefix("ap").size());
        deleteSet = store.deleteAllWithPrefix("ap");
        assertEquals(2, deleteSet.size());
        assertEquals(deleteSet.size(), testSet.size());
        if (!deleteSet.containsAll(testSet)){
            fail();
        }
        System.out.println("you passed reallyComplicatedDocumentStoreUndoTest, congratulations!!!");
    }
    @Test
public void testOrder() throws IOException, URISyntaxException{
this.uri1 = new URI("http://edu.yu.cs/com1320/project/doc1");
this.txt1 = "Apple Apple AppleProducts applesAreGood Apps APCalculus Apricots";

//init possible values for doc2
this.uri2 = new URI("http://edu.yu.cs/com1320/project/doc2");
this.txt2 = "Apple Apple Apple Apple Apple";

//init possible values for doc3
this.uri3 = new URI("http://edu.yu.cs/com1320/project/doc3");
this.txt3 = "APenguin APark APiccalo APants APain APossum";

URI uri4 = new URI("http://edu.yu.cs/com1320/project/doc4");
String txt4 = "ap APPLE apartment";
DocumentStoreImpl store = new DocumentStoreImpl();
store.putDocument(new ByteArrayInputStream(this.txt1.getBytes()), this.uri1, DocumentStore.DocumentFormat.TXT);
store.putDocument(new ByteArrayInputStream(this.txt2.getBytes()), this.uri2, DocumentStore.DocumentFormat.TXT);
store.putDocument(new ByteArrayInputStream(this.txt3.getBytes()), this.uri3, DocumentStore.DocumentFormat.TXT);
store.putDocument(new ByteArrayInputStream(txt4.getBytes()), uri4, DocumentStore.DocumentFormat.TXT);
List<Document> wordList = store.search("apple");
List<Document> prefixList = store.searchByPrefix("ap");
assertEquals(wordList.size(), 3);
assertEquals(wordList.get(0).getKey(), uri2);
assertEquals(wordList.get(1).getKey(), uri1);
assertEquals(wordList.get(2).getKey(), uri4);

assertEquals(prefixList.size(), 4);
assertEquals(prefixList.get(0).getKey(), uri1);
assertEquals(prefixList.get(1).getKey(), uri3);
assertEquals(prefixList.get(2).getKey(), uri2);

}


}
