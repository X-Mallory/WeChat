package com.cauc;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ProgressMonitorInputStream;

public class FileTransferHandler implements Runnable {
     private int port;
     private Inet4Address ip;
     private ServerSocket serverSocket;
     private Socket socket;
     private String filepath;

     
     public FileTransferHandler(String filePath)
     {
    	 try {
			serverSocket=new ServerSocket(0);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 port=serverSocket.getLocalPort();
    	 ip=(Inet4Address) serverSocket.getInetAddress();
    	 this.filepath=filePath;
     }

	public int getPort() {
		return port;
	}

	public Inet4Address getIp() {
		return ip;
	}
     
     @Override
    public void run() {

    	 try {
    		//设置等待超时时间，超过半分钟，则抛出超时异常，关闭serversocket;
    		 serverSocket.setSoTimeout(1000*30);
			socket=serverSocket.accept();
			serverSocket.close();
			
		} catch (SocketTimeoutException e) {
			try {
				serverSocket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null,  "对方没有接收文件","传输出错啦", JOptionPane.ERROR_MESSAGE); 
			return;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
            JPanel jPanel=new JPanel();
            jPanel.setBackground(Color.WHITE);
            
            try {
				ProgressMonitorInputStream pp=new ProgressMonitorInputStream(jPanel, "正在传输...", new FileInputStream(filepath));
				
				InputStream in=new BufferedInputStream(pp) ;
				OutputStream out=new BufferedOutputStream(socket.getOutputStream());
  	         
				byte[] b = new byte[1024*100];
				  int len = 0;
				   while ((len = in.read(b)) != -1) {
				        out.write(b);
				       System.out.println("发送了[" + len + "]个字节");
				       Thread.sleep(10);
				   }
				   System.out.println("文件发送完毕");
				   in.close();
				   out.close();
				   socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            JOptionPane.showMessageDialog(null,"发送完毕");
			
    }
     
}
