package com.cauc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ProgressMonitorInputStream;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.derby.impl.store.access.conglomerate.TemplateRow;
import org.apache.derby.tools.sysinfo;
import java.awt.Cursor;

public class Client extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int port = 9999;
	private String serverIP="localhost";
	private SSLSocket socket;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	private String localUserName;
	// “在线用户列表ListModel”,用于维护“在线用户列表”中显示的内容
	private final DefaultListModel<String> onlinUserDlm = new DefaultListModel<String>();
	// 用于控制时间信息显示格式
	// private final SimpleDateFormat dateFormat = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private final JPanel contentPane;
	private final JTextField textFieldUserName;
	private final JTextField textFieldMsgToSend;
	private final JTextPane textPaneMsgRecord;
	private final JList<String> listOnlineUsers;
	private final JButton btnLogon;
	private final JButton btnSendMsg;
	private final JButton btnSendFile;
	private Client client;
	public  volatile Map<String, Chat_Private> chat_privateMap=new HashMap<String, Chat_Private>();
	public  volatile Map<String, Chat_Group> chat_groupMap=new HashMap<String, Chat_Group>();
	/**
	 * Launch the application.
	 */
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
     
	
	
	public DefaultListModel<String> getOnlinUserDlm() {
		return onlinUserDlm;
	}



	public JList<String> getListOnlineUsers() {
		return listOnlineUsers;
	}



	/**
	 * Create the frame.
	 */
	public Client() {
		setBackground(Color.LIGHT_GRAY);
		client=this;
		
		//添加对关闭事件的监听
				this.addWindowListener(new WindowAdapter ()
				{
				 @Override
		            public void windowClosing ( WindowEvent e )
		            {
							// 向服务器发送用户下线消息
							UserStateMessage userStateMessage = new UserStateMessage(
									localUserName, "", false);
							try {
								synchronized (oos) {
									oos.writeObject(userStateMessage);
									oos.flush();
								}
								
							} catch (IOException e1) {
								e1.printStackTrace();
							}
		            }

				});
		
		setTitle("\u5BA2\u6237\u7AEF");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 612, 397);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panelNorth = new JPanel();
		panelNorth.setBorder(new EmptyBorder(0, 0, 5, 0));
		contentPane.add(panelNorth, BorderLayout.NORTH);
		panelNorth.setLayout(new BoxLayout(panelNorth, BoxLayout.X_AXIS));

		JLabel lblUserName = new JLabel("\u7528\u6237\u540D\uFF1A");
		lblUserName.setFont(new Font("微软雅黑 Light", Font.PLAIN, 12));
		panelNorth.add(lblUserName);

		textFieldUserName = new JTextField();
		panelNorth.add(textFieldUserName);
		textFieldUserName.setColumns(10);
		
		Component horizontalStrut_8 = Box.createHorizontalStrut(20);
		panelNorth.add(horizontalStrut_8);
		
		Component horizontalStrut_7 = Box.createHorizontalStrut(20);
		panelNorth.add(horizontalStrut_7);
		
		Component horizontalStrut_6 = Box.createHorizontalStrut(20);
		panelNorth.add(horizontalStrut_6);
		
		JButton button_SendmessageoffLine = new JButton("\u53D1\u9001\u79BB\u7EBF\u6D88\u606F");
		button_SendmessageoffLine.setFont(new Font("微软雅黑 Light", Font.PLAIN, 12));
		button_SendmessageoffLine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowInput showInput=new ShowInput();
				showInput.setVisible(true);
			}
		});
		button_SendmessageoffLine.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_SendmessageoffLine.setBackground(Color.WHITE);
		panelNorth.add(button_SendmessageoffLine);
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		panelNorth.add(horizontalStrut_5);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		panelNorth.add(horizontalStrut_4);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelNorth.add(horizontalStrut);
		
		JButton button_creatGroup = new JButton("\u521B\u5EFA\u7FA4\u804A");
		button_creatGroup.setFont(new Font("微软雅黑 Light", Font.PLAIN, 12));
		button_creatGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//群名称
				String groupString="";
				groupString=JOptionPane.showInputDialog("请输入群名称：");
				if("".equals(groupString) || groupString==null)
					return;
				
				groupString=groupString+localUserName;
				Chat_Group group=new Chat_Group();
				group.setLocationRelativeTo(client);
				group.setVisible(true);  
	            //设置群聊名称
				group.getLabel_Groupname().setText(groupString);
	            //设置群聊的名称
				group.setGroupName(groupString);
				group.setSrc(localUserName);
	            //将此client传入群聊聊窗口，方便退出群聊窗口是回调方法移除map中的
				group.setClient(client);
	            //首先将自己加入群名单中
				group.getGroupmeberDlm().addElement(localUserName);
				group.getGroupmember().add(localUserName);
				
	            //将群聊聊天对象和聊天窗口放入map中，方便监听到群聊聊消息时，查找窗口显示
	            synchronized (chat_groupMap) {
	            	chat_groupMap.put(groupString, group);
				}
				//将当前的Socket 传送给公聊聊
	            group.setSocket(socket);
	           
	            group.setOos(oos);

	            group.setIos(ois);
	            
	            group.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}
		});
		button_creatGroup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		panelNorth.add(button_creatGroup);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panelNorth.add(horizontalStrut_1);

		btnLogon = new JButton("\u767B\u5F55"); // “登录”按钮
		btnLogon.setFont(new Font("微软雅黑 Light", Font.PLAIN, 12));
		btnLogon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnLogon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnLogon.getText().equals("登录")) {
					localUserName = textFieldUserName.getText().trim();
					if (localUserName.length() > 0) {
						// 与服务器端建立Socket连接，如果抛出异常，则弹出对话框通知用户，并退出
						try {
							//改变为SSLsocket
							String passphrase = "654321";
							char[] password = passphrase.toCharArray();
							String trustStoreFile = "test.keys";    
							KeyStore ts = KeyStore.getInstance("JKS");
							ts.load(new FileInputStream(trustStoreFile), password);
							TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
							tmf.init(ts);
							SSLContext sslContext = SSLContext.getInstance("SSL");
							sslContext.init(null, tmf.getTrustManagers(), null);		
							SSLSocketFactory factory=sslContext.getSocketFactory();
							socket=(SSLSocket)factory.createSocket("localhost",port);	
							
							//socket = new Socket("localhost", port);
							// 将socket的输入流和输出流分别封装成对象输入流和对象输出流
							oos = new ObjectOutputStream(socket
									.getOutputStream());
							ois = new ObjectInputStream(socket.getInputStream());
						} catch (UnknownHostException e1) {
							JOptionPane.showMessageDialog(null, "找不到服务器主机");
							e1.printStackTrace();
							System.exit(0);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null,
									"服务器I/O错误，服务器未启动？");
							e1.printStackTrace();
							System.exit(0);
						} catch (KeyStoreException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchAlgorithmException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (CertificateException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (KeyManagementException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						// 向服务器发送用户上线信息，将自己的用户名发送给服务器
						UserStateMessage userStateMessage = new UserStateMessage(
								localUserName, "", true);
						try {
							oos.writeObject(userStateMessage);
							oos.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						// 在“消息记录”文本框中用红色添加“XX时间登录成功”的信息
						String msgRecord = dateFormat.format(new Date())
								+ " 登录成功\r\n";
						addMsgRecord(msgRecord, Color.red, 12, false, false);
						// 创建并启动“后台监听线程”,监听并处理服务器传来的信息
						new Thread(new ListeningHandler()).start();
						// 将“登录”按钮设为“退出”按钮
						btnLogon.setText("退出");
						// 将发送文件按钮设为可用状态
						btnSendFile.setEnabled(true);
						// 将发送消息按钮设为可用状态
						btnSendMsg.setEnabled(true);
					}
				} else if (btnLogon.getText().equals("退出")) {
					if (JOptionPane.showConfirmDialog(null, "是否退出?", "退出确认",
							JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
						// 向服务器发送用户下线消息
						UserStateMessage userStateMessage = new UserStateMessage(
								localUserName, "", false);
						try {
							synchronized (oos) {
								oos.writeObject(userStateMessage);
								oos.flush();
							}
							client.dispose();
							//System.exit(0);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

			}
		});
		panelNorth.add(btnLogon);

		JSplitPane splitPaneCenter = new JSplitPane();
		splitPaneCenter.setResizeWeight(1.0);
		contentPane.add(splitPaneCenter, BorderLayout.CENTER);

		JScrollPane scrollPaneMsgRecord = new JScrollPane();
		scrollPaneMsgRecord.setViewportBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "\u6D88\u606F\u8BB0\u5F55",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPaneCenter.setLeftComponent(scrollPaneMsgRecord);

		textPaneMsgRecord = new JTextPane();
		scrollPaneMsgRecord.setViewportView(textPaneMsgRecord);

		JScrollPane scrollPaneOnlineUsers = new JScrollPane();
		scrollPaneOnlineUsers.setViewportBorder(new TitledBorder(null,
				"\u5728\u7EBF\u7528\u6237", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		splitPaneCenter.setRightComponent(scrollPaneOnlineUsers);

		listOnlineUsers = new JList<String>(onlinUserDlm);
	
		scrollPaneOnlineUsers.setViewportView(listOnlineUsers);
	    listOnlineUsers.addMouseListener(new MouseAdapter() {
	    	 
			public void mouseClicked(MouseEvent e) {
	                if(listOnlineUsers.getSelectedIndex() != -1) {
	                	        //当在线用户列表上发生单击事件时
	                           if(e.getClickCount() == 1) 
	                                        oneClick(listOnlineUsers.getSelectedValue());
	                             //当用户列表发生双击事件时
	                          if(e.getClickCount() == 2) 
	                                        twoClick(listOnlineUsers.getSelectedValue());
	                }
	        }
	    });
         
	   
	    
		JPanel panelSouth = new JPanel();
		panelSouth.setBorder(new EmptyBorder(5, 0, 0, 0));
		contentPane.add(panelSouth, BorderLayout.SOUTH);
		panelSouth.setLayout(new BoxLayout(panelSouth, BoxLayout.X_AXIS));

		textFieldMsgToSend = new JTextField();
		panelSouth.add(textFieldMsgToSend);
		textFieldMsgToSend.setColumns(10);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		panelSouth.add(horizontalStrut_2);

		btnSendMsg = new JButton("\u53D1\u9001\u6D88\u606F"); // “发送消息”按钮
		btnSendMsg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String msgContent = textFieldMsgToSend.getText();
				if (msgContent.length() > 0) {
					// 将消息文本框中的内容作为公聊消息发送给服务器
					ChatMessage chatMessage = new ChatMessage(localUserName,
							"", msgContent);
					try {
						synchronized (oos) {
							oos.writeObject(chatMessage);
							oos.flush();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					// 在“消息记录”文本框中用蓝色显示发送的消息及发送时间
					String msgRecord = dateFormat.format(new Date()) + " 向大家说:"
							+ msgContent + "\r\n";
					addMsgRecord(msgRecord, Color.blue, 12, false, false);
				}
			}
		});
		panelSouth.add(btnSendMsg);

		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		panelSouth.add(horizontalStrut_3);

		btnSendFile = new JButton("\u53D1\u9001\u6587\u4EF6");
		panelSouth.add(btnSendFile);

		// 将发送文件按钮设为不可用状态
		btnSendFile.setEnabled(false);
		// 将发送消息按钮设为不可用状态
		btnSendMsg.setEnabled(false);
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
	
	 public  boolean removechat_private(String nameString)
     {
    	 if(chat_privateMap.containsKey(nameString))
    	 {
    		 chat_privateMap.remove(nameString);
    		 return true;
    	 }
    	 return false;
     }
	 public  boolean removechat_Group(String nameString)
     {
    	 if(chat_groupMap.containsKey(nameString))
    	 {
    		 chat_groupMap.remove(nameString);
    		 return true;
    	 }
    	 return false;
     }

	// 后台监听线程
	class ListeningHandler implements Runnable {
		
		private Client client;
		
		public Client getClient() {
			return client;
		}

		public void setClient(Client client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				while (true) {
					Message msg = null;
					synchronized (ois) {
						msg = (Message) ois.readObject();
					}
					if (msg instanceof UserStateMessage) {
						// 处理用户状态消息
						processUserStateMessage((UserStateMessage) msg);
					} else if (msg instanceof GroupMessage) {
						// 处理群聊天消息
						//processChatMessage((ChatMessage) msg);
						processgroupMessage((GroupMessage) msg);
					}else if (msg instanceof ChatMessage) {
						// 处理聊天消息
						processChatMessage((ChatMessage) msg);
					} else if (msg instanceof FileMessage) {
						// 处理文件传输消息
						//processChatMessage((FileMessage) msg);
                         processFileMessage( (FileMessage)msg);
					}else {
						// 这种情况对应着用户发来的消息格式 错误，应该发消息提示用户，这里从略
						System.err.println("用户发来的消息格式错误!");
					}
				}
			} catch (IOException e) {
				if (e.toString().endsWith("Connection reset")) {
					System.out.println("服务器端退出");
				} else {
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// 处理用户状态消息
		private void processUserStateMessage(UserStateMessage msg) {
			String srcUser = msg.getSrcUser();
			String dstUser = msg.getDstUser();
			if (msg.isUserOnline()) {
				if (msg.isPubUserStateMessage()) { // 新用户上线消息
					// 用绿色文字将用户名和用户上线时间添加到“消息记录”文本框中
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "上线了!\r\n";
					addMsgRecord(msgRecord, Color.green, 12, false, false);
					// 在“在线用户”列表中增加新上线的用户名
					onlinUserDlm.addElement(srcUser);
				}
				
				if (dstUser.equals(localUserName)) { // 用户在线消息
					onlinUserDlm.addElement(srcUser);
				}
			} else if (msg.isUserOffline()) { // 用户下线消息
				if (onlinUserDlm.contains(srcUser)) {
					// 用绿色文字将用户名和用户下线时间添加到“消息记录”文本框中
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "下线了!\r\n";
					addMsgRecord(msgRecord, Color.green, 12, false, false);
					// 在“在线用户”列表中删除下线的用户名
					onlinUserDlm.removeElement(srcUser);
				}
			}
		}
         //处理群聊消息
		private void processgroupMessage(GroupMessage msg)
		{
			 Chat_Group temp=chat_groupMap.get(msg.getGroupName());
			 
		      //System.out.println("消息中的群名称："+msg.getGroupName());
			//处理收到的群聊消息,将消息显示到群聊的窗口上
			   if(temp != null)
			   {
				   System.out.println(temp.getSrc()+1111);
				   System.out.println();
				   System.out.println(localUserName+"收到群聊消息：");
				   System.out.println("%%%%%%%%");
				   System.out.println("发信人："+msg.getSrcUser());
				   System.out.println("消息成员：");
				   for(String dd:msg.getGroupNameList())
				   {
					   System.out.println(dd);
				   }
				   System.out.println("%%%%%%%%");
			       synchronized (temp) {
					if (msg.isJoinGroup()) {
						temp.getGroupmember().add(msg.getNewGroupmamber());
						temp.addGroupmeberDlm(msg.getNewGroupmamber());

					}
					if (msg.isOutGruop()) {
						System.out.println(2222);
						temp.getGroupmember().remove(msg.getSrcUser());
						temp.removeGroupmeberDlm(msg.getSrcUser());
					}
				}
				chat_groupMap.get(msg.getGroupName()).show_Receive(msg);  
			   }
			   else {
				  // System.out.println("接收到邀请加群的断点：加群人  收件人 "+msg.getNewGroupmamber()+"  "+localUserName);
				   if(msg.isJoinGroup() && msg.getNewGroupmamber().equals(localUserName))
				   {
					  int n=JOptionPane.showConfirmDialog(null, msg.getSrcUser()+"邀请你加入群聊  "+msg.getGroupName()+"，是否打开群聊窗口？", "群聊消息", JOptionPane.YES_NO_OPTION);
				      if(n==JOptionPane.YES_OPTION)
				      {
				            Chat_Group group1=new Chat_Group();
				            group1.setLocationRelativeTo(null);
				            group1.setVisible(true);  
				            //设置群聊上显示的聊天对象
				            group1.getLabel_Groupname().setText(msg.getGroupName());
				            //设置此私聊的用户和聊天对象的名字
				            group1.setGroupName(msg.getGroupName());
							group1.setSrc(localUserName);
				            //将此client传入群聊聊窗口，方便退出群聊窗口是回调方法移除map中的
							group1.setClient(client);
							//将消息中的群成员加到新建的群聊面板中
							for(String member : msg.getGroupNameList()) {
								group1.addGroupmeberDlm(member);
								group1.getGroupmember().add(member);
								}
							
				            
				            //将群聊聊天对象和聊天窗口放入map中，方便监听到群聊聊消息时，查找窗口显示
				            synchronized (chat_groupMap) {
				            	chat_groupMap.put(msg.getGroupName(), group1);
							}
							//将当前的Socket 传送给公聊
				            group1.setSocket(socket);
				           
				            group1.setOos(oos);

				            group1.setIos(ois);
				            
				            group1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				            
				      }
				      else {
				    	  //不接受的情况下发送下线消息，说明未加入群聊
						msg.setOutGruop(true);
						msg.setJoinGroup(false);
						msg.setSrcUser(localUserName);
						
						synchronized (oos) {
							try {
								oos.writeObject(msg);
								oos.flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
		         }else {
					
				}
			   }
		}
		
		// 处理服务器转发来的聊天消息
		private void processChatMessage(ChatMessage msg) {
			String srcUser = msg.getSrcUser();
			String dString = msg.getDstUser();
			String msgContent = msg.getMsgContent();
			if (onlinUserDlm.contains(srcUser)) {
				if (msg.isPubChatMessage()) {
					// 用黑色文字将收到消息的时间、发送消息的用户名和消息内容添加到“消息记录”文本框中
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "向大家说: " + msgContent + "\r\n";
					addMsgRecord(msgRecord, Color.black, 12, false, false);
				}
				else if(msg.isIsoffLineMsg())
				{
					// 用灰色文字将收到离线消息的时间、发送消息的用户名和消息内容添加到“消息记录”文本框中
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "向你发送离线消息: " + msgContent + "\r\n";
					addMsgRecord(msgRecord, new Color(128,128,128), 12, false, false);
				}
				else {
					  //处理收到的私聊消息,将消息显示到私聊的窗口上
					   if(chat_privateMap.get(srcUser) != null)
					   {
					       synchronized (chat_privateMap) {
							chat_privateMap.get(srcUser).show_Receive(msg);
						}
					   }
					   else {
						   if(dString.equals(localUserName))
						   {
							  int n=JOptionPane.showConfirmDialog(null, srcUser+"发来私聊消息，是否打开私聊窗口？", "私聊消息", JOptionPane.YES_NO_OPTION);
						      if(n==JOptionPane.YES_OPTION)
						      {
						            Chat_Private temPrivate=new Chat_Private();
						            temPrivate.setLocationRelativeTo(null);
						            temPrivate.setVisible(true);  
						            //设置私聊上显示的聊天对象
						            temPrivate.getLblNewLabel_dstName().setText(srcUser);
						            //设置此私聊的用户和聊天对象的名字
						            temPrivate.setSrcNameString(localUserName);
						            temPrivate.setDstNaString(srcUser);
						            //将此client传给chatprivate,方便回调
						            temPrivate.setClient(client);
						   
									//将聊天对象和聊天窗口放入map中，方便监听到私聊消息时，根据私聊人查找窗口显示
						            synchronized (chat_privateMap) {
										chat_privateMap.put(srcUser, temPrivate);
									}
									//将当前的Socket 传送给私聊
						            temPrivate.setSocket(socket);
						            
						            temPrivate.setOos(oos);
						            
						           
									temPrivate.setIos(ois);
						            temPrivate.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
									temPrivate.show_Receive(msg);
						           
						      }
						      else {
								
							  }
						   }
						
					}
				    }
			 }
		}

		
	
		
		
	}
	//处理服务器发来的文件消息
	private void processFileMessage(FileMessage msg)
	{
		String src=msg.getSrcUser();
		String dst=msg.getDstUser();
		Inet4Address ip=msg.getIp();
		int port=msg.getPort();
		String fileNameString=msg.getFileName();
		long fileLength=msg.getFilelength();
	    
		int n=JOptionPane.showConfirmDialog(null, src+"发来文件："+fileNameString+"\r\n"
	    		                                  +"文件大小："+fileLength/1000+"kB \r\n"
		                                          +"是否存到【下载】目录下？", "文件消息", JOptionPane.YES_NO_OPTION);
		if(n==JOptionPane.YES_OPTION)
		{
			
				try {
					System.out.println("port:"+port);
					System.out.println("ip:"+ip);
					
					Socket socket_file=new Socket(ip,port);
					
					//创建progess完成文件下载和进度显示
					Progess process=new Progess(socket_file, fileNameString, fileLength, src);
					process.execute();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
             
			
		}
	}
	
	private OutputStream FileOutputStream(String string) {
		// TODO Auto-generated method stub
		return null;
	}



	public void login(String localUserName)
	{
		     this.localUserName=localUserName;
			if (localUserName.length() > 0) {
				// 与服务器端建立Socket连接，如果抛出异常，则弹出对话框通知用户，并退出
				try {
					//改变为SSLsocket
					String passphrase = "654321";
					char[] password = passphrase.toCharArray();
					String trustStoreFile = "test.keys";    
					KeyStore ts = KeyStore.getInstance("JKS");
					ts.load(new FileInputStream(trustStoreFile), password);
					TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
					tmf.init(ts);
					SSLContext sslContext = SSLContext.getInstance("SSL");
					sslContext.init(null, tmf.getTrustManagers(), null);		
					SSLSocketFactory factory=sslContext.getSocketFactory();
					socket=(SSLSocket)factory.createSocket(serverIP,port);	
					//socket = new Socket("localhost", port);
					// 将socket的输入流和输出流分别封装成对象输入流和对象输出流
					oos = new ObjectOutputStream(socket
							.getOutputStream());
					ois = new ObjectInputStream(socket.getInputStream());
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(null, "找不到服务器主机");
					e1.printStackTrace();
					System.exit(0);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,
							"服务器I/O错误，服务器未启动？");
					e1.printStackTrace();
					System.exit(0);
				} catch (KeyStoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CertificateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (KeyManagementException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// 向服务器发送用户上线信息，将自己的用户名发送给服务器
				UserStateMessage userStateMessage = new UserStateMessage(
						localUserName, "", true);
				try {
					oos.writeObject(userStateMessage);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				// 在“消息记录”文本框中用红色添加“XX时间登录成功”的信息
				String msgRecord = dateFormat.format(new Date())
						+ " 登录成功\r\n";
				addMsgRecord(msgRecord, Color.red, 12, false, false);
				// 创建并启动“后台监听线程”,监听并处理服务器传来的信息
				ListeningHandler listeningHandler=new ListeningHandler();
				//将此client传到监听线程
				listeningHandler.setClient(this);
				new Thread(listeningHandler).start();
				// 将“登录”按钮设为“退出”按钮
				btnLogon.setText("退出");
				// 将发送文件按钮设为可用状态
				btnSendFile.setEnabled(true);
				// 将发送消息按钮设为可用状态
				btnSendMsg.setEnabled(true);
				textFieldUserName.setText(localUserName);
				textFieldUserName.setEditable(false);
				//passwordFieldPwd.setEditable(false);
			}
	}
	    //当在线用户列表发生单击事件时调用的方法
	    private void oneClick(Object value) {
	    
         }
	     ///当在线用户列表发生双击事件时调用的方法，传入参数为双击选中的用户名
         private void twoClick(Object value) {
        	 //需要对话的用户
            String dString= (String) value;
            Chat_Private temPrivate=new Chat_Private();
            temPrivate.setLocationRelativeTo(null);
            temPrivate.setVisible(true);  
            //设置私聊上显示的聊天对象
            temPrivate.getLblNewLabel_dstName().setText(dString);
            //设置此私聊的用户和聊天对象的名字
            temPrivate.setSrcNameString(localUserName);
            temPrivate.setDstNaString(dString);
            //将此client传入chatprivate私聊窗口，方便关闭私聊窗口是回调方法移除map中的chatprivate
            temPrivate.setClient(this);
            
            //将聊天对象和聊天窗口放入map中，方便监听到私聊消息时，根据私聊人查找窗口显示
            synchronized (chat_privateMap) {
				chat_privateMap.put(dString, temPrivate);
			}
			//将当前的Socket 传送给私聊
            temPrivate.setSocket(socket);
           
            temPrivate.setOos(oos);

			temPrivate.setIos(ois);
            
            temPrivate.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
         }
         
         
        


 class ShowInput extends JDialog {
               
        		private JPanel contentPane;
        		private JTextField textField_messageDst;
        		private JTextField textField_Message;

        		/**
        		 * Launch the application.
        		 *//*
        		public static void main(String[] args) {
        			EventQueue.invokeLater(new Runnable() {
        				public void run() {
        					try {
        						ShowInput frame = new ShowInput();
        						frame.setVisible(true);
        					} catch (Exception e) {
        						e.printStackTrace();
        					}
        				}
        			});
        		}
*/
        		/**
        		 * Create the frame.
        		 */
        		public ShowInput() {
        			ShowInput tt=this;
        			getContentPane().setBackground(Color.WHITE);
        			getContentPane().setLayout(null);
        			
        			JLabel label_messagedst = new JLabel("\u8F93\u5165\u6536\u4FE1\u4EBA\uFF1A");
        			label_messagedst.setForeground(SystemColor.activeCaption);
        			label_messagedst.setFont(new Font("微软雅黑 Light", Font.PLAIN, 18));
        			label_messagedst.setBounds(12, 10, 108, 27);
        			getContentPane().add(label_messagedst);
        			
        			textField_messageDst = new JTextField();
        			textField_messageDst.setForeground(SystemColor.activeCaption);
        			textField_messageDst.setFont(new Font("微软雅黑 Light", Font.PLAIN, 14));
        			textField_messageDst.setBounds(112, 48, 205, 30);
        			getContentPane().add(textField_messageDst);
        			textField_messageDst.setColumns(10);
        			
        			JLabel label_message = new JLabel("\u8F93\u5165\u6D88\u606F\uFF1A");
        			label_message.setForeground(SystemColor.activeCaption);
        			label_message.setFont(new Font("微软雅黑 Light", Font.PLAIN, 18));
        			label_message.setBounds(12, 82, 108, 27);
        			getContentPane().add(label_message);
        			
        			textField_Message = new JTextField();
        			textField_Message.setFont(new Font("微软雅黑 Light", Font.PLAIN, 15));
        			textField_Message.setBounds(95, 106, 249, 92);
        			getContentPane().add(textField_Message);
        			textField_Message.setColumns(10);
        			
        			JButton btn_send = new JButton("\u786E\u8BA4\u53D1\u9001");
        			btn_send.addActionListener(new ActionListener() {
        				public void actionPerformed(ActionEvent e) {
        					String messagedst=textField_messageDst.getText();
        	                String string=textField_Message.getText();
        	                if(onlinUserDlm.contains(messagedst))
        	                {
        	                	JOptionPane.showMessageDialog(null,  "该用户为在线用户","离线消息出错啦", JOptionPane.ERROR_MESSAGE);
        	                    return;
        	                }
        	                
        	                ChatMessage mm=new ChatMessage(localUserName, messagedst, string);
        	                mm.setIsoffLineMsg(true); 
        	                try {
        						oos.writeObject(mm);
        						oos.flush();
        					} catch (IOException e1) {
        						// TODO Auto-generated catch block
        						e1.printStackTrace();
        					}
        	                tt.dispose();
        	            }
        					
        				
        			});
        			btn_send.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        			btn_send.setBackground(Color.WHITE);
        			btn_send.setForeground(SystemColor.activeCaption);
        			btn_send.setFont(new Font("微软雅黑 Light", Font.PLAIN, 16));
        			btn_send.setBounds(141, 215, 119, 32);
        			getContentPane().add(btn_send);
        			
        			setTitle("输入离线消息内容");
        		    setModal(true);
        		    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        			setBounds(100, 100, 450, 300);
        			contentPane = new JPanel();
        			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        			contentPane.setLayout(new BorderLayout(0, 0));
        			//setContentPane(contentPane);
        		}
        	}        
}
         
         
 