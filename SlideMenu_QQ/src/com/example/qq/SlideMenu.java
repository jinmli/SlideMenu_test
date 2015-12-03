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

	private View menuView;//�˵�����
	private View mainView;//������Ĳ���
	private int menuWidth;//�˵��Ŀ�
	private int mainWidth;//������Ŀ�
	private int width;//��ǰSlideMenu�Ŀ��
	private int dragRange;

	private ViewDragHelper viewDragHelper;
	FloatEvaluator floatEvaluator;//����ļ�����
	private DragState mState = DragState.Close;//��ǰSlideMenu��״̬��Ĭ���ǹر�
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
	 * ���ص�ǰSlideMenu��״̬
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
				//������
				close();
			}else {
				//���Ұ��
				open();
			}
			
			if(xvel>100 && mState==DragState.Close){
				//������һ������ٶȴ���100,�ʹ�
				open();
			}
			if(xvel<-100 && mState==DragState.Open){
				//������󻬶����ٶȴ���100,�͹ر�
				close();
			}
		}
		/**
		 * ʵ�ְ����ƶ�
		 */
		@Override
		public void onViewPositionChanged(View changedView, int left, int top,
				int dx, int dy) {
			super.onViewPositionChanged(changedView, left, top, dx, dy);
			if(changedView == menuView){
				//�ֶ��̶�סmenuView���������ƶ�
				menuView.layout(0,0,menuWidth,menuView.getMeasuredHeight());
				//��mainView�������ƶ�,������Ҫ�ٴ����Ʊ߽�
				int newLeft = mainView.getLeft()+dx;
				if(newLeft<0)newLeft=0;
				if(newLeft>dragRange)newLeft=dragRange;
				mainView.layout(newLeft, mainView.getTop(), newLeft+mainWidth,mainView.getBottom());
			}
			//1.�����ƶ��İٷֱ�
			float fraction = mainView.getLeft()*1f/dragRange;
			//2.�����ƶ��İٷֱ�ִ�а��涯��
			executeAnim(fraction);
			//3.����fraction�жϵ�ǰ��״̬
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
			//�ص�onDragging����
			if(listener!=null){
				listener.onDragging(fraction);
			}
		}
		
		
	};
	/**
	 * �رյķ���
	 */
	public void close(){
		viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}
	/**
	 * �򿪵ķ���
	 */
	public void open(){
		viewDragHelper.smoothSlideViewTo(mainView, dragRange, mainView.getTop());
		ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
	}
	/**
	 * ÿ��View���������������draw�����л����
	 */
	@Override
	public void computeScroll() {
		if(viewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(SlideMenu.this);
		}
	}
	/**
	 * ִ�а��涯��
	 * @param fraction
	 */
	protected void executeAnim(float fraction) {
		//fraction: 0-1
		//��СmainView
//		float scaleValue = 0.8f+(1-fraction)*0.2f;//1-0.8
		ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
		ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
		//�ƶ�,�Ŵ�,͸��menuView
		ViewHelper.setTranslationX(menuView, floatEvaluator.evaluate(fraction, -menuWidth/2, 0));
		ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
		ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
		ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));
		//��SLideMenu�ı�������һ������Ч��
		getBackground().setColorFilter((int) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), Mode.SRC_OVER);
	}
	private OnDragStateChangeListener listener;
	public void setOnDragStateChangeListener(OnDragStateChangeListener listener){
		this.listener = listener;
	}

	/**
	 * ����ӿڻص�
	 */
	public interface OnDragStateChangeListener{
		/**
		 * �򿪵Ļص�
		 */
		void onOpen();
		/**
		 * �رյĻص�
		 */
		void onClose();
		/**
		 * ��ק�����еĻص�
		 */
		void onDragging(float fraction);
	}
}
