package com.fontlose.relayctrl;

import java.io.IOException;
import java.net.Socket;

import android.os.Message;
import android.util.Log; 

public class ReadThread extends Thread { 
	boolean state;
	HandleMsg hOptMsg;
	byte[] sData;
	Socket socket;
	public ReadThread(HandleMsg hmsg,byte[] sData,Socket socket)
	{
		hOptMsg=hmsg; 
		this.sData=sData;
		this.socket=socket;
	}
	/*  
	 * @see java.lang.Thread#run()
	 * 线程接收主循环
	 */

	public void run() 
	 {
		int rlRead;  
		state=true;
		try 
		{
			while(state)
			{
				rlRead=socket.getInputStream().read(sData);//对方断开返回-1
				if(rlRead>0)
				{
					//RelayCtrlActivity.showMessage(sData.toString());
					unpackageCmd(sData,rlRead);
					
				}
				else 
				{
			    	state=false;
			    	hOptMsg.sendEmptyMessage(DataProcess.CLOSETCP);
					break;
 				}
			}
	    }
	    catch (Exception e) {
	    	Log.v("tcpserver",e.getMessage());
	    	state=false;
	    	hOptMsg.sendEmptyMessage(DataProcess.CLOSETCP);
	    }	    
	 }
	
	
	
	public void abortRead()
	{
		if(socket==null) return;
		try 
		{
		   socket.shutdownInput();
		   socket.shutdownOutput();
		   socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		state = false;	
	}
	
	public void unpackageCmd(byte[] cmd,int len)
	{
		int size=len;
		if(size<=0) return;
		
		int i;
		
		boolean cmdStart=false;
		int cmdInx = 0;
		byte sum=0;
		for(i=0;i<size;i++)
		{
			if(cmdStart==false)
			{//22 01 00 01 01 01 02 23
				if(cmd[i]==0x22) 
				{
					cmdStart=true;
					cmdInx  =0;
					sum=0x22;
				}
			}
			else
			{
				cmdInx++;
				switch(cmdInx)
				{
				   case 1:
					   if(cmd[i]!=0x01) cmdStart=false;
					   sum+=cmd[i];
					break;
				   case 2:
					   if((cmd[i]&0xFE)!=0x00) cmdStart=false;
					   sum+=cmd[i];
					break;					
				   case 3:
				   case 4:
				   case 5:
				   case 6:
					   sum+=cmd[i]; 
					break;
				   case 7:
					   if(cmd[i]==sum) 
					   {
							if(hOptMsg==null) return;
							Message msg=new Message();
							msg.what=DataProcess.RELAYSTATE;
							msg.arg1=cmd[i-1];
							msg.arg1=(msg.arg1<<8)|cmd[i-2];
							msg.arg1=(msg.arg1<<8)|cmd[i-3];
							msg.arg1=(msg.arg1<<8)|cmd[i-4]; 
							hOptMsg.sendMessage(msg);
					   }   
					   cmdStart=false;
						break;
				}
			}			
		} 
	}
}










