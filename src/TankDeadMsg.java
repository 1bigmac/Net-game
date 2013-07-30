import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class TankDeadMsg implements Msg {
	int type=Msg.TANKDEAD_MSG;
	int id;
	TankClient tc;
	public TankDeadMsg(int id){
		this.id=id;
	}
	public TankDeadMsg(TankClient tc){
		this.tc=tc;
	}
	@Override
	public void send(DatagramSocket ds, String ip, int udpPort) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.writeInt(type);
			dos.writeInt(id);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] buf=baos.toByteArray();
		try {
			DatagramPacket dp=new DatagramPacket(buf,buf.length,new InetSocketAddress(ip,udpPort));
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void read(DataInputStream dis) {
		try {
			int id =dis.readInt();
			if(tc.myTank.getId()==id){
				return;
			}
			for(int i=0;i<tc.tanks.size();i++){
				Tank t=tc.tanks.get(i);
				if(t.getId()==id){
					t.setLive(false);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
