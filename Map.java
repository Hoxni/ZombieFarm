import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Map extends Pane{
    protected Canvas canvas;

    public Map(String imagePath, String xmlPath){
        Image[] animation = null;
        canvas = new Canvas(3400, 2700);
        try{
            Image zombie = new Image(new File(imagePath).toURI().toString());
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(xmlPath));
            NodeList nodeList = document.getElementsByTagName("Tile");
            animation = new WritableImage[nodeList.getLength()];
            for(int i = 0; i < nodeList.getLength(); i++){
                Node node = nodeList.item(i);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    int
                            x = Integer.parseInt(element.getAttribute("x")),
                            y = Integer.parseInt(element.getAttribute("y")),
                            width = Integer.parseInt(element.getAttribute("width")),
                            height = Integer.parseInt(element.getAttribute("height"));
                    canvas.getGraphicsContext2D().drawImage(
                    new WritableImage(zombie.getPixelReader(), x, y, width, height), x, y);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
