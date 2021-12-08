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

public class DocumentStoreImplTestStage2 {
  URI[] uriArray = new URI[21];
  Document[] docArray = new Document[21];
  String[] stringArray = { "The blue parrot drove by the hitchhiking mongoose.",
      "She thought there'd be sufficient time if she hid her watch.",
      "Choosing to do nothing is still a choice, after all.", "He found the chocolate covered roaches quite tasty.",
      "The efficiency we have at removing trash has made creating trash more acceptable.",
      "Peanuts don't grow on trees, but cashews do.",
      "A song can make or ruin a person's day if they let it get to them.", "You bite up because of your lower jaw.",
      "He realized there had been several deaths on this road, but his concern rose when he saw the exact number.",
      "So long and thanks for the fish.", "Three years later, the coffin was still full of Jello.",
      "Weather is not trivial - it's especially important when you're standing in it.",
      "He walked into the basement with the horror movie from the night before playing in his head.",
      "He wondered if it could be called a beach if there was no sand.", "Jeanne wished she has chosen the red button.",
      "It's much more difficult to play tennis with a bowling ball than it is to bowl with a tennis ball.",
      "Pat ordered a ghost pepper pie.", "Everyone says they love nature until they realize how dangerous she can be.",
      "The memory we used to share is no longer coherent.",
      "My harvest will come Tiny valorous straw Among the millions Facing to the sun",
      "A dreamy-eyed child staring into night On a journey to storyteller's mind Whispers a wish speaks with the stars the words are silent in him" };

