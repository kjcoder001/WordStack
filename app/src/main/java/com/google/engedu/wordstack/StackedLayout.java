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

package com.google.engedu.wordstack;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Stack;

public class StackedLayout extends LinearLayout {

    private Stack<View> tiles = new Stack();

    public StackedLayout(Context context)
    {
        super(context);
    }

    /**
     * My code below.
     * push implementation for stackedLayout (analogous to stacks push).
     * -->The words are pushed in reverse order from onStartGame().
     * -->But the user should get the letters in the same order as that of scrambledWord. Hence the push method makes sure
     *    that the order of letters is preserved by calling removeView() and addView().
     * @param tile
     */
    public void push(View tile) {

        if(!tiles.empty())
            {
              removeView(tiles.peek());
            }

        tiles.push(tile);
        addView(tile);


    }

    /**
     * My code below.
     *  --> pop() is called from LetterTile.moveViewtoGroup() method.
     *  --> It is used to implement the logic of frozen and unfrozen tiles.
     * @return
     */
    public View pop() {

            View popped=null;
            // To avoid crashes.Takes care of test cases .
            if(!tiles.isEmpty())
                popped=tiles.pop();

            removeView(popped);
            if(!tiles.empty())
                addView(tiles.peek());
            return popped;
    }

    public View peek() {
        return tiles.peek();
    }

    public boolean empty() {
        return tiles.empty();
    }

    /**
     * My code below.
     * clear() is used to erase the current word and game and is called before initializing a new game.
     */
    public void clear() {
      if(!tiles.isEmpty())
            removeView(tiles.peek());
        tiles.clear();

    }
}
