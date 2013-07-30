import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;


public class TankNewMess implements Msg {
	int msgType=Msg.TANK_NEW_MES;
	Tank t;
	TankClient tc;
	public TankNewMess(TankClient tc){
		this.tc=tc;
	}
	public TankNewMess(Tank t){
		this.t=t;
	}
	public void send(DatagramSocket ds,String ip,int udpPort){
		ByteArrayOutputStream baos =new ByteArrayOutputStream();
		DataOutputStream dos=new DataOutputStream(baos);
		try {
			dos.writeInt(msgType);
			dos.writeInt(t.getId());
		
			dos.writeInt(t.x);
			dos.writeInt(t.y);
			dos.writeInt(t.dir.ordinal());
			dos.writeBoolean(t.good);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte buf[]=baos.toByteArray();
		try {
			DatagramPacket dp=new DatagramPacket(buf,buf.length,new InetSocketAddress(ip,udpPort) );
			ds.send(dp);
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}
	public void read(DataInputStream dis) {
		try {
			int id=dis.readInt();
			if(tc.myTank.getId()==id){
				return ;
			}
			
			int x=dis.readInt();
			int y=dis.readInt();
			Dir dir=Dir.values()[dis.readInt()];
			boolean good=dis.readBoolean();
//			System.out.println("id :"+id+" x :"+x+" y "+y+" dir "+dir+" good "+good);
			
			boolean exist=false;
			for(int i=0;i<tc.tanks.size();i++){
				Tank t=tc.tanks.get(i);
				if(t.getId()==id){
					exist=true;
					break;
				}
			}
			if(!exist){
				TankNewMess tnmsg=new TankNewMess(tc.myTank);
				tc.nc.send(tnmsg);
				
				Tank t=new Tank(x,y,good,dir,tc);
				t.setId(id);
				tc.tanks.add(t);
			}
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
	}
}
