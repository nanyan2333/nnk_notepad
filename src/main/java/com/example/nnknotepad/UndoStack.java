package com.example.nnknotepad;

import java.util.Stack;

public class UndoStack {
    private final Stack<String> textAreaStack;
    private int contain;
    private static final int MAX_SAVE_STEP = 15;

    public UndoStack() {
        textAreaStack = new Stack<>();
        contain = 0;
    }

    public void push(String quickPhoto) {
        if (contain < MAX_SAVE_STEP) {
            textAreaStack.push(quickPhoto);
            contain++;
        } else {
            textAreaStack.pop();
            textAreaStack.push(quickPhoto);
        }
    }
    public String pop() {
        if (contain > 0) {
            contain--;
            return textAreaStack.pop();
        } else {
            return null;
        }
    }
    public void clear() {
        textAreaStack.clear();
        contain = 0;
    }
}
