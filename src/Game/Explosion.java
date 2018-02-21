package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Explosion {
	int xpos;
	int ypos;
	int radius = 0;
	int faction;
	double maxRange;
	int startX;
	int startY;
	int range;
	int updates = 0;
	ArrayList <Point> newLocations = new ArrayList<Point>();
	ArrayList<Integer> newRanges = new ArrayList<Integer>();
	//ArrayList<Point> alreadyHit;
	boolean[][] alreadyHit;
	//int[][] ranges;
	public Explosion(double x, double y, int id, double range) {
		xpos = (int)x;
		ypos = (int)y;
		startX = (int)x;
		startY = (int)y;
		this.range=(int)range;
		faction=id;
		maxRange=range;
		alreadyHit = new boolean[((int)range*2)+1][((int)range*2)+1];//new ArrayList<Point>();
		//ranges = new int[((int)range*2)+1][((int)range*2)+1];
		ArrayList<Point> start = new ArrayList<Point>();
		start.add(new Point((int)x,(int)y));
		ArrayList<Integer> ranges = new ArrayList<Integer>();
		ranges.add((int)range);
		dealDamage(start, ranges);
		//System.out.println(xpos+","+ypos);
	}
	public void dealDamage(ArrayList<Point> locations, ArrayList<Integer> ranges) {
		newLocations.clear();
		newRanges.clear();

		for(int i=0; i<locations.size();i++) {
			Point currentLocation = locations.get(i);
			double distanceModifier = ((GamePanel.distanceBetween(startX, startY, currentLocation.x, currentLocation.y)/maxRange));
			int currentRange = ranges.get(i);
			if(ranges.get(i)>0&&!alreadyHit(currentLocation.x, currentLocation.y)) {

				//GamePanel.blocks.get(currentLocation.x, currentLocation.y).hp-=GamePanel.blocks.get(currentLocation.x, currentLocation.y).hardness;
				if(GamePanel.blocks.get(currentLocation.x, currentLocation.y)!=null) {
					GamePanel.blocks.get(currentLocation.x, currentLocation.y).takeDamageAndCheckIfDead(80*((currentRange/maxRange)), faction);
					if(GamePanel.blocks.get(currentLocation.x, currentLocation.y)!=null&&GamePanel.blocks.get(currentLocation.x, currentLocation.y).hp<=0)
						GamePanel.blocks.set(currentLocation.x, currentLocation.y, null);
				}
				hit(currentLocation.x, currentLocation.y);

				//left
				Item left = GamePanel.blocks.get(currentLocation.x-1, currentLocation.y);
				if(!alreadyHit(currentLocation.x-1, currentLocation.y)) {

					double hardness = 1;
					if(left!=null) hardness=left.hardness;
					newLocations.add(new Point(currentLocation.x-1, currentLocation.y));
					newRanges.add(currentRange-(int)(hardness*distanceModifier));
				}
				//right
				Item right = GamePanel.blocks.get(currentLocation.x+1, currentLocation.y);
				if(!alreadyHit(currentLocation.x+1, currentLocation.y)) {
					double hardness = 1;
					if(right!=null) hardness=right.hardness;
					newLocations.add(new Point(currentLocation.x+1, currentLocation.y));
					newRanges.add(currentRange-(int)(hardness*distanceModifier));
				}
				//top
				Item top = GamePanel.blocks.get(currentLocation.x, currentLocation.y-1);
				if(!alreadyHit(currentLocation.x, currentLocation.y-1)) {
					double hardness = 1;
					if(top!=null) hardness=top.hardness;
					newLocations.add(new Point(currentLocation.x, currentLocation.y-1));
					newRanges.add(currentRange-(int)(hardness*distanceModifier));
				}
				//bottom
				Item bottom = GamePanel.blocks.get(currentLocation.x, currentLocation.y+1);
				if(!alreadyHit(currentLocation.x, currentLocation.y+1)) {
					double hardness = 1;
					if(bottom!=null) hardness=bottom.hardness;
					newLocations.add(new Point(currentLocation.x, currentLocation.y+1));
					newRanges.add(currentRange-(int)(hardness*distanceModifier));
				}
			}
		}
		//		if(newLocations.size()>0)
		//			dealDamage(newLocations,newRanges);
	}

	public void hit(int x, int y) {
		alreadyHit[(x-startX)+range][(y-startY)+range] = true;
	}
	public boolean alreadyHit(int x, int y) {
		int x1 = (x-startX)+range;
		int y1 = (y-startY)+range;
		if(x1>=0&&y1>=0&&x1<alreadyHit.length&&y1<alreadyHit.length)
			return alreadyHit[x1][y1];
		return true;
	}
	public void Draw(Graphics2D g) {
		for(int i=0; i<newLocations.size();i++) {
			Point currentLocation = newLocations.get(i);
			if(GamePanel.blocks.get(currentLocation.x, currentLocation.y)!=null) {
				g.setColor(new Color(0,255,0,20));
				g.fillRect(((currentLocation.x*8)-(int)GamePanel.player.xpos)+(AppletUI.windowWidth/2), ((currentLocation.y*8)-(int)GamePanel.player.ypos)+(AppletUI.windowHeight/2), 8, 8);
				GamePanel.addLight(currentLocation.x, currentLocation.y, 13, 13,false);
			}

		}
		
		dealDamage(new ArrayList<Point>(newLocations),new ArrayList<Integer>(newRanges));
		
		updates++;

	}

}
