package com.cauc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GroupMessage extends ChatMessage  {
	   private static final long serialVersionUID = -1466479389299512377L;
       private String groupName;
       private String newGroupmamber;
       private volatile  LinkedList<String> groupNameList;
       //新加群或者退群消息的标志
       private boolean outGruop;
       private boolean joinGroup;
       
       private int flag;
       
       
      public int getFlag() {
		return flag;
	}



	public void setFlag(int flag) {
		this.flag = flag;
	}



	public GroupMessage(String gName,String srcname,LinkedList<String> member,String msg) 
      {
    	  super(srcname, "***",msg);
    	  groupName=gName;
    	  newGroupmamber=null;
    	  groupNameList=member;
    	  outGruop=false;
    	  joinGroup=false;
    	  
      }
     
    //public addGroupmember()
   
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getNewGroupmamber() {
		return newGroupmamber;
	}
	public void setNewGroupmamber(String newGroupmamber) {
		this.newGroupmamber = newGroupmamber;
	}
	public LinkedList<String> getGroupNameList() {
		return groupNameList;
	}
	public void setGroupNameList(LinkedList<String> groupNameList) {
		this.groupNameList = groupNameList;
	}
	public boolean isOutGruop() {
		return outGruop;
	}
	public void setOutGruop(boolean outGruop) {
		this.outGruop = outGruop;
	}
	public boolean isJoinGroup() {
		return joinGroup;
	}
	public void setJoinGroup(boolean joinGroup) {
		this.joinGroup = joinGroup;
	}
       
       
       
       
}
