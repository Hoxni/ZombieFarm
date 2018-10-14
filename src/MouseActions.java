import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class MouseActions implements EventHandler<MouseEvent>{
    protected final Vector2D mouseLocation;
    protected final List<Tree> trees;
    protected final Zombie zombie;
    protected final int OFFSET = 25;

    public MouseActions(Vector2D ml, List<Tree> t, Zombie z){
        mouseLocation = ml;
        trees = t;
        zombie = z;
    }

    @Override
    public void handle(MouseEvent event){
        zombie.stopWhiteWave();
        if(event.isSecondaryButtonDown()){
            mouseLocation.set(event.getSceneX(), event.getSceneY() - OFFSET);
            zombie.follow(mouseLocation);
            zombie.setCutDownMode(false);
        }
        if(event.isPrimaryButtonDown()){
            for(Tree tree : trees){
                if(tree.contains(event.getSceneX(), event.getSceneY() - OFFSET)){
                    zombie.setTreeTarget(tree);
                    mouseLocation.set(tree.getCutPosition().x, tree.getCutPosition().y);
                    zombie.follow(mouseLocation);
                    break;
                }
            }
        }
    }
}