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

package com.google.engedu.touringmusician;


import android.graphics.Point;
import android.util.Log;

import java.util.Iterator;

import static android.content.ContentValues.TAG;

public class CircularLinkedList implements Iterable<Point> {

    private class Node {
        Point point;
        Node prev, next;
        /**
         **
         **  YOUR CODE GOES HERE
         **
         **/
    }

    Node head;

    public void insertBeginning(Point p) {
        Node newNode = new Node();
        newNode.point = p;

        if(head == null){
            newNode.next = newNode;
            newNode.prev = newNode;
        }
        else{
            newNode.next = head;
            newNode.prev = head.prev;
            head.prev.next = newNode;
            head.prev = newNode;
        }

        head = newNode;
    }

    private float distanceBetween(Point from, Point to) {
        return (float) Math.sqrt(Math.pow(from.y-to.y, 2) + Math.pow(from.x-to.x, 2));
    }

    public float totalDistance() {
        if(head == null)
            return 0;

        float total = 0;
        Node currNode = head.next;
        while(!currNode.equals(head)) {
            total += distanceBetween(currNode.point, currNode.next.point);
            currNode = currNode.next;
        }
        return total;
    }

    public void insertNearest(Point p) {
        Node newNode = new Node();
        newNode.point = p;
        Node currNode = head;

        if(head == null){
            newNode.next = newNode;
            newNode.prev = newNode;
            head = newNode;
            return;
        }

        Node tempNode = head;
        float minDistance = (float)1e9;
        newNode.point = p;

        do{
            if(minDistance > distanceBetween(tempNode.point, newNode.point)) {
                currNode = tempNode;
                minDistance = distanceBetween(tempNode.point, newNode.point);
            }
            tempNode = tempNode.next;
        }while(!tempNode.equals(head));


        if(distanceBetween(newNode.point, currNode.next.point) <
                distanceBetween(newNode.point, currNode.prev.point)){

            newNode.prev = currNode;
            newNode.next = currNode.next;
            currNode.next.prev = newNode;
            currNode.next = newNode;
        }
        else{
            newNode.next = currNode;
            newNode.prev = currNode.prev;
            currNode.prev.next = newNode;
            currNode.prev = newNode;
        }


    }

    public void insertSmallest(Point p) {
        Node newNode = new Node();
        newNode.point = p;

        if(head == null){
            newNode.next = newNode;
            newNode.prev = newNode;
            head = newNode;
            return;
        }

        float totalDis = totalDistance();
        Node tempNode = head;
        Node currNode = head;
        float minDistance = (float)1e9;

        do{
            float currDis = distanceBetween(tempNode.point, tempNode.next.point);
            float newAdd = distanceBetween(newNode.point, tempNode.point) +
                    distanceBetween(newNode.point, tempNode.next.point);

            if(minDistance > totalDis - currDis + newAdd){
                minDistance = totalDis - currDis + newAdd;
                currNode = tempNode;
            }
            tempNode = tempNode.next;
        }while(!tempNode.equals(head));

        newNode.next = currNode.next;
        newNode.prev = currNode;
        currNode.next.prev = newNode;
        currNode.next = newNode;
    }

    public void reset() {
        head = null;
    }

    private class CircularLinkedListIterator implements Iterator<Point> {

        Node current;

        public CircularLinkedListIterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return (current != null);
        }

        @Override
        public Point next() {
            Point toReturn = current.point;
            current = current.next;
            if (current == head) {
                current = null;
            }
            return toReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Iterator<Point> iterator() {
        return new CircularLinkedListIterator();
    }


}
