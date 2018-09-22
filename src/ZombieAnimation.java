import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ZombieAnimation extends Pane{
    static final int
            ZOMBIE_ANIMATION_LAYER = 0,
            CLOTH_LAYER = 1,
            HAT_LAYER = 2,
            ZOMBIE_STAGES = 7;

    static final int
            STAND = 0,
            WALK_DOWN = 1,
            WALK_UP = 2,
            WALKWOOD_DOWN = 3,
            WALKWOOD_UP = 4,
            WOODCUT = 5,
            WAKEUP = 6;

    protected Timeline
            animation,
            woodFelling;
    protected int
            animationMode = 0,
            stage = 0;
    protected Animation[] zombieAnimations;
    protected Animation zombie;
    protected Hat hat;
    protected Cloth cloth;
    protected boolean
            hasHat,
            hasCloth;
    protected Canvas zombieLayer;

    public ZombieAnimation(){
        createZombieAnimations();
        this.setWidth(zombie.getWidth());
        this.setHeight(zombie.getHeight());
        hasHat = false;
        hasCloth = false;
        createTimeline();
    }

    private void createZombieAnimations(){
        zombieAnimations = new Animation[ZOMBIE_STAGES];
        zombieAnimations[0] = new Animation(Paths.WOODCUTTER_STAND, Paths.WOODCUTTER_STAND_XML);
        zombieAnimations[1] = new Animation(Paths.WOODCUTTER_WALK_DOWN, Paths.WOODCUTTER_WALK_DOWN_XML);
        zombieAnimations[2] = new Animation(Paths.WOODCUTTER_WALK_UP, Paths.WOODCUTTER_WALK_UP_XML);
        zombieAnimations[3] = new Animation(Paths.WOODCUTTER_WALKWOOD_DOWN, Paths.WOODCUTTER_WALKWOOD_DOWN_XML);
        zombieAnimations[4] = new Animation(Paths.WOODCUTTER_WALKWOOD_UP, Paths.WOODCUTTER_WALKWOOD_UP_XML);
        zombieAnimations[5] = new Animation(Paths.WOODCUTTER_WOODCUT, Paths.WOODCUTTER_WOODCUT_XML);
        zombieAnimations[6] = new Animation(Paths.WOODCUTTER_WAKEUP, Paths.WOODCUTTER_WAKEUP_XML);
        zombie = zombieAnimations[0];
        zombieLayer = new Canvas(zombie.getWidth(), zombie.getHeight());
        this.getChildren().add(ZOMBIE_ANIMATION_LAYER, zombieLayer);
        this.getChildren().add(CLOTH_LAYER, new Pane());
        this.getChildren().add(HAT_LAYER, new Pane());
    }

    protected void createTimeline(){
        stage = 0;
        animation = new Timeline(new KeyFrame(Duration.millis(Settings.ZOMBIE_ANIMATION_SPEED), event -> {
            GraphicsContext gc = this.zombieLayer.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());
            if(stage >= zombie.getLength()) stage = 0;
            gc.drawImage(zombie.getAnimationStage(stage), 0, 0);
            if(hasCloth) cloth.displayStage(stage);
            if(hasHat) hat.displayStage(stage);
            stage++;
        }));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();

        woodFelling = new Timeline(new KeyFrame(Duration.millis(Settings.WOOD_FELlING_DURATION), event -> stand()));
        woodFelling.setCycleCount(1);
    }

    public void setAnimationMode(int animationMode){
        this.animationMode = animationMode;
    }

    public int getAnimationMode(){
        return animationMode;
    }

    private void setClothesMode(boolean flag, Clothes clothes){
        if(flag){
            clothes.setMode(animationMode);
        }
    }

    public void setHat(Hat hat){
        this.hat = hat;
        hat.setMode(animationMode);
        this.getChildren().set(HAT_LAYER, hat);
        hasHat = true;
    }

    public void setCloth(Cloth cloth){
        this.cloth = cloth;
        cloth.setMode(animationMode);
        this.getChildren().set(CLOTH_LAYER, cloth);
        hasCloth = true;
    }

    public void walkUp(){
        animationMode = WALK_UP;
        start();
    }

    public void walkDown(){
        animationMode = WALK_DOWN;
        start();
    }

    public void walkwoodUp(){
        animationMode = WALKWOOD_UP;
        start();
    }

    public void walkwoodDown(){
        animationMode = WALKWOOD_DOWN;
        start();
    }

    public void woodFelling(){
        animationMode = WOODCUT;
        start();
        woodFelling.play();
    }

    public void wakeUp(){
        animation.stop();
        KeyFrame keyFrame = animation.getKeyFrames().get(0);
        animation.getKeyFrames().set(0, new KeyFrame(Duration.millis(Settings.WAKEUP_DURATION), event -> {
            GraphicsContext gc = this.zombieLayer.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());
            if(stage >= zombieAnimations[WAKEUP].getLength()) {
                animation.stop();
                stage = 0;
                animation.getKeyFrames().set(0, keyFrame);
                animation.play();
            }
            gc.drawImage(zombieAnimations[WAKEUP].getAnimationStage(stage++), 0, 0);
        }));
        animation.play();
    }

    public void start(){
        zombie = zombieAnimations[animationMode];
        setClothesMode(hasCloth, cloth);
        setClothesMode(hasHat, hat);
    }

    public void stand(){
        animationMode = STAND;
        start();
    }
}
