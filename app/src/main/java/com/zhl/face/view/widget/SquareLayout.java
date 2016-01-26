package com.zhl.face.view.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class SquareLayout extends RelativeLayout {

	 public SquareLayout(Context context) {
	        super(context);
	    }
	 
	    public SquareLayout(Context context, AttributeSet attr) {
	        super(context,attr);
	    }
	 
	    public SquareLayout(Context context, AttributeSet attr, int defStyle) {
	        super(context,attr,defStyle);
	    }
	 
	    @Override
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	        // For simple implementation, or internal size is always 0.
	        // We depend on the container to specify the layout size of
	        // our view. We can't really know what it is since we will be
	        // adding and removing different arbitrary views and do not
	        // want the layout to change as this happens.
	        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

	        // Children are just made to fill our space.
	        int childWidthSize = getMeasuredWidth();
	        int childHeightSize = getMeasuredHeight();
	        // if(childWidthSize > childHeightSize) {
	        // childWidthSize = childHeightSize;
	        // }else {
	        // childHeightSize = childWidthSize;
	        // }
	        //高度和宽度一样
	        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	    }
}
