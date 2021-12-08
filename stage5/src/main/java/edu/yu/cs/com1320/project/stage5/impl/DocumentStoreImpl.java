package edu.yu.cs.com1320.project.stage5.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.function.Function;
import java.util.*;

import edu.yu.cs.com1320.project.Undoable;
import edu.yu.cs.com1320.project.GenericCommand;
import edu.yu.cs.com1320.project.MinHeap;
import edu.yu.cs.com1320.project.CommandSet;
import edu.yu.cs.com1320.project.impl.BTreeImpl;
import edu.yu.cs.com1320.project.impl.HashTableImpl;
import edu.yu.cs.com1320.project.impl.StackImpl;
import edu.yu.cs.com1320.project.impl.TrieImpl;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.DocumentStore;
import edu.yu.cs.com1320.project.impl.MinHeapImpl;

public class DocumentStoreImpl implements DocumentStore {
    //private HashTableImpl<URI, Document> hashtable;
    private StackImpl<Undoable> commandStack;
    private TrieImpl<URI> documentTrie;
    private MinHeapImpl<MinHeapDocument> documentHeap;
    private int maxDocumentBytes;
    private int maxDocumentCount;
    private boolean isSetMaxDoc = false;
    private boolean isSetMaxBytes = false;
    private int currentNumberOfBytes = 0;
    private int currentNumberOfDocs = 0;
    private long sameTime = 0;
    private BTreeImpl<URI,Document> docBTree;
    private DocumentPersistenceManager dpm;
    private HashSet<MinHeapDocument> docsInHeap;
    private HashSet<MinHeapDocument> docsMovedToDisk;
    
    // Object for MinHeap to use to compare lastUseTime of Docs in docBTree
    private class MinHeapDocument implements Comparable<MinHeapDocument>{
        private URI uri;
        public MinHeapDocument(URI uri){
            this.uri = uri;
        }
        private URI getUri(){
            return this.uri;
        }
        @Override
        public int compareTo(MinHeapDocument other) {
            URI otherURI = other.getUri();
            if(docBTree.get(this.uri).getLastUseTime() > docBTree.get(otherURI).getLastUseTime()){
                return 1;
            }
            if(docBTree.get(this.uri).getLastUseTime() < docBTree.get(otherURI).getLastUseTime()){
                return -1;
            }
            if(docBTree.get(this.uri).getLastUseTime() == docBTree.get(otherURI).getLastUseTime()){
                return 0;
            }
            return 0;
        }
        @Override
        public boolean equals(Object other){
            if(this == other){
                return true;
            }
            if(other ==  null){
                return false;
            }
            if(!(other instanceof MinHeapDocument)){
                return false;
            }
            MinHeapDocument otherDoc = (MinHeapDocument) other;
            if(docBTree.get(otherDoc.getUri()) == null || docBTree.get(this.getUri()) == null){
                return false;
            }
            if(docBTree.get(this.getUri()).equals(docBTree.get(otherDoc.getUri()))){
                return true;
            }else{
                return false;
            }
        }
        @Override 
        public int hashCode(){
            return this.getUri().hashCode();
        }
    }

