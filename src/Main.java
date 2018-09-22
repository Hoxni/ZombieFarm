import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application{
    final double HEIGHT = 700;
    final double WIDTH = 900;
    Vector2D mouseLocation = new Vector2D( 100, 100);


    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Point");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        canvas.getGraphicsContext2D().fillRect(0, 0, 900, 700);
        Pane pane = new Pane();
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

        Vector2D location = new Vector2D( 100,100);
        Vector2D velocity = new Vector2D( 0,0);
        Vector2D acceleration = new Vector2D( 0,0);

        Tree tree = new Tree(
                Paths.TROPIC_PALM,
                Paths.TROPIC_PALM_XML,
                Paths.TROPIC_PALM_SHADOW,
                Paths.TROPIC_PALM_SHADOW_XML,
                Paths.TROPIC_PALM_STUMP,
                Paths.TROPIC_PALM_STUMP_XML);

        ZombieAnimation z = new ZombieAnimation();
        z.setCloth(cloth);
        z.setHat(hat);
        Point p = new Point();
        Zombie zombie = new Zombie(location, velocity, acceleration, z, p);
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(Settings.MOVING_SPEED), event -> zombie.update(mouseLocation)));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new MouseActions(mouseLocation, tree, zombie));
        pane.getChildren().add(canvas);
        pane.getChildren().add(zombie);
        pane.getChildren().add(tree);
        pane.getChildren().add(p);

        tree.relocate( 200, 200);
        root.setCenter(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}