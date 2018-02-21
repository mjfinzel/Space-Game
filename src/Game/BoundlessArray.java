package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class BoundlessArray {
	Item[][] array;
	int w, h;
	public BoundlessArray(int w, int h) {
		this.w=w;
		this.h=h;
		array = new Item[w][h];
	}
	public Item get(int x, int y) {
		while(x>=w) {
			x-=w;
		}
		while(y>=h) {
			y-=h;
		}
		while(x<0) {
			x=x+w;
		}
		while(y<0) {
			y=y+h;
		}
		return array[x][y];
	}
	public void set(int x, int y, Item value) {
		while(x>=w) {
			x-=w;
		}
		while(y>=h) {
			y-=h;
		}
		while(x<0) {
			x=x+w;
		}
		while(y<0) {
			y=y+h;
		}

		Chunk chunk = GamePanel.chunks[x/8][y/8];
		
		if(value!=null) {//add block
			chunk.lighting.clearRect((x%8), (y%8), 1, 1);
			
			//if(array[x][y]!=null) {
			if(array[x][y]!=null&&array[x][y].ID==GamePanel.beacon) {
				chunk.beacons.remove(array[x][y]);
			}
			if(array[x][y]!=null&&array[x][y].ID==GamePanel.sentry) {
				chunk.sentries.remove(array[x][y]);
			}
			
			if(value.ID==GamePanel.sentry) {
				chunk.sentries.add(value);
			}
			else if(value.ID==GamePanel.beacon) {
				chunk.beacons.add(value);
			}
			//}
			if(array[x][y]==null) {
				chunk.nullCount--;
			}
		}
		else {//remove block
			chunk.lighting.setColor(Color.white);
			chunk.lighting.fillRect((x%8), (y%8), 1, 1);
			if(array[x][y]!=null) {
				if(array[x][y].ID==GamePanel.sentry) {
					chunk.sentries.remove(array[x][y]);
				}
				else if(array[x][y].ID==GamePanel.beacon) {
					chunk.beacons.remove(array[x][y]);
				}
				chunk.nullCount++;
			}
		}
		chunk.graphics.clearRect((x%8)*8, (y%8)*8, 8, 8);
		array[x][y]=value;
		if(array[x][y]!=null) {
			array[x][y].updateLayout();
		}
	}
	public void fill(int x, int y, int w, int h, int type) {
		for(int i = x; i<=x+w; i++) {
			System.out.println("Filling ("+(double)((double)(i-x)/(double)w)*100.0+"%)");
			for(int j = y; j<=y+h;j++) {
				if(type==GamePanel.citadel_hull&&GamePanel.randomNumber(1, 4)==1) {
						GamePanel.blocks.set(i, j, new Item(GamePanel.hull,-1,-1, i, j,1, null));
				}
				else
					GamePanel.blocks.set(i, j, new Item(type,-1,-1, i, j,1, null));
			}
		}
	}
}
