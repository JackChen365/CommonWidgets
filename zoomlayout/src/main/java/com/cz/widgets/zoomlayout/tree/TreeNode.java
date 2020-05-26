package com.cz.widgets.zoomlayout.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by cz
 * @date 2020-05-09 21:10
 * @email bingo110@126.com
 */
public class TreeNode<E> {
    /**
     * The data of the node.
     */
    public E value;
    /**
     * The parent node of this node.
     */
    public TreeNode<E> parent=null;
    /**
     * All the child nodes of this node.
     */
    public List<TreeNode<E>> children = new ArrayList<>();
    /**
     * The depth value of the node.
     */
    public int depth = 0;
    /**
     * The breadth value of this node.
     */
    public int breadth = 0;
    /**
     * The center breadth value of its all child nodes.
     */
    public int centerBreadth = 0;
}
