package edu.yu.cs.com1320.project;

import java.io.ByteArrayInputStream;

import java.io.IOException;

import java.io.InputStream;

import java.net.URI;

import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import edu.yu.cs.com1320.project.impl.HashTableImpl;

import edu.yu.cs.com1320.project.impl.StackImpl;

import edu.yu.cs.com1320.project.stage5.DocumentStore;

import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;

import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;

public class TGTest {

    @Test

    void DocumentImplTest() throws URISyntaxException {

        URI uri1 = new URI("www.tuvwxyz.com");

        String txt = "YAGILU";

        URI uri2 = new URI("www.xyz.com");

        URI uri = new URI("cmpsci4days.com");

        String txt2 = "Up too late at night";

        byte[] pic = "Lots of Pics and Lots of Bytes".getBytes();

        byte[] pic2 = "Even more pix".getBytes();

        DocumentImpl doc1 = new DocumentImpl(uri1, txt);

        DocumentImpl pic1 = new DocumentImpl(uri2, pic2);

        DocumentImpl doc2 = new DocumentImpl(uri1, txt);

        assertEquals(uri1, doc1.getKey());

        assertEquals(uri2, pic1.getKey());

        assertEquals(txt, doc1.getDocumentTxt());

        assertEquals(pic2, pic1.getDocumentBinaryData());

        assertEquals(doc1.hashCode(), doc2.hashCode());

    }

    @Test

    void StackImplTest() {

        StackImpl stack = new StackImpl<Integer>();

        for (int i = 0; i < 30; i++) {

            stack.push(i);

        }

        assertEquals(29, stack.pop());

        assertEquals(28, stack.peek());

        assertEquals(29, stack.size());

        stack.push(123);

        assertEquals(123, stack.peek());

        assertEquals(123, stack.pop());

    }

    @Test

    void HashTableImplTest() {

        HashTableImpl table = new HashTableImpl<String, Integer>();

        table.put("WHY", 12);

        table.put("Do", 13);

        table.put("I Do", 14);

        table.put("Silly Things", 15);

        table.put("?", 16);

        assertEquals(12, table.get("WHY"));

        assertEquals(16, table.get("?"));

        assertNull(table.get("ALWAYS"));

        table.put("Always", 73);

        assertEquals(13, table.put("Do", 28347));

        assertEquals(28347, table.get("Do"));

        assertEquals(73, table.get("Always"));

        HashTableImpl test2 = new HashTableImpl<Integer, Integer>();

        for (int i = 0; i < 4000; i++) {

            test2.put(i, i + 1);

        }

        assertEquals(4000, test2.get(3999));

        assertEquals(2348, test2.put(2347, 1));

        assertEquals(1, test2.get(2347));

    }

    @Test

    void DocStoreTest() throws URISyntaxException, IOException {

        DocumentStoreImpl store = new DocumentStoreImpl();

        URI uri1 = new URI("www.tuvwxyz.com");

        String txt = "YAGILU";

        URI uri2 = new URI("www.xyz.com");

        URI uri = new URI("cmpsci4days.com");

        String txt2 = "Up too late at night";

        byte[] pic = "Lots of Pics and Lots of Bytes".getBytes();

        byte [] pic2 = "Even more pix".getBytes();

        DocumentImpl doc1 = new DocumentImpl(uri1, txt);

        DocumentImpl pic1 = new DocumentImpl(uri2, pic2);

        DocumentImpl doc2 = new DocumentImpl(uri1, txt);

        for (int i = 0; i < 30; i++) {

            String str = "";

            str += i + i * 2;

            String str2 = "";

            str2+= i * i;

            URI uri34 = new URI(str2);

            assertEquals(0,store.putDocument(new ByteArrayInputStream(str.getBytes()), uri34, DocumentStore.DocumentFormat.TXT));

        }

        DocumentImpl doc23 = new DocumentImpl(new URI("36"), "18");

        assertEquals(doc23,store.getDocument(new URI("36")));

        store.undo(new URI ("36"));

        assertNull(store.getDocument(new URI("36")));

        URI x = new URI("225");

        DocumentImpl doc225 = new DocumentImpl(x, "45");

        //Change the Value

        assertEquals(doc225.hashCode(), store.putDocument(new ByteArrayInputStream("1".getBytes()),x, DocumentStore.DocumentFormat.TXT));

        //Delete

        store.deleteDocument(x);

        //Make Sure it is gone

        assertNull(store.getDocument(x));

        //Put it back

        store.putDocument(new ByteArrayInputStream("123".getBytes()), x, DocumentStore.DocumentFormat.TXT);

        doc225 = new DocumentImpl(x, "123");

        assertEquals(doc225, store.getDocument(x));

        //Change the top of the command stack

        DocumentImpl doc764 = new DocumentImpl(new URI("764"), "789");

        store.putDocument(new ByteArrayInputStream("789".getBytes()), new URI ("764"), DocumentStore.DocumentFormat.TXT);

        store.undo(x);

        //Undid the put so should be deleted

        assertNull(store.getDocument(x));

        //should undo the delete

        store.undo(x);

        assertEquals(new DocumentImpl(x, "1"), store.getDocument(x));

        store.undo(x);

        assertEquals(new DocumentImpl(x, "45"), store.getDocument(x));

        //Undo the top of the command stack, whcih should get rid of doc764

        store.undo();

        assertNull(store.getDocument(new URI("764")));

    }
}

