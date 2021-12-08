package edu.yu.cs.com1320.project.impl;

import java.util.LinkedList;
import edu.yu.cs.com1320.project.Stack;

public class StackImpl<T> implements Stack<T> {
    private LinkedList<T> myStack;
     
    public StackImpl(){
        this.myStack = new LinkedList<T>();
    }
       /**
     * @param element object to add to the Stack
     */
    public void push(T element){
        this.myStack.addFirst(element);
    }

    /**
     * removes and returns element at the top of the stack
     * @return element at the top of the stack, null if the stack is empty
     */
   public T pop(){
       return this.myStack.pollFirst();
   }

    /**
     *
     * @return the element at the top of the stack without removing it
     */
   public T peek(){
        if(this.myStack.size() == 0){
            return null;
        }
        if(this.myStack.getFirst() == null){
            return null;
        } else{
            return this.myStack.getFirst();
        }
   }
    /**
     *
     * @return how many elements are currently in the stack
     */
   public int size(){
       return this.myStack.size();
   }
}

