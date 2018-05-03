package com.cauc;

class UserLoginMessage extends Message
{
	private String psw;
	private int flag;
	
	public UserLoginMessage(String srcUser, String dstUser, String pp) {
		super(srcUser, dstUser);
		this.psw = pp;
		this.flag=0;
	}
	
	public String getPsw() {
		return psw;
	}

	public void setPsw(String psw) {
		this.psw = psw;
	}
	
	public int getflag() {
		return flag;
	}

	public void setflag(int flag) {
		this.flag = flag;
	}
	
}