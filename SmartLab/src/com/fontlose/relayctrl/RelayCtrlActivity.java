package com.fontlose.relayctrl;
 
 
 

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

	
	
public class RelayCtrlActivity extends Activity {
	AlertDialog aDailog;
	DataProcess dataProcess;
	UiProcess   uiProcess  ;
    private static Toast MsgToast;	
    QMainMenu mainDialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        MsgToast=Toast.makeText(this, "", Toast.LENGTH_SHORT);
        dataProcess=new DataProcess(hMsg,this);
        uiProcess  =new UiProcess((LinearLayout)findViewById(R.id.mainLay),this,hMsg,dataProcess);
        createDialog();
        mainDialog=new QMainMenu(this); 
        mainDialog.setOnItemClickListener(new onMenuItemClick());
        uiProcess.createConfigWindow=uiProcess.createConfigWindow(); 
    }
    
    
    
	HandleMsg hMsg=new HandleMsg(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			
			if(dataProcess==null) return; 
			if(msg.what==dataProcess.RELAYOPT)
			{
				dataProcess.sendrelayCmd(msg.arg1,msg.arg2);
				
			}
			else if(msg.what==dataProcess.RELAYCHK)
			{
				if(ckeck)
				{
					dataProcess.sendrelayCmd(5,0);
					this.sendEmptyMessageDelayed(dataProcess.RELAYCHK, 2000);
				}
			} 
			else if(msg.what==dataProcess.CLOSETCP)
			{
				uiProcess.stopConn();
			} 	
			else if(msg.what==dataProcess.RELAYSTATE)
			{
				uiProcess.setRelayState(msg.arg1); 
			} 				
			else if(msg.what==dataProcess.APPQUIT)
			{
				aDailog.show();
			} 	 
			else if(msg.what==dataProcess.MAINMENU)
			{
				mainDialog.show();
			} 	 
		}		
	};
     
 
	public void createDialog()
    {
        aDailog=   new AlertDialog.Builder(RelayCtrlActivity.this)  
        						.setTitle(RelayCtrlActivity.this.getString(R.string.tileQuit))  
        						.setNegativeButton(RelayCtrlActivity.this.getString(R.string.lbCancle),  new DialogInterface.OnClickListener(){  
        							public void onClick(DialogInterface dialoginterface, int i){   
        								//uiProcess.soundPlay();
        								aDailog.dismiss(); 
        							}})
        						.setPositiveButton(RelayCtrlActivity.this.getString(R.string.lbQuit),new DialogInterface.OnClickListener(){  
        							public void onClick(DialogInterface dialoginterface, int i){   
        								//uiProcess.soundPlay();
        								uiProcess.saveIpPort();
        								Intent home = new Intent(Intent.ACTION_MAIN);  
        								home.addCategory(Intent.CATEGORY_HOME);  
        								RelayCtrlActivity.this.startActivity(home);  
        								try {
        									Thread.sleep(800);
        								} catch (InterruptedException e) {
        									e.printStackTrace();
        								}	
        								
        								
        								
        								System.exit(0);
               	   
        							}})
        						 .create() ;  


		 aDailog.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
				if((event.ACTION_DOWN==event.getAction())&&(event.getRepeatCount()==0))
				{
					if(keyCode==event.KEYCODE_BACK) 
					{	 
		 				Intent home = new Intent(Intent.ACTION_MAIN);  
						home.addCategory(Intent.CATEGORY_HOME);  
						RelayCtrlActivity.this.startActivity(home);  
						/**/	try {
							Thread.sleep(800);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						System.exit(0);
					}
					return true;
				}	
				return false;
			}
		});
    }
 
	class onMenuItemClick implements OnItemClickListener
    {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
			mainDialog.close();			
			if(arg2==0)
			{/*编辑服务*/ 
				uiProcess.createConfigWindow.showAtLocation((LinearLayout)findViewById(R.id.mainLay),Gravity.CENTER_VERTICAL, 0, 0); 
			} 
			else if(arg2==1)
			{	
				//AppConnect.getInstance(mct).showOffers(mct);
				mainDialog.close(); 
				createaboutWindow(); 
			}
			
		}
    }


	/**
     * 菜单点击事件
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.itset: 
        	 uiProcess.createConfigWindow.showAtLocation((LinearLayout)findViewById(R.id.mainLay),Gravity.CENTER_VERTICAL, 0, 0); 
            return true;
        case R.id.itabout: 
        	mainDialog.close(); 
			createaboutWindow(); 
           return true;
         default:return true;
       } 
    }
	

	public static void showMessage(String msg)
	{
		MsgToast.setText(msg);
       MsgToast.setDuration(MsgToast.LENGTH_SHORT);
       MsgToast.show();
	 }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
  
		if((event.ACTION_DOWN==event.getAction())&&(event.getRepeatCount()==0))
		{
			if(keyCode==event.KEYCODE_BACK) 
			{	 
				 hMsg.sendEmptyMessageDelayed(DataProcess.APPQUIT, 2); 
			}
			
			else if(keyCode==event.KEYCODE_MENU) 
			{	 
				hMsg.sendEmptyMessageDelayed(DataProcess.MAINMENU, 2);
				return true;
			}
			 
			return true;
		}	 
		return super.onKeyDown(keyCode, event);
	}
	

    protected QPopupWindow createaboutWindow(){/*增加连接按钮*/
		
		View  view= (LinearLayout)findViewById(R.id.mainLay);
 
		LayoutInflater factory=LayoutInflater.from(RelayCtrlActivity.this);
		
		LinearLayout lout=(LinearLayout)factory.inflate(R.layout.about,null);
 		QPopupWindow popupWindow = new QPopupWindow(lout,  LayoutParams.FILL_PARENT,  LayoutParams.WRAP_CONTENT,true);
 
 		TextView tv3=(TextView)lout.findViewById(R.id.textView3);
 		tv3.setText(Html.fromHtml("<u>mailto:tec@usr.cn</u>"));
 		//tv3.setTextColor(Color.rgb(red, green, blue));
 		tv3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse("mailto:tec@usr.cn");  
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				try {
					RelayCtrlActivity.this.startActivity(it); 
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		});
 		 		
 		popupWindow.setOutsideTouchable(true);		
		popupWindow.setTouchable(true); 
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.update(); 
		popupWindow.showAtLocation(view,Gravity.CENTER_VERTICAL, 0, 0);
		
		return popupWindow; 
	}
     
}