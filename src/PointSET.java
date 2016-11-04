import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by User on 10/30/2016.
 */
public class PointSET {
    private TreeSet<Point2D> pointBinaryTree;

    // construct an empty set of points
    public PointSET(){
        pointBinaryTree = new TreeSet<Point2D>();
    }

    // is the set empty?
    public  boolean isEmpty(){
        return pointBinaryTree.isEmpty();
    }

    // number of points in the set
    public int size(){
        return pointBinaryTree.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p){

        //may not even need this contains
        if(!pointBinaryTree.contains(p)){
            pointBinaryTree.add(p);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p)      {
        return  pointBinaryTree.contains(p);
    }

    // draw all points to standard draw
    public void draw(){

        //might not even need this clear
        StdDraw.clear();
        StdDraw.setPenRadius(0.01);
        for (Point2D point: pointBinaryTree) {
            point.draw();
        }
        StdDraw.show();
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect){

        //get all points in range (inclusive)
        Iterable<Point2D>  pointsInRange = pointBinaryTree.subSet(new Point2D(rect.xmin(), rect.ymin()), true, new Point2D(rect.xmax(), rect.ymax()), true);
        return pointsInRange;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
//    public Point2D nearest(Point2D p) {
//        if(pointBinaryTree.isEmpty()){
//            return null;
//        }
//
//        Point2D ceilingPoint  = pointBinaryTree.ceiling(p); //returns first point greater than OR EQUAL TO current point
//        Point2D lowerPoint = pointBinaryTree.lower(p); //returns first point strictly less than current point
//
//        Double closestPointDistance = Double.POSITIVE_INFINITY;
//        Point2D closestNeighbor = null;
//
//        if(ceilingPoint != null){
//            closestPointDistance = ceilingPoint.distanceTo(p);
//            closestNeighbor = ceilingPoint;
//        }
//
//        if(lowerPoint != null && lowerPoint.distanceTo(p) < closestPointDistance){
//            closestNeighbor = lowerPoint;
//        }
//
//        return closestNeighbor;
//    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if(pointBinaryTree.isEmpty()){
            return null;
        }

        Double closestPointDistance = Double.POSITIVE_INFINITY;
        Point2D closestNeighbor = null;

        for (Point2D point: pointBinaryTree) {
            if(point.distanceTo(p) < closestPointDistance){
                closestPointDistance = point.distanceTo(p);
                closestNeighbor = point;
            }
        }

        return closestNeighbor;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args){

    }
}
