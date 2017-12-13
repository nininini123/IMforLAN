import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
//import javax.swing.border.Border;

import java.io.*;
import java.net.*;
import javax.net.*;
import javax.net.ssl.*;
import java.lang.*;
import java.util.*;
class chatWnd extends JFrame {
	final static boolean SEND =true;
	final static boolean RCV =false;
	final static String SAVEPATH = "D:\\";

	friendLabel frdLabel;
	String id; 
	boolean status;
	Socket socket;
//	Socket encrp_socket;
	BufferedReader  br;
	PrintWriter  pw;
	DatagramSocket dsk;
	DatagramPacket dpk ;	//Ҫ�öԷ��ĵ�ַ��ʼ��
	File f ;

	//JComponents
		JPanel contentPane = null ;
		JTextArea chat_record = new JTextArea();
		JTextArea input_txt= new JTextArea();
		JButton btn_send = new JButton();
		JButton btn_clr = new JButton();
		JButton btn_file_transfer= new JButton();
		GridBagLayout gbLayout = new GridBagLayout();
		GridBagConstraints gbConstraints = new GridBagConstraints();

	public String getStatusStr() {
		if (status) {
			return "online";
		}
		return "offline";
	}
	public void setStatus(boolean sts) {
		this.status = sts ;
		this.setTitle(frdLabel.mWindow.MyID +" to " + id +" " + getStatusStr());
	}
	void buttonSendAction(ActionEvent e) {
		System.out.println("chatwnd.45 45 buttonSend clicked.");
		String sendMsg = input_txt.getText();
		input_txt.setText(null);
		System.out.println("chatwnd.48 sendMsg=" +sendMsg);
		if (status) pw.println(this.frdLabel.mWindow.MyID+"\t" +sendMsg+"\n");
		else pw.println("offlineMsgTo "+id +" "+this.frdLabel.mWindow.MyID +" "+sendMsg +" $end$ \n");
		chat_record.append("I:\t"+sendMsg+"\n");
		pw.flush();
	}
	void buttonClearAction(ActionEvent e) {
		input_txt.setText(null);
		chat_record.setText(null);
	}
	void buttonFileAction(ActionEvent e) {
		if (status) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fileChooser.setCurrentDirectory(new File("d:\\"));//�ļ�ѡ�����ĳ�ʼĿ¼��Ϊd��
			int state=fileChooser.showOpenDialog(null);//�˾��Ǵ��ļ�ѡ��������Ĵ������
			if(state==JFileChooser.CANCEL_OPTION){
				return;//�����򷵻�
			}
			else{
				f=fileChooser.getSelectedFile();//fΪѡ�񵽵��ļ�
//				DatagramSocket dsk;
//				DatagramPacket dpk ;	//Ҫ�öԷ��ĵ�ַ��ʼ��
				//dpk= new DatagramPacket(buf, buf.length,new InetSocketAddress(InetAddress.getByName("localhost"), UDPUtils.PORT));
//				DatagramSocket dsk = new DatagramSocket(UDPUtils.PORT + 1, InetAddress.getByName("localhost"));
				// DatagramPacket dpk = new DatagramPacket()
				try {
					//�Լ���ʼ��һ��udp�˿�
					//then�����Լ��Ķ˿ں�
					//�Է� ѡ������� �յ����ضԷ��Ķ˿ں� ��ʼ����
					//�Է��ܾ� ��return
					dsk = new DatagramSocket(0);
					pw.println("FILEPORT " + dsk.getLocalPort() +" " +f.getName() +" \n");
					System.out.println("FILEPORT " + dsk.getLocalPort() +" " +f.getName() +" \n");
					pw.flush();
				}
				catch (SocketException se) {
					System.out.println("error in chatwnd.76:\t" + se.toString());
				}
					chat_record.append("prepared to send file:\t"+f.getAbsolutePath());
			}

		}
		else {
			JOptionPane.showMessageDialog(null,"cannot transfer file to offline user","warning",JOptionPane.WARNING_MESSAGE);
		}
	}

	//constructor  ��������
	//һ����������ȥ �Լ�newһ��socket Ȼ��Ҫ�Է���ip�Ͷ˿ں�
	//һ�����յ��� ��Ҫ�Է���socket����serversocket��׽���ģ� Ȼ��Ҫ
	public chatWnd(String id, boolean status,friendLabel fLabel ,Socket socket,boolean posi_start) { //,Socket encrp_socket) {
																					//posi_start��ʾ�Լ���������ĻỰ
		//���socket ����������ǺͶԷ��� ���ߵĻ� ���Ƿ�������
		//Ȼ�������socketҲֻ�����﷢�� ������Ϣ����mwindow���ַ�����
		System.out.println("new chatwnd.62 wuwuwu: "+id+" "+status+" "+fLabel.getID()+" "+posi_start);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);//HIDE_ON_CLOSE);//
		this.frdLabel = fLabel;
		this.frdLabel.chatWindow = this;
		this.id = id;
		this.socket = socket;
		this.setStatus(status);//�������˱���
		this.setBounds(frdLabel.mWindow.getX(),frdLabel.mWindow.getY(),350,500);
