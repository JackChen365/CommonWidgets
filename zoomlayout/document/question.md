## Questions

* About zoom from large size to little size. For a recycler view. It will have a large blank space.

![question1](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/zoom_question_1.gif?raw=true)

Here is the solution.

Change the code here

```
return new ZoomOrientationHelper(layout) {
    @Override
    public int getEnd() {
        return wrappedHelper.getEnd();
    }
}
```

It will cause another problem. make an influence on the recycler process.

The original code was:

```
return new ZoomOrientationHelper(layout) {
    @Override
    public int getEnd() {
        float matrixScaleY = layout.getMatrixScaleY();
        return (int) (wrappedHelper.getEnd()/matrixScaleY);
    }
}
```

For a recycler view. we could recycler the view immediately after it moves out of the view.
Even when the view is zoom to another size. But if we use this solution we probably not recycler this view after it zoomed.

![image2](https://github.com/momodae/LibraryResources/blob/master/CommonWidgets/image/zoom_question_2.gif?raw=true)

That's the dilemma.





