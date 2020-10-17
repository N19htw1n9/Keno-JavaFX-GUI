import Components.MainMenuBar;
import Objects.DrawRandom;
import Objects.UserPick;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.stream.IntStream;

public class GameScreen {
    private Stage primaryStage;
    private Scene scene;
    private TextArea textBox;
    private UserPick pick;

    public GameScreen(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.pick = new UserPick();

        createScene();
    }

    public ArrayList<RadioButton> createSpots(ToggleGroup spotGroup, GridPane numbers) {
//        int[] spotValues = new int[] { 1, 4, 8, 10 };
        int[] spotValues = IntStream.range(1, 11).toArray();
        ArrayList<RadioButton> spots = new ArrayList<>();

        for (int val : spotValues) {
            RadioButton radioBtn = new RadioButton(Integer.toString(val));
            radioBtn.setOnMouseClicked(e -> {
                this.pick.setSpots(val);
                numbers.setDisable(false);
            });
            spots.add(radioBtn);
            radioBtn.setToggleGroup(spotGroup);
        }
        return spots;
    }

    public void createScene() {
        Text gameText = new Text("Start playing");

        // Create grid
        GridPane numbers = new GridPane();
        numbers.setAlignment(Pos.CENTER);
        numbers.setHgap(15);
        numbers.setVgap(15);
        numbers.setDisable(true);

        ToggleGroup spotGroup = new ToggleGroup();
        ArrayList<RadioButton> spots = createSpots(spotGroup, numbers);

        HBox spotsHolder = new HBox();
        spotsHolder.setAlignment(Pos.CENTER);
        spotsHolder.setSpacing(25);

        // Add all radio buttons to spotsHolder Horizontal box
        for (RadioButton spot : spots)
            spotsHolder.getChildren().add(spot);

        Text drawStatus = new Text();
        Button drawBtn = getDrawBtn(drawStatus);
        drawBtn.setDisable(true);

        addGrid(numbers, spotsHolder, drawBtn);

        VBox content = new VBox(
                20,
                gameText,
                spotsHolder,
                numbers,
                drawBtn,
                drawStatus
        );
        content.setPadding(new Insets(0, 20, 0, 20));

        this.scene = new Scene(
                new VBox(
                        20,
                        MainMenuBar.getMainMenuBar(primaryStage, textBox),
                        content
                ),
                800,
                800
        );
    }

    public Button getDrawBtn(Text showDrawStatus) {
        Button drawBtn = new Button("Draw");
        drawBtn.setOnAction(e -> {
            DrawRandom dr = new DrawRandom(80, 20, 1);
            TreeSet<Integer> draws = dr.draw();

            System.out.println("\nComputer is drawing....");
            System.out.println(draws.toString());

            int matched = 0;
            ArrayList<Integer> userPick = this.pick.getNumbers();
            for (int pick : userPick) {
                if (draws.contains(pick))
                    matched++;
            }
            showDrawStatus.setStyle("color: red");
            showDrawStatus.setText(Integer.toString(matched) + " were matched!");
        });
        return drawBtn;
    }

    /**
     * Generates a 8x10 grid and adds buttons
     * @param grid
     */
    public void addGrid(GridPane grid, HBox spotsHolder, Button drawBtn) {
        int counter = 1;
        for (int x = 0; x < 8; x++) {
            for (int i = 0; i < 10; i++) {
                CheckBox cb = new CheckBox(Integer.toString(counter));
                final int finalCounter = counter;
                cb.setOnAction(e -> {
                    if (cb.isSelected() && this.pick.getNumbers().size() < this.pick.getSpots()) {
                        this.pick.setNumber(finalCounter);
                        spotsHolder.setDisable(true);
                    }

                    if (!cb.isSelected())
                        this.pick.getNumbers().removeIf(elem -> elem == finalCounter);

                    if (this.pick.getNumbers().size() >= this.pick.getSpots()) {
                        grid.setDisable(true);
                        drawBtn.setDisable(false);
                    }
                });

                grid.add(cb, i, x);
                counter++;
            }
        }
    }

    public Scene getScene() {
        return this.scene;
    }
}
