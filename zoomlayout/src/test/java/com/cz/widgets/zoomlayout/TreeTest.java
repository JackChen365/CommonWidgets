package com.cz.widgets.zoomlayout;

import com.cz.widgets.zoomlayout.tree.TreeNode;
import com.cz.widgets.zoomlayout.tree.TreeNodeIndexer;

import org.junit.Test;

/**
 * @author Created by cz
 * @date 2020-05-10 09:53
 * @email bingo110@126.com
 */
public class TreeTest {

    @Test
    public void postOrderTraversal(){
        TreeNode<String> root = new TreeNode();
        root.value ="#";
        final int maxDepth = 5;
        final int levelCount = 3;
        buildTree(root, levelCount, 1, maxDepth);
        TreeNodeIndexer<String> treeNodeIndexer=new TreeNodeIndexer<>(root,TreeNodeIndexer.VERTICAL,100,100);
        int hierarchyBreadth = treeNodeIndexer.getHierarchyBreadth();
        int hierarchyDepth = treeNodeIndexer.getHierarchyDepth();
        System.out.println();
    }

    private static void buildTree(TreeNode<String> parent, int levelCount, int depth, int maxDepth) {
        if (depth < maxDepth) {
            for (int i=0;i<levelCount;i++) {
                String value = "Level:" + depth + (char)('A' + i);
                TreeNode<String> child = new TreeNode();
                child.value =value;
                child.depth = depth;
                child.parent = child;
                parent.children.add(child);

                levelCount = 3;
                buildTree(child, levelCount, depth + 1, maxDepth);
            }
        }
    }

    public static CharSequence padStart(CharSequence text,int length,char padChar){
        if (length < 0)
            throw new IllegalArgumentException("Desired length $length is less than zero.");
        if (length <= text.length())
            return text.subSequence(0, text.length());

        StringBuilder sb = new StringBuilder(length);
        for (int i=1;i<=(length - text.length());i++){
            sb.append(padChar);
        }
        sb.append(text);
        return sb;
    }
}
