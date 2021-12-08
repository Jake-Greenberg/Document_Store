package edu.yu.cs.com1320.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat;

//Jonathan Wenger's Tests
class DocumentStoreStageTwoTests {
    @Test
    void testStackUndo() throws IOException, URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        String str1 = "1"; byte[] array1 = str1.getBytes();
        ByteArrayInputStream stream1 = new ByteArrayInputStream(array1);
        ByteArrayInputStream stream11 = new ByteArrayInputStream(array1);
        URI uri1 = new URI("1");
        assertEquals(0, store.putDocument(stream1, uri1, DocumentFormat.BINARY));
        Document doc = new DocumentImpl(uri1, stream11.readAllBytes());
        assertEquals(doc, store.getDocument(uri1));
        store.undo();
        assertEquals(null, store.getDocument(uri1));
        boolean test = false;
        try {
            store.undo();
        } catch (IllegalStateException e) {
            test = true;
        }
        assertTrue(test);
    }
    @Test
    void testStackUndoUri() throws IOException, URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        String str1 = "1"; byte[] array1 = str1.getBytes();
        ByteArrayInputStream stream1 = new ByteArrayInputStream(array1); ByteArrayInputStream stream11 = new ByteArrayInputStream(array1);
        URI uri1 = new URI("1");
        assertEquals(0, store.putDocument(stream1, uri1, DocumentFormat.BINARY));
        Document doc = new DocumentImpl(uri1, stream11.readAllBytes());
        assertEquals(doc, store.getDocument(uri1));
        String str2 = "2";
        byte[] array2 = str2.getBytes();
        ByteArrayInputStream stream2 = new ByteArrayInputStream(array2);
        ByteArrayInputStream stream22 = new ByteArrayInputStream(array2);
        URI uri2 = new URI("2");
        assertEquals(0, store.putDocument(stream2, uri2, DocumentFormat.BINARY));
        Document doc2 = new DocumentImpl(uri2, stream22.readAllBytes());
        assertEquals(doc2, store.getDocument(uri2));
        store.undo(uri1);
        assertEquals(null, store.getDocument(uri1));
        assertEquals(doc2, store.getDocument(uri2));
    }
    @Test
    void testStackUriPutOverwrite() throws IOException, URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        String str1 = "1"; byte[] array1 = str1.getBytes();
        ByteArrayInputStream stream1 = new ByteArrayInputStream(array1); ByteArrayInputStream stream11 = new ByteArrayInputStream(array1);
        URI uri = new URI("1");
        assertEquals(0, store.putDocument(stream1, uri, DocumentFormat.BINARY));
        Document doc = new DocumentImpl(uri, stream11.readAllBytes());
        assertEquals(doc, store.getDocument(uri));
        String str2 = "2"; byte[] array2 = str2.getBytes();
        ByteArrayInputStream stream2 = new ByteArrayInputStream(array2); ByteArrayInputStream stream22 = new ByteArrayInputStream(array2);
        assertEquals(doc.hashCode(), store.putDocument(stream2, uri, DocumentFormat.BINARY));
        Document doc2 = new DocumentImpl(uri, stream22.readAllBytes());
        assertEquals(doc2, store.getDocument(uri));
        store.undo();
        assertNotEquals(doc2, store.getDocument(uri));
        assertEquals(doc, store.getDocument(uri));
        store.undo(); assertEquals(null, store.getDocument(uri));
    }
    @Test
    void testStackUriDeleteOverwrite() throws IOException, URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        String str1 = "1"; byte[] array1 = str1.getBytes();
        ByteArrayInputStream stream1 = new ByteArrayInputStream(array1); ByteArrayInputStream stream11 = new ByteArrayInputStream(array1);
        URI uri = new URI("1");
        assertEquals(0, store.putDocument(stream1, uri, DocumentFormat.BINARY));
        Document doc = new DocumentImpl(uri, stream11.readAllBytes());
        assertEquals(doc, store.getDocument(uri));
        String str2 = "2"; byte[] array2 = str2.getBytes();
        ByteArrayInputStream stream2 = new ByteArrayInputStream(array2); ByteArrayInputStream stream22 = new ByteArrayInputStream(array2);
        assertEquals(doc.hashCode(), store.putDocument(stream2, uri, DocumentFormat.BINARY));
        Document doc2 = new DocumentImpl(uri, stream22.readAllBytes());
        assertEquals(doc2, store.getDocument(uri));
        assertTrue(store.deleteDocument(uri)); assertEquals(null, store.getDocument(uri));
        String str3 = "3"; byte[] array3 = str3.getBytes();
        ByteArrayInputStream stream3 = new ByteArrayInputStream(array3); ByteArrayInputStream stream33 = new ByteArrayInputStream(array3);
        assertEquals(0, store.putDocument(stream3, uri, DocumentFormat.BINARY));
        Document doc3 = new DocumentImpl(uri, stream33.readAllBytes());
        assertEquals(doc3, store.getDocument(uri));
        store.undo(uri); 
        assertEquals(null, store.getDocument(uri));
        store.undo(uri); 
        assertEquals(doc2, store.getDocument(uri));
        store.undo(uri); 
        assertEquals(doc, store.getDocument(uri));
        store.undo(uri); 
        assertEquals(null, store.getDocument(uri));
    }
    @Test
    void testStackUriDeleteOverwriteNoParams() throws IOException, URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        String str1 = "1"; byte[] array1 = str1.getBytes();
        ByteArrayInputStream stream1 = new ByteArrayInputStream(array1); ByteArrayInputStream stream11 = new ByteArrayInputStream(array1);
        URI uri = new URI("1");
        assertEquals(0, store.putDocument(stream1, uri, DocumentFormat.BINARY));
        Document doc = new DocumentImpl(uri, stream11.readAllBytes());
        assertEquals(doc, store.getDocument(uri));
        String str2 = "2"; byte[] array2 = str2.getBytes();
        ByteArrayInputStream stream2 = new ByteArrayInputStream(array2); ByteArrayInputStream stream22 = new ByteArrayInputStream(array2);
        assertEquals(doc.hashCode(), store.putDocument(stream2, uri, DocumentFormat.BINARY));
        Document doc2 = new DocumentImpl(uri, stream22.readAllBytes());
        assertEquals(doc2, store.getDocument(uri));
        assertTrue(store.deleteDocument(uri)); assertEquals(null, store.getDocument(uri));
        String str3 = "3"; byte[] array3 = str3.getBytes();
        ByteArrayInputStream stream3 = new ByteArrayInputStream(array3); ByteArrayInputStream stream33 = new ByteArrayInputStream(array3);
        assertEquals(0, store.putDocument(stream3, uri, DocumentFormat.BINARY));
        Document doc3 = new DocumentImpl(uri, stream33.readAllBytes());
        assertEquals(doc3, store.getDocument(uri));
        store.undo(); assertEquals(null, store.getDocument(uri));
        store.undo(); assertEquals(doc2, store.getDocument(uri));
        store.undo(); assertEquals(doc, store.getDocument(uri));
        store.undo(); assertEquals(null, store.getDocument(uri));
    }
    @Test
    void testStackUriDeleteOverwriteMultipleDocs() throws IOException, URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        String str1 = "1"; byte[] array1 = str1.getBytes();
        ByteArrayInputStream stream1 = new ByteArrayInputStream(array1); ByteArrayInputStream stream11 = new ByteArrayInputStream(array1);
        URI uri = new URI("1");
        assertEquals(0, store.putDocument(stream1, uri, DocumentFormat.BINARY));
        Document doc = new DocumentImpl(uri, stream11.readAllBytes());
        assertEquals(doc, store.getDocument(uri));
        String str2 = "2"; byte[] array2 = str2.getBytes();
        ByteArrayInputStream stream2 = new ByteArrayInputStream(array2); ByteArrayInputStream stream22 = new ByteArrayInputStream(array2);
        assertEquals(doc.hashCode(), store.putDocument(stream2, uri, DocumentFormat.BINARY));
        Document doc2 = new DocumentImpl(uri, stream22.readAllBytes());
        assertEquals(doc2, store.getDocument(uri));
        assertTrue(store.deleteDocument(uri)); assertEquals(null, store.getDocument(uri));
        String str3 = "3"; byte[] array3 = str3.getBytes();
        URI uri2 = new URI("Hello");
        ByteArrayInputStream stream3 = new ByteArrayInputStream(array3); ByteArrayInputStream stream33 = new ByteArrayInputStream(array3);
        assertEquals(0, store.putDocument(stream3, uri2, DocumentFormat.BINARY));
        Document doc3 = new DocumentImpl(uri2, stream33.readAllBytes());
        assertEquals(doc3, store.getDocument(uri2));
        store.undo(uri); assertEquals(doc3, store.getDocument(uri2)); assertEquals(doc2, store.getDocument(uri));
        store.undo(); assertEquals(null, store.getDocument(uri2));
        store.undo(); assertEquals(doc, store.getDocument(uri));
        store.undo(); assertEquals(null, store.getDocument(uri));
    }
    @Test
