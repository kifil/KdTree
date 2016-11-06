import edu.princeton.cs.algs4.*;

/**
 * Created by User on 11/3/2016.
 */

public class KdTree {
    private Node root;
    private int size = 0;

    // Node helper class to keep track of nodes in the tree
    private class Node {
        private Point2D point;      //  the point
        private RectHV rect;    //  the axis-aligned rectangle corresponding to this node
        private Node left;        //  the left/bottom subtree
        private Node right;        //  the right/top subtree
        private boolean isVertical; // whether or not the node is a vertical line

        Node() {}

        Node(Point2D newPoint, Node previousNode) {
            point = newPoint;

            // no previous node, this is the root
            if (previousNode == null) {
                isVertical = true; // root starts vertical
                rect = new RectHV(0.0,0.0,1.0,1.0); // root has unit square sized rectangle
            }
            else{
                // flip node axis
                isVertical = !previousNode.isVertical;

                // create new containing rectangle
                double xMin,xMax,yMin,yMax = 0.0;

                if ( previousNode.isVertical) {
                    // new node is horizontal, shares old vertical boundary
                    yMin = previousNode.rect.ymin();
                    yMax = previousNode.rect.ymax();
                    // point to the right or on the vertical, horizontal min changes
                    if (previousNode.comparePoint(point) < 0) {
                        xMin = previousNode.point.x();
                        xMax = previousNode.rect.xmax();
                    }
                    // else point is to the left of the vertical and horizontal max changes
                    else{
                        xMin = previousNode.rect.xmin();
                        xMax = previousNode.point.x();

                    }
                }
                else{
                    // new node is vertical, shares old horizontal boundary
                    xMin = previousNode.rect.xmin();
                    xMax = previousNode.rect.xmax();
                    // point above or on the horizontal
                    if (previousNode.comparePoint(point) < 0) {
                        yMin = previousNode.point.y();
                        yMax = previousNode.rect.ymax();
                    }
                    else {
                        yMin = previousNode.rect.ymin();
                        yMax = previousNode.point.y();
                    }
                }

                rect = new RectHV(xMin,yMin,xMax,yMax);
            }
        }

        public int comparePoint(Point2D otherPoint) {
            if (point.equals(otherPoint)) {
                return 0;
            }
            if (isVertical) {
                // if current node is vertical, see if point falls to left or right of vertical
                if (point.x() > otherPoint.x()) {
                    return 1;
                }
                else{
                    return -1;
                }
            }
            else{
                if (point.y() > otherPoint.y()) {
                    return 1;
                }
                else{
                    return -1;
                }
            }
        }
    }

    public KdTree() {}

    public void insert(Point2D newPoint) {
        if (newPoint == null) {
            throw new java.lang.NullPointerException("argument to put() is null");
        }
        // recursively move down tree to add new node and reset root to point at new tree
        root = insert(root,newPoint, null);
    }

    private Node insert(Node currentNode, Point2D newPoint, Node previousNode) {
        if (currentNode == null) {
            size++;
            return new Node(newPoint, previousNode);
        } // terminate recursion by adding new node to the bottom

        // otherwsie recur down the correct branch based on point
        if (currentNode.comparePoint(newPoint) > 0) {
            currentNode.left = insert(currentNode.left, newPoint, currentNode);
        }
        if (currentNode.comparePoint(newPoint) < 0) {
            currentNode.right = insert(currentNode.right, newPoint, currentNode);
        }

        // else points are equal, no need to change node's current point
        return currentNode;
    }

    // does tree contain a certain point?
    public boolean contains(Point2D searchPoint) {
        if (searchPoint == null) {
            throw new IllegalArgumentException("argument to contains() is null");
        }

        return contains(root,searchPoint);
    }

    private boolean contains(Node currentNode, Point2D searchPoint) {
        if (currentNode == null) {
            return false;
        } // terminate recursion when point is not found

        // otherwise recur down the correct branch based on point
        if (currentNode.comparePoint(searchPoint) > 0) {
            return contains(currentNode.left, searchPoint);
        }
        if (currentNode.comparePoint(searchPoint) < 0) {
            return contains(currentNode.right, searchPoint);
        }

        // else points are equal and terminate recursion
        return true;
    }


    // get the nearest point in the tree to the search point
    public Point2D nearest(Point2D searchPoint) {
        if (searchPoint == null) {
            throw new java.lang.NullPointerException("argument to nearest() is null");
        }
        if (isEmpty()) {
            return null;
        }

        return nearest(root, searchPoint, root.point);
    }


