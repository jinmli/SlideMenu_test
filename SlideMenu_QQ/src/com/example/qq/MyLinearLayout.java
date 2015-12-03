package com.example.qq;

import com.example.qq.SlideMenu.DragState;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 在SlideMenu处于打开的状态下，应该拦截并消费掉所有的触摸事件
 * @author Administrator
 *
 */
public class MyLinearLayout extends LinearLayout{

	public MyLinearLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyLinearLayout(Context context) {
		super(context);
	}
	
	private SlideMenu slideMenu;
	public void setSlideMenu(SlideMenu slideMenu){
		this.slideMenu = slideMenu;
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(isSlideMenuOpen()){
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isSlideMenuOpen()){
			if(event.getAction()==MotionEvent.ACTION_UP){
				//按下的时候需要关闭SlideMenu
				slideMenu.close();
			}
			
			//需要消费掉事件
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	private boolean isSlideMenuOpen(){
		return slideMenu!=null && slideMenu.getCurrentState()==DragState.Open;
	}
}
