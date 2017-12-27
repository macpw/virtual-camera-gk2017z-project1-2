package com.mycompany.virtual_camera.model;

import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

/**
 *
 * @author Paweł Mac
 */
public class ViewportModel extends Observable {
    
    private static final int 
            MOVE_FORWARD       = 0,
            MOVE_BACKWARD      = 1,
            MOVE_LEFT          = 2,
            MOVE_RIGHT         = 3,
            MOVE_UPWARD        = 4,
            MOVE_DOWNWARD      = 5,
            ROTATE_LEFT        = 6,
            ROTATE_RIGHT       = 7,
            ROTATE_UPWARD      = 8,
            ROTATE_DOWNWARD    = 9,
            ROTATE_TILT_LEFT   = 10,
            ROTATE_TILT_RIGHT  = 11,
            NUMBER_OF_MATRICES = 12;
    
    private Set<Point3D> point3DsSet;
    private Set<Edge3D> edge3DsSet;
    private double distanceBetweenObserverAndViewport;
    private final int viewportWidth;
    private final int viewportHeight;
    
    private Map<Edge3D, Line2DHolder> edge3DToLine2DHolderMap;
    
    private double step = 10.0d;
    private double angleInDegrees = 1.0d;
    private final RealMatrix[] geometricTransformationMatrices = new RealMatrix[NUMBER_OF_MATRICES];
    
    public ViewportModel(Set<Point3D> point3DsSet, Set<Edge3D> edge3DsSet, double distanceBetweenObserverAndViewport, int viewportWidth, int viewportHeight) {
        this.point3DsSet = point3DsSet;
        this.edge3DsSet = edge3DsSet;
        this.distanceBetweenObserverAndViewport = distanceBetweenObserverAndViewport;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        
        this.edge3DToLine2DHolderMap = new HashMap<>();
        this.initEdge3DToLine2DHolderMap();
        this.initGeometricTransformationMatrices();
    }
    
    // Getters and Setters
    
    public double getDistanceBetweenObserverAndViewport() {
        return distanceBetweenObserverAndViewport;
    }
    
    public void setDistanceBetweenObserverAndViewport(double distanceBetweenObserverAndViewport) {
        this.distanceBetweenObserverAndViewport = distanceBetweenObserverAndViewport;
        //TODO
    }
    
    public double getStep() {
        return step;
    }
    
    public void setStep(double step) {
        this.step = step;
        //TODO
    }
    
    public double getAngleInDegrees() {
        return angleInDegrees;
    }
    
    public void setAngleInDegrees(double angleInDegrees) {
        this.angleInDegrees = angleInDegrees;
        //TODO
    }
    
    // Getters
    
    public int getViewportWidth() {
        return viewportWidth;
    }
    
    public int getViewportHeight() {
        return viewportHeight;
    }
    
    public Collection<Line2DHolder> getCollectionOfLine2DHolder() {
        return edge3DToLine2DHolderMap.values();
    }
    
    // Methods
    
