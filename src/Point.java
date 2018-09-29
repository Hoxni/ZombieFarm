import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.util.Duration;

public class Point extends Canvas{
    protected Animation point;
    protected Timeline timeline;
    protected final double
            WIDTH = 66,
            HEIGHT = 35,
            DURATION = 130;
    protected int stage = 0;

    public Point(){
        point = new Animation(Paths.WHITE_WAVE, Paths.WHITE_WAVE_XML);
        point.setWidth(WIDTH);
        point.setHeight(HEIGHT);
        this.setWidth(WIDTH);
        this.setHeight(HEIGHT);
        timeline = new Timeline(new KeyFrame(Duration.millis(DURATION), event -> {
            GraphicsContext gc = this.getGraphicsContext2D();
            gc.clearRect(0, 0, this.getWidth(), this.getHeight());
            if(stage >= point.getLength()) stage = 0;
            gc.drawImage(point.getAnimationStage(stage), 0, 0);
            stage++;
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
    }

    public void start(double x, double y){
        relocate(x - getWidth()/2.0, y - getHeight()/2.0);
        timeline.play();
    }

    public void stop(){
        timeline.stop();
        stage = 0;
        this.getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
    }
}
