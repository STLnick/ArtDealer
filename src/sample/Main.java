package sample;

import java.util.*;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;

public class Main extends Application {
    public static String selectPattern = "";                // Pattern that the cpu will select
    public static int selectPatternIndex;                   // Holds pattern index to determine single or multi card matching
    public static PatternMatch[] currentPatternMatches;     // Holds single card pattern matches
    public static String playerGuess = "";                  // Player Guess
    public final static int EASY = 0, MEDIUM = 1, HARD = 2; // Difficulty level codes
    public static int isCorrect = 0;

    private Stage mainStage;
    private Card[] selectedCards = new Card[4]; // Holds cards selected/dealt
    private static int guessCounter;            // Holds guesses left to use by player
    private static int difficulty;              // Holds difficulty level for game
    private static boolean isTwoPlayer = false; // Holds if the game is played by one or two players

    public void start(Stage primaryStage) throws Exception{
        this.mainStage = primaryStage;
        primaryStage.setOnCloseRequest(confirmCloseEventHandler);

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting size for the pane
        gridPane.setMinSize(500, 200);

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        // GridPane styling
        gridPane.setStyle("-fx-background-color: GREEN; -fx-font-family: 'Cambria Math';");

        // String that displays the difficulty chosen
        String difficultyDisplay = "";

        // Current Card section
        HBox hbox = new HBox();
        hbox.setMinHeight(150);
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        hbox.setStyle("-fx-background-color: #336699;");
        // Add hbox to gridPane at the bottom left
        gridPane.add(hbox, 0, 7, 8,2);

        // Guess Counter box - added to display after difficulty chosen
        VBox guessVbox = new VBox();
        Text guessLabel = new Text("Guesses Remaining:");
        guessVbox.setStyle("-fx-font-size: 125%; -fx-alignment: center; -fx-border-color: black; -fx-border-width: 2px;" +
                "-fx-border-radius: 95%;");

        // Add guessVbox to gridPane
        gridPane.add(guessVbox, 9,7,3,1);

        // Data structure to hold images of all cards
        Map<String, Card> cards = new HashMap<String, Card>();
        // Fill data structure
        setupCards(cards, hbox);

        // Add cards to GridPane
        addCards(gridPane, cards);

        /* * * * Buttons * * * */

        // How to Play Button
        Alert htpMsg = new Alert(AlertType.INFORMATION);
        htpMsg.setContentText("This is the Art Dealer Game! Here's how you play: The game is played using the standard poker deck of 52 cards. The Student will be the “Art Seller” and the computer will be the “Art Dealer”.\n " +
                "In the 6-8 Difficulty Level a second Student will be able to play the role of “Art Dealer” making it a two-player game.\n " +
                "The Art Dealer wants to “buy paintings” represented by the cards in the deck.\n " +
                "The “Art Seller” wants to discover what kinds of “paintings” (cards) the “Art Dealer” is purchasing. There will be varying patterns depending on the Difficulty Level; these will be listed in the Difficulty Levels section.\n" +
                "\n" +
                "Each turn the “Art Seller” will lay out 4 cards from the deck. The “Art Dealer” will buy certain cards that depend on the pattern determined for this round of the game. The “Art Seller” must observe what, if any, cards are bought to eventually determine what the pattern is that the “Art Dealer” is buying.\n" +
                "You win by having the buyer buy ALL 4 selected cards and guessing the right pattern. You lose when you run out of guesses\n");
        Button howtoBtn = new Button("How to Play");
        howtoBtn.setOnAction(event -> htpMsg.show());

        // -- Close Application --
        Button closeButton = new Button("Close Application");
        closeButton.setOnAction(event ->
                primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST))
        );

        // All Guessing Options : < 6 = k-2; < 17 = 3-5; entire list = 6-8
        String [] choices1 = {"All Red", "All Black", "All Kings", "All Jacks", "All Queens", "All Aces",
                "All Even", "All Odd", "All Face", "Black Kings", "Black Queens", "Black Aces", "Black Jacks",
                "Red Kings", "Red Queens","Red Aces","Red Jacks", "Two of a Kind", "Three of a Kind", "Four of a Kind", "Two Pair"};

        // Stores all the possible cards that match a pattern
        // i.e. cards to buy when a specific pattern is chosen
        Map<String, PatternMatch[]> patternMatches = new HashMap<String, PatternMatch[]>();

        // Build Map to store cards that match each single card pattern
        setupPatternMatches(patternMatches, cards, choices1);

        // Drop down menu for guesses
        ChoiceBox guesses = new ChoiceBox();
        for (int i= 0; i < choices1.length; i++) {
            guesses.getItems().add(choices1[i]);
        }
        gridPane.add(guesses, 13, 6);

        Text guessChoiceLabel = new Text("Choose guess above");
        gridPane.add(guessChoiceLabel, 13, 7);

        // -- Random Deal --
        Button randomDealBtn = new Button("Random Deal");
        randomDealBtn.setOnAction(e -> {
            // Current number of selected cards
            int numSelected = hbox.getChildren().size();

            // ArrayList holding 0-51 to access imageViews indices
            // * Using list to ensure that each number selected is unique when being chosen
            ArrayList<Integer> list = new ArrayList<Integer>();
            for (int k = 0; k < 52; k++) {
                list.add(new Integer(k));
            }
            Collections.shuffle(list);  // Shuffle list of 0-51 to 'randomize' it

            // Switch cascades to add the appropriate number of
            // randomly selected cards depending on how many are
            // already selected
            switch (numSelected) {
                case 0:
                    selectedCards[0] = cards.get("card" + list.get(0));
                    ImageView cardImage1 = selectedCards[0].getImage();
                    hbox.getChildren().add(cardImage1);
                case 1:
                    selectedCards[1] = cards.get("card" + list.get(1));
                    ImageView cardImage2 = selectedCards[1].getImage();
                    hbox.getChildren().add(cardImage2);
                case 2:
                    selectedCards[2] = cards.get("card" + list.get(2));
                    ImageView cardImage3 = selectedCards[2].getImage();
                    hbox.getChildren().add(cardImage3);
                case 3:
                    selectedCards[3] = cards.get("card" + list.get(3));
                    ImageView cardImage4 = selectedCards[3].getImage();
                    hbox.getChildren().add(cardImage4);
                    break;
            }
            // Disable Random Deal button
            randomDealBtn.setDisable(true);
        });

        // -- Reset Deal --
        Button resetDealBtn = new Button("Reset Cards");
        resetDealBtn.setOnAction(e -> {
            resetEvent(hbox, gridPane, randomDealBtn);
        });

        // -- Confirm Deal --
        // create an alert
        Alert a = new Alert(Alert.AlertType.NONE);

        Button confirmDealBtn = new Button("Confirm Selection");
        confirmDealBtn.setOnAction(e -> {
            int numSelected = hbox.getChildren().size();

            // If there are 4 cards selected to deal
            if (numSelected == 4) {
                a.setAlertType(Alert.AlertType.CONFIRMATION);
                a.setContentText("Are you sure you want to sell these cards?");
                Optional<ButtonType> result = a.showAndWait();

                if(!result.isPresent()) {
                    // Alert is exited, no button has been pressed.
                }
                // Deal Confirmed
                else if(result.get() == ButtonType.OK) {
                    // OK button is pressed
                    // Grab currently selected/dealt cards
                    Card card1 = selectedCards[0];
                    Card card2 = selectedCards[1];
                    Card card3 = selectedCards[2];
                    Card card4 = selectedCards[3];

                    // If Two Players, setup and show card selection box to buy cards
                    if (getIsTwoPlayer()) {
                        /* * * * * * * * * * * * * * * * * * * * * */
                        /* Code for Player 2 to see and buy cards  */
                        /* * * * * * * * * * * * * * * * * * * * * */

                        // Toggle buttons to choose which cards to buy
                        ToggleButton toggle1 = new ToggleButton();
                        ToggleButton toggle2 = new ToggleButton();
                        ToggleButton toggle3 = new ToggleButton();
                        ToggleButton toggle4 = new ToggleButton();

                        // Add image of cards selected to toggle buttons
                        toggle1.setStyle("-fx-graphic: url('" + card1.getImageUrl() + "')");
                        toggle2.setStyle("-fx-graphic: url('" + card2.getImageUrl() + "')");
                        toggle3.setStyle("-fx-graphic: url('" + card3.getImageUrl() + "')");
                        toggle4.setStyle("-fx-graphic: url('" + card4.getImageUrl() + "')");
                        // Set size of each toggle button
                        toggle1.setMinSize(125, 150);
                        toggle1.setMaxSize(125, 150);
                        toggle2.setMinSize(125, 150);
                        toggle2.setMaxSize(125, 150);
                        toggle3.setMinSize(125, 150);
                        toggle3.setMaxSize(125, 150);
                        toggle4.setMinSize(125, 150);
                        toggle4.setMaxSize(125, 150);

                        // Setup new window to choose cards to buy / show bought cards
                        final Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(primaryStage);

                        HBox dialogHbox = new HBox(20);
                        dialogHbox.getChildren().addAll(toggle1, toggle2, toggle3, toggle4);

                        // Confirm Buy Button
                        Button confirmBuyBtn = new Button("Purchase Selected 'Paintings'");
                        confirmBuyBtn.setOnAction(e2 -> {
                            // Add text
                            Text message = new Text("These were the cards purchased!!");
                            message.setStyle("-fx-font-size: 150%; -fx-font-weight: bolder;");
                            dialogHbox.getChildren().add(message);
                            // Place bought card images in dialogHbox
                            if (toggle1.isSelected())
                                dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                            if (toggle2.isSelected())
                                dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                            if (toggle3.isSelected())
                                dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                            if (toggle4.isSelected())
                                dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));

                            // Remove toggle buttons
                            dialogHbox.getChildren().removeAll(toggle1, toggle2, toggle3, toggle4);
                            // Remove confirmBuyBtn
                            dialogHbox.getChildren().remove(confirmBuyBtn);
                            // Close stage after x amount of time
                            PauseTransition delay = new PauseTransition(Duration.seconds(5));
                            delay.setOnFinished(event -> dialog.close());
                            delay.play();
                            // Reset cards
                            resetEvent(hbox, gridPane, randomDealBtn);
                        });
                        // Add confirmBuyBtn to dialogHbox
                        dialogHbox.getChildren().add(confirmBuyBtn);

                        Scene dialogScene = new Scene(dialogHbox, 800, 200);
                        dialog.setScene(dialogScene);
                        dialog.show();
                    // If One Player, run computer logic
                    } else {
                        /* * * * * * * * * * * * * * * * * * * * */
                        /* Code for computer logic to buy cards  */
                        /* * * * * * * * * * * * * * * * * * * * */

                        // Setup new window to show bought cards by computer
                        final Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(primaryStage);
                        // To hold bought cards in display
                        HBox dialogHbox = new HBox(20);

                        // Add text
                        Text message = new Text("These were the cards purchased!!");
                        message.setStyle("-fx-font-size: 150%; -fx-font-weight: bolder;");
                        dialogHbox.getChildren().add(message);

                        /* * Logic to choose cards and place them in the dialogHbox * */
                        /* Single card matching */
                        if (selectPatternIndex < 17) {
                            // Loops through all possible pattern matches
                            for (int i = 0; i < currentPatternMatches.length; i++) {
                                // If current PatternMatch isn't null and a card matches in value, suit and color
                                // it is added to display and 'bought'
                                if(currentPatternMatches[i] != null) {
                                    if (currentPatternMatches[i].getValue() == card1.getValue()) {
                                        if (currentPatternMatches[i].getSuit() == card1.getSuit()) {
                                            if (currentPatternMatches[i].getColor() == card1.getColor())
                                                dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                        }
                                    }
                                    if (currentPatternMatches[i].getValue() == card2.getValue()) {
                                        if (currentPatternMatches[i].getSuit() == card2.getSuit()) {
                                            if (currentPatternMatches[i].getColor() == card2.getColor())
                                                dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                        }
                                    }
                                    if (currentPatternMatches[i].getValue() == card3.getValue()) {
                                        if (currentPatternMatches[i].getSuit() == card3.getSuit()) {
                                            if (currentPatternMatches[i].getColor() == card3.getColor())
                                                dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                        }
                                    }
                                    if (currentPatternMatches[i].getValue() == card4.getValue()) {
                                        if (currentPatternMatches[i].getSuit() == card4.getSuit()) {
                                            if (currentPatternMatches[i].getColor() == card4.getColor())
                                                dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                        }
                                    }
                                }
                            }
                        } else {
                            /* Multi card matching */

                            // Store values of selected cards in variables for easier access and comparison
                            int val1 = card1.getValue();
                            int val2 = card2.getValue();
                            int val3 = card3.getValue();
                            int val4 = card4.getValue();

                            switch (selectPattern) {
                                case "Two of a Kind":
                                    // Finds two cards with same value i.e. two of a kind/pair

                                    // Setup in else if so only one pair is selected from the bunch
                                    if (val1 == val2) {
                                        dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                    } else if (val1 == val3) {
                                        dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                    } else if (val1 == val4) {
                                        dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                    } else if (val2 == val3) {
                                        dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                    } else if (val2 == val4) {
                                        dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                    } else if (val3 == val4) {
                                        dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                    }
                                    break;
                                case "Four of a Kind":
                                    if (val1 == val2 && val1 == val3 && val1 == val4) {
                                        dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                    }
                                    break;
                                case "Three of a Kind":
                                    // Finds three cards with same value i.e. three of a kind

                                    // Setup in else if so only one trio is selected from the bunch
                                    if (val1 == val2 && val1 == val3) {
                                        dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                    } else if (val1 == val2 && val1 == val4) {
                                        dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                    } else if (val1 == val3 && val1 == val4) {
                                        dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                    } else if (val2 == val3 && val2 == val4) {
                                        dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                        dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                    }
                                    break;
                                case "Two Pair":
                                    // Verifies there are two pairs of different cards

                                    if (val1 == val2) {
                                        if (val3 == val4 && val1 != val3) {
                                            dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                            dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                            dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                            dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                        }
                                    } else if (val1 == val3) {
                                        if (val2 == val4 && val1 != val2) {
                                            dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                            dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                            dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                            dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                        }
                                    } else if (val1 == val4) {
                                        if (val2 == val3 && val1 != val2) {
                                            dialogHbox.getChildren().add(new ImageView(card1.getImageUrl()));
                                            dialogHbox.getChildren().add(new ImageView(card2.getImageUrl()));
                                            dialogHbox.getChildren().add(new ImageView(card3.getImageUrl()));
                                            dialogHbox.getChildren().add(new ImageView(card4.getImageUrl()));
                                        }
                                    }
                                    break;
                            }
                        }
                        Scene dialogScene = new Scene(dialogHbox, 800, 200);
                        dialog.setScene(dialogScene);
                        dialog.show();

                        // Close stage after x amount of time
                        PauseTransition delay = new PauseTransition(Duration.seconds(5));
                        delay.setOnFinished(event -> dialog.close());
                        delay.play();

                        // Reset cards
                        resetEvent(hbox, gridPane, randomDealBtn);
                    }
                }
                // Deal cancelled
                else if(result.get() == ButtonType.CANCEL) {
                    // Cancel button is pressed
                }
            // If there aren't 4 cards selected to deal
            } else {
                a.setAlertType(AlertType.INFORMATION);
                a.setContentText("MUST be 4 cards chosen to deal!");
                a.show();
            }
        });

        // So that a lose condition can be enforced
        Button guessBtn = new Button("Make a Guess");
        guessBtn.setOnAction(e -> guessEvent(selectPattern, playerGuess, guesses, guessVbox, guessBtn,randomDealBtn, confirmDealBtn, resetDealBtn));

        // Start Game button: stores randomly selected pattern for this game and enable "in-game" buttons
        guessBtn.setDisable(true);
        randomDealBtn.setDisable(true);
        confirmDealBtn.setDisable(true);
        resetDealBtn.setDisable(true);
        Button startGame = new Button("Start Game");
        startGame.setOnAction(event -> storePattern(choices1, patternMatches, guessBtn, randomDealBtn, confirmDealBtn, resetDealBtn, startGame));

        // Restart button and function
        Button restartGame = new Button("Restart Game");
        restartGame.setOnAction( event ->
        {
            primaryStage.close();
            Platform.runLater( () -> {
                try {
                    new Main().start( new Stage() );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } ); // Reset Game Manually

        Alert c = new Alert(AlertType.WARNING);
        c.setContentText("0 Guesses remaining! Game Lost!");
        // Add buttons to GridPane
        gridPane.add(randomDealBtn, 13, 0);
        gridPane.add(guessBtn, 13, 3);
        gridPane.add(closeButton, 13, 8);
        gridPane.add(confirmDealBtn, 13, 2);
        gridPane.add(resetDealBtn, 13, 1);
        gridPane.add(restartGame, 11, 9, 2, 1);
        gridPane.add(startGame, 13, 9);
        gridPane.add(howtoBtn, 10, 9);
        Scene scene = new Scene(gridPane);

        primaryStage.setTitle("Art Dealer Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        /* * * After UI loads and displays... * * */

        // Have user choose difficulty and set ui and counters appropriately
        difficultyDisplay = setupDifficulty();

        //Label for difficulty
        Text difficultyLabel = new Text("Difficulty: " + difficultyDisplay);
        // Add difficulty label to gridPane
        gridPane.add(difficultyLabel, 9, 6, 2, 1);

        // Set guesses remaining text and add to ui
        Text guessesRemainingLabel = new Text(String.valueOf(getGuessCounter()));
        guessVbox.getChildren().addAll(guessLabel, guessesRemainingLabel);
    }

    // Maps keys and images together for all 52 cards
    public void setupCards(Map<String, Card> cards, HBox hbox) {
        int i = 0;  // Column counter and value(add 1 for constructor)
        int j = 0;  // Row counter and suit

        // Creating Map of Card Objects
        for (int k = 0; k < 52; k++) {
            /*  If/else constructs Card objects Black or Red relative to the order the images are stored
                Black Cards are 0-12 & 39-51  ---- Red Cards are 13-38 */
            // Black Cards
            if (k < 13 || k > 38 ) {
                cards.put("card" + k, new Card(i + 1, j, Card.BLACK, i, j, new ImageView(new Image("https://liveexample.pearsoncmg.com/book/image/card/"
                        + (k + 1) + ".png")), "https://liveexample.pearsoncmg.com/book/image/card/"
                        + (k + 1) + ".png"));
            // Red Cards
            } else {
                cards.put("card" + k, new Card(i + 1, j, Card.RED, i, j, new ImageView(new Image("https://liveexample.pearsoncmg.com/book/image/card/"
                        + (k + 1) + ".png")), "https://liveexample.pearsoncmg.com/book/image/card/"
                        + (k + 1) + ".png"));
            }
            // Increment j every 13th card and reset i to create 4 equal rows in display
            if((i % 12) == 0 && i != 0) {
                j++;
                i = 0;
            } else {
                i++;
            }

            int finalK = k; // used inside cardEventHandler, must be final to do so

            // Adds click event handler to place card in hbox
            cards.get("card" + k).getImage().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                int numSelected = hbox.getChildren().size();
                Card curCard = cards.get("card" + finalK);

                if(numSelected < 4) {
                    hbox.getChildren().add(curCard.getImage());
                    selectedCards[numSelected] = curCard;
                }
                e.consume();
            });

        }

    }

    // Add all cards in gridPane
    public void addCards(GridPane gridPane, Map<String, Card> cards) {
        int j = 0;  // Row counter
        int i = 0;  // Column counter
        for(int x = 0; x < 52; x++) {   // x is used to access all the card views
            String key = "card" + x;
            gridPane.add(cards.get(key).getImage(), i, j);
            // Adding cards to grid with 13 in each Row
            if((i % 12) == 0 && i != 0) {
                j++;
                i = 0;
            } else {
                i++;
            }
        }
    }

    public void resetEvent(HBox hbox, GridPane gridPane, Button randomDealBtn) {
        int numSelected = hbox.getChildren().size();
        Card curCard;

        for(int i = 0; i < numSelected; i++) {
            curCard = selectedCards[i];
            gridPane.add(curCard.getImage(), curCard.getXPos(), curCard.getYPos());
        }

        // Clear Selected Cards array
        Arrays.fill(selectedCards, null);

        // Reset random deal button since all cards deselected
        randomDealBtn.setDisable(false);
    };

    // Display alert to confirm user wants to close app
    private EventHandler<WindowEvent> confirmCloseEventHandler = event -> {
        Alert closeConfirmation = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to exit?"
        );
        Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(
                ButtonType.OK
        );
        exitButton.setText("Exit");
        closeConfirmation.setHeaderText("Confirm Exit");
        closeConfirmation.initModality(Modality.APPLICATION_MODAL);
        closeConfirmation.initOwner(mainStage);

        Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
        if (!ButtonType.OK.equals(closeResponse.get())) {
            event.consume();
        }
    };

    private void storePattern(String[] choices1, Map<String, PatternMatch[]> patternMatches, Button guessBtn, Button randomDealBtn, Button confirmDealBtn, Button resetDealBtn, Button startGame) // Cpu selects pattern at random
    {
        // Limit Question based on difficulty
        int upperBound = -1; 
        if (difficulty == 0)
            upperBound = 6;
        if (difficulty == 1)
            upperBound = 19;
        if (difficulty == 2)
            upperBound = choices1.length;

        // Randomly selecting pattern from choices with random index
        Random rand = new Random();
        selectPatternIndex = rand.nextInt(upperBound);
        selectPattern = choices1[selectPatternIndex];
  
        // Enable Buttons to play game
        guessBtn.setDisable(false);
        randomDealBtn.setDisable(false);
        confirmDealBtn.setDisable(false);
        resetDealBtn.setDisable(false);
        // Disable Start Game Button to prevent mid-game pattern changes
        startGame.setDisable(true);
        // 17 is last index of EASY and MEDIUM, all of which only match single cards
        if (selectPatternIndex < 17) {
            currentPatternMatches = new PatternMatch[patternMatches.get(selectPattern).length];
            currentPatternMatches = patternMatches.get(selectPattern).clone();
            // If above 17 it's a multi card matching pattern
        }
    }

    public void guessEvent(String s1, String playerGuess, ChoiceBox<String> guesses, VBox guessVbox, Button guessBtn, Button randomDealBtn, Button confirmDealBtn, Button resetDealBtn)
    {
        selectPattern = s1;
        playerGuess = guesses.getValue();
      
        // Alert if the player has used all guesses
        Alert a = new Alert(AlertType.WARNING);
        a.setContentText("0 Guesses remaining! Game Lost! Please Close or Restart Game");
      
        if (playerGuess.equals(selectPattern))
        {
            isCorrect = 1;
        }
        else{
            isCorrect = 2;
          
            if (guessCounter > 0) {
                // Decrement global guess counter
                guessCounter--;
                // Remove old label
                guessVbox.getChildren().remove(1);
                // Create new label
                Text guessCounterLabel = new Text(String.valueOf(guessCounter));
                // Add label to guessVbox
                guessVbox.getChildren().add(1, guessCounterLabel);
            }
            if (guessCounter == 0) {
                // Show game lost alert if guesses are 0
                a.show();
                guessBtn.setDisable(true);
                randomDealBtn.setDisable(true);
                confirmDealBtn.setDisable(true);
                resetDealBtn.setDisable(true);
            }
        }
      
        // Visual aid for correct or wrong guess
        Alert b = new Alert(AlertType.NONE); // Create new Alert for guesses
        if (isCorrect == 1)
        {
            b.setAlertType(AlertType.INFORMATION);
            b.setContentText("Congratulations! Your guess was correct! Please Close or Restart!");
            b.show();
            guessBtn.setDisable(true);
            randomDealBtn.setDisable(true);
            confirmDealBtn.setDisable(true);
            resetDealBtn.setDisable(true);
        }
        else
        {
            b.setAlertType(AlertType.INFORMATION);
            b.setContentText("Your guess was wrong! Try Again!");
            b.show();
        }
    }

    public static String setupDifficulty() {
        String diffDisplay = "";    // String to be returned to display chosen difficulty

        // Create alert and text to show inside
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Difficulty Level");
        alert.setHeaderText("Choose the level of Difficulty you'd like to play");
        alert.setContentText("Choose your option. AFTER you've selected your difficulty" +
                " press the 'Start Game' button in the bottom right corner of the main" +
                " screen. This will select the pattern for you to figure out!");

        ButtonType buttonEasy = new ButtonType("Easy");
        ButtonType buttonMedium = new ButtonType("Medium");
        ButtonType buttonHard = new ButtonType("Hard");

        // Add Buttons to Alert
        alert.getButtonTypes().setAll(buttonEasy, buttonMedium, buttonHard);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonEasy){
            // ... user chose "Easy"
            setDifficulty(EASY);
        } else if (result.get() == buttonMedium) {
            // ... user chose "Medium"
            setDifficulty(MEDIUM);
        } else if (result.get() == buttonHard) {
            // ... user chose "Hard"
            setDifficulty(HARD);

            // Give user option of One or Two Players for Hard
            alert.setTitle("Number of Players");
            alert.setHeaderText("Choose how many Players will participate");
            alert.setContentText("Choose your option: One or Two Players. Remember to press" +
                    "'Start Game' after selecting!");

            // Buttons to select One or Two Players
            ButtonType buttonOnePlayer = new ButtonType("One Player");
            ButtonType buttonTwoPlayer = new ButtonType("Two Players");

            // Remove difficulty choice buttons
            alert.getButtonTypes().removeAll(buttonEasy, buttonMedium, buttonHard);

            // Add Number of Players choice buttons
            alert.getButtonTypes().setAll(buttonOnePlayer, buttonTwoPlayer);

            // Get result of button user pressed
            Optional<ButtonType> playerResult = alert.showAndWait();

            // Two Players if chosen
            if (playerResult.get() == buttonTwoPlayer) {
                setIsTwoPlayer(true);
            // One Player if chosen or box exited
            } else {
                setIsTwoPlayer(false);
            }
        }

        // Set number of guesses allowed and display for difficulty level
        switch(getDifficulty()) {
            case EASY:
                setGuessCounter(10);
                diffDisplay = "Easy";
                break;
            case MEDIUM:
                setGuessCounter(8);
                diffDisplay = "Medium";
                break;
            case HARD:
                setGuessCounter(6);
                diffDisplay = "Hard";
                break;
        }
        return diffDisplay;
    }

    public static void setupPatternMatches(Map<String, PatternMatch[]> pmMap, Map<String, Card> cards, String[] patterns) {
        // Loops through all patterns and sets the matching patterns in appropriate Map structure
        for (int i = 0; i < patterns.length; i++) {
            // Store the pattern to
            String pattern = patterns[i];
            matchSingleCardsToPattern(pattern, pmMap, cards);
        }

    }

    // Iterates through cards array to check which cards match the given pattern and store them in pmMap
    public static void matchSingleCardsToPattern(String pattern, Map<String, PatternMatch[]> pmMap, Map<String, Card> cards) {
        PatternMatch[] colorMatches = new PatternMatch[26]; // for All Red and All Black
        PatternMatch[] valueMatches = new PatternMatch[4]; // for Same Card value cases
        PatternMatch[] colorValueMatches = new PatternMatch[2]; // for Same Card and Color cases
        PatternMatch[] faceMatches = new PatternMatch[12]; // for Jack, Queen, Kings - face card cases
        PatternMatch[] evenOddMatches = new PatternMatch[20]; // for Even or Odd cases

        int arrayCounter = 0;

        switch(pattern) {
            case "All Red":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getSuit() == Card.RED) {
                        colorMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, colorMatches);       // Place pattern and matching array in Map object
                break;
            case "All Black":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getSuit() == Card.BLACK) {
                        colorMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, colorMatches);       // Place pattern and matching array in Map object
                break;
            case "All Kings":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.KING) {
                        valueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, valueMatches);       // Place pattern and matching array in Map object
                break;
            case "All Jacks":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.JACK) {
                        valueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, valueMatches);       // Place pattern and matching array in Map object
                break;
            case "All Queens":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.QUEEN) {
                        valueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, valueMatches);       // Place pattern and matching array in Map object
                break;
            case "All Aces":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.ACE) {
                        valueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, valueMatches);       // Place pattern and matching array in Map object
                break;
            case "All Even":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() >= 2 && curCard.getValue() <= 10 && curCard.getValue() % 2 == 0) {
                        evenOddMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, evenOddMatches);     // Place pattern and matching array in Map object
                break;
            case "All Odd":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() > 2 && curCard.getValue() < 10 && curCard.getValue() % 2 != 0) {
                        evenOddMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, evenOddMatches);     // Place pattern and matching array in Map object
                break;
            case "All Face":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() >= 11) {
                        faceMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, faceMatches);     // Place pattern and matching array in Map object
                break;
            case "Black Kings":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.KING && curCard.getColor() == Card.BLACK) {
                        colorValueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, colorValueMatches);      // Place pattern and matching array in Map object
                break;
            case "Black Queens":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.QUEEN && curCard.getColor() == Card.BLACK) {
                        colorValueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, colorValueMatches);      // Place pattern and matching array in Map object
                break;
            case "Black Aces":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.ACE && curCard.getColor() == Card.BLACK) {
                        colorValueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, colorValueMatches);      // Place pattern and matching array in Map object
                break;
            case "Black Jacks":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.JACK && curCard.getColor() == Card.BLACK) {
                        colorValueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, colorValueMatches);      // Place pattern and matching array in Map object
                break;
            case "Red Kings":for (int i = 0; i < cards.size(); i++) {
                Card curCard = cards.get("card" + i);
                if (curCard.getValue() == Card.KING && curCard.getColor() == Card.RED) {
                    colorValueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                    arrayCounter++;
                }
            }
                pmMap.put(pattern, colorValueMatches);      // Place pattern and matching array in Map object
                break;
            case "Red Queens":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.QUEEN && curCard.getColor() == Card.RED) {
                        colorValueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, colorValueMatches);      // Place pattern and matching array in Map object
                break;
            case "Red Aces":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.ACE && curCard.getColor() == Card.RED) {
                        colorValueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, colorValueMatches);      // Place pattern and matching array in Map object
                break;
            case "Red Jacks":
                for (int i = 0; i < cards.size(); i++) {
                    Card curCard = cards.get("card" + i);
                    if (curCard.getValue() == Card.JACK && curCard.getColor() == Card.RED) {
                        colorValueMatches[arrayCounter] = new PatternMatch(curCard.getValue(), curCard.getSuit(), curCard.getColor());
                        arrayCounter++;
                    }
                }
                pmMap.put(pattern, colorValueMatches);      // Place pattern and matching array in Map object
                break;
        }
    }

    public static int getGuessCounter() { return guessCounter; }
    public static void setGuessCounter(int guesses) { guessCounter = guesses; }

    public static int getDifficulty() { return difficulty; }
    public static void setDifficulty(int diff) { difficulty = diff; }

    public static boolean getIsTwoPlayer() { return isTwoPlayer; }
    public static void setIsTwoPlayer(boolean twoPlayer) { isTwoPlayer = twoPlayer; }

    public static void main(String[] args) {
        launch(args);
    }
}