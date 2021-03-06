import javafx.scene.canvas.Canvas;
import javafx.scene.layout.Pane;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Building extends Pane implements Obstruction{
    protected final double OFFSET = 5;
    protected final ArrayList<Vector2D> cornerPoints;

    public Building(String building, String buildingXML, double x, double y){
        Animation animation = new Animation(building, buildingXML);
        Canvas canvas = new Canvas(animation.getWidth(), animation.getHeight());
        canvas.getGraphicsContext2D().drawImage(animation.getAnimationStage(0), 0, 0);
        this.getChildren().add(canvas);
        this.setWidth(canvas.getWidth());
        this.setHeight(canvas.getHeight());
        this.relocate(x, y);

        cornerPoints = new ArrayList<>();
        cornerPoints.add(new Vector2D(getLayoutX() - OFFSET, getLayoutY() + 145));
        cornerPoints.add(new Vector2D(getLayoutX() + 70, getLayoutY() + getHeight() + OFFSET));
        cornerPoints.add(new Vector2D(getLayoutX() + getWidth() + OFFSET, getLayoutY() + 145));
        cornerPoints.add(new Vector2D(getLayoutX() + 70, getLayoutY() + 100 - OFFSET));
    }

    @Override
    public boolean contains(double x, double y){

        if(getLayoutX() <= x && x <= getLayoutX() + getWidth()){
            return getLayoutY() + 100 <= y && y <= getLayoutY() + getHeight();
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
        return new Vector2D(cornerPoints.get(1).x, cornerPoints.get(0).y);
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