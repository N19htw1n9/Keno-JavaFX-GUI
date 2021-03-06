import Components.MainMenuBar;
import Objects.DrawRandom;
import Objects.UserPick;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.stream.IntStream;

public class GameScreen {
    private Stage primaryStage;
    private Scene scene;
    private VBox content;
    private Text text;
    private UserPick pick;
    private int matches;
    private int currentMatch;
    private boolean matcheSet;
    private Button quickPickButton;
    private Button nextMatchButton;
    private int won;
    private HBox matchButtonsHolder;
    private Text displayScore;

    public GameScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.pick = new UserPick();
        this.content = new VBox();
        this.text = new Text();
        this.matches = 1;
        this.currentMatch = 1;
        this.matcheSet = false;
        this.nextMatchButton = new Button("Next match");
        this.matchButtonsHolder = new HBox();
        this.displayScore = new Text("Score: 0");

        createScene();
    }

    public void createScene() {
        this.createContentVBox();

        ScrollPane scrollContent = new ScrollPane(this.content);

        this.scene = new Scene(
                new VBox(MainMenuBar.getGameMainMenuBar(this.primaryStage, this.text, this.content), scrollContent),
                660, 801);
    }

    public ArrayList<RadioButton> createMatchButtons(ToggleGroup matchButtonsGroup, HBox spotButtonsHolder) {
        int[] spotValues = IntStream.range(1, 5).toArray();
        ArrayList<RadioButton> matches = new ArrayList<>();

        for (int val : spotValues) {
            RadioButton radioBtn = new RadioButton(Integer.toString(val));
            radioBtn.setOnMouseClicked(e -> {
                spotButtonsHolder.setDisable(false);
                this.matches = val;
                this.createNextMatchButton();
                this.matcheSet = true;
            });
            matches.add(radioBtn);
            radioBtn.setToggleGroup(matchButtonsGroup);
        }
        return matches;
    }

    public ArrayList<RadioButton> createSpotsButtons(ToggleGroup spotButtonsGroup, GridPane numbers,
            Button quickPickButton) {
        int[] spotValues = IntStream.range(1, 11).toArray();
        ArrayList<RadioButton> spots = new ArrayList<>();

        for (int val : spotValues) {
            RadioButton radioBtn = new RadioButton(Integer.toString(val));
            radioBtn.setOnMouseClicked(e -> {
                this.pick.setSpots(val);
                numbers.setDisable(false);
                quickPickButton.setDisable(false);
            });
            spots.add(radioBtn);
            radioBtn.setToggleGroup(spotButtonsGroup);
        }
        return spots;
    }

    public void createNextMatchButton() {
        if (this.currentMatch < this.matches)
            this.nextMatchButton.setText("Next match");
        else
            this.nextMatchButton.setText("Play again");

        if (this.matcheSet)
            this.matchButtonsHolder.setDisable(true);
        else
            this.matchButtonsHolder.setDisable(false);

        this.nextMatchButton.setOnAction(e -> {
            if (this.currentMatch < this.matches) {
                this.currentMatch++;
                this.pick = new UserPick();
                this.createScene();
                this.primaryStage.setScene(this.scene);
            } else {
                this.pick = new UserPick();
                this.won = 0;
                this.matches = 1;
                this.currentMatch = 1;
                this.matcheSet = false;
                this.createScene();
                this.primaryStage.setScene(this.scene);
                this.displayScore.setText("Score: 0");
            }
        });
    }

    public void createContentVBox() {
        this.content = new VBox(20);
        createNextMatchButton();

        // Create grid
        GridPane numbers = new GridPane();
        numbers.setHgap(15);
        numbers.setVgap(15);
        numbers.setDisable(true);

        quickPickButton = new Button("Quick Pick!");
        quickPickButton.setDisable(true);

        // Spot buttons
        ToggleGroup spotButtonsGroup = new ToggleGroup();
        ArrayList<RadioButton> spotButtons = this.createSpotsButtons(spotButtonsGroup, numbers, quickPickButton);

        HBox spotButtonsHolder = new HBox();
        spotButtonsHolder.setSpacing(25);
        if (!this.matcheSet)
            spotButtonsHolder.setDisable(true);

        // Add all spot buttons to spotsHolder Horizontal box
        for (RadioButton spot : spotButtons)
            spotButtonsHolder.getChildren().add(spot);

        // Match buttons
        ToggleGroup matchButtonGroup = new ToggleGroup();
        ArrayList<RadioButton> matchButtons = this.createMatchButtons(matchButtonGroup, spotButtonsHolder);

        this.matchButtonsHolder = new HBox();
        matchButtonsHolder.setSpacing(25);
        if (this.matcheSet)
            this.matchButtonsHolder.setDisable(true);
        else
            this.matchButtonsHolder.setDisable(false);

        // Add all match buttons to matchButtonHolder Horizontal box
        for (RadioButton matchButton : matchButtons)
            matchButtonsHolder.getChildren().add(matchButton);

        Text drawStatus = new Text();
        Button drawBtn = new Button("Draw");
        this.getDrawBtn(drawStatus, spotButtonsHolder, numbers, drawBtn);
        drawBtn.setDisable(true);

        this.addGrid(numbers, spotButtonsHolder, drawBtn);

        createQuickPickButton(spotButtonsHolder, numbers, drawBtn);
        quickPickButton.setDisable(true);

        // Add children
        this.content.getChildren().addAll(this.text, this.displayScore,
                new Text("1. How many consecutive draws do you want to play?"),
                matchButtonsHolder,
                new Text("\n2. How many numbers (spots) do you want to play?"), spotButtonsHolder,
                new Text("\n3. Pick your own numbers, OR select with Quick Pick."), numbers, quickPickButton, drawBtn,
                drawStatus, this.nextMatchButton);
        this.content.setPadding(new Insets(0, 20, 20, 20));
    }

    public void createQuickPickButton(HBox spotButtonsHolder, GridPane numbers, Button drawBtn) {
        quickPickButton.setOnAction(e -> {
            pick.randomPick();
            ArrayList<Integer> userPicks = pick.getNumbers();

            int counter = 1;
            numbers.getChildren().clear();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 10; j++) {
                    CheckBox newCB = new CheckBox(Integer.toString(counter));
                    if (userPicks.contains(counter))
                        newCB.setSelected(true);
                    numbers.add(newCB, j, i);
                    counter++;
                }
            }
            numbers.setDisable(true);
            quickPickButton.setDisable(true);
            drawBtn.setDisable(false);
        });
    }

    public void getDrawBtn(Text showDrawStatus, HBox spotButtonsHolder, GridPane numbers, Button drawBtn) {
        drawBtn.setOnAction(e -> {
            drawBtn.setDisable(true);

            DrawRandom dr = new DrawRandom(80, 20, 1);
            TreeSet<Integer> draws = dr.draw();

            ArrayList<Integer> userPicks = this.pick.getNumbers();
            ArrayList<Integer> wonPicks = new ArrayList<>();

            // Render draw animation
            int counter = 1;
            numbers.setDisable(false);
            numbers.getChildren().clear();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 10; j++) {
                    boolean inDraws = draws.contains(counter);
                    boolean inUserPicks = userPicks.contains(counter);
                    CheckBox newCB = new CheckBox(Integer.toString(counter));

                    if (inDraws && inUserPicks) {
                        newCB.setSelected(true);
                        newCB.setTextFill(Color.BLUE);
                        wonPicks.add(counter);
                    } else if (inDraws) {
                        newCB.setSelected(true);
                    } else if (inUserPicks) {
                        newCB.setSelected(true);
                        newCB.setTextFill(Color.RED);
                    }
                    numbers.add(newCB, j, i);
                    counter++;
                }
            }

            this.won += wonPicks.size();
            showDrawStatus.setFill(Color.rgb(212, 62, 62));
            showDrawStatus.setText(Integer.toString(wonPicks.size()) + " won!");
            this.displayScore.setText("Score: " + this.won);
        });
    }

    public CheckBox createNumbersCheckbox(final int counter, HBox spotButtonsHolder, GridPane grid, Button drawBtn) {
        CheckBox cb = new CheckBox(Integer.toString(counter));
        cb.setId(Integer.toString(counter));
        cb.setOnAction(e -> {
            if (cb.isSelected() && this.pick.getNumbers().size() < this.pick.getSpots()) {
                this.pick.setNumber(counter);
                spotButtonsHolder.setDisable(true);
            }

            if (!cb.isSelected())
                this.pick.getNumbers().removeIf(elem -> elem == counter);

            if (this.pick.getNumbers().size() >= this.pick.getSpots()) {
                grid.setDisable(true);
                quickPickButton.setDisable(true);
                spotButtonsHolder.setDisable(false);
                drawBtn.setDisable(false);
            }
        });
        return cb;
    }

    /**
     * Generates a 8x10 grid and adds buttons
     * @param grid
     */
    public void addGrid(GridPane grid, HBox spotButtonsHolder, Button drawBtn) {
        int counter = 1;
        for (int x = 0; x < 8; x++) {
            for (int i = 0; i < 10; i++) {
                CheckBox cb = createNumbersCheckbox(counter, spotButtonsHolder, grid, drawBtn);
                grid.add(cb, i, x);
                counter++;
            }
        }
    }

    public Scene getScene() {
        return this.scene;
    }
}
