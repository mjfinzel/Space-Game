package Game;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Chunk {
	BufferedImage img;
	BufferedImage lightMap;
	ArrayList<Point> edges = new ArrayList<Point>();
	int width = 8;
	int height = 8;
	Graphics2D graphics;
	Graphics2D lighting;
	ArrayList<Item> sentries = new ArrayList<Item>();
	ArrayList<Item> beacons = new ArrayList<Item>();
	int nullCount = 64;
	public Chunk(int w, int h) {
		width = w;
		height = h;
		img = new BufferedImage(width*8, height*8, BufferedImage.TYPE_INT_ARGB);
		lightMap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		graphics = (Graphics2D)img.getGraphics();
		graphics.setBackground(new Color(0,0,0,0));
		lighting = (Graphics2D) lightMap.getGraphics();
		lighting.setBackground(new Color(0,0,0,0));
	}
	public void update(Graphics g) {
		//update sentries
		for(int i = 0; i<sentries.size();i++) {
			Item current = sentries.get(i);
			int x = current.x%8;
			int y = current.y%8;
			double distanceToPlayer = Math.sqrt(Math.pow(((double)current.x*8-GamePanel.player.xpos),2)+Math.pow(((double)current.y*8-GamePanel.player.ypos),2));
			graphics.clearRect((x)*8, (y)*8, 8, 8);
			//if the player is out of range
			if(!(distanceToPlayer<500||(current.alert&&distanceToPlayer<800))) {
				current.updates=0;
				graphics.drawImage(GamePanel.parts[3][2], (x)*8, (y)*8, null);
			}
			else {//if the player is in range
				current.alert=true;
				current.updates++;
				if(current.updates<=60&&current.updates%10==0) {
					double angle = Math.atan2(((double)GamePanel.player.ypos-current.y*8),(((double)GamePanel.player.xpos-current.x*8)));
					GamePanel.projectiles.add(new Projectile(1, current.x*8, current.y*8, angle,3000,1));
				}
				else if(current.updates>=200) {
					current.updates=0;
				}

				graphics.drawImage(GamePanel.parts[3][1], (x)*8, (y)*8, null);

			}
		}

		//update beacons
		for(int i = 0; i<beacons.size();i++) {
			Item current = beacons.get(i);
			int x = current.x%8;
			int y = current.y%8;
			graphics.drawImage(GamePanel.animatedTiles[current.updates/5][0], (x)*8, (y)*8, null);
			if(current.updates<35) {
				current.updates++;
			}
			else {
				current.updates=0;
			}
			GamePanel.beacons.add(new Point(x,y));
		}
		
	}
}