  @Test
  void testUndo() {
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
    DocumentStore documentStore = new DocumentStoreImpl();
    try {
      int testa1 = documentStore.putDocument(new ByteArrayInputStream(stringArray[0].getBytes()), uriArray[0],
          DocumentStore.DocumentFormat.TXT);
      int testa2 = documentStore.putDocument(new ByteArrayInputStream(stringArray[1].getBytes()), uriArray[1],
          DocumentStore.DocumentFormat.TXT);
      int testa3 = documentStore.putDocument(new ByteArrayInputStream(stringArray[2].getBytes()), uriArray[2],
          DocumentStore.DocumentFormat.TXT);
      int testa4 = documentStore.putDocument(new ByteArrayInputStream(stringArray[3].getBytes()), uriArray[3],
          DocumentStore.DocumentFormat.TXT);
      int testa5 = documentStore.putDocument(new ByteArrayInputStream(stringArray[4].getBytes()), uriArray[4],
          DocumentStore.DocumentFormat.TXT);
      int testa6 = documentStore.putDocument(new ByteArrayInputStream(stringArray[5].getBytes()), uriArray[5],
          DocumentStore.DocumentFormat.TXT);
      int testa7 = documentStore.putDocument(new ByteArrayInputStream(stringArray[6].getBytes()), uriArray[6],
          DocumentStore.DocumentFormat.TXT);
      assertEquals(testa1, 0);
      assertEquals(testa2, 0);
      assertEquals(testa3, 0);
      assertEquals(testa4, 0);
      assertEquals(testa5, 0);
      assertEquals(testa6, 0);
      assertEquals(testa7, 0);
    } catch (java.io.IOException e) {
      fail();
    }

    documentStore.undo();

    assertEquals(docArray[0], documentStore.getDocument(uriArray[0]));
    assertEquals(docArray[1], documentStore.getDocument(uriArray[1]));
    assertEquals(docArray[2], documentStore.getDocument(uriArray[2]));
    assertEquals(docArray[3], documentStore.getDocument(uriArray[3]));
    assertEquals(docArray[4], documentStore.getDocument(uriArray[4]));
    assertEquals(docArray[5], documentStore.getDocument(uriArray[5]));
    assertEquals(null, documentStore.getDocument(uriArray[6]));

    documentStore.undo(uriArray[1]);

    try {
      int testb1 = documentStore.putDocument(new ByteArrayInputStream(stringArray[7].getBytes()), uriArray[0],
          DocumentStore.DocumentFormat.BINARY);
      int testb2 = documentStore.putDocument(new ByteArrayInputStream(stringArray[8].getBytes()), uriArray[1],
          DocumentStore.DocumentFormat.BINARY);
      int testb3 = documentStore.putDocument(new ByteArrayInputStream(stringArray[9].getBytes()), uriArray[2],
          DocumentStore.DocumentFormat.BINARY);
      int testb4 = documentStore.putDocument(new ByteArrayInputStream(stringArray[10].getBytes()), uriArray[3],
          DocumentStore.DocumentFormat.BINARY);
      int testb5 = documentStore.putDocument(new ByteArrayInputStream(stringArray[11].getBytes()), uriArray[4],
          DocumentStore.DocumentFormat.BINARY);
      int testb6 = documentStore.putDocument(new ByteArrayInputStream(stringArray[12].getBytes()), uriArray[5],
          DocumentStore.DocumentFormat.BINARY);
      int testb7 = documentStore.putDocument(new ByteArrayInputStream(stringArray[13].getBytes()), uriArray[6],
          DocumentStore.DocumentFormat.BINARY);
      assertEquals(testb1, docArray[0].hashCode());
      assertEquals(testb2, 0);
      assertEquals(testb3, docArray[2].hashCode());
      assertEquals(testb4, docArray[3].hashCode());
      assertEquals(testb5, docArray[4].hashCode());
      assertEquals(testb6, docArray[5].hashCode());
      assertEquals(testb7, 0);
    } catch (java.io.IOException e) {
      fail();
    }

    documentStore.undo(uriArray[1]);
    documentStore.undo(uriArray[4]);
    documentStore.undo(uriArray[5]);

    assertEquals(docArray[7], documentStore.getDocument(uriArray[0]));
    assertEquals(null, documentStore.getDocument(uriArray[1]));
    assertEquals(docArray[9], documentStore.getDocument(uriArray[2]));
    assertEquals(docArray[10], documentStore.getDocument(uriArray[3]));
    assertEquals(docArray[4], documentStore.getDocument(uriArray[4]));
    assertEquals(docArray[5], documentStore.getDocument(uriArray[5]));
    assertEquals(docArray[13], documentStore.getDocument(uriArray[6]));

    try {
      int testc1 = documentStore.putDocument(new ByteArrayInputStream(stringArray[14].getBytes()), uriArray[0],
          DocumentStore.DocumentFormat.TXT);
      int testc2 = documentStore.putDocument(new ByteArrayInputStream(stringArray[15].getBytes()), uriArray[1],
          DocumentStore.DocumentFormat.TXT);
      int testc3 = documentStore.putDocument(new ByteArrayInputStream(stringArray[16].getBytes()), uriArray[2],
          DocumentStore.DocumentFormat.TXT);
      int testc4 = documentStore.putDocument(new ByteArrayInputStream(stringArray[17].getBytes()), uriArray[3],
          DocumentStore.DocumentFormat.TXT);
      int testc5 = documentStore.putDocument(new ByteArrayInputStream(stringArray[18].getBytes()), uriArray[4],
          DocumentStore.DocumentFormat.TXT);
      int testc6 = documentStore.putDocument(new ByteArrayInputStream(stringArray[19].getBytes()), uriArray[5],
          DocumentStore.DocumentFormat.TXT);
      int testc7 = documentStore.putDocument(new ByteArrayInputStream(stringArray[20].getBytes()), uriArray[6],
          DocumentStore.DocumentFormat.TXT);

      documentStore.undo(uriArray[1]);
      documentStore.undo(uriArray[6]);
      documentStore.undo();

      assertEquals(testc1, docArray[7].hashCode());
      assertEquals(testc2, 0);
      assertEquals(testc3, docArray[9].hashCode());
      assertEquals(testc4, docArray[10].hashCode());
      assertEquals(testc5, docArray[4].hashCode());
      assertEquals(testc6, docArray[5].hashCode());
      assertEquals(testc7, docArray[13].hashCode());
    } catch (java.io.IOException e) {
      fail();
    }

    assertEquals(docArray[14], documentStore.getDocument(uriArray[0]));
    assertEquals(null, documentStore.getDocument(uriArray[1]));
    assertEquals(docArray[16], documentStore.getDocument(uriArray[2]));
    assertEquals(docArray[17], documentStore.getDocument(uriArray[3]));
    assertEquals(docArray[18], documentStore.getDocument(uriArray[4]));
    assertEquals(docArray[5], documentStore.getDocument(uriArray[5]));
    assertEquals(docArray[13], documentStore.getDocument(uriArray[6]));

    for (int i = 0; i < 7; i++) {
      documentStore.undo();
    }

    assertEquals(docArray[7], documentStore.getDocument(uriArray[0]));
    assertEquals(null, documentStore.getDocument(uriArray[1]));
    assertEquals(docArray[2], documentStore.getDocument(uriArray[2]));
    assertEquals(docArray[3], documentStore.getDocument(uriArray[3]));
    assertEquals(docArray[4], documentStore.getDocument(uriArray[4]));
    assertEquals(docArray[5], documentStore.getDocument(uriArray[5]));
    assertEquals(null, documentStore.getDocument(uriArray[6]));
  }
}