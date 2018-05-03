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
    		//���õȴ���ʱʱ�䣬��������ӣ����׳���ʱ�쳣���ر�serversocket;
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
			JOptionPane.showMessageDialog(null,  "�Է�û�н����ļ�","���������", JOptionPane.ERROR_MESSAGE); 
			return;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
            JPanel jPanel=new JPanel();
            jPanel.setBackground(Color.WHITE);
            
            try {
				ProgressMonitorInputStream pp=new ProgressMonitorInputStream(jPanel, "���ڴ���...", new FileInputStream(filepath));
				
				InputStream in=new BufferedInputStream(pp) ;
				OutputStream out=new BufferedOutputStream(socket.getOutputStream());
  	         
				byte[] b = new byte[1024*100];
				  int len = 0;
				   while ((len = in.read(b)) != -1) {
				        out.write(b);
				       System.out.println("������[" + len + "]���ֽ�");
				       Thread.sleep(10);
				   }
				   System.out.println("�ļ��������");
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
            JOptionPane.showMessageDialog(null,"�������");
			
    }
     
}
