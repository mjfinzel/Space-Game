package Game;

import java.awt.Color;
import java.awt.Point;

public class Blocky {
	int xpos, ypos;
	int ID;
	double hp = 0;
	double maxHP=0;
	int hardness=1;
	int updates=0;
	boolean isEdge = false;
	boolean regens = false;
	boolean alert = false;
	boolean exploded = false;
	boolean animated = false;
	public Blocky(int id, int x, int y) {
		xpos = x;
		ypos = y;
		ID=id;
		if(id==GamePanel.hull||id==GamePanel.sentry) {
			isEdge=true;
			regens=true;
		}
		if(id==GamePanel.rock) {
			hardness=4;
			maxHP=20;
		}
		if(id==GamePanel.ice) {
			hardness=1;
			maxHP=20;
		}
		if(id==GamePanel.sentry) {
			maxHP=10;
		}
		if(id==GamePanel.hull) {
			hardness=15;
			maxHP=80;
		}
		if(id==GamePanel.uranium) {
			hardness=3;
			maxHP=20;
		}
		if(id==GamePanel.iron) {
			hardness=5;
			maxHP=50;
		}
		if(id==GamePanel.lead) {
			hardness=2;
			maxHP=50;
		}
		if(id==GamePanel.beacon) {
			hardness=8;
			maxHP=30;
			isEdge=true;
			animated=true;
		}
		hp=maxHP;
	}
	public boolean takeDamageAndCheckIfDead(double amt, int id) {
		isEdge=true;
		alert=true;
		if(this.ID==GamePanel.beacon&&GamePanel.randomNumber(1, 5)==1) {
			GamePanel.spawnEnemyShip();
		}
		hp-=amt;
		if(hp<=0) {
			hp=0;
			if(ID==GamePanel.uranium&&exploded==false) {
				exploded=true;
				GamePanel.explosions.add(new Explosion((int)xpos, (int)ypos, id,6));
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
}