    private Point2D nearest(Node currentNode, Point2D searchPoint, Point2D closestPoint) {
        if (currentNode == null) {
            return closestPoint;
        } // terminate recursion when we can't go down the tree anymore, return closest point so far

        if (currentNode.comparePoint(searchPoint) == 0) {
            // else points are equal and terminate recursion
            closestPoint = searchPoint;
            return closestPoint;
        }

        Double closestDistance = closestPoint.distanceTo(searchPoint);

        // current node is new closest point
        if (currentNode.point.distanceTo(searchPoint) < closestDistance) {
            closestPoint = currentNode.point;
            closestDistance = closestPoint.distanceTo(searchPoint);
        }

        // otherwise recur down the correct branch based on point
        Node firstNode = currentNode.right;
        Node secondNode = currentNode.left;

        // search left node first if point is "smaller" than point at current node
        if (currentNode.comparePoint(searchPoint) > 0 ) {
            firstNode = currentNode.left;
            secondNode = currentNode.right;
        }

        // only check down a subtree if a straight line to the containing rectangle is smaller than the closest distance found so far
        if (firstNode != null && firstNode.rect.distanceSquaredTo(searchPoint) <= closestDistance) {
            closestPoint = nearest(firstNode , searchPoint, closestPoint);
            closestDistance = closestPoint.distanceTo(searchPoint);
        }
        if (secondNode != null && secondNode.rect.distanceSquaredTo(searchPoint) <= closestDistance) {
            closestPoint = nearest(secondNode, searchPoint, closestPoint);
        }

        return closestPoint;
    }

    // get all points within a query rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new java.lang.NullPointerException("argument to nearest() is null");
        }

        Stack<Point2D> pointsInRange = new Stack<Point2D>();
        if (isEmpty()) {
            return pointsInRange;
        }

        return range(root, rect, pointsInRange);
    }

    // might not need distance param
    private Stack<Point2D> range(Node currentNode, RectHV queryRectangle,Stack<Point2D> pointsInRange) {
        if (currentNode == null) {
            return pointsInRange;
        } // terminate recursion when we can't go down the tree anymore, return points in range

        if (queryRectangle.contains(currentNode.point)) {
            // query rectangle contains this point, add it to the list
            pointsInRange.push(currentNode.point);
        }

        // otherwsie recur down the correct branch based on point
        Node firstNode = currentNode.right;
        Node secondNode = currentNode.left;

        // only check down a subtree if that tree's rectangle intersects the query rectangle
        if (firstNode  !=null && firstNode.rect.intersects(queryRectangle)) {
            pointsInRange = range(firstNode, queryRectangle, pointsInRange);
        }
        if (secondNode != null && secondNode.rect.intersects(queryRectangle)) {
            pointsInRange = range(secondNode, queryRectangle, pointsInRange);
        }

        // recursion finished, return resulting points in range
        return pointsInRange;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void draw() {
        if (isEmpty()) {
            return;
        }
        draw(root);
    }

    private void draw(Node currentNode) {
        if (currentNode == null) {
            return;
        }

        StdDraw.setPenRadius(0.01);
        currentNode.point.draw();

        StdDraw.setPenRadius();
        double xMin,xMax,yMin,yMax = 0.0;

        if (currentNode.isVertical) {
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

        // create and draw new segment
        StdDraw.line(xMin,yMin,xMax,yMax);

        // reset pen color
        StdDraw.setPenColor(StdDraw.BLACK);

        // recur down subtrees
        draw(currentNode.left);
        draw(currentNode.right);
    }

    //  unit testing of the methods (optional)
    public static void main(String[] args) {
        KdTree points = new KdTree();

        if (args[0] == "true") {
            // read in file and test that way
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
            StdOut.println(points.nearest(new Point2D(0.81, 0.30)));
        }
        else{
            // true
            StdOut.println(points.isEmpty());

            points.insert(new Point2D(0.5,0.5));

            // true
            StdOut.println(points.contains(new Point2D(0.5,0.5)));

            // 1
            StdOut.println(points.size());

            points.insert(new Point2D(0.7,0.7));
            points.insert(new Point2D(.9,.9));

            // 0.5,0.5
            StdOut.println(points.nearest(new Point2D(0.2,0.2)));

            // 0.5,0.5 - 0.7,0.7
            Iterable<Point2D> pointsInRange = points.range(new RectHV(0,0,.7,.7));
            for (Point2D point: pointsInRange
                    ) {
                StdOut.println(point);
            }
        }

        points.draw();

    }

}
