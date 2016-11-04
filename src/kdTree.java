import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

/**
 * Created by User on 11/3/2016.
 */
public class kdTree {
    private Node root;
    private int size = 0;;

    //make this implement comparator for clarity, cant cjust use point comparators since it fails when lines is horizontal
    private class Node {
        private Point2D point;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node left;        // the left/bottom subtree
        private Node right;        // the right/top subtree
        private boolean isVertical;

        Node(){}

        Node(Point2D newPoint, Node previousNode){
            point = newPoint;

            //no preivous node, this is the root
            if(previousNode == null){
                isVertical = true; //root starts vertical
                rect = new RectHV(0.0,0.0,1.0,1.0); //root has unit square sized rectangle
            }
            else{
                //flip node axis
                isVertical = !previousNode.isVertical;

                //create new containing rectangle
                double xMin,xMax,yMin,yMax = 0.0;

                if( previousNode.isVertical){
                    //new node is horizontal, shares old vertical boundary
                    yMin = previousNode.rect.ymin();
                    yMax = previousNode.rect.ymax();
                    //point to the right or on the vertical, horizontal min changes
                    if(previousNode.comparePoint(point) > 0){
                        xMin = previousNode.point.x();
                        xMax = previousNode.rect.xmax();
                    }
                    //else point is to the left of the vertical and horizontal max changes
                    else{
                        xMin = previousNode.rect.xmin();
                        xMax = previousNode.point.x();

                    }
                }
                else{
                    //new node is vertical, shares old horizontal boundary
                    xMin = previousNode.rect.xmin();
                    xMax = previousNode.rect.xmax();
                    //point above or on the horizontal
                    if(previousNode.comparePoint(point)> 0){
                        yMin = previousNode.point.y();
                        yMax = previousNode.rect.ymax();
                    }
                    else{
                        yMin = previousNode.rect.ymin();
                        yMax = previousNode.point.y();
                    }
                }

                rect = new RectHV(xMin,yMin,xMax,yMax);
            }
        }

        public int comparePoint(Point2D otherPoint){
            if(point.x() == otherPoint.x() && point.y() == otherPoint.y()){
                return 0;
            }
            if(isVertical){
                //if current node is vertical, see if point falls to left or right of vertical
                if(point.x() >= otherPoint.x()){
                    return 1;
                }
                else{
                    return -1;
                }
            }
            else{
                if(point.y() >= otherPoint.y()){
                    return 1;
                }
                else{
                    return -1;
                }

            }

        }
    }

    public kdTree(){}

    public void put(Point2D newPoint){
        if(newPoint == null){
            throw new java.lang.NullPointerException("argument to put() is null");
        }
        //recursively move down tree to add new node and reset root to point at new tree
        root = put(root,newPoint, null);
    }

    private Node put(Node currentNode, Point2D newPoint, Node previousNode) {
        if(currentNode == null){
            size++;
            return new Node(newPoint, previousNode);
        } //terminate recursion by adding new node to the bottom

        //otherwsie recur down the correct branch based on point
        if(currentNode.comparePoint(newPoint) < 0){
            currentNode.left = put(currentNode.left, newPoint, currentNode);
        }
        if(currentNode.comparePoint(newPoint) > 0){
            currentNode.right = put(currentNode.right, newPoint, currentNode);
        }

        //else points are equal, no need to change node's current point
        return currentNode;
    }

    public boolean contains(Point2D searchPoint){
        if(searchPoint == null){
            throw new IllegalArgumentException("argument to contains() is null");
        }

        return contains(root,searchPoint);
    }

    private boolean contains(Node currentNode, Point2D searchPoint){
        if(currentNode == null){
            return false;
        } //terminate recursion when point is not found

        //otherwsie recur down the correct branch based on point

        if(currentNode.comparePoint(searchPoint) < 0){
            return contains(currentNode.left, searchPoint);
        }
        if(currentNode.comparePoint(searchPoint) > 0){
            return contains(currentNode.right, searchPoint);
        }
        //else points are equal and terminate recursion
        return true;
    }


    public Point2D nearest(Point2D searchPoint){
        if(searchPoint == null){
            throw new java.lang.NullPointerException("argument to nearest() is null");
        }
        if(isEmpty()){
            //not eaxactly sure what to throw here
            throw new java.lang.NullPointerException("tree is empty for nearest()!");

        }

        Point2D closestPoint = root.point;

        return nearest(root, searchPoint, closestPoint);
    }

