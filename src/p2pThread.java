import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class p2pThread extends Thread {
	String id;
//	boolean status;
	mainWnd wnd;
	Socket s;
	BufferedReader br;
	PrintWriter pw;
	String rcvMsg;

	p2pThread(mainWnd wnd, Socket s) {
		System.out.println("p2p thread starting... p2pthread.java ...17 ");
		this.wnd = wnd;
		this.s = s;
		try {
			this.br = new BufferedReader(new InputStreamReader(this.s.getInputStream()));
			this.pw = new PrintWriter(this.s.getOutputStream(),true);
		}
		catch (IOException ioe) {
			System.out.println("error in p2pthread constructor >_<...:\t"+ioe.toString());
		}
//		new chatWnd(s);	//被动开窗口
		;;;;;;;;;;;;;;;;;;;;
	}
	public void run() {
		try {
			System.out.println("p2pthread.33 send sth for a test..");
			this.pw.println("send test from p2pthread.34");
			System.out.println("ready to readline p2pthread.java 23333");
			rcvMsg = this.br.readLine();
			System.out.println("rcv p2pthread....233333:\t"+rcvMsg);
			if (rcvMsg.startsWith("chatFrom")) {
				id = rcvMsg.split(" ")[1];
				//////////////////////////////////////
				System.out.println("gonna open chatwindow p2pthread 25...");
				new chatWnd(id,true,wnd.findFriendLabel(id),s,chatWnd.RCV);//fLabel 要想办法改改改改改///////////////////
			}
//			while (true) {
//				rcvMsg= this.br.readLine();
//			}
//			先注释 后面再恢复
		}
		catch (IOException ioe) {
			System.out.println("error in p2pthread.43 run >_<...:\t"+ioe.toString());
		}
		;;;;;;;;;;;;;;;;;;
	}
}