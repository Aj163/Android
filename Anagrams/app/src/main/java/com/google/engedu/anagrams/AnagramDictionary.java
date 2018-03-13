/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.anagrams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import android.nfc.Tag;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class AnagramDictionary {

    private static final int MIN_NUM_ANAGRAMS = 5;
    private static final int DEFAULT_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 9;
    private Random random = new Random();

    static int wordLength = DEFAULT_WORD_LENGTH;
    ArrayList<String> wordList;
    HashSet<String> wordSet;
    HashMap<String, ArrayList<String>> lettersToWord;
    HashMap<Integer, ArrayList<String>> sizeToWords;

    public AnagramDictionary(Reader reader) throws IOException {
        BufferedReader in = new BufferedReader(reader);
        String line;

        wordList = new ArrayList<String>();
        wordSet = new HashSet<String>();
        lettersToWord = new HashMap<String, ArrayList<String>>();
        sizeToWords = new HashMap<Integer, ArrayList<String>>();

        while((line = in.readLine()) != null) {
            String word = line.trim();
            wordList.add(word);
            wordSet.add(word);
            sortLetters(word);

            if(!sizeToWords.containsKey(word.length()))
                sizeToWords.put(word.length(), new ArrayList<String>());
            sizeToWords.get(word.length()).add(word);
        }

        if(wordLength < MAX_WORD_LENGTH)
            wordLength++;
    }

    public String sortLetters(String a){
        char[] b = a.toCharArray();
        Arrays.sort(b);
        String ret = new String(b);

        if(wordSet.contains(a))
        {
            if (lettersToWord.containsKey(ret)) {
                if (lettersToWord.get(ret).indexOf(a) == -1)
                    lettersToWord.get(ret).add(a);
            } else {
                lettersToWord.put(ret, new ArrayList<String>());
                lettersToWord.get(ret).add(a);
            }
        }
        return ret;
    }

    public boolean isGoodWord(String word, String base) {
        if(word.contains(base))
            return false;
        if(wordSet.contains(word))
            return true;
        return false;
    }

    public ArrayList<String> getAnagrams(String targetWord) {
        if(lettersToWord.containsKey(sortLetters(targetWord)))
            return lettersToWord.get(sortLetters(targetWord));
        return new ArrayList<String>();
    }

    public List<String> getAnagramsWithOneMoreLetter(String word) {
        ArrayList<String> result = new ArrayList<String>();
        for(int i='a'; i<='z'; i++) {
            String tempString = word;
            tempString = tempString + Character.toString((char) i);

            ArrayList<String> anag = getAnagrams(tempString);
            for(String wordInAnag : anag)
                if(isGoodWord(wordInAnag, word))
                    result.add(wordInAnag);
        }

        return result;
    }

    public String pickGoodStarterWord() {
        String ret;
        while(true){
            int index = random.nextInt(sizeToWords.get(wordLength).size());
            ret = sizeToWords.get(wordLength).get(index);
            if(getAnagramsWithOneMoreLetter(ret).size() >= MIN_NUM_ANAGRAMS)
                break;
        }

        return ret;
    }
}