    //might not need distance param
    private Point2D nearest(Node currentNode, Point2D searchPoint, Point2D closestPoint){
        if(currentNode == null){
            return closestPoint;
        } //terminate recursion when we can't go down the tree anymore, return closest point so far

        if(currentNode.comparePoint(searchPoint) == 0){
            //else points are equal and terminate recursion
            closestPoint = searchPoint;
            return closestPoint;
        }

        Double closestDistance = closestPoint.distanceTo(searchPoint);

        //current node is new closest point
        if(currentNode.point.distanceTo(searchPoint) < closestDistance){
            closestPoint = currentNode.point;
            closestDistance = closestPoint.distanceTo(searchPoint);
        }

        //otherwsie recur down the correct branch based on point
        Node firstNode = currentNode.right;
        Node secondNode = currentNode.left;

        //search left node first if point is "smaller" than point at current node
        if(currentNode.comparePoint(searchPoint) < 0 ){
            firstNode = currentNode.left;
            secondNode = currentNode.right;
        }

        //only check down a subtree if a straight line to the containing rectangle is smaller than the closest distance found so far
        if(firstNode  !=null && firstNode.rect.distanceSquaredTo(searchPoint) < closestDistance){
            closestPoint = nearest(firstNode , searchPoint, closestPoint);
            closestDistance = closestPoint.distanceTo(searchPoint);
        }
        if(secondNode != null && secondNode.rect.distanceSquaredTo(searchPoint) < closestDistance){
            closestPoint = nearest(secondNode, searchPoint, closestPoint);
        }


        return closestPoint;
    }



    public Iterable<Point2D> range(RectHV rect){
        if(rect == null){
            throw new java.lang.NullPointerException("argument to nearest() is null");
        }
        if(isEmpty()){
            //not exactly sure what to throw here
            throw new java.lang.NullPointerException("tree is empty for nearest()!");

        }
        Stack<Point2D> pointsInRange = new Stack<Point2D>();


        return range(root, rect, pointsInRange);
    }

    //might not need distance param
    private Stack<Point2D> range(Node currentNode, RectHV queryRectangle,Stack<Point2D> pointsInRange){
        if(currentNode == null){
            return pointsInRange;
        } //terminate recursion when we can't go down the tree anymore, return points in range

        if(queryRectangle.contains(currentNode.point)){
            //query rectangle contains this point, add it to the list
            pointsInRange.push(currentNode.point);
        }

        //otherwsie recur down the correct branch based on point
        Node firstNode = currentNode.right;
        Node secondNode = currentNode.left;

        //only check down a subtree if that tree's rectangle intersects the query rectangle
        if(firstNode  !=null && firstNode.rect.intersects(queryRectangle)){
            pointsInRange = range(firstNode , queryRectangle, pointsInRange);
        }
        if(secondNode != null && secondNode.rect.intersects(queryRectangle)){
            pointsInRange = range(secondNode, queryRectangle, pointsInRange);
        }

        //recursion finished, return resulting points in range
        return pointsInRange;
    }

    public int size(){
        return size;
    }

    public boolean isEmpty(){
        return size() == 0;
    }

    public void draw(){
        if(isEmpty()){
            return;
        }

        draw(root);

    }

    private void draw(Node currentNode){
        if(currentNode == null){
            return;
        }

        StdDraw.setPenRadius(0.01);
        currentNode.point.draw();

        StdDraw.setPenRadius();
        double xMin,xMax,yMin,yMax = 0.0;

        if(currentNode.isVertical){
            StdDraw.setPenColor(StdDraw.RED);
            xMin = currentNode.point.x();
            xMax = currentNode.point.x();
            yMin = currentNode.rect.ymin();
            yMax = currentNode.rect.ymax();
        }
        else{
            StdDraw.setPenColor(StdDraw.BLUE);
            xMin = currentNode.rect.xmin();
            xMax = currentNode.rect.xmax();
            yMin = currentNode.point.y();
            yMax = currentNode.point.y();
        }

        //create and draw new segment
        StdDraw.line(xMin,yMin,xMax,yMax);

        //reset pen color
        StdDraw.setPenColor(StdDraw.BLACK);

        //recur down subtrees
        draw(currentNode.left);
        draw(currentNode.right);

    }

}
