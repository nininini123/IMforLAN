//这个是加密的 
//客户端-服务器

import java.io.*;
import java.net.Socket;

class clientThread extends Thread {
	private Socket s;
	private mainWnd mWindow;
	clientThread(mainWnd wnd, Socket s) {
		this.mWindow = wnd;
		this.s=s;
		System.out.println("new clientThread.");
	}

	public void run() {
		BufferedReader br;
		PrintWriter pw;
//		String rcvMsg = null;
		String[] str_temp;
		try {
			br = new BufferedReader( new InputStreamReader(s.getInputStream()));		
			pw = new PrintWriter(s.getOutputStream(),true);
			while(true) {
				mWindow.encrptRcvMsg = br.readLine();
				System.out.println("clientthread 26 receive:\t"+mWindow.encrptRcvMsg);
				if (mWindow.encrptRcvMsg.startsWith("FRIENDLIST")) {
					str_temp = mWindow.encrptRcvMsg.split(" ");
					mWindow.setFriendStatus(str_temp[1],true);
					System.out.println("setFriendStatus\t" + str_temp[1] + " "+ str_temp[0]);
				}
				else if (mWindow.encrptRcvMsg.startsWith("offlineMsgFrom")) {
					String[] tempMsg = mWindow.encrptRcvMsg.split(" ");
					chatWnd rcvChatWnd;
					rcvChatWnd = mWindow.findFriendLabel(tempMsg[1]).chatWindow;
					if (rcvChatWnd== null) {
				 		//还没开聊天窗口
						mWindow.findFriendLabel(tempMsg[1]).chatWindow = new chatWnd(tempMsg[1],false,mWindow.findFriendLabel(tempMsg[1]),s,chatWnd.RCV);
						rcvChatWnd=mWindow.findFriendLabel(tempMsg[1]).chatWindow;
					}
					for (int i=1;i<tempMsg.length;i++) {
						rcvChatWnd.chat_record.append(tempMsg[i]+" ");
					}
					rcvChatWnd.chat_record.append("\n");
				}
			}
		}
		catch (IOException e) {
			System.out.println("IOException in clientThread here...\t:\n" + e.toString());
		}
	}
}