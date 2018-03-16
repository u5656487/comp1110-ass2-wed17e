package comp1110.ass2.gui;

import comp1110.ass2.Grid;
import comp1110.ass2.StepsGame;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.*;


import static comp1110.ass2.StepsGame.dictionary1;
import static comp1110.ass2.StepsGame.getViablePiecePlacements;
import static comp1110.ass2.StepsGame.isPlacementSequenceValid;

/* Note :  All codes in this file are done by group. */

public class Board extends Application {
    private static final int BOARD_WIDTH = 933;
    private static final int BOARD_HEIGHT = 700;
    private static final int SQUARE_SIZE = 60;  //size of each peg/ring
    private static final int PIECE_IMAGE_SIZE = (int) ((3*SQUARE_SIZE)*1.33);   //size of each piece
    private static final int MARGIN_X = (BOARD_WIDTH - 4 * 2 * SQUARE_SIZE) / 2 - 180;    //for unplaced pieces
    private static final int MARGIN_Y = 200; //same as above
    private static final int TOP_LEFT_X = 230;
    private static final int TOP_LEFT_Y = 100;

    private static final String URI_BASE = "assets/";
    private static final String LOOP_URI = Board.class.getResource(URI_BASE + "13-graze-the-roof.wav").toString();
    private AudioClip loop;

    /* game variables */
    private boolean loopPlaying = false;
    private boolean loopForbidden = false;

    /* message on completion */
    private Text completionText = new Text("Well done!");
    private Text gameTitle = new Text("IQ Step");
    private Text wrongMessage = new Text("Wrong Step!");
    private Text operationInfo = new Text("press 'm' to stop or play music\npress '/' for hint\n\nwhen on board, scroll mouse to rotate pieces\nwhen off board, scroll mouse to rotate and flip pieces\n\nEnjoy your game~! haha");
    private Text steps = new Text("Step Count: " + Integer.toString(step));

    private static Group root = new Group();
    private static Group pegs = new Group();
    private static Group pieces = new Group();
    private static Group homePieces = new Group();
    private static Group startingPieces = new Group();
    private static Group controls = new Group();
    private static Group controlsHome = new Group();
    private static Group hint = new Group();
    private static Group background = new Group();
    private static Group operationHelp = new Group();

    /* the difficulty slider */
    private static final Slider difficulty = new Slider();

    private static Random rand = new Random();
    private static int indexOfGame = 0;
    private static int difficultyLevel = 1;

    /* the state of the pieces */
    private static String placement;
    private static int step;

    // FIXME Task 7: Implement a basic playable Steps Game in JavaFX that only allows pieces to be placed in valid places

    // FIXME Task 8: Implement starting placements

    // FIXME Task 10: Implement hints

    // FIXME Task 11: Generate interesting starting placements



    /**
     * An inner class that represents masks used in the game.
     * Each of these is a visual representaton of a piece.
     */
    class FXPiece extends ImageView {
        char piece;

        /**
         * Construct a particular playing piece
         * @param piece The letter representing the piece to be created.
         */
        FXPiece(char piece) {
            if (!(piece >= 'A' && piece <= 'H'))
                throw new IllegalArgumentException("Bad piece: \"" + piece + "\"");
            setImage(new Image(Board.class.getResource(URI_BASE + piece + "A.png").toString()));
            this.piece = piece;
            setFitHeight(PIECE_IMAGE_SIZE);
            setFitWidth(PIECE_IMAGE_SIZE);
        }
        /**
         * Construct a particular playing piece at a given orientation.
         * @param orientation A character describing the position of the piece
         */
        FXPiece(char piece, char orientation) {
            this(piece);
            if (!(orientation >= 'A' && orientation <= 'H'))
                throw new IllegalArgumentException("Bad orientation string: " + orientation);
            int o = (orientation - 'A') % 4;
            if ((orientation - 'A') / 4 == 1)
                setImage(new Image(Board.class.getResource(URI_BASE + piece + "E.png").toString()));
            setRotate(90 * o);
        }

