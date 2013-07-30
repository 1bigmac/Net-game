import java.awt.*;
//import java.awt.Dialog;
//import java.awt.FlowLayout;
//import java.awt.Frame;
//import java.awt.Graphics;
//import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class TankClient extends Frame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;

	Tank myTank = new Tank(50, 50, true, Dir.STOP, this);

	List<Missile> missiles = new ArrayList<Missile>();
	List<Explode> explodes = new ArrayList<Explode>();
	List<Tank> tanks = new ArrayList<Tank>();

	Image offScreenImage = null;

	NetClient nc = new NetClient(this);
	connectDialog dialog = new connectDialog();

	@Override
	public void paint(Graphics g) {
		g.drawString("missiles count:" + missiles.size(), 10, 50);
		g.drawString("explodes count:" + explodes.size(), 10, 70);
		g.drawString("tanks    count:" + tanks.size(), 10, 90);

		for (int i = 0; i < missiles.size(); i++) {
			Missile m = missiles.get(i);
//			m.hitTanks(myTank);
			if(m.hitTank(myTank)){
				TankDeadMsg msg1=new TankDeadMsg(myTank.getId());
				nc.send(msg1);

				MissileDeadMsg msg2=new MissileDeadMsg(m.tankid,m.missileid);
 				nc.send(msg2);
			}
			m.draw(g);
		}

		for (int i = 0; i < explodes.size(); i++) {
			Explode e = explodes.get(i);
			e.draw(g);
		}

		for (int i = 0; i < tanks.size(); i++) {
			Tank t = tanks.get(i);
			t.draw(g);
		}

		myTank.draw(g);

	}

	@Override
	public void update(Graphics g) {
		if (offScreenImage == null) {
			offScreenImage = this.createImage(800, 600);
		}
		Graphics gOffScreen = offScreenImage.getGraphics();
		Color c = gOffScreen.getColor();
		gOffScreen.setColor(Color.GREEN);
		gOffScreen.fillRect(0, 0, GAME_WIDTH, GAME_HEIGHT);
		gOffScreen.setColor(c);
		paint(gOffScreen);
		g.drawImage(offScreenImage, 0, 0, null);
	}

	public void launchFrame() {

		this.setLocation(400, 300);
		this.setSize(GAME_WIDTH, GAME_HEIGHT);
		this.setTitle("TankWar");
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

		});
		this.setResizable(false);
		this.setBackground(Color.GREEN);

		this.addKeyListener(new KeyMonitor());

		this.setVisible(true);

		new Thread(new PaintThread()).start();

//		nc.connnect("127.0.0.2", TankServer.port);
	}

	public static void main(String[] args) {
		TankClient tc = new TankClient();
		tc.launchFrame();
	}

	class PaintThread implements Runnable {

		public void run() {
			while (true) {
				repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class KeyMonitor extends KeyAdapter {

		@Override
		public void keyReleased(KeyEvent e) {
			myTank.keyReleased(e);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_C) {
				// dialog.show();
				dialog.setVisible(true);
			} else {
				myTank.keyPressed(e);
			}
		}

	}

	class connectDialog extends Dialog {
		Button sure=new Button("sure");
		Label label_ip=new Label("ip :");
		TextField host_ip=new TextField("127.0.0.1",12); 
		Label label_server_port=new Label("port :");
		TextField server_port=new TextField(""+TankServer.port);
		Label label_udp_port =new Label("My udp port");
		TextField udp_port=new TextField("2223",4);
		
		connectDialog() {
			super(TankClient.this, true);
			
			this.setLayout(new FlowLayout());
			this.add(label_ip);
			this.add(host_ip);
			this.add(label_server_port);
			this.add(server_port);
			this.add(label_udp_port);
			this.add(udp_port);
			this.setLocation(300, 300);
			this.add(sure);
			sure.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					String ip=host_ip.getText().trim();
					int port=Integer.parseInt(server_port.getText().trim()); 
					int myudpport=Integer.parseInt(udp_port.getText().trim());
					setVisible(false);
					nc.setUdpPort(myudpport);
					nc.connnect(ip, port);
				}
				
			});
			this.pack();
			this.addWindowListener(new WindowAdapter(){

				@Override
				public void windowClosing(WindowEvent e) {
					setVisible(false);
//					connectDialog.this.setVisible(false);
				}
				
			});
		}

	}
}
