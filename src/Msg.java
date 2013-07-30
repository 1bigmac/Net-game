
import java.io.DataInputStream;
import java.net.*;
public interface Msg {//const variable and virtual funtion
	public static final int TANK_NEW_MES=1;
	public static final int TANK_MOVE_MES=2;
	public static final int MISSILE_NEW_MES=3;
	public static final int TANKDEAD_MSG=4;
	public static final int MissileDead_Msg=5;
	public void send(DatagramSocket ds,String ip,int udpPort);
	public void read(DataInputStream dis);
}
