package com.cauc;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.awt.Component;

import javax.management.loading.PrivateClassLoader;
import javax.swing.Box;
import javax.swing.DefaultListModel;

import java.awt.Dimension;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.derby.impl.sql.catalog.SYSPERMSRowFactory;

import javax.swing.UIManager;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import java.awt.Cursor;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionEvent;

public class Chat_Group extends JFrame {

	private JPanel contentPane;
	private String groupName;
	private volatile ArrayList<String> groupmember=new  ArrayList<String>() ;

	private JTextField textFieldMessage;
	private String src; 
    
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ios;
	//private Boolean flag_firstchat=true;
	private  DefaultListModel<String> groupmeberDlm = new DefaultListModel<String>();
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	//用于回调产生此群聊窗口的客户端，当私聊窗口被关闭的时候，调用client的removeChat_private方法将私聊窗口从map中移出；
	private Client client;
	private String filepath;
	private JTextPane textPaneMessage;
	private JLabel label_Groupname;
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Chat_Group frame = new Chat_Group();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
     
	
	public void addGroupmeberDlm(String name)
	{
		groupmeberDlm.addElement(name);
	}
	public void removeGroupmeberDlm(String name)
	{
		groupmeberDlm.removeElement(name);
	}
	
	public void setGroupmeberDlm(DefaultListModel<String> groupmeberDlm) {
		this.groupmeberDlm = groupmeberDlm;
	}

	

	public Client getClient() {
		return client;
	}



	public void setClient(Client client) {
		this.client = client;
	}



	public String getSrc() {
		return src;
	}



	public void setSrc(String src) {
		this.src = src;
	}



	public JLabel getLabel_Groupname() {
		return label_Groupname;
	}



	public void setLabel_Groupname(JLabel label_Groupname) {
		this.label_Groupname = label_Groupname;
	}



	public DefaultListModel<String> getGroupmeberDlm() {
		return groupmeberDlm;
	}


	public Socket getSocket() {
		return socket;
	}

    
	public void setSocket(Socket socket) {
		this.socket = socket;
	}


	public ObjectOutputStream getOos() {
		return oos;
	}


	public void setOos(ObjectOutputStream oos) {
		this.oos = oos;
	}


	public ObjectInputStream getIos() {
		return ios;
	}


	public void setIos(ObjectInputStream ios) {
		this.ios = ios;
	}


