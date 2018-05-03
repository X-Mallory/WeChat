package com.cauc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

public class Server extends JFrame {
	private SSLServerSocket serverSocket;
	private final int port = 9999;
	// 保存在线用户的用户名与Socket信息
	private final UserManager userManager = new UserManager();
	// “在线用户列表ListModel”,用于维护“在线用户列表”中显示的内容
	final DefaultTableModel onlineUsersDtm = new DefaultTableModel();
	// 用于控制时间信息显示格式
	// private final SimpleDateFormat dateFormat = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private final JPanel contentPane;
	private final JTable tableOnlineUsers;
	private final JTextPane textPaneMsgRecord;
	//用于记录离线消息的hashmap
	private HashMap<String, LinkedList<ChatMessage>> hashMap=new HashMap<String, LinkedList<ChatMessage>>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Server() {
		setTitle("\u670D\u52A1\u5668");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 561, 403);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JSplitPane splitPaneNorth = new JSplitPane();
		splitPaneNorth.setResizeWeight(0.5);
		contentPane.add(splitPaneNorth, BorderLayout.CENTER);

		JScrollPane scrollPaneMsgRecord = new JScrollPane();
		scrollPaneMsgRecord.setPreferredSize(new Dimension(100, 300));
		scrollPaneMsgRecord.setViewportBorder(new TitledBorder(null,
				"\u6D88\u606F\u8BB0\u5F55", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		splitPaneNorth.setLeftComponent(scrollPaneMsgRecord);

		textPaneMsgRecord = new JTextPane();
		textPaneMsgRecord.setPreferredSize(new Dimension(100, 100));
		scrollPaneMsgRecord.setViewportView(textPaneMsgRecord);

		JScrollPane scrollPaneOnlineUsers = new JScrollPane();
		scrollPaneOnlineUsers.setPreferredSize(new Dimension(100, 300));
		splitPaneNorth.setRightComponent(scrollPaneOnlineUsers);

		onlineUsersDtm.addColumn("用户名");
		onlineUsersDtm.addColumn("IP");
		onlineUsersDtm.addColumn("端口");
		onlineUsersDtm.addColumn("登录时间");
		tableOnlineUsers = new JTable(onlineUsersDtm);
		tableOnlineUsers.setPreferredSize(new Dimension(100, 270));
		tableOnlineUsers.setFillsViewportHeight(true); // 让JTable充满它的容器
		scrollPaneOnlineUsers.setViewportView(tableOnlineUsers);

		JPanel panelSouth = new JPanel();
		contentPane.add(panelSouth, BorderLayout.SOUTH);
		panelSouth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		final JButton btnStart = new JButton("\u542F\u52A8");
		// "启动"按钮
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//读取keystore文件，创建keymanager，创建SSlcontext,改变为SSLseverSocket

			try {
				String keyStoreFile = "test.keys";
			    String passphrase = "654321";
			    KeyStore ks = KeyStore.getInstance("JKS");
			    char[] password = passphrase.toCharArray();
			    ks.load(new FileInputStream(keyStoreFile), password);
			    KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			    kmf.init(ks, password);
                
			    SSLContext sslContext = SSLContext.getInstance("SSL");
			    sslContext.init(kmf.getKeyManagers(), null, null);
			    SSLServerSocketFactory factory=sslContext.getServerSocketFactory();
					// 创建ServerSocket打开端口8888监听客户端连接
					serverSocket = (SSLServerSocket)factory.createServerSocket(port);
					// 在“消息记录”文本框中用红色显示“服务器启动成功X”和启动时间信息
					String msgRecord = dateFormat.format(new Date())
							+ " 服务器启动成功" + "\r\n";
					addMsgRecord(msgRecord, Color.red, 12, false, false);
					// 创建并启动“接受用户连接线程”，接受并处理客户端连接请求
					new Thread() {
						@Override
						public void run() {
							while (true) {
								try {
									// 调用serverSocket.accept()方法接受用户连接请求
									Socket socket = serverSocket.accept();
									// 为新来的用户创建并启动“用户服务线程”
									// 并把serverSocket.accept()方法返回的socket对象交给“用户服务线程”来处理
									UserHandler userHandler = new UserHandler(
											socket);
									new Thread(userHandler).start();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						};
					}.start();
					btnStart.setEnabled(false);
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (KeyStoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (NoSuchAlgorithmException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (CertificateException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnrecoverableKeyException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (KeyManagementException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panelSouth.add(btnStart);
	}

	// 向消息记录文本框中添加一条消息记录
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
				Document docs = textPaneMsgRecord.getDocument();
				try {
					docs.insertString(docs.getLength(), msgRecord, attrset);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
	}

	class UserHandler implements Runnable {
		private final Socket currentUserSocket;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;

		public UserHandler(Socket currentUserSocket) {
			this.currentUserSocket = currentUserSocket;
			try {
				ois = new ObjectInputStream(currentUserSocket.getInputStream());
				oos = new ObjectOutputStream(
						currentUserSocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				while (true) {
					Message msg = (Message) ois.readObject();
					if (msg instanceof UserLoginMessage) {
						// 处理登陆消息
						processLoginMessage((UserLoginMessage) msg);
					}else if (msg instanceof UserStateMessage) {
						// 处理用户状态消息
						processUserStateMessage((UserStateMessage) msg);
					}else if (msg instanceof GroupMessage) {
						// 处理群聊消息
						 processGroupChatMessage((GroupMessage)msg);
					}else if (msg instanceof ChatMessage) {
						// 处理聊天消息
						processChatMessage((ChatMessage) msg);
					}else if (msg instanceof FileMessage) {
						// 处理文件传输消息
						processFileMessage( (FileMessage)msg);
						
					}
					
					/*if (msg instanceof UserStateMessage) {
						// 处理用户状态消息
						processUserStateMessage((UserStateMessage) msg);
					} else if (msg instanceof ChatMessage) {
						// 处理聊天消息
						processChatMessage((ChatMessage) msg);
					} else if (msg instanceof UserLoginMessage) {
						// 处理聊天消息
						processLoginMessage((UserLoginMessage) msg);
					}*/
					else {
						// 这种情况对应着用户发来的消息格式 错误，应该发消息提示用户，这里从略
						System.err.println("用户发来的消息格式错误!");
					}
				}
			} catch (IOException e) {
				if (e.toString().endsWith("Connection reset")) {
					System.out.println("客户端退出");
					// 如果用户未发送下线消息就直接关闭了客户端，应该在这里补充代码，删除用户在线信息
				} else {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (currentUserSocket != null) {
					try {
						currentUserSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// 向其它用户转发消息
		private void transferMsgToOtherUsers(Message msg) {
			String[] users = userManager.getAllUsers();
			for (String user : users) {
				if (userManager.getUserSocket(user) != currentUserSocket) {
					try {
						ObjectOutputStream o = userManager.getUserOos(user);
						synchronized (o) {
							o.writeObject(msg);
							o.flush();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
       //转发私聊消息
		public void transPrivatemsg(ChatMessage msg)
		{
			
			String dString=msg.getDstUser();
			if (userManager.getUserSocket(dString) != currentUserSocket) {
				try {
					ObjectOutputStream o = userManager.getUserOos(dString);
					synchronized (o) {
						o.writeObject(msg);
						o.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		// 处理用户状态消息
		private void processUserStateMessage(UserStateMessage msg) {
			String srcUser = msg.getSrcUser();
			if (msg.isUserOnline()) { // 用户上线消息
				System.out.println("用户上线："+msg.isUserOnline());
				if (userManager.hasUser(srcUser)) {
					// 这种情况对应着用户重复登录，应该发消息提示客户端，这里从略
					System.err.println("用户重复登录");
					return;
				}
				// 向新上线的用户转发当前在线用户列表
				String[] users = userManager.getAllUsers();
				
				try {
					for (String user : users) {
						
						UserStateMessage userStateMessage = new UserStateMessage(
								user, srcUser, true);
						
						synchronized (userStateMessage) {
							oos.writeObject(userStateMessage);
							oos.flush();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 向所有其它在线用户转发用户上线消息
				transferMsgToOtherUsers(msg);
				// 将用户信息加入到“在线用户”列表中
				onlineUsersDtm.addRow(new Object[] { srcUser,
						currentUserSocket.getInetAddress().getHostAddress(),
						currentUserSocket.getPort(),
						dateFormat.format(new Date()) });
				boolean tt=userManager.addUser(srcUser, currentUserSocket, oos, ois);
				System.out.println("添加用户上线情况:"+tt);
				// 用绿色文字将用户名和用户上线时间添加到“消息记录”文本框中
				String ip = currentUserSocket.getInetAddress().getHostAddress();
				final String msgRecord = dateFormat.format(new Date()) + " "
						+ srcUser + "(" + ip + ")" + "上线了!\r\n";
				addMsgRecord(msgRecord, Color.green, 12, false, false);
			
			    
				
				
				//查询是否有该用户的离线消息，如果有的话向该用户转发
				if(hashMap.get(msg.getSrcUser())!=null)
				{
					ObjectOutputStream offoos = userManager.getUserOos(msg.getSrcUser());
					
					for(ChatMessage ss:hashMap.get(msg.getSrcUser()))
					{
						try {
							synchronized (offoos) {
							offoos.writeObject(ss);
							offoos.flush();
}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//显示离线消息已经发送出去
						final String str = dateFormat.format(new Date()) + " "
								+ msg.getSrcUser()+"获取了"+ss.getSrcUser()+"发送的离线消息\r\n";
						addMsgRecord(str, new Color(255,140,0), 12, false, false);
					}
					//从hashmap中移除
				    hashMap.remove(msg.getSrcUser()) ;
				}
			
			} else { // 用户下线消息
				if (!userManager.hasUser(srcUser)) {
					// 这种情况对应着用户未发送上线消息就直接发送了下线消息，应该发消息提示客户端，这里从略
					System.err.println("用户未发送登录消息就发送了下线消息");
					return;
				}
				// 用绿色文字将用户名和用户下线时间添加到“消息记录”文本框中
				String ip = userManager.getUserSocket(srcUser).getInetAddress()
						.getHostAddress();
				final String msgRecord = dateFormat.format(new Date()) + " "
						+ srcUser + "(" + ip + ")" + "下线了!\r\n";
				addMsgRecord(msgRecord, Color.green, 12, false, false);
				// 在“在线用户列表”中删除下线用户
				userManager.removeUser(srcUser);
				
				for (int i = 0; i < onlineUsersDtm.getRowCount(); i++) {
					if (onlineUsersDtm.getValueAt(i, 0).equals(srcUser)) {
						onlineUsersDtm.removeRow(i);
					}
				}
				// 将用户下线消息转发给所有其它在线用户
				transferMsgToOtherUsers(msg);
			}
		}

		// 处理用户发来的聊天消息
		private void processChatMessage(ChatMessage msg) {
			String srcUser = msg.getSrcUser();
			String dstUser = msg.getDstUser();
			String msgContent = msg.getMsgContent();
			if (userManager.hasUser(srcUser)) {
				
				//如果是离线消息，将消息放到服务器的hashmap中
				if(msg.isIsoffLineMsg())
				{
					//用灰色文字显示离线消息
					final String msgRecord = dateFormat.format(new Date()) + " "
							+ srcUser + "向" + dstUser+ "发送离线消息\r\n";
					addMsgRecord(msgRecord, new Color(128,128,128), 12, false, false);
					
					//如果hashmap中已经有消息接收对象的键值对
					if(hashMap.get(msg.getDstUser())!=null)
					{
						hashMap.get(msg.getDstUser()).add(msg);
					}   //hashmap中还没有的话，新建一个
					else {
						LinkedList<ChatMessage> templist=new LinkedList<ChatMessage>();
						templist.add(msg);
						hashMap.put(msg.getDstUser(), templist);
					}
					
					return;
				}
				
				// 用黑色文字将收到消息的时间、发送消息的用户名和消息内容添加到“消息记录”文本框中
				final String msgRecord = dateFormat.format(new Date()) + " "
						+ srcUser +" 向 "+dstUser+ "说: " + msgContent +"\r\n";
				addMsgRecord(msgRecord, Color.black, 12, false, false);
				if (msg.isPubChatMessage()) {
					// 将公聊消息转发给所有其它在线用户
					transferMsgToOtherUsers(msg);
				} else {
					// 将私聊消息转发给目标用户，这里未实现
					transPrivatemsg(msg);
				}
			} else {
				
				// 这种情况对应着用户未发送上线消息就直接发送了聊天消息，应该发消息提示客户端，这里从略
				System.err.println("用户未发送上线消息就直接发送了聊天消息");
				return;
			}
		}
	     
		 //处理登陆信息
		private void processLoginMessage(UserLoginMessage msg) throws IOException {
				String nameString=msg.getSrcUser();
				String pswString=msg.getPsw();
				
				try {
					Database database=new Database();
					boolean flag=database.checkUserPassword(nameString, pswString);
							
					if(flag == true)
					{
						
						UserLoginMessage aa=new UserLoginMessage(nameString, "", "");
						aa.setflag(1);
						
					   oos.writeObject(aa);
				       oos.flush();
				      
					}
					else {
						UserLoginMessage aa=new UserLoginMessage(nameString, "", "");
						aa.setflag(0);
					   oos.writeObject(aa);
						
					      oos.flush();
					     
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
		        // 处理用户发来的群聊消息
				private void processGroupChatMessage(GroupMessage msg) {
					String srcUser = msg.getSrcUser();
					String groupname=msg.getGroupName();
					String msgContent = msg.getMsgContent();
					//将服务器收到的群聊消息中的全部成员打印
					
					System.out.println("*************8**");
					System.out.println("群聊发信人："+msg.getSrcUser());
					System.out.println("消息随机数："+msg.getFlag());
					System.out.println("消息中的成员数："+msg.getGroupNameList().size());
					System.out.println("消息中的成员：");
					for(String ss:msg.getGroupNameList())
					{
						System.out.println(ss);
					}
					System.out.println("****************");
					
					
					if (userManager.hasUser(srcUser)) {
						// 用黑色文字将收到消息的时间、发送消息的用户名和消息内容添加到“消息记录”文本框中
						if(msg.isJoinGroup())
						{
							final String msgRecord = dateFormat.format(new Date()) + " "
									+ srcUser +" 邀请 "+msg.getNewGroupmamber()+ "加入群 ：  " + msg.getGroupName() +"\r\n";
							addMsgRecord(msgRecord, Color.black, 12, false, false);
						}
						else if(msg.isOutGruop())
						{
							final String msgRecord = dateFormat.format(new Date()) + " "
									+ srcUser +" 退出群：  "+ msg.getGroupName() +"\r\n";
							addMsgRecord(msgRecord, Color.black, 12, false, false);
						}
						else {
							final String msgRecord = dateFormat.format(new Date()) + " "
								+ srcUser +" 向群  "+groupname+ "说: " + msgContent +"\r\n";
						addMsgRecord(msgRecord, Color.black, 12, false, false);
						}
						
						
							// 将群聊聊消息转发给所有群用户
						LinkedList<String> users = msg.getGroupNameList();
						
						for (String user : users) {
							
							if (userManager.getUserSocket(user) != currentUserSocket) {
								try {
									
									
									
									ObjectOutputStream o = userManager.getUserOos(user);
									synchronized (o) {
										o.writeObject(msg);
										o.flush();
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
						
					} else {
						// 这种情况对应着用户未发送上线消息就直接发送了聊天消息，应该发消息提示客户端，这里从略
						System.err.println("用户未发送上线消息就直接发送了聊天消息");
						return;
					}
				
					
				}
       
		//处理文件发送消息
		private void processFileMessage(FileMessage msg)
		{
			String dString=msg.getDstUser();
			String srcUser=msg.getSrcUser();
			String fileName=msg.getFileName();
			// 用黑色文字将收到消息的时间、发送消息的用户名和消息内容添加到“消息记录”文本框中
			final String msgRecord = dateFormat.format(new Date()) + " "
					+ srcUser +" 向 "+dString+ "发送文件 " +fileName+"\r\n";
			addMsgRecord(msgRecord, new Color(255,127,80), 12, false, false);
			
			if (userManager.getUserSocket(dString) != currentUserSocket) {
				try {
					ObjectOutputStream o = userManager.getUserOos(dString);
					synchronized (o) {
						o.writeObject(msg);
						o.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	    
		
		
	}
}

// 管理在线用户信息
class UserManager {
	private final Map<String, User> onLineUsers;

	public UserManager() {
		onLineUsers = new HashMap<String, User>();
	}

	// 判断某用户是否在线
	public boolean hasUser(String userName) {
		return onLineUsers.containsKey(userName);
	}

	// 判断在线用户列表是否空
	public boolean isEmpty() {
		return onLineUsers.isEmpty();
	}

	// 获取在线用户的Socket的的输出流封装成的对象输出流
	public ObjectOutputStream getUserOos(String userName) {
		if (hasUser(userName)) {
			return onLineUsers.get(userName).getOos();
		}
		return null;
	}

	// 获取在线用户的Socket的的输入流封装成的对象输入流
	public ObjectInputStream getUserOis(String userName) {
		if (hasUser(userName)) {
			return onLineUsers.get(userName).getOis();
		}
		return null;
	}

	// 获取在线用户的Socket
	public Socket getUserSocket(String userName) {
		if (hasUser(userName)) {
			return onLineUsers.get(userName).getSocket();
		}
		return null;
	}

	// 添加在线用户
	public boolean addUser(String userName, Socket userSocket) {
		if ((userName != null) && (userSocket != null)) {
			onLineUsers.put(userName, new User(userSocket));
			return true;
		}
		return false;
	}

	// 添加在线用户
	public boolean addUser(String userName, Socket userSocket,
			ObjectOutputStream oos, ObjectInputStream ios) {
		if ((userName != null) && (userSocket != null) && (oos != null)
				&& (ios != null)) {
			onLineUsers.put(userName, new User(userSocket, oos, ios));
			return true;
		}
		return false;
	}

	// 删除在线用户
	public boolean removeUser(String userName) {
		if (hasUser(userName)) {
			onLineUsers.remove(userName);
			return true;
		}
		return false;
	}

	// 获取所有在线用户名
	public String[] getAllUsers() {
		String[] users = new String[onLineUsers.size()];
		int i = 0;
		for (Map.Entry<String, User> entry : onLineUsers.entrySet()) {
			users[i++] = entry.getKey();
		}
		return users;
	}

	// 获取在线用户个数
	public int getOnlineUserCount() {
		return onLineUsers.size();
	}
}

class User {
	private final Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private final Date logonTime;

	public User(Socket socket) {
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		logonTime = new Date();
	}

	public User(Socket socket, ObjectOutputStream oos, ObjectInputStream ois) {
		this.socket = socket;
		this.oos = oos;
		this.ois = ois;
		logonTime = new Date();
	}

	public User(Socket socket, ObjectOutputStream oos, ObjectInputStream ois,
			Date logonTime) {
		this.socket = socket;
		this.oos = oos;
		this.ois = ois;
		this.logonTime = logonTime;
	}

	public Socket getSocket() {
		return socket;
	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public ObjectInputStream getOis() {
		return ois;
	}

	public Date getLogonTime() {
		return logonTime;
	}

}
