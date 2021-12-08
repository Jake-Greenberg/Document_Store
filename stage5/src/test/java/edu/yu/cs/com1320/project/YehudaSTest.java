package edu.yu.cs.com1320.project;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.HashTable;
import edu.yu.cs.com1320.project.stage5.impl.*;
import edu.yu.cs.com1320.project.stage5.*;
import java.net.URISyntaxException;


public class YehudaSTest {
	@Test
	public void DocumentStoreImplTest() throws URISyntaxException, IOException {
		String initialString = "string";
		String initialString1 = "string1";
		String initialString2 = "string2";
		String initialString3 = "string3";
		String initialString4 = "string4";
		String initialString5 = "string5";
		String initialString6 = "string6";
		String initialString7 = "string7";
		String initialString8 = "string8";
		String initialString9 = "string9";
		String initialString13 = "string13";
		String initialString11 = "string11";
		String initialString12 = "string12";
		
	    InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
	    InputStream targetStream1 = new ByteArrayInputStream(initialString1.getBytes());
	    InputStream targetStream2 = new ByteArrayInputStream(initialString2.getBytes());
	    InputStream targetStream3 = new ByteArrayInputStream(initialString3.getBytes());
	    InputStream targetStream4 = new ByteArrayInputStream(initialString4.getBytes());
	    InputStream targetStream5 = new ByteArrayInputStream(initialString5.getBytes());
	    InputStream targetStream6 = new ByteArrayInputStream(initialString6.getBytes());
	    InputStream targetStream7 = new ByteArrayInputStream(initialString7.getBytes());
	    InputStream targetStream8 = new ByteArrayInputStream(initialString8.getBytes());
	    InputStream targetStream9 = new ByteArrayInputStream(initialString9.getBytes());
	    InputStream targetStream13 = new ByteArrayInputStream(initialString13.getBytes());
	    InputStream targetStream11 = new ByteArrayInputStream(initialString11.getBytes());
	    InputStream targetStream12 = new ByteArrayInputStream(initialString12.getBytes());
	    
	    InputStream targetStream10 = new ByteArrayInputStream(initialString.getBytes());
	   
	    
	    DocumentStore documentStore = new DocumentStoreImpl();
		
	  	String byteTxt = "https://www.HashTableImplByte.org/"; 
	  	String stringTxt = "https://www.HashTableImplString.org/"; 
	  	URI byteUri = new URI(byteTxt); 
	  	URI stringUri = new URI(stringTxt); 
	  	
	  	String byteTxt1 = "https://www.HashTableImplByteOne.org/"; 
	  	String stringTxt1 = "https://www.HashTableImplStringOne.org/"; 
	  	URI byteUri1 = new URI(byteTxt1); 
	  	URI stringUri1 = new URI(stringTxt1);
	  	
	  	String byteTxt2 = "https://www.HashTableImplByteTwo.org/"; 
	  	String stringTxt2 = "https://www.HashTableImplStringTwo.org/"; 
	  	URI byteUri2 = new URI(byteTxt2); 
	  	URI stringUri2 = new URI(stringTxt2); 
	  	
	  	String byteTxt3 = "https://www.HashTableImplByteThree.org/"; 
	  	String stringTxt3 = "https://www.ThreeHashTableImplString.org/"; 
	  	URI byteUri3 = new URI(byteTxt3); 
	  	URI stringUri3 = new URI(stringTxt3); 
	  	
	  	String byteTxt4 = "https://www.HashTableImplByteFour.org/"; 
	  	String stringTxt4 = "https://www.HashTableImplStringFour.org/"; 
	  	URI byteUri4 = new URI(byteTxt4); 
	  	URI stringUri4 = new URI(stringTxt4); 
	  	
	  	String byteTxt5 = "https://www.HashTableImplByteFive.org/"; 
	  	String stringTxt5 = "https://www.HashTableImplStringFive.org/"; 
	  	URI byteUri5 = new URI(byteTxt5); 
	  	URI stringUri5 = new URI(stringTxt5); 
	  	
	  	String byteTxt6 = "https://www.SixHashTableImplByte.org/"; 
	  	String stringTxt6 = "https://www.SixHashTableImplString.org/"; 
	  	URI byteUri6 = new URI(byteTxt6); 
	  	URI stringUri6 = new URI(stringTxt6); 
	  	
	  	String byteTxt7 = "https://www.HashTableImplByteSeven.org/"; 
	  	String stringTxt7 = "https://www.HashTableImplStringSeven.org/"; 
	  	URI byteUri7 = new URI(byteTxt7); 
	  	URI stringUri7 = new URI(stringTxt7); 
	  	
	  	String byteTxt8 = "https://www.HashTableImplByteEight.org/"; 
	  	String stringTxt8 = "https://www.HashTableImplStringEight.org/"; 
	  	URI byteUri8 = new URI(byteTxt8); 
	  	URI stringUri8 = new URI(stringTxt8); 
	  	
	  	String byteTxt9 = "https://www.HashTableImplByteNine.org/"; 
	  	String stringTxt9 = "https://www.HashTableImplStringNine.org/"; 
	  	URI byteUri9 = new URI(byteTxt9); 
	  	URI stringUri9 = new URI(stringTxt9); 
	  	
	  	byte byteInput[] = {20,10,30,5};
	  	InputStream targetStreamBytes = new ByteArrayInputStream(byteInput);
	  	
	  	byte byteInput1[] = {20,10,30,5,6};
	  	InputStream targetStreamBytes1 = new ByteArrayInputStream(byteInput1);
	  	
	  	byte byteInput2[] = {20,10,30,5,7};
	  	InputStream targetStreamBytes2 = new ByteArrayInputStream(byteInput2);
	  	
	  	byte byteInput3[] = {20,10,30,5,8};
	  	InputStream targetStreamBytes3 = new ByteArrayInputStream(byteInput3);
	  	
	  	byte byteInput4[] = {20,10,30,5,9};
	  	InputStream targetStreamBytes4 = new ByteArrayInputStream(byteInput4);
	  	
	  	byte byteInput5[] = {20,10,30,5,5};
	  	InputStream targetStreamBytes5 = new ByteArrayInputStream(byteInput5);
	  	
	  	byte byteInput6[] = {20,10,30,5,4};
	  	InputStream targetStreamBytes6 = new ByteArrayInputStream(byteInput6);
	  	
	  	byte byteInput7[] = {20,10,30,5,3};
	  	InputStream targetStreamBytes7 = new ByteArrayInputStream(byteInput7);
	  	
	  	byte byteInput8[] = {20,10,30,5,2};
	  	InputStream targetStreamBytes8 = new ByteArrayInputStream(byteInput8);
	  	
	  	byte byteInput9[] = {20,10,30,5,1};
	  	InputStream targetStreamBytes9 = new ByteArrayInputStream(byteInput9);
	  	
	  	
	  	byte byteInput10[] = {20,10,30,5};
	  	InputStream targetStreamBytes10 = new ByteArrayInputStream(byteInput10);
	  	
	  	
	  	
	  	int x = documentStore.putDocument(targetStream, stringUri, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(x,0);
	  	int y = documentStore.putDocument(targetStreamBytes, byteUri, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(y,0);
	  	
	  	//int previousDocHashcode = documentStore.getDocument(stringUri).hashCode();
	  	
	  	int z = documentStore.putDocument(targetStream1, stringUri1, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(z, 0);
	  	
	  	//int previousDocHashcode1 = documentStore.getDocument(stringUri1).hashCode();
	  	
	  	int zz = documentStore.putDocument(targetStream2, stringUri2, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(0, zz);
	  	
	  	int zzzss = documentStore.putDocument(targetStream8, stringUri8, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(0, zzzss);
	  	
	  	int zzzq = documentStore.putDocument(targetStream3, stringUri3, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(0, zzzq);
	  	
	  	int zzzw = documentStore.putDocument(targetStream4, stringUri4, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(0, zzzw);
	  	
	  	int zzze = documentStore.putDocument(targetStream5, stringUri5, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(0, zzze);
	  	
	  	int zzzs = documentStore.putDocument(targetStream6, stringUri6, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(0, zzzs);
	  	
	  	int zzza = documentStore.putDocument(targetStream7, stringUri7, DocumentStore.DocumentFormat.TXT);
	  	assertEquals(0, zzza);

	  	

	  	assertEquals(documentStore.getDocument(stringUri8).getDocumentTxt(), "string8");
	  	assertEquals(documentStore.getDocument(stringUri7).getDocumentTxt(), "string7");
	  	assertEquals(documentStore.getDocument(stringUri6).getDocumentTxt(), "string6");
	  	assertEquals(documentStore.getDocument(stringUri5).getDocumentTxt(), "string5");
	  	assertEquals(documentStore.getDocument(stringUri4).getDocumentTxt(), "string4");
	  	assertEquals(documentStore.getDocument(stringUri3).getDocumentTxt(), "string3");
	  	assertEquals(documentStore.getDocument(stringUri2).getDocumentTxt(), "string2");
	  	assertEquals(documentStore.getDocument(stringUri1).getDocumentTxt(), "string1");
	  	assertEquals(documentStore.getDocument(stringUri).getDocumentTxt(), "string");


	  	//assertEquals(documentStore.getDocument(stringUri1).getDocumentTxt(), "string7");
	  	//int qqqq = documentStore.getDocument(stringUri1).hashCode();
	  	int asdg = documentStore.putDocument(targetStream12, stringUri7, DocumentStore.DocumentFormat.TXT);
	  	//assertEquals(asdg, qqqq);  
	  	assertEquals(documentStore.getDocument(stringUri7).getDocumentTxt(), "string12");
	  	
	  	int wwww = documentStore.getDocument(stringUri7).hashCode();
	  	assertEquals(documentStore.putDocument(targetStream11, stringUri7, DocumentStore.DocumentFormat.TXT), wwww);  
	  	
	  	assertEquals(documentStore.getDocument(stringUri7).getDocumentTxt(), "string11");
	  	
	  	
	  	int wwwwq = documentStore.getDocument(stringUri7).hashCode();
	  	assertEquals(documentStore.putDocument(targetStream13, stringUri7, DocumentStore.DocumentFormat.TXT), wwwwq);
	  	assertEquals(documentStore.getDocument(stringUri7).getDocumentTxt(), "string13");
	
	  	
	}

    
}
