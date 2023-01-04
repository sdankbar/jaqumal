/**
 * The MIT License
 * Copyright © 2020 Stephen Dankbar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.sdankbar.qml.utility;


import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Wraps text to a given line width.
 * 
 * Experiment with Github Copilot.
 */
public class TextWrapper {

    private TextWrapper() {
        // Empty Implementation
    }

    public static String wrap(String input, int lineWidth, List<String> wordSeparators) {
        Objects.requireNonNull(input, "input is null");
        if (input.length() <= lineWidth) {
            return input;
        }

        Objects.requireNonNull(wordSeparators, "wordSeparators is null");
        Preconditions.checkArgument(lineWidth > 0, "lineWidth must be > 0 for non-empty string input");

        StringBuilder output = new StringBuilder(input.length() + input.length() / lineWidth);
        int nextIndexIntoInput = 0;
        int currentLineLength = 0;
        AtomicReference<String> foundWordSeparator = new AtomicReference<>();

        while (nextIndexIntoInput < input.length()) {
            int nextWordLength = getNextWordLength(input, nextIndexIntoInput, wordSeparators, foundWordSeparator);
            if (currentLineLength + nextWordLength > lineWidth) {
                output.append('\n');
                currentLineLength = 0;
            } else {
                output.append(input, nextIndexIntoInput, nextIndexIntoInput + nextWordLength);
                currentLineLength += nextWordLength;
                nextIndexIntoInput += nextWordLength;
            }
        }

        return output.toString();
    }

    private static int getNextWordLength(String input, int startIndex, List<String> wordSeparators, AtomicReference<String> foundWordSeparator) {
        int nextWordStart;
        if (wordSeparators.size() > 1) {
            nextWordStart = findIndexOfString(input, startIndex, wordSeparators, foundWordSeparator);
        } else {
            nextWordStart = input.indexOf(wordSeparators.get(0), startIndex);
            foundWordSeparator.set(wordSeparators.get(0));
        }
        if (nextWordStart == -1) {
            return input.length() - startIndex;
        } else {
            return nextWordStart - startIndex + foundWordSeparator.get().length();
        }
    }

    private static int findIndexOfString(String input, int startIndex, List<String> wordSeparators, AtomicReference<String> foundWordSeparator) {
        int index = -1;
        for (String s : wordSeparators) {
            int i = input.indexOf(s, startIndex);
            if (i != -1 && (index == -1 || i < index)) {
                index = i;
                foundWordSeparator.set(s);
            }
        }
        return index;
    }
}
