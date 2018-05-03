package com.cauc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/*import java.awt.BorderLayout; 
import java.awt.Component; 
import java.awt.FlowLayout; 
import java.awt.event.KeyEvent; 
import java.awt.event.KeyListener; 
import java.awt.event.MouseEvent; 
import java.awt.event.MouseListener; 
import java.awt.event.WindowAdapter; 
import java.awt.event.WindowEvent; 
import java.awt.event.WindowListener; 

import javax.swing.JCheckBox; 
import javax.swing.JFrame; 
import javax.swing.JLabel; 
import javax.swing.JList; 
import javax.swing.JPanel; 
import javax.swing.JScrollPane; 
import javax.swing.ListCellRenderer; 
import javax.swing.ListModel; 
import javax.swing.ListSelectionModel; 
import javax.swing.UIManager; 
import javax.swing.border.Border; 
import javax.swing.border.EmptyBorder; 
import javax.swing.border.EtchedBorder; 
import javax.swing.border.TitledBorder; 

public class Test extends JFrame { 

protected JList m_list; 
protected JLabel m_total; 

public Test() { 
super("Swing List [Check boxes]"); 
setSize(260, 240); 
getContentPane().setLayout(new FlowLayout()); 

InstallData[] options = { new InstallData("Program executable", 118), 
new InstallData("Help files", 52), 
new InstallData("Tools and converters", 83), 
new InstallData("Source code", 133) }; 

m_list = new JList(options); 
CheckListCellRenderer renderer = new CheckListCellRenderer(); 
m_list.setCellRenderer(renderer); 
m_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 

CheckListener lst = new CheckListener(this); 
m_list.addMouseListener(lst); 
m_list.addKeyListener(lst); 

JScrollPane ps = new JScrollPane(); 
ps.getViewport().add(m_list); 

m_total = new JLabel("Space required: 0K"); 

JPanel p = new JPanel(); 
p.setLayout(new BorderLayout()); 
p.add(ps, BorderLayout.CENTER); 
p.add(m_total, BorderLayout.SOUTH); 
p.setBorder(new TitledBorder(new EtchedBorder(), 
"Please select options:")); 
getContentPane().add(p); 

WindowListener wndCloser = new WindowAdapter() { 
public void windowClosing(WindowEvent e) { 
System.exit(0); 
} 
}; 
addWindowListener(wndCloser); 

setVisible(true); 

recalcTotal(); 
} 

public void recalcTotal() { 
ListModel model = m_list.getModel(); 
int total = 0; 
for (int k = 0; k < model.getSize(); k++) { 
InstallData data = (InstallData) model.getElementAt(k); 
if (data.isSelected()) 
total += data.getSize(); 
} 
m_total.setText("Space required: " + total + "K"); 
} 

public static void main(String argv[]) { 
new Test(); 
} 
} 

class CheckListCellRenderer extends JCheckBox implements ListCellRenderer { 
protected static Border m_noFocusBorder = new EmptyBorder(1, 1, 1, 1); 

public CheckListCellRenderer() { 
super(); 
setOpaque(true); 
setBorder(m_noFocusBorder); 
} 

public Component getListCellRendererComponent(JList list, Object value, 
int index, boolean isSelected, boolean cellHasFocus) { 
setText(value.toString()); 

setBackground(isSelected ? list.getSelectionBackground() : list 
.getBackground()); 
setForeground(isSelected ? list.getSelectionForeground() : list 
.getForeground()); 

InstallData data = (InstallData) value; 
setSelected(data.isSelected()); 

setFont(list.getFont()); 
setBorder((cellHasFocus) ? UIManager 
.getBorder("List.focusCellHighlightBorder") : m_noFocusBorder); 

return this; 
} 
} 

class CheckListener implements MouseListener, KeyListener { 
protected Test m_parent; 
protected JList m_list; 

public CheckListener(Test parent) { 
m_parent = parent; 
m_list = parent.m_list; 
} 

public void mouseClicked(MouseEvent e) { 
if (e.getX() < 20) 
doCheck(); 
} 

public void mousePressed(MouseEvent e) { 
} 

public void mouseReleased(MouseEvent e) { 
} 

public void mouseEntered(MouseEvent e) { 
} 

public void mouseExited(MouseEvent e) { 
} 

public void keyPressed(KeyEvent e) { 
if (e.getKeyChar() == ' ') 
doCheck(); 
} 

public void keyTyped(KeyEvent e) { 
} 

public void keyReleased(KeyEvent e) { 
} 

protected void doCheck() { 
int index = m_list.getSelectedIndex(); 
if (index < 0) 
return; 
InstallData data = (InstallData) m_list.getModel().getElementAt(index); 
data.invertSelected(); 
m_list.repaint(); 
m_parent.recalcTotal(); 
} 
} 

 class InstallData { 
protected String m_name; 
protected int m_size; 
protected boolean m_selected; 

public InstallData(String name, int size) { 
m_name = name; 
m_size = size; 
m_selected = false; 
} 

public String getName() { 
return m_name; 
} 

public int getSize() { 
return m_size; 
} 

public void setSelected(boolean selected) { 
m_selected = selected; 
} 

public void invertSelected() { 
m_selected = !m_selected; 
} 

public boolean isSelected() { 
return m_selected; 
} 

public String toString() { 
return m_name + " (" + m_size + " K)"; 
} 
} */


