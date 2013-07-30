import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
public class NetClient {
	TankClient tc;
	
	private int udpPort;
	String serverIp;
	
	DatagramSocket ds=null;
	NetClient(TankClient tc){
		this.tc=tc;

	}
	public void connnect(String ip,int port){
		this.serverIp=ip;
		try {
			ds=new DatagramSocket(udpPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		try {
			Socket client=new Socket(ip,port);
			
			DataOutputStream dos=new DataOutputStream(client.getOutputStream());
			dos.writeInt(udpPort);
			dos.flush();
			DataInputStream dis=new DataInputStream(client.getInputStream());
			int id=dis.readInt();
			tc.myTank.setId(id);
			
			if(id%2==0) tc.myTank.good=false;
			else tc.myTank.good=true;
			
			System.out.println("connected to server and server give me a Id"+id);
			
			client.close();
			dos.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		TankNewMess mess=new TankNewMess(tc.myTank);
		send(mess);
		
		new Thread(new UdpRecvServer()).start();
		
//		finally{
//			if(client!=null){
//				try {
//					client.close();
//					dos.close();
//					client=null;
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}
	public int getUdpPort() {
		return udpPort;
	}
	public void setUdpPort(int udpPort) {
		this.udpPort = udpPort;
	}
	public void send(Msg mess){
		mess.send(ds, serverIp, TankServer.UDP_PORT);
	}
	private class UdpRecvServer implements Runnable{

		byte buf[]=new byte[1024];
		public void run() {
			while(ds!=null){
				DatagramPacket dp=new DatagramPacket(buf,buf.length);
				try {
					ds.receive(dp);
					read(dp);
					System.out.println("a packet received from server");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		private void read(DatagramPacket dp){
			ByteArrayInputStream bais =new ByteArrayInputStream(buf);
			DataInputStream dis=new DataInputStream(bais);
			int type = 0;
			try {
				type = dis.readInt();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Msg msg=null;
			
			switch (type){
			case Msg.TANKDEAD_MSG:
				msg=new TankDeadMsg(tc);
				msg.read(dis);
				break;
			case Msg.TANK_MOVE_MES:
				msg=new TankMoveMess(tc);
				System.out.println("here is Tank Move mess");
				msg.read(dis);
				break;
			case Msg.TANK_NEW_MES:
				msg=new TankNewMess(NetClient.this.tc);
				msg.read(dis);
				break;
			case Msg.MISSILE_NEW_MES:
				msg=new MissileNewMsg(NetClient.this.tc);
				msg.read(dis);
				break;
			case Msg.MissileDead_Msg:
				msg=new MissileDeadMsg(NetClient.this.tc);
				msg.read(dis);
				break;
			}
			
		}
	}
}