        /**
         * Construct a particular playing piece at a particular place on the
         * board at a given orientation.
         * @param position A character describing the position of the piece
         */
        FXPiece(char piece, char orientation, char position) {
            this(piece,orientation);
            if (!((position >= 'A' && position <= 'Y') || (position >= 'a' && position <= 'y') || position == '.'))
                throw new IllegalArgumentException("Bad position string: " + position);
            if ((position >= 'A' && position <= 'Y') || (position >= 'a')) {
                int x = Grid.getX(position);
                int y = Grid.getY(position);
                setLayoutX(TOP_LEFT_X + ((y-2) * SQUARE_SIZE));
                setLayoutY(TOP_LEFT_Y + ((x-2) * SQUARE_SIZE));
            } else {
                int homeX = (piece - 'A') % 4 * 2 * SQUARE_SIZE + MARGIN_X;
                setLayoutX(homeX);
                int homeY = BOARD_HEIGHT - MARGIN_Y - (1 - (piece - 'A') / 4) * 2 * SQUARE_SIZE;
                setLayoutY(homeY);
                setScaleX(0.4);
                setScaleY(0.4);
            }
        }
    }
    /**
     * This class extends FXPiece with the capacity for it to be dragged and dropped,
     * and snap-to-grid.
     */
    class DraggableFXPiece extends FXPiece {
        int homeX, homeY;           // the position in the window where the piece should be when not on the board
        double mouseX, mouseY;      // the last known mouse positions (used when dragging)
        char flipstate = 'A';       // 'A' if not flipped, 'E' if flipped

        /**
         * Construct a draggable piece
         * @param piece The piece identifier ('A' - 'H')
         */
        DraggableFXPiece(char piece) {
            super(piece);
            homeX = (piece - 'A') % 4 * 2 * SQUARE_SIZE + MARGIN_X;
            setLayoutX(homeX);
            homeY = BOARD_HEIGHT - MARGIN_Y - (1 - (piece - 'A') / 4) * 2 * SQUARE_SIZE;
            setLayoutY(homeY);
            setScaleX(0.5);
            setScaleY(0.5);

            /* event handlers */
            setOnScroll(event -> {            // scroll to change orientation
                hideCompletion();
                if (atHome()) rotateAndFlip();
                else if (canRotate(1)) rotate(1);
                else if (canRotate(2)) rotate(2);
                else if (canRotate(3)) rotate(3);
                event.consume();
            });
            setOnMousePressed(event -> {      // mouse press indicates begin of drag
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
                setScaleX(1);
                setScaleY(1);
            });
            setOnMouseDragged(event -> {      // mouse is being dragged
                hideCompletion();
                toFront();
                double movementX = event.getSceneX() - mouseX;
                double movementY = event.getSceneY() - mouseY;
                setLayoutX(getLayoutX() + movementX);
                setLayoutY(getLayoutY() + movementY);
                mouseX = event.getSceneX();
                mouseY = event.getSceneY();
                event.consume();
            });
            setOnMouseReleased(event -> {     // drag is complete
                snapToGrid();
                step++;
                hideStep(false);
            });
        }

        /**
         * @return an array containing the nearest coordinate (row and column) of the current layout
         */
        private int[] getGridCoord() {
            return new int[] {(int)(Math.round((getLayoutX() - TOP_LEFT_X) / SQUARE_SIZE)+2), (int)(Math.round((getLayoutY() - TOP_LEFT_Y) / SQUARE_SIZE)+2)};
        }

        /**
         * Snap the piece to the nearest grid position (if it is over the grid)
         */
        private void snapToGrid() {
            if (onBoard()) {
                setLayoutX(TOP_LEFT_X + ((getGridCoord()[0])-2) * SQUARE_SIZE);
                setLayoutY(TOP_LEFT_Y + ((getGridCoord()[1])-2) * SQUARE_SIZE);
                for (int i = 0; i < placement.length(); i+=3) {
                    if (placement.charAt(i) == piece) {
                        placement = placement.substring(0, i) + placement.substring(i + 3);
                        break;
                    }
                }
                placement = placement + getPiecePlacement();
                showCompletion();
            } else {
                snapToHome();
            }
        }

        /**
         * @return true if the mask is on the board (judged by task 5)
         */
        private boolean onBoard() {
            String placemt = placement;
            String piece = getPiecePlacement();
            for (int i = 0; i < placemt.length(); i+=3) {
                if (placemt.charAt(i) == piece.charAt(0)) {
                    placemt = placemt.substring(0,i) + placemt.substring(i+3, placemt.length());
                    break;
                }
            }
            String newPlacement = placemt + piece;
            return isPlacementSequenceValid(newPlacement);
        }

