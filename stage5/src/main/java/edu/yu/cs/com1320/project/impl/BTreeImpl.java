package edu.yu.cs.com1320.project.impl;

import java.io.IOException;

import edu.yu.cs.com1320.project.BTree;
import edu.yu.cs.com1320.project.stage5.Document;
import edu.yu.cs.com1320.project.stage5.PersistenceManager;

public class BTreeImpl<Key extends Comparable<Key>,Value> implements BTree<Key,Value> {
    //max children per B-tree node = MAX-1 (must be an even number and greater than 2)
    private Node root; //root of the B-tree
    private int height; //height of the B-tree
    private int n; //number of key-value pairs in the B-tree
    private PersistenceManager<Key,Value> pm;
    //Node Constructor
    class Node{
        private int entryCount; // number of entries
        private Entry[] entries = new Entry[6]; // the array of children

        public Node(int entries){
            this.entryCount = entries;
        }
    }
    //Entry constructor
    class Entry<Key extends Comparable<Key>,Value>{
        private Key key;
        private Value val;
        private Node child;

        public Entry(Key key, Value val, Node child){
            this.key = key;
            this.val = val;
            this.child = child;
        }
    }
    //BTreeImpl Constructor 
    public BTreeImpl(){
        this.root = new Node(0);
    }
    
    public Value get(Key k){
        if (k == null){
            throw new IllegalArgumentException("argument to get() is null");
        }
        Entry<Key,Value> entry = (Entry<Key,Value>)this.get(this.root, k, this.height);
        if(entry != null){
            if(entry.val == null){
                try {
                    Value v = pm.deserialize(k);
                    if(v!= null){
                        //Exists on disk- return from disk and set value in BTree to document instead of null
                        this.put(k,v);
                        return v;
                    } else{ //Doesn't have value anywhere
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //Return value in memory
            return (Value)entry.val;
        } 
        return null;
    }

    private Entry get(Node currentNode, Key k, int height){
        Entry[] entries = currentNode.entries;

        //current node is external (i.e. height == 0)
        if (height == 0){
            for (int j = 0; j < currentNode.entryCount; j++){
                if(isEqual(k, entries[j].key)){
                    //found desired key. Return its value
                    return entries[j];
                }
            }
        //didn't find the key
        return null;
        } else{ // current node is internal so recursively call until get to leaf node
            for (int j = 0; j < currentNode.entryCount; j++){
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be in the subtree below the current entry),
                //then recurse into the current entry’s child
                if (j + 1 == currentNode.entryCount || less(k, entries[j + 1].key)){
                    return this.get(entries[j].child, k, height - 1);
                }
            }
            //didn't find the key
            return null;
        }
    }
    /**
     * Inserts the key-value pair into the symbol table, overwriting the old
     * value with the new value if the key is already in the symbol table. If
     * the value is null, this effectively deletes the key from the
     * symbol table.
     *
     * @param key the key
     * @param val the value
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public Value put(Key k, Value v){
        if (k == null){
            throw new IllegalArgumentException("argument key to put() is null");
        }
        //if the key already exists in the b-tree, simply replace the value
        Entry alreadyThere = this.get(this.root, k, this.height);
        if(alreadyThere != null){
            Value oldValue = (Value)alreadyThere.val;
            if(alreadyThere.val != null){
                alreadyThere.val = v;
                return oldValue;
            } else{
                try {
                    pm.delete(k);
                    alreadyThere.val = v;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return oldValue;
            } 
        }
        //Not in memory nor on disk so make new node
        Node newNode = this.put(this.root, k, v, this.height);
        this.n++;
        if (newNode == null){ //No split of root so we're done
            return null;
        }

        //split the root:
        //Create a new node to be the root.
        //Set the old root to be new root's first entry.
        //Set the node returned from the call to put to be new root's second entry
        Node newRoot = new Node(2);
        newRoot.entries[0] = new Entry(this.root.entries[0].key, null, this.root);
        newRoot.entries[1] = new Entry(newNode.entries[0].key, null, newNode);
        this.root = newRoot;
        //a split at the root always increases the tree height by 1
        this.height++;
        return null;
    }
    private Node put(Node currentNode, Key k, Value v, int height){
        int j;
        Entry newEntry = new Entry(k, v, null);

        //external node
        if (height == 0){
            //find index in currentNode’s entry[] to insert new entry
            //we look for key < entry.key since we want to leave j
            //pointing to the slot to insert the new entry, hence we want to find
            //the first entry in the current node that key is LESS THAN
            for (j = 0; j < currentNode.entryCount; j++){
                if (less(k, currentNode.entries[j].key)){
                    break;
                }
            }
        }

        // internal node
        else{
            //find index in node entry array to insert the new entry
            for(j = 0; j < currentNode.entryCount; j++){
                //if (we are at the last key in this node OR the key we
                //are looking for is less than the next key, i.e. the
                //desired key must be added to the subtree below the current entry),
                //then do a recursive call to put on the current entry’s child
                if ((j + 1 == currentNode.entryCount) || less(k, currentNode.entries[j + 1].key)){
                    //increment j (j++) after the call so that a new entry created by a split
                    //will be inserted in the next slot
                    Node newNode = this.put(currentNode.entries[j++].child, k, v, height - 1);
                    if (newNode == null){
                        return null;
                    }
                    //if the call to put returned a node, it means I need to add a new entry to
                    //the current node
                    newEntry.key = newNode.entries[0].key;
                    newEntry.val = null;
                    newEntry.child = newNode;
                    break;
                }
            }
        }
        //shift entries over one place to make room for new entry
        for (int i = currentNode.entryCount; i > j; i--){
            currentNode.entries[i] = currentNode.entries[i - 1];
        }
        //add new entry
        currentNode.entries[j] = newEntry;
        currentNode.entryCount++;
        if (currentNode.entryCount < 6){
            //no structural changes needed in the tree
            //so just return null
            return null;
        }
        else{
            //will have to create new entry in the parent due
            //to the split, so return the new node, which is
            //the node for which the new entry will be created
            return this.split(currentNode, height);
        }
    }
        /**
     * split node in half
     * @param currentNode
     * @return new node
     */
    private Node split(Node currentNode, int height){
        Node newNode = new Node(3);
        //by changing currentNode.entryCount, we will treat any value
        //at index higher than the new currentNode.entryCount as if
        //it doesn't exist
        currentNode.entryCount = 3;
        //copy top half of h into t
        for (int j = 0; j < 3; j++){
            newNode.entries[j] = currentNode.entries[3 + j];
            //null out the indices which used to hold the keys which were moved to the new node
            currentNode.entries[3 + j] = null;
        }
        return newNode;
    }
    //Uses documentPersistenceManager
    public void moveToDisk(Key k) throws Exception{
        Value value = this.get(k);
        //Replace old value with new value on disk
        this.put(k, null); //Fix this: what needs to be the new value? A reference to the document which will now be on disk
        pm.serialize(k, value);
    }

    public void setPersistenceManager(PersistenceManager<Key,Value> pm){
        this.pm = pm;
    }
    // comparison functions - make Comparable instead of Key to avoid casts
    private boolean less(Comparable k1, Comparable k2){
        return k1.compareTo(k2) < 0;
    }

    private boolean isEqual(Comparable k1, Comparable k2){
        return k1.compareTo(k2) == 0;
    }
}
