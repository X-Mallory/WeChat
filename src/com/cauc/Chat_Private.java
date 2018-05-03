package com.cauc;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.omg.CORBA.PUBLIC_MEMBER;

import java.awt.Color;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JTextField;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.Rectangle;

public class Chat_Private extends JFrame {

	private JPanel contentPane;
	private JTextField textField_Message;
	private JLabel lblNewLabel_dstName;
	private String srcNameString;
	private String dstNaString;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ios;
	//private Boolean flag_firstchat=true;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	//���ڻص�������˽�Ĵ��ڵĿͻ��ˣ���˽�Ĵ��ڱ��رյ�ʱ�򣬵���client��removeChat_private������˽�Ĵ��ڴ�map���Ƴ���
	private Client client;
	private String filepath;
	
	
	public JTextPane textPane_MsgRecord;

	
	 
	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public JLabel getLblNewLabel_dstName() {
		return lblNewLabel_dstName;
	}

	public void setLblNewLabel_dstName(JLabel lblNewLabel_dstName) {
		this.lblNewLabel_dstName = lblNewLabel_dstName;
	}
    
	public String getSrcNameString() {
		return srcNameString;
	}

	public void setSrcNameString(String srcNameString) {
		this.srcNameString = srcNameString;
	}

	public String getDstNaString() {
		return dstNaString;
	}

