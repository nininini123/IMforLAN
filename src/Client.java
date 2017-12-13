import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.lang.*;

class Client extends JFrame {
	static final String SERVER_IP = "192.168.177.1";
	//static final String SERVER_IP = "localhost";

	private static final long serialVersionUID = 213009532321387674L;

	// class Client extends JFrame implements MouseListener {
	final static int encrpt_PORT = 10002;

	//JComponents...
		JPanel contentPane;
		CardLayout CLayout = new CardLayout();
		JMenu wndMenu= new JMenu();

		//log in component
		JPanel lg_panel = new JPanel();
		JLabel lg_label_id = new JLabel() ;
		JLabel lg_label_pwd = new JLabel();
		JTextField lg_txt_id = new JTextField() ; 
//		JTextField lg_txt_pwd = new JTextField() ;
		JPasswordField lg_txt_pwd = new JPasswordField();
		JLabel lg_label_signup = new JLabel() ;
		JLabel lg_label_findp = new JLabel();
		JButton buttonLogin = new JButton();

		//sign up
		JPanel su_panel = new JPanel();
		JLabel su_label_id = new JLabel() ;
		JLabel su_label_tel = new JLabel() ; 
		JLabel su_label_pwd = new JLabel() ;
		JTextField su_txt_id = new JTextField() ; 
		JTextField su_txt_tel = new JTextField() ;
//		JTextField su_txt_pwd = new JTextField() ;
		JPasswordField su_txt_pwd = new JPasswordField();
		JLabel su_label_login = new JLabel() ;
		JLabel su_label_findp = new JLabel();
		JButton buttonSignUp = new JButton();

		//find password
		JPanel fp_panel = new JPanel();
		JLabel fp_label_id = new JLabel() ;
		JLabel fp_label_tel = new JLabel() ;
		JTextField fp_txt_id = new JTextField() ; 
		JTextField fp_txt_tel = new JTextField() ;
		JLabel fp_label_login = new JLabel() ;
		JLabel fp_label_signup = new JLabel();
		JButton buttonFindPwd = new JButton();

	//µÇÂ¼×¢²áµÄ·¢ËÍÐÅÏ¢
	encrypt_info send_info = new encrypt_info();
	final static String host="localhost";

	//
	SocketFactory factory = null;// SSLSocketFactory.getDefault();
	Socket socket= null;// factory.createSocket("localhost", encrpt_PORT);
	ServerSocket ListenSocket=null;	//���ڼ���
	PrintWriter encrp_pw = null;// new ObjectOutputStream(socket.getOutputStream());
	InputStreamReader isr = null;// new InputStreamReader(socket.getInputStream());
	BufferedReader encrp_br = null;// new BufferedReader(isr);	


	void buttonLoginAction(ActionEvent e) {
		String rcvmsg = null;
		try {
			encrp_pw.println(encrypt_info.LOG_IN+" "+lg_txt_id.getText() +" "+null +" "+String.valueOf(lg_txt_pwd.getPassword()));

			rcvmsg = encrp_br.readLine();
			System.out.println(rcvmsg);
			if (rcvmsg.equals("correct password")) {
				ListenSocket = new ServerSocket(0); 
				encrp_pw.println( InetAddress.getLocalHost().getHostAddress()+" "+ListenSocket.getLocalPort() );
				// encrp_os.writeObject( InetAddress.getLocalHost().getHostAddress()+"\t"+ListenSocket.getLocalPort() );
				// new mainWnd(ListenSocket,encrp_br,encrp_os);
				new mainWnd(this,ListenSocket,socket,lg_txt_id.getText());
				this.setVisible(false);
			}
			else if (rcvmsg.equals("wrong password")) {
				JOptionPane.showMessageDialog(null, "��������˺������벻ƥ��", "��½ʧ��", JOptionPane.WARNING_MESSAGE);
			}
			else if (rcvmsg.equals("wrong username")) {
				JOptionPane.showMessageDialog(null, "��������˺Ų�����", "��½ʧ��", JOptionPane.WARNING_MESSAGE);
			}
			else {
				System.out.println("get msg what what what???...:\n"+rcvmsg);
			}
		}
		catch (IOException ioe) {
			System.out.println(ioe.toString());
		}
	}
	void buttonSignupAction(ActionEvent e) {
		String rcvmsg = null;
		try {
			encrp_pw.println(encrypt_info.SIGN_UP+" "+su_txt_id.getText() +" "+su_txt_tel.getText() +" "+String.valueOf(su_txt_pwd.getPassword() ));
			rcvmsg=encrp_br.readLine();
			System.out.println(rcvmsg);
			if (rcvmsg.equals("succeed to sign up")) {
				CLayout.show(contentPane, "log in panel");
        		this.setTitle("�����ҵ�½");
				JOptionPane.showMessageDialog(null, "ע��ɹ�", "ע��ɹ�", JOptionPane.PLAIN_MESSAGE);
				;
			}
			else if (rcvmsg.equals("fail to sign up")) {
				JOptionPane.showMessageDialog(null, "ID�ѱ�ע��", "ע��ʧ��", JOptionPane.WARNING_MESSAGE);
			}
			else {
				System.out.println("get msg what what what???2...:\n"+rcvmsg);
			}			
		}
		catch (IOException ioe) {
			System.out.println(ioe.toString());
		}	
	}
	void buttonFindpwdAction(ActionEvent e) {
		String rcvmsg = null;
		try {
			// encrp_os.writeObject 
			encrp_pw.println(encrypt_info.FIND_PWD+" "+fp_txt_id.getText() +" "+fp_txt_tel.getText() +" "+null);
			rcvmsg=encrp_br.readLine();
			System.out.println(rcvmsg);
			if (rcvmsg.equals("illegal id")) {
				JOptionPane.showMessageDialog(null, "��������˺Ų�����","�˺Ŵ���",  JOptionPane.WARNING_MESSAGE);
			}
			if (rcvmsg.startsWith("pwd")) {
				CLayout.show(contentPane, "log in panel");
        		this.setTitle("�����ҵ�½");
				JOptionPane.showMessageDialog(null, "��������Ϊ��\n"+rcvmsg.split(" ")[1],"�һ�����",  JOptionPane.PLAIN_MESSAGE);
			}
			else if (rcvmsg.equals("wrong tel")) {
				JOptionPane.showMessageDialog(null, "��������˺���Ԥ���ֻ��Ų�ƥ��","�����һ�ʧ��",  JOptionPane.WARNING_MESSAGE);
			}
			else {
				System.out.println("get msg what what what???2...:\n"+rcvmsg);
			}	
		}
		catch (IOException ioe) {
			System.out.println(ioe.toString());
		}
	}

