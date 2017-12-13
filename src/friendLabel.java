import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.lang.*;

// class friendLabel extends JLabel implements MouseListener {
class friendLabel extends JLabel  {
	private boolean status;
	private String id;
	public mainWnd mWindow;
	static final int FL_WIDTH = 200;
	static final int FL_HEIGHT = 40;
	Socket socket;
	Socket encrp_socket;
	chatWnd chatWindow = null;
	// BufferedReader encrp_br= null;
	PrintWriter encrp_pw = null;
	public boolean isOnline() {
		return this.status;
	}
	public String getID() {
		return this.id;
	}
	public void setStatus(boolean a) {
		this.status = a;
	}

	friendLabel(String id, boolean status, Socket encrp_socket,mainWnd parentMainWnd) {
		this.id = id;
		this.socket = null;
		this.encrp_socket = encrp_socket;
		this.mWindow = parentMainWnd;
		try {
			// encrp_br = new BufferedReader(new InputStreamReader(encrp_socket.getInputStream()));
			encrp_pw = new PrintWriter(encrp_socket.getOutputStream(), true);
		}
		catch (IOException ioe) {
			System.out.println(ioe.toString());
		}
		// this.status = status;
		setStatus(status);
		setText(id);
		// this.setSize(FL_WIDTH, FL_HEIGHT);
		setHorizontalAlignment(SwingConstants.CENTER);
		this.setHorizontalTextPosition(CENTER);
		setOpaque(true);//不透明

		friendLabel thisFLabel = this;

		this.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
        		System.out.println("mousePressed...");
				if (chatWindow==null) {
					//先get对方状态
					String[] rcvMsg;
					String friend_host = null;
					int friend_port;
					if(encrp_pw==null) System.out.println("boom shakaraka");
					encrp_pw.println("getStatus "+thisFLabel.getID());
					System.out.println("getting status friendlabel.java 64 233...");
					// if (encrp_br==null) System.out.println("boom friendlabel 67...");

					do {
						try {
							Thread.sleep(5);
						}
						catch (InterruptedException intrptE) {
							System.out.println("sth in friendlabel 72..gods knows..44:\n"+intrptE.toString());
						}
						rcvMsg = mWindow.encrptRcvMsg.split(" ");
						System.out.println("eto....friendlabel 70 .."+rcvMsg[0]);
						System.out.println("friendlabel.76 status is:\t"+ rcvMsg[1]);
						if (rcvMsg[0].equals(id) && rcvMsg[1].equals("1")) {	//online
							System.out.println("gonna set status 1");
							friend_host = rcvMsg[2];
							friend_port =  Integer.parseInt(rcvMsg[3]);
							try {
								System.out.println("friendlabel 78 new socket...:\t"+friend_host+friend_port);
								socket = new Socket(friend_host,friend_port);
							}
							catch (IOException ioe) {
								System.out.println("friendlabel 81 tututtu\t"+ioe.toString());
							}
							System.out.println("setting status true.. friendlabel 84444");
							setStatus(true);
							System.out.println("friendlabel.90 here status is " +isOnline());
						}
						else if (rcvMsg[1].equals("0")) {	//offline
							System.out.println("gonna set status 0");
							socket = encrp_socket;
							setStatus(false);
						}
						else {
							System.out.println("emmmmm...friendlabel77..."+rcvMsg[0]+"\t "+rcvMsg[1]);
						}
					} while (!rcvMsg[0].equals(id));
					//主动打开tcp连接
					System.out.println("friendlabel.10777777gonna new chatwnd... "+id+" "+isOnline());
					chatWindow = new chatWnd(id,isOnline(),thisFLabel, socket,chatWnd.SEND);//,encrp_socket);
				}
				else {
					chatWindow.requestFocus(); //跳转当前活跃窗口
				}
		    }
		        } );
	}

}
