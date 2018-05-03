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
	//���ڻص�������Ⱥ�Ĵ��ڵĿͻ��ˣ���˽�Ĵ��ڱ��رյ�ʱ�򣬵���client��removeChat_private������˽�Ĵ��ڴ�map���Ƴ���
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
		
		//��ӶԹر��¼��ļ���
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
							JOptionPane.showMessageDialog(null, "��Ⱥ�Ĵ���δ��map���Ƴ�", "���ڹرճ�����", JOptionPane.ERROR_MESSAGE);
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
		label.setFont(new Font("���� Light", Font.PLAIN, 18));
		
		label_Groupname = new JLabel("\u804A\u5929\u4EBA");
		label_Groupname.setForeground(new Color(95, 158, 160));
		label_Groupname.setFont(new Font("���� Light", Font.PLAIN, 20));
		
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
					System.out.println("�ͻ��˷���Ϣǰ����Ϣ��Ա��");
					System.out.println(999999);
					System.out.println("�����ˣ�"+msg.getSrcUser());
					System.out.println("���ų�Ա��");
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
					// �ڡ���Ϣ��¼���ı���������ɫ��ʾ���͵���Ϣ������ʱ��
					String msgRecord = dateFormat.format(new Date()) + " "+src+"��"
							+ msg.getMsgContent() + "\r\n";
					addMsgRecord(msgRecord, new Color(30,144,255), 12, false, false);
				}
				}
			
		});
		buttonSentMessage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		buttonSentMessage.setForeground(new Color(60, 179, 113));
		buttonSentMessage.setFont(new Font("���� Light", Font.BOLD, 14));
		buttonSentMessage.setBackground(Color.WHITE);
		
		JButton button_SendFile = new JButton("\u53D1\u9001\u6587\u4EF6");
		button_SendFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			     if(button_SendFile.getText().equals("�����ļ�"))
	               {
					 JFileChooser fileChooser  =new JFileChooser("����\\���ܲ���");
					 if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
					 {
						filepath=fileChooser.getSelectedFile().getPath();
						System.out.println("����Ⱥ�ļ���"+filepath);
						
						textFieldMessage.setText(filepath);
						button_SendFile.setText("����");
						return ;
						
					 }
	               }
	               if(button_SendFile.getText().equals("����"))
	               {
	                   if(filepath.isEmpty() || filepath.equals("null"))
	                   {
	                	   button_SendFile.setText("�����ļ�");
	                	   JOptionPane.showMessageDialog(null,  "������ѡ���ļ���","�ļ�������", JOptionPane.ERROR_MESSAGE);
	                	   return ;
	                   }
	                   //����һ���ļ����󣬻�ȡ�ļ��ĳ��ȣ����֣���׺��
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
	                   /*//�����µ��̴߳����ļ�,�ȷ����ļ���Ϣ
	                   //FileTransferHandler fileTransferHandler=new FileTransferHandler(filepath);
	                    //����seversocketchanl�����������ļ�
	                  // ServerSocket serverSocket=new ServerSocket(0);
	                 //��̬������������selector
	           		Selector selector=Selector.open();
	           		//��̬������������seversocketChannel;
	           		ServerSocketChannel seveChannel=ServerSocketChannel.open();
	           		//ʹ����ͬһ�������Ϲر��˷��������򣬽�����������ʱ
	           		//����˳�������ӵ�ͬһ���˿��ϣ������ڰ�֮ǰ���ò���Ч
	           		seveChannel.socket().setReuseAddress(true);
	           		//Ĭ��SeverSocketchannelΪ����ģʽ���������óɷ�����ģʽ
	           		seveChannel.configureBlocking(false);
	           		//�������󶨱��ض˿�
	           		seveChannel.socket().bind(new InetSocketAddress(8866));
	           		System.out.println("�����������ɹ���");
	                Inet4Address ip=(Inet4Address)seveChannel.socket().getInetAddress();
	                int port=seveChannel.socket().getLocalPort();
	                  
	                   
	                   //new Thread(fileTransferHandler).start();
	                 button_SendFile.setText("�����ļ�");
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
						// �ڡ���Ϣ��¼���ı���������ɫ��ʾ���͵���Ϣ������ʱ��
						String msgRecord = dateFormat.format(new Date()) + " ��Ⱥ"+groupName+"�����ļ�:"+filename+ "\r\n";
						addMsgRecord(msgRecord, new Color(60,179,113), 12, false, false);
	                   */
	                   
	                   //����
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
		button_SendFile.setFont(new Font("���� Light", Font.BOLD, 14));
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
				newmember=JOptionPane.showInputDialog("��������ӵ��û���");	
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
						   System.out.println("�¼����Ա��"+newmember);
					       groupmember.add(newmember);
					}
					if(!groupmeberDlm.contains(newmember))
					{
					       groupmeberDlm.addElement(newmember);
					}
					else
					{
					//JOptionPane.showMessageDialog(null,  "�¼ӵĳ�Ա�Ѿ���Ⱥ����","��Ⱥ������", JOptionPane.ERROR_MESSAGE);
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
					System.err.println("���ͼ�����Ϣǰ");
					
					System.err.println("!!!!!");
			        System.err.println("�����ˣ�"+msgss.getSrcUser());
			        System.err.println("��Ϣ�������"+msgss.getFlag());
			        System.err.println("��Ϣ��Ա����"+msgss.getGroupNameList().size());
			        System.err.println("��Ϣ��Ա��");
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
							System.out.println("���ͼ�����Ϣ��");
							
							System.out.println("!333333");
					        System.out.println("�����ˣ�"+msgss.getSrcUser());
					        System.out.println("��Ϣ�������"+msgss.getFlag());
					        System.out.println("��Ϣ��Ա����"+msgss.getGroupNameList().size());
					        System.out.println("��Ϣ��Ա��");
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
					JOptionPane.showMessageDialog(null,  "�����б���û�д��û�","��ӳ�����", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton.setForeground(new Color(0, 139, 139));
		btnNewButton.setFont(new Font("���� Light", Font.PLAIN, 14));
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
					JOptionPane.showMessageDialog(null, "��Ⱥ�Ĵ���δ��map���Ƴ�", "���ڹرճ�����", JOptionPane.ERROR_MESSAGE);
				}
				
				
			}
		});
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setForeground(new Color(0, 139, 139));
		button.setFont(new Font("���� Light", Font.PLAIN, 14));
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
				.getBorder("TitledBorder.border"), "Ⱥ��Ա",
				TitledBorder.LEADING, TitledBorder.TOP, new Font("���� Light",Font.PLAIN, 14), new Color(102,205,170)));
		splitPaneCenter.setLeftComponent(scrollPane_left);
		
		JList listGroupmember = new JList<String>(groupmeberDlm);
		
		scrollPane_left.setViewportView(listGroupmember);
		
		JScrollPane scrollPane_right = new JScrollPane();
		scrollPane_right.setViewportBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "�����¼",
				TitledBorder.LEADING, TitledBorder.TOP, new Font("���� Light",Font.PLAIN, 14), new Color(102,205,170)));
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

		// ����ɫ���ֽ��յ���Ⱥ��Ϣ��ӵ�����Ϣ��¼���ı�����
		final String msgRecord = dateFormat.format(new Date())
				+ " " + ss + "����  " + msg.getNewGroupmamber() + "����Ⱥ��\r\n";
		addMsgRecord(msgRecord, new Color(30,144,255), 12, false, false);
		
		
		}else if(msg.isOutGruop())
		{
		// �ú�ɫɫ���ֽ��յ���Ⱥ��Ϣ��ӵ�����Ϣ��¼���ı�����
		final String msgRecord = dateFormat.format(new Date())
				+ " " + ss  + "�˳�Ⱥ��\r\n";
		addMsgRecord(msgRecord, new Color(205,92,92), 12, false, false);
		}
		else
		{
			// �û�ɫ���ֽ�Ⱥ��Ϣ��ӵ�����Ϣ��¼���ı�����
			final String msgRecord = dateFormat.format(new Date())
					+ " " + ss  + "˵:"+msg.getMsgContent() +"\r\n";
			addMsgRecord(msgRecord, new Color(119,136,153), 12, false, false);
		}
		
	}
    
	
	//����Ⱥ���ļ�ʱ�ķ�����������ʽ
	public void service(RandomAccessFile ff,String filename,long filelength) throws IOException
	{
		
		       //��̬������������selector
			 Selector selector=Selector.open();
				//��̬������������seversocketChannel;
			ServerSocketChannel seveChannel=ServerSocketChannel.open();
				//ʹ����ͬһ�������Ϲر��˷��������򣬽�����������ʱ
				//����˳�������ӵ�ͬһ���˿��ϣ������ڰ�֮ǰ���ò���Ч
			seveChannel.socket().setReuseAddress(true);
				//Ĭ��SeverSocketchannelΪ����ģʽ���������óɷ�����ģʽ
			seveChannel.configureBlocking(false);
			
				//�������󶨱��ض˿�
			seveChannel.socket().bind(new InetSocketAddress("localhost",0));
		    System.out.println("�����������ɹ���");
		       //sevenChannel��selectorע�����Ӿ����¼�
		    seveChannel.register(selector,SelectionKey.OP_ACCEPT);
		    
		    //SocketAddress socketAddress=seveChannel.socket().getLocalSocketAddress();
		    Inet4Address ip=(Inet4Address) seveChannel.socket().getInetAddress();
            int port=seveChannel.socket().getLocalPort();
		    System.out.println("Ⱥ���ļ��󶨶˿ڣ�"+port);
            for(String str:groupmember)
            {
            	FileMessage filerquest=new FileMessage(src, str, ip,port,filename,filelength);
                filerquest.setRequest_or_reply(FileMessage.REQUEST);
                
						synchronized (oos) {
							oos.writeObject(filerquest);
							oos.flush();
						}
            }
            //����ע���¼�����ʱ
		while(selector.select()>0)
		{
			//���������¼�װ��һ��������
			Set readkeySet=selector.selectedKeys();
			//��һ���������������������
			Iterator iterator =readkeySet.iterator();
			//��������������
			while(iterator.hasNext())
			{
				//����һ��selectkey����ȡ�¼�
				SelectionKey key =null;
				 
				key=(SelectionKey) iterator.next();
				//��ȡ�¼���Ӽ������Ƴ�
				iterator.remove();
				
				//��������Ӿ������¼�
				if(key.isAcceptable())
				{
					//������selection���������ServerSocketChannel����socket���������
					ServerSocketChannel sscChannel =(ServerSocketChannel) key.channel();
					//����һ����ͻ������ӵ�socketchannel
					
					SocketChannel socketChannel=sscChannel.accept();
					System.out.println("�ӵ��ͻ������ӣ����ԣ�"+socketChannel.socket().getInetAddress()+":"+socketChannel.socket().getPort());
				    
					//�������socketchannelΪ������ģʽ
					socketChannel .configureBlocking(false);
					
				    long n=0;
				    
					//���socketChannel��selectorע��׼���õ��¼���д�����¼�
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
						//��д������ʱ��ȡ��ע��
						key.cancel();
					}
					
					
				}
				
			}
		}
			
	}
	
	public void Writer(SelectionKey key,RandomAccessFile file) throws InterruptedException
	{
		//���ض�Ӧע���д�����¼���SocketChannel
		SocketChannel socketChannel=(SocketChannel) key.channel();

		//���ظ��������ڼ�¼��һ�η������ݵ�λ�õ�������
		long n= (long)key.attachment();
		
		//�����ַ�����ÿ�δ�randomaccessfie�ж���һ������
		byte[] b = new byte[1024*100];
		
		try {
			//���ø��ļ�����ƫ����ָ�룬��һ�ζ�ȡʱ����
			file.seek(n);
			
			file.read(b);
			//��ȡ������һ��֮����ļ�ƫ����
		    n=file.getFilePointer();
			
		   
			
		    //����ƫ��������д�뵱ǰsocket��Ӧ��SelectKey�ĸ�����
		    key.attach(n);
		    
		    //����������תΪ�ֽ����飬�ŵ�һ����������
		    ByteBuffer buffer=ByteBuffer.wrap(b);
		    
		    //���ʹ�����
		    Thread.sleep(50);// 
			socketChannel.write(buffer);
			  //���n�������ļ��Ĵ�С��˵�������ˣ�ȡ��ע�ᣬ�ر�
			if(n>=file.length())
			{
				key.cancel();
				socketChannel.close();
			}
		
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			
			System.out.println("д���ݳ���");
		       }
	}
	


}
