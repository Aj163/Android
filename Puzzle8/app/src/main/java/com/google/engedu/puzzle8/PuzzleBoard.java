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

package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;
    private int steps = 0;
    private PuzzleBoard previousBoard = this;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        tiles = new ArrayList<>();
        bitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
        for(int i=0; i<NUM_TILES; i++)
            for(int j=0; j<NUM_TILES; j++) {
                int index = i*NUM_TILES+j;
                if(index == NUM_TILES*NUM_TILES -1) {
                    tiles.add(null);
                    continue;
                }
                tiles.add(new PuzzleTile(Bitmap.createBitmap(bitmap, j * parentWidth / NUM_TILES,
                        i * parentWidth / NUM_TILES, parentWidth / NUM_TILES,
                        parentWidth / NUM_TILES), i * NUM_TILES + j));
            }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps +1;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }

    public void setPreviousBoard(PuzzleBoard t1){
        previousBoard = t1;
    }

    PuzzleBoard getPreviousBoard(){
        return previousBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        ArrayList<PuzzleBoard> neighbourList = new ArrayList<PuzzleBoard>();
        int pos=0;
        for(int i=0; i<NUM_TILES*NUM_TILES; i++)
            if(tiles.get(i) == null){
                pos = i;
                break;
            }

        for(int i=0; i<4; i++){
            int x = pos/NUM_TILES + NEIGHBOUR_COORDS[i][0];
            int y = pos%NUM_TILES + NEIGHBOUR_COORDS[i][1];
            if(x<0 || x>=NUM_TILES || y<0 || y>= NUM_TILES)
                continue;

            PuzzleBoard newNeighbour = new PuzzleBoard(this);
            newNeighbour.swapTiles(pos, x*NUM_TILES+y);
            neighbourList.add(newNeighbour);
        }
        return neighbourList;
    }

    public int priority() {
        int ret = 0;
        for(int i=0; i<NUM_TILES*NUM_TILES; i++){
            if(tiles.get(i) == null)
                continue;
            int x = tiles.get(i).getNumber()/NUM_TILES;
            int y = tiles.get(i).getNumber()%NUM_TILES;
            ret += Math.abs(x - i/NUM_TILES) + Math.abs(y - i%NUM_TILES);
        }
        return ret + steps;
    }

}
