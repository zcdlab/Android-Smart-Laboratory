package com.fontlose.relayctrl;

import java.util.ArrayList;
import java.util.HashMap;
 

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
 

/**
 * @author root
 * ����ButtonDialog
 *
 *
 *
 */
public   class QButtonDialog {
	ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
	GridView menuGrid;
	View menuView;
	AlertDialog menuDialog;
	//QPopupWindow popupWindow;
	
	OnItemClickListener itemClick;
    public void close()
    {
    	menuDialog.dismiss();
    	
    }
	public QButtonDialog(Context mct) {
		super();		 
		SimpleAdapter simperAdapter = 
			   new SimpleAdapter(mct, data,R.layout.item_menu, new String[] { "itemImage", "itemText" },
				new int[] { R.id.item_image, R.id.item_text });
		menuView = View.inflate(mct, R.layout.gridview_menu, null);
		menuGrid = (GridView) menuView.findViewById(R.id.gridview);
		menuGrid.setAdapter(simperAdapter);
		
		menuDialog = new AlertDialog.Builder(mct).create();		
		/*QPopupWindow popupWindow = new QPopupWindow(menuView,  LayoutParams.WRAP_CONTENT,  LayoutParams.WRAP_CONTENT,true);
		popupWindow.setOutsideTouchable(true);		
		popupWindow.setTouchable(true); 
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.update(); */
		menuDialog.setView(menuView);
		menuGrid.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) 
			{ 	//QButtonDialog.this.onItemClick(arg0,arg1,arg2,arg3);
				if(itemClick!=null)
				  itemClick.onItemClick(arg0, arg1, arg2, arg3);
			}
		}); 
	}	 
	
	public void addMenu(String text,int image)
	{
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("itemImage", image);
		map.put("itemText", text);
		data.add(map);		
	}	
	
	public void show( )
	{ 
		menuGrid.invalidate();
		//popupWindow.showAtLocation(parent, Gravity.CENTER, 0, 0) ;
		menuDialog.show();
	} 
	
	public void setOnItemClickListener(OnItemClickListener itemclick)
	{
		itemClick=itemclick;	
	}
	
	
}
