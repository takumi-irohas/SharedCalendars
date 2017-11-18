package higa.sharedcalendars.view.customview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by higashiyamamasahiro on 西暦17/09/13.
 */

public class NonSwipeAbleViewPager extends ViewPager {
	public NonSwipeAbleViewPager(Context context) {
		super(context);
	}
	
	public NonSwipeAbleViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
// Never allow swiping to switch between pages
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
// Never allow swiping to switch between pages
		return false;
	}
}
