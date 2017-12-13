import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Timer;
import java.util.TimerTask;

// import com.bill.udp.util.UDPUtils;

public class UDPClient {
	
//	private static final String SEND_FILE_PATH = "D:\\miscellaneous\\MyProgram\\java\\netWorks\\udpTest\\test.ISO";
	
//	public static void main(String[] args){
//		new UDPClient(dpk,dsk);
//
//	}
	public UDPClient( DatagramSocket dsk ,InetAddress receiverIP,int receiverPort ,String sendFilePath) {

		long startTime = System.currentTimeMillis();
		
		byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
		byte[] receiveBuf = new byte[1];

		
		RandomAccessFile accessFile = null;
		DatagramPacket dpk = null;
		// DatagramSocket dsk = null;
		int readSize = -1;
		// byte flag = 0;
		try {
			dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(receiverIP, receiverPort));
			//dsk = new DatagramSocket(UDPUtils.PORT, InetAddress.getByName("localhost"));
			buf[0] = 1;
			accessFile = new RandomAccessFile(sendFilePath,"r");
			int sendCount = 0;
			while((readSize = accessFile.read(buf,1,buf.length-1)) != -1){
				if (buf[0] == 1) buf[0] = 0;
				else buf[0] = 1;
				dpk.setData(buf, 0, readSize+1);
				dsk.send(dpk);
				Timer timer ;
				timer= new Timer();
				timer.schedule(new resendTask( dsk,dpk, buf, readSize,timer,receiverIP, receiverPort) , 1000);

				// wait server response 
				{
					dpk.setData(receiveBuf, 0, receiveBuf.length);
					dsk.receive(dpk);
					timer.cancel();

					// confirm server receive
					// if(!UDPUtils.isEqualsByteArray(UDPUtils.successData,receiveBuf,dpk.getLength())){
					// 	System.out.println("resend ...");
					// 	dpk.setData(buf, 0, readSize);
					// 	dsk.send(dpk);
					// }else break;
				}
				
				System.out.println("send count of "+(++sendCount)+"!");
			}
			// send exit wait server response
			while(true){
				System.out.println("client send exit message ..");
				dpk.setData(UDPUtils.exitData,0,UDPUtils.exitData.length);
				dsk.send(dpk);

				dpk.setData(receiveBuf,0,receiveBuf.length);
				dsk.receive(dpk);
				// byte[] receiveData = dpk.getData();
				if(!UDPUtils.isEqualsByteArray(UDPUtils.exitData, receiveBuf, dpk.getLength())){
					System.out.println("client Resend exit message ....");
					dsk.send(dpk);
				}else
					break;
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(accessFile != null)
					accessFile.close();
				if(dsk != null)
					dsk.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("time:"+(endTime - startTime));
	}
}

class resendTask extends TimerTask {
	DatagramSocket dsk;
	DatagramPacket dpk;
	byte[] buf;
	int readSize;
	//UDPClient udpClient;
	Timer timer;
	InetAddress receiverIP; int receiverPort ;
	public resendTask( DatagramSocket dsk,DatagramPacket dpk, byte[] buf,int readSize,Timer t,InetAddress receiverIP, int receiverPort) {
		this.dpk=dpk;
		this.dsk=dsk;
		this.buf=buf;
		this.readSize=readSize;
		this.timer = t;
		this.receiverIP = receiverIP;
		this.receiverPort = receiverPort ;
	}
	@Override
	public void run() {
		System.out.println("timeout resend ...");
		dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(receiverIP, receiverPort));
		System.out.println("new dpk");
		dpk.setData(buf, 0, readSize+1);
		System.out.println("data for resend set...");
		try {
			dsk.send(dpk);
			System.out.println("resending  "+ buf[0]+"  ...");
		}
		catch (IOException ioE) {
			System.out.println("UDPClient.118 error:" +ioE.toString());
		}

		//Timer timer= new Timer();
		timer.schedule(new resendTask( dsk,dpk, buf, readSize,timer,receiverIP, receiverPort) , 1000);
		System.out.println("new timer schedule...");
	}
}