    public DocumentStoreImpl(){
        //this.hashtable = new HashTableImpl<URI, Document>();
        this.commandStack = new StackImpl<Undoable>();
        this.documentTrie = new TrieImpl<URI>();
        this.documentHeap = new MinHeapImpl<MinHeapDocument>();
        this.docBTree = new BTreeImpl<URI, Document>();  
        this.dpm = new DocumentPersistenceManager(null);
        this.docBTree.setPersistenceManager(this.dpm);
        this.docsInHeap = new HashSet<>();
        this.docsMovedToDisk = new HashSet<>();
    }
    public DocumentStoreImpl(File baseDir){
        //this.hashtable = new HashTableImpl<URI, Document>();
        this.commandStack = new StackImpl<Undoable>();
        this.documentTrie = new TrieImpl<URI>();
        this.documentHeap = new MinHeapImpl<MinHeapDocument>();
        this.docBTree = new BTreeImpl<URI, Document>(); 
        this.dpm = new DocumentPersistenceManager(baseDir); // Check with someone regarding this
        this.docBTree.setPersistenceManager(this.dpm);
        this.docsInHeap = new HashSet<>();
        this.docsMovedToDisk = new HashSet<>();
    }
    /**
     * @param input the document being put
     * @param uri unique identifier for the document
     * @param format indicates which type of document format is being passed
     * @return if there is no previous doc at the given URI, return 0. If there is a previous doc, return the hashCode of the previous doc. If InputStream is null, this is a delete, and thus return either the hashCode of the deleted doc or 0 if there is no doc to delete.
     */
    public int putDocument(InputStream input, URI uri, DocumentFormat format) throws IOException{
        if(uri == null || format == null){
            throw new IllegalArgumentException();
        }
        if(input == null){ // User wants to delete the document - no need to check for storage
            return deleteFromPutCall(uri);
        }
        MinHeapDocument minHeapDoc = new MinHeapDocument(uri); 
        //Create Documents- either of type "text" or of type "binaryData"
        byte[] byteArray = input.readAllBytes();
        String text = new String (byteArray);
        if(format == DocumentFormat.TXT){
        Document textDocument = new DocumentImpl(uri, text);
        // Check if enough storage to execute put
            Document docReturned = getDocumentTemporarily(uri); // See if the put about to be executed will be replacing a doc. If 
            //not null then it is replacing doc- adjust the bytes level before checking if enough storage for put to be executed
            if(docReturned != null){
                // deleteDocumentMiddleOfHeap(docReturned);
                currentNumberOfBytes -= getBytesOfDocument(docReturned);
                currentNumberOfDocs--;
            }
            // if(getBytesOfDocument(textDocument) > this.maxDocumentBytes){
            //     try {
            //         docBTree.moveToDisk(uri);
            //     } catch (Exception e) {
            //         e.printStackTrace();
            //     }
            // }
            if(!enoughMemory(textDocument)){
                try {
                    moveDocsToDiskUntilEnoughRoom(textDocument);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Document textDocumentReturned = (Document) this.docBTree.put(uri, textDocument);
            // this.documentHeap.insert(minHeapDoc);
            //Update lastUsedTime of Doc just put in
            long lastTimeUsed = System.nanoTime();
            textDocument.setLastUseTime(lastTimeUsed);
            //Add to storage count
            this.currentNumberOfBytes += text.getBytes().length;
            Set<String> words = parseText(text);
            //Put the document in the trie at each word of its text
            for(String s : words){
            documentTrie.put(s,textDocument.getKey());
            }
        if(textDocumentReturned == null){
            this.currentNumberOfDocs++;
            this.documentHeap.insert(minHeapDoc);
            if(!this.docsInHeap.contains(minHeapDoc)){
                 this.docsInHeap.add(minHeapDoc);
            }
            runLambdaForDeleteDocPutIn(uri, words, textDocument, this.docsMovedToDisk);
            return 0;
            }else{
                Set<String> wordsOfReturnedDoc = parseText(textDocumentReturned.getDocumentTxt());
                if(wordsOfReturnedDoc != null){
                    for(String s : wordsOfReturnedDoc){
                        documentTrie.delete(s,textDocumentReturned.getKey());
                    }
                }
                // No need to subtract the bytes of returned document- taken care of above
                this.currentNumberOfDocs++;
                // deleteDocumentMiddleOfHeap(textDocumentReturned); //Doc count already adjusted above so no need to subtract 1
                this.adjustHeapAfterReplace(textDocumentReturned);
                runLambdaForPutBackDeletedDoc(uri, wordsOfReturnedDoc, textDocumentReturned, textDocument, this.docsMovedToDisk);
                return textDocumentReturned.hashCode();
            }
        }else if(format == DocumentFormat.BINARY){
            DocumentImpl binaryDocument = new DocumentImpl(uri, byteArray);
            // Check if enough storage to execute put
            if(!enoughMemory(binaryDocument)){
                try {
                    moveDocsToDiskUntilEnoughRoom(binaryDocument);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Add to storage count
            this.currentNumberOfBytes +=byteArray.length;
            Document binaryDocumentReturned = (Document) this.docBTree.put(uri, binaryDocument);
            //Update lastUsedTime of Doc just put in
            long lastTimeUsed = System.nanoTime();
            binaryDocument.setLastUseTime(lastTimeUsed);
            if(binaryDocumentReturned == null){
                this.currentNumberOfDocs++;
                this.documentHeap.insert(minHeapDoc);
                if(!this.docsInHeap.contains(minHeapDoc)){
                    this.docsInHeap.add(minHeapDoc);
                }
                runLambdaForDeleteDocPutIn(uri, null, binaryDocument, this.docsMovedToDisk);
                return 0;
            }else {
                this.adjustHeapAfterReplace(binaryDocumentReturned);
                //deleteDocumentMiddleOfHeap(binaryDocumentReturned);
                // No need to subtract the bytes of returned document- taken care of above
                this.currentNumberOfDocs++;
                //deleteDocumentMiddleOfHeap(binaryDocumentReturned);
                runLambdaForPutBackDeletedDoc(uri, null, binaryDocumentReturned, binaryDocument, this.docsMovedToDisk);
                return binaryDocumentReturned.hashCode();
            }
        } 
        return 0;
    }
    // If user calls putDocument() with a null input
    private int deleteFromPutCall(URI uri){
        Document docToBeDeleted = this.getDocumentTemporarily(uri);
        if(docToBeDeleted != null){
            deleteDocumentMiddleOfHeap(docToBeDeleted); //Remove from heap
        }
        Document deletedDocument = (Document)this.docBTree.put(uri, null);
            if(deletedDocument == null){
                noOpLambda(uri); 
                return 0;
            } else{
                // deleteDocumentMiddleOfHeap(deletedDocument); //Remove from heap
                this.currentNumberOfBytes -= getBytesOfDocument(deletedDocument);//Adjust storage levels
                this.currentNumberOfDocs--;
                Set<String> words = parseText(deletedDocument.getDocumentTxt());
                if(words != null){
                    for(String s : words){
                        documentTrie.delete(s, deletedDocument.getKey());
                    }
                }
                runLambdaForPutBackDeletedDoc(uri, words, deletedDocument, null, this.docsMovedToDisk);
                return deletedDocument.hashCode();
            }
    }
    private Set<String> parseText(String docText){
        if(docText != null){
            String[] arrayOfWords = docText.trim().toLowerCase().replaceAll("[^A-Za-z0-9\\s+]", "").split("\\s+");//CHECK TO MAKE SURE RIGHT
            List<String> listOfWords = (Arrays.asList(arrayOfWords));
            Set<String> words = new HashSet<>(listOfWords);
            return words;
        }
        return null;
        
    }
    // LAMBDAS - UPDATE LAST USED TIME FOR THE DOC BEING UNDONE ONLY IF UNDO IS PUTTING DOC BACK
    //Lambda Methods
    private void noOpLambda(URI uri){
        Function<URI,Boolean> noOpCommand = (URI uriLambda) -> {
            return true;
        };
        Undoable newCommand = new GenericCommand<URI>(uri, noOpCommand);
        commandStack.push(newCommand);
    }
    private void runLambdaForDeleteDocPutIn(URI uri, Set<String> words, Document doc, HashSet<MinHeapDocument> docsMovedToDisk){
        Function<URI,Boolean> deleteDocPutIn = (URI uriLambda) -> {
            this.currentNumberOfBytes -= getBytesOfDocument(doc); // Adjust storage count
            this.currentNumberOfDocs--;
            deleteDocumentMiddleOfHeap(doc); // Remove from heap as well
            this.docBTree.put(uriLambda, null);
            if(words != null){
                for(String s : words){
                    this.documentTrie.delete(s,doc.getKey());
                }
            }
            for(MinHeapDocument d : docsMovedToDisk){
                this.documentHeap.insert(d);
                this.currentNumberOfBytes += getBytesOfDocument(this.getDocumentTemporarily(d.getUri()));
                this.currentNumberOfDocs ++;
            }
            return true;
        };
        Undoable newCommand = new GenericCommand<URI>(uri, deleteDocPutIn);
        commandStack.push(newCommand);
    }
    private void runLambdaForPutBackDeletedDoc(URI uri, Set<String> words, Document doc, Document docToRemoveFromHeap, HashSet<MinHeapDocument> docsMoved){
        Function<URI,Boolean> putBackDeletedDoc = (URI uriLambda) -> {
            // // Check if enough storage to execute put
            // if(!enoughMemory(doc)){
            // moveDocsToDiskUntilEnoughRoom(doc);
            // }
            MinHeapDocument minDoc = new MinHeapDocument(doc.getKey());
            if(docToRemoveFromHeap != null){
                deleteDocumentMiddleOfHeap(docToRemoveFromHeap);
            }
            if(!enoughMemory(doc)){
                try {
                    moveDocsToDiskUntilEnoughRoom(doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            this.currentNumberOfBytes += getBytesOfDocument(doc);
            this.currentNumberOfDocs++;
            this.docBTree.put(uri, doc);
            if(words != null){
                for(String s : words){
                    this.documentTrie.put(s, doc.getKey());
                } 
            }
            long lastUsedTime = System.nanoTime();
            doc.setLastUseTime(lastUsedTime);
            this.documentHeap.insert(minDoc); //Insert into heap after updating its lastUsedTime
            this.docsInHeap.add(minDoc);
            // for(MinHeapDocument d : docsMoved){
            //     this.documentHeap.insert(d);
            //     this.currentNumberOfBytes += getBytesOfDocument(this.getDocumentTemporarily(d.getUri()));
            //     this.currentNumberOfDocs ++;
            // }
            return true;

        };
        Undoable newCommand = new GenericCommand<URI>(uri, putBackDeletedDoc);
        commandStack.push(newCommand);
    }
    // Lambda For CommandSet Instances
    private void runLambdaForDeleteAlls(URI uri, Document doc, Set<String> words, CommandSet<GenericCommand<URI>> commandSet){
        Function<URI,Boolean> putBackDeletedDoc = (URI uriLambda) -> {
            // // Check if enough storage to execute put
            // if(!enoughMemory(doc)){
            //     moveDocsToDiskUntilEnoughRoom(doc);
            // }
            this.docBTree.put(uri, doc);
            this.currentNumberOfBytes += getBytesOfDocument(doc);
            this.currentNumberOfDocs++;
            // Set all the docs to the same lastUsedTime
            long lastUsedTime = this.sameTime; 
            doc.setLastUseTime(lastUsedTime);
            MinHeapDocument minDoc = new MinHeapDocument(doc.getKey());
            if(!this.docsInHeap.contains(minDoc)){
                this.documentHeap.insert(minDoc); //Insert into heap after updating its lastUsedTime
                this.docsInHeap.add(minDoc);
            }
            for(String s : words){
                this.documentTrie.put(s, doc.getKey());
            }
            return true;
        };
        GenericCommand newGenericCommand = new GenericCommand<URI>(uri, putBackDeletedDoc);
        commandSet.addCommand(newGenericCommand);
    }
    
    /**
     * @param uri the unique identifier of the document to get
     * @return the given document
     */
    public Document getDocument(URI uri) {
        Document doc = this.docBTree.get(uri);
        long lastUsedTime = System.nanoTime();
        MinHeapDocument minHeapDoc = new MinHeapDocument(uri);
        // Update lastUsedTime of Doc gotten
        if(doc != null){
            doc.setLastUseTime(lastUsedTime);
            if(!this.docsInHeap.contains(minHeapDoc)){
                if(!enoughMemory(doc)){
                    try {
                        this.moveDocsToDiskUntilEnoughRoom(doc);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                this.docsInHeap.add(minHeapDoc);
                this.documentHeap.insert(minHeapDoc);
                this.currentNumberOfDocs++;
                this.currentNumberOfBytes += getBytesOfDocument(doc);
            } else{
                this.documentHeap.reHeapify(minHeapDoc);
            }
            //adjustHeapAfterReplace(doc);
            return doc;
        }else{
            return null;
        }
    }
    private Document getDocumentTemporarily(URI uri){
        MinHeapDocument minDoc = new MinHeapDocument(uri);
        Document doc = this.docBTree.get(uri);
        if(!this.docsInHeap.contains(minDoc)){
            try {
                this.docBTree.moveToDisk(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return doc;
    }
   /**
     * @param uri the unique identifier of the document to delete
     * @return true if the document is deleted, false if no document exists with
     *         that URI
     */
    public boolean deleteDocument(URI uri) {
        if(uri == null){
            throw new IllegalArgumentException();
        }
        Set<String> words = new HashSet<>();
        Document doc = getDocumentTemporarily(uri);
        if(doc != null && doc.getDocumentTxt() != null){
             words = parseText(doc.getDocumentTxt());
            for(String s : words){
                documentTrie.delete(s, doc.getKey());
            }
        }
        if(doc != null){
            //Remove from heap/memory
        deleteDocumentMiddleOfHeap(doc);
        }
    Document deletedDocument = (Document) this.docBTree.put(uri, null);
       if(deletedDocument == null){
            noOpLambda(uri);
            return false;
       } else{
            currentNumberOfBytes -= getBytesOfDocument(doc);
            currentNumberOfDocs--;
            runLambdaForPutBackDeletedDoc(uri, words, doc, null, this.docsMovedToDisk);
        }
       return true;
    }
   
    /**
     * undo the last put or delete command
     * @throws IllegalStateException if there are no actions to be undone, i.e.
     * the command stack is empty
     */
    public void undo() throws IllegalStateException{
        if(this.commandStack.size() == 0){
           throw new IllegalStateException();
        }
        Undoable commandToBeUndone = this.commandStack.pop();
        if(commandToBeUndone instanceof GenericCommand){
            commandToBeUndone = (GenericCommand)commandToBeUndone;
            commandToBeUndone.undo();
        }else{
            commandToBeUndone = (CommandSet)commandToBeUndone;
            // Get current time which will be used to set all docs to same time inside lambda 
            // for undoing deleteAlls
            this.sameTime = System.nanoTime();
            commandToBeUndone.undo();
        }
    }

   /**
     * undo the last put or delete that was done with the given URI as its key
     * @param uri
     * @throws IllegalStateException if there are no actions on the command stack for the given URI
     */
   public void undo(URI uri) throws IllegalStateException{
        if(this.commandStack.size() == 0){
            throw new IllegalStateException();  
        }
        if(uri == null){
            throw new IllegalArgumentException();
        }
        StackImpl<Undoable> tempStack = new StackImpl<Undoable>();
        int counter = 0;
        while(this.commandStack.size() != 0){ 
            Undoable currentCommand = this.commandStack.peek();
            if(currentCommand instanceof GenericCommand){
                //GenericCommand Instance
                GenericCommand genericCommand = (GenericCommand)currentCommand;
                if(!genericCommand.getTarget().equals(uri)){
                    Undoable commandForTempStack = this.commandStack.pop();
                    tempStack.push(commandForTempStack);
                } else{
                    Undoable commandToBeUndone = this.commandStack.pop();
                    commandToBeUndone.undo();
                    counter++;
                    break;
                }
            } else{ //CommandSet Instance
                CommandSet commandSet = (CommandSet)currentCommand;
                if(!commandSet.containsTarget(uri)){
                    Undoable commandForTempStack = this.commandStack.pop();
                    tempStack.push(commandForTempStack);
                } else{
                    commandSet.undo(uri);
                    if(commandSet.size() == 0){
                        this.commandStack.pop();
                    }
                    counter++;
                    break;
                }
            }
        } 
        while(tempStack.size() != 0){
            Undoable commandFromTempStack = tempStack.pop();
            this.commandStack.push(commandFromTempStack);
        }
        if(counter == 0){
            throw new IllegalStateException();  
        }
    }
     /**
     * Retrieve all documents whose text contains the given keyword.
     * Documents are returned in sorted, descending order, sorted by the number of times the keyword appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keyword
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> search(String keyword){
        long timeLastUsed = System.nanoTime();
        String lowerCaseKeyword = keyword.toLowerCase();
        Comparator<URI> documentComparison = (URI uri1, URI uri2) ->{
            Document document1 = this.getDocumentTemporarily(uri1);
            Document document2 = this.getDocumentTemporarily(uri2);
            if(document1.wordCount(lowerCaseKeyword) < document2.wordCount(lowerCaseKeyword)){
                return 1;
            }
            if(document1.wordCount(lowerCaseKeyword) > document2.wordCount(lowerCaseKeyword)){
                return -1;
            }
            if(document1.wordCount(lowerCaseKeyword) == document2.wordCount(lowerCaseKeyword)){
                return 0;
            }
            return 0;
        }; 
        List<URI> uriMatches = documentTrie.getAllSorted(lowerCaseKeyword, documentComparison);
        List<Document> documentMatches = new ArrayList<>();
        if(uriMatches != null){
            for(URI u : uriMatches){
                Document d = this.getDocument(u);
                documentMatches.add(d);
            }
        }
        if(documentMatches == null || documentMatches.size() == 0){
            List<Document> noMatches = new ArrayList<>();
            return noMatches;
        } else{
            updateLastUseTimeForDocs(timeLastUsed, documentMatches);
            return documentMatches;
        }
    }
    /**
     * Retrieve all documents whose text starts with the given prefix
     * Documents are returned in sorted, descending order, sorted by the number of times the prefix appears in the document.
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a List of the matches. If there are no matches, return an empty list.
     */
    public List<Document> searchByPrefix(String keywordPrefix){
        long timeLastUsed = System.nanoTime();
        String lowerCasePrefix = keywordPrefix.toLowerCase();
        Comparator<URI> documentComparison = (URI doc1, URI doc2) ->{
            Document document1 = this.getDocumentTemporarily(doc1);
            Document document2 = this.getDocumentTemporarily(doc2);
            if(document1.wordCount(lowerCasePrefix) < document2.wordCount(lowerCasePrefix)){
                return 1;
            }
            if(document1.wordCount(lowerCasePrefix) > document2.wordCount(lowerCasePrefix)){
                return -1;
            }
            if(document1.wordCount(lowerCasePrefix) == document2.wordCount(lowerCasePrefix)){
                return 0;
            }
            return 0;
        }; 
        List<URI> uriMatches = documentTrie.getAllWithPrefixSorted(lowerCasePrefix, documentComparison);
        List<Document> documentMatches = new ArrayList<>();
        // Get the documents that were searched for- both from disk and from memory
        for(URI u : uriMatches){
            Document d = this.getDocument(u);
            documentMatches.add(d);
        }
        if(documentMatches.size() == 0){
            List<Document> noMatches = new ArrayList<>();
            return noMatches;
        } else{
            updateLastUseTimeForDocs(timeLastUsed, documentMatches);
            return documentMatches;
        }
    }

    /**
     * Completely remove any trace of any document which contains the given keyword
     * @param keyword
     * @return a Set of URIs of the documents that were deleted.
     */
    // EDITED FOR STAGE 5 -----------------------------------------------------------------
    public Set<URI> deleteAll(String keyword){
        CommandSet<GenericCommand<URI>> setOfDeleteCommands = new CommandSet<GenericCommand<URI>>();
        keyword = keyword.toLowerCase();
        Set<URI> urisOfDeletedDocs = documentTrie.deleteAll(keyword);
        if(urisOfDeletedDocs.size() == 0){
            Set<URI> nothingDeleted = new HashSet<>();
            return nothingDeleted;
        } 
        commandStack.push(setOfDeleteCommands);
        for(URI u : urisOfDeletedDocs){
            Document doc = this.docBTree.get(u);
            Set<String> words = parseText(doc.getDocumentTxt());
            deleteDocument(u);
            //Counteract the extra pushes of the individual deleteDocument() commands above:
            this.commandStack.pop();
            //Set<String> words = parseText(doc.getDocumentTxt());
            this.sameTime = System.nanoTime();
            runLambdaForDeleteAlls(u, doc, words, setOfDeleteCommands);
        }
        return urisOfDeletedDocs;
    }

    /**
     * Completely remove any trace of any document which contains a word that has the given prefix
     * Search is CASE INSENSITIVE.
     * @param keywordPrefix
     * @return a Set of URIs of the documents that were deleted.
     */
    public Set<URI> deleteAllWithPrefix(String keywordPrefix){
        CommandSet<GenericCommand<URI>> setOfDeleteCommands = new CommandSet<GenericCommand<URI>>();
        Set<URI> nothingDeleted = new HashSet<>();
        if(keywordPrefix == ""){
            return nothingDeleted;
        }
        keywordPrefix = keywordPrefix.toLowerCase();
         Set<URI> urisOfDeletedDocs = documentTrie.deleteAllWithPrefix(keywordPrefix);
        if(urisOfDeletedDocs.size() == 0){
            return nothingDeleted;
        } 
        for(URI u : urisOfDeletedDocs){
            Document doc = this.docBTree.get(u);
            Set<String> words = parseText(doc.getDocumentTxt());
            deleteDocument(u);
            //Counteract the extra pushes of the individual delete commands above
            this.commandStack.pop();
            this.sameTime = System.nanoTime();
            runLambdaForDeleteAlls(doc.getKey(), doc, words, setOfDeleteCommands);
        }
        this.commandStack.push(setOfDeleteCommands);
        return urisOfDeletedDocs;
    }
    private void deleteFromTrie(Set<String> words, Document doc){
        for(String s : words){
            documentTrie.delete(s, doc.getKey());
        }
    }
    // METHODS ADDED FOR STAGE 4 ---------------------------------------
        /**
     * set maximum number of documents that may be stored
     * @param limit
     */
    public void setMaxDocumentCount(int limit){
        if(this.maxDocumentCount > limit){
            while(this.maxDocumentCount > limit){
                MinHeapDocument docToBeDeleted = this.documentHeap.remove();
                deleteDocDueToStorage(docToBeDeleted.getUri());
            }
        }
        this.maxDocumentCount = limit;
        this.isSetMaxDoc = true;
    }
    /**
     * set maximum number of bytes of memory that may be used by all the documents in memory combined
     * @param limit
     */
    public void setMaxDocumentBytes(int limit){
        if(this.maxDocumentBytes > limit){
            while(this.maxDocumentBytes > limit){
                MinHeapDocument docToBeDeleted = this.documentHeap.remove();
                deleteDocDueToStorage(docToBeDeleted.getUri());
            }
        }
        this.maxDocumentBytes = limit;
        this.isSetMaxBytes = true;
    }
    // Updates lastUsedTime for multiple docs involved in one action: search() and searchByPrefix()
    private void updateLastUseTimeForDocs(long time, Collection<Document> docs){
        for(Document d : docs){
            d.setLastUseTime(time);
            this.adjustHeapAfterReplace(d);
            
        }
    }
    private int getBytesOfDocument(Document doc){
        if(doc.getDocumentTxt() == null){
            return doc.getDocumentBinaryData().length;
        } else{
            return doc.getDocumentTxt().getBytes().length;
        }
    }
    // Checks to see if storage necessary to execute put or, in a Lambda, to 
    // execute an undo and thereby a put is currently available in docStore
    private boolean enoughMemory(Document doc ){
        if(this.isSetMaxBytes == false && this.isSetMaxDoc == false){
            return true; //Neither limit has been set
        }
        //If above is false then at least one limit has been set. Check which one is set
        //or if both are and then determine if enough storage to execute put or undo on given doc
        // If max has been set for number of possible documents:
        if(this.isSetMaxDoc == true && this.isSetMaxBytes == false){
            if(this.currentNumberOfDocs + 1 <= this.maxDocumentCount){
                return true;
            } else{
                return false;
            }
        }
        // If max has been set for number of possible bytes:
        if(this.isSetMaxBytes == true && this.isSetMaxDoc == false){
            if(this.currentNumberOfBytes + getBytesOfDocument(doc) <= this.maxDocumentBytes){
                return true;
            } else{
                return false;
            }
        }
        // If Max has been set for both # of documents and bytes:
        if(this.isSetMaxBytes == true && this.isSetMaxDoc == true){
            if(this.currentNumberOfBytes + getBytesOfDocument(doc) <= this.maxDocumentBytes && this.currentNumberOfDocs + 1 <= this.maxDocumentCount){
                return true;
            } else{
                return false;
            }
        }
        return true;
    }
    // Deletes all references to Doc without creating an undo in the commandStack,
    // hence making sure that this doc can never be retrieved after being deleted
    private void deleteDocDueToStorage(URI uri){
        if(uri == null){
            throw new IllegalArgumentException();
        }
        Document doc = getDocumentTemporarily(uri);
        if(doc != null && doc.getDocumentTxt() != null){
             Set<String> words = parseText(doc.getDocumentTxt());
            // Delete from Trie
             deleteFromTrie(words,doc);
        }
        //Delete From BTree
        Document deletedDocument = (Document) this.docBTree.put(uri, null);
        if(deletedDocument != null){
            // Adjust storage levels
            currentNumberOfBytes -= getBytesOfDocument(deletedDocument);
            currentNumberOfDocs--;
            // Delete from commandStack without undoing each command
            removeCommandsFromStack(uri);
        }
    }
    // Removes however many docs necessary from heap and moves them to disk- can be undone and brought back though(Stage 5)
    // @param doc --> document we want to put in memory
    private void moveDocsToDiskUntilEnoughRoom(Document doc) throws Exception{
        while(!enoughMemory(doc)){
            MinHeapDocument docToDisk = this.documentHeap.remove();
            Document docMovedToDisk = docBTree.get(docToDisk.getUri());
            docBTree.moveToDisk(docToDisk.getUri());
            //Remove from memory because was put in disk/storage
            this.currentNumberOfBytes -= getBytesOfDocument(docMovedToDisk);
            this.currentNumberOfDocs--;
            this.docsMovedToDisk.add(docToDisk);
            this.docsInHeap.remove(docToDisk);
            //this.documentsInDisk.add(docToDisk); --> Don't think I need this line
            // FOR STAGE 5: ADD IN COMMAND FOR COMMAND STACK TO DESERIALIZE AND BRING BACK TO MEMORY
        }
    }
    private void removeCommandsFromStack(URI uri){
        StackImpl<Undoable> tempStack = new StackImpl<Undoable>();
            while(this.commandStack.size() != 0){ 
                Undoable currentCommand = this.commandStack.peek();
                if(currentCommand instanceof GenericCommand){
                    //GenericCommand Instance
                    GenericCommand genericCommand = (GenericCommand)currentCommand;
                    Undoable commandForTempStackOrDelete = this.commandStack.pop();
                    if(!genericCommand.getTarget().equals(uri)){
                        tempStack.push(commandForTempStackOrDelete);
                    } 
                } else{ //CommandSet Instance
                    CommandSet commandSet = (CommandSet)currentCommand;
                    if(!commandSet.containsTarget(uri)){
                        // The current CommandSet doesn't contain a command for the given uri so push to tempStack
                        Undoable commandForTempStack = this.commandStack.pop();
                        tempStack.push(commandForTempStack);
                    } else{ // The current CommandSet contains a GenericCommand for the given uri so iterate to it and remove it:
                        Iterator commandSetItr = commandSet.iterator();
                        while(commandSetItr.hasNext()){
                            GenericCommand current = (GenericCommand)commandSetItr.next();
                            if(current.getTarget().equals(uri)){
                                commandSetItr.remove(); //ASK ABOUT CONCURRENT MOD EXCEPTION BC WANT TO REMOVE ALL COMMANDS with given uri 
                            } //MIGHT HAVE TO REMOVE ITERATOR FOR STAGE 5
                        }
                        if(commandSet.size() == 0){
                            this.commandStack.pop();
                        }
                    }
                }
            }
        while(tempStack.size() != 0){
            Undoable commandFromTempStack = tempStack.pop();
            this.commandStack.push(commandFromTempStack);
        }
    }
    private void deleteDocumentMiddleOfHeap(Document document){
        MinHeapDocument doc = new MinHeapDocument(document.getKey());
        document.setLastUseTime(0);
        this.documentHeap.reHeapify(doc);
        this.documentHeap.remove();
        this.docsInHeap.remove(doc);
    }
    private void adjustHeapAfterReplace(Document doc){
        MinHeapDocument minDoc = new MinHeapDocument(doc.getKey());
        if(!this.docsInHeap.contains(minDoc)){
            this.documentHeap.insert(minDoc);
            this.docsInHeap.add(minDoc);
        } else{
            this.documentHeap.reHeapify(minDoc);
        }
    }
}
