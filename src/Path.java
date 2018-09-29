import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Path{
    public ArrayList<Vector2D> points;

    public double radius;

    public Path(Canvas canvas){
        points = new ArrayList<>();
        points.add(new Vector2D(340, 350));
        points.add(new Vector2D(270, 390));
        points.add(new Vector2D(200, 350));

        canvas.getGraphicsContext2D().setStroke(Color.WHITE);
        canvas.getGraphicsContext2D().strokeLine(340, 350, 270, 390);

        canvas.getGraphicsContext2D().strokeLine(270, 390, 200, 350);


        radius = 0.2;
    }
}
