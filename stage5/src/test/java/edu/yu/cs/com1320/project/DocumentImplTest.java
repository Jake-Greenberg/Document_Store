package edu.yu.cs.com1320.project;

import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Unit test for simple App.
 */
class DocumentImplTest {
    /**
     * Testing Basic Text Document
     * 
     * @throws URISyntaxException
     */
    @Test
  public void docTest() throws URISyntaxException {
    URI me1 = new URI("hello1");
    URI me2 = new URI("hello2");
    String s1 = "Four xcor and ajdfjajfjf this i the fghhghg jdfjdjkjfdkfkdsfk sdjfdf this is my password backwards ffsfsdf%^&*^%&^%&^";
    byte[] b1 = { 1, 2, 4, 56, 7, 7, 6, 5, 43, 4, 6, 7, 8, 8, 8, 55, 52, 5, 2, 52, 75, 95, 25, 85, 74, 52, 52, 5, 67,
        61 };
    Document doc1 = new DocumentImpl(me1, s1);
    Document doc2 = new DocumentImpl(me2, b1);
    assertEquals("Four xcor and ajdfjajfjf this i the fghhghg jdfjdjkjfdkfkdsfk sdjfdf this is my password backwards ffsfsdf%^&*^%&^%&^", doc1.getDocumentTxt());
    assertEquals(b1, doc2.getDocumentBinaryData());
  }
    @Test
    void TestBasicTxtDocument() throws URISyntaxException {
        URI uri = new URI ("Hello");
        DocumentImpl text = new DocumentImpl(uri, "Hello");
        assertEquals("Hello", text.getDocumentTxt());
        assertEquals(null, text.getDocumentBinaryData());
        assertEquals(uri, text.getKey());
        assertEquals(true, text.equals(new DocumentImpl(uri, "Hello")));
    }
    @Test
    void TestBasicBinaryDocument() throws URISyntaxException {
        String str = "Hello";
        URI uri = new URI ("Hello");
        byte[] test = str.getBytes();
        DocumentImpl binary = new DocumentImpl(uri, test);
        assertEquals(null, binary.getDocumentTxt());
        assertEquals(test, binary.getDocumentBinaryData());
        assertEquals(uri, binary.getKey());
        assertTrue(binary.equals(new DocumentImpl(uri, test)));
    }

    @Test
    void illegal () throws URISyntaxException {
        boolean test = false;
        String str = "Hello";
        URI uri = new URI ("Hello");
        byte[] byteArray = str.getBytes();
        String nullString = null;
        URI nullUri = null;
        byte[] nullArray = null;
        String emptyString = "";
        byte[] emptyArray = new byte[0];
        URI emptyUri = new URI("");
        try{
            DocumentImpl testNullUriInString = new DocumentImpl(nullUri, str);
        } catch (IllegalArgumentException e) {
            test=true;
        }
        assertEquals(true, test);
        test=false;
        try{
            DocumentImpl TestNullUriInByte = new DocumentImpl(nullUri, byteArray); 
        } catch (IllegalArgumentException e) {
            test=true;
        }
        assertEquals(true, test);
        test=false;
        try{
            DocumentImpl TestNullStringInString = new DocumentImpl(uri, nullString); 
        } catch (IllegalArgumentException e) {
            test=true;
        }
        assertEquals(true, test);
        test=false;
        try{
            DocumentImpl TestNullByteInByte = new DocumentImpl(uri, nullArray); 
        } catch (IllegalArgumentException e) {
            test=true;
        }
        assertEquals(true, test);
        test=false;
        try{
            DocumentImpl TestEmptyStringInString = new DocumentImpl(uri, emptyString); 
        } catch (IllegalArgumentException e) {
            test=true;
        }
        assertEquals(true, test);
        test=false;
        try{
            DocumentImpl TestEmptyByteInByte = new DocumentImpl(uri, emptyArray); 
        } catch (IllegalArgumentException e) {
            test=true;
        }
        assertEquals(true, test);
        test=false;
        try{
            DocumentImpl TestEmptyUriInByte = new DocumentImpl(emptyUri, byteArray); 
        } catch (IllegalArgumentException e) {
            test=true;
        }
        assertEquals(true, test);
        test=false;
        try{
            DocumentImpl TestEmptyUriInString = new DocumentImpl(emptyUri, str); 
        } catch (IllegalArgumentException e) {
            test=true;
        }
        assertEquals(true, test);
        test=false;

    }
}
