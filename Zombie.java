import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Zombie extends Sprite{
    protected ZombieAnimation zombie;
    protected Point point;
    protected Tree treeTarget;
    protected boolean cutDown = false;
    protected boolean hasTree = false;
    protected boolean busy = true;
    protected boolean wakeUp = true;

    public Zombie(Vector2D location, Vector2D velocity, Vector2D acceleration, ZombieAnimation zombie, Point point){
        super(location, velocity, acceleration);
        this.zombie = zombie;
        this.point = point;
        this.getChildren().add(zombie);
        setWidth(zombie.getWidth());
        setHeight(zombie.getHeight());
        setCenter();
        zombie.wakeUp();
        Timeline t = new Timeline(new KeyFrame(Duration.millis(Settings.WAKEUP_DELAY), event -> wakeUp = false));
        t.setCycleCount(1);
        t.play();
    }

    @Override
    public void update(Vector2D v){
        if(wakeUp) return;

        //prohibits to change location while zombie carry a timber to initial point
        if(busy){
            if(Vector2D.subtract(Settings.INITIAL_POINT, location).magnitude() < Settings.STOP_DISTANCE){
                busy = false;
                hasTree = false;
                v.set(Settings.INITIAL_POINT.x, Settings.INITIAL_POINT.y);
            }
            v = Settings.INITIAL_POINT;
        }

        if(Vector2D.subtract(location, v).magnitude() < Settings.STOP_DISTANCE){
            stop();
        } else {
            point.start(v.x, v.y);
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
        point.stop();

        //if zombie will fell a tree, cut-animation will play instead of stay-animation
        if(cutDown){
            this.toFront(); //want some fix pos of zombie and tree
            setScaleX(-1);
            treeTarget.chopDown(this);
        } else zombie.stand();
    }

    public void setChopping(boolean mode){
        cutDown = mode;
    }

    public void setTreeTarget(Tree tree){
        //prohibits to cut down a tree while zombie carry a timber
        if(busy) return;

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
        busy = true;
    }
}