	public Client() {	//¹¹º¯
		try {
			System.setProperty("javax.net.ssl.trustStore","mysocket.jks");//ÉèÖÃ¿ÉÐÅÈÎµÄÃÜÔ¿²Ö¿â 
			System.setProperty("javax.net.ssl.trustStorePassword","mysocket"); //ÉèÖÃ¿ÉÐÅÈÎµÄÃÜÔ¿²Ö¿âµÄÃÜÂë 
			factory = SSLSocketFactory.getDefault();
			//socket= factory.createSocket("localhost", encrpt_PORT);
			socket= factory.createSocket(SERVER_IP, encrpt_PORT);
			// encrp_os = new ObjectOutputStream(socket.getOutputStream());
			encrp_pw = new PrintWriter(socket.getOutputStream(),true);
			isr = new InputStreamReader(socket.getInputStream());
			encrp_br = new BufferedReader(isr);	

			ListenSocket = new ServerSocket();
		}	
		catch (IOException ioe2) {
			System.out.println("cry...Client 192:\t"+ioe2.toString());
		}	

		//Éè¶¨¹Ø±Õ´°¿Ú²Ù×÷¡£
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);

           
		//ÉèÖÃ´°¿Ú´óÐ¡
		int scrHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		int scrWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		final int wndWidth = 330;
		final int wndHeight = 250; 
		this.setBounds( (scrWidth - wndWidth )/2 ,(scrHeight - wndHeight) /2 , wndWidth, wndHeight);
        JFrame thisWnd=this;

        //layout
			//ÊµÏÖ¿¨Æ¬²¼¾Ö
			contentPane = (JPanel)this.getContentPane();
			contentPane.setLayout( CLayout );
			contentPane.add("log in panel",lg_panel);
			contentPane.add("find pwd panel",fp_panel);
			contentPane.add("sign up panel",su_panel);

