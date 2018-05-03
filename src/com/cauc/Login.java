package com.cauc;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.cauc.Client.ListeningHandler;


import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.Cursor;

public class Login extends JFrame{

	public JFrame frame;
	private JTextField textField_userName;
	private JPasswordField passwordField_userPsw;
    private SSLSocket socket;
    private int port=9999;
    private String severIP="localhost";
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    
    
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frame.setVisible(true);
					window.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {
	
		Login tempLogin=this;
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.getContentPane().setLayout(null);
		
		JLabel label = new JLabel("");
		
        java.net.URL imgURL = Login.class.getResource("Image/Login.PNG");
        label.setIcon(new ImageIcon(imgURL));
		label.setBounds(155, 35, 120, 113);
		frame.getContentPane().add(label);
		
		JLabel label_name = new JLabel("\u7528\u6237\u540D\uFF1A");
		label_name.setForeground(new Color(51, 153, 204));
		label_name.setFont(new Font("幼圆", Font.BOLD, 18));
		label_name.setBounds(81, 229, 82, 44);
		frame.getContentPane().add(label_name);
		
		textField_userName = new JTextField();
		textField_userName.setFont(new Font("等线 Light", Font.BOLD, 14));
		textField_userName.setBounds(173, 240, 144, 27);
		frame.getContentPane().add(textField_userName);
		textField_userName.setColumns(10);
		
		JLabel label_1 = new JLabel("  \u5BC6\u7801\uFF1A");
		label_1.setForeground(new Color(51, 153, 204));
		label_1.setFont(new Font("幼圆", Font.BOLD, 18));
		label_1.setBounds(81, 293, 82, 44);
		frame.getContentPane().add(label_1);
		
		JLabel lblNewLabel = new JLabel("Login");
		lblNewLabel.setForeground(new Color(0, 153, 153));
		lblNewLabel.setFont(new Font("Segoe Script", Font.BOLD | Font.ITALIC, 24));
		lblNewLabel.setBounds(177, 165, 88, 33);
		frame.getContentPane().add(lblNewLabel);
		
		passwordField_userPsw = new JPasswordField();
		passwordField_userPsw.setBounds(173, 303, 144, 27);
		frame.getContentPane().add(passwordField_userPsw);
		
		JButton btn_login = new JButton("\u767B\u9646");
		btn_login.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn_login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String localUserName=textField_userName.getText();
				String psw=passwordField_userPsw.getText();
				
				if(localUserName.isEmpty() || psw.isEmpty())
				{
					JOptionPane.showMessageDialog(null,  "输入信息不能为空","登陆出错啦", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
	         	// 与服务器端建立Socket连接，如果抛出异常，则弹出对话框通知用户，并退出
			   try {
				   //改变成SSLsocket
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
					socket=(SSLSocket)factory.createSocket(severIP,port);	
				       // socket = new Socket("localhost", port);
					   // 将socket的输入流和输出流分别封装成对象输入流和对象输出流
					oos = new ObjectOutputStream(socket	.getOutputStream());
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
					
			         //向服务器发送登陆消息，验证用户名和密码,ff为登陆成功的标志
			        int ff=0;
			         UserLoginMessage userLoginMessage=new UserLoginMessage(localUserName, "###", psw);
			         try {
							oos.writeObject(userLoginMessage);
							oos.flush();
							UserLoginMessage bb=(UserLoginMessage) ois.readObject();
						    ff=bb.getflag();
						
						
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (ClassNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} 
			         finally
			         {
			        	 /*try {
			        		 oos.close();
			        		 ois.close();
							socket.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}*/
			         }
			        
			         if(ff == 1)
			         {
			           JOptionPane.showMessageDialog(null,  "登陆成功！");
			            tempLogin.setVisible(false);
						Client client=new Client();
						client.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						client.setLocationRelativeTo(null);
		                client.setVisible(true);               		                		                
		                client.login(localUserName);
		                
		                
					}
			         else {
			        	 
			        	 JOptionPane.showMessageDialog(null,  "登陆失败！请检查用户名或者密码","登陆出错啦", JOptionPane.ERROR_MESSAGE);
							return;
			              } 
			}
		});
		btn_login.setFont(new Font("等线 Light", Font.BOLD, 16));
		btn_login.setForeground(new Color(0, 153, 255));
		btn_login.setBackground(new Color(255, 255, 255));
		btn_login.setBounds(148, 360, 127, 33);
		frame.getContentPane().add(btn_login);
		
		JButton button = new JButton("\u6CE8\u518C\u8D26\u53F7");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Register register= new Register();
				register.frame.setLocationRelativeTo(null);
                register.frame.setVisible(true);               
                register.frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			}
		});
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.setForeground(new Color(0, 153, 255));
		button.setFont(new Font("等线 Light", Font.BOLD, 16));
		button.setBackground(new Color(255, 255, 255));
		button.setBounds(148, 416, 127, 33);
		frame.getContentPane().add(button);
		
		JLabel lblNewLabel_1 = new JLabel("____________________________________________");
		lblNewLabel_1.setForeground(new Color(153, 204, 204));
		lblNewLabel_1.setBounds(70, 470, 331, 15);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel label_2 = new JLabel("Copyright \u00A92017 by Suguoyu. All Rights Reserved");
		label_2.setForeground(new Color(32, 178, 170));
		label_2.setFont(new Font("微软雅黑 Light", Font.PLAIN, 14));
		label_2.setBounds(69, 483, 320, 38);
		frame.getContentPane().add(label_2);
		
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 560);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
