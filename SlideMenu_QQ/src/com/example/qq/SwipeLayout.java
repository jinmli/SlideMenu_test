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
	private int contentWidth;//contentView�Ŀ�
	private int contentHeight;//contentView�Ŀ�
	private int deleteWidth;//deleteView�Ŀ�
	private int width;//SwipeLayout�Ŀ�
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
	private SwipeState mState = SwipeState.Close;//��ǰ��state��Ĭ���ǹر�״̬
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
			//�ر��Ѿ��򿪵�
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
				//����View������
				requestDisallowInterceptTouchEvent(true);
				//slideMenu.setFocusable(false);
				//slideMenu.setFocusableInTouchMode(false);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			//1.��ȡ�ƶ������е�x,y
			float moveX = ev.getX();
			float moveY = ev.getY();
			//2.����x��y�����ƶ��ľ���
			float deltaX = moveX - downX;
			float deltaY = moveY - downY;
			//3.�ж������ƫ����ˮƽ������ôӦ����SwipeLayout���������ù�
			if((left1>-deleteWidth&&left1<0)){
				//����View������
				requestDisallowInterceptTouchEvent(true);
			}
			//4.����downX��downY
			downX = moveX;
			downY = moveY;
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		//ÿ�δ�������Ҫ�жϵ�ǰ�Ƿ�Ӧ�ÿ��Ի���
		if(!SwipeLayoutManager.getInstance().isCanSwipe(this)){
			requestDisallowInterceptTouchEvent(true);
			return true;
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getX();
			downY = event.getY();
			if(left1>=-deleteWidth&&left1<0){
				//����View������
				requestDisallowInterceptTouchEvent(true);
				
			}
			break;
		case MotionEvent.ACTION_MOVE:
			//1.��ȡ�ƶ������е�x,y
			float moveX = event.getX();
			float moveY = event.getY();
			//2.����x��y�����ƶ��ľ���
			float deltaX = moveX - downX;
			float deltaY = moveY - downY;
			//3.�ж������ƫ����ˮƽ������ôӦ����SwipeLayout���������ù�
			if(Math.abs(deltaX)>Math.abs(deltaY)){
				//����View������
				requestDisallowInterceptTouchEvent(true);
			}
			//4.����downX��downY
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
				//��deleteView�����ƶ�
				int newLeft = deleteView.getLeft()+dx;
				deleteView.layout(newLeft,deleteView.getTop(),newLeft+deleteWidth,deleteView.getBottom());
			}else if (changedView==deleteView) {
				//��contentView�����ƶ�
				int newLeft = contentView.getLeft()+dx;
				contentView.layout(newLeft,contentView.getTop(),newLeft+contentWidth,contentView.getBottom());
			}
			
			//����contentView��leftֵ�ж϶�Ӧ��״̬
			if(contentView.getLeft()==-deleteWidth && mState!=SwipeState.Open){
				mState = SwipeState.Open;
				
				//˵����ǰ��SwipeLayout�Ѿ����ˣ���ô����Ҫ��managerȥ��¼
				SwipeLayoutManager.getInstance().setSwipeLayout(SwipeLayout.this);
				
			}else if (contentView.getLeft()==0 && mState!=SwipeState.Close) {
				mState = SwipeState.Close;
			
				//˵����ǰ��SwipeLayout�ر���,��ô����Ҫ���
				SwipeLayoutManager.getInstance().clearCurrentLayout();
			}
		}

		@Override
		public void onViewReleased(View releasedChild, float xvel, float yvel) {
			super.onViewReleased(releasedChild, xvel, yvel);
			if(contentView.getLeft()<-deleteWidth/2){
				//Ӧ�ô�
				open();
			}else {
				//Ӧ�ùر�
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
