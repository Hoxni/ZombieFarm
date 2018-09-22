import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class MouseActions implements EventHandler<MouseEvent>{
    protected Vector2D mouseLocation;
    protected ArrayList<Tree> trees;
    protected Zombie zombie;

    public MouseActions(Vector2D ml, ArrayList<Tree> t, Zombie z){
        mouseLocation = ml;
        trees = t;
        zombie = z;
    }

    @Override
    public void handle(MouseEvent event){
        if(event.isSecondaryButtonDown()){
            mouseLocation.set(event.getSceneX(), event.getSceneY());
            zombie.setChopping(false);
        }
        if(event.isPrimaryButtonDown()){
            for(Tree tree : trees){
                if(tree.contains(event.getSceneX(), event.getSceneY())){
                    zombie.setTreeTarget(tree);
                    mouseLocation.set(tree.getPosX(), tree.getPosY());
                    break;
                }
            }
        }
    }
}
