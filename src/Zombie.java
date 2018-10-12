import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Zombie extends Sprite{
    protected Vector2D currentTargetPoint;
    protected ZombieAnimation zombie;
    protected WhiteWave whiteWave;
    protected Tree treeTarget;
    protected boolean whiteWaveStarted = false;
    protected boolean cutDown = false;
    protected boolean hasTree = false;
    protected boolean returnToStart = true;
    protected boolean wakeUp = true;
    protected boolean goDown = true;
    protected ArrayList<Vector2D> points;
    protected ArrayList<Obstruction> obstructions;
    protected ArrayDeque<Vector2D> layers;
    protected int pointIndex = 0;

    public Zombie(Vector2D location, ZombieAnimation zombie, WhiteWave point, ArrayList<? extends Obstruction> b){
        super(location);
        this.zombie = zombie;
        this.whiteWave = point;
        getChildren().add(zombie);
        setWidth(zombie.getWidth());
        setHeight(zombie.getHeight());
        setCenter();
        currentTargetPoint = Settings.INITIAL_POINT.copy();
        points = new ArrayList<>();
        obstructions = (ArrayList<Obstruction>) b;
        layers = new ArrayDeque<>();
        zombie.wakeUp();
        Timeline t = new Timeline(new KeyFrame(Duration.millis(Settings.WAKEUP_DELAY), event -> wakeUp = false));
        t.setCycleCount(1);
        t.play();
    }

    public void update(){
        if(wakeUp) return;

        //prohibits to change location while zombie carry a timber to initial point
        if(returnToStart){
            if(Vector2D.subtract(Settings.INITIAL_POINT, location).magnitude() < Settings.STOP_DISTANCE){
                returnToStart = false;
                hasTree = false;
            }
        }

        if(Vector2D.subtract(location, currentTargetPoint).magnitude() < Settings.STOP_DISTANCE){
            stop();
        } else {
            if(location.y > currentTargetPoint.y){
                if(location.x <= currentTargetPoint.x){
                    setScaleX(-1);
                } else {
                    setScaleX(1);
                }
                if(hasTree) zombie.walkwoodUp();
                else zombie.walkUp();
            } else {
                if(location.x <= currentTargetPoint.x){
                    setScaleX(-1);
                } else {
                    setScaleX(1);
                }
                if(hasTree) zombie.walkwoodDown();
                else zombie.walkDown();
            }
        }

        if(!layers.isEmpty()){
            if(goDown){
                if(location.y >= layers.getFirst().y){
                    setTranslateZ(layers.getFirst().x);
                    toFront();
                    layers.pop();
                }
            } else {
                if(location.y <= layers.getFirst().y){
                    setTranslateZ(layers.getFirst().x);
                    toBack();
                    layers.pop();
                }
            }
        }

        super.update(currentTargetPoint);
    }

    public void stop(){
        pointIndex++;
        if(pointIndex < points.size()){
            currentTargetPoint = points.get(pointIndex);
        } else {
            whiteWave.stop();
            whiteWaveStarted = false;
        }

        //if zombie will fell a tree, cut-animation will play instead of stay-animation
        if(cutDown){
            //this.toFront(); //want some fix pos of zombie and tree
            setScaleX(-1);
            treeTarget.chopDown(this);
        } else if(!whiteWaveStarted){//prohibits stand-animation if zombie going to target point
            zombie.stand();
        }
    }

    public void setChopping(boolean mode){
        cutDown = mode;
    }

    public void setTreeTarget(Tree tree){
        //prohibits to cut down a tree while zombie carry a timber
        if(returnToStart) return;

        cutDown = true;
        treeTarget = tree;
    }

    public Tree getTreeTarget(){
        return treeTarget;
    }

    public int getZombieMode(){
        return zombie.getAnimationMode();
    }

    public void pickTree(){
        cutDown = false;
        hasTree = true;
        returnToStart = true;
        follow(Settings.INITIAL_POINT);
    }

    //create path to target point
    void follow(Vector2D target){
        if(returnToStart) target = Settings.INITIAL_POINT;
        pointIndex = 0;
        points.clear();
        layers.clear();
        List<List<Vector2D>> paths = new ArrayList<>();
        List<Vector2D> layersCount = new ArrayList<>();

        if(location.y < target.y){
            goDown = true;
        } else {
            goDown = false;
        }

        //get collections of bypass points
        for(Obstruction obstruction : obstructions){
            List<Vector2D> intersectionPoints = Obstruction.getIntersectionPoints(location, target, obstruction.getCornerPoints());
            if(!intersectionPoints.isEmpty()){
                paths.add(obstruction.getBypass(location, target, intersectionPoints));
                if(Math.abs(obstruction.getCenter().y - location.y) +
                        Math.abs(obstruction.getCenter().y - target.y) <=
                        Math.abs(location.y - target.y)){
                    layersCount.add(new Vector2D(obstruction.getLayer(), obstruction.getCenter().y));
                }
            }
        }

        //sort collections of bypass point from closest to farthest
        //collection sorts by first bypass point
        paths.sort(Comparator.comparingDouble(c -> Vector2D.subtract(location, c.get(0)).magnitude()));

        layersCount.sort(Comparator.comparingDouble(c -> Math.abs(location.y - c.y)));
        layers.addAll(layersCount);

        //add points
        for(List<Vector2D> path : paths){
            points.addAll(path);
        }

        //add target as final point
        points.add(target);

        currentTargetPoint = points.get(pointIndex);
        whiteWave.start(target.x, target.y);
        whiteWaveStarted = true;
    }
}