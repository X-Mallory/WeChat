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
	// �������û��б�ListModel��,����ά���������û��б�����ʾ������
	private final DefaultListModel<String> onlinUserDlm = new DefaultListModel<String>();
	// ���ڿ���ʱ����Ϣ��ʾ��ʽ
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
		
		//��ӶԹر��¼��ļ���
				this.addWindowListener(new WindowAdapter ()
				{
				 @Override
		            public void windowClosing ( WindowEvent e )
		            {
							// ������������û�������Ϣ
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
		lblUserName.setFont(new Font("΢���ź� Light", Font.PLAIN, 12));
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
		button_SendmessageoffLine.setFont(new Font("΢���ź� Light", Font.PLAIN, 12));
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
		button_creatGroup.setFont(new Font("΢���ź� Light", Font.PLAIN, 12));
		button_creatGroup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Ⱥ����
				String groupString="";
				groupString=JOptionPane.showInputDialog("������Ⱥ���ƣ�");
				if("".equals(groupString) || groupString==null)
					return;
				
				groupString=groupString+localUserName;
				Chat_Group group=new Chat_Group();
				group.setLocationRelativeTo(client);
				group.setVisible(true);  
	            //����Ⱥ������
				group.getLabel_Groupname().setText(groupString);
	            //����Ⱥ�ĵ�����
				group.setGroupName(groupString);
				group.setSrc(localUserName);
	            //����client����Ⱥ���Ĵ��ڣ������˳�Ⱥ�Ĵ����ǻص������Ƴ�map�е�
				group.setClient(client);
	            //���Ƚ��Լ�����Ⱥ������
				group.getGroupmeberDlm().addElement(localUserName);
				group.getGroupmember().add(localUserName);
				
	            //��Ⱥ�������������촰�ڷ���map�У����������Ⱥ������Ϣʱ�����Ҵ�����ʾ
	            synchronized (chat_groupMap) {
	            	chat_groupMap.put(groupString, group);
				}
				//����ǰ��Socket ���͸�������
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

		btnLogon = new JButton("\u767B\u5F55"); // ����¼����ť
		btnLogon.setFont(new Font("΢���ź� Light", Font.PLAIN, 12));
		btnLogon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnLogon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (btnLogon.getText().equals("��¼")) {
					localUserName = textFieldUserName.getText().trim();
					if (localUserName.length() > 0) {
						// ��������˽���Socket���ӣ�����׳��쳣���򵯳��Ի���֪ͨ�û������˳�
						try {
							//�ı�ΪSSLsocket
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
							// ��socket����������������ֱ��װ�ɶ����������Ͷ��������
							oos = new ObjectOutputStream(socket
									.getOutputStream());
							ois = new ObjectInputStream(socket.getInputStream());
						} catch (UnknownHostException e1) {
							JOptionPane.showMessageDialog(null, "�Ҳ�������������");
							e1.printStackTrace();
							System.exit(0);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(null,
									"������I/O���󣬷�����δ������");
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
						// ������������û�������Ϣ�����Լ����û������͸�������
						UserStateMessage userStateMessage = new UserStateMessage(
								localUserName, "", true);
						try {
							oos.writeObject(userStateMessage);
							oos.flush();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						// �ڡ���Ϣ��¼���ı������ú�ɫ��ӡ�XXʱ���¼�ɹ�������Ϣ
						String msgRecord = dateFormat.format(new Date())
								+ " ��¼�ɹ�\r\n";
						addMsgRecord(msgRecord, Color.red, 12, false, false);
						// ��������������̨�����̡߳�,�����������������������Ϣ
						new Thread(new ListeningHandler()).start();
						// ������¼����ť��Ϊ���˳�����ť
						btnLogon.setText("�˳�");
						// �������ļ���ť��Ϊ����״̬
						btnSendFile.setEnabled(true);
						// ��������Ϣ��ť��Ϊ����״̬
						btnSendMsg.setEnabled(true);
					}
				} else if (btnLogon.getText().equals("�˳�")) {
					if (JOptionPane.showConfirmDialog(null, "�Ƿ��˳�?", "�˳�ȷ��",
							JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
						// ������������û�������Ϣ
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
	                	        //�������û��б��Ϸ��������¼�ʱ
	                           if(e.getClickCount() == 1) 
	                                        oneClick(listOnlineUsers.getSelectedValue());
	                             //���û��б���˫���¼�ʱ
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

		btnSendMsg = new JButton("\u53D1\u9001\u6D88\u606F"); // ��������Ϣ����ť
		btnSendMsg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String msgContent = textFieldMsgToSend.getText();
				if (msgContent.length() > 0) {
					// ����Ϣ�ı����е�������Ϊ������Ϣ���͸�������
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
					// �ڡ���Ϣ��¼���ı���������ɫ��ʾ���͵���Ϣ������ʱ��
					String msgRecord = dateFormat.format(new Date()) + " ����˵:"
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

		// �������ļ���ť��Ϊ������״̬
		btnSendFile.setEnabled(false);
		// ��������Ϣ��ť��Ϊ������״̬
		btnSendMsg.setEnabled(false);
	}

	// ����Ϣ��¼�ı��������һ����Ϣ��¼
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

	// ��̨�����߳�
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
						// �����û�״̬��Ϣ
						processUserStateMessage((UserStateMessage) msg);
					} else if (msg instanceof GroupMessage) {
						// ����Ⱥ������Ϣ
						//processChatMessage((ChatMessage) msg);
						processgroupMessage((GroupMessage) msg);
					}else if (msg instanceof ChatMessage) {
						// ����������Ϣ
						processChatMessage((ChatMessage) msg);
					} else if (msg instanceof FileMessage) {
						// �����ļ�������Ϣ
						//processChatMessage((FileMessage) msg);
                         processFileMessage( (FileMessage)msg);
					}else {
						// ���������Ӧ���û���������Ϣ��ʽ ����Ӧ�÷���Ϣ��ʾ�û����������
						System.err.println("�û���������Ϣ��ʽ����!");
					}
				}
			} catch (IOException e) {
				if (e.toString().endsWith("Connection reset")) {
					System.out.println("���������˳�");
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

		// �����û�״̬��Ϣ
		private void processUserStateMessage(UserStateMessage msg) {
			String srcUser = msg.getSrcUser();
			String dstUser = msg.getDstUser();
			if (msg.isUserOnline()) {
				if (msg.isPubUserStateMessage()) { // ���û�������Ϣ
					// ����ɫ���ֽ��û������û�����ʱ����ӵ�����Ϣ��¼���ı�����
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "������!\r\n";
					addMsgRecord(msgRecord, Color.green, 12, false, false);
					// �ڡ������û����б������������ߵ��û���
					onlinUserDlm.addElement(srcUser);
				}
				
				if (dstUser.equals(localUserName)) { // �û�������Ϣ
					onlinUserDlm.addElement(srcUser);
				}
			} else if (msg.isUserOffline()) { // �û�������Ϣ
				if (onlinUserDlm.contains(srcUser)) {
					// ����ɫ���ֽ��û������û�����ʱ����ӵ�����Ϣ��¼���ı�����
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "������!\r\n";
					addMsgRecord(msgRecord, Color.green, 12, false, false);
					// �ڡ������û����б���ɾ�����ߵ��û���
					onlinUserDlm.removeElement(srcUser);
				}
			}
		}
         //����Ⱥ����Ϣ
		private void processgroupMessage(GroupMessage msg)
		{
			 Chat_Group temp=chat_groupMap.get(msg.getGroupName());
			 
		      //System.out.println("��Ϣ�е�Ⱥ���ƣ�"+msg.getGroupName());
			//�����յ���Ⱥ����Ϣ,����Ϣ��ʾ��Ⱥ�ĵĴ�����
			   if(temp != null)
			   {
				   System.out.println(temp.getSrc()+1111);
				   System.out.println();
				   System.out.println(localUserName+"�յ�Ⱥ����Ϣ��");
				   System.out.println("%%%%%%%%");
				   System.out.println("�����ˣ�"+msg.getSrcUser());
				   System.out.println("��Ϣ��Ա��");
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
				  // System.out.println("���յ������Ⱥ�Ķϵ㣺��Ⱥ��  �ռ��� "+msg.getNewGroupmamber()+"  "+localUserName);
				   if(msg.isJoinGroup() && msg.getNewGroupmamber().equals(localUserName))
				   {
					  int n=JOptionPane.showConfirmDialog(null, msg.getSrcUser()+"���������Ⱥ��  "+msg.getGroupName()+"���Ƿ��Ⱥ�Ĵ��ڣ�", "Ⱥ����Ϣ", JOptionPane.YES_NO_OPTION);
				      if(n==JOptionPane.YES_OPTION)
				      {
				            Chat_Group group1=new Chat_Group();
				            group1.setLocationRelativeTo(null);
				            group1.setVisible(true);  
				            //����Ⱥ������ʾ���������
				            group1.getLabel_Groupname().setText(msg.getGroupName());
				            //���ô�˽�ĵ��û���������������
				            group1.setGroupName(msg.getGroupName());
							group1.setSrc(localUserName);
				            //����client����Ⱥ���Ĵ��ڣ������˳�Ⱥ�Ĵ����ǻص������Ƴ�map�е�
							group1.setClient(client);
							//����Ϣ�е�Ⱥ��Ա�ӵ��½���Ⱥ�������
							for(String member : msg.getGroupNameList()) {
								group1.addGroupmeberDlm(member);
								group1.getGroupmember().add(member);
								}
							
				            
				            //��Ⱥ�������������촰�ڷ���map�У����������Ⱥ������Ϣʱ�����Ҵ�����ʾ
				            synchronized (chat_groupMap) {
				            	chat_groupMap.put(msg.getGroupName(), group1);
							}
							//����ǰ��Socket ���͸�����
				            group1.setSocket(socket);
				           
				            group1.setOos(oos);

				            group1.setIos(ois);
				            
				            group1.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				            
				      }
				      else {
				    	  //�����ܵ�����·���������Ϣ��˵��δ����Ⱥ��
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
		
		// ���������ת������������Ϣ
		private void processChatMessage(ChatMessage msg) {
			String srcUser = msg.getSrcUser();
			String dString = msg.getDstUser();
			String msgContent = msg.getMsgContent();
			if (onlinUserDlm.contains(srcUser)) {
				if (msg.isPubChatMessage()) {
					// �ú�ɫ���ֽ��յ���Ϣ��ʱ�䡢������Ϣ���û�������Ϣ������ӵ�����Ϣ��¼���ı�����
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "����˵: " + msgContent + "\r\n";
					addMsgRecord(msgRecord, Color.black, 12, false, false);
				}
				else if(msg.isIsoffLineMsg())
				{
					// �û�ɫ���ֽ��յ�������Ϣ��ʱ�䡢������Ϣ���û�������Ϣ������ӵ�����Ϣ��¼���ı�����
					final String msgRecord = dateFormat.format(new Date())
							+ " " + srcUser + "���㷢��������Ϣ: " + msgContent + "\r\n";
					addMsgRecord(msgRecord, new Color(128,128,128), 12, false, false);
				}
				else {
					  //�����յ���˽����Ϣ,����Ϣ��ʾ��˽�ĵĴ�����
					   if(chat_privateMap.get(srcUser) != null)
					   {
					       synchronized (chat_privateMap) {
							chat_privateMap.get(srcUser).show_Receive(msg);
						}
					   }
					   else {
						   if(dString.equals(localUserName))
						   {
							  int n=JOptionPane.showConfirmDialog(null, srcUser+"����˽����Ϣ���Ƿ��˽�Ĵ��ڣ�", "˽����Ϣ", JOptionPane.YES_NO_OPTION);
						      if(n==JOptionPane.YES_OPTION)
						      {
						            Chat_Private temPrivate=new Chat_Private();
						            temPrivate.setLocationRelativeTo(null);
						            temPrivate.setVisible(true);  
						            //����˽������ʾ���������
						            temPrivate.getLblNewLabel_dstName().setText(srcUser);
						            //���ô�˽�ĵ��û���������������
						            temPrivate.setSrcNameString(localUserName);
						            temPrivate.setDstNaString(srcUser);
						            //����client����chatprivate,����ص�
						            temPrivate.setClient(client);
						   
									//�������������촰�ڷ���map�У����������˽����Ϣʱ������˽���˲��Ҵ�����ʾ
						            synchronized (chat_privateMap) {
										chat_privateMap.put(srcUser, temPrivate);
									}
									//����ǰ��Socket ���͸�˽��
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
	//����������������ļ���Ϣ
	private void processFileMessage(FileMessage msg)
	{
		String src=msg.getSrcUser();
		String dst=msg.getDstUser();
		Inet4Address ip=msg.getIp();
		int port=msg.getPort();
		String fileNameString=msg.getFileName();
		long fileLength=msg.getFilelength();
	    
		int n=JOptionPane.showConfirmDialog(null, src+"�����ļ���"+fileNameString+"\r\n"
	    		                                  +"�ļ���С��"+fileLength/1000+"kB \r\n"
		                                          +"�Ƿ�浽�����ء�Ŀ¼�£�", "�ļ���Ϣ", JOptionPane.YES_NO_OPTION);
		if(n==JOptionPane.YES_OPTION)
		{
			
				try {
					System.out.println("port:"+port);
					System.out.println("ip:"+ip);
					
					Socket socket_file=new Socket(ip,port);
					
					//����progess����ļ����غͽ�����ʾ
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
				// ��������˽���Socket���ӣ�����׳��쳣���򵯳��Ի���֪ͨ�û������˳�
				try {
					//�ı�ΪSSLsocket
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
					// ��socket����������������ֱ��װ�ɶ����������Ͷ��������
					oos = new ObjectOutputStream(socket
							.getOutputStream());
					ois = new ObjectInputStream(socket.getInputStream());
				} catch (UnknownHostException e1) {
					JOptionPane.showMessageDialog(null, "�Ҳ�������������");
					e1.printStackTrace();
					System.exit(0);
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null,
							"������I/O���󣬷�����δ������");
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
				// ������������û�������Ϣ�����Լ����û������͸�������
				UserStateMessage userStateMessage = new UserStateMessage(
						localUserName, "", true);
				try {
					oos.writeObject(userStateMessage);
					oos.flush();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				// �ڡ���Ϣ��¼���ı������ú�ɫ��ӡ�XXʱ���¼�ɹ�������Ϣ
				String msgRecord = dateFormat.format(new Date())
						+ " ��¼�ɹ�\r\n";
				addMsgRecord(msgRecord, Color.red, 12, false, false);
				// ��������������̨�����̡߳�,�����������������������Ϣ
				ListeningHandler listeningHandler=new ListeningHandler();
				//����client���������߳�
				listeningHandler.setClient(this);
				new Thread(listeningHandler).start();
				// ������¼����ť��Ϊ���˳�����ť
				btnLogon.setText("�˳�");
				// �������ļ���ť��Ϊ����״̬
				btnSendFile.setEnabled(true);
				// ��������Ϣ��ť��Ϊ����״̬
				btnSendMsg.setEnabled(true);
				textFieldUserName.setText(localUserName);
				textFieldUserName.setEditable(false);
				//passwordFieldPwd.setEditable(false);
			}
	}
	    //�������û��б��������¼�ʱ���õķ���
	    private void oneClick(Object value) {
	    
         }
	     ///�������û��б���˫���¼�ʱ���õķ������������Ϊ˫��ѡ�е��û���
         private void twoClick(Object value) {
        	 //��Ҫ�Ի����û�
            String dString= (String) value;
            Chat_Private temPrivate=new Chat_Private();
            temPrivate.setLocationRelativeTo(null);
            temPrivate.setVisible(true);  
            //����˽������ʾ���������
            temPrivate.getLblNewLabel_dstName().setText(dString);
            //���ô�˽�ĵ��û���������������
            temPrivate.setSrcNameString(localUserName);
            temPrivate.setDstNaString(dString);
            //����client����chatprivate˽�Ĵ��ڣ�����ر�˽�Ĵ����ǻص������Ƴ�map�е�chatprivate
            temPrivate.setClient(this);
            
            //�������������촰�ڷ���map�У����������˽����Ϣʱ������˽���˲��Ҵ�����ʾ
            synchronized (chat_privateMap) {
				chat_privateMap.put(dString, temPrivate);
			}
			//����ǰ��Socket ���͸�˽��
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
        			label_messagedst.setFont(new Font("΢���ź� Light", Font.PLAIN, 18));
        			label_messagedst.setBounds(12, 10, 108, 27);
        			getContentPane().add(label_messagedst);
        			
        			textField_messageDst = new JTextField();
        			textField_messageDst.setForeground(SystemColor.activeCaption);
        			textField_messageDst.setFont(new Font("΢���ź� Light", Font.PLAIN, 14));
        			textField_messageDst.setBounds(112, 48, 205, 30);
        			getContentPane().add(textField_messageDst);
        			textField_messageDst.setColumns(10);
        			
        			JLabel label_message = new JLabel("\u8F93\u5165\u6D88\u606F\uFF1A");
        			label_message.setForeground(SystemColor.activeCaption);
        			label_message.setFont(new Font("΢���ź� Light", Font.PLAIN, 18));
        			label_message.setBounds(12, 82, 108, 27);
        			getContentPane().add(label_message);
        			
        			textField_Message = new JTextField();
        			textField_Message.setFont(new Font("΢���ź� Light", Font.PLAIN, 15));
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
        	                	JOptionPane.showMessageDialog(null,  "���û�Ϊ�����û�","������Ϣ������", JOptionPane.ERROR_MESSAGE);
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
        			btn_send.setFont(new Font("΢���ź� Light", Font.PLAIN, 16));
        			btn_send.setBounds(141, 215, 119, 32);
        			getContentPane().add(btn_send);
        			
        			setTitle("����������Ϣ����");
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
         
         
 