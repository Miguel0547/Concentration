package gui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Card;
import model.ConcentrationModel;
import model.Observer;

import java.util.*;

/**
 * The ConcentrationGUI application is the UI for Concentration.
 *
 * @author Miguel Reyes
 */
public class ConcentrationGUI extends Application
        implements Observer<ConcentrationModel, Object> {

    /**
     * the font size of the labels
     */
    private final static int LABEL_FONT_SIZE = 18;

    /**
     * pokeball images
     */
    private final Image pokeball = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/pokeball.png")));
    private final Image abra = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/abra.png")));
    private final Image bulbasaur = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/bulbasaur.png")));
    private final Image charmander = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/charmander.png")));
    private final Image jigglypuff = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/jigglypuff.png")));
    private final Image meowth = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/meowth.png")));
    private final Image pikachu = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/pikachu.png")));
    private final Image squirtle = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/squirtle.png")));
    private final Image venomoth = new Image(Objects.requireNonNull(getClass().getResourceAsStream("resources/venomoth.png")));


    /**
     * list of images. Used to map numbers 0-7 to each image respectively.
     */
    private final List<Image> pokemonNames = Arrays.asList(abra, bulbasaur, charmander, jigglypuff, meowth, pikachu, squirtle, venomoth);
    /**
     * The map used to map the integers 0-7(card types) to the images respectively.
     */
    private final HashMap<Integer, Image> map = new HashMap<>();
    /**
     * Stage used for the cheat window
     */
    private final Stage cheatStage = new Stage();
    /**
     * The buttons that are on the main GridPane the player plays on
     */
    private final ArrayList<PokemonButton> buttons = new ArrayList<>();
    /**
     * The buttons on the cheat window
     */
    private final ArrayList<PokemonButton> cheatButtons = new ArrayList<>();
    /**
     * The model for the view and controller.
     */
    private ConcentrationModel model;
    /**
     * GridPane used for the game
     */
    private GridPane gpane;
    /**
     * Gridpane used for the cheat window. All buttons facedup will go here.
     */
    private GridPane gcheatPane;
    /**
     * Label that is used to tell the player their current choice of move.
     */
    private Label topLabel;
    /**
     * Label that highlights how many moves have been made
     */
    private Label bottomLeftlabel;
    /**
     * Flowpane that organizes the reset, undo, and cheat buttons left to right
     */
    private FlowPane fpane;

    /**
     * main entry point launches the JavaFX GUI.
     *
     * @param args not used
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Functions maps our pokemon images to the card numbers 0-7
     */
    public void mapCardtoImage() {
        for (int i = 0; i < 8; i++) {
            this.map.put(i, pokemonNames.get(i));
        }
    }

    /**
     * This is called by the update function to update the grid the player is playing on as well as populate the cheat
     * grid.
     *
     * @param cards ArrayList of cards that we get from the model - can be a list of cards with all cards faced up(cheat)
     *              or the original list we get from initializing the model, or all cards face down(reset) etc
     * @param cheat - boolean that represents whether we are changing the buttons of the grid the player plays on
     *              or the buttons on the cheat grid to all faceup.
     */
    public void updateGrid(ArrayList<Card> cards, boolean cheat) {
        int i = 0;
        PokemonButton button;
        for (Card f : cards) {
            if (cheat) {
                button = this.cheatButtons.get(i);
            } else {
                button = this.buttons.get(i);
            }
            if (f.isFaceUp()) {
                button.setImage(this.map.get(f.getNumber()));
            } else {
                button.setImage(pokeball);
            }
            ++i;
        }
    }

    /**
     * Function that builds the main GridPane and the GridPane used for the cheat window. Both grids are filled
     * with buttons.
     */
    public void gridDisplay() {
        this.gpane = new GridPane();
        this.gcheatPane = new GridPane();
        int size = 4;
        int i = 0;
        for (int row = 0; row < size; ++row) {
            for (int col = 0; col < size; ++col) {
                // get the next type of pokemon and create a button for it
                PokemonButton button = new PokemonButton();
                PokemonButton cheatButton = new PokemonButton();
                //add buttons to their list
                this.buttons.add(button); //main buttons
                this.cheatButtons.add(cheatButton); //cheatwindow buttons
                int finalI = i;
                //if button on main grid selected then call the models selectCard function
                button.setOnAction(event -> this.model.selectCard(finalI));
                ++i;

                //Add buttons to both grids
                gcheatPane.add(cheatButton, col, row);
                gpane.add(button, col, row);
            }
        }
    }

    /**
     * Function creates the flow pain used on the main BorderPane with the buttons reset, undo, and cheat.
     */
    public void flowPaneDisplay() {
        this.fpane = new FlowPane();
        Button reset = new Button("Reset");
        Button undo = new Button("Undo");
        Button cheat = new Button("Cheat");
        fpane.getChildren().addAll(reset, undo, cheat);
        fpane.setAlignment(Pos.CENTER);

        reset.setOnAction(event -> this.model.reset());
        undo.setOnAction(event -> this.model.undo());
        cheat.setOnAction(event -> this.model.cheat());
    }

    /**
     * Function is called by the update function to update the top label as well as the label that records how many
     * moves have occurred.
     */
    public void updateAllLabelsDisplay() {
        this.bottomLeftlabel.setText(this.model.getMoveCount() + "Moves");
        switch (this.model.howManyCardsUp()) {
            case 0 -> this.topLabel.setText("Select the first card.");
            case 1 -> this.topLabel.setText("Select the second card.");
            case 2 -> this.topLabel.setText("No Match: Undo or select a card.");
        }
    }

    @Override
    public void init() {
        this.model = new ConcentrationModel();
        this.model.addObserver(this);
    }


    /**
     * Here we build our GUI - a borderpane that contains a label in its top area, a grid pane with 16 buttons in its
     * center area and a HBox that contains 3 buttons and a label.
     */
    @Override
    public void start(Stage stage) {
        mapCardtoImage();
        //Main border pane that holds the main GridPane(gpane), the top label(topLabel), and the bottom border pane
        BorderPane bmainPane = new BorderPane();
        //Main Grid
        gridDisplay();
        bmainPane.setCenter(this.gpane);
        //Top Label
        this.topLabel = new Label("Select the first card.");
        this.topLabel.setFont(new Font(LABEL_FONT_SIZE));
        bmainPane.setTop(this.topLabel);
        this.topLabel.setAlignment(Pos.CENTER_LEFT);
        //Bottom border pane - holds the flow pane that contains the reset, undo, cheat buttons, as well as the label
        //that keeps track of how many moves have been taken
        BorderPane bottomPane = new BorderPane();
        flowPaneDisplay();
        bottomPane.setCenter(this.fpane);
        this.bottomLeftlabel = new Label(this.model.getMoveCount() + "Moves");
        this.bottomLeftlabel.setFont(new Font(LABEL_FONT_SIZE));
        bottomPane.setRight(this.bottomLeftlabel);
        bmainPane.setBottom(bottomPane);
        //The border pane for the cheat window
        BorderPane bcheatPane = new BorderPane();
        bcheatPane.setCenter(this.gcheatPane);
        this.cheatStage.setScene(new Scene(bcheatPane));
        this.cheatStage.setTitle("Cheat Window");

        stage.setScene(new Scene(bmainPane));
        stage.setTitle("Concentration");
        stage.show();
    }

    /**
     * Our update function will call on all update helper functions to update the view
     * every time the models data changes. It does so by calling on the updateGrid function to update either the main
     * grid or the grid for the cheat window. It depends on if the caller is passing in a non-null argument for arg.
     * Also calling on the updateAllLabelsDisplay to update all labels in the program.
     */
    @Override
    public void update(ConcentrationModel o, Object arg) {
        // if arg is not null, it means the user wants to get the "cheat" board
        // with all cards face up
        if (arg != null) {
            updateGrid(this.model.getCheat(), true);
            this.cheatStage.show();
        } else {
            updateGrid(this.model.getCards(), false);
        }
        updateAllLabelsDisplay();
//         display a win if all cards are face up (not cheating)
        if (this.model.getCards().stream()
                .allMatch(face -> face.isFaceUp())) {
            this.topLabel.setText("YOU WIN!");
        }
    }

    /**
     * Create all Pokemon buttons with the default image of a pokeball
     */
    private class PokemonButton extends Button {
        public PokemonButton() {
            // set the graphics on the button with the pokeball image
            this.setGraphic(new ImageView(pokeball));
        }

        /**
         * Sets the buttons' graphics to the image passed in.
         *
         * @param pokemon Image object
         */
        public void setImage(Image pokemon) {
            this.setGraphic(new ImageView(pokemon));
        }

    }
}