        /**
         * get the current 3-letter piece placement
         * @return a string of current piece state
         */
        private String getPiecePlacement() {
            return Character.toString(piece) + Character.toString((char)(flipstate + getRotate() / 90))
                    + Character.toString((char)((10*getGridCoord()[1]+getGridCoord()[0]+'A')+(10*getGridCoord()[1]+getGridCoord()[0])/25*7));
        }

        /**
         * @return true if this piece can be rotated
         */
        private boolean canRotate(int time) {
            if (atHome()) return true;
            String newPiece = Character.toString(piece) + Character.toString((char)(flipstate + ((getRotate() + 90 * time) / 90 % 4)))
                    + Character.toString((char)((10*getGridCoord()[1]+getGridCoord()[0]+'A')+(10*getGridCoord()[1]+getGridCoord()[0])/25*7));
            String placemt = placement;
            for (int i = 0; i < placemt.length(); i+=3) {
                if (placemt.charAt(i) == newPiece.charAt(0)) {
                    placemt = placemt.substring(0,i) + placemt.substring(i+3, placemt.length());
                    break;
                }
            }
            String newPlacement = placemt + newPiece;
            return isPlacementSequenceValid(newPlacement);
        }

        /**
         * @return true if the piece is at home
         */
        private boolean atHome() {
            return getLayoutX() == homeX && getLayoutY() == homeY;
        }

        /**
         * Snap the mask to its home position (if it is not on the grid)
         */
        private void snapToHome() {
            setLayoutX(homeX);
            setLayoutY(homeY);
            setRotate(0);
            setScaleX(0.4);
            setScaleY(0.4);
            for (int i = 0; i < placement.length(); i+=3) {
                if (placement.charAt(i) == piece) {
                    placement = placement.substring(0, i) + placement.substring(i + 3);
                    break;
                }
            }
            flipstate = 'A';
            setImage(new Image(Board.class.getResource(URI_BASE + piece + "A.png").toString()));
        }


        /**
         * Rotate the piece by 90 degrees each time
         */
        private void rotate(int time) {
            setRotate((getRotate() + 90 * time) % 360);
            for (int i = 0; i < placement.length(); i+=3) {
                if (placement.charAt(i) == piece) {
                    placement = placement.substring(0, i) + placement.substring(i + 3);
                    break;
                }
            }
            placement = placement + getPiecePlacement();
            toFront();
            showCompletion();
        }

        private void rotateAndFlip() {
            char orientation = getPiecePlacement().charAt(1);
            if (orientation >= 'A' && orientation <= 'C') setRotate((getRotate() + 90) % 360);
            if (orientation == 'D') {
                setImage(new Image(Board.class.getResource(URI_BASE + piece + "E.png").toString()));
                setRotate(0);
                flipstate = 'E';
            }
            if (orientation >= 'E' && orientation <= 'G') setRotate((getRotate() + 90) % 360);
            if (orientation == 'H') {
                setImage(new Image(Board.class.getResource(URI_BASE + piece + "A.png").toString()));
                setRotate(0);
                flipstate = 'A';
            }
        }

        /** @return the mask placement represented as a string */
        public String toString() {
            return getPiecePlacement();
        }
    }

