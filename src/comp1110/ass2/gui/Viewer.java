package comp1110.ass2.gui;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

/* Note :  All codes without author are done by group. */

/**
 * A very simple viewer for piece placements in the steps game.
 *
 * NOTE: This class is separate from your main game class.  This
 * class does not play a game, it just illustrates various piece
 * placements.
 */
public class Viewer extends Application {

    /* board layout */
    private static final int SQUARE_SIZE = 60;
    private static final int PIECE_IMAGE_SIZE = (int) ((3*SQUARE_SIZE)*1.33);
    private static final int VIEWER_WIDTH = 750;
    private static final int VIEWER_HEIGHT = 500;

    private static final String URI_BASE = "assets/";

    private final Group root = new Group();
    private final Group controls = new Group();
    private Group placements = new Group();
    private final Group pegs = new Group();
    TextField textField;


    /**
     * Draw a placement in the window, removing any previously drawn one
     *
     * @param placement  A valid placement string

     */
    //completed by Jiawen
    void makePlacement(String placement) {
        // FIXME Task 4: implement the simple placement viewe
        placements.getChildren().clear();
        placements.toFront();
        if (placement != null) {
            ImageView[] imgvs = new ImageView[placement.length()/3];
            int i = 0;
            while (placement.length() != 0) {
                String piece = placement.substring(0,3);
                String id = piece.substring(0,1) + (char)((piece.charAt(1) - 'A') / 4 * 4 + 'A');
                Image pieceImage = new Image(Viewer.class.getResource(URI_BASE + id + ".png").toString());
                imgvs[i] = new ImageView();
                imgvs[i].setImage(pieceImage);
                imgvs[i].setScaleX(0.58);
                imgvs[i].setScaleY(0.58);
                imgvs[i].setRotate(90*((piece.charAt(1)-'A')%4));
                imgvs[i].setTranslateX(getTransX(piece.charAt(2)));
                imgvs[i].setTranslateY(getTransY(piece.charAt(2)));
                placements.getChildren().add(imgvs[i]);
                placement = placement.substring(3,placement.length());
            }
        }
    }

    // 2 methods below by Jiawen
    double getTransY(char pos) {
        if (pos >= 'A' && pos <= 'Y') {
            return (pos - 'A') / 10 * 40 + 14;
        } else if (pos >= 'a' && pos <= 'y') {
            return ((pos - 'a' + 5) / 10 + 2) * 40 + 14;
        }
        return 0;
    }

    double getTransX(char pos) {
        if (pos >= 'A' && pos <= 'Y') {
            return (pos - 'A') % 10 * 40 + 61;
        } else if (pos >= 'a' && pos <= 'y') {
            return (pos - 'a' + 5) % 10 * 40 + 61;
        }
        return 0;
    }




    /**
     * Create a basic text field for input and a refresh button.
     */
    private void makeControls() {
        Label label1 = new Label("Placement:");
        textField = new TextField ();
        textField.setPrefWidth(300);
        Button button = new Button("Refresh");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                makePlacement(textField.getText());
                textField.clear();
            }
        });
        HBox hb = new HBox();
        hb.getChildren().addAll(label1, textField, button);
        hb.setSpacing(10);
        hb.setLayoutX(130);
        hb.setLayoutY(VIEWER_HEIGHT - 50);
        controls.getChildren().add(hb);
    }


    // 3 methods below by Jiawen
    void drawPeg(int i, int j) {
        if ((i+j)%2==0) {
            Circle peg = new Circle();
            peg.setCenterX(40*j+200);
            peg.setCenterY(40*i+150);
            peg.setRadius(18);
            peg.setFill(Color.GRAY);
            pegs.getChildren().add(peg);
        }
    }

    void drawPegsRow(int i) {
        int j = 0;
        while (j < 10) {
            drawPeg(i,j);
            j++;
        }
    }

    void drawPegs() {
        int i = 0;
        while (i < 5) {
            drawPegsRow(i);
            i++;
        }
    }



    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("StepsGame Viewer");
        Scene scene = new Scene(root, VIEWER_WIDTH, VIEWER_HEIGHT);

        root.getChildren().add(controls);

        makeControls();
        root.getChildren().add(placements);

        drawPegs();
        root.getChildren().add(pegs);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
