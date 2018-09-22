import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

abstract class Clothes extends Canvas{
    protected final int
            NUMBER_OF_MODES = 6;
    protected Animation[] modes;
    protected GraphicsContext graphicsContext;
    protected int modeIndex = 0;

    public Clothes(
            String stand,
            String standXML,
            String down,
            String downXML,
            String up,
            String upXML,
            String woodDown,
            String woodDownXML,
            String woodUp,
            String woodUpXML,
            String woodCut,
            String woodCutXML){
        modes = new Animation[NUMBER_OF_MODES];
        modes[0] = new Animation(stand, standXML);
        modes[1] = new Animation(down, downXML);
        modes[2] = new Animation(up, upXML);
        modes[3] = new Animation(woodDown, woodDownXML);
        modes[4] = new Animation(woodUp, woodUpXML);
        modes[5] = new Animation(woodCut, woodCutXML);
        setWidth(modes[0].getWidth());
        setHeight(modes[0].getHeight());
        graphicsContext = getGraphicsContext2D();
    }

    public void setMode(int i){
        modeIndex = i;
    }

    public void displayStage(int i){
        graphicsContext.clearRect(0, 0,getWidth(),getHeight());
        graphicsContext.drawImage(modes[modeIndex].getAnimationStage(i), 0, 0);
    }
}
