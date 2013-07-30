import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class TankServer {
	public static final int port = 8877;
	public static final int UDP_PORT = 6666;
	public static int ID = 0;
	List<Client> clients = new ArrayList<Client>();

	public void start() {
		ServerSocket ss = null;
		new Thread(new UdpServer()).start();

		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (true) {
			Socket connect_client = null;
			try {
				connect_client = ss.accept();
				DataInputStream dis = new DataInputStream(
						connect_client.getInputStream());
				int udpPort = dis.readInt();
				String ip = connect_client.getInetAddress().getHostAddress();
				Client c = new Client(ip, udpPort);
				clients.add(c);
				DataOutputStream dos = new DataOutputStream(
						connect_client.getOutputStream());
				dos.writeInt(ID++);
				connect_client.close();
				System.out.println("a client connect addr"
						+ connect_client.getInetAddress() + " port"
						+ connect_client.getPort() + "  udp port" + udpPort);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	class UdpServer implements Runnable {

		byte buf[] = new byte[1024];

		public void run() {
			DatagramSocket ds = null;
			try {
				ds = new DatagramSocket(UDP_PORT);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			System.out.println("udp thread start at port " + UDP_PORT);
			while (ds != null) {
				DatagramPacket dp = new DatagramPacket(buf, buf.length);
				try {
					ds.receive(dp);
					System.out.println("a packet received!");

					for (int i = 0; i < clients.size(); i++) {
						Client c = clients.get(i);
						dp.setSocketAddress(new InetSocketAddress(c.IP,
								c.UDPPort));
						ds.send(dp);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String args[]) {

		new TankServer().start();
	}

}

class Client {
	String IP;
	int UDPPort;

	public Client(String ip, int udpPort) {
		this.IP = ip;
		this.UDPPort = udpPort;
	}

}
