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

import android.content.res.AssetManager;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private static final int WORD_LENGTH = 5;
    public static final int LIGHT_BLUE = Color.rgb(176, 200, 255);
    public static final int LIGHT_GREEN = Color.rgb(200, 255, 200);
    private ArrayList<String> words = new ArrayList<>();
    private Random random = new Random();
    private StackedLayout stackedLayout;
    private String word1, word2,userWord1,userWord2;

    // placedTiles keeps track of the tiles that have been played or moved into the Linearlayout.
    Stack<LetterTile> placedTiles=new Stack<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while((line = in.readLine()) != null) {
                String word = line.trim();
                // My code below . Add the words from the text file asset into an ArrayList<>().
                if(word.length()==WORD_LENGTH)
                {
                    words.add(word);
                }
            }
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        LinearLayout verticalLayout = (LinearLayout) findViewById(R.id.vertical_layout);
        stackedLayout = new StackedLayout(this);
        verticalLayout.addView(stackedLayout, 3);

        View word1LinearLayout = findViewById(R.id.word1);
        word1LinearLayout.setOnTouchListener(new TouchListener());
        word1LinearLayout.setOnDragListener(new DragListener());
        View word2LinearLayout = findViewById(R.id.word2);
        word2LinearLayout.setOnTouchListener(new TouchListener());
        word2LinearLayout.setOnDragListener(new DragListener());
        userWord1="";
        userWord2="";
    }

    private class TouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !stackedLayout.empty()) {
                LetterTile tile = (LetterTile) stackedLayout.peek();
                tile.moveToViewGroup((ViewGroup) v);
                if (stackedLayout.empty()) {
                    TextView messageBox = (TextView) findViewById(R.id.message_box);
                    messageBox.setText(word1 + " " + word2);
                }
                placedTiles.push(tile);

                return true;
            }
            return false;
        }
    }

    private class DragListener implements View.OnDragListener {

        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackgroundColor(LIGHT_GREEN);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(LIGHT_BLUE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(Color.WHITE);
                    v.invalidate();
                    return true;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign Tile to the target Layout
                    LetterTile tile = (LetterTile) event.getLocalState();

                    /**
                     * Following snippet is used to record the response of the the user/player to eventually declare the winner
                     */

                    if(v.getId()==R.id.word1)
                        userWord1 += tile.moveToViewGroup((ViewGroup) v);
                    else
                        userWord2 += tile.moveToViewGroup((ViewGroup)v);


                    if (stackedLayout.empty()) {
                        //TextView messageBox = (TextView) findViewById(R.id.message_box);
                        //messageBox.setText(word1 + " " + word2);
                        Log.i("tagy",userWord1+" "+userWord2);
                        checkwin();
                    }
                    //When a tile is placed in the linearLayout via drag and drop.
                    else
                    {
                            placedTiles.push(tile);
                    }

                    return true;
            }
            return false;
        }
    }

    /**
     * My code below
     * -->onStartGame() initializes a new game by getting two words from the dictionary(arraylist) and scrambling them
     *    according to some logic. Logic used is here is to maintain a counter of the both the words, select any word at
     *    random and append the character at the counter to the scrambledWord .
     *
     * -->Your logic should ensure that the order of the letters in the words is preserved.
     *
     * @param view
     * @returns true
     */
    public boolean onStartGame(View view) {
        // The layouts in which both the words are going to be placed.
        ViewGroup word1linearlayout=(ViewGroup) findViewById(R.id.word1);
        ViewGroup word2linearlayout=(ViewGroup) findViewById(R.id.word2);

        // Takes care of clearing/removing previous games's tiles
        word1linearlayout.removeAllViews();
        word2linearlayout.removeAllViews();

        // Clears the stack and assigns a new word / letter
        stackedLayout.clear();
        userWord1="";
        userWord2="";


        int i=0,j=0,k=0;
        word1=words.get(random.nextInt(words.size()));
        word2=words.get(random.nextInt(words.size()));


        // Ensures same word is not chosen twice (would make the game easy).
        if(word1.equalsIgnoreCase(word2))
        {
            while(!word1.equalsIgnoreCase(word2))
            {
                word2=words.get(random.nextInt(words.size()));
            }
        }


        // Logic for scrambling the word . Generate a random number between 0-10 and check if its even or odd.
        // If even choose word1 else word2.
        Log.i("tag1",word1+" "+word2);
        String scrambledWord="";
        while(i<word1.length()&&j<word2.length())
        {
            if(random.nextInt(10)%2==0)
            {
                scrambledWord+=word1.charAt(i)+"";
                i++;
            }
            else
            {
                scrambledWord+=word2.charAt(j)+"";
                j++;
            }
        }


        // Two while loops in case one string gets exhausted before the other. We simply append the remaining characters
        // to the scrambledWord.
        while(i<word1.length())
        {
            scrambledWord+=word1.substring(i)+"";
            i+=word1.substring(i).length();
        }
        while(j<word2.length())
        {
            scrambledWord+=word2.substring(j)+"";
            j+=word2.substring(j).length();
        }
        Log.i("tag",scrambledWord);

        TextView messageBox = (TextView) findViewById(R.id.message_box);
        messageBox.setText(scrambledWord);

        // Pushes the words in reverse order into a LetterTile stack .
        for(int index=scrambledWord.length()-1;index>=0;index--)
        {
            stackedLayout.push(new LetterTile(this,scrambledWord.charAt(index)));
        }

        return true;
    }


    /**
     * My code below.
     * -->To make the game easier undo operation is implemented where a user can undo his/her moves until he is confident about
     *    the word.
     * -->Undo operation basically pops the tile and places it back into the stackedLayout, i.e undoes the most recent move
     *    of the player.
     * @param view
     * @return boolean
     */
    public boolean onUndo(View view) {
        // if (!empty()) avoids crashing of the app when the stack is empty,i.e when you have undoed all your previous moves
        if(!placedTiles.empty()) {
            LetterTile poppedTile = placedTiles.pop();
            poppedTile.moveToViewGroup(stackedLayout);
        }

        return true;
    }

    /**
     * My code below.
     * Checks the winning condition if the stackedLayout becomes empty.
     */
    public void checkwin()
    {
        TextView messageBox=(TextView)findViewById(R.id.message_box);
        if(word1.equalsIgnoreCase(userWord1)&&word2.equalsIgnoreCase(userWord2))
        {
            messageBox.setText("YOU WIN" +" "+ word1+" "+word2);
            Toast.makeText(this, "YOU WIN!!!", Toast.LENGTH_LONG).show();
        }
        else
        {
            messageBox.setText("TRY AGAIN!");
        }
    }
}
