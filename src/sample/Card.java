package sample;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.jmx.MXNodeAlgorithm;
import com.sun.javafx.jmx.MXNodeAlgorithmContext;
import com.sun.javafx.sg.prism.NGNode;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class Card extends Node {

    public final static int SPADES = 0,       // Codes for the 4 suits.
            HEARTS = 1,
            DIAMONDS = 2,
            CLUBS = 3;

    public final static int ACE = 1,          // Codes for the non-numeric cards.
            JACK = 11,        //   Cards 2 through 10 have their
            QUEEN = 12,       //   numerical values for their codes.
            KING = 13;

    public final static int BLACK = 0, RED = 1;

    private final int suit;   // The suit of this card, one of the constants
    //    SPADES, HEARTS, DIAMONDS, CLUBS.

    private final int color;  // The color of this card, RED or BLACK

    private final int value;  // The value of this card, from 1 to 13.

    private final int xPos;   // Hold x coordinate in GridPane

    private final int yPos;   // Hold y coordinate in GridPane

    private final ImageView image;

    private final String imageUrl;

    public Card(int theValue, int theSuit, int theColor, int x, int y, ImageView image, String imageUrl) {
        // Construct a card with the specified value and suit.
        // Value must be between 1 and 13.  Suit must be between
        // 0 and 3.  If the parameters are outside these ranges,
        // the constructed card object will be invalid.
        this.value = theValue;
        this.suit = theSuit;
        this.color = theColor;
        this.xPos = x;
        this.yPos = y;
        this.image = image;
        this.imageUrl = imageUrl;
    }

    // Return the int that codes for this card's suit.
    public int getSuit() { return suit; }
    // Return the int that codes for this card's value.
    public int getValue() { return value; }
    // Return the int that codes for this card's color.
    public int getColor() { return color; }
    // Return x coordinate in GridPane
    public int getXPos() { return this.xPos; }
    // Return y coordinate in GridPane
    public int getYPos() { return this.yPos; }
    // Return associated image for card
    public ImageView getImage() { return this.image; }
    public String getImageUrl() { return this.imageUrl; }

    public String getSuitAsString() {
        // Return a String representing the card's suit.
        // (If the card's suit is invalid, "??" is returned.)
        switch ( suit ) {
            case SPADES:   return "Spades";
            case HEARTS:   return "Hearts";
            case DIAMONDS: return "Diamonds";
            case CLUBS:    return "Clubs";
            default:       return "??";
        }
    }

    public String getColorAsString() {
        switch (color) {
            case BLACK: return "Black";
            case RED:   return "Red";
            default:    return "??";
        }
    }

    public String getValueAsString() {
        // Return a String representing the card's value.
        // If the card's value is invalid, "??" is returned.
        switch ( value ) {
            case 1:   return "Ace";
            case 2:   return "2";
            case 3:   return "3";
            case 4:   return "4";
            case 5:   return "5";
            case 6:   return "6";
            case 7:   return "7";
            case 8:   return "8";
            case 9:   return "9";
            case 10:  return "10";
            case 11:  return "Jack";
            case 12:  return "Queen";
            case 13:  return "King";
            default:  return "??";
        }
    }

    @Override
    protected NGNode impl_createPeer() {
        return null;
    }
    @Override
    public BaseBounds impl_computeGeomBounds(BaseBounds bounds, BaseTransform tx) {
        return null;
    }
    @Override
    protected boolean impl_computeContains(double localX, double localY) {
        return false;
    }
    @Override
    public Object impl_processMXNode(MXNodeAlgorithm alg, MXNodeAlgorithmContext ctx) {
        return null;
    }

    public String toString() {
        // Return a String representation of this card, such as
        // "10 of Hearts" or "Queen of Spades".
        return getValueAsString() + " of " + getSuitAsString();
    }


}
