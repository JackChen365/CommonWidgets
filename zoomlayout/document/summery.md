##

* How to calculate the breadth of the tree

![image](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/zoom_tree_image.png?raw=true)

```
public class TreeTest {

    private static int breadthIndex=0;

    public static void main(String[] args) {
        TreeNode<String> root = new TreeNode("#");
        final int maxDepth = 5;
        final int levelCount = 3;
        buildTree(root, levelCount, 1, maxDepth);
        postOrderTraversal(root);
    }

    private static void postOrderTraversal(TreeNode<String> node) {
        List<TreeNode<String>> children = node.children;
        for(int i=0;i<children.size();i++){
            TreeNode<String> child = children.get(i);
            if(child.children.isEmpty()){
                breadthIndex++;
            }
            postOrderTraversal(child);
        }
        node.breadth= breadthIndex;
        System.out.println(padStart("",node.depth,'\t')+node.item+" breadth:"+ node.breadth);
    }

    private static void buildTree(TreeNode<String> parent,int levelCount,int depth,int maxDepth) {
        if (depth < maxDepth) {
            for (int i=0;i<levelCount;i++) {
                String value = "Level:" + depth + (char)('A' + i);
                TreeNode<String> child = new TreeNode(value);
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


    /**
     * @author Created by cz
     * @date 2020-05-09 21:10
     * @email bingo110@126.com
     */
    public static class TreeNode<E> {
        /**
         * The data of the node.
         */
        public E item;
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
        int breadth = 0;

        public TreeNode(E item) {
            this.item = item;
        }
    }
}

```


* Quickly get position in an ordered array

```
private int binarySearchStartIndex(SparseIntArray array, int value){
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
```