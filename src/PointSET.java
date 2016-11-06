import edu.princeton.cs.algs4.*;

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
        if(p == null){
            throw new NullPointerException();
        }

        //may not even need this contains
        if(!pointBinaryTree.contains(p)){
            pointBinaryTree.add(p);
        }
    }

    // does the set contain point p?
    public boolean contains(Point2D p)      {
        if(p == null){
            throw new NullPointerException();
        }
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
        if(rect == null){
            throw new NullPointerException();
        }

        //get all points in range (inclusive)
        Iterable<Point2D>  pointsInRange = pointBinaryTree.subSet(new Point2D(rect.xmin(), rect.ymin()), false, new Point2D(rect.xmax(), rect.ymax()), false);
        return pointsInRange;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if(p == null){
            throw new NullPointerException();
        }

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
        PointSET points = new PointSET();

        if(args[0] == "true") {
            //read in file and test that way
            In in = new In(args[1]);
            double[] inputPointCoordinates = in.readAllDoubles();
            Double xCoord = null;
            Double yCoord = null;

            for (double inputPointCoordinate : inputPointCoordinates) {
                if (xCoord == null) {
                    xCoord = inputPointCoordinate;
                } else if (yCoord == null) {
                    yCoord = inputPointCoordinate;
                    points.insert(new Point2D(xCoord, yCoord));
                    xCoord = null;
                    yCoord = null;
                }
            }

            StdOut.println(points.size());
            StdOut.println(points.nearest(new Point2D(0.2,0.2)));
        }
        else{
            //true
            StdOut.println(points.isEmpty());

            points.insert(new Point2D(0.5,0.5));

            //true
            StdOut.println(points.contains(new Point2D(0.5,0.5)));

            //1
            StdOut.println(points.size());

            points.insert(new Point2D(0.7,0.7));
            points.insert(new Point2D(1,1));

            //0.5,0.5
            StdOut.println(points.nearest(new Point2D(0.2,0.2)));

            //0.5,0.5 - 0.7,0.7
            Iterable<Point2D> pointsInRange = points.range(new RectHV(0.0,0.0,0.0000000001,0.0000000001));
            for (Point2D point: pointsInRange
                    ) {
                StdOut.println(point);
            }
        }

        points.draw();

    }
}
