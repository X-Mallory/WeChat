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
	// ���������û����û�����Socket��Ϣ
	private final UserManager userManager = new UserManager();
	// �������û��б�ListModel��,����ά���������û��б�����ʾ������
	final DefaultTableModel onlineUsersDtm = new DefaultTableModel();
	// ���ڿ���ʱ����Ϣ��ʾ��ʽ
	// private final SimpleDateFormat dateFormat = new
	// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	private final JPanel contentPane;
	private final JTable tableOnlineUsers;
	private final JTextPane textPaneMsgRecord;
	//���ڼ�¼������Ϣ��hashmap
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

		onlineUsersDtm.addColumn("�û���");
		onlineUsersDtm.addColumn("IP");
		onlineUsersDtm.addColumn("�˿�");
		onlineUsersDtm.addColumn("��¼ʱ��");
		tableOnlineUsers = new JTable(onlineUsersDtm);
		tableOnlineUsers.setPreferredSize(new Dimension(100, 270));
		tableOnlineUsers.setFillsViewportHeight(true); // ��JTable������������
		scrollPaneOnlineUsers.setViewportView(tableOnlineUsers);

		JPanel panelSouth = new JPanel();
		contentPane.add(panelSouth, BorderLayout.SOUTH);
		panelSouth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		final JButton btnStart = new JButton("\u542F\u52A8");
		// "����"��ť
		btnStart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//��ȡkeystore�ļ�������keymanager������SSlcontext,�ı�ΪSSLseverSocket

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
					// ����ServerSocket�򿪶˿�8888�����ͻ�������
					serverSocket = (SSLServerSocket)factory.createServerSocket(port);
					// �ڡ���Ϣ��¼���ı������ú�ɫ��ʾ�������������ɹ�X��������ʱ����Ϣ
					String msgRecord = dateFormat.format(new Date())
							+ " �����������ɹ�" + "\r\n";
					addMsgRecord(msgRecord, Color.red, 12, false, false);
					// �����������������û������̡߳������ܲ�����ͻ�����������
					new Thread() {
						@Override
						public void run() {
							while (true) {
								try {
									// ����serverSocket.accept()���������û���������
									Socket socket = serverSocket.accept();
									// Ϊ�������û��������������û������̡߳�
									// ����serverSocket.accept()�������ص�socket���󽻸����û������̡߳�������
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
						// �����½��Ϣ
						processLoginMessage((UserLoginMessage) msg);
					}else if (msg instanceof UserStateMessage) {
						// �����û�״̬��Ϣ
						processUserStateMessage((UserStateMessage) msg);
					}else if (msg instanceof GroupMessage) {
						// ����Ⱥ����Ϣ
						 processGroupChatMessage((GroupMessage)msg);
					}else if (msg instanceof ChatMessage) {
						// ����������Ϣ
						processChatMessage((ChatMessage) msg);
					}else if (msg instanceof FileMessage) {
						// �����ļ�������Ϣ
						processFileMessage( (FileMessage)msg);
						
					}
					
					/*if (msg instanceof UserStateMessage) {
						// �����û�״̬��Ϣ
						processUserStateMessage((UserStateMessage) msg);
					} else if (msg instanceof ChatMessage) {
						// ����������Ϣ
						processChatMessage((ChatMessage) msg);
					} else if (msg instanceof UserLoginMessage) {
						// ����������Ϣ
						processLoginMessage((UserLoginMessage) msg);
					}*/
					else {
						// ���������Ӧ���û���������Ϣ��ʽ ����Ӧ�÷���Ϣ��ʾ�û����������
						System.err.println("�û���������Ϣ��ʽ����!");
					}
				}
			} catch (IOException e) {
				if (e.toString().endsWith("Connection reset")) {
					System.out.println("�ͻ����˳�");
					// ����û�δ����������Ϣ��ֱ�ӹر��˿ͻ��ˣ�Ӧ�������ﲹ����룬ɾ���û�������Ϣ
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

		// �������û�ת����Ϣ
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
       //ת��˽����Ϣ
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
		// �����û�״̬��Ϣ
		private void processUserStateMessage(UserStateMessage msg) {
			String srcUser = msg.getSrcUser();
			if (msg.isUserOnline()) { // �û�������Ϣ
				System.out.println("�û����ߣ�"+msg.isUserOnline());
				if (userManager.hasUser(srcUser)) {
					// ���������Ӧ���û��ظ���¼��Ӧ�÷���Ϣ��ʾ�ͻ��ˣ��������
					System.err.println("�û��ظ���¼");
					return;
				}
				// �������ߵ��û�ת����ǰ�����û��б�
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
				// ���������������û�ת���û�������Ϣ
				transferMsgToOtherUsers(msg);
				// ���û���Ϣ���뵽�������û����б���
				onlineUsersDtm.addRow(new Object[] { srcUser,
						currentUserSocket.getInetAddress().getHostAddress(),
						currentUserSocket.getPort(),
						dateFormat.format(new Date()) });
				boolean tt=userManager.addUser(srcUser, currentUserSocket, oos, ois);
				System.out.println("����û��������:"+tt);
				// ����ɫ���ֽ��û������û�����ʱ����ӵ�����Ϣ��¼���ı�����
				String ip = currentUserSocket.getInetAddress().getHostAddress();
				final String msgRecord = dateFormat.format(new Date()) + " "
						+ srcUser + "(" + ip + ")" + "������!\r\n";
				addMsgRecord(msgRecord, Color.green, 12, false, false);
			
			    
				
				
				//��ѯ�Ƿ��и��û���������Ϣ������еĻ�����û�ת��
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
						
						//��ʾ������Ϣ�Ѿ����ͳ�ȥ
						final String str = dateFormat.format(new Date()) + " "
								+ msg.getSrcUser()+"��ȡ��"+ss.getSrcUser()+"���͵�������Ϣ\r\n";
						addMsgRecord(str, new Color(255,140,0), 12, false, false);
					}
					//��hashmap���Ƴ�
				    hashMap.remove(msg.getSrcUser()) ;
				}
			
			} else { // �û�������Ϣ
				if (!userManager.hasUser(srcUser)) {
					// ���������Ӧ���û�δ����������Ϣ��ֱ�ӷ�����������Ϣ��Ӧ�÷���Ϣ��ʾ�ͻ��ˣ��������
					System.err.println("�û�δ���͵�¼��Ϣ�ͷ�����������Ϣ");
					return;
				}
				// ����ɫ���ֽ��û������û�����ʱ����ӵ�����Ϣ��¼���ı�����
				String ip = userManager.getUserSocket(srcUser).getInetAddress()
						.getHostAddress();
				final String msgRecord = dateFormat.format(new Date()) + " "
						+ srcUser + "(" + ip + ")" + "������!\r\n";
				addMsgRecord(msgRecord, Color.green, 12, false, false);
				// �ڡ������û��б���ɾ�������û�
				userManager.removeUser(srcUser);
				
				for (int i = 0; i < onlineUsersDtm.getRowCount(); i++) {
					if (onlineUsersDtm.getValueAt(i, 0).equals(srcUser)) {
						onlineUsersDtm.removeRow(i);
					}
				}
				// ���û�������Ϣת�����������������û�
				transferMsgToOtherUsers(msg);
			}
		}

		// �����û�������������Ϣ
		private void processChatMessage(ChatMessage msg) {
			String srcUser = msg.getSrcUser();
			String dstUser = msg.getDstUser();
			String msgContent = msg.getMsgContent();
			if (userManager.hasUser(srcUser)) {
				
				//�����������Ϣ������Ϣ�ŵ���������hashmap��
				if(msg.isIsoffLineMsg())
				{
					//�û�ɫ������ʾ������Ϣ
					final String msgRecord = dateFormat.format(new Date()) + " "
							+ srcUser + "��" + dstUser+ "����������Ϣ\r\n";
					addMsgRecord(msgRecord, new Color(128,128,128), 12, false, false);
					
					//���hashmap���Ѿ�����Ϣ���ն���ļ�ֵ��
					if(hashMap.get(msg.getDstUser())!=null)
					{
						hashMap.get(msg.getDstUser()).add(msg);
					}   //hashmap�л�û�еĻ����½�һ��
					else {
						LinkedList<ChatMessage> templist=new LinkedList<ChatMessage>();
						templist.add(msg);
						hashMap.put(msg.getDstUser(), templist);
					}
					
					return;
				}
				
				// �ú�ɫ���ֽ��յ���Ϣ��ʱ�䡢������Ϣ���û�������Ϣ������ӵ�����Ϣ��¼���ı�����
				final String msgRecord = dateFormat.format(new Date()) + " "
						+ srcUser +" �� "+dstUser+ "˵: " + msgContent +"\r\n";
				addMsgRecord(msgRecord, Color.black, 12, false, false);
				if (msg.isPubChatMessage()) {
					// ��������Ϣת�����������������û�
					transferMsgToOtherUsers(msg);
				} else {
					// ��˽����Ϣת����Ŀ���û�������δʵ��
					transPrivatemsg(msg);
				}
			} else {
				
				// ���������Ӧ���û�δ����������Ϣ��ֱ�ӷ�����������Ϣ��Ӧ�÷���Ϣ��ʾ�ͻ��ˣ��������
				System.err.println("�û�δ����������Ϣ��ֱ�ӷ�����������Ϣ");
				return;
			}
		}
	     
		 //�����½��Ϣ
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
		        // �����û�������Ⱥ����Ϣ
				private void processGroupChatMessage(GroupMessage msg) {
					String srcUser = msg.getSrcUser();
					String groupname=msg.getGroupName();
					String msgContent = msg.getMsgContent();
					//���������յ���Ⱥ����Ϣ�е�ȫ����Ա��ӡ
					
					System.out.println("*************8**");
					System.out.println("Ⱥ�ķ����ˣ�"+msg.getSrcUser());
					System.out.println("��Ϣ�������"+msg.getFlag());
					System.out.println("��Ϣ�еĳ�Ա����"+msg.getGroupNameList().size());
					System.out.println("��Ϣ�еĳ�Ա��");
					for(String ss:msg.getGroupNameList())
					{
						System.out.println(ss);
					}
					System.out.println("****************");
					
					
					if (userManager.hasUser(srcUser)) {
						// �ú�ɫ���ֽ��յ���Ϣ��ʱ�䡢������Ϣ���û�������Ϣ������ӵ�����Ϣ��¼���ı�����
						if(msg.isJoinGroup())
						{
							final String msgRecord = dateFormat.format(new Date()) + " "
									+ srcUser +" ���� "+msg.getNewGroupmamber()+ "����Ⱥ ��  " + msg.getGroupName() +"\r\n";
							addMsgRecord(msgRecord, Color.black, 12, false, false);
						}
						else if(msg.isOutGruop())
						{
							final String msgRecord = dateFormat.format(new Date()) + " "
									+ srcUser +" �˳�Ⱥ��  "+ msg.getGroupName() +"\r\n";
							addMsgRecord(msgRecord, Color.black, 12, false, false);
						}
						else {
							final String msgRecord = dateFormat.format(new Date()) + " "
								+ srcUser +" ��Ⱥ  "+groupname+ "˵: " + msgContent +"\r\n";
						addMsgRecord(msgRecord, Color.black, 12, false, false);
						}
						
						
							// ��Ⱥ������Ϣת��������Ⱥ�û�
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
						// ���������Ӧ���û�δ����������Ϣ��ֱ�ӷ�����������Ϣ��Ӧ�÷���Ϣ��ʾ�ͻ��ˣ��������
						System.err.println("�û�δ����������Ϣ��ֱ�ӷ�����������Ϣ");
						return;
					}
				
					
				}
       
		//�����ļ�������Ϣ
		private void processFileMessage(FileMessage msg)
		{
			String dString=msg.getDstUser();
			String srcUser=msg.getSrcUser();
			String fileName=msg.getFileName();
			// �ú�ɫ���ֽ��յ���Ϣ��ʱ�䡢������Ϣ���û�������Ϣ������ӵ�����Ϣ��¼���ı�����
			final String msgRecord = dateFormat.format(new Date()) + " "
					+ srcUser +" �� "+dString+ "�����ļ� " +fileName+"\r\n";
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

// ���������û���Ϣ
class UserManager {
	private final Map<String, User> onLineUsers;

	public UserManager() {
		onLineUsers = new HashMap<String, User>();
	}

	// �ж�ĳ�û��Ƿ�����
	public boolean hasUser(String userName) {
		return onLineUsers.containsKey(userName);
	}

	// �ж������û��б��Ƿ��
	public boolean isEmpty() {
		return onLineUsers.isEmpty();
	}

	// ��ȡ�����û���Socket�ĵ��������װ�ɵĶ��������
	public ObjectOutputStream getUserOos(String userName) {
		if (hasUser(userName)) {
			return onLineUsers.get(userName).getOos();
		}
		return null;
	}

	// ��ȡ�����û���Socket�ĵ���������װ�ɵĶ���������
	public ObjectInputStream getUserOis(String userName) {
		if (hasUser(userName)) {
			return onLineUsers.get(userName).getOis();
		}
		return null;
	}

	// ��ȡ�����û���Socket
	public Socket getUserSocket(String userName) {
		if (hasUser(userName)) {
			return onLineUsers.get(userName).getSocket();
		}
		return null;
	}

	// ��������û�
	public boolean addUser(String userName, Socket userSocket) {
		if ((userName != null) && (userSocket != null)) {
			onLineUsers.put(userName, new User(userSocket));
			return true;
		}
		return false;
	}

	// ��������û�
	public boolean addUser(String userName, Socket userSocket,
			ObjectOutputStream oos, ObjectInputStream ios) {
		if ((userName != null) && (userSocket != null) && (oos != null)
				&& (ios != null)) {
			onLineUsers.put(userName, new User(userSocket, oos, ios));
			return true;
		}
		return false;
	}

	// ɾ�������û�
	public boolean removeUser(String userName) {
		if (hasUser(userName)) {
			onLineUsers.remove(userName);
			return true;
		}
		return false;
	}

	// ��ȡ���������û���
	public String[] getAllUsers() {
		String[] users = new String[onLineUsers.size()];
		int i = 0;
		for (Map.Entry<String, User> entry : onLineUsers.entrySet()) {
			users[i++] = entry.getKey();
		}
		return users;
	}

	// ��ȡ�����û�����
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
