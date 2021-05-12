import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.awt.Point;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IouCalculatorTest {

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testValidOutputBoundary() {
        IouCalculator object = new IouCalculator();
        Point leftTop = new Point(0, 10);
        Point rightBottom = new Point(10, 0);

        double iou = object.calculateIOU(leftTop, rightBottom);

        assertTrue((iou >= 0) && (iou <= 1));
    }

    @Test
    public void testPerfectPredict() {
        IouCalculator object = new IouCalculator();
        Point leftTop = new Point(10, 20);
        Point rightBottom = new Point(20, 10);

        double iou = object.calculateIOU(leftTop, rightBottom);

        assertEquals(1, iou);
    }

    @ParameterizedTest
    @MethodSource("providePartiallyOverlappedRectangle")
    public void testPartiallyOverlapped(Point leftTop, Point rightBottom, double expectedIou) {
        IouCalculator object = new IouCalculator();

        double iou = object.calculateIOU(leftTop, rightBottom);

        assertEquals(expectedIou, iou);
    }

    private static Stream<Arguments> providePartiallyOverlappedRectangle() {
        return Stream.of(
                Arguments.of(new Point(8, 22), new Point(18, 12), 64.0/136.0), // Predict partially cover Truth
                Arguments.of(new Point(12, 18), new Point(18, 12), 36.0/100.0), // Truth fully cover Predict
                Arguments.of(new Point(8, 22), new Point(22, 8), 100.0/196.0) // Predict fully cover Truth
        );
    }

    @ParameterizedTest
    @MethodSource("provideNoOverlappedRectangle")
    public void testNoOverlapped(Point leftTop, Point rightBottom) {
        IouCalculator object = new IouCalculator();

        double iou = object.calculateIOU(leftTop, rightBottom);

        assertEquals(0, iou);
    }

    private static Stream<Arguments> provideNoOverlappedRectangle() {
        return Stream.of(
                Arguments.of(new Point(0, 1), new Point(1, 0)), // Completely not matched
                Arguments.of(new Point(15, 15), new Point(15, 15)), // Zero area in Truth
                Arguments.of(new Point(0, 20), new Point(10, 10)), // Predict stick left side of Truth
                Arguments.of(new Point(10, 30), new Point(20, 20)), // Predict stick top of Truth
                Arguments.of(new Point(20, 20), new Point(30, 10)), // Predict stick right side of Truth
                Arguments.of(new Point(10, 10), new Point(20, 0)) // Predict stick bottom of Truth
        );
    }

    @ParameterizedTest
    @MethodSource("provideWrongLeftToRightBottom")
    public void testWrongLeftTopRightBottom(Point leftTop, Point rightBottom, double expectedIou) {
        IouCalculator object = new IouCalculator();

        double iou = object.calculateIOU(leftTop, rightBottom);

        assertEquals(expectedIou, iou);
    }

    private static Stream<Arguments> provideWrongLeftToRightBottom() {
        return Stream.of(
                Arguments.of(new Point(18, 22), new Point(8, 12), 64.0/136.0), // RightTop, LeftBottom points
                Arguments.of(new Point(8, 12), new Point(18, 22), 64.0/136.0), // LeftBottom, RightTop points
                Arguments.of(new Point(18, 12), new Point(8, 22), 64.0/136.0) // RightBottom, LeftTop points
        );
    }

    @Test
    public void testNullPoints() {
        IouCalculator object = new IouCalculator();
        Point point = new Point(10, 20);

        assertThrows(IllegalArgumentException.class, () -> object.calculateIOU(point, null));
        assertThrows(IllegalArgumentException.class, () -> object.calculateIOU(null, point));
        assertThrows(IllegalArgumentException.class, () -> object.calculateIOU(point, null));
    }
}