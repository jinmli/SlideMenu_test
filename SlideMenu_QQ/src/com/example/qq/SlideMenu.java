package com.example.qq;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.view.ViewHelper;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

public class SlideMenu extends FrameLayout {

	private View menuView;//菜单布局
	private View mainView;//主界面的布局
	private int menuWidth;//菜单的宽
	private int mainWidth;//主界面的宽
	private int width;//当前SlideMenu的宽度
	private int dragRange;

	private ViewDragHelper viewDragHelper;
	FloatEvaluator floatEvaluator;//浮点的计算器
	private DragState mState = DragState.Close;//当前SlideMenu的状态，默认是关闭
	enum DragState{
		Open,Close
	}
	public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public SlideMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SlideMenu(Context context) {
		super(context);
		init();
	}

	private void init() {
		floatEvaluator = new FloatEvaluator();
		viewDragHelper = ViewDragHelper.create(this, callback);
	}
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if(getChildCount()!=2){
			throw new IllegalArgumentException("SlideMenu only can have 2 children!");
		}
		menuView = getChildAt(0);
		mainView = getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		menuWidth = menuView.getMeasuredWidth();
		mainWidth = mainView.getMeasuredWidth();
		width = getMeasuredWidth();
		dragRange = (int) (width*0.6f);
	}
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = viewDragHelper.shouldInterceptTouchEvent(ev);
		return result;
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		viewDragHelper.processTouchEvent(event);
		return true;
	}
	/**
	 * 返回当前SlideMenu的状态
	 * @return
	 */
	public DragState getCurrentState(){
		return mState;
	}
	private ViewDragHelper.Callback callback = new Callback() {
		@Override
		public boolean tryCaptureView(View child, int pointerId) {
			return child==menuView || child==mainView;
		}
		@Override
		public int getViewHorizontalDragRange(View child) {
			return dragRange;
		}
		@Override
		public int clampViewPositionHorizontal(View child, int left, int dx) {
			if(child == mainView){
				if(left<0){
					left = 0;
				}else if(left>dragRange){
					left = dragRange;
				}
			}
			return left;
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if(mainView.getLeft()<dragRange/2){
				//在左半边
				close();
			}else {
				//在右半边
				open();
			}
			
			if(xvel>100 && mState==DragState.Close){
				//如果向右滑动的速度大于100,就打开
				open();
			}
			if(xvel<-100 && mState==DragState.Open){
				//如果向左滑动的速度大于100,就关闭
				close();
			}
		}
		/**
		 * 实现伴随移动
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if(changedView == menuView){
				//手动固定住menuView，不让它移动
				menuView.layout(0,0,menuWidth,menuView.getMeasuredHeight());
				//让mainView作伴随移动,并且需要再次限制边界
				int newLeft = mainView.getLeft()+dx;
				if(newLeft<0)newLeft=0;
				if(newLeft>dragRange)newLeft=dragRange;
				mainView.layout(newLeft, mainView.getTop(), newLeft+mainWidth,mainView.getBottom());
			}
			//1.计算移动的百分比
			float fraction = mainView.getLeft()*1f/dragRange;
			//2.根据移动的百分比执行伴随动画
			executeAnim(fraction);
			//3.根据fraction判断当前的状态
			if(fraction == 0f && mState != DragState.Close){
				mState = DragState.Close;
				if(listener!=null){
					listener.onClose();
				}
			}else if(fraction==1f && mState !=DragState.Open){
				mState = DragState.Open;
				if(listener!=null){
					listener.onOpen();
				}
			}
			//回调onDragging方法
			if(listener!=null){
				listener.onDragging(fraction);
			}
		}
		
		
	};
	/**
	 * 关闭的方法
	 */
	public void close(){
		viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}
	/**
	 * 打开的方法
	 */
	public void open(){
		viewDragHelper.smoothSlideViewTo(mainView, dragRange, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}
	/**
	 * 每个View都有这个方法，在draw方法中会调用
	 */
	@Override
	public void computeScroll() {
		if(viewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}
	}
	/**
	 * 执行伴随动画
	 * @param fraction
	 */
	protected void executeAnim(float fraction) {
		//fraction: 0-1
		//缩小mainView
//		float scaleValue = 0.8f+(1-fraction)*0.2f;//1-0.8
		ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
		ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
		//移动,放大,透明menuView
		ViewHelper.setTranslationX(menuView, floatEvaluator.evaluate(fraction, -menuWidth/2, 0));
		ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
		ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
		ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));
		//给SLideMenu的背景覆盖一层遮罩效果
		getBackground().setColorFilter((int) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
	}
	private OnDragStateChangeListener listener;
	public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
		this.listener = listener;
	}

	/**
	 * 定义接口回调
	 */
	public interface OnDragStateChangeListener{
		/**
		 * 打开的回调
		 */
		void onOpen();
		/**
		 * 关闭的回调
		 */
		void onClose();
		/**
		 * 拖拽过程中的回调
		 */
		void onDragging(float fraction);
	}
}
