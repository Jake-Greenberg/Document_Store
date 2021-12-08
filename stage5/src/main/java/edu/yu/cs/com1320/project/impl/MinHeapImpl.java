package edu.yu.cs.com1320.project.impl; //Ask why it needs main.java.

import java.util.Arrays;
import java.util.HashMap;
import java.util.NoSuchElementException;

import edu.yu.cs.com1320.project.MinHeap;

public class MinHeapImpl<E extends Comparable<E>> extends MinHeap<E>{ //Check regarding syntax here with extends and Comparable
    private HashMap<E, Integer> elementIndices;
    
    public MinHeapImpl(){
       this.elements = (E[]) new Comparable[100];
       this.elementIndices = new HashMap<>();
   }
   @Override //DEFINITELY CHECK- might just be upHeap() and then downHeap() bc they do comparisons
   public void reHeapify(E element){
       if(!Arrays.asList(elements).contains(element) || isEmpty()){
           throw new NoSuchElementException();
       }
       int index = getArrayIndex(element);
       upHeap(index);
       int newIndex = getArrayIndex(element);
       downHeap(newIndex);
   }
   @Override
   protected int getArrayIndex(E element){
        if(this.isEmpty() || !Arrays.asList(elements).contains(element)){
            return -1;
        }
        for(E e : elements){
            if(e != null){
                if(e.equals(element)){
                    return Arrays.asList(elements).indexOf(e);
                }
            }
        }
        return 0;
   }
   @Override
   protected void doubleArraySize(){
        E[] oldArray = this.elements;
        this.elements = (E[])new Comparable[elements.length * 2];
        for(int i = 0; i < this.count; i++){
            this.elements[i] = oldArray[i];
        }
   }
}