    private void initGeometricTransformationMatrices() {
        /****************
        * |1, 0, 0, Tx| *
        * |0, 1, 0, Ty| *
        * |0, 0, 1, Tz| *
        * |0, 0, 0,  1| *
        *****************/
        geometricTransformationMatrices[MOVE_FORWARD] = MatrixUtils.createRealMatrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, -step},
            {0, 0, 0, 1},
        });
        geometricTransformationMatrices[MOVE_BACKWARD] = MatrixUtils.createRealMatrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, 0},
            {0, 0, 1, +step},
            {0, 0, 0, 1},
        });
        geometricTransformationMatrices[MOVE_LEFT] = MatrixUtils.createRealMatrix(new double[][]{
            {1, 0, 0, +step},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1},
        });
        geometricTransformationMatrices[MOVE_RIGHT] = MatrixUtils.createRealMatrix(new double[][]{
            {1, 0, 0, -step},
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1},
        });
        geometricTransformationMatrices[MOVE_UPWARD] = MatrixUtils.createRealMatrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, -step},
            {0, 0, 1, 0},
            {0, 0, 0, 1},
        });
        geometricTransformationMatrices[MOVE_DOWNWARD] = MatrixUtils.createRealMatrix(new double[][]{
            {1, 0, 0, 0},
            {0, 1, 0, +step},
            {0, 0, 1, 0},
            {0, 0, 0, 1},
        });
        
        double angleInRadians = Math.toRadians(angleInDegrees);
        
        geometricTransformationMatrices[ROTATE_LEFT] = MatrixUtils.createRealMatrix(new double[][]{
            { Math.cos(angleInRadians), 0, Math.sin(angleInRadians), 0},
            {                        0, 1,                        0, 0},
            {-Math.sin(angleInRadians), 0, Math.cos(angleInRadians), 0},
            {                        0, 0,                        0, 1},
        });
        geometricTransformationMatrices[ROTATE_RIGHT] = MatrixUtils.createRealMatrix(new double[][]{
            { Math.cos(-angleInRadians), 0, Math.sin(-angleInRadians), 0},
            {                         0, 1,                         0, 0},
            {-Math.sin(-angleInRadians), 0, Math.cos(-angleInRadians), 0},
            {                         0, 0,                         0, 1},
        });
        geometricTransformationMatrices[ROTATE_UPWARD] = MatrixUtils.createRealMatrix(new double[][]{
            { 1,                        0,                         0, 0},
            { 0, Math.cos(angleInRadians), -Math.sin(angleInRadians), 0},
            { 0, Math.sin(angleInRadians),  Math.cos(angleInRadians), 0},
            { 0,                        0,                         0, 1},
        });
        geometricTransformationMatrices[ROTATE_DOWNWARD] = MatrixUtils.createRealMatrix(new double[][]{
            { 1,                         0,                          0, 0},
            { 0, Math.cos(-angleInRadians), -Math.sin(-angleInRadians), 0},
            { 0, Math.sin(-angleInRadians),  Math.cos(-angleInRadians), 0},
            { 0,                         0,                          0, 1},
        });
        geometricTransformationMatrices[ROTATE_TILT_LEFT] = MatrixUtils.createRealMatrix(new double[][]{
            { Math.cos(-angleInRadians), -Math.sin(-angleInRadians), 0, 0},
            { Math.sin(-angleInRadians),  Math.cos(-angleInRadians), 0, 0},
            {                        0,                         0, 1, 0},
            {                        0,                         0, 0, 1},
        });
        geometricTransformationMatrices[ROTATE_TILT_RIGHT] = MatrixUtils.createRealMatrix(new double[][]{
            { Math.cos(angleInRadians), -Math.sin(angleInRadians), 0, 0},
            { Math.sin(angleInRadians),  Math.cos(angleInRadians), 0, 0},
            {                         0,                          0, 1, 0},
            {                         0,                          0, 0, 1},
        });
    }
    
    private void updateMoveMatrices() {
        geometricTransformationMatrices[MOVE_FORWARD] .setEntry(2, 3, -step);
        geometricTransformationMatrices[MOVE_BACKWARD].setEntry(2, 3, +step);
        geometricTransformationMatrices[MOVE_LEFT]    .setEntry(0, 3, +step);
        geometricTransformationMatrices[MOVE_RIGHT]   .setEntry(0, 3, -step);
        geometricTransformationMatrices[MOVE_UPWARD]  .setEntry(1, 3, -step);
        geometricTransformationMatrices[MOVE_DOWNWARD].setEntry(1, 3, +step);
    }
    
    private void updateRotationMatrices() {
        double angleInRadians = Math.toRadians(angleInDegrees);
        double cosPositiveAngle = Math.cos(angleInRadians);
        double sinPositiveAngle = Math.sin(angleInRadians);
        double cosNegativeAngle = Math.cos(-angleInRadians);
        double sinNegativeAngle = Math.sin(-angleInRadians);
        geometricTransformationMatrices[ROTATE_LEFT].setEntry(0, 0,  cosPositiveAngle);
        geometricTransformationMatrices[ROTATE_LEFT].setEntry(0, 2,  sinPositiveAngle);
        geometricTransformationMatrices[ROTATE_LEFT].setEntry(2, 0, -sinPositiveAngle);
        geometricTransformationMatrices[ROTATE_LEFT].setEntry(2, 2,  cosPositiveAngle);
        geometricTransformationMatrices[ROTATE_RIGHT].setEntry(0, 0,  cosNegativeAngle);
        geometricTransformationMatrices[ROTATE_RIGHT].setEntry(0, 2,  sinNegativeAngle);
        geometricTransformationMatrices[ROTATE_RIGHT].setEntry(2, 0, -sinNegativeAngle);
        geometricTransformationMatrices[ROTATE_RIGHT].setEntry(2, 2,  cosNegativeAngle);
        geometricTransformationMatrices[ROTATE_UPWARD].setEntry(1, 1,  cosPositiveAngle);
        geometricTransformationMatrices[ROTATE_UPWARD].setEntry(1, 2, -sinPositiveAngle);
        geometricTransformationMatrices[ROTATE_UPWARD].setEntry(2, 1,  sinPositiveAngle);
        geometricTransformationMatrices[ROTATE_UPWARD].setEntry(2, 2,  cosPositiveAngle);
        geometricTransformationMatrices[ROTATE_DOWNWARD].setEntry(1, 1,  cosNegativeAngle);
        geometricTransformationMatrices[ROTATE_DOWNWARD].setEntry(1, 2, -sinNegativeAngle);
        geometricTransformationMatrices[ROTATE_DOWNWARD].setEntry(2, 1,  sinNegativeAngle);
        geometricTransformationMatrices[ROTATE_DOWNWARD].setEntry(2, 2,  cosNegativeAngle);
        geometricTransformationMatrices[ROTATE_TILT_LEFT].setEntry(0, 0,  cosNegativeAngle);
        geometricTransformationMatrices[ROTATE_TILT_LEFT].setEntry(0, 1, -sinNegativeAngle);
        geometricTransformationMatrices[ROTATE_TILT_LEFT].setEntry(1, 0,  sinNegativeAngle);
        geometricTransformationMatrices[ROTATE_TILT_LEFT].setEntry(1, 1,  cosNegativeAngle);
        geometricTransformationMatrices[ROTATE_TILT_RIGHT].setEntry(0, 0,  cosPositiveAngle);
        geometricTransformationMatrices[ROTATE_TILT_RIGHT].setEntry(0, 1, -sinPositiveAngle);
        geometricTransformationMatrices[ROTATE_TILT_RIGHT].setEntry(1, 0,  sinPositiveAngle);
        geometricTransformationMatrices[ROTATE_TILT_RIGHT].setEntry(1, 1,  cosPositiveAngle);
    }
    
    private void initEdge3DToLine2DHolderMap() {
        for (Edge3D edge3D : edge3DsSet) {
            Line2D line2D = new Line2D.Double(0.0d, 0.0d, 0.0d, 0.0d);
            Line2DHolder line2DHolder = new Line2DHolder(line2D);
            this.calculateLine2DOnViewport(edge3D, line2DHolder);
            this.edge3DToLine2DHolderMap.put(edge3D, line2DHolder);
        }
    }
    
    private void calculateLine2DOnViewport(Edge3D edge3D, Line2DHolder line2DHolder) {
        double x1 = (edge3D.getFirst() .getX() * distanceBetweenObserverAndViewport) / edge3D.getFirst() .getZ();
        double y1 = (edge3D.getFirst() .getY() * distanceBetweenObserverAndViewport) / edge3D.getFirst() .getZ();
        double x2 = (edge3D.getSecond().getX() * distanceBetweenObserverAndViewport) / edge3D.getSecond().getZ();
        double y2 = (edge3D.getSecond().getY() * distanceBetweenObserverAndViewport) / edge3D.getSecond().getZ();
        x1 =  x1 + (viewportWidth  / 2.0d);
        y1 = -y1 + (viewportHeight / 2.0d);
        x2 =  x2 + (viewportWidth  / 2.0d);
        y2 = -y2 + (viewportHeight / 2.0d);
        line2DHolder.getLine2D().setLine(x1, y1, x2, y2);
        
        if (edge3D.getFirst().getZ() > distanceBetweenObserverAndViewport) {
            line2DHolder.setFirstInFrontOfViewport(true);
        } else {
            line2DHolder.setFirstInFrontOfViewport(false);
        }
        
        if (edge3D.getSecond().getZ() > distanceBetweenObserverAndViewport) {
            line2DHolder.setSecondInFrontOfViewport(true);
        } else {
            line2DHolder.setSecondInFrontOfViewport(false);
        }
    }
    
    private void updateEdge3DToLine2DHolderMap() {
        for (Map.Entry<Edge3D, Line2DHolder> entry : edge3DToLine2DHolderMap.entrySet()) {
            Edge3D keyEdge3D = entry.getKey();
            Line2DHolder valueLine2DHolder = entry.getValue();
            this.calculateLine2DOnViewport(keyEdge3D, valueLine2DHolder);
        }
    }
    
    // motions
    public void moveForward() {
        RealMatrix moveForwardMatrix = geometricTransformationMatrices[MOVE_FORWARD];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(moveForwardMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void moveBackward() {
        RealMatrix moveBackwardMatrix = geometricTransformationMatrices[MOVE_BACKWARD];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(moveBackwardMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void moveLeft() {
        RealMatrix moveLeftMatrix = geometricTransformationMatrices[MOVE_LEFT];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(moveLeftMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void moveRight() {
        RealMatrix moveRightMatrix = geometricTransformationMatrices[MOVE_RIGHT];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(moveRightMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void moveUpward() {
        RealMatrix moveUpwardMatrix = geometricTransformationMatrices[MOVE_UPWARD];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(moveUpwardMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void moveDownward() {
        RealMatrix moveDownwardMatrix = geometricTransformationMatrices[MOVE_DOWNWARD];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(moveDownwardMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    // rotations
    public void rotateLeft() {
        RealMatrix rotateLeftMatrix = geometricTransformationMatrices[ROTATE_LEFT];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(rotateLeftMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void rotateRight() {
        RealMatrix rotateRightMatrix = geometricTransformationMatrices[ROTATE_RIGHT];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(rotateRightMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void rotateUpward() {
        RealMatrix rotateUpwardMatrix = geometricTransformationMatrices[ROTATE_UPWARD];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(rotateUpwardMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void rotateDownward() {
        RealMatrix rotateDownwardMatrix = geometricTransformationMatrices[ROTATE_DOWNWARD];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(rotateDownwardMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void rotateTiltLeft() {
        RealMatrix rotateTiltLeftMatrix = geometricTransformationMatrices[ROTATE_TILT_LEFT];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(rotateTiltLeftMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
    public void rotateTiltRight() {
        RealMatrix rotateTiltRightMatrix = geometricTransformationMatrices[ROTATE_TILT_RIGHT];
        for (Point3D point3D : point3DsSet) {
            RealMatrix coordinates = point3D.getCoordinates();
            point3D.setCoordinates(rotateTiltRightMatrix.multiply(coordinates));
        }
        this.updateEdge3DToLine2DHolderMap();
        this.setChanged();
        this.notifyObservers();
    }
}
