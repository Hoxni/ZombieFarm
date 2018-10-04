import java.util.*;

public interface Barrier{
    int OUT_LEFT = 1;
    int OUT_TOP = 2;
    int OUT_RIGHT = 4;
    int OUT_BOTTOM = 8;

    //Normal code below

    private static int outcode(double pX, double pY, double rectX, double rectY, double rectWidth, double rectHeight){
        int out = 0;
        if(rectWidth <= 0){
            out |= OUT_LEFT | OUT_RIGHT;
        } else if(pX < rectX){
            out |= OUT_LEFT;
        } else if(pX > rectX + rectWidth){
            out |= OUT_RIGHT;
        }
        if(rectHeight <= 0){
            out |= OUT_TOP | OUT_BOTTOM;
        } else if(pY < rectY){
            out |= OUT_TOP;
        } else if(pY > rectY + rectHeight){
            out |= OUT_BOTTOM;
        }
        return out;
    }

    static boolean intersectsLine(Vector2D location, Vector2D target, double rectX, double rectY, double rectWidth, double rectHeight){
        double lineX1 = location.x;
        double lineY1 = location.y;
        double lineX2 = target.x;
        double lineY2 = target.y;
        int out1, out2;
        if((out2 = outcode(lineX2, lineY2, rectX, rectY, rectWidth, rectHeight)) == 0){
            return true;
        }
        while((out1 = outcode(lineX1, lineY1, rectX, rectY, rectWidth, rectHeight)) != 0){
            if((out1 & out2) != 0){
                return false;
            }
            if((out1 & (OUT_LEFT | OUT_RIGHT)) != 0){
                double x = rectX;
                if((out1 & OUT_RIGHT) != 0){
                    x += rectWidth;
                }
                lineY1 = lineY1 + (x - lineX1) * (lineY2 - lineY1) / (lineX2 - lineX1);
                lineX1 = x;
            } else {
                double y = rectY;
                if((out1 & OUT_BOTTOM) != 0){
                    y += rectHeight;
                }
                lineX1 = lineX1 + (y - lineY1) * (lineX2 - lineX1) / (lineY2 - lineY1);
                lineY1 = y;
            }
        }
        return true;
    }

    //-------------------- Normal code starts here ----------------------------------------------------------

    double EQUITY_TOLERANCE = 0.000000001d;

    private static boolean isEqual(double d1, double d2){
        return Math.abs(d1 - d2) <= EQUITY_TOLERANCE;
    }

    static Vector2D getIntersectionPoint(Vector2D l1p1, Vector2D l1p2, Vector2D l2p1, Vector2D l2p2){
        double A1 = l1p2.y - l1p1.y;
        double B1 = l1p1.x - l1p2.x;
        double C1 = A1 * l1p1.x + B1 * l1p1.y;

        double A2 = l2p2.y - l2p1.y;
        double B2 = l2p1.x - l2p2.x;
        double C2 = A2 * l2p1.x + B2 * l2p1.y;

        //lines are parallel
        double det = A1 * B2 - A2 * B1;
        if(isEqual(det, 0d)){
            return null; //parallel lines
        } else {
            double x = (B2 * C1 - B1 * C2) / det;
            double y = (A1 * C2 - A2 * C1) / det;
            boolean online1 = ((Math.min(l1p1.x, l1p2.x) < x || isEqual(Math.min(l1p1.x, l1p2.x), x))
                    && (Math.max(l1p1.x, l1p2.x) > x || isEqual(Math.max(l1p1.x, l1p2.x), x))
                    && (Math.min(l1p1.y, l1p2.y) < y || isEqual(Math.min(l1p1.y, l1p2.y), y))
                    && (Math.max(l1p1.y, l1p2.y) > y || isEqual(Math.max(l1p1.y, l1p2.y), y))
            );
            boolean online2 = ((Math.min(l2p1.x, l2p2.x) < x || isEqual(Math.min(l2p1.x, l2p2.x), x))
                    && (Math.max(l2p1.x, l2p2.x) > x || isEqual(Math.max(l2p1.x, l2p2.x), x))
                    && (Math.min(l2p1.y, l2p2.y) < y || isEqual(Math.min(l2p1.y, l2p2.y), y))
                    && (Math.max(l2p1.y, l2p2.y) > y || isEqual(Math.max(l2p1.y, l2p2.y), y))
            );

            if(online1 && online2)
                return new Vector2D(x, y);
        }
        return null; //intersection is at out of at least one segment.
    }

    static List<Vector2D> getIntersectionPoints(Vector2D l1p1, Vector2D l1p2, List<Vector2D> poly){
        Set<Vector2D> set = new TreeSet<>((o1, o2) -> {
            if(Math.abs(o1.x - o2.x) <= EQUITY_TOLERANCE && Math.abs(o1.y - o2.y) <= EQUITY_TOLERANCE)
                return 0;
            else return 1;
        });
        for(int i = 0; i < poly.size(); i++){

            int next = (i + 1 == poly.size()) ? 0 : i + 1;

            Vector2D ip = getIntersectionPoint(l1p1, l1p2, poly.get(i), poly.get(next));

            if(ip != null) set.add(ip);

        }

        return new ArrayList<>(set);
    }

    static boolean isPointInsidePoly(Vector2D test, List<Vector2D> poly)
    {
        int i;
        int j;
        boolean result = false;
        for (i = 0, j = poly.size() - 1; i < poly.size(); j = i++)
        {
            if ((poly.get(i).y > test.y) != (poly.get(j).y > test.y) &&
                    (test.x < (poly.get(j).x - poly.get(i).x) * (test.y - poly.get(i).y) / (poly.get(j).y - poly.get(i).y) + poly.get(i).x))
            {
                result = true;
            }
        }
        return result;
    }
}
