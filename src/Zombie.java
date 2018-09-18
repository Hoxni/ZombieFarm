import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Zombie extends Sprite{
    protected ZombieAnimation zombie;
    protected Point point;
    protected Tree treeTarget;
    protected boolean pointStarted = false;
    protected boolean chop = false;
    protected boolean hasTree = false;
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
        if(Vector2D.subtract(location, v).magnitude() < Settings.STOP_DISTANCE){
            stop();
        } else {
            if(!pointStarted){
                point.start(v.x, v.y);
            }
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
        if(chop) {
            this.toFront(); //want some fix pos of zombie and tree
            setScaleX(-1);
            zombie.woodFelling();
            treeTarget.chopDown(this);
        } else zombie.stand();
        point.stop();
        pointStarted = false;
    }

    public boolean isChopping(){
        return chop;
    }

    public void setChopping(boolean mode){
        chop = mode;
    }

    public void setTreeTarget(Tree tree){
        chop = true;
        treeTarget = tree;
    }

    public void pickTree(boolean mode){
        hasTree = mode;
    }
}