    /**
     * Set up event handlers for the main game
     *
     * @param scene  The Scene used by the game.
     */
    private void setUpHandlers(Scene scene) {
        /* create handlers for key press and release events */
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.M) {
                loopForbidden = !loopForbidden;
                playORStopSound();
                event.consume();
            } else if (event.getCode() == KeyCode.Q) {
                Platform.exit();
                event.consume();
            } else if (event.getCode() == KeyCode.SLASH) {
                makeHint();
                event.consume();
            }
        });
        scene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.SLASH) {
                hint.getChildren().clear();
                event.consume();
            }
        });
    }


    /**
     * Set up the sound loop (to play when the 'start' key is pressed)
     */
    private void setUpSoundLoop() {
        try {
            loop = new AudioClip(LOOP_URI);
            loop.setCycleCount(10000);
        } catch (Exception e) {
            System.err.println(":-( something bad happened ("+LOOP_URI+"): "+e);
        }
    }

    private void playSound() {
        if (!loopPlaying && !loopForbidden) {
            loop.play();
            loopPlaying = true;
        }
    }

    /**
     * Turn the sound loop on or off
     */
    private void playORStopSound() {
        if (loopPlaying)
            loop.stop();
        else
            loop.play();
        loopPlaying = !loopPlaying;
    }

    /**
     * The three methods below are used for drawing pegs.
     */
    private void drawPeg(int i, int j) {
        if ((i+j)%2==0) {
            Circle peg = new Circle();
            peg.setCenterX(SQUARE_SIZE*j+TOP_LEFT_X);
            peg.setCenterY(SQUARE_SIZE*i+TOP_LEFT_Y);
            peg.setRadius(SQUARE_SIZE/2.5);
            peg.setFill(Color.GRAY);
            peg.setOpacity(0.5);
            pegs.getChildren().add(peg);
        }
    }

    private void drawPegsRow(int i) {
        int j = 0;
        while (j < 10) {
            drawPeg(i,j);
            j++;
        }
    }

    private void drawPegs() {
        pegs.getChildren().clear();
        int i = 0;
        while (i < 5) {
            drawPegsRow(i);
            i++;
        }
    }

    /**
     * draw a light version of fixed pieces at home
     */
    private void drawLightHomePieces() {
        String all = "ABCDEFGH";
        homePieces.getChildren().clear();
        for (int i = 0; i < all.length(); i++)
            homePieces.getChildren().add(new FXPiece(all.charAt(i),'A','.'));
        homePieces.setOpacity(0.2);
        homePieces.toBack();
    }

    /**
     * make the pieces that can be dragged
     */


    private void drawDraggedPieces() {
        pieces.getChildren().clear();
        Set<Character> startingPieces = new HashSet<>();
        for (int i = 0; i < placement.length(); i+=3)
            startingPieces.add(placement.charAt(i));
        for (int i = 'A'; i <= 'H'; i++)
            if (!startingPieces.contains((char)i)) pieces.getChildren().add(new DraggableFXPiece((char)i));
        pieces.toFront();
    }

    /**
     * implement the starting placements
     */
    String start;
    private void drawStartingPieces() {
        Random r = new Random();
        int ir = r.nextInt(16);
        start = dictionary1[ir];
        placement = start.substring(0,(24-3*difficultyLevel));
        startingPieces.getChildren().clear();
        for (int i = 0; i < placement.length(); i += 3) {
            startingPieces.getChildren().add(new FXPiece(placement.charAt(i), placement.charAt(i + 1), placement.charAt(i + 2)));
        }
    }

    /**
     * Put all of the masks back in their home position
     */
    private void resetPieces() {
        pieces.toFront();
        for (Node n : pieces.getChildren()) {
            ((DraggableFXPiece) n).snapToHome();
        }
    }

    /**
     * Create the controls that allow the game to be restarted
     */
    private void makeControls() {
        ImageView start = new ImageView();
        start.setImage(new Image(Board.class.getResource(URI_BASE + "start.png").toString()));
        start.setLayoutX(BOARD_WIDTH/2 + 150);
        start.setLayoutY(BOARD_HEIGHT - 350);
        start.setScaleX(0.1);
        start.setScaleY(0.1);
        start.setOpacity(0.5);
        start.setOnMouseClicked(event -> newGame());
        controls.getChildren().add(start);
        difficulty.setMin(1);
        difficulty.setMax(5);
        difficulty.setValue(0);
        difficulty.setShowTickLabels(true);
        difficulty.setShowTickMarks(true);
        difficulty.setMajorTickUnit(1);
        difficulty.setMinorTickCount(0);
        difficulty.setSnapToTicks(true);
        difficulty.setLayoutX(BOARD_WIDTH - 260);
        difficulty.setLayoutY(BOARD_HEIGHT - 100);
        controls.getChildren().add(difficulty);
        final Label difficultyCaption = new Label("Difficulty:");
        difficultyCaption.setTextFill(Color.GREY);
        difficultyCaption.setLayoutX(BOARD_WIDTH - 330);
        difficultyCaption.setLayoutY(BOARD_HEIGHT - 100);
        controls.getChildren().add(difficultyCaption);

        //back arrow
        ImageView back = new ImageView();
        back.setImage(new Image((Board.class.getResource(URI_BASE + "backArrow.png").toString())));
        back.setScaleX(0.2);
        back.setScaleY(0.2);
        back.setLayoutX(-50);
        back.setLayoutY(-50);
        back.setOpacity(0.5);
        back.setOnMouseClicked(event -> {
            hideControls(true);
            hideGameTitle(true);
            hideStep(true);
            pieces.getChildren().clear();
            startingPieces.getChildren().clear();
            homePieces.getChildren().clear();
            pegs.getChildren().clear();
            hideHomePageControls(false);
            hideCompletion();
        });
        controls.getChildren().add(back);

        //operation help
        ImageView help = new ImageView();
        help.setImage(new Image(Board.class.getResource(URI_BASE + "help.png").toString()));
        help.setScaleX(0.3);
        help.setScaleY(0.3);
        help.setLayoutX(BOARD_WIDTH -180);
        help.setLayoutY(-30);
        help.setOpacity(0.5);
        help.setOnMousePressed(event -> hideHelp(false));
        help.setOnMouseReleased(event -> hideHelp(true));
        help.toFront();
        controls.getChildren().add(help);
    }

    /**
     * hide or show the controls (restart and difficulty level)
     */
    private void hideControls(boolean hide) {
        if (hide)
            controls.setOpacity(0);
        else
            controls.setOpacity(1);
    }

    /**
     * create the controls at home page
     */
    private void makeHomePageControls() {
        controlsHome.getChildren().clear();

        ImageView playButton = new ImageView();
        playButton.setImage(new Image(Board.class.getResource(URI_BASE + "play.png").toString()));
        playButton.setLayoutX(-340);
        playButton.setLayoutY(-450);
        playButton.setScaleX(0.1);
        playButton.setScaleY(0.1);
        playButton.setOnMouseClicked(event -> {
            newGame();
            playSound();
        });
        controlsHome.getChildren().add(playButton);

        ImageView title = new ImageView();
        title.setImage(new Image(Board.class.getResource(URI_BASE + "title.jpg").toString()));
        title.setLayoutX(370);
        title.setLayoutY(100);
        title.setScaleX(1.5);
        title.setScaleY(1.5);
        controlsHome.getChildren().add(title);
    }

    /**
     * hide or show home page
     */
    private void hideHomePageControls(boolean hide) {
        if (hide)
            controlsHome.setOpacity(0);
        else
            controlsHome.setOpacity(1);
    }

    /**
     * Create the message to be displayed when the player completes the puzzle.
     */
    private void makeCompletion() {
        completionText.setFill(Color.DARKSLATEGRAY);
        completionText.setCache(true);
        completionText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD,80));
        completionText.setLayoutX(BOARD_WIDTH/2-100);
        completionText.setLayoutY(235);
        completionText.setTextAlignment(TextAlignment.CENTER);
    }

    private boolean testComplete() {
        return placement.length() == 24;
    }

    /**
     * Show the completion message
     */
    private void showCompletion() {
        if (testComplete()) {
            completionText.toFront();
            completionText.setOpacity(1);
            completionText.setLayoutX(BOARD_WIDTH/2-200);
            completionText.setLayoutY(235);
            pieces.setOpacity(0.4);
            startingPieces.setOpacity(0.4);
            pegs.setOpacity(0.1);
        }
    }

    /**
     * Hide the completion message
     */
    private void hideCompletion() {
        completionText.toBack();
        completionText.setOpacity(0);
        pieces.setOpacity(1);
        startingPieces.setOpacity(1);
        pegs.setOpacity(0.5);
    }

    /**
     *  get the next step (implement hint)
     */
    private void makeHint() {
        hint.getChildren().clear();
        String solution = start;
        Set<String> viable = getViablePiecePlacements(placement,solution);
        if (placement.length() == 24) return;
        if (viable.size() != 0) {
            String next = new ArrayList<>(viable).get(0);
            hint.getChildren().add(new FXPiece(next.charAt(0),next.charAt(1),next.charAt(2)));
            hint.setOpacity(1);
            hint.toFront();
        } else {
            String wrong = placement.substring(placement.length()-3, placement.length());
            FXPiece wrongPiece = new FXPiece(wrong.charAt(0),wrong.charAt(1),wrong.charAt(2));
            ColorAdjust blackout = new ColorAdjust();
            blackout.setBrightness(-1.0);
            wrongPiece.setEffect(blackout);
            makeWrongMessage();
            hint.getChildren().add(wrongMessage);
            hint.getChildren().add(wrongPiece);
            hint.setOpacity(1);
            hint.toFront();
        }
    }

    /**
     * show wrong message when we can't get a next step
     */
    private void makeWrongMessage() {
        wrongMessage.setFill(Color.RED);
        wrongMessage.setCache(true);
        wrongMessage.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD,40));
        wrongMessage.setLayoutX(BOARD_WIDTH/2-150);
        wrongMessage.setLayoutY(430);
        wrongMessage.setTextAlignment(TextAlignment.CENTER);
    }

    /**
     * game title when playing the game
     */
    private void makeGameTitle() {
        DropShadow ds = new DropShadow();
        ds.setOffsetY(4.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
        gameTitle.setFill(Color.GREY);
        gameTitle.setEffect(ds);
        gameTitle.setCache(true);
        gameTitle.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD,40));
        gameTitle.setLayoutX(BOARD_WIDTH-270);
        gameTitle.setLayoutY(BOARD_HEIGHT-170);
        gameTitle.setTextAlignment(TextAlignment.CENTER);
    }

    private void hideGameTitle(boolean hide) {
        if (hide)
            gameTitle.setOpacity(0);
        else
            gameTitle.setOpacity(1);
    }

    private void makeHelp() {
        operationHelp.getChildren().clear();

        operationInfo.setTextAlignment(TextAlignment.CENTER);
        operationInfo.setFont(Font.font("Arial",20));
        operationInfo.setCache(true);
        operationInfo.setLayoutX(BOARD_WIDTH/2-240);
        operationInfo.setLayoutY(BOARD_HEIGHT/2-150);
        operationInfo.toFront();
        operationInfo.setFill(Color.BROWN);

        Rectangle box = new Rectangle();
        box.setHeight(220);
        box.setWidth(560);
        box.setLayoutX(BOARD_WIDTH/2-255);
        box.setLayoutY(BOARD_HEIGHT/2-200);
        box.toBack();
        box.setFill(Color.WHITE);
        box.setOpacity(0.9);

        operationHelp.getChildren().add(box);
        operationHelp.getChildren().add(operationInfo);
        operationHelp.toFront();
    }

    private void hideHelp(boolean hide) {
        if (hide)
            operationHelp.getChildren().clear();
        else
            makeHelp();
    }

    private void makeBackgroundImage() {
        ImageView backGround = new ImageView();
        backGround.setImage(new Image(Board.class.getResource(URI_BASE + "background.jpg").toString()));
        backGround.setLayoutX(-620);
        backGround.setLayoutY(-205);
        backGround.setScaleX(0.6);
        backGround.setScaleY(0.6);
        background.getChildren().add(backGround);
        background.toBack();
        background.setOpacity(0.2);
    }

    private void makeStep() {
        steps.setFill(Color.BROWN);
        steps.setLayoutX(80);
        steps.setLayoutY(BOARD_HEIGHT/2+70);
        steps.setCache(true);
        steps.setTextAlignment(TextAlignment.CENTER);
        steps.setFont(Font.font("Arial",FontWeight.EXTRA_BOLD,20));
        steps.toFront();
    }

    private void hideStep(boolean hide) {
        if (hide)
            steps.setOpacity(0);
        else {
            steps.setText("Step Count: " + Integer.toString(step));
            steps.setOpacity(1);
        }
    }

    /**
     * Start a new game, resetting everything as necessary
     */
    private void newGame() {
        try {
            hideControls(false);
            difficultyLevel = (int) difficulty.getValue();
            if (difficultyLevel == 5) indexOfGame = rand.nextInt(1);
            else indexOfGame = rand.nextInt(15);
            step = 0;
            hideGameTitle(false);
            hideCompletion();
            hideHomePageControls(true);
            drawPegs();
            drawStartingPieces();
            drawLightHomePieces();
            drawDraggedPieces();
            hideCompletion();
            showCompletion();
            makeStep();
            hideStep(false);
        } catch (IllegalArgumentException e) {
            System.err.println("Uh oh. "+ e);
            e.printStackTrace();
            Platform.exit();
        }
        resetPieces();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("IQ Steps");
        Scene scene = new Scene(root, BOARD_WIDTH, BOARD_HEIGHT);
        makeBackgroundImage();
        makeControls();
        hideControls(true);
        makeHomePageControls();
        makeGameTitle();
        hideGameTitle(true);
        makeCompletion();
        hideCompletion();
        root.getChildren().add(background);
        root.getChildren().add(completionText);
        root.getChildren().add(gameTitle);
        root.getChildren().add(pegs);
        root.getChildren().add(controls);
        root.getChildren().add(controlsHome);
        root.getChildren().add(homePieces);
        root.getChildren().add(pieces);
        root.getChildren().add(startingPieces);
        root.getChildren().add(hint);
        root.getChildren().add(operationHelp);
        root.getChildren().add(steps);

        setUpHandlers(scene);
        setUpSoundLoop();

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
