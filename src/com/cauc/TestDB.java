package com.cauc;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class TestDB {
       
     public static void main(String args[]) throws FileNotFoundException, SQLException
     {
    	 Database bb=new Database();
    	 
    	 boolean aa=bb.insertUser("aa","bb", "cc@qq.com", "ÄÐ");
    	 System.out.println(aa);
    	 
    	 aa=bb.checkUserPassword("aa", "bb");
    	 System.out.println(aa);
     }
}
