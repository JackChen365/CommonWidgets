## Recycler Strategy for table.

For the table, especially for a scalable table.
Trying to recycle the child views that out of the screen is a hard job.

I have learn how to recycler a list like ListView or LinearLayoutManager in RecyclerView.
But for a table, I have no idea.

After I have finish the TableLayout use the recycle strategy in LinearLayoutManager. When I start to develop the HierarchyLayout.
I Just realized I do not know how to layout the tree. So I built a tree as a table.

![tree](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/zoom_tree_image.png?raw=true)

The tree would like this table.

```
        (6)
    (3)      (9)
(1)     (5)  (7)
(2)     (4)  (8)


After We fill the blank node.

(x) (x) (6) (x)
(x) (3) (x) (9)
(1) (x) (5) (7)
(2) (x) (4) (8)

```

So we back to the table. I have found that no matter where the tree node is.
If we know the row and the column in the table, I could calculate the location.

For example

```
Column
(100)(100)(120)(120)
When your node located in the 225, It is between the second column and third column.
So I know I should layout the second column.

For row of the table. we do the same thing.
```

When you keep moving. From the second column move to the third. I will recycle the second column.
When we arrive at the fourth column. We load this column in the table.


That's how the recycling strategy worked. I have try this recycling strategy in HierarchyLayout. It works very well.

Sorry about the TableLayout I do not have enough time to change the recycling strategy for it.