class Test {
	public static void main(String[] args) throws Exception {
		Random random=new Random();
		
		
	    FileOutputStream outputStream=new FileOutputStream("MessageTest.txt");
		
		ObjectOutputStream oos=new ObjectOutputStream(outputStream);
		 
		
		//第一次写
	    LinkedList<String> tempList=new LinkedList<String>();
		tempList.add("aaa");
		tempList.add("bbb");
		
	    TestMessage message=new TestMessage(random.nextInt(), tempList);
	    
	    System.out.println();
		System.out.println("************");
	    System.out.println("第一次读取对象前：");
	    System.out.println("随机数："+message.getFlag());
		for(String tt:message.getList())
		{
			System.out.println(tt);
		}
		System.out.println("***********");
	
	    oos.writeObject(message);
	    oos.flush();
	    
	    //第一次读
	    FileInputStream inputStream=new FileInputStream("MessageTest.txt");
	    ObjectInputStream ois=new ObjectInputStream(inputStream);
	    TestMessage afterMessage=null;
	    afterMessage=(TestMessage) ois.readObject();
	   
	    System.out.println();
	    System.out.println("!!!!!!!!!!!!!!!");
	    System.out.println("第一次读取对象后：");
	    System.out.println("随机数："+afterMessage.getFlag());
		for(String tt:afterMessage.getList())
		{
			System.out.println(tt);
		}
		System.out.println("!!!!!!!!!!!!!!!");
		
		//ois.close();
		
		
		
		
		//ObjectOutputStream oos_second=new ObjectOutputStream(new FileOutputStream("MessageTest.txt"));
		//第二次写
		tempList.add("ccc");
		tempList.add("ddd");
       TestMessage message_second=new TestMessage(random.nextInt(), tempList);
	    
	    System.out.println();
		System.out.println("************");
	    System.out.println("第二次读取对象前：");
	    System.out.println("随机数："+message_second.getFlag());
		for(String tt:message_second.getList())
		{
			System.out.println(tt);
		}
		System.out.println("***********");
	
		oos.writeObject(message_second);
		oos.flush();
	    
	    //第二次读
	   // ObjectInputStream ios_second=new ObjectInputStream(new FileInputStream("MessageTest.txt"));
	    TestMessage afterMessage_second=null;
	
	    afterMessage_second=(TestMessage) ois.readObject();
	   
	    System.out.println();
	    System.out.println("!!!!!!!!!!!!!!!");
	    System.out.println("第二次读取对象后：");
	    System.out.println("随机数："+afterMessage_second.getFlag());
		for(String tt:afterMessage_second.getList())
		{
			System.out.println(tt);
		}
		System.out.println("!!!!!!!!!!!!!!!");
		
		oos.close();
		ois.close();
		//ios_second.close();
		//oos_second.close();
		
	    
	}
	
	
	
	
	
	
}

class TestMessage implements Serializable
	{
		private int flag;
		private LinkedList<String> list;
		
		public TestMessage(int f,LinkedList<String> l)
		{
			flag=f;
			list=l;
		}

		public int getFlag() {
			return flag;
		}

		public void setFlag(int flag) {
			this.flag = flag;
		}

		public LinkedList<String> getList() {
			return list;
		}

		public void setList(LinkedList<String> list) {
			this.list = list;
		}
		
		
	}