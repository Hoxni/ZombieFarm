import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Main extends Application{
    final double HEIGHT = 700;
    final double WIDTH = 900;
    Vector2D mouseLocation = new Vector2D(100, 100);
    ArrayList<Tree> trees;
    Pane pane;


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("WhiteWave");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        canvas.getGraphicsContext2D().fillRect(0, 0, 900, 700);
        pane = new Pane();
        Hat hat = new Hat(
                Paths.DOUBLE_HAT_STAND,
                Paths.DOUBLE_HAT_STAND_XML,
                Paths.DOUBLE_HAT_WALK_DOWN,
                Paths.DOUBLE_HAT_WALK_DOWN_XML,
                Paths.DOUBLE_HAT_WALK_UP,
                Paths.DOUBLE_HAT_WALK_UP_XML,
                Paths.DOUBLE_HAT_WALKWOOD_DOWN,
                Paths.DOUBLE_HAT_WALKWOOD_DOWN_XML,
                Paths.DOUBLE_HAT_WALKWOOD_UP,
                Paths.DOUBLE_HAT_WALKWOOD_UP_XML,
                Paths.DOUBLE_HAT_WOODCUT,
                Paths.DOUBLE_HAT_WOODCUT_XML);
        Cloth cloth = new Cloth(
                Paths.CLOTH_STAND,
                Paths.CLOTH_STAND_XML,
                Paths.CLOTH_WALK_DOWN,
                Paths.CLOTH_WALK_DOWN_XML,
                Paths.CLOTH_WALK_UP,
                Paths.CLOTH_WALK_UP_XML,
                Paths.CLOTH_WALKWOOD_DOWN,
                Paths.CLOTH_WALKWOOD_DOWN_XML,
                Paths.CLOTH_WALKWOOD_UP,
                Paths.CLOTH_WALKWOOD_UP_XML,
                Paths.CLOTH_WOODCUT,
                Paths.CLOTH_WOODCUT_XML);

        Vector2D location = new Vector2D(100, 100);
        Vector2D velocity = new Vector2D(0, 0);
        Vector2D acceleration = new Vector2D(0, 0);

        ZombieAnimation z = new ZombieAnimation();
        z.setCloth(cloth);
        z.setHat(hat);
        WhiteWave p = new WhiteWave();
        Building tower = new Building(Paths.TOWER, Paths.TOWER_XML, 200, 200);
        Building tower1 = new Building(Paths.TOWER, Paths.TOWER_XML, 400, 400);
        ArrayList<Building> towers = new ArrayList<>();
        towers.add(tower);
        towers.add(tower1);

        Zombie zombie = new Zombie(location, velocity, acceleration, z, p, towers);

        WhiteWave pMob = new WhiteWave();
        //ZombieAnimation zMob = new ZombieAnimation();
        //Zombie zombieMob = new Zombie(new Vector2D(500, 500), new Vector2D(0 ,0), new Vector2D(0, 0), zMob, pMob, towers);
        canvas.getGraphicsContext2D().setStroke(Color.WHITE);
        canvas.getGraphicsContext2D().setLineWidth(20);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(Settings.MOVING_SPEED), event -> {
            zombie.update();
            //zombieMob.update();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

       /* Random random = new Random();
        Timeline timeline1 = new Timeline(new KeyFrame(Duration.seconds(3), event -> zombieMob.follow(new Vector2D(random.nextInt(900), random.nextInt(700)))));
        timeline1.setCycleCount(Timeline.INDEFINITE);
        timeline1.play();*/

        pane.getChildren().add(canvas);
        createTrees();

        pane.getChildren().add(zombie);
        //pane.getChildren().add(zombieMob);

        pane.getChildren().add(tower);
        pane.getChildren().add(tower1);

        Canvas b = new Canvas(700, 700);
        b.getGraphicsContext2D().setFill(Color.WHITE);

        for(Vector2D v : tower.bypassPoints){
            b.getGraphicsContext2D().fillRect(v.x, v.y, 5, 5);
        }
        for(Vector2D v : tower.rectPoints){
            b.getGraphicsContext2D().fillRect(v.x, v.y, 5, 5);

        }
        pane.getChildren().add(b);

        pane.getChildren().add(p);

        root.setCenter(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new MouseActions(mouseLocation, trees, zombie));

    }

    public void createTrees(){
        Random positions = new Random();
        trees = new ArrayList<>();
        for(int i = 0; i < Settings.TREE_NUMBER; i++){
            Tree tree = new Tree(
                    Paths.TROPIC_PALM,
                    Paths.TROPIC_PALM_XML,
                    Paths.TROPIC_PALM_SHADOW,
                    Paths.TROPIC_PALM_SHADOW_XML,
                    Paths.TROPIC_PALM_STUMP,
                    Paths.TROPIC_PALM_STUMP_XML);
            double xPos = 50 + (500) * positions.nextDouble();
            double yPos = 100 + (100) * positions.nextDouble();
            tree.relocate(xPos, yPos);
            pane.getChildren().add(tree);
            trees.add(tree);
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
