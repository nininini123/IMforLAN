import java.io.*;
import java.net.*;
import java.security.KeyStore;
// import java.net.ssl.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;   
import javax.net.ssl.SSLContext;   
import javax.net.ssl.SSLServerSocketFactory;
import javax.swing.*;
import java.lang.*;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;



public class socketServer extends JFrame{
	public static final int PORT=10002;
    // private final static Logger logger = Logger.getLogger(MyServer.class.getName());
	// db_query serverDB = new db_query();
	JPanel contentPane;
	GridBagLayout gbLayout = new GridBagLayout();
	GridBagConstraints gbConstraints = new GridBagConstraints();
	// JPanel scrollPane = new JPanel();

	//new JScrollPane()
	JTextArea textArea = new JTextArea();
	JScrollPane textPane = new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// HORIZONTAL_SCROLLBAR_NEVER);
	JButton buttonCls = new JButton();

	public static void main(String[] args) {
		new socketServer();

    }
    public socketServer() {
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		contentPane = (JPanel)this.getContentPane();
		contentPane.setLayout(gbLayout);

		//网格包layout的布局
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;	//行数列数
		gbConstraints.weightx = 1;

		gbConstraints.gridheight = 5;	//行数列数
		gbConstraints.weighty = 1;
		// textPane.add(textArea) ;
		gbLayout.setConstraints(textPane,gbConstraints);
		contentPane.add(textPane);

		gbConstraints.gridheight = 1;	//行数列数
		gbConstraints.weighty = 0;
		gbConstraints.weightx = 1;
		gbLayout.setConstraints(buttonCls,gbConstraints);
		contentPane.add(buttonCls);
		buttonCls.setText("CLEAR");
		textArea.setEditable(false);

		buttonCls.addActionListener(  new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(null);
			}
		} );


		this.setBounds(200,200,400,400);
		this.setVisible(true);

		try {
			System.out.println("server starting...\n");
			ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
			ServerSocket SSLServer = factory.createServerSocket(PORT);

			while (true) {
				Socket socket = SSLServer.accept();
				System.out.println("accepting connection...\n");
				new ServerThread(socket,this).start();	//创建进程
				// invoke(socket);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			System.out.println(ex.toString());
		}

	}
}

class ServerThread extends Thread {
	private Socket s;
	socketServer serverWindow;
	ServerThread(Socket s,socketServer serverWnd) {
		this.s=s;
		this.serverWindow = serverWnd;
	}

