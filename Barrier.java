public interface Barrier{
    int OUT_LEFT = 1;
    int OUT_TOP = 2;
    int OUT_RIGHT = 4;
    int OUT_BOTTOM = 8;

    private static int outcode(double pX, double pY, double rectX, double rectY, double rectWidth, double rectHeight) {
        int out = 0;
        if (rectWidth <= 0) {
            out |= OUT_LEFT | OUT_RIGHT;
        } else if (pX < rectX) {
            out |= OUT_LEFT;
        } else if (pX > rectX + rectWidth) {
            out |= OUT_RIGHT;
        }
        if (rectHeight <= 0) {
            out |= OUT_TOP | OUT_BOTTOM;
        } else if (pY < rectY) {
            out |= OUT_TOP;
        } else if (pY > rectY + rectHeight) {
            out |= OUT_BOTTOM;
        }
        return out;
    }

    static boolean intersectsLine(double lineX1, double lineY1, double lineX2, double lineY2, double rectX, double rectY, double rectWidth, double rectHeight) {
        int out1, out2;
        if ((out2 = outcode(lineX2, lineY2, rectX, rectY, rectWidth, rectHeight)) == 0) {
            return true;
        }
        while ((out1 = outcode(lineX1, lineY1, rectX, rectY, rectWidth, rectHeight)) != 0) {
            if ((out1 & out2) != 0) {
                return false;
            }
            if ((out1 & (OUT_LEFT | OUT_RIGHT)) != 0) {
                double x = rectX;
                if ((out1 & OUT_RIGHT) != 0) {
                    x += rectWidth;
                }
                lineY1 = lineY1 + (x - lineX1) * (lineY2 - lineY1) / (lineX2 - lineX1);
                lineX1 = x;
            } else {
                double y = rectY;
                if ((out1 & OUT_BOTTOM) != 0) {
                    y += rectHeight;
                }
                lineX1 = lineX1 + (y - lineY1) * (lineX2 - lineX1) / (lineY2 - lineY1);
                lineY1 = y;
            }
        }
        return true;
    }
}