			//log in 
			lg_panel.setLayout(null);
			lg_panel.add(lg_label_id);
			lg_panel.add(lg_label_pwd);
			lg_panel.add(lg_txt_id);
			lg_panel.add(lg_txt_pwd);
			lg_panel.add(lg_label_signup);
			lg_panel.add(lg_label_findp);
			lg_panel.add(buttonLogin);           
	        this.setTitle("�����ҵ�½");
	        lg_label_id.setText("�˺�");
	        lg_label_id.setBounds(new Rectangle(45,45,50,25));
	        lg_label_pwd.setText("����");
	        lg_label_pwd.setBounds(new Rectangle(45,87,50,25));
	        lg_txt_id.setBounds(100,45,155,25);
	        lg_txt_pwd.setBounds(100,87,155,25);
	        lg_label_signup.setText("ע��");
	        lg_label_signup.setBounds(28, 135, 66, 40);
	        buttonLogin.setText("��½");
	       	buttonLogin.setBounds(103, 135, 66, 40);
	        lg_label_findp.setText("�һ�����");
	        lg_label_findp.setBounds(190, 135, 92, 40);
	        //ÇÐ»»
	        lg_label_signup.addMouseListener(new MouseAdapter() {
	        	public void mousePressed(MouseEvent e) {
	        		CLayout.show(contentPane, "sign up panel");
	        		thisWnd.setTitle("������ע��");
	        	}
	        });
	        lg_label_findp.addMouseListener(new MouseAdapter() {
	        	public void mousePressed(MouseEvent e) {
	        		CLayout.show(contentPane, "find pwd panel");
	        		thisWnd.setTitle("�һ�����");
	        	}
	        });
	        //°´¼üµã»÷ÊÂ¼þ
	        buttonLogin.addActionListener( new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		buttonLoginAction(e);
	        	}
	        } );

        

	        //sign up
	        su_panel.setLayout( new BorderLayout() );
	        JPanel su_center_panel = new JPanel();
	        JPanel su_south_panel = new JPanel();
	        su_panel.add(su_center_panel,"Center");
	        su_panel.add(su_south_panel,"South");
	        su_center_panel.setLayout(null);
	        su_south_panel.setLayout(new FlowLayout());
			su_center_panel.add(su_label_id);
			su_center_panel.add(su_label_tel);
			su_center_panel.add(su_label_pwd);
			su_center_panel.add(su_txt_id);
			su_center_panel.add(su_txt_tel);
			su_center_panel.add(su_txt_pwd);
			su_south_panel.add(su_label_login);
			su_south_panel.add(su_label_findp);
			su_south_panel.add(buttonSignUp);    
	        su_label_id.setText("�˺�");
	        su_label_id.setBounds(new Rectangle(45,35,50,25));
	        su_label_tel.setText("�ֻ���");
	        su_label_tel.setBounds(new Rectangle(45,77,50,25));
	        su_label_pwd.setText("����");
	        su_label_pwd.setBounds(new Rectangle(45,122,50,25));
	        su_txt_id.setBounds(100,35,155,25);
	        su_txt_tel.setBounds(100,77,155,25);
	        su_txt_pwd.setBounds(100,120,155,25);
	        buttonSignUp.setText("ע��");
	        buttonSignUp.setBounds(23, 135, 66, 40);
	        su_label_login.setText("��½");
	       	su_label_login.setBounds(103, 135, 66, 40);
	        su_label_findp.setText("�һ�����");
	        su_label_findp.setBounds(185, 135, 92, 40);
	        su_label_login.addMouseListener(new MouseAdapter() {
	        	public void mousePressed(MouseEvent e) {
	        		CLayout.show(contentPane,"log in panel");
	        		thisWnd.setTitle("�����ҵ�½");
	        	}
	        } );
	        su_label_findp.addMouseListener(new MouseAdapter() {
	        	public void mousePressed(MouseEvent e) {
	        		CLayout.show(contentPane,"find pwd panel");
	        		thisWnd.setTitle("�һ�����");
	        	}
	        } );
	        //°´¼üµã»÷ÊÂ¼þ
	        buttonSignUp.addActionListener( new ActionListener() {
	        	public void actionPerformed(ActionEvent e) {
	        		buttonSignupAction(e);
	        	}
	        } );

	        //find pwd
	        fp_panel.setLayout(null);
			fp_panel.add(fp_label_id);
			fp_panel.add(fp_label_tel);
			fp_panel.add(fp_txt_id);
			fp_panel.add(fp_txt_tel);
			fp_panel.add(fp_label_login);
			fp_panel.add(fp_label_signup);
			fp_panel.add(buttonFindPwd);          
	        fp_label_id.setText("�˺�");
	        fp_label_id.setBounds(new Rectangle(45,45,50,25));
	        fp_label_tel.setText("�ֻ���");
	        fp_label_tel.setBounds(new Rectangle(45,87,50,25));
	        fp_txt_id.setBounds(100,45,155,25);
	        fp_txt_tel.setBounds(100,87,155,25);
	        fp_label_signup.setText("ע��");
	        fp_label_signup.setBounds(28, 135, 66, 40);
	        fp_label_login.setText("��½");
	       	fp_label_login.setBounds(103, 135, 66, 40);
	        buttonFindPwd.setText("�һ�����");
	        buttonFindPwd.setBounds(190, 135, 92, 40);
        //ÇÐ»»
        fp_label_signup.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
        		CLayout.show(contentPane, "sign up panel");
        		thisWnd.setTitle("������ע��");
        	}
        });
        fp_label_login.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
        		CLayout.show(contentPane, "log in panel");
        		thisWnd.setTitle("�����ҵ�½");
        	}
        });
        //°´¼üµã»÷ÊÂ¼þ
        buttonFindPwd.addActionListener( new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		buttonFindpwdAction(e);
        	}
        } );

        CLayout.show(contentPane,"0");
		
		this.setVisible(true);
		
	}


	public static void main(String[] args) {
		 //Ö¸¶¨Ê¹ÓÃµ±Ç°µÄLook&Feel×°ÊÎ´°¿Ú¡£±ØÐëÔÚ´´½¨´°¿ÚÇ°Éè¶¨¡£
        JFrame.setDefaultLookAndFeelDecorated(true);
		Client MyLoginFrame= new Client();
	}
}

