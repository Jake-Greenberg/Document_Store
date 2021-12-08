package edu.yu.cs.com1320.project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassUtils;

import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.stage5.impl.DocumentImpl;
import edu.yu.cs.com1320.project.stage5.impl.DocumentStoreImpl;
import edu.yu.cs.com1320.project.stage5.DocumentStore.DocumentFormat;

public class RadinskyDocStoreTest {
    // much of this code was copied from the github repo
    URI[] uriArray = new URI[21];
    Document[] docArray = new Document[21];
    String[] stringArray = { 
            "The blue parrot drove by the hitchhiking mongoose.",
            "She thought there'd be sufficient time if she hid her watch.",
            "Choosing to do nothing is still a choice, after all.",
            "He found the chocolate covered roaches quite tasty.",
            "The efficiency we have at removing trash has made creating trash more acceptable.",
            "Peanuts don't grow on trees, but cashews do.",
            "A song can make or ruin a person's day if they let it get to them.",
            "You bite up because of your lower jaw.",
            "He realized there had been several deaths on this road, but his concern rose when he saw the exact number.",
            "So long and thanks for the fish.", "Three years later, the coffin was still full of Jello.",
            "Weather is not trivial - it's especially important when you're standing in it.",
            "He walked into the basement with the horror movie from the night before playing in his head.",
            "He wondered if it could be called a beach if there was no sand.",
            "Jeanne wished she has chosen the red button.",
            "It's much more difficult to play tennis with a bowling ball than it is to bowl with a tennis ball.",
            "Pat ordered a ghost pepper pie.",
            "Everyone says they love nature until they realize how dangerous she can be.",
            "The memory we used to share is no longer coherent.",
            "My harvest will come Tiny valorous straw Among the millions Facing to the sun",
            "A dreamy-eyed child staring into night On a journey to storyteller's mind Whispers a wish speaks with the stars the words are silent in him" };

    @Test
    void testUndo() throws IOException {
        for (int i = 0; i < 7; i++) {
            uriArray[i] = URI.create("www.google" + i + ".com");
        }

        for (int i = 0; i < 7; i++) {
            docArray[i] = new DocumentImpl(uriArray[i], stringArray[i]);
        }
        for (int i = 0; i < 7; i++) {
            docArray[i + 7] = new DocumentImpl(uriArray[i], stringArray[i + 7].getBytes());
        }
        for (int i = 0; i < 7; i++) {
            docArray[i + 14] = new DocumentImpl(uriArray[i], stringArray[i + 14]);
        }

        // first as TXT
        DocumentStore store = new DocumentStoreImpl();
        for (int i = 0; i < 7; i++) {
            store.putDocument(new ByteArrayInputStream(stringArray[i].getBytes()), uriArray[i], DocumentFormat.TXT);
        }
        store.undo();
        assertNotNull(store.getDocument(uriArray[3]));
        store.undo(uriArray[3]);
        assertNull(store.getDocument(uriArray[3]));

        // then as Binary
        DocumentStore stor = new DocumentStoreImpl();
        for (int i = 0; i < 7; i++) {
            stor.putDocument(new ByteArrayInputStream(stringArray[i].getBytes()), uriArray[i], DocumentFormat.BINARY);
        }
        stor.undo();
        assertNotNull(stor.getDocument(uriArray[3]));
        stor.undo(uriArray[3]);
        assertNull(stor.getDocument(uriArray[3]));
    }
    @Test
    void hasParameterlessPublicConstructorTestHashTable() {
        try {
            new HashTableImpl<>();
        } catch (RuntimeException e) {
            assertTrue(false);
        }
    }
    @Test
    void hasParameterlessPublicConstructorTestStack() {
        try {
            new StackImpl<>();
        } catch (RuntimeException e) {
            fail("no parameterless constructor");
        }
    }
    @Test
    void testParameterLessUndoOnDoc() throws URISyntaxException, IOException {
        URI uri = new URI("YouAreEye");
        String first = "first";
        String second = "second";
        Document one = new DocumentImpl(uri, first);
        Document two = new DocumentImpl(uri, second);
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(first.getBytes()), uri, DocumentFormat.TXT);
        assertEquals(store.getDocument(uri), one);
        assertEquals(store.putDocument(new ByteArrayInputStream(second.getBytes()), uri,
                DocumentFormat.TXT), one.hashCode());
        assertEquals(store.getDocument(uri), two);
        store.undo();
        assertEquals(store.getDocument(uri), one);
    }
    @Test
    void testParameterUndoOnDoc() throws URISyntaxException, IOException {
        URI uri = new URI("YouAreEye");
        String first = "first";
        String second = "second";
        Document one = new DocumentImpl(uri, first);
        Document two = new DocumentImpl(uri, second);
        DocumentStore store = new DocumentStoreImpl();
        store.putDocument(new ByteArrayInputStream(first.getBytes()), uri, DocumentFormat.TXT);
        assertEquals(store.getDocument(uri), one);
        assertEquals(store.putDocument(new ByteArrayInputStream(second.getBytes()), uri, DocumentFormat.TXT),
                one.hashCode());
        assertEquals(store.getDocument(uri), two);
        store.undo(uri);
        assertEquals(store.getDocument(uri), one);
    }
    @Test
    void testUndoWithEmptyStack() throws URISyntaxException {
        DocumentStore store = new DocumentStoreImpl();
        assertThrows(IllegalStateException.class, ()->{
            store.undo();
        });
        assertThrows(IllegalStateException.class, () -> {
            store.undo(new URI("uri"));
        });
    }
}