	public String getFilepath() {
		return filepath;
	}


	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}


	public String getGroupName() {
		return groupName;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public ArrayList<String> getGroupmember() {
		return groupmember;
	}


	public void setGroupmember(ArrayList<String> groupmember) {
		this.groupmember = groupmember;
	}
    
	private void addMsgRecord(final String msgRecord, Color msgColor,
			int fontSize, boolean isItalic, boolean isUnderline) {
		final SimpleAttributeSet attrset = new SimpleAttributeSet();
		StyleConstants.setForeground(attrset, msgColor);
		StyleConstants.setFontSize(attrset, fontSize);
		StyleConstants.setUnderline(attrset, isUnderline);
		StyleConstants.setItalic(attrset, isItalic);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Document docs = textPaneMessage.getDocument();
				try {
					docs.insertString(docs.getLength(), msgRecord, attrset);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
		 
		
	}

	/**
	 * Create the frame.
	 */
	public Chat_Group() {
		
		//添加对关闭事件的监听
				this.addWindowListener(new WindowAdapter ()
				{
				 @Override
		            public void windowClosing ( WindowEvent e )
		            {
					 GroupMessage msg=new GroupMessage(groupName, src,new LinkedList<String>(groupmember) , "***");
						msg.setJoinGroup(false);
						msg.setOutGruop(true);
						try {
							oos.writeObject(msg);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
		                 
						if (!getClient().removechat_Group(groupName) ){
							JOptionPane.showMessageDialog(null, "此群聊窗口未从map中移除", "窗口关闭出错啦", JOptionPane.ERROR_MESSAGE);
						}
					
		            }

				});
		
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 503, 423);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JPanel panelNouth = new JPanel();
		panelNouth.setBackground(new Color(144, 238, 144));
		
		JLabel label = new JLabel("\u7FA4\u804A\uFF1A");
		label.setForeground(Color.WHITE);
		label.setFont(new Font("等线 Light", Font.PLAIN, 18));
		
		label_Groupname = new JLabel("\u804A\u5929\u4EBA");
		label_Groupname.setForeground(new Color(95, 158, 160));
		label_Groupname.setFont(new Font("等线 Light", Font.PLAIN, 20));
		
		JPanel panelSouth = new JPanel();
		panelSouth.setBackground(Color.WHITE);
		
		textFieldMessage = new JTextField();
		textFieldMessage.setColumns(10);
		
		JButton buttonSentMessage = new JButton("\u53D1\u9001\u6D88\u606F");
		buttonSentMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String messagesString=textFieldMessage.getText();
				if(!messagesString.isEmpty())
				{
					GroupMessage msg=new GroupMessage(groupName, src, new LinkedList<String>(groupmember),messagesString);
					System.out.println();
					System.out.println("客户端发消息前的消息成员：");
					System.out.println(999999);
					System.out.println("发信人："+msg.getSrcUser());
					System.out.println("发信成员：");
					for(String tt:msg.getGroupNameList())
					{
						System.out.println(tt);
					}
					System.out.println(999999);
					try {
						synchronized (oos) {
							oos.writeObject(msg);
							oos.flush();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					textFieldMessage.setText("");
					// 在“消息记录”文本框中用蓝色显示发送的消息及发送时间
					String msgRecord = dateFormat.format(new Date()) + " "+src+"："
							+ msg.getMsgContent() + "\r\n";
					addMsgRecord(msgRecord, new Color(30,144,255), 12, false, false);
				}
				}
			
		});
		buttonSentMessage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonSentMessage.setForeground(new Color(60, 179, 113));
		buttonSentMessage.setFont(new Font("等线 Light", Font.BOLD, 14));
		buttonSentMessage.setBackground(Color.WHITE);
		
		JButton button_SendFile = new JButton("\u53D1\u9001\u6587\u4EF6");
		button_SendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			     if(button_SendFile.getText().equals("发送文件"))
	               {
					 JFileChooser fileChooser  =new JFileChooser("测试\\加密测试");
					 if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
					 {
						filepath=fileChooser.getSelectedFile().getPath();
						System.out.println("发送群文件："+filepath);
						
						textFieldMessage.setText(filepath);
						button_SendFile.setText("发送");
						return ;
						
					 }
	               }
	               if(button_SendFile.getText().equals("发送"))
	               {
	                   if(filepath.isEmpty() || filepath.equals("null"))
	                   {
	                	   button_SendFile.setText("发送文件");
	                	   JOptionPane.showMessageDialog(null,  "请重新选择文件！","文件出错啦", JOptionPane.ERROR_MESSAGE);
	                	   return ;
	                   }
	                   //创建一个文件对象，获取文件的长度，名字，后缀等
	                   File file=new File(filepath);
	                   String filename=file.getName();
	                   long filelength=file.length();
	                 
	                   new Thread(){
	                	   public void run() {
	                		   try {
	                			RandomAccessFile randomAccessFile=new RandomAccessFile(filepath, "r");
								service(randomAccessFile, filename, filelength);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	                	   };
	                   }.start();
	                   
	               }  
	                   /*//创建新的线程传输文件,先发送文件消息
	                   //FileTransferHandler fileTransferHandler=new FileTransferHandler(filepath);
	                    //创建seversocketchanl非阻塞传输文件
	                  // ServerSocket serverSocket=new ServerSocket(0);
	                 //静态工厂方法创建selector
	           		Selector selector=Selector.open();
	           		//静态工厂方法创建seversocketChannel;
	           		ServerSocketChannel seveChannel=ServerSocketChannel.open();
	           		//使得在同一个主机上关闭了服务器程序，紧接着在连接时
	           		//可以顺利的链接到同一个端口上，必须在绑定之前设置才有效
	           		seveChannel.socket().setReuseAddress(true);
	           		//默认SeverSocketchannel为阻塞模式，现在设置成非阻塞模式
	           		seveChannel.configureBlocking(false);
	           		//服务器绑定本地端口
	           		seveChannel.socket().bind(new InetSocketAddress(8866));
	           		System.out.println("服务器启动成功！");
	                Inet4Address ip=(Inet4Address)seveChannel.socket().getInetAddress();
	                int port=seveChannel.socket().getLocalPort();
	                  
	                   
	                   //new Thread(fileTransferHandler).start();
	                 button_SendFile.setText("发送文件");
	                 textFieldMessage.setText("");
	                   
	                  // System.out.println("port1:"+port);
	                  // System.out.println("ip1:"+ip);
	                   FileMessage filerquest=new FileMessage(srcNameString, dstNaString, ip, port,filename,filelength);
	                   filerquest.setRequest_or_reply(FileMessage.REQUEST);
	                   try {
							synchronized (oos) {
								oos.writeObject(filerquest);
								oos.flush();
							}
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						// 在“消息记录”文本框中用蓝色显示发送的消息及发送时间
						String msgRecord = dateFormat.format(new Date()) + " 向群"+groupName+"发送文件:"+filename+ "\r\n";
						addMsgRecord(msgRecord, new Color(60,179,113), 12, false, false);
	                   */
	                   
	                   //创建
	            	   /*
	            	    * **8***********
	            	    * 
	            	    * 
	            	    * 
	            	    * 
	            	    */
	            	   
	            
				
			}
		});
		button_SendFile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_SendFile.setForeground(new Color(32, 178, 170));
		button_SendFile.setFont(new Font("等线 Light", Font.BOLD, 14));
		button_SendFile.setBackground(Color.WHITE);
		GroupLayout gl_panelSouth = new GroupLayout(panelSouth);
		gl_panelSouth.setHorizontalGroup(
			gl_panelSouth.createParallelGroup(Alignment.TRAILING)
				.addGap(0, 452, Short.MAX_VALUE)
				.addGroup(gl_panelSouth.createSequentialGroup()
					.addComponent(textFieldMessage, GroupLayout.PREFERRED_SIZE, 346, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panelSouth.createParallelGroup(Alignment.LEADING)
						.addComponent(buttonSentMessage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(button_SendFile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panelSouth.setVerticalGroup(
			gl_panelSouth.createParallelGroup(Alignment.TRAILING)
				.addGap(0, 81, Short.MAX_VALUE)
				.addGroup(gl_panelSouth.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_panelSouth.createSequentialGroup()
						.addContainerGap()
						.addComponent(buttonSentMessage)
						.addGap(11)
						.addComponent(button_SendFile, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
					.addGroup(gl_panelSouth.createParallelGroup(Alignment.BASELINE)
						.addComponent(textFieldMessage, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)))
		);
		panelSouth.setLayout(gl_panelSouth);
		
		JSplitPane splitPaneCenter = new JSplitPane();
		splitPaneCenter.setDividerLocation(0.3);
		
		splitPaneCenter.setBackground(Color.WHITE);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
						.addComponent(splitPaneCenter, Alignment.LEADING)
						.addComponent(panelNouth, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
						.addComponent(panelSouth, GroupLayout.PREFERRED_SIZE, 452, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panelNouth, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(splitPaneCenter, GroupLayout.PREFERRED_SIZE, 259, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panelSouth, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		
		JButton btnNewButton = new JButton("\u52A0\u4EBA");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String newmember="";
				newmember=JOptionPane.showInputDialog("请输入添加的用户：");	
				if("".equals(newmember) || newmember==null)
					return;
				if(client.getOnlinUserDlm().contains(newmember))
				{   
					if(!groupmember.contains(src))
					{
						groupmember.add(src);
					}
					if(!groupmeberDlm.contains(src))
					{
						groupmeberDlm.addElement(src);
					}
					if(!groupmember.contains(newmember))
					{
						   System.out.println("新加入成员："+newmember);
					       groupmember.add(newmember);
					}
					if(!groupmeberDlm.contains(newmember))
					{
					       groupmeberDlm.addElement(newmember);
					}
					else
					{
					//JOptionPane.showMessageDialog(null,  "新加的成员已经在群聊中","加群出错啦", JOptionPane.ERROR_MESSAGE);
					//return;
					}
					
					GroupMessage msgss=new GroupMessage(groupName, src,new LinkedList<String>(groupmember) , "");
					msgss.setJoinGroup(true);
					msgss.setNewGroupmamber(newmember);
					///****
					///??????
					msgss.setGroupNameList(new LinkedList<String>(groupmember));
					msgss.setFlag(new Random().nextInt());
					//msgss.getGroupNameList().add(newmember);
					
					
					System.err.println();
					System.err.println("发送加人消息前");
					
					System.err.println("!!!!!");
			        System.err.println("发信人："+msgss.getSrcUser());
			        System.err.println("消息随机数："+msgss.getFlag());
			        System.err.println("消息成员数："+msgss.getGroupNameList().size());
			        System.err.println("消息成员：");
					for(String uu:msgss.getGroupNameList())
					{
						System.err.println(uu);
					}
					System.err.println("!!!!3");
					try {
						
							synchronized (oos) {
								oos.writeObject(msgss);
								oos.flush();
							}
							System.out.println();
							System.out.println("发送加人消息后");
							
							System.out.println("!333333");
					        System.out.println("发信人："+msgss.getSrcUser());
					        System.out.println("消息随机数："+msgss.getFlag());
					        System.out.println("消息成员数："+msgss.getGroupNameList().size());
					        System.out.println("消息成员：");
							for(String uu:msgss.getGroupNameList())
							{
								System.out.println(uu);
							}
							System.out.println("!333333");
							
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
				}
				else
				{
					JOptionPane.showMessageDialog(null,  "在线列表中没有此用户","添加出错啦", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton.setForeground(new Color(0, 139, 139));
		btnNewButton.setFont(new Font("等线 Light", Font.PLAIN, 14));
		btnNewButton.setBackground(Color.WHITE);
		btnNewButton.setVerticalAlignment(SwingConstants.BOTTOM);
		
		JButton button = new JButton("\u9000\u51FA\u7FA4\u804A");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GroupMessage msg=new GroupMessage(groupName, src,new LinkedList<String>(groupmember) , "***");
				msg.setJoinGroup(false);
				msg.setOutGruop(true);
				try {
					oos.writeObject(msg);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				client.chat_groupMap.get(groupName).dispose();
				if (!getClient().removechat_Group(groupName)) {
					JOptionPane.showMessageDialog(null, "此群聊窗口未从map中移除", "窗口关闭出错啦", JOptionPane.ERROR_MESSAGE);
				}
				
				
			}
		});
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setForeground(new Color(0, 139, 139));
		button.setFont(new Font("等线 Light", Font.PLAIN, 14));
		button.setBackground(Color.WHITE);
		button.setVerticalAlignment(SwingConstants.BOTTOM);
		GroupLayout gl_panelNouth = new GroupLayout(panelNouth);
		gl_panelNouth.setHorizontalGroup(
			gl_panelNouth.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelNouth.createSequentialGroup()
					.addGap(96)
					.addComponent(label)
					.addGap(18)
					.addComponent(label_Groupname)
					.addGap(66)
					.addComponent(btnNewButton)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(button)
					.addContainerGap())
		);
		gl_panelNouth.setVerticalGroup(
			gl_panelNouth.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelNouth.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_panelNouth.createParallelGroup(Alignment.BASELINE)
						.addComponent(button)
						.addComponent(btnNewButton)
						.addComponent(label)
						.addComponent(label_Groupname)))
		);
		panelNouth.setLayout(gl_panelNouth);
		
		JScrollPane scrollPane_left = new JScrollPane();
		scrollPane_left.setViewportBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "群成员",
				TitledBorder.LEADING, TitledBorder.TOP, new Font("等线 Light",Font.PLAIN, 14), new Color(102,205,170)));
		splitPaneCenter.setLeftComponent(scrollPane_left);
		
		JList listGroupmember = new JList<String>(groupmeberDlm);
		
		scrollPane_left.setViewportView(listGroupmember);
		
		JScrollPane scrollPane_right = new JScrollPane();
		scrollPane_right.setViewportBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "聊天记录",
				TitledBorder.LEADING, TitledBorder.TOP, new Font("等线 Light",Font.PLAIN, 14), new Color(102,205,170)));
		splitPaneCenter.setRightComponent(scrollPane_right);
		
		textPaneMessage = new JTextPane();
		
		textPaneMessage.setEditable(false);
		scrollPane_right.setViewportView(textPaneMessage);
		contentPane.setLayout(gl_contentPane);
	}
	public void show_Receive(GroupMessage msg)
	{
		String ss=msg.getSrcUser();
		String dd=msg.getDstUser();
		if(msg.isJoinGroup() )
		{

		// 用蓝色文字将收到加群消息添加到“消息记录”文本框中
		final String msgRecord = dateFormat.format(new Date())
				+ " " + ss + "邀请  " + msg.getNewGroupmamber() + "加入群聊\r\n";
		addMsgRecord(msgRecord, new Color(30,144,255), 12, false, false);
		
		
		}else if(msg.isOutGruop())
		{
		// 用红色色文字将收到加群消息添加到“消息记录”文本框中
		final String msgRecord = dateFormat.format(new Date())
				+ " " + ss  + "退出群聊\r\n";
		addMsgRecord(msgRecord, new Color(205,92,92), 12, false, false);
		}
		else
		{
			// 用灰色文字将群消息添加到“消息记录”文本框中
			final String msgRecord = dateFormat.format(new Date())
					+ " " + ss  + "说:"+msg.getMsgContent() +"\r\n";
			addMsgRecord(msgRecord, new Color(119,136,153), 12, false, false);
		}
		
	}
    
	
	//用于群发文件时的非阻塞工作方式
	public void service(RandomAccessFile ff,String filename,long filelength) throws IOException
	{
		
		       //静态工厂方法创建selector
			 Selector selector=Selector.open();
				//静态工厂方法创建seversocketChannel;
			ServerSocketChannel seveChannel=ServerSocketChannel.open();
				//使得在同一个主机上关闭了服务器程序，紧接着在连接时
				//可以顺利的链接到同一个端口上，必须在绑定之前设置才有效
			seveChannel.socket().setReuseAddress(true);
				//默认SeverSocketchannel为阻塞模式，现在设置成非阻塞模式
			seveChannel.configureBlocking(false);
			
				//服务器绑定本地端口
			seveChannel.socket().bind(new InetSocketAddress("localhost",0));
		    System.out.println("服务器启动成功！");
		       //sevenChannel向selector注册连接就绪事件
		    seveChannel.register(selector,SelectionKey.OP_ACCEPT);
		    
		    //SocketAddress socketAddress=seveChannel.socket().getLocalSocketAddress();
		    Inet4Address ip=(Inet4Address) seveChannel.socket().getInetAddress();
            int port=seveChannel.socket().getLocalPort();
		    System.out.println("群发文件绑定端口："+port);
            for(String str:groupmember)
            {
            	FileMessage filerquest=new FileMessage(src, str, ip,port,filename,filelength);
                filerquest.setRequest_or_reply(FileMessage.REQUEST);
                
						synchronized (oos) {
							oos.writeObject(filerquest);
							oos.flush();
						}
            }
            //当有注册事件发生时
		while(selector.select()>0)
		{
			//将发生的事件装到一个集合中
			Set readkeySet=selector.selectedKeys();
			//用一个迭代器来遍历这个集合
			Iterator iterator =readkeySet.iterator();
			//迭代器遍历集合
			while(iterator.hasNext())
			{
				//定义一个selectkey来读取事件
				SelectionKey key =null;
				 
				key=(SelectionKey) iterator.next();
				//读取事件后从集合中移除
				iterator.remove();
				
				//如果是链接就绪的事件
				if(key.isAcceptable())
				{
					//返回与selection对象关联的ServerSocketChannel对象（socket对象替代）
					ServerSocketChannel sscChannel =(ServerSocketChannel) key.channel();
					//定义一个与客户端连接的socketchannel
					
					SocketChannel socketChannel=sscChannel.accept();
					System.out.println("接到客户端连接，来自："+socketChannel.socket().getInetAddress()+":"+socketChannel.socket().getPort());
				    
					//设置这个socketchannel为非阻塞模式
					socketChannel .configureBlocking(false);
					
				    long n=0;
				    
					//这个socketChannel向selector注册准备好的事件和写就绪事件
					socketChannel.register(selector,SelectionKey.OP_READ|SelectionKey.OP_WRITE,n);
				    
					
				}
				if(key.isReadable())
				{
						
				}
				if(key.isWritable())
				{
					try {
						Writer(key,ff);	
					} catch (Exception e) {
						//当写有问题时，取消注册
						key.cancel();
					}
					
					
				}
				
			}
		}
			
	}
	
	public void Writer(SelectionKey key,RandomAccessFile file) throws InterruptedException
	{
		//返回对应注册此写就绪事件的SocketChannel
		SocketChannel socketChannel=(SocketChannel) key.channel();

		//返回附件，用于记录上一次发送数据的位置的整形数
		long n= (long)key.attachment();
		
		//定义字符串，每次从randomaccessfie中读出一行数据
		byte[] b = new byte[1024*100];
		
		try {
			//设置该文件流的偏移量指针，下一次读取时发生
			file.seek(n);
			
			file.read(b);
			//获取读完这一行之后的文件偏移量
		    n=file.getFilePointer();
			
		   
			
		    //将此偏移量重新写入当前socket对应的SelectKey的附件中
		    key.attach(n);
		    
		    //将此行数据转为字节数组，放到一个缓冲区中
		    ByteBuffer buffer=ByteBuffer.wrap(b);
		    
		    //发送此数据
		    Thread.sleep(50);// 
			socketChannel.write(buffer);
			  //如果n超过了文件的大小，说明发完了，取消注册，关闭
			if(n>=file.length())
			{
				key.cancel();
				socketChannel.close();
			}
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			
			System.out.println("写数据出错！");
		       }
	}
	


}
