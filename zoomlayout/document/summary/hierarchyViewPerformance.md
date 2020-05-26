## HierarchyView Performance


If we just detach all the view and fill the table.
```
@Override
public void draw(Canvas canvas) {
    long st= SystemClock.elapsedRealtime();
    super.draw(canvas);
    Log.i(TAG,"draw:"+(SystemClock.elapsedRealtime()-st));
}

//The drawing performance comsumption.
05-11 22:06:08.770 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:6
05-11 22:06:08.803 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:6
05-11 22:06:08.813 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:1
05-11 22:06:08.885 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:5
05-11 22:06:08.916 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:5
05-11 22:06:08.948 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:5
05-11 22:06:08.982 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:5
05-11 22:06:09.007 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:5
05-11 22:06:09.046 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:5
05-11 22:06:09.069 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:5
05-11 22:06:09.100 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:5
05-11 22:06:09.125 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:4
05-11 22:06:09.164 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:4
05-11 22:06:09.181 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:1
05-11 22:06:09.375 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:4
05-11 22:06:09.425 5195-5195/com.cz.widgets.sample I/HierarchyLayout: draw:5

//The scroll performance consumption
05-11 22:45:10.684 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:21
05-11 22:45:10.712 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:18
05-11 22:45:10.739 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:14
05-11 22:45:10.762 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:15
05-11 22:45:10.787 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:16
05-11 22:45:10.816 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:15
05-11 22:45:10.839 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:14
05-11 22:45:10.863 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:16
05-11 22:45:10.893 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:15
05-11 22:45:10.922 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:13
05-11 22:45:10.945 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:14
05-11 22:45:10.970 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:16
05-11 22:45:11.000 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:14
05-11 22:45:11.024 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:14
05-11 22:45:11.048 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:16
05-11 22:45:11.077 5742-5742/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:16


//After change recycler strategy:
05-11 23:10:42.216 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:0
05-11 23:10:42.233 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.250 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.266 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.284 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:2
05-11 23:10:42.301 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:0
05-11 23:10:42.316 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.333 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.350 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.366 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.382 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.397 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.413 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.432 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.448 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:1
05-11 23:10:42.465 12706-12706/com.cz.widgets.sample I/HierarchyLayout: scrollByInternal:0




//My old version.
//The drawing performance comsumption.
05-11 14:09:30.665 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:13
05-11 14:09:30.683 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:12
05-11 14:09:30.702 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:13
05-11 14:09:30.720 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:14
05-11 14:09:30.739 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:13
05-11 14:09:30.764 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:10
05-11 14:09:30.779 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:11
05-11 14:09:30.799 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:12
05-11 14:09:30.817 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:13
05-11 14:09:30.835 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:11
05-11 14:09:30.853 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:11
05-11 14:09:30.872 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:13
05-11 14:09:30.892 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:13
05-11 14:09:30.911 5448-5448/com.cz.laboratory.app I/HierarchyView: draw:13

//The scroll performance comsumption
05-11 14:49:28.351 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.372 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.395 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.417 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.441 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.464 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.535 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.559 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.584 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.609 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.636 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.660 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.683 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.705 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.728 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.756 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.833 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.857 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.881 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.905 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:1
05-11 14:49:28.928 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
05-11 14:49:28.951 5950-5950/com.cz.laboratory.app I/AbsHierarchyLayout: onScrollChanged:2
```


About preview

```
In my huawei-30 pro+
I/PreViewLayout: time:741

In the low performance device
I/PreViewLayout: time:2405

// The old preview
I/HierarchyLayout: onMeasure:2239
I/PreViewLayout: time1:2240
I/PreViewLayout: time2:2240
I/HierarchyLayout: draw:105
I/PreViewLayout: time3:2345
I/PreViewLayout: time4:2348
I/PreViewLayout: time5:2349

// The new version of preview.
I/HierarchyLayoutImpl: onMeasure:14
I/PreViewLayout: time1:14
I/PreViewLayout: time2:14
I/HierarchyLayoutImpl: draw:197
I/PreViewLayout: time3:211
I/PreViewLayout: time4:213
I/PreViewLayout: time5:214

//Binding the specific data for each node.

//This will add additional extra drawing time from 197 to 691
//Context context = getContext();
//adapter.onBindView(context,childView,hierarchyNode,hierarchyNode.item);
I/HierarchyLayoutImpl: onMeasure:15
I/PreViewLayout: time1:16
I/PreViewLayout: time2:17
I/HierarchyLayoutImpl: draw:658
I/PreViewLayout: time3:676
I/PreViewLayout: time4:678
I/PreViewLayout: time5:678


```