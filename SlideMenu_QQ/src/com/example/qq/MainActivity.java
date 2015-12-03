package com.example.qq;

import java.util.Random;
import com.example.qq.SlideMenu.OnDragStateChangeListener;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//初始化view
		SlideMenu slideMenu = (SlideMenu) findViewById(R.id.slideMenu);
		final ImageView iv_head = (ImageView) findViewById(R.id.iv_head);
		final ListView menuListView = (ListView) findViewById(R.id.menu_listview);
		ListView mainListView = (ListView) findViewById(R.id.main_listview);
		MyLinearLayout my_layout = (MyLinearLayout) findViewById(R.id.my_layout);
		
		my_layout.setSlideMenu(slideMenu);
		
		//设置数据
		menuListView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,Constant.sCheeseStrings){
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = (TextView) super.getView(position, convertView, parent);
				textView.setTextColor(Color.WHITE);
				return textView;
			}
		});
		mainListView.setAdapter(new MyAdapter());
		mainListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState==OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
					//关闭已经打开的
					SwipeLayoutManager.getInstance().closeCurrentLayout();
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		
		slideMenu.setOnDragStateChangeListener(new OnDragStateChangeListener() {
			@Override
			public void onOpen() {
				menuListView.smoothScrollToPosition(new Random().nextInt(menuListView.getCount()));
			}
			@Override
			public void onDragging(float fraction) {
				ViewHelper.setAlpha(iv_head,1-fraction);
			}
			@Override
			public void onClose() {
				ViewPropertyAnimator.animate(iv_head).translationXBy(20)
				.setInterpolator(new CycleInterpolator(10))
				.setDuration(500)
				.start();
			}
		});
			
	}
	class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return Constant.NAMES.length;
		}
		@Override
		public String getItem(int position) {
			return Constant.NAMES[position];
		}
		@Override
		public long getItemId(int position) {
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView==null){
				convertView = View.inflate(MainActivity.this, R.layout.adapter_list, null);
			}
			ViewHolder holder = ViewHolder.getHolder(convertView);
			
			holder.tv_name.setText(Constant.NAMES[position]);
			
			return convertView;
		}
		
	}

	static class ViewHolder{
		TextView tv_name;
		TextView tv_delete;
		public ViewHolder(View convertView){
			tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			tv_delete = (TextView) convertView.findViewById(R.id.tv_delete);
		}
		
		public static ViewHolder getHolder(View convertView){
			ViewHolder holder = (ViewHolder) convertView.getTag();
			if(holder==null){
				holder = new ViewHolder(convertView);
				convertView.setTag(holder);
			}
			return holder;
		}
	}
}
