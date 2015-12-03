package com.example.qq;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SwipeLayout extends FrameLayout{
	private View contentView;
	private View deleteView;
	private int contentWidth;//contentView的宽
	private int contentHeight;//contentView的宽
	private int deleteWidth;//deleteView的宽
	private int width;//SwipeLayout的宽
	private int left1 = -1;
	private ViewDragHelper viewDragHelper;
	private Object object = new Object();

	public SwipeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SwipeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SwipeLayout(Context context) {
		super(context);
		init();
	}
	
	enum SwipeState{
		Open,Close
	}
	private SwipeState mState = SwipeState.Close;//当前的state，默认是关闭状态
	private SlideMenu slideMenu;
	
	private void init(){
		viewDragHelper = ViewDragHelper.create(this, callback);
		//slideMenu = new SlideMenu(getContext());
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		contentView = getChildAt(0);
		deleteView = getChildAt(1);
	}
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		contentWidth = contentView.getMeasuredWidth();
		contentHeight = contentView.getMeasuredHeight();
		deleteWidth = deleteView.getMeasuredWidth();
		width = getMeasuredWidth();
	}
	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
		contentView.layout(0, 0,contentWidth, contentHeight);
		deleteView.layout(contentView.getRight(),0,contentView.getRight()+deleteWidth,
				contentHeight);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		if(!SwipeLayoutManager.getInstance().isCanSwipe(this)){
			//关闭已经打开的
			SwipeLayoutManager.getInstance().closeCurrentLayout();
			result = true;
		}
		return result;
	}
	
	private float downX,downY;
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = ev.getX();
			downY = ev.getY();
			Log.e("left1", left1+"");
			if((left1>=-deleteWidth&&left1<0)){
				//请求父View不拦截
				requestDisallowInterceptTouchEvent(true);
				//slideMenu.setFocusable(false);
				//slideMenu.setFocusableInTouchMode(false);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			//1.获取移动过程中的x,y
			float moveX = ev.getX();
			float moveY = ev.getY();
			//2.计算x和y方向移动的距离
			float deltaX = moveX - downX;
			float deltaY = moveY - downY;
			//3.判断如果是偏向于水平方向，那么应该由SwipeLayout处理，否则不用管
			if((left1>-deleteWidth&&left1<0)){
				//请求父View不拦截
				requestDisallowInterceptTouchEvent(true);
			}
			//4.更新downX，downY
			downX = moveX;
			downY = moveY;
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//每次触摸都需要判断当前是否应该可以滑动
		if(!SwipeLayoutManager.getInstance().isCanSwipe(this)){
			requestDisallowInterceptTouchEvent(true);
			return true;
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			if(left1>=-deleteWidth&&left1<0){
				//请求父View不拦截
				requestDisallowInterceptTouchEvent(true);
				
			}
			break;
		case MotionEvent.ACTION_MOVE:
			//1.获取移动过程中的x,y
			float moveX = event.getX();
			float moveY = event.getY();
			//2.计算x和y方向移动的距离
			float deltaX = moveX - downX;
			float deltaY = moveY - downY;
			//3.判断如果是偏向于水平方向，那么应该由SwipeLayout处理，否则不用管
			if(Math.abs(deltaX)>Math.abs(deltaY)){
				//请求父View不拦截
				requestDisallowInterceptTouchEvent(true);
			}
			//4.更新downX，downY
			downX = moveX;
			downY = moveY;
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		viewDragHelper.processTouchEvent(event);
		return true;
	}
	
	private ViewDragHelper.Callback callback = new Callback() {

		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==contentView || child==deleteView;
		}
		@Override
		public int getViewHorizontalDragRange(View child) {
			return deleteWidth;
		}
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			left1 = left;
			if(child==contentView){
				if(left>0){
					left = 0;
					left1 = 0;
				}
				if(left<-deleteWidth){
					left = -deleteWidth;
					left1 = -deleteWidth;
				}
			}else if (child==deleteView) {
				if(left<(width-deleteWidth)){
					left = width - deleteWidth;
					left1 = width - deleteWidth;
				}
				if(left>contentWidth)left = contentWidth;
				if(left>contentWidth)left1 = contentWidth;
			}
			return left;
		}
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if(changedView==contentView){
				//让deleteView伴随移动
				int newLeft = deleteView.getLeft()+dx;
				deleteView.layout(newLeft,deleteView.getTop(),newLeft+deleteWidth,deleteView.getBottom());
			}else if (changedView==deleteView) {
				//让contentView伴随移动
				int newLeft = contentView.getLeft()+dx;
				contentView.layout(newLeft,contentView.getTop(),newLeft+contentWidth,contentView.getBottom());
			}
			
			//根据contentView的left值判断对应的状态
			if(contentView.getLeft()==-deleteWidth && mState!=SwipeState.Open){
				mState = SwipeState.Open;
				
				//说明当前的SwipeLayout已经打开了，那么就需要让manager去记录
				SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);
				
			}else if (contentView.getLeft()==0 && mState!=SwipeState.Close) {
				mState = SwipeState.Close;
			
				//说明当前的SwipeLayout关闭了,那么就需要清除
				SwipeLayoutManager.getInstance().clearCurrentLayout();
			}
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if(contentView.getLeft()<-deleteWidth/2){
				//应该打开
				open();
			}else {
				//应该关闭
				close();
			}
		}
	};
	@Override
	public void computeScroll() {
		if(viewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
		}
	}

	public void close() {
		viewDragHelper.smoothSlideViewTo(contentView,0,contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	}

	public void open() {
		viewDragHelper.smoothSlideViewTo(contentView,-deleteWidth,contentView.getTop());
		ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
	}
	
}
