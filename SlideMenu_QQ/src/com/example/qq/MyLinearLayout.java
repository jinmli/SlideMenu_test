package com.example.qq;

import com.example.qq.SlideMenu.DragState;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * ��SlideMenu���ڴ򿪵�״̬�£�Ӧ�����ز����ѵ����еĴ����¼�
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
				//���µ�ʱ����Ҫ�ر�SlideMenu
				slideMenu.close();
			}
			
			//��Ҫ���ѵ��¼�
			return true;
		}
		return super.onTouchEvent(event);
	}
	
	private boolean isSlideMenuOpen(){
		return slideMenu!=null && slideMenu.getCurrentState()==DragState.Open;
	}
}
