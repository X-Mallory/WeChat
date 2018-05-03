package com.cauc;

import java.net.Inet4Address;

import javax.management.loading.PrivateClassLoader;

public class FileMessage extends Message {
   public static final int REQUEST=0;
   public static final int REPLY=1;
   
   public static final int RESIVE=1;
   public static final int NORESIVE=0;
	
   private Inet4Address ip;
   private int port;
   private int request_or_reply;
   private int resive_or_noresive;
   private String fileName;
   private long filelength;
   
   public FileMessage(String srcUser,String dstUser,Inet4Address ip,int port,String fName,long fLength)
   {
	   super(srcUser, dstUser);
	   //默认构造方法创建的为一个请求消息
	   this.ip=ip;
	   this.port=port;
	   this.fileName=fName;
	   this.filelength=fLength;
	   request_or_reply=REQUEST;
	   resive_or_noresive=RESIVE;
   }
   
   public boolean isRequestFileMessage()
   {
	   if(request_or_reply==REQUEST)
		    return true;
	   else 
		    return false;
	
   }
   
   public boolean isResive()
   {
	   if(resive_or_noresive==RESIVE)
		    return true;
	   else 
		    return false;
   }
   
   
   
   
   
public String getFileName() {
	return fileName;
}

public void setFileName(String fileName) {
	this.fileName = fileName;
}

public long getFilelength() {
	return filelength;
}

public void setFilelength(long filelength) {
	this.filelength = filelength;
}

public Inet4Address getIp() {
	return ip;
}
public void setIp(Inet4Address ip) {
	this.ip = ip;
}
public int getPort() {
	return port;
}
public void setPort(int port) {
	this.port = port;
}
public int getRequest_or_reply() {
	return request_or_reply;
}
public void setRequest_or_reply(int request_or_reply) {
	this.request_or_reply = request_or_reply;
}
    
   
   
}
