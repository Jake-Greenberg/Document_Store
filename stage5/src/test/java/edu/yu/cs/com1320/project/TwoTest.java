package edu.yu.cs.com1320.project;

import static org.junit.jupiter.api.Assertions.*;
import edu.yu.cs.com1320.project.impl.*;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import edu.yu.cs.com1320.project.Stack;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage5.*;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat;

public class TwoTest{
    @Test
    public void oneStackImpl(){
        Stack<Integer> stack = new StackImpl<>();
        for(int i = 0; i < 100; i++){
            stack.push(i);
            
        }
        for(int i = 99; i >= 0; i--){
            assertEquals(stack.peek(), i);
            assertEquals(stack.pop(), i);
        }
        passMessage();
    }
//Code below belongs to Azriel Bachrach
    @Test
    public void simplePushAndPop() {
        Stack<String> s = new StackImpl<>();
        s.push("one");
        s.push("two");
        s.push("three");
        assertEquals(3, s.size());
        assertEquals("three", s.peek());
        assertEquals("three", s.pop());
        assertEquals("two", s.peek());
        assertEquals("two", s.peek());
        assertEquals(2, s.size());
        assertEquals("two", s.pop());
        assertEquals("one", s.pop());
        assertEquals(0, s.size());
        passMessage();
    }

    @Test
    public void aLotOfData() {
        Stack<Integer> s = new StackImpl<>();
        for (int i = 0; i < 1000; i++) {
            s.push(i);
            assertEquals((Integer)i, s.peek());
        }
        assertEquals(1000, s.size());
        assertEquals((Integer)999, s.peek());
        for (int i = 999; i >= 0; i--) {
            assertEquals((Integer)i, s.peek());
            assertEquals((Integer)i, s.pop());
        }
        assertEquals(0, s.size());
        passMessage();
    }
    public void passMessage(){
        System.out.println("\nPassed test \"" + Thread.currentThread().getStackTrace()[2].getMethodName() + "\"\n");
    }
    @Test
    public void undoTest() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();
        Boolean test = false;
        try {
            documentStore.undo();
        } catch (IllegalStateException e) {
            test = true;
        }
        assertEquals(true, test);
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
        passMessage();
    }
    @Test
    public void testUndoSpecificUri() throws IOException {
        DocumentStore documentStore = new DocumentStoreImpl();

        String string1 = "It was a dark and stormy night";
        String string2 = "It was the best of times, it was the worst of times";
        String string3 = "It was a bright cold day in April, and the clocks were striking thirteen";
        String string4 = "I am free, no matter what rules surround me.";
        InputStream inputStream1 = new ByteArrayInputStream(string1.getBytes());
        InputStream inputStream2 = new ByteArrayInputStream(string2.getBytes());
        InputStream inputStream3 = new ByteArrayInputStream(string3.getBytes());
        InputStream inputStream4 = new ByteArrayInputStream(string4.getBytes());
        URI uri1 = URI.create("www.wrinkleintime.com");
        URI uri2 = URI.create("www.taleoftwocities.com");
        URI uri3 = URI.create("www.1984.com");

        documentStore.putDocument(inputStream1, uri1, DocumentFormat.TXT);
        assertEquals(string1, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.putDocument(inputStream2, uri2, DocumentFormat.TXT);
        assertEquals(string2, documentStore.getDocument(uri2).getDocumentTxt());
        documentStore.undo(uri1);
        assertEquals(null, documentStore.getDocument(uri1));
        assertEquals(string2, documentStore.getDocument(uri2).getDocumentTxt());
        documentStore.putDocument(inputStream3, uri1, DocumentFormat.TXT);
        assertEquals(string3, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.putDocument(inputStream4, uri1, DocumentFormat.TXT);
        assertEquals(string4, documentStore.getDocument(uri1).getDocumentTxt());
        documentStore.deleteDocument(uri1);
        assertEquals(null, documentStore.getDocument(uri1));
        documentStore.undo(uri2);
        assertEquals(null, documentStore.getDocument(uri2));
        documentStore.undo();
        assertEquals(string4, documentStore.getDocument(uri1).getDocumentTxt());
        System.out.println(documentStore.getDocument(uri1));
        documentStore.undo(uri1);
        System.out.println(documentStore.getDocument(uri1));
        assertEquals(string3, documentStore.getDocument(uri1).getDocumentTxt());

        Boolean test = false;
        try {
            documentStore.undo(uri3);
        } catch (IllegalStateException e) {
            test = true;
        }
        assertEquals(true, test);
    }
}