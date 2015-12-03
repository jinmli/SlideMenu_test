package com.example.qq;

/**
 * ʹ�õ���ģʽȥ�����Ѿ��򿪵�SwipeLayout
 * @author Administrator
 *
 */
public class SwipeLayoutManager {
	private SwipeLayoutManager(){}
	private static SwipeLayoutManager mInstance = new SwipeLayoutManager();
	public static SwipeLayoutManager getInstance(){
		return mInstance;
	}
	private SwipeLayout currentLayout;//��¼��ǰ�Ѿ��򿪵�SwipeLayout
	public void setSwipeLayout(SwipeLayout currentLayout){
		this.currentLayout = currentLayout;
	}
	
	/**
	 * �жϵ�ǰ�Ƿ��ܹ�����,����д򿪵ģ���ô�Ͳ��ܻ���,
	 * ������Ҫ�жϣ���ǰ�Ѿ��򿪵ĺ����ڴ����Ƿ���ͬһ���������ͬһ��������Ի�������֮�Ͳ���
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
	 * �ر��Ѿ��򿪵�SwipeLayout
	 */
	public void closeCurrentLayout(){
		if(currentLayout!=null){
			currentLayout.close();
		}
	}
	/**
	 * �����ǰ��¼��SwipeLayout
	 */
	public void clearCurrentLayout(){
		currentLayout = null;
	}
}
