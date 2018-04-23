package hwk2.cis350.upenn.edu.memorygame;


import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;


public class gameActivity extends AppCompatActivity {

    //list of cards to be initialized randomly (size based on difficulty)
    List<Card> cardList;


    //helpful state variables
    boolean clickableState = true;
    boolean onSecond = false;
    Card firstOfMatch = null;
    int numMatches = 0;
    int numMoves = 0;
    TextView numMatchesText;
    TextView numMovesText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String difficulty = getIntent().getStringExtra("DIFFICULTY");

        //dummy
        int numCards = 0;

        //check difficulty from intent and initialize View and numCards
        if (difficulty.equals("Easy")) {
            setContentView(R.layout.easy_grid);
            numCards = 6;
        } else if (difficulty.equals("Medium")) {
            setContentView(R.layout.medium_grid);
            numCards = 12;
        } else if (difficulty.equals("Hard")) {
            setContentView(R.layout.hard_grid);
            numCards = 24;
        }

        //set up deck of cards
        setUpDeck(numCards);

        //initialize the counters
        numMatchesText = (TextView) findViewById(R.id.matchCounterText);
        numMovesText = (TextView) findViewById(R.id.moveCounterText);
    }


    //helper method to be used during onCreate to set up deck
    //and initialize all the cards on the screen
    private void setUpDeck(int numCards) {
        //pic files method found at bottom of file
        //picture Files -- hard coded at end of file
        List<Integer> picFiles = initializePicFiles();


        //shuffle pictures for random draw of playing cards
        Collections.shuffle(picFiles);

        //initialize card list with the imagebuttons attached as a field
        cardList = new ArrayList<Card>();
        //get random numbers
        int[] buttonIDs = getShuffled(numCards);
        for (int i = 0; i < numCards/2; i++) {
            String buttonID = "card" + String.valueOf(buttonIDs[2*i]) + "Button";
            int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            cardList.add(new Card(2*i, (ImageButton)
                    findViewById(resID), picFiles.get(i)));
            buttonID = "card" + buttonIDs[(2*i+1)] + "Button";
            resID = getResources().getIdentifier(buttonID, "id", getPackageName());
            cardList.add(new Card((2*i)+1, (ImageButton)
                    findViewById(resID), picFiles.get(i)));
        }
        //shuffle card list
        //Note: don't think this method is doing anything helpful
        Collections.shuffle(cardList);


    }

    //helper method to get a shuffled array of numbers on [0,upTo)
    private int[] getShuffled(int upTo) {
        List<Integer> shuffled = new ArrayList<Integer>();
        int [] result = new int[upTo];
        for (int i = 0; i < upTo; i++) {
            shuffled.add(i);
        }
        Collections.shuffle(shuffled);
        for (int i = 0; i < upTo; i++) {
            result[i] = shuffled.get(i);
        }
        return result;
    }


    //click method for EVERY card's imagebutton
    //happens when any card is clicked no matter what state
    public void onCardClick(View view) {

        //no clicks allowed in this state
        if (!clickableState) return;

        if (!onSecond) {
            firstOfMatch = null;
        }
        //find the card object given the imagebutton view
        //there might be a better way to do this?
        ImageButton button = (ImageButton) view;
        Card clicked = null;
        for (Card c: cardList) {
            if (c.ib.equals(button)) {
                clicked = c;
            }
        }

        //make a copy of the clicked card as final so we can
        //use it in anonmyous inner classes
        final Card currentClick = clicked;

        //if the click is meaningless then return
        if (clicked.matched || clicked.equals(firstOfMatch)) return;

        //flip over the card
        if (!clicked.flipped) {
            clicked.flip();
        }

        //first half of the move, set the first part of match and return
        if (!onSecond) {
            firstOfMatch = clicked;
            onSecond = true;
            return;
        }

        //we are on the second half of the move
        //check if it is a pair and run corresponding method
        if (firstOfMatch.isPair(clicked)) {
            makeAMatch(currentClick, firstOfMatch, view);
        } else {

            makeAMismatch(clicked, firstOfMatch, view);

        }
    }


    //helper method for game logic and UI for making a match
    private void makeAMatch(Card clicked, Card firstOfMatch, View view) {

        final Card currentClick = clicked;
        final Card first = firstOfMatch;

        //highlight green
        firstOfMatch.makeGreen();
        clicked.makeGreen();

        //pause clickable state until runnable is finished
        clickableState = false;

        //stuff that should happen when a match is found
        //make it happen one second after green highlight
        view.postDelayed(new Runnable() {
            public void run() {

                first.eliminate();
                currentClick.eliminate();
                numMatches++;
                numMatchesText.setText("Matches Left: " + (cardList.size()/2 - numMatches));
                onSecond = false;
                if (numMatches==cardList.size()/2) {
                    endGame();
                }

                numMovesText.setText("Moves Made: " + ++numMoves);
                clickableState = true;
            }
        }, 1000);
    }

    //helper method for game logic and UI for making a mismatch
    private void makeAMismatch(Card clicked, Card firstOfMatch, View view) {

        final Card currentClick = clicked;
        final Card first = firstOfMatch;

        //incorrect match case
        clickableState = false;
        firstOfMatch.makeRed();
        clicked.makeRed();
        view.postDelayed(new Runnable() {
            public void run() {
                first.makeClear();
                currentClick.makeClear();
                first.flip();
                currentClick.flip();
                clickableState = true;
            }
        }, 1000);

        onSecond = false;
        numMovesText.setText("Moves Made: " + ++numMoves);

    }

    //helper method for game logic and UI for ending game
    //sends back to parent activity (main screen)
    private void endGame() {
        String message = "You Won! \n Great Job! \n Now Returning to Main Menu...";
        Toast.makeText(this.getApplicationContext(), message, Toast.LENGTH_LONG).show();
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 3000);

    }


    //Card class
    //each card class has access to its underlying imagebutton

    private class Card {
        int number;
        boolean flipped;
        ImageButton ib;
        int picID;
        boolean matched= false;

        public Card(int number, ImageButton ib, int picID) {
            this.number = number;
            flipped = false;
            this.ib = ib;
            this.picID = picID;
            this.ib.setImageResource(R.drawable.card_back);
            makeClear();
        }

        public boolean equals(Card that) {
            if (that == null) return false;
            return this.number==that.number;
        }

        public void flip() {
            if (flipped) {
                this.ib.setImageResource(R.drawable.card_back);
            } else {
                this.ib.setImageResource(this.picID);
            }
            flipped = !flipped;
        }

        public void eliminate() {
            matched = true;
            this.ib.setAlpha(.3f);
        }

        public boolean isPair(Card that) {
            if (that == null) return false;
            return this.picID==that.picID;
        }

        public void makeRed() {
            this.ib.setBackgroundColor(Color.RED);
        }

        public void makeClear() {
            this.ib.setBackgroundColor(Color.TRANSPARENT);
        }

        public void makeGreen() {
            this.ib.setBackgroundColor(Color.GREEN);
        }

    }

    //helper method to initialize drawable IDs for every picture file in folder
    //pictures stored in res/drawable/pi  for i between [1,52]
    private List<Integer> initializePicFiles() {
        List<Integer> picFiles = new ArrayList<Integer>();
        for (int i = 0; i < 52; i++) {
            picFiles.add(getResources().getIdentifier(getApplicationContext().getPackageName()
                    + ":drawable/p" + String.valueOf(i+1), null, null));
        }
        return picFiles;
    }
}