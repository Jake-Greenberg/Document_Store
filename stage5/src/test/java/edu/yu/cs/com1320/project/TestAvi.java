package edu.yu.cs.com1320.project;

import static org.junit.jupiter.api.Assertions.*;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.stage5.impl.*;
import edu.yu.cs.com1320.project.stage5.*;

public class TestAvi {
  @Test
  public void hashTableImplSimplePutAndGet() {
    HashTable<Integer, Integer> hashTable = new HashTableImpl<Integer, Integer>();
    hashTable.put(1, 2);
    hashTable.put(3, 6);
    hashTable.put(7, 14);
    int x = hashTable.get(1);
    int y = hashTable.get(3);
    int z = hashTable.get(7);
    assertEquals(2, x);
    assertEquals(6, y);
    assertEquals(14, z);

  }

  @Test
  public void hashTableImplALotOfInfoTest() {
    HashTable<Integer, Integer> hashTable = new HashTableImpl<Integer, Integer>();
    for (int i = 0; i < 1000; i++) {
      hashTable.put(i, 2 * i);
    }

    int aa = hashTable.get(450);
    assertEquals(900, aa);
    p("passed Test: hashTableImplALotOfInfoTest");
  }

  @Test
  public void hashTableImplCollisionTest() {
    HashTable<Integer, Integer> hashTable = new HashTableImpl<Integer, Integer>();
    hashTable.put(1, 9);
    hashTable.put(6, 12);
    hashTable.put(11, 22);
    int a = hashTable.get(1);
    int b = hashTable.get(6);
    int c = hashTable.get(11);
    assertEquals(9, a);
    assertEquals(12, b);
    assertEquals(22, c);
    p("passed Test: hashTableImplCollisionTest");
  }

  @Test
  public void hashTableImplReplacementTest() {
    HashTable<Integer, Integer> hashTable = new HashTableImpl<Integer, Integer>();
    hashTable.put(1, 2);
    int a = hashTable.put(1, 3);
    assertEquals(2, a);
    int b = hashTable.put(1, 4);
    assertEquals(3, b);
    int c = hashTable.put(1, 9);
    assertEquals(4, c);
    p("passed Test: hashTableImplReplacementTest");
  }

  @Test
  public void hashTableImplReplacementTest2() {
    HashTable<Integer, Integer> hashTable = new HashTableImpl<Integer, Integer>();
    hashTable.put(1, 2);
    p("passed Test: hashTableImplReplacementTest2");
  }

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
    p("passed Test: docTest");
  }

  @Test
  public void basicCollision() {
    HashTable<Integer, String> hashTable = new HashTableImpl<Integer, String>();
    hashTable.put(1, "Avi");
    hashTable.put(5, "dinsky");
    hashTable.put(6, "Radinsky");
    hashTable.put(11, "gami");
    assertEquals("gami", hashTable.put(11, "gthir"));
    assertEquals("gthir", hashTable.get(11));
    assertEquals("Avi", hashTable.get(1));
    assertEquals("Radinsky", hashTable.get(6));
    p("passed Test: basicCollision");
  }
  private void p(String s) {
    System.out.println(s);
  }
}