import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class MouseActions implements EventHandler<MouseEvent>{
    protected final Vector2D mouseLocation;
    protected final List<Tree> trees;
    protected final Zombie zombie;

    public MouseActions(Vector2D ml, List<Tree> t, Zombie z){
        mouseLocation = ml;
        trees = t;
        zombie = z;
    }

    @Override
    public void handle(MouseEvent event){
        zombie.whiteWaveDisplayed = false;
        if(event.isSecondaryButtonDown()){
            mouseLocation.set(event.getSceneX(), event.getSceneY());
            zombie.follow(mouseLocation);
            zombie.setCutDownMode(false);
        }
        if(event.isPrimaryButtonDown()){
            for(Tree tree : trees){
                if(tree.contains(event.getSceneX(), event.getSceneY())){
                    zombie.setTreeTarget(tree);
                    mouseLocation.set(tree.getCutPosition().x, tree.getCutPosition().y);
                    zombie.follow(mouseLocation);
                    break;
                }
            }
        }
    }
}