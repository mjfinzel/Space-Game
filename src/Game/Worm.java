package Game;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class Worm {
	int xpos, ypos;
	ArrayList<Worm> segments;
	public Worm(int x, int y, ArrayList<Worm> s) {
		segments = s;
	}
	public void move() {
		if(segments.get(0)==this) {
			for(int i = 1; i<segments.size();i++) {
				segments.get(i).xpos=segments.get(i-1).xpos;
				segments.get(i).ypos=segments.get(i-1).ypos;
			}
		}
	}
	public void Draw(Graphics2D g) {
		for(int i = 0; i<segments.size();i++) {
			
		}
	}
}
