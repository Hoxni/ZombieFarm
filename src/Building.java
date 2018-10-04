import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Building extends Pane implements Barrier{
    protected final double OFFSET = 120;
    protected ArrayList<Vector2D> rectPoints;
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

        rectPoints = new ArrayList<>();
        /*rectPoints.add(new Vector2D(getLayoutX(), getLayoutY() + 100));
        rectPoints.add(new Vector2D(getLayoutX(), getLayoutY() + getHeight()));
        rectPoints.add(new Vector2D(getLayoutX() + getWidth(), getLayoutY() + getHeight()));
        rectPoints.add(new Vector2D(getLayoutX() + getWidth(), getLayoutY() + 100));*/

        bypassPoints = new ArrayList<>();
        bypassPoints.add(new Vector2D(getLayoutX(), getLayoutY() + 145));
        bypassPoints.add(new Vector2D(getLayoutX() + 70, getLayoutY() + getHeight()));
        bypassPoints.add(new Vector2D(getLayoutX() + getWidth(), getLayoutY() + 145));
        bypassPoints.add(new Vector2D(getLayoutX() + 70, getLayoutY() + 100));

        rectPoints = new ArrayList<>(bypassPoints);
    }

    public double getX(){
        return rectPoints.get(0).x;
    }

    public double getY(){
        return rectPoints.get(0).y;
    }

    public ArrayList<Vector2D> getRectPoints(){
        return rectPoints;
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
            if(getLayoutY() + 100 <= y && y <= getLayoutY() + getHeight()){
                return true;
            }
        }
        return false;
    }

    /*
    location of bypass points

             +-------3------+
             |              |
             0   building   2
             |              |
             +-------1----- +

     */
    //Need some fixes
    public List<Vector2D> getBypassPoints(Vector2D location, Vector2D target, List<Vector2D> intersectionPoints){

        //if target-point is situated inside of building
        if(Barrier.isPointInsidePoly(target, rectPoints)){
            intersectionPoints.addAll(Barrier.getIntersectionPoints(target, rectPoints.get(0), rectPoints));
            target.set(rectPoints.get(0).x, rectPoints.get(0).y);
            if(Barrier.isPointInsidePoly(location, rectPoints)){
                return new ArrayList<>();
            }
        }

        Vector2D start = Collections.min(intersectionPoints, Comparator.comparingDouble(c -> Vector2D.subtract(location, c).magnitude()));
        Vector2D fin = Collections.min(intersectionPoints, Comparator.comparingDouble(c -> Vector2D.subtract(target, c).magnitude()));

        //points of bypass
        List<Vector2D> path = new ArrayList<>();
        path.add(start);

        Vector2D closestToLocation = Collections.min(bypassPoints, Comparator.comparingDouble(c -> Vector2D.subtract(start, c).magnitude()));
        Vector2D closestToTarget = Collections.min(bypassPoints, Comparator.comparingDouble(c -> Vector2D.subtract(fin, c).magnitude()));

        //if zombie must bypasses only one corner of building (see the scheme above)
        if(closestToLocation == closestToTarget){
            path.add(closestToLocation);
            path.add(intersectionPoints.get(0));
            return path;
        }

        //first point of bypass
        path.add(closestToLocation);

        //next code chooses middle point of bypass
        int bypassSide = bypassPoints.indexOf(closestToTarget);

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
        path.add(fin);

        return path;
    }
}
