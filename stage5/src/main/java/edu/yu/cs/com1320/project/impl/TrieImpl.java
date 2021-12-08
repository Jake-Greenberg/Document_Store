package edu.yu.cs.com1320.project.impl;

import java.util.*;
import edu.yu.cs.com1320.project.Trie;

public class TrieImpl<Value> implements Trie<Value> {
    private final int ALPHABET_SIZE = 36; //ASK WHAT TO DO WITHOUT STATIC KEYWORD AND WHY 36 AS OPPOSED TO 35
    private Node<Value> root;
    class Node <Value>{
        protected List<Value> valuesAtNode;
        protected Node[] links = new Node[ALPHABET_SIZE];
        Node(){
            valuesAtNode = null; //Ask what else can be constructed if not this?
        }
    }

    public TrieImpl(){
        this.root = new Node<Value>();
    }
    
    /**
     * add the given value at the given key
     * @param key
     * @param val
     */
    public void put(String key, Value val){
        if(key == null){
            throw new IllegalArgumentException();
        }
        key = key.toLowerCase();
        if(key.equals("")){
            return;
        }
        //deleteAll the value from this key
        if(val == null){
            return;
        }
        else{
            this.put(this.root, key, val, 0);
        }
    }
    private Node<Value> put(Node<Value> x, String key, Value val, int d){
        //create a new node
        if(x == null){
            x = new Node<Value>();
        }
        //we've reached the last node in the key,
        //set the value for the key and return the node
        if(d == key.length()){
            if(x.valuesAtNode == null){
                x.valuesAtNode = new ArrayList<>();
            }
            //Add to list of Values if isn't already contained in list and replace if already exists
            if(!x.valuesAtNode.contains(val)){
                x.valuesAtNode.add(val);
            }
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        if(Character.isDigit(c)){
            x.links[c-22] = this.put(x.links[c-22], key, val, d +1);
        } else{
            x.links[c-97] = this.put(x.links[c-97], key, val, d + 1);
        }
        return x;
    }

    /**
     * get all exact matches for the given key, sorted in descending order.
     * Search is CASE INSENSITIVE.
     * @param key
     * @param comparator used to sort  values
     * @return a List of matching Values, in descending order
     */
    public List<Value> getAllSorted(String key, Comparator<Value> comparator){ 
        List<Value> emptyList = new ArrayList<>();
        if(key == null || comparator == null){
            throw new IllegalArgumentException();
        }
        key = key.toLowerCase();
        if(key == ""){
            return emptyList;
        }
        Node<Value> x = this.get(this.root, key, 0);
        if(x == null){ //No matches :(
            return emptyList;
        }
        if(x.valuesAtNode != null){
            x.valuesAtNode.sort(comparator);
        }
        //x.valuesAtNode.sort(comparator);//WHAT SHOULD BE DONE HERE? STILL DON'T CHAP
        return x.valuesAtNode;
    }
    private Node<Value> get(Node<Value> x, String key, int d){
        //link was null - return null, indicating a miss
        if(x == null){
            return null;
        }
        //we've reached the last node in the key,
        //return the node
        if(d == key.length()){
            return x;
        }
        //proceed to the next node in the chain of nodes that
        //forms the desired key
        char c = key.charAt(d);
        if(Character.isDigit(c)){
            return this.get(x.links[c-22], key, d + 1);
        }else{
            return this.get(x.links[c-97], key, d + 1);
        }
    }


    /**
     * get all matches which contain a String with the given prefix, sorted in descending order.
     * For example, if the key is "Too", you would return any value that contains "Tool", "Too", "Tooth", "Toodle", etc.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @param comparator used to sort values
     * @return a List of all matching Values containing the given prefix, in descending order
     */
    public List<Value> getAllWithPrefixSorted(String prefix, Comparator<Value> comparator){
        List<Value> emptyList = new ArrayList<>();
        if(prefix == null || comparator == null){
            throw new IllegalArgumentException();
        }
        prefix = prefix.toLowerCase();
        if(prefix == ""){
            return emptyList;
        }
        Node<Value> x = this.get(this.root, prefix, 0);
        if(x == null){ //No matches :(
            return emptyList;
        }
        List<Value> allValsOfPrefix = new ArrayList<>();
        if(x.valuesAtNode != null){
            allValsOfPrefix.addAll(x.valuesAtNode);
        }
        
        this.addValuesAfterPrefix(x, allValsOfPrefix);

        //BEFORE SORTING we want to add all the other values from the other lists at each node 
        //below the node at the end of the prefix given --> Ex: S, H, E(node) 
        //and then "shell" and "sheet" and so on
        allValsOfPrefix.sort(comparator);//WHAT SHOULD BE DONE HERE? 
        return allValsOfPrefix;         
    }
    //Travel from the last node of the prefix and see if there are any more nodes 
    //that have any values stored at them:
    private void addValuesAfterPrefix(Node x, List<Value> listOfValues){
        //CHECK WITH TA OR PT REGARDING THIS. COULD BE DONE
        for(int c = 0; c < 36; c ++){
            Node<Value> nextNode = x.links[c];
            if(nextNode != null){//Check and see if current node has link to next node and if yes
                List<Value> valsAtNode= nextNode.valuesAtNode;
                if(valsAtNode != null){
                    for(Value v : valsAtNode){
                        if(!listOfValues.contains(v)){
                        listOfValues.add(v);
                        }
                    }
                }
                addValuesAfterPrefix(nextNode, listOfValues);
            } 
        }
    }
    /**
     * Delete the subtree rooted at the last character of the prefix.
     * Search is CASE INSENSITIVE.
     * @param prefix
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAllWithPrefix(String prefix){
        if(prefix == null){
            throw new IllegalArgumentException();
        }
        if(prefix == ""){
            Set<Value> emptySet = new HashSet<>();
            return emptySet;
        }
        prefix = prefix.toLowerCase();
        Set<Value> deletedValues = new HashSet<>();
        deleteAllWithPrefix(this.root, prefix, 0, deletedValues);
        //Travel down to end of prefix and set the value of the node equal to null
        //Then go through every node in its array of nodes and if not null, travel to it 
        //And check if the value at that node is not null. If not null, set to null 
        //and traverse that Node's array of nodes etc.

        return deletedValues;
    }
    private void deleteValuesAfterPrefix(Node<Value> x, Set<Value> setOfValues){
        //CHECK WITH TA OR PT REGARDING THIS. COULD BE DONE
        for(int c = 0; c < 36; c ++){
            Node<Value> nextNode = x.links[c];
            if(nextNode != null){//Check and see if current node has link to next node and if yes
                List<Value> valsAtNode= nextNode.valuesAtNode;
                Set<Value> valsToRemove = new HashSet<>();
                if(valsAtNode != null){
                    for(Value v : valsAtNode){
                        if(!setOfValues.contains(v)){
                            setOfValues.add(v);
                            valsToRemove.add(v);
                            if(valsAtNode.size()== 0){
                                valsAtNode = null;
                            }
                        }
                    }
                    valsAtNode.removeAll(valsToRemove);
                }
                deleteValuesAfterPrefix(nextNode, setOfValues);
            } 
        }
    }
    private Node<Value> deleteAllWithPrefix(Node<Value> x, String prefix, int d, Set<Value> valuesDeleted){
        prefix = prefix.toLowerCase();
        //Can't delete a word that isn't in the trie so 
        //return null which will return empty set in method above:
        if(x == null){
            return null;
        }
        //we're at the node to del - set the val to null
        if(d == prefix.length()){
            if(x.valuesAtNode != null){
               valuesDeleted.addAll(x.valuesAtNode); 
            }
            x.valuesAtNode = null;
            deleteValuesAfterPrefix(x, valuesDeleted);
        }
        //continue down the trie to the target node
        else{
            char c = prefix.charAt(d);
            if(Character.isDigit(c)){
                x.links[c-22] = this.deleteAllWithPrefix(x.links[c-22], prefix, d + 1, valuesDeleted);
            }else{
                x.links[c-97] = this.deleteAllWithPrefix(x.links[c-97], prefix, d + 1, valuesDeleted);
            }
            
        }
        return checkForOtherNodes(x);
    }

    /**
     * Delete all values from the node of the given key (do not remove the values from other nodes in the Trie)
     * @param key
     * @return a Set of all Values that were deleted.
     */
    public Set<Value> deleteAll(String key){
        if(key == null){
            throw new IllegalArgumentException();
        }
        key = key.toLowerCase();
        Set<Value> deletedValues = new HashSet<>();
        deleteAll(this.root, key, 0, deletedValues);
        return deletedValues;
    }
    private Node deleteAll(Node x, String key, int d, Set<Value> valuesDeleted){
        //Can't delete a word that isn't in the trie so 
        //return null which will return empty set in method above:
        if(x == null){
            return null;
        }
        //we're at the node to del - set the val to null
        if(d == key.length()){
            if(x.valuesAtNode != null){
               valuesDeleted.addAll(x.valuesAtNode); 
            }
            x.valuesAtNode = null;
        }
        //continue down the trie to the target node
        else{
            char c = key.charAt(d);
            if(Character.isDigit(c)){
                x.links[c-22] = this.deleteAll(x.links[c-22], key, d + 1, valuesDeleted);
            }else{
                x.links[c-97] = this.deleteAll(x.links[c-97], key, d + 1, valuesDeleted);
            }  
        }
        return checkForOtherNodes(x);
    }
    private Node checkForOtherNodes(Node x){
        //this node has a val – do nothing, return the node
        if(x.valuesAtNode != null){
            return x;
        }
        //remove subtrie rooted at x if it is completely empty	
        for(int c = 0; c < 36; c++){
            if (x.links[c] != null){
                return x; //not empty
            }
        }
        //empty - set this link to null in the parent
        return null;
    }
    

    /**
     * Remove the given value from the node of the given key (do not remove the value from other nodes in the Trie)
     * @param key
     * @param val
     * @return the value which was deleted. If the key did not contain the given value, return null.
     */
    public Value delete(String key, Value val){
        if(key == null || val == null){
            throw new IllegalArgumentException();
        }
        key = key.toLowerCase();
        List<Value> deletedValue = new ArrayList<>();
        this.delete(this.root, key, val, 0, deletedValue);
        if(deletedValue.size() == 0){
            return null;
        }
        return deletedValue.get(0);
        
    }
    private Node delete(Node x, String key, Value val, int d, List<Value> deletedValue){
        //Trie does not contain this key and, in turn, does not contain the value
        if(x == null){
            return null;
        }
        if(d == key.length()){
            if(x.valuesAtNode != null){
                List<Value> valuesToBeRemoved = new ArrayList<>();
                for(Value v : (List<Value>)x.valuesAtNode){
                    if(v.equals(val)){
                       Value valueDeleted = v;
                       deletedValue.add(valueDeleted);
                       valuesToBeRemoved.add(v);
                    }
                }
                x.valuesAtNode.removeAll(valuesToBeRemoved);
                // Value valueDeleted = val;
                // deletedValue.add(valueDeleted);//ASK JONATHAN ABOUT HOW TO RETURN THIS
                // x.valuesAtNode.remove(val);
                if(x.valuesAtNode.size()== 0){
                    x.valuesAtNode = null;
                    checkForOtherNodes(x);
                } 
            }
            return x;
        }
        char c = key.charAt(d);
        if(Character.isDigit(c)){
            x.links[c-22] = this.delete(x.links[c-22], key, val, d + 1, deletedValue);
        }else{
            x.links[c-97] = this.delete(x.links[c-97], key, val, d + 1, deletedValue);
        }
        return x; 
    }
}
