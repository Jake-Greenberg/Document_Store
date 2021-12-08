package edu.yu.cs.com1320.project.impl;

import edu.yu.cs.com1320.project.HashTable;

public class HashTableImpl<Key, Value> implements HashTable<Key, Value> {
/**
 * Instances of HashTable should be constructed with two type parameters, one for the type of the keys in the table and one for the type of the values
 * @param <Key>
 * @param <Value>
 */
class Entry<Key,Value>{
    Key key;
    Value value;
    private Entry<Key,Value> next;
    //Constructor for Entry Objects
    Entry(Key k, Value v){
        if(k == null){
            throw new IllegalArgumentException();
        }
        key = k;
        value = v;
        this.next = null;
    }
}
private Entry<?,?>[] table;
private static final double MAX_LOAD_FACTOR = 0.75;
private int currentNumberOfEntries = 0;
//Constructor for HashTableImpl Objects
public HashTableImpl(){
    this.table = new Entry[5];
}
private int hashFunction(Key key){
    return (key.hashCode() & 0x7fffffff) % this.table.length;//What should be instead of length here?
}
private double loadFactor(){
    return (double) this.currentNumberOfEntries/this.table.length;
}
private void rehash(){//ADD THE INCREMENT FOR SIZE OF NUMBER OF ENTRIES
    Entry<Key,Value>[] oldHashTable = (HashTableImpl<Key, Value>.Entry<Key, Value>[]) this.table;
    this.table = (Entry<Key,Value>[])new Entry[2 * oldHashTable.length];
    this.currentNumberOfEntries = 0;
    for(int i = 0; i < oldHashTable.length; i++){
        Entry<Key,Value> current = oldHashTable[i];
        while(current != null){
            this.put(current.key, current.value);
            current = current.next;
        }
    }
}
    /**
     * @param k the key whose value should be returned
     * @return the value that is stored in the HashTable for k, or null if there is no such key in the table
     */
    public Value get(Key k){
        int index = this.hashFunction(k);
        Entry<?,?> current = this.table[index];
        if(current != null){
            while(current != null && !current.key.equals(k)){
                current = current.next;
            }  
            if(current != null){
                return (Value) current.value;
            }
        }
        return null;
    }

    /**
     * @param k the key at which to store the value
     * @param v the value to store.
     * To delete an entry, pass a null as the Value argument.
     * @return if the key was already present in the HashTable, return the previous value stored for the key. If the key was not already present, return null.
     */
    //Put in new Entry altogether, alter the Value of an existing one, or delete an Entry.
    public Value put(Key k, Value v){
        if(loadFactor() >= MAX_LOAD_FACTOR){
            rehash();
        }
        if(k == null){
            throw new IllegalArgumentException();
        }
        if(v == null){
            return delete(k, v);
        }
        int index = hashFunction(k);
        Entry<Key,Value> oldEntry = (Entry<Key,Value>)this.table[index];
        Entry<Key,Value> newEntry = new Entry<Key,Value>(k,v);
        // Case One: Check if the index in the hashtable/array itself is null. If yes, replace null with the new Entry. If not null, go to Case Two.
        if(oldEntry == null){
            this.table[index] = newEntry;
            this.currentNumberOfEntries++;
            return null;
        } // Case Two: Look to see if the key we are looking for already exists in the hashtable- if yes, replace the old Value with the desired value. If the key is not yet in the hashtable, go to Case Three.
        while(oldEntry.next != null){
            if(oldEntry.key.equals(k)){
                Value oldEntryValue = oldEntry.value;
                oldEntry.value = v;
                return oldEntryValue;
            }
            oldEntry = oldEntry.next;
        // Case Three: Check to see if the key is located in the last slot in the list at this index. If yes, replace the key's Value with the desired Value. If not, go to Case Four.
        } if(oldEntry.key.equals(k)){
            Value oldEntryValue = oldEntry.value;
                oldEntry.value = v;
                return oldEntryValue;
        }
        // Case Four: Put the new Entry at the end of the list at the index.
        oldEntry.next = newEntry;
        this.currentNumberOfEntries++;
        return null;
    }


    private Value delete(Key k, Value v){
        int index = hashFunction(k);
        Entry<Key,Value> current = (Entry<Key,Value>) this.table[index];
        if(current == null || current.key == null){
            return null;
        }
        if(current.key.equals(k)){
            this.table[index] = current.next;
            this.currentNumberOfEntries--;
            return current.value;
        }
        Entry<Key,Value> previous = null;
        while(current != null && !current.key.equals(k)){
            previous = current;
            current = current.next;
        }
        if(current != null){ 
            if(current.next == null){
                previous.next = null;
                this.currentNumberOfEntries--;
                return current.value;
            }
            previous.next = current.next;
            this.currentNumberOfEntries--;
            return (Value) current.next.value;
        } else{
            return null;
        }
    }
}