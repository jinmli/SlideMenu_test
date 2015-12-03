package com.example.qq;

/**
 * 使用单例模式去管理已经打开的SwipeLayout
 * @author Administrator
 *
 */
public class SwipeLayoutManager {
	private SwipeLayoutManager(){}
	private static SwipeLayoutManager mInstance = new SwipeLayoutManager();
	public static SwipeLayoutManager getInstance(){
		return mInstance;
	}
	private SwipeLayout currentLayout;//记录当前已经打开的SwipeLayout
	public void setSwipeLayout(SwipeLayout currentLayout){
		this.currentLayout = currentLayout;
	}
	
	/**
	 * 判断当前是否能够滑动,如果有打开的，那么就不能滑动,
	 * 而且需要判断，当前已经打开的和正在触摸是否是同一个，如果是同一个，则可以滑动，反之就不能
	 * @return
	 */
	public boolean isCanSwipe(SwipeLayout swipeLayout){
		if(currentLayout==null){
			return true;
		}else {
			return currentLayout==swipeLayout;
		}
	}
	/**
	 * 关闭已经打开的SwipeLayout
	 */
	public void closeCurrentLayout(){
		if(currentLayout!=null){
			currentLayout.close();
		}
	}
	/**
	 * 清除当前记录的SwipeLayout
	 */
	public void clearCurrentLayout(){
		currentLayout = null;
	}
}
