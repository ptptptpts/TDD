import java.awt.Point;

import static java.lang.Integer.max;
import static java.lang.Integer.min;

public class IouCalculator {
    Point truthLeftTop = new Point(10, 20);
    Point truthRightBottom = new Point(20, 10);

    public IouCalculator() {
    }

    public double calculateIOU(Point predictPointA, Point predictPointB) throws IllegalArgumentException {
        if ((predictPointA == null) || (predictPointB == null)) {
            throw new IllegalArgumentException();
        } else {
            return findIou(predictPointA, predictPointB);
        }
    }

    private double findIou(Point predictPointA, Point predictPointB) {
        Point predictLeftTop = findLeftTopPoint(predictPointA, predictPointB);
        Point predictRightBottom = findRightBottomPoint(predictPointA, predictPointB);

        int overlapArea = getOverlapRectangleArea(predictLeftTop, predictRightBottom);
        int truthArea = getRectangleArea(truthLeftTop, truthRightBottom);
        int predictArea = getRectangleArea(predictLeftTop, predictRightBottom);

        return (double)overlapArea / (double) getUnionArea(truthArea, predictArea, overlapArea);
    }

    private int getOverlapRectangleArea(Point predictLeftTop, Point predictRightBottom) {
        int area = 0;

        if (isOverlapped(predictLeftTop, predictRightBottom)) {
            Point overlapLeftTop = findRightBottomPoint(predictLeftTop, truthLeftTop);
            Point overlapRightBottom = findLeftTopPoint(predictRightBottom, truthRightBottom);

            area = getRectangleArea(overlapLeftTop, overlapRightBottom);
        }

        return area;
    }

    private boolean isOverlapped(Point predictLeftTop, Point predictRightBottom) {
        return !((predictLeftTop.x >= truthRightBottom.x)
                || (predictLeftTop.y <= truthRightBottom.y)
                || (predictRightBottom.x <= truthLeftTop.x)
                || (predictRightBottom.y >= truthLeftTop.y));
    }

    private Point findRightBottomPoint(Point pointA, Point pointB) {
        int right = max(pointA.x, pointB.x);
        int bottom = min(pointA.y, pointB.y);

        return new Point(right, bottom);
    }

    private Point findLeftTopPoint(Point pointA, Point pointB) {
        int left = min(pointA.x, pointB.x);
        int top = max(pointA.y, pointB.y);

        return new Point(left, top);
    }

    private int getRectangleArea(Point overlapLeftTop, Point overlapRightBottom) {
        return (overlapRightBottom.x - overlapLeftTop.x) * (overlapLeftTop.y - overlapRightBottom.y);
    }

    private int getUnionArea(int truthArea, int predictArea, int overlapArea) {
        return truthArea + predictArea - overlapArea;
    }
}