public void undoTest() throws IOException {
    DocumentStore documentStore = new DocumentStoreImpl();

    String string1 = "It was a dark and stormy night";
    String string2 = "It was the best of times, it was the worst of times";
    String string3 = "It was a bright cold day in April, and the clocks were striking thirteen";
    InputStream inputStream1 = new ByteArrayInputStream(string1.getBytes());
    InputStream inputStream2 = new ByteArrayInputStream(string2.getBytes());
    InputStream inputStream3 = new ByteArrayInputStream(string3.getBytes());
    URI uri1 = URI.create("www.wrinkleintime.com");

    documentStore.putDocument(inputStream1, uri1, DocumentFormat.TXT);
    assertEquals(string1, documentStore.getDocument(uri1).getDocumentTxt());
    documentStore.putDocument(inputStream2, uri1, DocumentFormat.TXT);
    assertEquals(string2, documentStore.getDocument(uri1).getDocumentTxt());
    documentStore.undo();
    assertEquals(string1, documentStore.getDocument(uri1).getDocumentTxt());
    documentStore.undo();
    assertEquals(null, documentStore.getDocument(uri1));

    documentStore.putDocument(inputStream3, uri1, DocumentFormat.TXT);
    assertEquals(string3, documentStore.getDocument(uri1).getDocumentTxt());
    documentStore.deleteDocument(uri1);
    assertEquals(null, documentStore.getDocument(uri1));
    documentStore.undo();
    assertEquals(string3, documentStore.getDocument(uri1).getDocumentTxt());
}
    @Test
    void testThrowsException() throws URISyntaxException, IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        boolean test = false;
        try {
            store.undo();
        } catch (IllegalStateException e) {
            test = true;
        }
        assertTrue(test);
        test = false;
        String str1 = "1"; byte[] array1 = str1.getBytes();
        ByteArrayInputStream stream1 = new ByteArrayInputStream(array1); ByteArrayInputStream stream11 = new ByteArrayInputStream(array1);
        URI uri = new URI("1");
        assertEquals(0, store.putDocument(stream1, uri, DocumentFormat.BINARY));
        Document doc = new DocumentImpl(uri, stream11.readAllBytes());
        assertEquals(doc, store.getDocument(uri));
        URI uriFake = new URI("ThisIsAFake");
        try {
            store.undo(uriFake);
        } catch (IllegalStateException e) {
            test = true;
        }
        assertTrue(test);
    }

    //all the things I commented out I think are incorrect

    /*
    @Test
    void testPointlessDeleteEmptyUndo() throws URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        URI uri = new URI("Pizza");
        assertFalse(store.deleteDocument(uri));
        store.undo();
    }*//* 
    @Test
    void testPointlessDeleteFullUndo() throws URISyntaxException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        URI uri = new URI("Pizza");
        assertFalse(store.deleteDocument(uri));
        store.undo(uri);
    }*//* 
    @Test
    void testPointlessPutEmptyUndo() throws URISyntaxException, IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        String str1 = "1"; 
        byte[] array1 = str1.getBytes();
        URI uri = new URI("1");
        assertEquals(0, store.putDocument(null, uri, DocumentFormat.TXT));
        store.undo();
    }*//* 
    @Test
    void testPointlessPutFullUndo() throws URISyntaxException, IOException {
        DocumentStoreImpl store = new DocumentStoreImpl();
        String str1 = "1";
        URI uri = new URI("1");
        assertEquals(0, store.putDocument(null, uri, DocumentFormat.TXT));
        assertNull(store.getDocument(uri));
        boolean test = false;
        try {
            store.undo(new URI("Pizza"));
        } catch (IllegalStateException e) {
            test = true;
        }
        assertTrue(test);
        store.undo(uri);
    } */
}