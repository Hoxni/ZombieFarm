import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;

public class Zombie extends Sprite{
    protected Vector2D currentTargetPoint;
    protected final ZombieAnimation zombie;
    protected final WhiteWave whiteWave;
    protected Tree treeTarget;
    protected boolean whiteWaveDisplayed = false;
    protected boolean cutDown = false; //"true" when zombie is going to cut down a tree
    protected boolean hasTimber = false; //"true" when zombie carries a timber
    protected boolean returnToStart = true; //"true" when zombie returns to initial point
    protected boolean wakeUp = true; //used for wake up animation when game starts
    protected boolean goDown = true; //"true" when zombie goes from top to bottom of frame
    protected final List<Vector2D> points;
    protected final List<Obstruction> obstructions;
    protected final Deque<Pair<Double, Double>> layers; //layers of obstructions
    protected int pointIndex = 0; //index of current target point

    public Zombie(Vector2D location, ZombieAnimation zombie, WhiteWave point, List<? extends Obstruction> b){
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
                hasTimber = false;
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
                if(hasTimber) zombie.walkwoodUp();
                else zombie.walkUp();
            } else {
                if(location.x <= currentTargetPoint.x){
                    setScaleX(-1);
                } else {
                    setScaleX(1);
                }
                if(hasTimber) zombie.walkwoodDown();
                else zombie.walkDown();
            }
        }

        if(!layers.isEmpty()){
            if(goDown){
                if(location.y >= layers.getFirst().getValue()){
                    setTranslateZ(layers.getFirst().getKey() - 0.5);
                    //toFront();
                    layers.pop();
                }
            } else {
                if(location.y <= layers.getFirst().getValue()){
                    setTranslateZ(layers.getFirst().getKey() + 0.5);
                    //toBack();
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
            whiteWaveDisplayed = false;
        }

        //if zombie will cut down a tree, cut-animation will play instead of stay-animation
        //zombie can cut down a tree if stands on special point (cutPosition) near this tree
        if(cutDown && Vector2D.subtract(location, treeTarget.getCutPosition()).magnitude() < Settings.STOP_DISTANCE){
            setScaleX(-1);
            treeTarget.chopDown(this);
        } else if(!whiteWaveDisplayed){//prohibits stand-animation if zombie is going to the target point
            zombie.stand();
        }
    }

    public void setCutDownMode(boolean mode){
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

    /**
     * zombie goes to the initial point with a timber
     * when he cut down a tree
     */
    public void pickTree(){
        cutDown = false;
        hasTimber = true;
        returnToStart = true;
        follow(Settings.INITIAL_POINT);
    }

    /**
     * create path to the target point
     */
    void follow(Vector2D target){
        if(returnToStart) target = Settings.INITIAL_POINT.copy();
        pointIndex = 0;
        points.clear();
        layers.clear();
        List<List<Vector2D>> paths = new ArrayList<>();
        List<Pair<Double, Double>> layersList = new ArrayList<>();

        if(location.y < target.y){
            goDown = true;
        } else {
            goDown = false;
        }

        //get collections of bypass points
        for(Obstruction obstruction : obstructions){
            //if obstruction is between location and target
            //zombie need to change the layer when passes near
            if(Math.abs(obstruction.getCenter().y - location.y) +
                    Math.abs(obstruction.getCenter().y - target.y) <=
                    Math.abs(location.y - target.y)){
                layersList.add(new Pair<>(obstruction.getLayer(), obstruction.getCenter().y));
            }

            List<Vector2D> intersectionPoints = Obstruction.getIntersectionPoints(location, target, obstruction.getCornerPoints());
            if(!intersectionPoints.isEmpty()){
                paths.add(obstruction.getBypass(location, target, intersectionPoints));
            }
        }

        //sort collections of bypass point from closest to farthest
        //collection sorts by first bypass point
        paths.sort(Comparator.comparingDouble(c -> Vector2D.subtract(location, c.get(0)).magnitude()));

        layersList.sort(Comparator.comparingDouble(c -> Math.abs(location.y - c.getValue())));
        layers.addAll(layersList);

        //add points
        for(List<Vector2D> path : paths){
            points.addAll(path);
        }

        //add target as final point
        points.add(target);

        currentTargetPoint = points.get(pointIndex);
        whiteWave.start(target.x, target.y);
        whiteWaveDisplayed = true;
    }

    public ZombieAnimation getZombieAnimation(){
        return zombie;
    }

    public void stopWhiteWave(){
        whiteWaveDisplayed = false;
    }
}
