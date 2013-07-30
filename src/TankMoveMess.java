import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class TankMoveMess implements Msg{
	int msgType=Msg.TANK_MOVE_MES;
	int id;
	Dir dir;
	Dir ptDir;
	int x,y;
	TankClient tc;
	public TankMoveMess(int id,int x,int y,Dir dir,Dir pt) {
		this.id = id;
		this.dir = dir;
		this.x=x;
		this.y=y;
		this.ptDir=pt;
	}
	public TankMoveMess(TankClient tc) {
		this.tc=tc;
	}
	@Override
	public void send(DatagramSocket ds, String ip, int udpPort) {
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			System.out.println("Tank Move and send to client server");
			dos.writeInt(msgType);
			dos.writeInt(id);
			dos.writeInt(x);
			dos.writeInt(y);
			dos.writeInt(dir.ordinal());
			dos.writeInt(ptDir.ordinal());
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
			System.out.println("Tank move message get it and deal with it");
			int id =dis.readInt();
			int x=dis.readInt();
			int y=dis.readInt();
			
			if(tc.myTank.getId()==id){
				return;
			}
			Dir dir=Dir.values()[dis.readInt()];
			Dir ptdir=Dir.values()[dis.readInt()];
			for(int i=0;i<tc.tanks.size();i++){
				Tank t=tc.tanks.get(i);
				if(t.getId()==id){
					t.x=x;
					t.y=y;
					t.dir=dir;
					t.ptDir=ptdir;
					break;
				}
			}
//			if(!exist){// add new Tank
//				
//			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
