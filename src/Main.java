import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.transformation.SortedList;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Main extends Application{
    private final double HEIGHT = 700;
    private final double WIDTH = 900;
    private final Vector2D mouseLocation = new Vector2D(100, 100);
    private List<Tree> trees;
    private List<Building> buildings;
    private List<Obstruction> obstructions;
    private List<Zombie> zombies;
    private Zombie zombie;
    private Pane pane;


    @Override
    public void start(Stage primaryStage){
        primaryStage.setTitle("Zombie Farm");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, WIDTH, HEIGHT, true);

        pane = new Pane();

        createTrees();
        createBuildings();
        setObstructions();
        createMainZombie();
        createMobZombies();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(Settings.MOVING_SPEED), event -> {
            zombie.update();
            for(Zombie mob : zombies){
                mob.update();
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Random random = new Random();
        Timeline timeline1 = new Timeline(new KeyFrame(Duration.seconds(3), event -> {
            for(Zombie mob : zombies){
                Vector2D target = new Vector2D(random.nextInt(900), random.nextInt(700));
                mob.follow(target);
                for(Tree tree : trees){
                    if(tree.contains(target.x, target.y)){
                        mob.setTreeTarget(tree);
                        target.set(tree.getCutPosition().x, tree.getCutPosition().y);
                        mob.follow(target);
                        break;
                    }
                }
            }
        }));
        timeline1.setCycleCount(Timeline.INDEFINITE);
        timeline1.play();

        root.setCenter(pane);
        primaryStage.setScene(scene);
        primaryStage.show();
        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new MouseActions(mouseLocation, trees, zombie));

    }

    private void createMainZombie(){
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
        ZombieAnimation z = new ZombieAnimation();
        z.setCloth(cloth);
        z.setHat(hat);
        WhiteWave p = new WhiteWave();
        p.setTranslateZ(-obstructions.size() - 1);
        pane.getChildren().add(p);
        zombie = new Zombie(location, z, p, obstructions);
        zombie.setTranslateZ(0);
        pane.getChildren().add(zombie);
    }

    private void createMobZombies(){
        zombies = new ArrayList<>();
        WhiteWave wwMob = new WhiteWave();

        for(int i = 0; i < Settings.ZOMBIES_NUMBER; i++){
            ZombieAnimation zaMob = new ZombieAnimation();
            Zombie zombieMob = new Zombie(new Vector2D(100, 100), zaMob, wwMob, obstructions);
            zombies.add(zombieMob);
            pane.getChildren().add(zombieMob);
        }
    }

    private void setObstructions(){
        obstructions = new ArrayList<>();
        obstructions.addAll(trees);
        obstructions.addAll(buildings);
        obstructions.sort(Comparator.comparingDouble(o -> o.getCenter().y));
        double size = obstructions.size();
        for(int i = 0; i < size; i++){
            obstructions.get(i).setLayer(-i - 1);
            System.out.println(obstructions.get(i).getCenter().y + " " + obstructions.get(i).getLayer());
        }
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        canvas.getGraphicsContext2D().fillRect(0, 0, 900, 700);
        pane.getChildren().add(canvas);
        canvas.setTranslateZ(0);
    }

    private void createBuildings(){
        buildings = new ArrayList<>();
        for(int i = 0; i < Settings.BUILDINGS_NUMBER; i++){
            double y = 100 + i*100;
            double x = 100 + Math.random()*500;
            Building building = new Building(
                    Paths.TOWER,
                    Paths.TOWER_XML,
                    x, y);
            pane.getChildren().add(building);
            buildings.add(building);
        }
    }

    private void createTrees(){
        Random positions = new Random();
        trees = new ArrayList<>();
        for(int i = 0; i < Settings.TREES_NUMBER; i++){
            double xPos = 50 + (500) * positions.nextDouble();
            double yPos = 100 + (100) * positions.nextDouble();
            Tree tree = new Tree(
                    Paths.TROPIC_PALM,
                    Paths.TROPIC_PALM_XML,
                    Paths.TROPIC_PALM_SHADOW,
                    Paths.TROPIC_PALM_SHADOW_XML,
                    Paths.TROPIC_PALM_STUMP,
                    Paths.TROPIC_PALM_STUMP_XML,
                    xPos,
                    yPos);
            pane.getChildren().add(tree);
            trees.add(tree);
        }
    }

    public static void main(String[] args){
        launch(args);
    }
}
