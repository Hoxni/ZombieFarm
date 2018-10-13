import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Animation{
    protected final Image[] animation;
    protected double width = 267;
    protected double height = 200;
    public Animation(String imagePath, String xmlPath){
        animation = getAnimationStages(imagePath, xmlPath);
        setWidth(animation[0].getWidth());
        setHeight(animation[0].getHeight());
    }
    protected Image[] getAnimationStages(String imagePath, String xmlPath){
        Image[] animation = null;
        try{
            Image zombie = new Image(new File(imagePath).toURI().toString());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlPath));
            NodeList nodeList = document.getElementsByTagName("Frame");
            animation = new WritableImage[nodeList.getLength()];
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    int
                            x = Integer.parseInt(element.getAttribute("x")),
                            width = Integer.parseInt(element.getAttribute("width")),
                            height = Integer.parseInt(element.getAttribute("height"));
                    animation[i] = new WritableImage(zombie.getPixelReader(), x, 0, width, height);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return animation;
    }

    public Image getAnimationStage(int i){
        return animation[i];
    }

    public int getLength(){
        return animation.length;
    }

    public double getWidth(){
        return width;
    }

    public double getHeight(){
        return height;
    }

    public void setWidth(double width){
        this.width = width;
    }

    public void setHeight(double height){
        this.height = height;
    }
}