	public void setDstNaString(String dstNaString) {
		this.dstNaString = dstNaString;
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
	
	public void show_Receive(ChatMessage msg)
	{
		if(!msg.isPubChatMessage())
		{
		String ss=msg.getSrcUser();
		String dd=msg.getDstUser();
		if(dd.equals(srcNameString))
		{
		// �÷�ɫ���ֽ��յ���Ϣ��ʱ�䡢������Ϣ���û�������Ϣ������ӵ�����Ϣ��¼���ı�����
		final String msgRecord = dateFormat.format(new Date())
				+ " " + ss + "˵: " + msg.getMsgContent() + "\r\n";
		addMsgRecord(msgRecord, new Color(255,192,203), 12, false, false);
		}
		
		}
		
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Chat_Private frame = new Chat_Private();
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
	public Chat_Private() {
		
		//��ӶԹر��¼��ļ���
		this.addWindowListener(new WindowAdapter ()
		{
		 @Override
            public void windowClosing ( WindowEvent e )
            {
			 
           
				if (!getClient().removechat_private(dstNaString)) {
					JOptionPane.showMessageDialog(null, "��˽�Ĵ���δ��map���Ƴ�", "���ڹرճ�����", JOptionPane.ERROR_MESSAGE);
				}
			
            }

		});

		setResizable(false);
		setBackground(new Color(32, 178, 170));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 468, 423);
		contentPane = new JPanel();
		contentPane.setForeground(new Color(255, 255, 255));
		contentPane.setBackground(new Color(255, 255, 255));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel pane_Nourth = new JPanel();
		pane_Nourth.setBackground(new Color(176, 224, 230));
		contentPane.add(pane_Nourth, BorderLayout.NORTH);
		
		JLabel lblNewLabel_do = new JLabel("\u6B63\u5728\u548C");
		lblNewLabel_do.setForeground(Color.WHITE);
		lblNewLabel_do.setFont(new Font("���� Light", Font.PLAIN, 18));
		pane_Nourth.add(lblNewLabel_do);
		
		Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
		pane_Nourth.add(rigidArea);
		
		lblNewLabel_dstName = new JLabel("\u804A\u5929\u4EBA");
		lblNewLabel_dstName.setForeground(new Color(95, 158, 160));
		lblNewLabel_dstName.setFont(new Font("���� Light", Font.PLAIN, 20));
		pane_Nourth.add(lblNewLabel_dstName);
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		pane_Nourth.add(rigidArea_1);
		
		JLabel lblNewLabel_chat = new JLabel("\u804A\u5929");
		lblNewLabel_chat.setFont(new Font("���� Light", Font.PLAIN, 18));
		lblNewLabel_chat.setForeground(Color.WHITE);
		pane_Nourth.add(lblNewLabel_chat);
		
		JPanel panel_South = new JPanel();
		panel_South.setBackground(Color.WHITE);
		contentPane.add(panel_South, BorderLayout.SOUTH);
		
		textField_Message = new JTextField();
		textField_Message.setColumns(10);
		
		JButton button_sendmsg = new JButton("\u53D1\u9001\u6D88\u606F");
		button_sendmsg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/*if(flag_firstchat)
				{
					try {
						oos=new ObjectOutputStream(socket.getOutputStream());
						ios=new ObjectInputStream(socket.getInputStream());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					flag_firstchat=false;
				}*/
			
				 
				String msg=textField_Message.getText();
				if (msg.length() > 0) {
					// ����Ϣ�ı����е�������Ϊ˽����Ϣ���͸�������
					ChatMessage chatMessage = new ChatMessage(srcNameString,
							dstNaString, msg);
					try {
						synchronized (oos) {
							oos.writeObject(chatMessage);
							oos.flush();
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					// �ڡ���Ϣ��¼���ı���������ɫ��ʾ���͵���Ϣ������ʱ��
					String msgRecord = dateFormat.format(new Date()) + " "+srcNameString+"��"
							+ msg + "\r\n";
					addMsgRecord(msgRecord, new Color(30,144,255), 12, false, false);
				}
				
				return;
			}
		});
		button_sendmsg.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_sendmsg.setForeground(new Color(60, 179, 113));
		button_sendmsg.setBackground(new Color(255, 255, 255));
		button_sendmsg.setFont(new Font("���� Light", Font.BOLD, 14));
		
		JButton button_sendfile = new JButton("\u53D1\u9001\u6587\u4EF6");
		button_sendfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
               if(button_sendfile.getText().equals("�����ļ�"))
               {
				 JFileChooser fileChooser  =new JFileChooser("����\\���ܲ���");
				 if(fileChooser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION)
				 {
					filepath=fileChooser.getSelectedFile().getPath();
					System.out.println(filepath);
					
					textField_Message.setText(filepath);
					button_sendfile.setText("����");
					return ;
					
				 }
               }
               if(button_sendfile.getText().equals("����"))
               {
                   if(filepath.isEmpty() || filepath.equals("null"))
                   {
                	   button_sendfile.setText("�����ļ�");
                	   JOptionPane.showMessageDialog(null,  "������ѡ���ļ���","�ļ�������", JOptionPane.ERROR_MESSAGE);
                	   return ;
                   }
                   //����һ���ļ����󣬻�ȡ�ļ��ĳ��ȣ����֣���׺��
                   File file=new File(filepath);
                   String filename=file.getName();
                   long filelength=file.length();
                   
                   //�����µ��̴߳����ļ�,�ȷ����ļ���Ϣ
                   FileTransferHandler fileTransferHandler=new FileTransferHandler(filepath);
                   Inet4Address ip=fileTransferHandler.getIp();
                   int port=fileTransferHandler.getPort();
                   new Thread(fileTransferHandler).start();
                   button_sendfile.setText("�����ļ�");
                   textField_Message.setText("");
                   
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
					String msgRecord = dateFormat.format(new Date()) + " ��"+srcNameString+"�����ļ�:"+filename+ "\r\n";
					addMsgRecord(msgRecord, new Color(60,179,113), 12, false, false);
                   
                   
                   //����
            	   /*
            	    * **8***********
            	    * 
            	    * 
            	    * 
            	    * 
            	    */
            	   
               }
			}
		});
		button_sendfile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button_sendfile.setForeground(new Color(32, 178, 170));
		button_sendfile.setFont(new Font("���� Light", Font.BOLD, 14));
		button_sendfile.setBackground(Color.WHITE);
		GroupLayout gl_panel_South = new GroupLayout(panel_South);
		gl_panel_South.setHorizontalGroup(
			gl_panel_South.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_South.createSequentialGroup()
					.addComponent(textField_Message, GroupLayout.PREFERRED_SIZE, 346, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_South.createParallelGroup(Alignment.LEADING)
						.addComponent(button_sendmsg, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(button_sendfile, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_South.setVerticalGroup(
			gl_panel_South.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_South.createParallelGroup(Alignment.BASELINE)
					.addComponent(textField_Message, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_panel_South.createSequentialGroup()
						.addContainerGap()
						.addComponent(button_sendmsg)
						.addGap(11)
						.addComponent(button_sendfile, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
						.addContainerGap()))
		);
		panel_South.setLayout(gl_panel_South);
		
		JPanel panel_center = new JPanel();
		contentPane.add(panel_center, BorderLayout.CENTER);
		
		JScrollPane scrollPane_MsgRecord = new JScrollPane();
		scrollPane_MsgRecord.setViewportBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "�����¼",
				TitledBorder.LEADING, TitledBorder.TOP, new Font("���� Light",Font.PLAIN, 14), new Color(102,205,170)));
		GroupLayout gl_panel_center = new GroupLayout(panel_center);
		gl_panel_center.setHorizontalGroup(
			gl_panel_center.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane_MsgRecord, GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
		);
		gl_panel_center.setVerticalGroup(
			gl_panel_center.createParallelGroup(Alignment.LEADING)
				.addComponent(scrollPane_MsgRecord, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
		);
		
		textPane_MsgRecord = new JTextPane();
		textPane_MsgRecord.setEditable(false);
		scrollPane_MsgRecord.setViewportView(textPane_MsgRecord);
		panel_center.setLayout(gl_panel_center);
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
				Document docs = textPane_MsgRecord.getDocument();
				try {
					docs.insertString(docs.getLength(), msgRecord, attrset);
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
		});
		 
		
	}
	
	/*// ��̨�����߳�
		class ListeningHandler implements Runnable {
			@Override
			public void run() {
				try {
					while (true) {
						Message msg = null;
						synchronized (ios) {
							msg = (Message) ios.readObject();
						}
					    if (msg instanceof ChatMessage) {
							// ֻ����˽�������������ͬһ����������Ϣ
						   ChatMessage msgPrivate=(ChatMessage) msg;
						   //ֻ����˽����Ϣ
						   if(!msgPrivate.isPubChatMessage())
						   {   
							   ////˽����Ϣ��˫���͵�ǰ���ڵ�˫����ͬ
							   if(msgPrivate.getDstUser().equals(srcNameString) && msgPrivate.getSrcUser().equals(dstNaString))
							   {
								// �÷�ɫ���ֽ��յ���Ϣ��ʱ�䡢������Ϣ���û�������Ϣ������ӵ�����Ϣ��¼���ı�����
									final String msgRecord = dateFormat.format(new Date())
											+ " " + msgPrivate.getSrcUser() + ": " + msgPrivate.getMsgContent() + "\r\n";
									addMsgRecord(msgRecord, new Color(255,192,203), 12, false, false);
							   }
						   }
						   else {
							return;
						}
						} else {
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
		}*/

}

