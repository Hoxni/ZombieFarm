import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Tree extends Pane{
    protected Canvas tree;
    protected final double OFFSET = 12;
    protected Animation stumpView;
    protected boolean isFelled = false;

    public Tree(
            String palm,
            String palmXML,
            String shadow,
            String shadowXML,
            String stump,
            String stumpXML){
        Animation treeView = new Animation(palm, palmXML);
        Animation shadowView = new Animation(shadow, shadowXML);
        tree = new Canvas(treeView.getWidth(), treeView.getHeight());
        tree.getGraphicsContext2D().drawImage(shadowView.getAnimationStage(0), 0, 0);
        tree.getGraphicsContext2D().drawImage(treeView.getAnimationStage(0), 0, 0);
        this.getChildren().add(tree);

        stumpView = new Animation(stump, stumpXML);
    }

    public void chopDown(Zombie zombie){
        zombie.zombie.woodFelling();

        Timeline cutProcess = new Timeline(new KeyFrame(Duration.millis(Settings.WOOD_FELlING_DURATION), event -> {
            //if zombie stood near tree all necessary time, tree replaced by stump
            if(zombie.getZombieMode() == ZombieAnimation.WOODCUT && zombie.getTreeTarget() == this){
                tree.getGraphicsContext2D().clearRect(0, 0, tree.getWidth(), tree.getHeight());
                double x = tree.getWidth() / 2.0 - stumpView.getWidth() / 2.0;
                double y = tree.getHeight() - stumpView.getHeight();
                tree.getGraphicsContext2D().drawImage(stumpView.getAnimationStage(0), x, y);
                zombie.pickTree();
                isFelled = true;
            }
        }));
        cutProcess.setCycleCount(1);
        cutProcess.play();
    }

    public double getPosX(){
        return this.getLayoutX() + tree.getWidth() / 2.0 - OFFSET;
    }

    public double getPosY(){
        return this.getLayoutY() + tree.getHeight() - OFFSET;
    }

    @Override
    public boolean contains(double x, double y){
        if(isFelled) return false;
        double leftBound = this.getLayoutX() + tree.getWidth() / 2.0 - stumpView.getWidth() / 2.0;
        double rightBound = this.getLayoutX() + tree.getWidth() / 2.0 + stumpView.getWidth() / 2.0;
        double bottom = this.getLayoutY() + this.getHeight();
        double top = this.getLayoutY();
        if(x >= leftBound && x <= rightBound){
            if(y <= bottom && y >= top){
                return true;
            }
        }
        return false;
    }
}
