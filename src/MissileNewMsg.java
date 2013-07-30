import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class MissileNewMsg implements Msg {
	int type=Msg.MISSILE_NEW_MES;
	TankClient tc;
	Missile m;
	public MissileNewMsg(Missile m){
		this.m=m;
	}
	public MissileNewMsg(TankClient tc){
		this.tc=tc;
	}
	@Override
	public void send(DatagramSocket ds, String ip, int udpPort) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.writeInt(type);
			dos.writeInt(m.tankid);
			dos.writeInt(m.missileid);
			dos.writeInt(m.x);
			dos.writeInt(m.y);
			dos.writeInt(m.dir.ordinal());
			dos.writeBoolean(m.isGood());
			
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
			int id=dis.readInt();
			int missileid=dis.readInt();
			if(id==tc.myTank.getId()) return;
			int x=dis.readInt();
			int y=dis.readInt();
			Dir dir=Dir.values()[dis.readInt()];
			boolean good=dis.readBoolean();
			Missile m=new Missile(id,x,y,good,dir,tc);
			m.missileid=missileid;
			tc.missiles.add(m);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
