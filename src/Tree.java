import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Tree extends Pane implements Obstruction{
    protected final Canvas tree;
    protected final double OFFSET = 12;
    protected final Animation stumpView;
    protected boolean isCutDown = false;
    protected final List<Vector2D> cornerPoints;

    public Tree(
            String palm,
            String palmXML,
            String shadow,
            String shadowXML,
            String stump,
            String stumpXML, double x, double y){
        Animation treeView = new Animation(palm, palmXML);
        Animation shadowView = new Animation(shadow, shadowXML);
        tree = new Canvas(treeView.getWidth(), treeView.getHeight());
        tree.getGraphicsContext2D().drawImage(shadowView.getAnimationStage(0), 0, 0);
        tree.getGraphicsContext2D().drawImage(treeView.getAnimationStage(0), 0, 0);
        this.getChildren().add(tree);
        this.setWidth(tree.getWidth());
        this.setHeight(tree.getHeight());

        stumpView = new Animation(stump, stumpXML);

        relocate(x, y);

        cornerPoints = new ArrayList<>();
        cornerPoints.add(new Vector2D(getPosX() - OFFSET * 3, getPosY()));
        cornerPoints.add(new Vector2D(getPosX(), getPosY() + OFFSET));
        cornerPoints.add(new Vector2D(getPosX() + OFFSET * 3, getPosY()));
        cornerPoints.add(new Vector2D(getPosX() + OFFSET, getPosY() - OFFSET));
    }

    public void chopDown(Zombie zombie){
        if(!isCutDown){
            zombie.zombie.woodFelling();
        }

        Timeline cutProcess = new Timeline(new KeyFrame(Duration.millis(Settings.WOOD_FELlING_DURATION), event -> {
            //if zombie stood near tree all necessary time, tree replaced by stump
            if(zombie.getZombieMode() == ZombieAnimation.WOODCUT && zombie.getTreeTarget() == this){
                tree.getGraphicsContext2D().clearRect(0, 0, tree.getWidth(), tree.getHeight());
                double x = tree.getWidth() / 2.0 - stumpView.getWidth() / 2.0;
                double y = tree.getHeight() - stumpView.getHeight();
                tree.getGraphicsContext2D().drawImage(stumpView.getAnimationStage(0), x, y);
                zombie.pickTree();
                isCutDown = true;
                return;
            }
        }));
        cutProcess.setCycleCount(1);
        cutProcess.play();
    }

    public double getPosX(){
        return this.getLayoutX() + tree.getWidth() / 2.0;
    }

    public double getPosY(){
        return this.getLayoutY() + tree.getHeight() - OFFSET;
    }

    public Vector2D getCutPosition(){
        return new Vector2D(cornerPoints.get(0).x + OFFSET, cornerPoints.get(0).y + OFFSET);
    }

    @Override
    public boolean contains(double x, double y){
        if(isCutDown) return false;
        double leftBound = this.getLayoutX() + tree.getWidth() / 2.0 - stumpView.getWidth() / 2.0;
        double rightBound = this.getLayoutX() + tree.getWidth() / 2.0 + stumpView.getWidth() / 2.0;
        double bottom = this.getLayoutY() + this.getHeight();
        double top = this.getLayoutY();
        if(x >= leftBound && x <= rightBound){
            return y <= bottom && y >= top;
        }
        return false;
    }

    //the same as in "Building"
    @Override
    public List<Vector2D> getBypass(Vector2D location, Vector2D target, List<Vector2D> intersectionPoints){

        //if target-point is situated inside of building
        if(intersectionPoints.size() == 1){
            if(Obstruction.isPointInPolygon(target, cornerPoints)){
                Vector2D v = Obstruction.getIntersectionPoints(location, target, cornerPoints).get(0);
                target.set(v.x, v.y);
            }
            return intersectionPoints;
        }

        //find edges which have intersection with path-line
        //this code duplicates "getIntersectionPoints" and can be optimized as described above
        List<Pair<Integer, Vector2D>> intersectedEdges = new ArrayList<>();
        for(int i = 0; i < cornerPoints.size(); i++){
            int next = (i + 1 == cornerPoints.size()) ? 0 : i + 1;

            Vector2D ip = Obstruction.getIntersectionPoint(location, target, cornerPoints.get(i), cornerPoints.get(next));

            if(ip != null){
                intersectedEdges.add(new Pair<>(i, ip));
            }
        }

        Vector2D firstIntersection = Collections.min(intersectionPoints, Comparator.comparingDouble(c -> Vector2D.subtract(location, c).magnitude()));
        Vector2D secondIntersection = Collections.min(intersectionPoints, Comparator.comparingDouble(c -> Vector2D.subtract(target, c).magnitude()));

        //points of bypass
        List<Vector2D> path = new ArrayList<>();
        path.add(firstIntersection);


        //if line intersects two adjacent edges
        if(Math.abs((intersectedEdges.get(0).getKey() - intersectedEdges.get(1).getKey()) % 2) == 1){
            int min = Math.min(intersectedEdges.get(0).getKey(), intersectedEdges.get(1).getKey());
            int index = Math.abs(intersectedEdges.get(0).getKey() - intersectedEdges.get(1).getKey()) % 3 + min;
            path.add(cornerPoints.get(index));
            path.add(secondIntersection);
            return path;
        }

        //if line intersects two opposite edges
        //find second bypass point
        Vector2D closestToSecond = Collections.min(cornerPoints, Comparator.comparingDouble(c -> Vector2D.subtract(secondIntersection, c).magnitude()));
        int a = Collections.min(intersectedEdges, Comparator.comparingDouble(c -> Vector2D.subtract(firstIntersection, c.getValue()).magnitude())).getKey(); //first intersected edge
        int c = cornerPoints.indexOf(closestToSecond); //index of closest corner-point for secondIntersection-point
        int index = -1;

        //some hard-to-explain checks to find first bypass point
        switch(c){
            case 0:
                if(a == 1) index = 1;
                else index = 3;
                break;
            case 1:
                if(a == 2) index = 2;
                else index = 0;
                break;
            case 2:
                if(a == 0) index = 1;
                else index = 3;
                break;
            case 3:
                if(a == 0) index = 0;
                else index = 2;
                break;
        }

        //get first bypass point
        Vector2D p = cornerPoints.get(index);
        //yes, second bypass point is calculating earlier than first

        path.add(p);
        path.add(closestToSecond);
        path.add(secondIntersection);

        return path;
    }

    @Override
    public List<Vector2D> getCornerPoints(){
        return cornerPoints;
    }

    @Override
    public Vector2D getCenter(){
        return new Vector2D(getPosX(), getPosY());
    }

    @Override
    public void setLayer(double layer){
        setTranslateZ(layer);
    }

    @Override
    public double getLayer(){
        return getTranslateZ();
    }
}
