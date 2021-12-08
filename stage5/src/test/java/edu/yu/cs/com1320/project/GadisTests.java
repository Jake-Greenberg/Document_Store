package edu.yu.cs.com1320.project;

import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.stage5.impl.*;
import edu.yu.cs.com1320.project.stage5.*;
public class GadisTests{
    //Adapted from the tests previously posted on piazza:
@Test
public void testPutNullDeletion() throws IOException {
    DocumentStore documentStore = new DocumentStoreImpl();
    String string1 = "It was a dark and stormy night";
    String string2 = "It was the best of times, it was the worst of times";
    String string3 = "It was a bright cold day in April, and the clocks were striking thirteen";
    String string4 = "I am free, no matter what rules surround me.";
    byte[] bytes3 = string3.getBytes();
    byte[] bytes4 = string4.getBytes();
    InputStream inputStream1 = new ByteArrayInputStream(string1.getBytes());
    InputStream inputStream2 = new ByteArrayInputStream(string2.getBytes());
    InputStream inputStream3 = new ByteArrayInputStream(bytes3);
    InputStream inputStream4 = new ByteArrayInputStream(bytes4);
    URI uri1 =  URI.create("www.wrinkleintime.com");
    URI uri2 =  URI.create("www.taleoftwocities.com");
    URI uri3 =  URI.create("www.1984.com");
    URI uri4 =  URI.create("www.themoonisaharshmistress.com");
    int putTXT1 = documentStore.putDocument(inputStream1,uri1,DocumentStore.DocumentFormat.TXT);
    int putTXT2 = documentStore.putDocument(inputStream2,uri2,DocumentStore.DocumentFormat.TXT);
    int putBINARY1 = documentStore.putDocument(inputStream3,uri3,DocumentStore.DocumentFormat.BINARY);
    int putBINARY2 = documentStore.putDocument(inputStream4,uri4,DocumentStore.DocumentFormat.BINARY);
    assertEquals(putTXT1,0);
    assertEquals(putTXT2,0);
    assertEquals(putBINARY1,0);
    assertEquals(putBINARY2,0);
    documentStore.putDocument(null,uri1,DocumentStore.DocumentFormat.TXT);
    documentStore.putDocument(null,uri2,DocumentStore.DocumentFormat.TXT);
    documentStore.putDocument(null,uri3,DocumentStore.DocumentFormat.BINARY);
    documentStore.putDocument(null,uri4,DocumentStore.DocumentFormat.BINARY);
    Document nullDoc1 = documentStore.getDocument(uri1);
    Document nullDoc2 = documentStore.getDocument(uri2);
    Document nullDoc3 = documentStore.getDocument(uri3);
    Document nullDoc4 = documentStore.getDocument(uri4);
    assertEquals(nullDoc1,null);
    assertEquals(nullDoc2,null);
    assertEquals(nullDoc3,null);
    assertEquals(nullDoc4,null);
}
@Test
public void testSimplePutValues() throws IOException {
    DocumentStore documentStore = new DocumentStoreImpl();
    String string1 = "It was a dark and stormy night";
    String string2 = "It was the best of times, it was the worst of times";
    String string3 = "It was a bright cold day in April, and the clocks were striking thirteen";
    String string4 = "I am free, no matter what rules surround me.";
    byte[] bytes3 = string3.getBytes();
    byte[] bytes4 = string4.getBytes();
    InputStream inputStream1 = new ByteArrayInputStream(string1.getBytes());
    InputStream inputStream2 = new ByteArrayInputStream(string2.getBytes());
    InputStream inputStream3 = new ByteArrayInputStream(bytes3);
    InputStream inputStream4 = new ByteArrayInputStream(bytes4);
    URI uri1 =  URI.create("www.wrinkleintime.com");
    URI uri2 =  URI.create("www.taleoftwocities.com");
    URI uri3 =  URI.create("www.1984.com");
    URI uri4 =  URI.create("www.themoonisaharshmistress.com");
    int putTXT1 = documentStore.putDocument(inputStream1,uri1,DocumentStore.DocumentFormat.TXT);
    int putTXT2 = documentStore.putDocument(inputStream2,uri2,DocumentStore.DocumentFormat.TXT);
    int putBINARY1 = documentStore.putDocument(inputStream3,uri3,DocumentStore.DocumentFormat.BINARY);
    int putBINARY2 = documentStore.putDocument(inputStream4,uri4,DocumentStore.DocumentFormat.BINARY);
    assertEquals(putTXT1,0);
    assertEquals(putTXT2,0);
    assertEquals(putBINARY1,0);
    assertEquals(putBINARY2,0);
}
@Test
public void testCollisionPutValues() throws IOException {
    DocumentStore documentStore = new DocumentStoreImpl();
    String string1 = "It was a dark and stormy night";
    String string2 = "It was the best of times, it was the worst of times";
    String string3 = "It was a bright cold day in April, and the clocks were striking thirteen";
    String string4 = "I am free, no matter what rules surround me.";
    byte[] bytes3 = string3.getBytes();
    byte[] bytes4 = string4.getBytes();
    InputStream inputStream1 = new ByteArrayInputStream(string1.getBytes());
    InputStream inputStream2 = new ByteArrayInputStream(string2.getBytes());
    InputStream inputStream3 = new ByteArrayInputStream(bytes3);
    InputStream inputStream4 = new ByteArrayInputStream(bytes4);
    URI uri1 =  URI.create("www.wrinkleintime.com");
    URI uri2 =  URI.create("www.taleoftwocities.com");
    URI uri3 =  URI.create("www.1984.com");
    URI uri4 =  URI.create("www.themoonisaharshmistress.com");
    documentStore.putDocument(inputStream1,uri1,DocumentStore.DocumentFormat.TXT);
    int putTXT2 = documentStore.putDocument(inputStream2,uri2,DocumentStore.DocumentFormat.TXT);
    int putBINARY1 = documentStore.putDocument(inputStream3,uri3,DocumentStore.DocumentFormat.BINARY);
    int putBINARY2 = documentStore.putDocument(inputStream4,uri4,DocumentStore.DocumentFormat.BINARY);
    int test1 = documentStore.getDocument(uri1).hashCode();
    int test2 = documentStore.getDocument(uri2).hashCode();
    int test3 = documentStore.getDocument(uri3).hashCode();
    int test4 = documentStore.getDocument(uri4).hashCode();
    InputStream inputStream1b = new ByteArrayInputStream(string1.getBytes());
    InputStream inputStream2b = new ByteArrayInputStream(string2.getBytes());
    InputStream inputStream3b = new ByteArrayInputStream(bytes3);
    InputStream inputStream4b = new ByteArrayInputStream(bytes4);
    int putBINARY1Collision = documentStore.putDocument(inputStream1b,uri1,DocumentStore.DocumentFormat.BINARY);
    int putBINARY2Collision = documentStore.putDocument(inputStream2b,uri2,DocumentStore.DocumentFormat.BINARY);
    int putTXT1Collision = documentStore.putDocument(inputStream3b,uri3,DocumentStore.DocumentFormat.TXT);
    int putTXT2Collision = documentStore.putDocument(inputStream4b,uri4,DocumentStore.DocumentFormat.TXT);
    Document document1 = new DocumentImpl(uri1, string1.getBytes());
    Document document2 = new DocumentImpl(uri2, string2.getBytes());
    Document document3 = new DocumentImpl(uri3, string3);
    Document document4 = new DocumentImpl(uri4, string4);
    assertEquals(putBINARY1Collision,test1);
    assertEquals(putBINARY2Collision,test2);
    assertEquals(putTXT1Collision,test3);
    assertEquals(putTXT2Collision,test4);
}
    
}