//		this.setSize(400,600);

		try {
			if(status) br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			pw = new PrintWriter(this.socket.getOutputStream());
			//�ոս��������ȸ��Է������Լ��ĸ�����Ϣ
			if (posi_start) {

				pw.println("chatFrom "+ fLabel.mWindow.MyID+"\n");
//				pw.println("chatFrom "+ fLabel.mWindow.MyID+"\n");///////////������������һ���ղ���������������
				pw.flush();
				if (status) {
					System.out.println("chatwnd.java.78 sending: chatFrom "+ fLabel.mWindow.MyID);
					System.out.println("chatwnd.java.79...rcv ing :\t"+br.readLine() );
				}
			}
		}
		catch (IOException ioe) {
			System.out.println("chatWnd constructor initializing error..76..:\t"+ioe.toString());
		}

		//layout
			contentPane = (JPanel)this.getContentPane();
			contentPane.setLayout(gbLayout);
			//�����layout�Ĳ���
			gbConstraints.fill = GridBagConstraints.BOTH;
			gbConstraints.gridwidth = GridBagConstraints.REMAINDER;	//��������
			gbConstraints.gridheight = 8;	//��������
			gbConstraints.weightx = 1;
			gbConstraints.weighty = 0.6;

			JScrollPane chat_recordPane = new JScrollPane(chat_record, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// HORIZONTAL_SCROLLBAR_NEVER);
			gbLayout.setConstraints(chat_recordPane,gbConstraints);
			contentPane.add(chat_recordPane);
			chat_record.setEditable(false);
			chat_record.setLineWrap(true);
			chat_record.setText("test\n");
			chat_record.setAlignmentX(LEFT_ALIGNMENT);
			chat_record.setAlignmentY(TOP_ALIGNMENT);
			chat_record.setBorder(BorderFactory.createLineBorder(Color.BLACK));

			gbConstraints.gridheight = 3;	//��������
			gbConstraints.weighty = 0.4;
			JScrollPane input_txtPane = new JScrollPane(input_txt, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);// HORIZONTAL_SCROLLBAR_NEVER);
			gbLayout.setConstraints(input_txtPane,gbConstraints);
			contentPane.add(input_txtPane);
			input_txt.setEditable(true);
			input_txt.setLineWrap(true);
			input_txt.setAlignmentX(LEFT_ALIGNMENT);
			input_txt.setAlignmentY(TOP_ALIGNMENT);
			input_txt.setBorder(BorderFactory.createLineBorder(Color.BLACK));

			//������������
			gbConstraints.fill = GridBagConstraints.HORIZONTAL;
			gbConstraints.gridwidth = 2;	//��������
			gbConstraints.gridheight = 1;	//��������
			gbConstraints.weightx = 0.3;
			gbConstraints.weighty = 0;
			gbConstraints.ipadx=5;
			gbConstraints.ipady=1;

			btn_clr.setText("���");
			gbLayout.setConstraints(btn_clr,gbConstraints);
			contentPane.add(btn_clr);
			btn_send.setText("����");
			gbLayout.setConstraints(btn_send,gbConstraints);
			contentPane.add(btn_send);
			btn_file_transfer.setText("���ļ�");
			gbLayout.setConstraints(btn_file_transfer,gbConstraints);
			contentPane.add(btn_file_transfer);

		btn_send.addActionListener( new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		buttonSendAction(e);
        	}
        } );
		btn_clr.addActionListener( new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		buttonClearAction(e);
        	}
        } );
		btn_file_transfer.addActionListener( new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		buttonFileAction(e);
        	}
        } );
		this.setVisible(true);
		if(status) new getMsgThread(this,br).start();
	}
}

class getMsgThread extends Thread {
	private BufferedReader br;
	chatWnd chatWin;
	int bindPort;
	getMsgThread(chatWnd chatwin,BufferedReader br) {
		System.out.println("chatwnd.163hhhhh new getMsgThread");
		this.br = br;
		this.chatWin=chatwin;
	}

	@Override
	public void run() {
		try {
			System.out.println("prepared for getting msg...");
			String msgReceived;

			while (true) {
				msgReceived =  br.readLine();
				chatWin.chat_record.append(msgReceived+"\n");
				System.out.println("chatwnd.174:success get msg...\t"+msgReceived);
				if (msgReceived.startsWith("FILEPORT")) {
					bindPort = Integer.parseInt( msgReceived.split(" ")[1] );
					System.out.println("port of filesender is "+ bindPort);
					chatWin.dsk = new DatagramSocket(0);
					chatWin.dsk.connect(new InetSocketAddress(this.chatWin.socket.getInetAddress(),bindPort));
					if ( JOptionPane.showConfirmDialog(null,
							"save file "+msgReceived.split(" ")[2] +" as "+chatWnd.SAVEPATH +msgReceived.split(" ")[2],
							"receive file",JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION )
					{
						chatWin.dsk = new DatagramSocket(0);
						chatWin.pw.println("MYRCVPORT "+ chatWin.dsk.getLocalPort() +" \n");
						chatWin.pw.flush();
						// chatWin.dpk = new DatagramPacket()
						new UDPServer(this.chatWin.dsk, this.chatWin.socket.getInetAddress(),bindPort, chatWnd.SAVEPATH +msgReceived.split(" ")[2]);
					}
				}
				else if (msgReceived.startsWith("MYRCVPORT") ) {
					int rcvPort = Integer.parseInt( msgReceived.split(" ")[1] );
					UDPClient fileSender = new UDPClient(this.chatWin.dsk, this.chatWin.socket.getInetAddress(), rcvPort, chatWin.f.getAbsolutePath());

				}
			}
		}
		catch (IOException ioe) {
			System.out.println("chatwnd.184 getMsgThread run() encountered sth:\t"+ioe.toString());
		}
	}
}

