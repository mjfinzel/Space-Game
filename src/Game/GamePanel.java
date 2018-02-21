package Game;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JPanel;

import com.sun.glass.events.KeyEvent;

public class GamePanel extends JPanel{

	private static final long serialVersionUID = 7734877696044080629L;

	public static int swarmDisplacement = 1;

	public static double cameraScale = 1.0;
	public static BufferedImage[][] parts = Images.cut("/Textures/parts_colorful.png", 8, 8);
	public static BufferedImage[][] tiles = Images.cut("/Textures/tiles.png", 9, 9);
	public static BufferedImage[][] animatedTiles = Images.cut("/Textures/AnimatedTiles.png", 8, 8);
	//public static BufferedImage[][] tiles = Images.cut("/Textures/tiles.png", 8, 8);
	public static BufferedImage[][] projectileImages = Images.cut("/Textures/Projectiles.png", 2, 2);
	public static BufferedImage background = Images.load("/Textures/background.png");
	public static BufferedImage[][] beamImage = Images.cut("/Textures/Beam.png",800,7);
	public static BufferedImage light = Images.load("/Textures/light.png");
	public static BufferedImage dark = Images.load("/Textures/dark.png");

	static int worldWidth = 2000;
	static int worldHeight = 2000;
	//int[][] blocks = new int[worldWidth][worldHeight];
	static BoundlessArray blocks = new BoundlessArray(worldWidth, worldHeight);
	static Chunk[][] chunks = new Chunk[worldWidth/8][worldHeight/8];

	//ship
	static final int shipCore=1;
	static final int cockpit=2;
	static final int engine=3;
	static final int laser=4;
	static final int fuel=5;
	static final int hull=6;
	static final int cargo=7;
	static final int acceleration_module = 8;
	static final int solar_panel = 9;
	static final int repair_module = 10;
	static final int generator = 11;
	static final int battery = 12;
	static final int beam = 13;
	static final int replacer_module = 14;

	//world
	static final int air=-1;
	static final int astroid=20;
	static final int rock=21;
	static final int ice=22;
	static final int sentry=23;
	static final int uranium=24;
	static final int iron=25;
	static final int lead=26;
	static final int beacon=27;
	static final int citadel_hull=28;

	public static Ship player = new Ship(0, (worldWidth/2)*8, (worldHeight/2)*8);
	public static ArrayList<Ship> ships = new ArrayList<Ship>();
	public static ArrayList<Projectile> projectiles = new ArrayList<Projectile>();
	public static ArrayList<Explosion> explosions = new ArrayList<Explosion>();
	public static ArrayList<CombatText> combatText = new ArrayList<CombatText>();
	public static ArrayList<LootText> lootText = new ArrayList<LootText>();

	//public static boolean showExperimental = true;
	public static boolean buildMode = true;
	public static boolean godMode = false;
	public static Item cursorItem = null;
	public static int premade = 1;
	static ArrayList<Point> possibleSpawns = new ArrayList<Point>();
	static ArrayList<Point> beacons = new ArrayList<Point>();

	static BufferedImage lightMask = new BufferedImage((AppletUI.windowWidth/8)+1, (AppletUI.windowHeight/8)+1, BufferedImage.TYPE_INT_ARGB);
	static Graphics2D lmg;
	static int danger = 0;

