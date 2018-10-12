import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Building extends Pane implements Obstruction{
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

    @Override
    public boolean contains(double x, double y){

        if(getLayoutX() <= x && x <= getLayoutX() + getWidth()){
            if(getLayoutY() + 100 <= y && y <= getLayoutY() + getHeight()){
                return true;
            }
        }
        return false;
    }

    /*


    building is represented as a rhombus
    it has 4 corner points and 4 edges

    designation:
        |building - image of building
        |0, 1, 2, 3 - indexes of points
        |0e, 1e, 2e, 3e - indexes of edges

             scheme

              + 3 +
      3e->  +       +  <-2e
          +           +
         0   building  2
          +           +
      0e->  +       +   <-1e
              + 1 +



     */

    //can be optimized rewriting "getIntersectionPoints" in "Obstruction" interface
    //for example "getIntersectionPoints" can return intersected edges
    //or replace "getIntersectionPoints" with "isIntersected" method
    @Override
    public List<Vector2D> getBypass(Vector2D location, Vector2D target, List<Vector2D> intersectionPoints){

        //if target-point is situated inside of building
        if(intersectionPoints.size() == 1){
            Vector2D v = intersectionPoints.get(0);
            target.set(v.x, v.y);
            return intersectionPoints;
        }

        Vector2D firstIntersection = Collections.min(intersectionPoints, Comparator.comparingDouble(c -> Vector2D.subtract(location, c).magnitude()));
        Vector2D secondIntersection = Collections.min(intersectionPoints, Comparator.comparingDouble(c -> Vector2D.subtract(target, c).magnitude()));

        //points of bypass
        List<Vector2D> path = new ArrayList<>();
        path.add(firstIntersection);

        //find edges which have intersection with path-line
        //this code duplicates "getIntersectionPoints" and can be optimized as described above
        List<Integer> intersectedEdges = new ArrayList<>();
        for(int i = 0; i < rectPoints.size(); i++){
            int next = (i + 1 == rectPoints.size()) ? 0 : i + 1;

            Vector2D ip = Obstruction.getIntersectionPoint(location, target, rectPoints.get(i), rectPoints.get(next));

            if(ip != null){
                intersectionPoints.add(ip);
                intersectedEdges.add(i);
            }
        }

        //if line intersects two adjacent edges
        if(Math.abs((intersectedEdges.get(0) - intersectedEdges.get(1)) % 2) == 1){
            int min = Math.min(intersectedEdges.get(0), intersectedEdges.get(1));
            int index = Math.abs(intersectedEdges.get(0) - intersectedEdges.get(1)) % 3 + min;
            path.add(rectPoints.get(index));
            path.add(secondIntersection);
            return path;
        }

        //if line intersects two opposite edges
        //find second bypass point
        Vector2D closestToSecond = Collections.min(bypassPoints, Comparator.comparingDouble(c -> Vector2D.subtract(secondIntersection, c).magnitude()));
        int a = intersectedEdges.get(0); //first intersected edge
        int c = rectPoints.indexOf(closestToSecond); //index of closest corner-point for secondIntersection-point
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
        Vector2D p = rectPoints.get(index);
        //yes, second bypass point is calculating earlier than first

        path.add(p);
        path.add(closestToSecond);
        path.add(secondIntersection);

        return path;
    }

    @Override
    public List<Vector2D> getCornerPoints(){
        return rectPoints;
    }

    @Override
    public Vector2D getCenter(){
        return new Vector2D(rectPoints.get(1).x, rectPoints.get(0).y);
    }

    @Override
    public double getLayer(){
        return getTranslateZ();
    }
}