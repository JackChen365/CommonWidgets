package com.cz.widgets.zoomlayout.tree;

import android.util.SparseArray;
import android.util.SparseIntArray;

import androidx.annotation.Nullable;

import com.cz.widgets.zoomlayout.RecyclerZoomLayout;

/**
 * @author Created by cz
 * @date 2020-05-09 22:53
 * @email bingo110@126.com
 *
 * The tree node location indexer.
 *
 * When you are scroll on the screen. You could use the location:x/y to return the specific node.
 * We do not just traverse all the tree node to check if the nodes contain the point.
 * But calculate an table instead.
 *
 * Just imaging your tree as a table. Each node has its own breadth and depth.
 * We will build a table by the node's breadth and depth.
 *
 *
 * For example:
 * The tree would look like this.
 *      - xxx
 *  - xx
 * x
 *  - xx
 *      - xxx
 * The location could be: 35x35. Each node's side could be:20x20
 * The table we build are:
 * depth horizontally:[20,40,60]
 * vertically:[20,40,60,80]
 * So for location:35x35. First we check the point:x is 35. This actually in position 1, and the point:y is the same.
 * The node would be the one who is depth:1 and breadth:1.
 */
public class TreeNodeIndexer<E> {
    public static final int HORIZONTAL = RecyclerZoomLayout.HORIZONTAL;
    public static final int VERTICAL = RecyclerZoomLayout.VERTICAL;
    private final SparseArray<TreeNode<E>> treeNodeArray=new SparseArray<>();
    private final OrientationHelper orientationHelper;
    private final SparseIntArray horizontalArray;
    private final SparseIntArray verticalArray;
    private int hierarchyBreadth = 0;
    private int hierarchyDepth = 0;


    public TreeNodeIndexer(TreeNode<E> root,int orientation, int nodeWidth, int nodeHeight) {
        horizontalArray=new SparseIntArray();
        verticalArray=new SparseIntArray();
        treeNodeArray.clear();
        //Step:1 post-order traverse all the tree node to know the breadth and the depth of the tree.
        postOrderTraversal(root,0);
        //Step:2 build a table by using the pre-render node's size.
        traversalHierarchyTree(root,nodeWidth,nodeHeight,horizontalArray,verticalArray);
        //Step:3 Put all the nodes to the sparse array
        organizeTreeNodes(root);

        //We add an extra column in order to avoid the finger scroll over the right side.
        hierarchyDepth++;
        int left = horizontalArray.get(hierarchyDepth-1);
        horizontalArray.put(hierarchyDepth,left+nodeWidth);
        int top = verticalArray.get(hierarchyBreadth-1);
        verticalArray.put(hierarchyBreadth,top+nodeHeight);

        orientationHelper=OrientationHelper.create(orientation,hierarchyDepth,hierarchyBreadth,nodeWidth,nodeHeight);

    }

    private void postOrderTraversal(TreeNode<E> node, int depth) {
        for(int i=0;i<node.children.size();i++){
            TreeNode<E> child = node.children.get(i);
            postOrderTraversal(child,depth+1);
            if(child.children.isEmpty()){
                hierarchyBreadth++;
            }
        }
        //Always record the max depth of the tree.
        if(hierarchyDepth<depth){
            hierarchyDepth=depth;
        }
        node.depth=depth;
        node.breadth= hierarchyBreadth;
        int centerBreadth;
        int childNodeCount = node.children.size();
        if(0 == childNodeCount){
            centerBreadth=node.breadth;
        } else if(1 ==  childNodeCount){
            TreeNode firstChildNode = node.children.get(0);
            centerBreadth=firstChildNode.centerBreadth;
        } else {
            TreeNode firstChildNode = node.children.get(0);
            TreeNode lastChildNode = node.children.get(childNodeCount-1);
            centerBreadth=firstChildNode.centerBreadth+(lastChildNode.centerBreadth-firstChildNode.centerBreadth)/2;
        }
        node.centerBreadth=centerBreadth;
    }

