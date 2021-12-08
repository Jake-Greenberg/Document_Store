package edu.yu.cs.com1320.project.stage5.impl;

import java.net.URI;
import java.util.*;
import edu.yu.cs.com1320.project.stage5.Document;

public class DocumentImpl implements Document {
    
    private URI uri;
    private String text;
    private byte binaryData[];
    private HashMap<String, Integer> wordMap;
    private Set<String> wordsInDoc;
    private String[] arrayOfWords;
    private long lastUseTime;
    
    public DocumentImpl(URI uri, String text){
        if(uri == null || uri.toString().equals("") || text == null || text == ""){
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.text = text;
        this.wordsInDoc = this.getWords();
        this.wordMap = new HashMap<String,Integer>();
        this.fillHashMap();
    }
    public DocumentImpl(URI uri, byte[] binaryData){
        if(uri == null || uri.toString().equals("") || binaryData == null || binaryData.length == 0) {
            throw new IllegalArgumentException();
        }
        this.uri = uri;
        this.binaryData = binaryData;
    }
    @Override
    public String getDocumentTxt() {
       return this.text; 
    }

    @Override
    public byte[] getDocumentBinaryData() {
        return this.binaryData;
    }

    @Override
    public URI getKey() {
        return this.uri;
    }

    /**
     * how many times does the given word appear in the document?
     * @param word
     * @return the number of times the given words appears in the document. If it's a binary document, return 0.
     */
    @Override
    public int wordCount(String word){
        if(word == null){
            throw new IllegalArgumentException();
        }
        //If it's a binary document
        if(this.text == null){
            return 0;
        }
        String lowerCaseWord = word.toLowerCase();
        String regularWord = lowerCaseWord.replaceAll("[^A-Za-z0-9\\s+]", "");
        //If it's a text document:
        int numberOfTimesWordAppears = 0;
        for(String s: this.arrayOfWords){
            if(s.startsWith(regularWord)){
                numberOfTimesWordAppears++;
            }
        }
        return numberOfTimesWordAppears;
    }
    /**
     * @return all the words that appear in the document
     */
    @Override
    public Set<String> getWords(){
        if(this.text == null){
            Set<String>emptySet = new HashSet<>();
            return emptySet;
        }
        this.arrayOfWords = text.trim().toLowerCase().replaceAll("[^A-Za-z0-9\\s+]", "").split("\\s+");//CHECK TO MAKE SURE RIGHT
        List<String> listOfWords = (Arrays.asList(this.arrayOfWords));
        Set<String> words = new HashSet<>(listOfWords); 
        return words;
    }
    private void fillHashMap(){
        for(String word : this.wordsInDoc){
            wordMap.put(word, wordCount(word));
        }
    }

    @Override
    public int hashCode(){
        int result = uri.hashCode();
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(binaryData);
        return result;
    }

    @Override
    public boolean equals(Object o){
        if(o == null){
            return false;
        }
        if(this.hashCode() == o.hashCode()){
            return true;
        }
        return false;
    }
    public long getLastUseTime(){
        return this.lastUseTime;
    }
    //Update any time a put, get, search, or undo is performed 
    //Make sure to call this method each time any of these methods are called!
    public void setLastUseTime(long timeInNanoseconds){
        this.lastUseTime = timeInNanoseconds; 
    }
    @Override
    public int compareTo(Document d) {
        if(this.getLastUseTime() > d.getLastUseTime()){
            return 1;
        }
        if(this.getLastUseTime() < d.getLastUseTime()){
            return -1;
        }
        if(this.getLastUseTime() == d.getLastUseTime()){
            return 0;
        }
        return 0;
    }
    /**
     * @return a copy of the word to count map so it can be serialized
     */
    @Override
    public Map<String, Integer> getWordMap() {
        if(this.getDocumentTxt() != null){
            Map<String, Integer> copyOfWordMap = new HashMap<>();
            copyOfWordMap.putAll(this.wordMap);
            return copyOfWordMap;
        } else{ //Binary Document so return empty map
            Map<String, Integer> noWords = new HashMap<>();
            return noWords;
        }

    }
    @Override
     /**
     * This must set the word to count map during deserialization
     * @param wordMap
     */
    public void setWordMap(Map<String,Integer> wordMap){
        this.wordMap = (HashMap<String, Integer>) wordMap;
        
    }

}