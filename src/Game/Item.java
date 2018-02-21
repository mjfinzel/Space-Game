package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class Item {
	int amount;
	int maxStackSize = 999;
	int ID=-1;
	String name = "Glitch Item";
	String description = "Glitch Item - Should not be obtainable!";
	double turnSpeed=0;
	double speed=0;
	double projectileSpeed=0;
	double fuelRegen=0;
	double energyRegen=0;
	int fuel=0;
	int energy=0;

	double conversionRate=0;
	Point icon = new Point(7,7);
	double distanceFromCore=0;
	double angleFromCore=0;
	int x,y;
	double maxHP = 0;
	double hp = 0;
	double hardness=1;

	int buffRange = 0;
	int inventorySlots = 0;
	double repairAmt = 0;
	double energyCost = 0;
	Weapon weapon;
	Ship ship;
	int rarity = 0;

	int updates = 0;

	boolean isEdge = false;
	boolean exploded = false;
	boolean alert = false;
	boolean regens = false;
	boolean animated = false;
	int layout = 0;
	int textureY = 0;

	public Item(int id, double d, double a, int x, int y, int amt, Ship ship) {
		this.ship=ship;
		this.x=x;
		this.y=y;
		distanceFromCore=d;
		angleFromCore=a;
		ID = id;
		amount = amt;
		if(ID==GamePanel.shipCore) {
			name = "Ship Core";
			description = "The core of your ship. Keep it safe!";
			icon = new Point(0,0);
			speed=3;
			maxHP=60;
			hardness=1;
			rarity = 0;
		}
		else if(ID==GamePanel.cockpit) {
			name = "Cockpit";
			description = "Cockpit - Increases turn speed.";
			icon = new Point(1,0);
			turnSpeed=.2;
			maxHP=40;
			hardness=3;
			rarity = 8;
		}
		else if(ID==GamePanel.engine) {
			name = "Engine";
			description = "Engine - Increases speed.";
			icon = new Point(2,0);
			speed=1;
			maxHP=50;
			hardness=2;
			rarity = 6;
		}
		else if(ID==GamePanel.laser) {
			name = "Laser";
			description = "Laser - Adds firepower!";
			speed = -.1;
			icon = new Point(3,0);
			maxHP=40;
			hardness=1;
			weapon = new Weapon(0, distanceFromCore, angleFromCore, ship, x, y);
			rarity = 5;
		}
		else if(ID==GamePanel.fuel) {
			name = "Fuel Tank";
			description = "Fuel Tank - Gives +5,000 fuel limit. Keep this safe!";
			icon = new Point(4,0);
			fuel=5000;
			maxHP=5;
			hardness=.5;
			rarity = 7;
		}
		else if(ID==GamePanel.hull) {
			name = "Hull";
			description = "Hull - Armor for your ship. Use this to shield sensative equipment!";
			icon = new Point(5,0);
			maxHP=90;
			hardness=15;
			speed=-.02;
			rarity = 3;
			textureY=5;
		}
		else if(ID==GamePanel.citadel_hull) {
			name = "Citadel Armor";
			description = "Citadel Armor - A very tough material used to fortify citadels.";
			icon = new Point(5,1);
			maxHP=250;
			hardness=50;
			speed=-.02;
			rarity = 4;
			textureY=6;
		}
		else if(ID==GamePanel.cargo) {
			name = "Cargo Bay";
			description = "Cargo Bay - Adds an additional inventory slot. If it is destroyed you will lose its contents.";
			icon = new Point(6,0);
			maxHP=40;
			hardness=2;
			inventorySlots=1;
			rarity = 6;
		}
		else if(ID==GamePanel.acceleration_module) {
			name = "Laser Amplifier";
			description = "Laser Amplifier - Increases laser penetration ability.";
			icon = new Point(7,0);
			maxHP=20;
			projectileSpeed=1;
			hardness=1;
			rarity = 4;
		}
		else if(ID==GamePanel.solar_panel) {
			name = "Solar Panel";
			description = "Solar Panel - A slow but passive source of energy.";
			icon = new Point(0,1);
			energyRegen=.03;
			maxHP=20;
			hardness=1;
			rarity = 3;
		}
		else if(ID==GamePanel.repair_module) {
			name = "Repair Module";
			description = "Repair Module - Uses energy to repair nearby ship parts.";
			icon = new Point(1,1);
			maxHP=10;
			hardness=2;
			repairAmt = .025;
			buffRange = 3;
			energyCost = .25;
			rarity = 8;
		}
		else if(ID==GamePanel.generator) {
			name = "Generator";
			description = "Generator - Converts fuel into energy.";
			icon = new Point(2,1);
			maxHP=30;
			hardness=2;
			conversionRate=.5;
			rarity = 4;
		}
		else if(ID==GamePanel.battery) {
			name = "Battery";
			description = "Battery - Gives +5,000 energy limit. Keep this safe!";
			icon = new Point(4,1);
			maxHP=30;
			hardness=2;
			energy=5000;
			rarity = 7;
		}
		else if(ID==GamePanel.beam) {
			name = "Beam Gun";
			description = "Beam Gun - Shoots a devastating beam. Uses an immense amount of energy.";
			icon = new Point(3,3);
			maxHP=200;
			hardness=2;
			speed=-3;
			weapon = new Weapon(2, distanceFromCore, angleFromCore, ship, x, y);
			rarity = 9;
		}

		else if(ID==GamePanel.rock) {
			name = "Rock";
			description = "Rock - A pile of useless rocks.";
			icon = new Point(0,7);
			maxStackSize=9999;
			rarity = 1;
			hardness=4;
			maxHP=20;
			textureY=0;
		}
		else if(ID==GamePanel.ice) {
			name = "Ice";
			description = "Ice - A source of fuel.";
			icon = new Point(1,7);
			rarity = 2;
			hardness=1;
			maxHP=20;
			textureY=1;
		}
		else if(ID==GamePanel.uranium) {
			name = "Uranium";
			description = "Uranium - Can be converted into energy using a reactor.";
			icon = new Point(2,7);
			rarity = 3;
			hardness=3;
			maxHP=20;
			textureY=2;
		}
		else if(ID==GamePanel.iron) {
			name = "Iron";
			description = "Iron - Used to build ship parts.";
			icon = new Point(3,7);
			rarity = 2;
			hardness=5;
			maxHP=50;
			textureY=3;
		}
		else if(ID==GamePanel.lead) {
			name = "Lead";
			description = "Lead - Used to build ship parts.";
			icon = new Point(4,7);
			rarity = 2;
			hardness=2;
			maxHP=50;
			textureY=4;
		}
		else if(ID==GamePanel.replacer_module) {
			name = "Replacer Module";
			description = "Replacer Module - Uses energy to replace destroyed nearby ship parts.";
			icon = new Point(1,2);
			maxHP=10;
			hardness=2;
			buffRange = 4;
			energyCost = 500;
			rarity = 8;
		}

		//unobtainable
		else if(id==GamePanel.beacon) {
			hardness=8;
			maxHP=30;
			isEdge=true;
			animated=true;
		}
		else if(id==GamePanel.sentry) {
			maxHP=10;
			isEdge=true;
			regens=true;
		}
		if(amount>maxStackSize) amount=maxStackSize;
		if(ship!=null&&ship.id!=0) maxHP=maxHP*1.2;
		hp=maxHP;
	}
	public void takeDamage(double amount) {
		hp-=amount;
		if(hp<=0) {
			ship.removePartFromShip(ID, x, y);
			if(ship.id>0) {
				if(ID!=GamePanel.shipCore) {
					if(GamePanel.randomNumber(1,10)<=2) {//20% chance to drop
						GamePanel.player.addItemToInventory(this);
					}
				}
				else {
					for(int i = 0; i<ship.partList.size();i++) {
						if(GamePanel.randomNumber(1,100)<=5&&ID!=GamePanel.shipCore) {//5% chance to drop
							GamePanel.player.addItemToInventory(ship.partList.get(i));
						}
					}
				}
			}
		}
		else {
			Graphics g = ship.ship.getGraphics();
			g.clearRect(x*8, y*8, 8, 8);
			g.drawImage(GamePanel.parts[icon.x][icon.y], x*8, y*8, null);
			int alpha = (int)(double)(200.0*(1.0-(hp/maxHP))); if(alpha>255) alpha=255; if(alpha<0) alpha=0;
			g.setColor(new Color(30,0,0,alpha));
			g.fillRect(x*8, y*8, 8, 8);
			g.dispose();
		}
	}
	public boolean takeDamageAndCheckIfDead(double amt, int id) {
		
		updateLayout();
		alert=true;
		if(this.ID==GamePanel.beacon&&GamePanel.randomNumber(1, 5)==1) {
			GamePanel.spawnEnemyShip();
		}
		hp-=amt;
		if(hp<=0) {
			for(int i = (int)(x)-1; i<=(int)(x)+1;i++) {
				for(int j = (int)(y)-1; j<=(int)(y)+1;j++) {
					if(GamePanel.blocks.get(i,j)!=null) {
						GamePanel.blocks.get(i,j).isEdge = true;
						GamePanel.blocks.get(i,j).updateLayout();
					}
				}
			}
			hp=0;
			if(ID==GamePanel.uranium&&exploded==false) {
				exploded=true;
				GamePanel.explosions.add(new Explosion((int)x, (int)y, id,6));
				return true;
			}

			if(id==0) {
				if(this.ID==GamePanel.sentry||this.ID==GamePanel.beacon) {
					if(GamePanel.randomNumber(1, 10)<=8) {//80% chance to drop a ship part
						int roll = GamePanel.randomNumber(1, 1000);
						Item loot = null;

						if	   (roll<=40) loot = new Item(GamePanel.cockpit, -1, -1, -1, -1, GamePanel.randomNumber(1, 1), null);//					4%		4%
						else if(roll<=80) loot = new Item(GamePanel.replacer_module, -1, -1, -1, -1, GamePanel.randomNumber(1, 1), null);//			4%		8%
						else if(roll<=120) loot = new Item(GamePanel.repair_module, -1, -1, -1, -1, GamePanel.randomNumber(1, 1), null);//			4%		8%
						else if(roll<=180) loot = new Item(GamePanel.battery, -1, -1, -1, -1, GamePanel.randomNumber(1, 1), null);//				5%		13%
						else if(roll<=220) loot = new Item(GamePanel.fuel, -1, -1, -1, -1, GamePanel.randomNumber(1, 1), null);//					5%		18%
						else if(roll<=290) loot = new Item(GamePanel.engine, -1, -1, -1, -1, GamePanel.randomNumber(1, 1), null);//					7%		25%
						else if(roll<=360) loot = new Item(GamePanel.cargo, -1, -1, -1, -1, GamePanel.randomNumber(1, 1), null);//					7%		32%
						else if(roll<=510) loot = new Item(GamePanel.acceleration_module, -1, -1, -1, -1, GamePanel.randomNumber(1, 1), null);//	15%		47%
						else if(roll<=660) loot = new Item(GamePanel.generator, -1, -1, -1, -1, GamePanel.randomNumber(1, 1), null);//				15%		62%
						else if(roll<=770) loot = new Item(GamePanel.laser, -1, -1, -1, -1, GamePanel.randomNumber(1, 2), null);//					11%		77%
						else if(roll<=1000) loot = new Item(GamePanel.solar_panel, -1, -1, -1, -1, GamePanel.randomNumber(1, 4), null);//			23%		100%

						if(!GamePanel.player.addItemToInventory(loot)){
							GamePanel.combatText.add(new CombatText((int)GamePanel.player.xpos, (int)GamePanel.player.ypos, "Inventory is full!", new Color(200,200,200)));
						}
					}

				}
				else {
					Item item = new Item(this.ID, -1,-1,-1,-1,1, null);
					GamePanel.player.addItemToInventory(item);
					//GamePanel.addLootMessage(item);
				}

			}
			return true;
		}
		return false;
	}
	public void updateLayout() {
		//get refrences to adjacent parts
		Item tl = null;//top left
		Item t = null;//top
		Item tr = null;//top right
		Item l = null;//left
		Item r = null;//right
		Item bl = null;//bottom left
		Item b = null;//bottom
		Item br = null;//bottom right
		if(ship==null) {
			tl=GamePanel.blocks.get(x-1, y-1);
			t=GamePanel.blocks.get(x, y-1);
			tr=GamePanel.blocks.get(x+1, y-1);
			l=GamePanel.blocks.get(x-1, y);
			r=GamePanel.blocks.get(x+1, y);
			bl=GamePanel.blocks.get(x-1, y+1);
			b=GamePanel.blocks.get(x, y+1);
			br=GamePanel.blocks.get(x+1, y+1);
		}
		else {
			tl=ship.getPart(x-1, y-1);
			t=ship.getPart(x, y-1);
			tr=ship.getPart(x+1, y-1);
			l=ship.getPart(x-1, y);
			r=ship.getPart(x+1, y);
			bl=ship.getPart(x-1, y+1);
			b=ship.getPart(x, y+1);
			br=ship.getPart(x+1, y+1);
		}
		//booleans to check if each adjacent block has the same ID as this block or not
		boolean nw = false;
		if(tl!=null&&tl.ID==ID) nw=true;
		boolean n = false;
		if(t!=null&&t.ID==ID) n=true;
		boolean ne = false;
		if(tr!=null&&tr.ID==ID) ne=true;
		boolean w = false;
		if(l!=null&&l.ID==ID) w=true;
		boolean e = false;
		if(r!=null&&r.ID==ID) e=true;
		boolean sw = false;
		if(bl!=null&&bl.ID==ID) sw=true;
		boolean s = false;
		if(b!=null&&b.ID==ID) s=true;
		boolean se = false;
		if(br!=null&&br.ID==ID) se=true;

		boolean nwb = (tl!=null);
		boolean nb = (t!=null);
		boolean neb = (tr!=null);
		boolean wb = (l!=null);
		boolean eb = (r!=null);
		boolean swb = (bl!=null);
		boolean sb = (b!=null);
		boolean seb = (br!=null);

		//set layout according to adjacent blocks
		if(n&&w&&e&&s) layout=0+GamePanel.randomNumber(0, 2);
		else if(!nb&&!wb&&e&&s) layout=3+GamePanel.randomNumber(0, 2);
		else if(!nb&&w&&e&&s) layout=6+GamePanel.randomNumber(0, 2);
		else if(!nb&&w&&!eb&&s) layout=9+GamePanel.randomNumber(0, 2);
		else if(n&&w&&!eb&&s) layout=12+GamePanel.randomNumber(0, 2);
		else if(n&&w&&!eb&&!sb) layout=15+GamePanel.randomNumber(0, 2);
		else if(n&&w&&e&&!sb) layout=18+GamePanel.randomNumber(0, 2);
		else if(n&&!wb&&e&&!sb) layout=21+GamePanel.randomNumber(0, 2);
		else if(n&&!wb&&e&&s) layout=24+GamePanel.randomNumber(0, 2);
		else if(!nb&&!wb&&!e&&s) layout=27+GamePanel.randomNumber(0, 2);
		else if(n&&!wb&&!eb&&!s) layout=30+GamePanel.randomNumber(0, 2);
		else if(!nb&&!wb&&e&&!sb) layout=33+GamePanel.randomNumber(0, 2);
		else if(!nb&&w&&!eb&&!sb) layout=36+GamePanel.randomNumber(0, 2);
		else if(!nb&&w&&e&&!sb) layout=39+GamePanel.randomNumber(0, 2);
		else if(n&&!wb&&!eb&&s) layout=42+GamePanel.randomNumber(0, 2);
		else if(!nb&&!wb&&!eb&&!sb) layout=45+GamePanel.randomNumber(0, 2);

		else if(n&&w&&e&&s) layout=45+0+GamePanel.randomNumber(0, 2);
		else if(!n&&!w&&e&&s) layout=45+3+GamePanel.randomNumber(0, 2);
		else if(!n&&w&&e&&s) layout=45+6+GamePanel.randomNumber(0, 2);
		else if(!n&&w&&!e&&s) layout=45+9+GamePanel.randomNumber(0, 2);
		else if(n&&w&&!e&&s) layout=45+12+GamePanel.randomNumber(0, 2);
		else if(n&&w&&!e&&!s) layout=45+15+GamePanel.randomNumber(0, 2);
		else if(n&&w&&e&&!s) layout=45+18+GamePanel.randomNumber(0, 2);
		else if(n&&!w&&e&&!s) layout=45+21+GamePanel.randomNumber(0, 2);
		else if(n&&!w&&e&&s) layout=45+24+GamePanel.randomNumber(0, 2);
		else if(!n&&!w&&!e&&s) layout=45+27+GamePanel.randomNumber(0, 2);
		else if(n&&!w&&!e&&!s) layout=45+30+GamePanel.randomNumber(0, 2);
		else if(!n&&!w&&e&&!s) layout=45+33+GamePanel.randomNumber(0, 2);
		else if(!n&&w&&!e&&!s) layout=45+36+GamePanel.randomNumber(0, 2);
		else if(!n&&w&&e&&!s) layout=45+39+GamePanel.randomNumber(0, 2);
		else if(n&&!w&&!e&&s) layout=45+42+GamePanel.randomNumber(0, 2);
		else if(!n&&!w&&!e&&!s) layout=45+45+GamePanel.randomNumber(0, 2);

		while(x>=GamePanel.worldWidth) x-=GamePanel.worldWidth;
		while(y>=GamePanel.worldHeight) y-=GamePanel.worldHeight;
		while(x<0) x+=GamePanel.worldWidth;
		while(y<0) y+=GamePanel.worldHeight;

		if(ship==null&&x>=0&&y>=0&&x/8<250&&y/8<250) {
			//GamePanel.chunks[x/8][y/8].graphics.clearRect((x%8)*8, (y%8)*8, 8,8);
			if(ID==GamePanel.beacon) {
				if(updates/5<=7)
				GamePanel.chunks[x/8][y/8].graphics.drawImage(GamePanel.animatedTiles[updates/5][0], (x%8)*8, (y%8)*8, null);
			}
			else {
				GamePanel.chunks[x/8][y/8].graphics.drawImage(GamePanel.tiles[layout][textureY], (x%8)*8, (y%8)*8, null);
			}

		}
	}
	public void repair(double amt) {
		if(hp<maxHP) {
			hp+=amt;
			if(hp>maxHP) {
				hp=maxHP;
				Graphics g = ship.ship.getGraphics();
				g.clearRect(x*8, y*8, 8, 8);
				g.drawImage(GamePanel.parts[icon.x][icon.y], x*8, y*8, null);
				g.dispose();

			}
		}
		else {
			Graphics g = ship.ship.getGraphics();
			g.clearRect(x*8, y*8, 8, 8);
			g.drawImage(GamePanel.parts[icon.x][icon.y], x*8, y*8, null);
			g.setColor(new Color(30,0,0,(int)(double)(200.0*(1.0-(hp/maxHP)))));
			g.fillRect(x*8, y*8, 8, 8);
			g.dispose();
		}
	}
}
