import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Zombie extends Sprite{
    protected Vector2D v;
    protected ZombieAnimation zombie;
    protected Point point;
    protected Tree treeTarget;
    protected boolean pointStarted = false;
    protected boolean cutDown = false;
    protected boolean hasTree = false;
    protected boolean returnToStart = true;
    protected boolean wakeUp = true;
    protected ArrayList<Vector2D> points;
    protected ArrayList<Building> buildings;
    protected int pointIndex = 0;

    public Zombie(Vector2D location, Vector2D velocity, Vector2D acceleration, ZombieAnimation zombie, Point point, ArrayList<Building> b){
        super(location, velocity, acceleration);
        this.zombie = zombie;
        this.point = point;
        this.getChildren().add(zombie);
        setWidth(zombie.getWidth());
        setHeight(zombie.getHeight());
        setCenter();
        v = new Vector2D(Settings.INITIAL_POINT.x, Settings.INITIAL_POINT.y);
        points = new ArrayList<>();
        buildings = b;
        zombie.wakeUp();
        Timeline t = new Timeline(new KeyFrame(Duration.millis(Settings.WAKEUP_DELAY), event -> wakeUp = false));
        t.setCycleCount(1);
        t.play();
    }

    //@Override
    public void update(){
        if(wakeUp) return;

        //prohibits to change location while zombie carry a timber to initial point
        if(returnToStart){
            if(Vector2D.subtract(Settings.INITIAL_POINT, location).magnitude() < Settings.STOP_DISTANCE){
                returnToStart = false;
                hasTree = false;
                //v.set(Settings.INITIAL_POINT.x, Settings.INITIAL_POINT.y);
            }
        }

        if(Vector2D.subtract(location, v).magnitude() < Settings.STOP_DISTANCE){
            stop();
        } else {
            if(location.y > v.y){
                if(location.x <= v.x){
                    setScaleX(-1);
                } else {
                    setScaleX(1);
                }
                if(hasTree) zombie.walkwoodUp();
                else zombie.walkUp();
            } else {
                if(location.x <= v.x){
                    setScaleX(-1);
                } else {
                    setScaleX(1);
                }
                if(hasTree) zombie.walkwoodDown();
                else zombie.walkDown();
            }
        }

        super.update(v);
    }

    public void stop(){
        pointIndex++;
        if(pointIndex < points.size()){
            v = points.get(pointIndex);
        } else {
            point.stop();
            pointStarted = false;
        }

        //if zombie will fell a tree, cut-animation will play instead of stay-animation
        if(cutDown){
            //this.toFront(); //want some fix pos of zombie and tree
            setScaleX(-1);
            treeTarget.chopDown(this);
        } else if(!pointStarted) {//prohibits stand-animation if zombie going to target point
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
        List<List<Vector2D>> paths = new ArrayList<>();

        //adds bypass points
        for(Building building : buildings){
            List<Vector2D> intersectionPoints = Barrier.getIntersectionPoints(location, target, building.getRectPoints());
            if(!intersectionPoints.isEmpty()){
                paths.add(building.getBypassPoints(location, target, intersectionPoints));
                //points.add(new Vector2D(building.getLayoutX() + 100, building.getLayoutY()));

            }
        }

        //sort points from closest to farthest for correct queue of passage
        paths.sort(Comparator.comparingDouble(c -> Vector2D.subtract(location, c.get(0)).magnitude()));

        //adds points
        for(List<Vector2D> path : paths){
            points.addAll(path);
        }

        //adds target as final point
        points.add(target);

        v = points.get(pointIndex);
        point.start(target.x, target.y);
        pointStarted = true;
    }
}