	public GamePanel(){
		this.setDoubleBuffered(true);
		for(int i = 0; i<worldWidth/8;i++) {
			for(int j = 0; j<worldHeight/8;j++) {
				chunks[i][j] = new Chunk(8,8);
			}
		}
		initializeMap();
		lmg = (Graphics2D)lightMask.getGraphics();
		//ships.add(new Ship(1, ((worldWidth/2)+170)*8, ((worldHeight/2)+7)*8));
		//ships.add(new Ship(2, ((worldWidth/2)-150)*8, ((worldHeight/2)+7)*8));
	}
	public void initializeMap() {
		for(int i = 0; i<worldWidth; i++) {
			for(int j = 0; j<worldHeight; j++) {
				//by default a tile is empty
				//blocks[i][j]=air;
				blocks.set(i, j, null);

				//if this isn't near the spawn location
				if(locationIsNearSpawn(i,j)==false) {
					//add seed rocks to the map
					if(randomNumber(1,5000)==1) {
						//blocks[i][j]=rock;
						blocks.set(i, j, new Item(astroid,-1,-1, i, j, 1, null));
					}
				}
			}
		}
		for(int i = 0; i<worldWidth; i++) {
			for(int j = 0; j<worldHeight; j++) {
				if(blocks.get(i, j)!=null&&blocks.get(i, j).ID==astroid) {
					blocks.set(i, j, new Item(rock,-1,-1, i, j, 1, null));
					ArrayList<Point> rocks = new ArrayList<Point>();
					rocks.add(new Point(i,j));
					int small=0;
					int medium = 1;
					int large = 2;
					int giant = 3;
					int type=rock;
					int typeRand = randomNumber(1,100);
					if(typeRand<=10) {
						type=ice;
					}

					int rand = randomNumber(0,3);
					if(rand==small)
						enlargeRock(rocks, randomNumber(50,100),type, false);
					else if(rand==medium)
						enlargeRock(rocks, randomNumber(200,300),type, false);
					else if(rand==large)
						enlargeRock(rocks, randomNumber(700,1000),rock, false);
					else if(rand==giant)
						enlargeRock(rocks, randomNumber(5000,8000),rock, false);

				}
			}
		}
		for(int i = 0; i<worldWidth; i++) {
			for(int j = 0; j<worldHeight; j++) {
				if(blocks.get(i, j)!=null&&getAdjacentVoidTiles(new Point(i, j), false).size()!=0) {
					blocks.get(i, j).isEdge=true;
				}
				if(blocks.get(i, j)!=null&&blocks.get(i, j).ID==rock&&blocks.get(i, j-1)==null&&blocks.get(i, j-2)==null&&blocks.get(i, j-3)==null) {
					if(randomNumber(1,80)<=1) {
						blocks.set(i, j-3, new Item(sentry,-1,-1, i, j-3,1, null));
						if(randomNumber(1,4)==1)
							blocks.set(i, j-2, new Item(beacon,-1,-1, i, j-2,1, null));
						else
							blocks.set(i, j-2, new Item(hull,-1,-1, i, j-2,1, null));
						blocks.set(i, j-1, new Item(hull,-1,-1, i, j-1,1, null));
						blocks.set(i, j, new Item(hull,-1,-1, i, j,1, null));
						blocks.set(i-1, j, new Item(hull,-1,-1, i-1, j,1, null));
						blocks.set(i+1, j, new Item(hull,-1,-1, i+1, j,1, null));

					}
				}
			}
		}
		for(int i = (int)(player.xpos/8)-50;i<=(int)(player.xpos/8)+50;i++) {
			for(int j = (int)(player.ypos/8)-50;j<=(int)(player.ypos/8)+50;j++) {


				//if(blocks.get(i, j)!=null)
				if(randomNumber(1,20)>12) {
					blocks.set(i, j, new Item(hull,-1,-1, i, j,1, null));
					blocks.get(i,j).hp=randomNumber(10,(int)blocks.get(i, j).maxHP);
				}
				if(i>(int)(player.xpos/8)-48&&i<(int)(player.xpos/8)+48) {
					if(j>(int)(player.ypos/8)-48&&j<(int)(player.ypos/8)+48) {
						blocks.set(i, j, null);
					}
				}
			}
		}
		generateCitadel();
		for(int i = 0; i<worldWidth; i++) {
			for(int j = 0; j<worldHeight; j++) {
				if(blocks.get(i, j)!=null)
					blocks.get(i, j).updateLayout();
			}
		}
		
	}
	public void generateCitadel() {
		System.out.println("Generating citadel");
		int xpos = (worldWidth/2)+60;
		int ypos = worldHeight/2;
		blocks.fill(xpos,ypos,100,100, citadel_hull);
	}
	public static void addLootMessage(Item item) {
		if(lootText!=null) {
			for(int i = 0; i<lootText.size();i++) {
				LootText current = lootText.get(i);
				if(current.item.ID==item.ID) {
					current.amount+=item.amount;
					current.appearTime=System.currentTimeMillis();
					lootText.remove(current);
					lootText.add(current);
					return;
				}
			}
			lootText.add(new LootText(item));
		}
	}
	public void enlargeRock(ArrayList<Point> rocks, int amount, int type, boolean overwrite) {
		if(amount>0&&rocks.size()>0) {
			int randomEdge = randomNumber(0, rocks.size()-1);
			int tries = 0;
			while(rocks.size()>0&&getAdjacentVoidTiles(rocks.get(randomEdge),overwrite).size()==0&&tries<1000) {
				//rocks.remove(randomEdge);
				tries++;
				randomEdge = randomNumber(0, rocks.size()-1);
			}
			if(rocks.size()>0&&tries<1000) {
				ArrayList<Point> edges = getAdjacentVoidTiles(rocks.get(randomEdge),overwrite);

				Point rand = edges.get(randomNumber(0,edges.size()-1));
				if(randomNumber(1,200)==1) {
					blocks.set(rand.x, rand.y, new Item(ice,-1,-1, rand.x, rand.y,1, null));
				}
				else {
					blocks.set(rand.x, rand.y, new Item(type,-1,-1, rand.x, rand.y,1, null));
				}
				rocks.add(new Point(rand.x, rand.y));

				enlargeRock(rocks,amount-1, type, overwrite);
			}
		}
		if(amount==0&&!overwrite){
			if((randomNumber(2,3)<=2&&type==ice)||(type==rock&&randomNumber(1,30)==1)) {
				for(int i = 0; i<rocks.size();i++) {
					if(getAdjacentVoidTiles(rocks.get(i), overwrite).size()>0) {
						if(randomNumber(1,10)==1) {
							if(randomNumber(1,5)==1)
								blocks.set(rocks.get(i).x, rocks.get(i).y, new Item(beacon,-1,-1, rocks.get(i).x, rocks.get(i).y,1, null));
							else
								blocks.set(rocks.get(i).x, rocks.get(i).y, new Item(sentry,-1,-1, rocks.get(i).x, rocks.get(i).y,1, null));
						}
						else {
							blocks.set(rocks.get(i).x, rocks.get(i).y, new Item(hull,-1,-1, rocks.get(i).x, rocks.get(i).y,1, null));
						}
					}
				}
			}
			if(type==rock) {
				addResourceNodes(rocks, ice, 15);
				addResourceNodes(rocks, uranium, 5);
				if(rocks.size()>4000)
					addResourceNodes(rocks, iron, 200);
				else
					addResourceNodes(rocks, iron, 80);
				addResourceNodes(rocks, lead, 20);
			}
		}
	}
	public void addResourceNodes(ArrayList<Point> rocks, int type, int amt) {
		int nodes = randomNumber(0,(rocks.size()/1000)+1);
		if(nodes>5) {
			nodes=5;
		}
		for(int i = 0; i<nodes;i++) {
			ArrayList<Point> start = new ArrayList<Point>();
			start.add(rocks.get(randomNumber(0,rocks.size()-1)));
			enlargeRock(start, randomNumber(amt-(amt/2),amt+(amt/2)), type, true);
		}
	}
	public static ArrayList<Point> getAdjacentVoidTiles(Point p, boolean overwrite){
		ArrayList<Point> result = new ArrayList<Point>();
		for(int i = p.x-1; i<=p.x+1;i++) {
			for(int j = p.y-1; j<=p.y+1; j++) {
				if(blocks.get(i, j)==null||(overwrite==true&&!(i==p.x&&j==p.y))) {
					result.add(new Point(i,j));
				}
			}
		}
		return result;
	}
	public boolean locationIsNearSpawn(int x, int y) {
		//minimum distance in terms of blocks to be considered "near"
		int limit = 20;
		//x direction
		if(x>=(worldWidth/2)-limit&&x<=(worldWidth/2)+limit) {
			//y direction
			if(y>=(worldHeight/2)-limit&&y<=(worldHeight/2)+limit) {
				return true;
			}
		}
		return false;
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Draw((Graphics2D)g);
	}
	static int randomNumber(int min, int max){
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	public void drawProjectiles(Graphics2D g) {
		for(int i =0; i<projectiles.size();i++) {
			projectiles.get(i).Draw(g);
			if(projectiles.get(i).distanceTravelled>=projectiles.get(i).range||projectiles.get(i).collided) {
				projectiles.remove(i);
				if(i>0) i--;
			}
		}
	}
	public void drawExplosions(Graphics2D g) {
		for(int i =0; i<explosions.size();i++) {

			if(explosions.get(i).newLocations.size()>0) {
				explosions.get(i).Draw(g);
			}
			else {
				explosions.remove(i);
				if(i>0) i--;
			}
		}
	}
	public static double distanceBetween(double x1, double y1, double x2, double y2) {
		return Math.sqrt(Math.pow(((double)x1-x2),2)+Math.pow(((double)y1-y2),2));
	}
	public void drawHelp(Graphics2D g) {
		if(buildMode==false) {
			int count = 1;
			if(Controller.wasPressed[KeyEvent.VK_SPACE]==false) {
				count++;
				Font font = new Font("Iwona Heavy",Font.BOLD,24);
				g.setFont(font);
				g.setColor(Color.orange);
				g.drawString("[Space] Use brakes", AppletUI.windowWidth-330, AppletUI.windowHeight-(count*30));
			}
			if(Controller.wasPressed[KeyEvent.VK_D]==false) {
				count++;
				Font font = new Font("Iwona Heavy",Font.BOLD,24);
				g.setFont(font);
				g.setColor(Color.orange);
				g.drawString("[D] Move Right", AppletUI.windowWidth-330, AppletUI.windowHeight-(count*30));
			}
			if(Controller.wasPressed[KeyEvent.VK_S]==false) {
				count++;
				Font font = new Font("Iwona Heavy",Font.BOLD,24);
				g.setFont(font);
				g.setColor(Color.orange);
				g.drawString("[S] Move Backwards", AppletUI.windowWidth-330, AppletUI.windowHeight-(count*30));
			}
			if(Controller.wasPressed[KeyEvent.VK_A]==false) {
				count++;
				Font font = new Font("Iwona Heavy",Font.BOLD,24);
				g.setFont(font);
				g.setColor(Color.orange);
				g.drawString("[A] Turn Left", AppletUI.windowWidth-330, AppletUI.windowHeight-(count*30));
			}
			if(Controller.wasPressed[KeyEvent.VK_W]==false) {
				count++;
				Font font = new Font("Iwona Heavy",Font.BOLD,24);
				g.setFont(font);
				g.setColor(Color.orange);
				g.drawString("[W] Move Forwards", AppletUI.windowWidth-330, AppletUI.windowHeight-(count*30));
			}
			//mouse
			if(Controller.lmbWasPressed==false) {
				count++;
				Font font = new Font("Iwona Heavy",Font.BOLD,24);
				g.setFont(font);
				g.setColor(Color.orange);
				g.drawString("[LMB] Fire in Direction", AppletUI.windowWidth-330, AppletUI.windowHeight-(count*30));
			}
			if(Controller.rmbWasPressed==false) {
				count++;
				Font font = new Font("Iwona Heavy",Font.BOLD,24);
				g.setFont(font);
				g.setColor(Color.orange);
				g.drawString("[RMB] Fire At Point", AppletUI.windowWidth-330, AppletUI.windowHeight-(count*30));
			}
		}
	}
	public void clearLights() {
		if(godMode) {
			//Graphics2D g = (Graphics2D)lightMask.getGraphics();
			lmg.setBackground(new Color(255,255,255,0));
			lmg.clearRect(0, 0, lightMask.getWidth(), lightMask.getHeight());		
		}
	}
	public static void addLight(double x, double y, int w, int h, boolean dark) {
		if(godMode) {
			int x1 = ((int)x-(int)(GamePanel.player.xpos))+(AppletUI.windowWidth/2);
			int y1 = ((int)y-(int)(GamePanel.player.ypos))+(AppletUI.windowHeight/2);

			if(dark==false)
				lmg.drawImage(GamePanel.light, 1+(x1/8)-(w/2), 1+(y1/8)-(h/2),w,h, null);
			else
				lmg.drawImage(GamePanel.dark, 1+(x1/8)-(w/2), 1+(y1/8)-(h/2),w,h, null);
		}
		//l.dispose();
	}
	public void applyLights(Graphics2D gr) {
		if(godMode) {
			//Graphics2D g = (Graphics2D)lightMask.getGraphics();
			int[] pixels = lightMask.getRGB(0, 0, lightMask.getWidth(), lightMask.getHeight(), null, 0, lightMask.getWidth());
			for (int i = pixels.length-1; i >= 0; i--) {
				pixels[i] = 0xFFFFFFFF-pixels[i];
			}
			lightMask.setRGB(0, 0, lightMask.getWidth(), lightMask.getHeight(), pixels, 0, lightMask.getWidth());
			//g.dispose();
			//System.out.println(lightMask.getWidth()+","+lightMask.getHeight());
			int x = -(((int)player.xpos%8));
			int y = -(((int)(player.ypos-4)%8));
			if(x==0) x=-8;
			if(y==0) y=-8;
			gr.drawImage(lightMask, x, y, AppletUI.windowWidth+8, AppletUI.windowHeight+8, null);
		}
	}
	public static void spawnEnemyShip() {
		if(possibleSpawns.size()>0) {
			Point position = possibleSpawns.get(randomNumber(0, possibleSpawns.size()-1));
			int type = randomNumber(1,1000);
			int whatShip = 4;
			if(type<=100&&player.partList.size()>=0) whatShip=4;
			else if(type<=200&&player.partList.size()>=100) whatShip=2;
			else if(type<=400&&player.partList.size()>=150) whatShip=6;
			else if(type<=650&&player.partList.size()>=230) whatShip=5;
			else if(type<=800&&player.partList.size()>=200) whatShip=1;
			else if(type<=920&&player.partList.size()>=230) whatShip=8;
			else if(type<=980&&player.partList.size()>=280) whatShip=9;
			else if(type<=1000&&player.partList.size()>=350) whatShip=3;

			ships.add(new Ship(whatShip, position.x*8, position.y*8));
		}
	}
	public Chunk getChunk(int x, int y) {
		while(x>=worldWidth/8) {
			x-=worldWidth/8;
		}
		while(y>=(worldHeight/8)) {
			y-=(worldHeight/8);
		}
		while(x<0) {
			x=x+(worldWidth/8);
		}
		while(y<0) {
			y=y+(worldHeight/8);
		}
		return chunks[x][y];
	}
	public void Draw(Graphics2D g){

		double percentFuel = (player.fuel/player.fuelLimit);
		double percentEnergy = (player.energy/player.energyLimit);
		if(buildMode==true) {
			//draw inventory
			g.setColor(new Color(90,90,90));
			int width = 764;
			int leftEdge = AppletUI.windowWidth-width-5;
			g.fillRect(leftEdge, 5, width, AppletUI.windowHeight-10);
			g.setColor(new Color(50,50,50));
			g.fillRect(5, AppletUI.windowHeight-180, leftEdge-5, 175);

			Font font = new Font("Iwona Heavy",Font.BOLD,22);
			g.setFont(font);
			g.setColor(new Color(140,30,10));
			g.drawRect(5, AppletUI.windowHeight-180, leftEdge-6, 174);
			g.drawRect(leftEdge, 5, width-1, AppletUI.windowHeight-11);
			g.setColor(new Color(100,35,15));
			g.drawString("Available Parts", leftEdge+3, 23);
			g.drawLine(leftEdge, 25, AppletUI.windowWidth-6, 25);

			//draw items
			font = new Font("Iwona Heavy",Font.BOLD,12);
			g.setFont(font);
			FontMetrics m = g.getFontMetrics();
			int w = 21;
			for(int i = 0; i<378;i++) {
				g.setColor(new Color(140,140,140));
				g.fillRect(leftEdge+3+((i%w)*36), 30+((i/w)*48), 34, 46);

				g.setColor(new Color(160,160,160));
				g.fillRect(leftEdge+3+((i%w)*36), 30+34+((i/w)*48), 34, 12);
				if(i>=player.inventoryLimit)
					g.setColor(new Color(255,0,0,10));
				else
					g.setColor(new Color(0,255,0,10));
				g.fillRect(leftEdge+3+((i%w)*36), 30+((i/w)*48), 34, 46);
				if(i<player.inventory.size()&&player.inventory.get(i)!=null) {
					g.setColor(Rarity.rarityColor[player.inventory.get(i).rarity]);
					g.drawRect(leftEdge+3+((i%w)*36), 30+((i/w)*48), 33, 33);
					g.setColor(Color.black);
					g.drawImage(parts[player.inventory.get(i).icon.x][player.inventory.get(i).icon.y], leftEdge+4+((i%w)*36), 31+((i/w)*48), 32, 32, null);
					String as = player.inventory.get(i).amount+"";
					g.drawString(as,leftEdge+4+32-m.stringWidth(as)+((i%w)*36), 31+44+((i/w)*48));
				}

			}

			int xOffset = 0;//(AppletUI.windowWidth/2) - (player.ship.getWidth());
			int yOffset = 0;//(AppletUI.windowHeight/2) - (player.ship.getHeight());
			g.drawImage(player.ship, xOffset,yOffset, player.ship.getWidth()*2, player.ship.getHeight()*2,null);
			boolean found = false;
			//draw player's ship grid
			for(int i = 0; i<player.maxWidth; i++) {
				for(int j = 0; j<player.maxHeight;j++) {
					if(player.parts[i][j]==null) {
						//if the position is usable
						if((i>0&&player.parts[i-1][j]!=null)||(i<player.maxWidth-1&&player.parts[i+1][j]!=null)||(j>0&&player.parts[i][j-1]!=null)||(j<player.maxWidth-1&&player.parts[i][j+1]!=null)) 	
							g.setColor(new Color(0,60,0));
						else
							g.setColor(new Color(40,0,0));

						g.drawRect(xOffset+(i*16), yOffset+(j*16), 15, 15);
					}
					if(cursorItem!=null) {
						if(Controller.mousePosition.x>=xOffset+(i*16)&&Controller.mousePosition.x<xOffset+(i*16)+16){
							if(Controller.mousePosition.y>=yOffset+(j*16)&&Controller.mousePosition.y<yOffset+(j*16)+16){
								found=true;
								g.drawImage(parts[cursorItem.icon.x][cursorItem.icon.y], xOffset+(i*16), yOffset+(j*16), 16, 16, null);
							}
						}
					}
				}
			}
			int edgeAlpha = 220;
			int fillAlpha = 6;
			if(cursorItem==null||!(cursorItem.ID==repair_module||cursorItem.ID==replacer_module)) {
				edgeAlpha=100;
				fillAlpha=3;
			}
			//draw buff ranges
			for(int i = 0; i<player.partList.size();i++) {
				//if this part gives an aoe buff
				if(player.partList.get(i).buffRange>0) {
					if(player.partList.get(i).ID==repair_module) {
						g.setColor(new Color(0,255,255,edgeAlpha));
					}
					else if(player.partList.get(i).ID==replacer_module) {
						g.setColor(new Color(255,0,255,edgeAlpha));
					}
					int x1 = (player.partList.get(i).x-player.partList.get(i).buffRange)*16;
					int x2 = (player.partList.get(i).x+player.partList.get(i).buffRange+1)*16;
					int y1 = (player.partList.get(i).y-player.partList.get(i).buffRange)*16;
					int y2 = (player.partList.get(i).y+player.partList.get(i).buffRange+1)*16;
					g.drawRect(x1, y1, (x2-x1)-1, (y2-y1)-1);
					if(player.partList.get(i).ID==repair_module) {
						g.setColor(new Color(0,255,255,fillAlpha));
					}
					else if(player.partList.get(i).ID==replacer_module) {
						g.setColor(new Color(255,0,255,fillAlpha));
					}
					g.fillRect(x1, y1, (x2-x1), (y2-y1));
				}
			}


			font = new Font("Iwona Heavy",Font.BOLD,30);
			g.setFont(font);
			//item description sprite border
			g.setColor(new Color(200,200,200));
			g.fillRect(9, AppletUI.windowHeight-171,162,162);

			//draw descriptions
			g.setColor(new Color(40,10,5));
			if(cursorItem!=null) {
				if(!found)g.drawImage(parts[cursorItem.icon.x][cursorItem.icon.y], Controller.mousePosition.x-8, Controller.mousePosition.y-8, 16, 16, null);
				g.drawString(cursorItem.description, 180, AppletUI.windowHeight-140);

				if(cursorItem.ID==repair_module) {
					g.drawString("Restores 1.5 HP to each damaged part every second at the cost of 30 energy each.", 180, AppletUI.windowHeight-110);
					g.drawString("HP: "+cursorItem.maxHP, 180, AppletUI.windowHeight-80);
					g.drawString("Hardness: "+cursorItem.hardness, 180, AppletUI.windowHeight-50);
				}
				else {
					g.drawString("HP: "+cursorItem.maxHP, 180, AppletUI.windowHeight-110);
					g.drawString("Hardness: "+cursorItem.hardness, 180, AppletUI.windowHeight-80);
				}
				g.drawImage(parts[cursorItem.icon.x][cursorItem.icon.y], 10, AppletUI.windowHeight-170,160,160,null);
			}
			else {
				g.drawString("Choose an item to add it to your ship!", 180, AppletUI.windowHeight-140);
			}

			//draw ship stats


			for(int i = 0; i<10; i++) {

				if(i%2==0)
					g.setColor(new Color(10,10,10));
				else
					g.setColor(new Color(20,20,20));
				g.fillRect((player.ship.getWidth()*2)+5, 25+(i*30), 350, 30);

				//g.setColor(new Color(255,100,0));
				//g.drawRect((player.ship.getWidth()*2)+5, 25+(i*30), 500, 30);
			}
			font = new Font("Iwona Heavy",Font.BOLD,18);
			g.setFont(font);
			g.setColor(Color.white);

			DecimalFormat df = new DecimalFormat("#.###");
			g.drawString("Speed:", (player.ship.getWidth()*2)+20, 50);
			g.drawString(df.format(player.speed*AppletUI.GAME_FPS)+"m/s", (player.ship.getWidth()*2)+250, 50);

			g.drawString("Turn Speed:", (player.ship.getWidth()*2)+20, 80);
			g.drawString(df.format(player.turnSpeed)+"", (player.ship.getWidth()*2)+250, 80);

			g.drawString("Weapon Use Cost:", (player.ship.getWidth()*2)+20, 110);
			g.drawString(df.format(-player.weaponCost)+"e", (player.ship.getWidth()*2)+250, 110);

			g.drawString("Guns:", (player.ship.getWidth()*2)+20, 140);
			g.drawString(player.weapons.size()+"", (player.ship.getWidth()*2)+250, 140);

			g.drawString("Projectile Speed:", (player.ship.getWidth()*2)+20, 170);
			g.drawString(df.format((player.projectileSpeed+6)*AppletUI.GAME_FPS)+"m/s", (player.ship.getWidth()*2)+250, 170);

			g.drawString("Maximum Fuel:", (player.ship.getWidth()*2)+20, 200);
			g.drawString(player.fuelLimit+"", (player.ship.getWidth()*2)+250, 200);

			g.drawString("Fuel Regen:", (player.ship.getWidth()*2)+20, 230);
			g.drawString(df.format(player.fuelRegen*AppletUI.GAME_FPS)+"/s", (player.ship.getWidth()*2)+250, 230);

			g.drawString("Maximum Energy:", (player.ship.getWidth()*2)+20, 260);
			g.drawString(df.format(player.energyLimit)+"", (player.ship.getWidth()*2)+250, 260);

			g.drawString("Energy Regen:", (player.ship.getWidth()*2)+20, 290);
			g.drawString(df.format(player.energyRegen*AppletUI.GAME_FPS)+"/s", (player.ship.getWidth()*2)+250, 290);

			g.drawString("Fuel -> Energy:", (player.ship.getWidth()*2)+20, 320);
			g.drawString(df.format(player.conversionRate*AppletUI.GAME_FPS)+"/s", (player.ship.getWidth()*2)+250, 320);

			g.drawString("Parts: ", 10, (player.ship.getHeight()*2)+30);
			g.drawString("("+player.partList.size()+"/"+player.partLimit+")", 100, (player.ship.getHeight()*2)+30);

			font = new Font("Iwona Heavy",Font.BOLD,34);
			g.setFont(font);
			g.setColor(Color.orange);
			if(cursorItem!=null)
				g.drawString("[LMB] Attach Part", AppletUI.windowWidth-330, AppletUI.windowHeight-130);
			else
				g.drawString("[LMB] Select Part", AppletUI.windowWidth-330, AppletUI.windowHeight-130);
			g.drawString("[RMB] Remove Part", AppletUI.windowWidth-330, AppletUI.windowHeight-80);
			g.drawString("[B] Exit build mode", AppletUI.windowWidth-330, AppletUI.windowHeight-30);
		}
		else {
			if(godMode) {
				clearLights();
			}
			player.update();

			//draw background image
			if(AppletUI.windowWidth>1920||AppletUI.windowHeight>1080) {
				g.drawImage(background, 0,0,AppletUI.windowWidth,AppletUI.windowHeight,null);
			}
			else {
				g.drawImage(background, (AppletUI.windowWidth/2)-960,(AppletUI.windowHeight/2)-540,null);
			}
			//draw blocks
			int leftBound = ((int)player.xpos/64)-((AppletUI.windowWidth/2)/64);
			int rightBound = ((int)player.xpos/64)+((AppletUI.windowWidth/2)/64);

			int topBound = ((int)player.ypos/64)-((AppletUI.windowHeight/2)/64);
			int bottomBound = ((int)player.ypos/64)+((AppletUI.windowHeight/2)/64);
			possibleSpawns.clear();
			beacons.clear();
			for(int i = leftBound-1; i<=rightBound+1;i++) {
				for(int j = topBound-1; j<=bottomBound+1;j++) {

					int x = ((i*64)-(int)player.xpos)+(AppletUI.windowWidth/2);
					int y = ((j*64)-(int)player.ypos)+(AppletUI.windowHeight/2);
					Chunk currentChunk = getChunk(i,j);
					currentChunk.update(g);
					g.drawImage(currentChunk.img,x,y,null);
					lmg.drawImage(currentChunk.lightMap,1+(x/8),1+(y/8),null);
					if(currentChunk.nullCount>0&&(i==leftBound-1||i==rightBound+1||j==bottomBound-1||j==bottomBound+1)) {
						possibleSpawns.add(new Point(i*8,j*8));
					}
					if(currentChunk.nullCount==64) {
						lmg.setColor(Color.white);
						lmg.fillRect(1+(x/8), 1+(y/8), 8, 8);
					}
					else {
						for(int k = 0; k<8;k++) {
							for(int l = 0; l<8;l++ ) {
								Item current = blocks.get((i*8)+k, (j*8)+l);
								if(current!=null&&current.isEdge)
									addLight((i*64)+(k*8),(j*64)+(l*8),7,7,false);
							}
						}

					}
				}
			}
			//			for(int i = leftBound-1; i<=rightBound;i++) {
			//				for(int j = topBound-1; j<=bottomBound;j++) {
			//					Item currentBlock = blocks.get(i,j);
			//					int x = ((i*8)-(int)player.xpos)+(AppletUI.windowWidth/2);
			//					int y = ((j*8)-(int)player.ypos)+(AppletUI.windowHeight/2);
			//
			//					if(currentBlock!=null) {
			//						if(currentBlock.isEdge||godMode) {
			//							if(godMode&&currentBlock.isEdge) addLight(i*8,j*8,5,5,false);
			////							if(currentBlock.ID==sentry) {
			////								
			////								double distanceToPlayer = Math.sqrt(Math.pow(((double)i*8-player.xpos),2)+Math.pow(((double)j*8-player.ypos),2));
			////								//if the player is out of range
			////								if(!(distanceToPlayer<500||(currentBlock.alert&&distanceToPlayer<800))) {
			////									currentBlock.updates=0;
			////									g.drawImage(parts[3][2], x, y, null);
			////								}
			////								else {//if the player is in range
			////									currentBlock.alert=true;
			////									currentBlock.updates++;
			////									if(currentBlock.updates<=60&&currentBlock.updates%10==0) {
			////										double angle = Math.atan2(((double)player.ypos-j*8),(((double)player.xpos-i*8)));
			////										projectiles.add(new Projectile(1, i*8, j*8, angle,3000,1));
			////									}
			////									else if(currentBlock.updates>=200) {
			////										currentBlock.updates=0;
			////									}
			////
			////									g.drawImage(parts[3][1], x, y, null);
			////
			////								}
			////							}
			//							else if(currentBlock.ID==beacon) {
			//								g.drawImage(animatedTiles[currentBlock.updates/5][0], x, y, null);
			//								if(currentBlock.updates<35) {
			//									currentBlock.updates++;
			//								}
			//								else {
			//									currentBlock.updates=0;
			//								}
			//								beacons.add(new Point(i,j));
			//
			//							}
			//						}
			//						else {
			//								g.setColor(new Color(30,30,30));
			//								g.fillRect(x, y, 8, 8);
			//						}
			//						if(currentBlock.hp<currentBlock.maxHP) {
			//							//g.setColor(new Color(30,0,0,255-blocks.get(i, j).hp));
			//							int alpha = (int)(double)(200.0*(1.0-(currentBlock.hp/currentBlock.maxHP)));
			//							if(alpha>=0&&alpha<=255)
			//								g.setColor(new Color(30,0,0,alpha));
			//							g.fillRect(x, y, 8, 8);
			//						}
			//						if(currentBlock.regens==true&&randomNumber(1,20)==1&&currentBlock.hp<currentBlock.maxHP) {
			//							currentBlock.hp++;
			//						}
			//					}
			//					else if(i==leftBound-1||j==topBound-1||i==rightBound||j==bottomBound){
			//						possibleSpawns.add(new Point(i,j));
			//
			//					}
			//
			//				}
			//
			//			}

			//draw beacon lighting effect
			for(int i = 0; i<beacons.size();i++) {
				Point current = beacons.get(i);
				Item currentBlock = blocks.get(current.x, current.y);
				if(currentBlock!=null) {
					if(currentBlock.updates/5==0) addLight(((current.x*8)+4)-4, ((current.y*8)+4)-4, 9, 9,false);
					else if(currentBlock.updates/5==1) addLight(((current.x*8)+4), ((current.y*8)+4)-4, 9, 9,false);
					else if(currentBlock.updates/5==2) addLight(((current.x*8)+4)+4, ((current.y*8)+4)-4, 9, 9,false);
					else if(currentBlock.updates/5==3) addLight(((current.x*8)+4)+4, ((current.y*8)+4), 9, 9,false);
					else if(currentBlock.updates/5==4) addLight(((current.x*8)+4)+4, ((current.y*8)+4)+4, 9, 9,false);
					else if(currentBlock.updates/5==5) addLight(((current.x*8)+4), ((current.y*8)+4)+4, 9, 9,false);
					else if(currentBlock.updates/5==6) addLight(((current.x*8)+4)-4, ((current.y*8)+4)+4, 9, 9,false);
					else if(currentBlock.updates/5==7) addLight(((current.x*8)+4)-4, ((current.y*8)+4), 9, 9,false);
				}
			}
			//spawn enemy ships
			if(ships.size()<=10&&possibleSpawns.size()>0) {
				int seconds = (int)(double)(60.0*(Math.pow(.8, beacons.size())));
				seconds = (int)(double)((double)seconds*(1.0 - (double)player.partList.size()/(double)(player.partLimit*10)));
				if(seconds<3) seconds=3;
				int roll = randomNumber(1,AppletUI.fps*10*seconds);
				Point position = possibleSpawns.get(randomNumber(0, possibleSpawns.size()-1));
				if(roll<=10) {
					if(ships.size()<=5) {
						spawnEnemyShip();
					}
					else {
						if(player.partList.size()<500)
							ships.add(new Ship(4, position.x*8, position.y*8));
						else {
							for(int i = 0; i<randomNumber(1,5);i++) {
								if(ships.size()<10) {
									ships.add(new Ship(randomNumber(1,2)*2, position.x*8, position.y*8));
									possibleSpawns.get(randomNumber(0, possibleSpawns.size()-1));
								}
								else {
									break;
								}
							}
						}
					}
				}
				Font font = new Font("Iwona Heavy",Font.BOLD,12);
				g.setFont(font);
				//draw danger
				danger = (int)(100-(double)(((double)seconds/60.0)*100.0));
				g.setColor(new Color(255,255,255));
				g.drawString("Danger: "+danger+"%", 10, 140);
			}
			else {
				System.out.println("Spawn failed! Unable to spawn more ships right now!");
			}

			//draw other ships
			for(int i = 0; i<ships.size();i++) {
				double distanceFromPlayer = Math.sqrt(Math.pow(((double)ships.get(i).xpos-player.xpos),2)+Math.pow(((double)ships.get(i).ypos-player.ypos),2));
				if(distanceFromPlayer<2000&&!ships.get(i).dead) {
					ships.get(i).Draw(g);
				}
				else {
					ships.remove(i);
				}
			}
			//update player
			//draw player
			player.Draw(g);
			//draw projectiles
			drawProjectiles(g);
			drawExplosions(g);
			if(godMode) {
				applyLights(g);
			}

			//draw combat text
			for(int i = 0; i<combatText.size();i++){
				combatText.get(i).Draw(g);
			}
			//draw loot text
			for(int i = 0; i<lootText.size();i++){
				lootText.get(i).Draw(g, (lootText.size()-1)-i);
			}


			if(player.fuel>5000&&player.energy<player.energyLimit&&percentEnergy<percentFuel) {
				player.changeFuel(-player.conversionRate);
				player.changeEnergy(player.conversionRate);
			}
			Font font = new Font("Iwona Heavy",Font.BOLD,12);
			g.setFont(font);
			//draw fuel
			g.setColor(new Color(160,160,160,50));
			g.fillRect(5, 55, 302, 22);
			g.setColor(new Color(250,250,135,50));
			g.fillRect(6, 56, (int)(double)(300.0*percentFuel), 20);
			g.setColor(Color.black);
			g.drawString("Fuel: "+(int)player.fuel+"/"+(int)player.fuelLimit, 10, 70);

			//draw energy
			g.setColor(new Color(160,160,160,50));
			g.fillRect(5, 85, 302, 22);
			g.setColor(new Color(135,250,250,50));
			g.fillRect(6, 86, (int)(double)(300.0*percentEnergy), 20);
			g.setColor(Color.black);
			g.drawString("Energy: "+(int)player.energy+"/"+(int)player.energyLimit, 10, 100);
		}

		//draw fps
		Font font = new Font("Iwona Heavy",Font.BOLD,12);
		g.setFont(font);
		g.setColor(new Color(255,255,255,10));
		g.drawString(""+ships.size(), 10, 120);
		g.setColor(Color.white);
		g.drawString("FPS: "+AppletUI.fps, 10, 20);
		g.setColor(Color.orange);
		g.drawString("[H] Help", 10, 40);

		drawHelp(g);

		font = new Font("Iwona Heavy",Font.BOLD, 26);
		g.setFont(font);
		g.setColor(Color.red);
		FontMetrics m = g.getFontMetrics();
		if(percentFuel<.2) {
			String txt = "Low Fuel";
			int x = (AppletUI.windowWidth/2)-(m.stringWidth(txt)/2);
			int y = (AppletUI.windowHeight/2)+(((int)player.radius*8))+50;
			g.drawString(txt, x, y);
		}
		if(percentEnergy<.2) {
			String txt = "Low Energy";
			int x = (AppletUI.windowWidth/2)-(m.stringWidth(txt)/2);
			int y = (AppletUI.windowHeight/2)+(((int)player.radius*8))+80;
			g.drawString(txt, x, y);
		}
		if(player.inventory.size()==player.inventoryLimit) {
			String txt = "Inventory is full!";
			int x = (AppletUI.windowWidth/2)-(m.stringWidth(txt)/2);
			int y = (AppletUI.windowHeight/2)+(((int)player.radius*8))+110;
			g.drawString(txt, x, y);
		}
	}
}
