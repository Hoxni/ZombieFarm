import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Building extends Pane implements Barrier{
    protected final double OFFSET = 120;
    protected double posX, posY;
    protected ArrayList<Vector2D> bypassPoints;

    public Building(String building, String buildingXML, double x, double y){
        Animation animation = new Animation(building, buildingXML);
        Canvas canvas = new Canvas(animation.getWidth(), animation.getHeight());
        canvas.getGraphicsContext2D().drawImage(animation.getAnimationStage(0), 0, 0);
        this.getChildren().add(canvas);
        this.setWidth(canvas.getWidth());
        this.setHeight(canvas.getHeight());
        this.relocate(x, y);
        canvas.getGraphicsContext2D().setStroke(Color.WHITE);
        canvas.getGraphicsContext2D().strokeLine(0, 0, getWidth(), getHeight());

        bypassPoints = new ArrayList<>();
        bypassPoints.add(new Vector2D(getLayoutX() - 50, getLayoutY() + 30 + getHeight() / 2));
        bypassPoints.add(new Vector2D(getLayoutX() + getWidth() / 2, getLayoutY() + 50 + getHeight()));
        bypassPoints.add(new Vector2D(getLayoutX() + 30 + getWidth(), getLayoutY() + 50 + getHeight() / 2));
        bypassPoints.add(new Vector2D(getLayoutX() + getWidth() / 2, getLayoutY() + 50));
        canvas.getGraphicsContext2D().setFill(Color.WHITE);
        canvas.getGraphicsContext2D().fillRect(0, getHeight() / 2 + 30, 5, 5);
        canvas.getGraphicsContext2D().fillRect(getWidth() / 2, getHeight() - 20, 5, 5);
        canvas.getGraphicsContext2D().fillRect(getWidth() - 20, getHeight() / 2 + 30, 5, 5);
        canvas.getGraphicsContext2D().fillRect(getWidth() / 2, 50, 5, 5);
    }

    @Override
    public boolean contains(double x, double y){
       /* double bot = getHeight();
        double left = 0;
        double right = getWidth();
        double horizontalMiddle = 150.0;
        double verticalMiddle = 70.0;
        double top = horizontalMiddle - (bot - horizontalMiddle);
        */
        if(getLayoutX() <= x && x <= getLayoutX() + getWidth()){
            if(getLayoutY() + 120.0 <= y && y <= getLayoutY() + getHeight()){
                return true;
            }
        }
        return false;
    }

    /*
    location of bypass points
                 3
             |--------|
          0  |building|  2
             |--------|
                 1
     */
    public ArrayList<Vector2D> getBypassPoints(Vector2D location, Vector2D target){
        //points of bypass
        ArrayList<Vector2D> path = new ArrayList<>();

        //if target-point is situated inside of building
        if(this.contains(target.x, target.y)){
            target.set(bypassPoints.get(1).x, bypassPoints.get(1).y);
            path.add(target);
            return path;
        }

        Vector2D closestPoint = Collections.min(bypassPoints, Comparator.comparingDouble(c -> Vector2D.subtract(location, c).magnitude()));
        Vector2D closestToTarget = Collections.min(bypassPoints, Comparator.comparingDouble(c -> Vector2D.subtract(target, c).magnitude()));

        //if zombie must bypasses only one corner of building (see the scheme above)
        if(closestPoint == closestToTarget){
            path.add(closestPoint);
            return path;
        }

        //first point of bypass
        path.add(closestPoint);
        //next code chooses middle point of bypass
        int bypassSide = bypassPoints.indexOf(closestPoint);
        //if zombie has come from left or right side of building (see the scheme above)
        if(bypassSide == 0 || bypassSide == 2){
            if(Vector2D.subtract(target, bypassPoints.get(1)).magnitude() <= Vector2D.subtract(target, bypassPoints.get(3)).magnitude()){
                path.add(bypassPoints.get(1));
            } else {
                path.add(bypassPoints.get(3));
            }
        //if zombie has come from top or bottom sie of building (see the scheme above)
        } else {
            if(Vector2D.subtract(target, bypassPoints.get(0)).magnitude() <= Vector2D.subtract(target, bypassPoints.get(2)).magnitude()){
                path.add(bypassPoints.get(0));
            } else {
                path.add(bypassPoints.get(2));
            }
        }
        return path;
    }
}
