package com.cauc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

public class Progess extends SwingWorker<Integer, Integer> {

	private long length;
	private InputStream in;
	private OutputStream out;
	private JFrame jFrame;
	private JProgressBar progressbar;
	private String fname;
	private Socket socketf;
	
	public Progess(Socket socket,String filename,long filelength,String srcname)
	{
		length=filelength;
		this.socketf=socket;
		fname=filename;
		try {
			in=new BufferedInputStream(socketf.getInputStream()) ;
			FileOutputStream fileInputStream=new FileOutputStream("D:\\MyEclipse\\Workpath\\Wechat\\下载\\recive_"+srcname+"_"+fname);
			out=new BufferedOutputStream(fileInputStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    jFrame=new JFrame();
		
		jFrame.setSize(new Dimension(300,70));
		jFrame.setLocationRelativeTo(null);
		jFrame.setBackground(Color.WHITE);
		jFrame.setTitle("下载进度");
		jFrame.setResizable(false);
		jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		Container contentPanel=jFrame.getContentPane();
		
		progressbar=new JProgressBar();
		progressbar.setOrientation(JProgressBar.HORIZONTAL); 
		progressbar.setMinimum(0); 
		progressbar.setMaximum(100); 
		progressbar.setValue(0); 
		progressbar.setStringPainted(true); 
		progressbar.setPreferredSize(new Dimension(300,20)); 
		progressbar.setBorderPainted(true); 
		progressbar.setBackground(new Color(245,245,245)); 
		progressbar.setForeground(new Color(32,178,170));
		contentPanel.add(progressbar,BorderLayout.CENTER);
		jFrame.setVisible(true); 
	}
	
	protected Integer doInBackground() throws Exception {
		byte[] b = new byte[1024*1000 ];
		  int len = 0;
		  int progess=0;
		   while ((len = in.read(b)) != -1) {
		        out.write(b,0,len);
		        progess=progess+len;
		        System.err.println("progess:"+progess);
		        process(progess);
		       System.err.println("下载了[" + len + "]个字节");
		   }
        return len;
    }

	 protected void process(int x) {
		 float xx=(float)x;
		 float ll=(float)length;
		 float pp=xx/ll;
		   System.err.println("xx/length:"+pp);
		   //转成百分数乘100
	       progressbar.setValue((int) (pp*100));
	       System.err.println("int xx/length:"+(int)pp*100);
	    }

	 protected void done() 
	 {
		 try {
			in.close();
			 out.close();
			 socketf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 jFrame.dispose();
		 JOptionPane.showMessageDialog(null,"下载完成");
	 }
	
}
