import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class MissileDeadMsg implements Msg {
	int type=Msg.MissileDead_Msg;
	int missileid;
	int tankid;
	TankClient tc;
	public MissileDeadMsg(int tankid,int missile){
		this.missileid=missile;
		this.tankid=tankid;
	}
	public MissileDeadMsg(TankClient tc){
		this.tc=tc;
	}
	@Override
	public void send(DatagramSocket ds, String ip, int udpPort) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.writeInt(type);
			dos.writeInt(tankid);
			dos.writeInt(missileid);
			
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
			int tankid =dis.readInt();
//			if(tc.myTank.getId()==tankid){
//				return;
//			}
			int id=dis.readInt();
			for(int i=0;i<tc.missiles.size();i++){
				Missile m=tc.missiles.get(i);
				if(m.tankid==tankid && m.missileid==id){
					tc.explodes.add(new Explode(m.x,m.y,tc));
					m.live=false;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
