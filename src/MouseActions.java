import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public class MouseActions implements EventHandler<MouseEvent>{
    protected Vector2D mouseLocation;
    protected Tree tree;
    protected Zombie zombie;

    public MouseActions(Vector2D ml, Tree t, Zombie z){
        mouseLocation = ml;
        tree = t;
        zombie = z;
    }

    @Override
    public void handle(MouseEvent event){
        if(event.isSecondaryButtonDown()){
            mouseLocation.set(event.getSceneX(), event.getSceneY());
            zombie.setChopping(false);
        }
        if(event.isPrimaryButtonDown()){
            if(tree.contains(event.getSceneX(), event.getSceneY())){
                mouseLocation.set(tree.getPosX(), tree.getPosY());
                zombie.setTreeTarget(tree);
            }
        }
    }
}