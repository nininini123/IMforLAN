import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.lang.*;
import java.util.*;
import java.util.List;

class mainWnd extends JFrame {

	// static final int dirPORT= 10002;	//明文传输的端口号
	ServerSocket ListenSocket;
	Socket encrp_socket;
	BufferedReader encrp_socket_in;
	PrintWriter encrp_socket_out;
	public String encrptRcvMsg;
	String MyID;

	//JComponents
		JPanel contentPane;
		JPanel scrollPane = new JPanel();
		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints gbConstraints = new GridBagConstraints();

		JLabel onlineFriendLabel = new JLabel();
		//new JScrollPane()
		JScrollPane onlineFriendPane = new JScrollPane(scrollPane, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);// HORIZONTAL_SCROLLBAR_NEVER);
		// JLabel offlineFriendLabel = new JLabel();
		// JScrollPane offlineFriendPane = new JScrollPane();
		JLabel searchLabel = new JLabel();
		JTextField searchText = new JTextField();

		java.util.List<friendLabel> friend_list = new ArrayList<friendLabel>();
		// java.util.List<friendLabel> f_offline_list = new ArrayList<friendLabel>();
	
	public mainWnd(Client loginClientWnd,ServerSocket ListenSocket,Socket encrp_socket,String MyID) throws IOException {
		this.ListenSocket=ListenSocket;
		this.encrp_socket=encrp_socket;
		this.MyID = MyID;

		try {	
//			encrp_socket_in = new BufferedReader( new InputStreamReader(encrp_socket.getInputStream()) );
			encrp_socket_out = new PrintWriter(encrp_socket.getOutputStream(),true);
		}
		catch (IOException ioe1) {
			System.out.println("boom mainwnd.java ...:\t"+ioe1.toString());
		}

		//layout
			contentPane = (JPanel)this.getContentPane();
			contentPane.setLayout(gbLayout);

			//网格包layout的布局
			gbConstraints.fill = GridBagConstraints.BOTH;
			gbConstraints.gridwidth = GridBagConstraints.REMAINDER;	//行数列数
			gbConstraints.weightx = 1;
			// gbConstraints.gridx = 0;
			// gbConstraints.gridy = 0;

			gbConstraints.gridheight = 1;	//行数列数
			gbConstraints.weighty = 0;
			gbLayout.setConstraints(onlineFriendLabel,gbConstraints);
			contentPane.add(onlineFriendLabel);
			onlineFriendLabel.setText("我的好友");
			// onlineFriendLabel.setAlignmentX(CENTER_ALIGNMENT);
			onlineFriendLabel.setHorizontalAlignment(SwingConstants.CENTER);

			gbConstraints.gridheight = 10;	//行数列数
			gbConstraints.weighty = 1;
			gbConstraints.weightx = 1;
			gbLayout.setConstraints(onlineFriendPane,gbConstraints);
			contentPane.add(onlineFriendPane);
			/////////////test
			// onlineFriendPane.getViewport()
			// onlineFriendPane.setViewportView(new JPanel());
			BoxLayout boxLayout = new BoxLayout(scrollPane,BoxLayout.Y_AXIS);
			scrollPane.setLayout(boxLayout);

			this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			this.setResizable(true);
			this.setTitle("聊天室 "+MyID);
//			this.setSize(300,600);
			this.setBounds(loginClientWnd.getX(),loginClientWnd.getY(),250,500);
			this.setVisible(true);


		new clientThread(this,encrp_socket).start();
		new client_listen_threads(ListenSocket,this).start();
	}

	public void setFriendStatus(String id, boolean online) {
		boolean lbAlreadyExists = false;
		////////////////////////////////
		//先判断是否在列表中
		//然后有就改状态
		//没有就增加该朋友
		for (int i = 0;i < friend_list.size(); i ++) {
			if (friend_list.get(i).getID().equals(id)) {
				lbAlreadyExists=true;
				friend_list.get(i).setStatus(online);
				System.out.println(id+ "already exits . status changing...");
				break;
			}
		}
		if (!lbAlreadyExists) {
			friendLabel tempFriendLabel = new friendLabel(id,online,encrp_socket,this) ;
			friend_list.add( tempFriendLabel );
			scrollPane.add(tempFriendLabel);
			System.out.println("adding friendLabel...");
			scrollPane.updateUI();
		}
	}
	public friendLabel findFriendLabel(String id) {
		for (int i= 0;i<friend_list.size();i++) {
			if (friend_list.get(i).getID().equals(id))	return friend_list.get(i) ;
		}
		return null;
	}
}

// 以下要改改改啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊
// clientThread 是加密的吧。。。。。。。。。。。。。。。。。。。。。。。。。。。。。。
// 监听端口 收到用户消息就开窗口
class client_listen_threads extends Thread  {
	private ServerSocket ListenSocket;
	private mainWnd mainWindow;
	public client_listen_threads(ServerSocket ListenSocket, mainWnd mainWindow) {
		System.out.println("client_listen_threads starting...mainwnd 135 ddddd....");
		this.ListenSocket=ListenSocket;
		this.mainWindow = mainWindow;
	}
	public void run() {
		while (true) {
			try {
				//s 来自别人发来的消息
				Socket s = ListenSocket.accept();
				System.out.println( "mainWnd.java clientlistenthread 144  accepting connection...\t"+s.getInetAddress().toString());
				new p2pThread(mainWindow,s).start();
			}
			catch (IOException e) {
				System.out.println("damn mainwnd.java 130:\t"+e.toString());
			}
		}
	}
}
