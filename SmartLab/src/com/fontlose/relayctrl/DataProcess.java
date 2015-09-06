package com.fontlose.relayctrl;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import android.content.Context;
import android.util.Log;

public class DataProcess {
	
	public static byte RELAYOPT=1;
	public static byte RELAYCHK=2;
	public static byte RELAYSTATE=3;
	public static byte APPQUIT=4;
	
	public static byte CLOSETCP=5;
	public static byte MAINMENU=6;
	     
   
   
   protected Socket socket            ;//Socket 数据  
         //目标端口  
   public boolean State;
 	private byte[] sData=new byte[1024];//接收缓存
 	ReadThread readThread=null;
 	HandleMsg hOptMsg=null;
 	Context mct=null;
	 /**
	 * @param s
	 * @param ctrlHandle
	 */
 	public DataProcess(HandleMsg hmsg,Context context) 
 	{ 
		socket  = new Socket();
		hOptMsg=hmsg;
		mct=context;
	}
	
	public boolean sendData(byte[] data) throws IOException {
		// TODO Auto-generated method stub
		OutputStream out=socket.getOutputStream();
		if(out==null) return false;
		out.write(data);
		return true;
	}
	
	public boolean stopConn() {
		State=false;
		if(readThread==null) return false;
		readThread.abortRead();
		return false;
	}

 
	public boolean startConn( String  ip,int port) { 
		if(socket.isClosed()) socket=new Socket();
		SocketAddress remoteAddr=new InetSocketAddress(ip,port);
		try {
			socket.connect(remoteAddr, 2000);
		} catch (IOException e) {
			socket=new Socket();
			Log.v("tcpserver", e.getMessage());
			return false;
		}
		this.readThread=new ReadThread(hOptMsg, sData,socket);
		readThread.start();
		State=true;
		return true; 
	}	
	
	
	
	
	public byte[] packageCmd(byte id,byte opt)
	{
		
		if(id>5) return null;
				
		byte[] cmd=new byte[]{0x55,0x01,0x01,0,0,0,0,0};
		if(id==5)
		{
			cmd[2]=0;
		}
		else if(id==0)
		{
			cmd[3]=opt;
			cmd[4]=opt;
			cmd[5]=opt; 
			cmd[6]=opt; 
		}
		else cmd[2+id]=opt;
		cmd[7]=(byte)(cmd[0]+cmd[1]+cmd[2]+cmd[3]+cmd[4]+cmd[5]+cmd[6]);
		return cmd;
	}
	
	
	public void sendrelayCmd(int id,int opt)
	{
		byte[] cmd=packageCmd((byte)id,(byte)opt);
		if(cmd==null) return;
		if((readThread==null)||(readThread.state==false))
		{
			RelayCtrlActivity.showMessage(mct.getString(R.string.msg5)); 
			hOptMsg.stateCheck(0);
			return;
		}
		try {
			sendData(cmd);
			if(id!=5) hOptMsg.stateCheck(2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			RelayCtrlActivity.showMessage(mct.getString(R.string.msg4)); 
		//	hOptMsg.sendEmptyMessage(DataProcess.CLOSETCP);
		}
	}
}
