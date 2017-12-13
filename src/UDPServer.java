import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Timer;

// import com.bill.udp.util.UDPUtils;


public class UDPServer {
	
	//private static final String SAVE_FILE_PATH = "D:\\miscellaneous\\MyProgram\\java\\netWorks\\udpTest\\test2.iso";
	
//	public static void main(String[] args) {
//		new UDPServer();
//	}
		
	public UDPServer(DatagramSocket _dsk,InetAddress senderIP, int senderPort,String savePath) {
		byte[] buf = new byte[UDPUtils.BUFFER_SIZE];
		
		DatagramPacket dpk = null;
		DatagramSocket dsk = null;
		BufferedOutputStream bos = null;
		try {
			
			dpk = new DatagramPacket(buf, buf.length,new InetSocketAddress(senderIP, senderPort));
			dsk = _dsk;	//new DatagramSocket(UDPUtils.PORT + 1, InetAddress.getByName("localhost"));
			bos = new BufferedOutputStream(new FileOutputStream(savePath));
			System.out.println("wait client ....");
			dsk.receive(dpk);
			
			int readSize = 0;
			int readCount = 0;
			int flushSize = 0;
			byte lastRcvFlag = 1;
			while((readSize = dpk.getLength()) != 0){
				// validate client send exit flag  
				if(UDPUtils.isEqualsByteArray(UDPUtils.exitData, buf, readSize)){
					System.out.println("server exit ing ...");
					// send exit flag 
					dpk.setData(UDPUtils.exitData, 0, UDPUtils.exitData.length);
					dsk.send(dpk);
					break;
				}
				if (lastRcvFlag!=buf[0]) {
					bos.write(buf, 1, readSize-1);
					lastRcvFlag = buf[0];
					dpk.setData(UDPUtils.successData, 0, UDPUtils.successData.length);
					dsk.send(dpk);
					dpk.setData(buf,0, buf.length);
					System.out.println("receive count of "+ ( ++readCount ) +" !");
				}
				else {
					System.out.println("not a valid packet"+lastRcvFlag+" "+buf[0]);
				}
				if(++flushSize % 1000 == 0){
					flushSize = 0;
					bos.flush();
				}


				dsk.receive(dpk);
			}
			
			// last flush 
			bos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(bos != null)
					bos.close();
				if(dsk != null)
					dsk.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
	}
}