	public void run() {
		// ObjectInputStream is = null;
		BufferedReader br = null;
		// ObjectOutputStream os;
		PrintWriter pw = null;
		String rcvMsg[] = null;

			
			db_query serverDB = new db_query();
			ResultSet rs = null;
			Object obj = null ;
			encrypt_info info = null ;
			encrypt_info fr_info = null ;

		try {
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			// is = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));	//收到的socket的字节流 转化成字符流
			// os = new ObjectOutputStream(socket.getOutputStream());////
			pw = new PrintWriter(s.getOutputStream(),true);	///要上还是要下

			while (true) {
				// System.out.println("thread...");
				// info=new encrypt_info();
				String readLineBuff;
				readLineBuff = br.readLine();
				System.out.println( new BASE64Encoder().encodeBuffer(readLineBuff.getBytes()) );
				info = new encrypt_info( readLineBuff );//(String)is.readObject() );
				System.out.println(
					"type: " + info.getInfoType() +
					"\nuser: " + info.getName() +
					"\npwd:" + info.getPassword() +
					"\ntel:" + info.getTel()	);
				serverWindow.textArea.append(
						"type: " + info.getInfoType() +
								"\nuser: " + info.getName() +
								"\npwd:" + info.getPassword() +
								"\ntel:" + info.getTel()	);
				if (info.getInfoType().equals(encrypt_info.LOG_IN)) {
					System.out.println("processing logging request...");
					rs=serverDB.SQLExecute("SELECT * FROM user WHERE (id = \"" + info.getName() + "\");");
					//log in success
					if (rs.next()) {
						if (rs.getString("pwd").equals(info.getPassword())) {
							pw.println("correct password");
							serverWindow.textArea.append("correct password\n");

							//读取发送 在线 离线好友
							////////////////////////////////////////是否为空 会产生异常
							rs=serverDB.SQLExecute("SELECT * FROM user;");
							while (rs.next()) {
								if (!rs.getString("id").equals( info.getName()) ) {
									System.out.println("socketserver.92 getting friends...");
									pw.println("FRIENDLIST " + rs.getString("id") );
									pw.flush();
								}
							}

							System.out.println("serversocket.103333 sql:\t SELECT * FROM user WHERE (id = \"" + info.getName() + "\");");
							rs=serverDB.SQLExecute("SELECT * FROM user WHERE (id = \"" + info.getName() + "\");");
							if (rs.next()) {
								if (rs.getInt("offline_msg")==1) {
									//登陆后发送离线消息
									FileInputStream fileIS = new FileInputStream("D:\\miscellaneous\\MyProgram\\java\\netWorks\\intelliJ\\out\\production\\intelliJ\\" + info.getName());
									BufferedReader fileBR = null;
									try {
										fileBR = new BufferedReader(new InputStreamReader(fileIS, "UTF-8"), 512);
										// 读取一行，存储于字符串列表中
										for (String line = fileBR.readLine(); line != null; line = fileBR.readLine()) {
											pw.println("offlineMsgFrom " + line);
										}
										serverDB.SQLUpdate("update user set offline_msg=0 WHERE id = \""+ info.getName() + "\" ;");
									} catch (FileNotFoundException fnfe) {
										fnfe.printStackTrace();
										System.out.println("socketserver.118wuwawawa exception:\t" + fnfe.toString());
									} catch (IOException ioe) {
										ioe.printStackTrace();
										System.out.println("socketserver.121wuwawawa exception:\t" + ioe.toString());
									} finally {
										try {
											if (fileIS != null) {
												fileIS.close();
												fileIS = null;
											}
										} catch (IOException e) {
											e.printStackTrace();
											System.out.println("socketserver.130wuwawawa exception:\t" + e.toString());
										}
									}
								}
							}
							//以下是服务器与客户端发送各种消息
							// SocketAddress userAddr = (SocketAddress)is.readObject();
							String[] userAddr;
							userAddr = br.readLine().split(" ");
							System.out.println("user address:\t" +userAddr[0] +" "+userAddr[1] );//(String)is.readObject());
							System.out.println("update user set ip_address=\""+userAddr[0]+"\"  WHERE id = \""+ info.getName() + "\" ;");
							if ( serverDB.SQLUpdate("update user set ip_address=\""+userAddr[0]+"\"  WHERE id = \""+ info.getName() + "\" ;") == 0 ) System.out.println("database update failed...socketServer.java >_<");
							System.out.println("update user set port="+userAddr[1]+"  WHERE id = \""+ info.getName() + "\" ;");
							if ( serverDB.SQLUpdate("update user set port="+userAddr[1]+"  WHERE id = \""+ info.getName() + "\" ;") == 0 ) System.out.println("database update failed...socketServer.java >_<");
							serverDB.SQLUpdate("update user set online=1 WHERE id = \""+ info.getName() + "\" ;");
							System.out.println("update user set online=1 WHERE id = \""+ info.getName() + "\" ;");
							while (true) {
								//接收登陆后用户的各种请求
								//更新好友状态
								//发送离线消息
								rcvMsg = (br.readLine()).split(" ") ;
								// rcvMsg = ((String) is.readObject()).split(" ") ;

								System.out.println("socketserver.120 get msg:\t"+ rcvMsg[0]);
								if (rcvMsg[0].equals("getStatus")) {
									;///
									try {
										rs = serverDB.SQLExecute("SELECT  online,ip_address,port FROM user WHERE (id = \"" + rcvMsg[1] + "\");");
										if (rs.next()) {
											System.out.println(rcvMsg[1] + " " + rs.getInt("online")+" "+rs.getString("ip_address")+" "+rs.getInt("port"));
											pw.println(rcvMsg[1] + " " + rs.getInt("online")+" "+rs.getString("ip_address")+" "+rs.getInt("port"));
										}
									}
									catch (SQLException sqle1) {
										System.out.println("sockerserver.java boom 12319:\t"+sqle1.toString());
									}
								}
								else if (rcvMsg[0].equals("chatFrom")) {
									pw.println("offline msg will not ne received immediately.");
								}
								else if (rcvMsg[0].equals("offlineMsgTo")) {
									try {
										int i;
										StringBuffer sb=new StringBuffer();
										//建立文件 存储离线消息 置数据库offline_msg为1
										//FileOutputStream offlineMsgFileOS = new FileOutputStream(rcvMsg[1]) ;
										//rcvMsg[1]是接收方用户名
										System.out.println("socketserver.144...sql update:\tupdate user set offline_msg=1 WHERE id = \""+ rcvMsg[1] + "\" ;");
										serverDB.SQLUpdate("update user set offline_msg=1 WHERE id = \""+ rcvMsg[1] + "\" ;");
										File file=new File("D:\\miscellaneous\\MyProgram\\java\\netWorks\\intelliJ\\out\\production\\intelliJ\\"+rcvMsg[1]);
										if(!file.exists())		file.createNewFile();
										i=2;
										FileOutputStream offlineMsgFileOS =new FileOutputStream(file,true);
										while (!rcvMsg[i].equals("$end$")) {
											for ( ; i < rcvMsg.length; i++) {
												if (rcvMsg[i].equals("$end$")) {
													sb.append("\n");
													break;
												} else sb.append(rcvMsg[i]+" ");
											}
											if (!rcvMsg[i].equals("$end$")) i=0;
										}
										offlineMsgFileOS.write(sb.toString().getBytes("utf-8"));
										offlineMsgFileOS.close();
									}
									catch (IOException e) {
										System.out.println("socketserver.156 ioexception:\t"+e.toString());
									}
								}
							}
						}
						else {
							pw.println("wrong password");
							serverWindow.textArea.append("wrong password");
							pw.flush();
						}
					}	
					else {
						pw.println("wrong username");						
						pw.flush();
					} 
				}
	    		else if (info.getInfoType().equals(encrypt_info.SIGN_UP)) {
					System.out.println("processing signing up request...");
					serverWindow.textArea.append("processing signing up request...");
					rs=serverDB.SQLExecute("SELECT * FROM user WHERE (id = \"" + info.getName() + "\");");
					if (!rs.next()) {
						int update_flag = serverDB.SQLUpdate("INSERT INTO user( id, pwd, tel) VALUES( \""
							+ info.getName() + "\", \"" + info.getPassword() + "\", \"" + info.getTel() + "\" );");
						switch (update_flag) {
							case (0)	:pw.println("fail to sign up");
							case (1)	:pw.println("succeed to sign up");
							default 	:pw.println("check socketServer.java...do not no what happened...");
						}
						serverWindow.textArea.append("signing up sucessful");
					}
					else {
						pw.println("username already exists");
						serverWindow.textArea.append("username already exists");
					}
				}
				else if (info.getInfoType().equals(encrypt_info.FIND_PWD)) {
					System.out.println("processing finding password request...");
					// pw.println("processing finding password request ... ");
					rs=serverDB.SQLExecute("SELECT * FROM user WHERE (id = \"" + info.getName() + "\");");
					if (!rs.next()) {
						pw.println("illegal id");
					}
					else {
						if (rs.getString("tel").equals(info.getTel())) {
							pw.println("pwd: "+rs.getString("pwd"));
						}
						else {
							pw.println("wrong tel");
						}
					}
				}
			}
		}
		catch (IOException e) {
			System.out.println(e.toString());
		}
//		catch (ClassNotFoundException ce) {
//			System.out.println(ce.toString());
//		}
		catch (SQLException sql1) {
			System.out.println("maybe a wrong username? get Exception:\t" +sql1.toString());
			pw.println("wrong username 233");
		}

		// finally {
		System.out.println("closing connection ...\n");
		serverDB.SQLUpdate("update user set online=0 WHERE id = \""+ info.getName() + "\" ;");
		serverWindow.textArea.append("update user set online=0 WHERE id = \""+ info.getName() + "\" ;");
		System.out.println("update user set online=0 WHERE id = \""+ info.getName() + "\" ;");
		try {
			if (br!= null) 
				br.close();
			if (pw!= null) 
				pw.close();
			if (s!= null) 
				s.close();
		}
		catch (IOException e) {
			System.out.println("here 1\n"+ e.toString());
		}
		
	}

}


class chatThread extends Thread {
	private Socket s;
	chatThread(Socket s) {
		this.s=s;
	}
	public void run() {
		;
	}
}