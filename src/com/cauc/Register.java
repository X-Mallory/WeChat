package com.cauc;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import java.awt.Cursor;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class Register extends JFrame{

	public JFrame frame;
	private JTextField textField_userName;
	private JTextField textField_userSex;
	private JTextField textField_Email;
	private JPasswordField passwordField_psw;
	private JPasswordField passwordField_pswAgin;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Register window = new Register();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Register() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		Register register=this;
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		
		frame.setResizable(false);
		frame.setBounds(100, 100, 454, 484);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel label = new JLabel("");
		label.setBounds(156, 9, 120, 106);
		java.net.URL imgURL = Register.class.getResource("Image/register.PNG");
        frame.getContentPane().setLayout(null);
        label.setIcon(new ImageIcon(imgURL));
		frame.getContentPane().add(label);
		
		JLabel lblNewLabel = new JLabel("\u8BF7\u8F93\u5165\u4EE5\u4E0B\u4FE1\u606F\u5B8C\u6210\u6CE8\u518C\uFF1A");
		lblNewLabel.setForeground(new Color(0, 153, 153));
		lblNewLabel.setFont(new Font("微软雅黑 Light", Font.BOLD, 20));
		lblNewLabel.setBounds(106, 123, 262, 36);
		frame.getContentPane().add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("\u7528\u6237\u540D\uFF1A");
		lblNewLabel_1.setForeground(new Color(0, 153, 153));
		lblNewLabel_1.setFont(new Font("微软雅黑 Light", Font.BOLD, 18));
		lblNewLabel_1.setBounds(50, 176, 76, 26);
		frame.getContentPane().add(lblNewLabel_1);
		
		JLabel label_1 = new JLabel("\u6027\u522B\uFF1A");
		label_1.setForeground(new Color(0, 153, 153));
		label_1.setFont(new Font("微软雅黑 Light", Font.BOLD, 18));
		label_1.setBounds(50, 212, 76, 26);
		frame.getContentPane().add(label_1);
		
		JLabel label_2 = new JLabel("\u90AE\u7BB1\uFF1A");
		label_2.setForeground(new Color(0, 153, 153));
		label_2.setFont(new Font("微软雅黑 Light", Font.BOLD, 18));
		label_2.setBounds(50, 253, 76, 26);
		frame.getContentPane().add(label_2);
		
		JLabel label_3 = new JLabel("\u767B\u9646\u5BC6\u7801\uFF1A");
		label_3.setForeground(new Color(0, 153, 153));
		label_3.setFont(new Font("微软雅黑 Light", Font.BOLD, 18));
		label_3.setBounds(50, 294, 102, 26);
		frame.getContentPane().add(label_3);
		
		JLabel label_4 = new JLabel("\u786E\u8BA4\u5BC6\u7801\uFF1A");
		label_4.setForeground(new Color(0, 153, 153));
		label_4.setFont(new Font("微软雅黑 Light", Font.BOLD, 18));
		label_4.setBounds(50, 332, 102, 26);
		frame.getContentPane().add(label_4);
		
		textField_userName = new JTextField();
		textField_userName.setBounds(166, 174, 203, 27);
		frame.getContentPane().add(textField_userName);
		textField_userName.setColumns(10);
		
		textField_userSex = new JTextField();
		textField_userSex.setColumns(10);
		textField_userSex.setBounds(166, 213, 203, 27);
		frame.getContentPane().add(textField_userSex);
		
		textField_Email = new JTextField();
		textField_Email.setColumns(10);
		textField_Email.setBounds(166, 255, 203, 27);
		frame.getContentPane().add(textField_Email);
		
		passwordField_psw = new JPasswordField();
		passwordField_psw.setBounds(165, 293, 205, 28);
		frame.getContentPane().add(passwordField_psw);
		
		passwordField_pswAgin = new JPasswordField();
		passwordField_pswAgin.setBounds(165, 330, 205, 28);
		frame.getContentPane().add(passwordField_pswAgin);
		
		JButton btnNewButton = new JButton("\u6CE8\u518C");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name=null;
				String sex=null;
				String email=null;
				String psw=null;
				String pswAgian=null;
				
				name=textField_userName.getText();
				sex=textField_userSex.getText();
				email=textField_Email.getText();
				psw=passwordField_psw.getText();
				pswAgian=passwordField_pswAgin.getText();
				
				if(name.equals("") || name==null)
				{
					JOptionPane.showMessageDialog(null, "姓名不能为空","信息填写出错啦",  JOptionPane.ERROR_MESSAGE);
				     return;
				}
				
				if(sex.equals("") || sex==null)
				{
					JOptionPane.showMessageDialog(null, "性别不能为空","信息填写出错啦",  JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(email.equals("") || email==null)
				{
					JOptionPane.showMessageDialog(null, "邮箱不能为空","信息填写出错啦",  JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(psw.equals("") || psw==null)
				{
					JOptionPane.showMessageDialog(null,  "密码不能为空","信息填写出错啦", JOptionPane.ERROR_MESSAGE);
							return;
				}
				
				if(pswAgian.equals("") || pswAgian==null)
				{
					JOptionPane.showMessageDialog(null,  "请再次输入密码","信息填写出错啦", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(!(sex.equals("男") || sex.equals("女")))
				{
					JOptionPane.showMessageDialog(null, "性别只能填写【男】或【女】","信息填写出错啦",  JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(!email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*"))
				{
					JOptionPane.showMessageDialog(null, "请检查邮箱是否输入正确","信息填写出错啦",  JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				if(!psw.equals(pswAgian))
				{
					JOptionPane.showMessageDialog(null,  "两次输入的密码不一致","信息填写出错啦", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				
				try {
					Database db=new Database();
					if(db.insertUser(name, psw, email, sex))
					{
						JOptionPane.showMessageDialog(null, "注册成功");
						//register.frame.disable();
					}
					else {
						JOptionPane.showMessageDialog(null,  "注册失败","出错啦", JOptionPane.ERROR_MESSAGE);
					}
					db.shutdownDatabase();
					
				} catch (FileNotFoundException | SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
						
			}
		});
		btnNewButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnNewButton.setForeground(new Color(0, 153, 153));
		btnNewButton.setBackground(new Color(255, 255, 255));
		btnNewButton.setFont(new Font("等线 Light", Font.BOLD, 18));
		btnNewButton.setBounds(174, 388, 110, 32);
		frame.getContentPane().add(btnNewButton);
	}

}