    /**
     * Traverse all the tree nodes all at once.
     */
    private void traversalHierarchyTree(TreeNode<E> node, int nodeWidth, int nodeHeight, SparseIntArray horizontalArray, SparseIntArray verticalArray) {
        int left=node.depth*nodeWidth;
        int top=node.centerBreadth*nodeHeight;
        horizontalArray.put(node.depth,left);
        verticalArray.put(node.centerBreadth,top);
        //Continue loop;
        for(TreeNode<E> child:node.children){
            traversalHierarchyTree(child,nodeWidth,nodeHeight,horizontalArray,verticalArray);
        }
    }

    /**
     * Organize all the tree nodes.
     * We put all the tree node to the sparse array and make each node a unique key for faster query.
     * @param node
     */
    private void organizeTreeNodes(TreeNode<E> node) {
        treeNodeArray.append((hierarchyBreadth+1)*node.depth+node.centerBreadth,node);
        for(int i=0;i<node.children.size();i++){
            TreeNode<E> child = node.children.get(i);
            organizeTreeNodes(child);
        }
    }

    public int getHierarchyBreadth() {
        return hierarchyBreadth;
    }

    public int getHierarchyDepth() {
        return hierarchyDepth;
    }

    public int findHierarchyBreadthIndex(float value){
        return binarySearchStartIndex(verticalArray, value);
    }

    public int findHierarchyDepthIndex(float value){
        return binarySearchStartIndex(horizontalArray, value);
    }

    @Nullable
    public TreeNode<E> findTreeNode(int breadth, int depth){
//        hierarchyBreadth*node.depth+node.centerBreadth
        int key=(hierarchyBreadth+1)*depth+breadth;
        TreeNode<E> eTreeNode = treeNodeArray.get(key);
        return eTreeNode;
    }

    public int getTreeMeasuredWidth() {
        return orientationHelper.getTreeMeasuredWidth();
    }

    public int getTreeMeasuredHeight() {
        return orientationHelper.getTreeMeasuredHeight();
    }

    private int binarySearchStartIndex(SparseIntArray array, float value){
        int start = 0;
        int result = -1;
        int end = array.size() - 1;
        while (start <= end) {
            int middle = (start + end) / 2;
            int middleValue = array.get(middle);
            if (value == middleValue) {
                result = middle;
                break;
            } else if (value < middleValue) {
                end = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        if (-1 == result) {
            result = start-1;
        }
        return result;
    }


    /**
     * The orientation helper of how to calculates the dimension of the tree.
     */
    private static abstract class OrientationHelper {
        /**
         * Return the depth dimension of the tree
         * @return
         */
        public abstract int getTreeMeasuredWidth();

        /**
         * Return the breadth dimension of the tree
         * @return
         */
        public abstract int getTreeMeasuredHeight();

        private static OrientationHelper create(int orientation,int depth, int breadth, int width, int height){
            if(HORIZONTAL==orientation){
                return createHorizontalHelper(depth,breadth,width,height);
            } else if(VERTICAL==orientation){
                return createVerticalHelper(depth,breadth,width,height);
            }
            throw new IllegalArgumentException("This orientation value:"+orientation+" is not supported!");
        }

        private static OrientationHelper createHorizontalHelper(final int depth, final int breadth, final int width, final int height){
            return new OrientationHelper() {
                @Override
                public int getTreeMeasuredWidth() {
                    return depth*width;
                }

                @Override
                public int getTreeMeasuredHeight() {
                    return breadth*height;
                }
            };
        }

        public static OrientationHelper createVerticalHelper(final int depth, final int breadth, final int width, final int height){
            return new OrientationHelper() {
                @Override
                public int getTreeMeasuredWidth() {
                    return breadth*width;
                }

                @Override
                public int getTreeMeasuredHeight() {
                    return depth*height;
                }
            };
        }
    }
}
