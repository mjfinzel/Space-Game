package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Ship {
	double xpos, ypos;
	int maxWidth = 49;
	int maxHeight = 49;
	int id;
	Item[][] parts = new Item[maxWidth][maxHeight];
	Item[][] design = new Item[maxWidth][maxHeight];
	ArrayList<Item> partList = new ArrayList<Item>();
	ArrayList<Item> edgeParts = new ArrayList<Item>();
	//ArrayList<ArrayList<Item>> partsByRadius = new ArrayList<ArrayList<Item>>();
	ArrayList<ArrayList<Item>> partsByRadius = new ArrayList<ArrayList<Item>>(maxWidth);

	final int partLimit = 1000;
	double angleInDegrees;
	double angleInRadians;
	double turnSpeed = .5;
	double speed = .5;
	double projectileSpeed = .5;
	double fuel=20000;
	double fuelLimit = 0;
	double fuelRegen = 0;

	double energy=2000;
	double energyLimit=0;
	double energyRegen=0;
	double conversionRate = 0;

	double weaponCost = 1;


	double xMomentum = 0;
	double yMomentum = 0;

	double radius = 0;
	boolean dead = false;

	BufferedImage ship = new BufferedImage(maxWidth*8, maxHeight*8, BufferedImage.TYPE_INT_ARGB);
	ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	ArrayList<Item> inventory = new ArrayList<Item>();
	int inventoryLimit = 12;
	double distanceToWeapons;
	double angleToWeapons;
	int AI_attack_range = 500;
	public Ship(int id, int x, int y) {
		this.id=id;
		xpos = x;
		ypos = y;
		clearPartsByRadius();
		for(int i = 0; i<maxWidth;i++) {
			for(int j = 0; j<maxHeight;j++) {
				parts[i][j]=null;
			}
		}



		if(id>0) {//if this is not the player's ship

			AI_attack_range = 400+(GamePanel.randomNumber(1, 8)*50);
			buildFromPremade(new PremadeShip(id));

		}
		else {
			//add basic parts to ship
			for(int i = 23; i<=25;i++) {
				for(int j = 23; j<=25;j++) {
					addPartToShip(GamePanel.hull, i, j);
				}
			}
			addPartToShip(GamePanel.shipCore, 24, 24);
			addItemToInventory(new Item(GamePanel.cockpit,-1,-1,-1,-1, 1, null));//					1
			addItemToInventory(new Item(GamePanel.engine,-1,-1,-1,-1, 5, null));//					2
			addItemToInventory(new Item(GamePanel.laser,-1,-1,-1,-1, 5, null));//					2
			addItemToInventory(new Item(GamePanel.hull,-1,-1,-1,-1, 24, null));//					24
			addItemToInventory(new Item(GamePanel.fuel,-1,-1,-1,-1, 1, null));//					1
			addItemToInventory(new Item(GamePanel.cargo,-1,-1,-1,-1, 0, null));//					0
			addItemToInventory(new Item(GamePanel.acceleration_module,-1,-1,-1,-1, 2, null));//		2
			addItemToInventory(new Item(GamePanel.solar_panel,-1,-1,-1,-1, 4, null));//				4
			addItemToInventory(new Item(GamePanel.repair_module,-1,-1,-1,-1, 0, null));//			0
			addItemToInventory(new Item(GamePanel.generator,-1,-1,-1,-1, 1, null));//				1
			addItemToInventory(new Item(GamePanel.battery,-1,-1,-1,-1, 1, null));//					1
			addItemToInventory(new Item(GamePanel.beam,-1,-1,-1,-1, 0, null));//					0
			addItemToInventory(new Item(GamePanel.replacer_module,-1,-1,-1,-1, 20, null));//		0
			//addPartToShip(1,24,24);
			buildFromPremade(new PremadeShip(7));
		}

	}
	public Item getPart(int i, int j) {
		if(i>=0&&i<maxWidth&&j>=0&&j<maxHeight)
			return parts[i][j];
		else
			return null;
	}
	public void addCheaterItems() {
		inventory.clear();
		addPartToShip(GamePanel.cargo, 24, 23);
		addPartToShip(GamePanel.cargo, 24, 25);
		addItemToInventory(new Item(GamePanel.cockpit,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.engine,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.laser,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.hull,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.fuel,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.cargo,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.acceleration_module,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.solar_panel,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.repair_module,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.generator,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.battery,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.beam,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.replacer_module,-1,-1,-1,-1, 999, null));
		addItemToInventory(new Item(GamePanel.citadel_hull,-1,-1,-1,-1, 999, null));
	}
	public void buildFromPremade(PremadeShip ps) {
		initializeShipVariables();
		//partList.clear();
		if(id!=3) {
			for(int i =0;i<maxWidth;i++) {
				for(int j = 0; j<maxHeight;j++) {
					if(ps.layout[j][i]!=-1) {
						double angleFromCore = Math.atan2(((double)i-24.0),(((double)j-24.0)));
						double distanceFromCore = Math.sqrt(Math.pow(((double)j-24.0),2)+Math.pow(((double)i-24.0),2));
						parts[j][i] = new Item(ps.layout[j][i], distanceFromCore*8, angleFromCore,j,i, 1, this);
					}
				}
			}
		}
		else {
			for(int i =0;i<maxWidth;i++) {
				for(int j = 0; j<maxHeight;j++) {
					if(GamePanel.player.design[j][i]!=null) {
						double angleFromCore = Math.atan2(((double)i-24.0),(((double)j-24.0)));
						double distanceFromCore = Math.sqrt(Math.pow(((double)j-24.0),2)+Math.pow(((double)i-24.0),2));
						parts[j][i] = new Item(GamePanel.player.design[j][i].ID, distanceFromCore*8, angleFromCore,j,i, 1, this);
					}
				}
			}
		}
		generateShip();
		storeDesign();
		if(id!=0) {
			fuel=fuelLimit;
			energy=energyLimit;
		}
	}
	public void storeDesign() {
		for(int i = 0; i<maxWidth; i++) {
			design[i] = parts[i].clone();
		}
	}
	public void printLayout() {
		System.out.println("int[][] temp = {");
		for(int i = 0; i<maxWidth; i++) {
			System.out.print("{");
			for(int j = 0; j<maxHeight; j++) {
				if(parts[i][j]!=null) {
					if(parts[i][j].ID>=0&&parts[i][j].ID<10) {
						System.out.print(" "+parts[i][j].ID+",");
					}
					else {
						System.out.print(""+parts[i][j].ID+",");
					}
				}
				else {
					System.out.print("-1,");
				}
			}
			System.out.println("},");

		}
		System.out.println("};");
		System.out.println("layout=temp;");
	}
	public int getStackableInventoryItem(Item item,boolean includeFullStack) {
		for(int i = 0; i<inventory.size();i++) {
			if(inventory.get(i).ID==item.ID&&(includeFullStack||inventory.get(i).amount<inventory.get(i).maxStackSize)) {
				return i;
			}
		}
		return -1;
	}
	public void getWeaponsLocations() {
		double totalX = 0;
		double totalY = 0;
		for(int i = 0; i<weapons.size();i++) {
			Weapon currentWeapon = weapons.get(i);
			double y = currentWeapon.xpos;
			double x = currentWeapon.ypos;
			totalX+=(x);
			totalY+=(y);
		}
		double avgX = 24;
		double avgY = 24;
		if(weapons.size()>0) {
			avgX = (maxWidth-1)-(totalX/(double)weapons.size());
			avgY = (maxHeight-1)-(totalY/(double)weapons.size());
		}
		distanceToWeapons = GamePanel.distanceBetween(24.0, 24.0, avgX, avgY)*8;
		angleToWeapons = Math.atan2(((double)(avgY)-24),(((double)(avgX)-24)));

	}
	public boolean addItemToInventory(Item item) {
		if(item.amount>0) {
			int alreadyHad = getStackableInventoryItem(item,false);
			if(alreadyHad==-1) {
				if(inventory.size()<inventoryLimit) {
					inventory.add(new Item(item.ID,-1,-1,-1,-1,item.amount, null));
					GamePanel.addLootMessage(item);
					return true;
				}
			}
			else {
				int extra = inventory.get(alreadyHad).maxStackSize-inventory.get(alreadyHad).amount;
				inventory.get(alreadyHad).amount+=item.amount;
				if(inventory.get(alreadyHad).amount>inventory.get(alreadyHad).maxStackSize) {
					if(inventory.size()<inventoryLimit) {
						inventory.add(new Item(item.ID, -1, -1, -1, -1, extra, null));
						inventory.get(alreadyHad).amount=inventory.get(alreadyHad).maxStackSize;
					}
				}

				GamePanel.addLootMessage(item);
				return true;
			}
		}
		return false;
	}
	public boolean removeItemFromInventory(Item item) {
		if(item.amount>0) {
			int alreadyHad = getStackableInventoryItem(item,true);
			if(alreadyHad==-1) {
				return false;
			}
			else {
				Item existingItem = inventory.get(alreadyHad);
				existingItem.amount-=item.amount;
				if(existingItem.amount<1) {
					inventory.remove(alreadyHad);

				}
				return true;
			}
			//return false;
		}
		return false;
	}
	public void setAngle(double angle) {
		if(angle>360)
			angle = angle%360;
		else if(angle<0) {
			while(angle<0) {
				angle+=360;
			}
		}
		angleInDegrees = angle;
		angleInRadians = Math.toRadians(angleInDegrees);
	}
	public void move() {//1 for forward, -1 for backward

		double newX = xpos+(xMomentum*AppletUI.delta);
		double newY = ypos+(yMomentum*AppletUI.delta);

		if(GamePanel.blocks.get((int)newX/8, (int)ypos/8)==null)
			xpos = newX;
		else {
			xMomentum=0;
		}
		if(GamePanel.blocks.get((int)xpos/8, (int)newY/8)==null)
			ypos = newY;
		else {
			yMomentum=0;
		}

	}
	public void fireWeapons(boolean focusFire) {
		for(int i = 0; i<weapons.size(); i++) {
			weapons.get(i).fire(angleInRadians, focusFire);
		}
	}
	public void changeFuel(double amt) {
		if(amt>0) {
			if(fuel<fuelLimit) {
				fuel+=amt;
				if(fuel>fuelLimit) {
					fuel=fuelLimit;
				}
			}
		}
		else {
			if(fuel>0) {
				fuel+=amt;
				if(fuel<0) {
					fuel=0;
				}
			}
		}
	}
	public void changeEnergy(double amt) {
		if(amt>0) {
			if(energy<energyLimit) {
				energy+=amt;
				if(energy>energyLimit) {
					energy=energyLimit;
				}
			}
		}
		else {
			if(energy>0) {
				energy+=amt;
				if(energy<0) {
					energy=0;
				}
			}
		}
	}
	public void initializeShipVariables() {
		//fuel=0;
		fuelLimit=20000;
		speed=1;
		projectileSpeed=.5;
		fuelRegen=0;
		turnSpeed=.5;
		weaponCost=1;

		//energy=0;
		energyLimit=10000;
		energyRegen=0;
		conversionRate = 0;

		inventoryLimit=12;

		weapons.clear();
		partList.clear();
		edgeParts.clear();
		clearPartsByRadius();
	}
	public void addPartStats(Item part, int x, int y) {
		projectileSpeed+=part.projectileSpeed;
		//if(part.ID==GamePanel.fuel)
		//fuel+=part.fuel*.1;
		//if(part.ID==GamePanel.shipCore)
		//	fuel+=part.fuel;

		fuelLimit+=part.fuel;
		fuelRegen+=part.fuelRegen;
		energyLimit+=part.energy;
		energyRegen+=part.energyRegen;
		conversionRate+=part.conversionRate;
		inventoryLimit+=part.inventorySlots;

		if(speed+part.speed>=.5)
			speed+=part.speed;
		else {
			speed=.5;
		}
		turnSpeed+=part.turnSpeed;
		if(part.ID==GamePanel.laser||part.ID==GamePanel.beam) {
			weapons.add(part.weapon);
		}
	}
	public void generateShip() {
		ship = new BufferedImage(maxWidth*8, maxHeight*8, BufferedImage.TYPE_INT_ARGB);
		Graphics g = ship.getGraphics();
		radius=0;
		for(int i = 0; i<maxWidth;i++) {
			for(int j = 0; j<maxHeight;j++) {
				if(!(i==0||j==0||i==maxWidth-1||j==maxHeight-1)){
					if(parts[i-1][j]!=null&&parts[i+1][j]!=null&&parts[i][j-1]!=null&&parts[i][j+1]!=null) {
						g.setColor(new Color(50,50,50));
						g.fillRect(i*8, j*8, 8, 8);
					}
				}
				if(parts[i][j]!=null) {
					parts[i][j].updateLayout();
					Item current = parts[i][j];
					double distance = GamePanel.distanceBetween(i,j,24,24);
					if(distance>radius) radius = distance;
					partList.add(current);
					addPartStats(current, i, j);

					//for collision detection
					addPartToPartsByRadius(current, (int)distance);
					if(isEdge(i,j)) {
						edgeParts.add(current);
					}


					//draw the part on the ship
					if(current.ID!=GamePanel.hull&&current.ID!=GamePanel.citadel_hull)
						g.drawImage(GamePanel.parts[current.icon.x][current.icon.y],i*8,j*8,null);
					else
						g.drawImage(GamePanel.tiles[current.layout][current.textureY],i*8,j*8,null);

					//draw the damage to the part on the ship
					int alpha = (int)(double)(200.0*(1.0-(current.hp/current.maxHP))); if(alpha>255) alpha=255; if(alpha<0) alpha=0;
					g.setColor(new Color(30,0,0,alpha));
					g.fillRect(i*8, j*8, 8, 8);
				}

			}
		}
		speed = 30*(1.0-Math.pow(.99, speed));
		projectileSpeed = 20*(1.0-Math.pow(.99, projectileSpeed));
		weaponCost = (-30)*(1.0-Math.pow(.985, weapons.size()+(projectileSpeed)));
		if(fuel>fuelLimit) {
			fuel=fuelLimit;
		}
		if(id==0)
			getWeaponsLocations();
		g.dispose();
	}
	public void clearPartsByRadius() {
		partsByRadius.clear();
		for(int i = 0; i<36;i++) {
			partsByRadius.add(new ArrayList<Item>());
		}
	}
	public void addPartToPartsByRadius(Item part, double distance) {
		partsByRadius.get((int)distance).add(part);
	}
	public boolean isEdge(int x, int y) {
		if(x>0&&x<maxWidth-1&&y>0&&y<maxHeight-1) {
			if(parts[x-1][y-1]==null) return true;
			if(parts[x-1][y]==null) return true;
			if(parts[x-1][y+1]==null) return true;
			if(parts[x][y-1]==null) return true;
			if(parts[x][y+1]==null) return true;
			if(parts[x+1][y-1]==null) return true;
			if(parts[x+1][y]==null) return true;
			if(parts[x+1][y+1]==null) return true;
		}
		else {
			return true;
		}
		return false;
	}
	public void addPartToShip(int part, int x, int y) {
		//if the player is allowed to add additional parts to their ship
		if(partList.size()<partLimit) {
			//reset variables related to the ship
			initializeShipVariables();

			//set the part at it's desired position on the ship
			double angleFromCore = Math.atan2(((double)y-24.0),(((double)x-24.0)));
			double distanceFromCore = Math.sqrt(Math.pow(((double)x-24.0),2)+Math.pow(((double)y-24.0),2));
			parts[x][y] = new Item(part, distanceFromCore*8, angleFromCore,x,y, 1, this);

			//generate the ship
			generateShip();
		}
	}
	public void removePartFromShip(int part, int x, int y) {
		if(part!=GamePanel.shipCore) {
			initializeShipVariables();
			parts[x][y]=null;
			generateShip();
		}
		else {
			if(id==0) {
				for(int i = 0; i<10;i++) {
					int randX = (int)xpos+GamePanel.randomNumber(-AppletUI.windowWidth/2, AppletUI.windowWidth/2);
					int randY = (int)ypos+GamePanel.randomNumber(-AppletUI.windowHeight/2, AppletUI.windowHeight/2);
					GamePanel.combatText.add(new CombatText(randX, randY, "You Died", new Color(GamePanel.randomNumber(20, 255),0,0)));
					xpos = GamePanel.worldWidth*4;
					ypos = GamePanel.worldHeight*4;
					GamePanel.ships.clear();
				}
			}
			else {
				parts[x][y]=null;
				dead=true;
			}
		}
	}
	public void updateWeapons() {
		for(int i = 0; i<weapons.size();i++) {
			if(weapons.get(i).updates>0) {
				weapons.get(i).updates--;
			}
		}
	}
	public void decelerate(int a) {
		double amt = speed/a;
		if(xMomentum>0) {
			if(xMomentum-amt>0) {
				xMomentum-=amt;
			}
			else {
				xMomentum=0;
			}
		}
		else if(xMomentum<0) {
			if(xMomentum+amt<0) {
				xMomentum+=amt;
			}
			else {
				xMomentum=0;
			}
		}
		if(yMomentum>0) {
			if(yMomentum-amt>0) {
				yMomentum-=amt;
			}
			else {
				yMomentum=0;
			}
		}
		else if(yMomentum<0) {
			if(yMomentum+amt<0) {
				yMomentum+=amt;
			}
			else {
				yMomentum=0;
			}
		}
	}
	public void applyBuffs() {

		for(int i = 0; i<partList.size();i++) {
			Item module = partList.get(i);
			boolean found = false;
			if((module.ID==GamePanel.repair_module||module.ID==GamePanel.replacer_module)&&energy>module.energyCost) {
				//loop through buff range
				for(int j = module.x-module.buffRange; j<=module.x+module.buffRange;j++) {
					for(int k = module.y-module.buffRange; k<=module.y+module.buffRange;k++) {
						if(j>=0&&k>=0&&j<maxWidth&&k<maxHeight&&(energy-module.energyCost>1000)) {
							Item currentPart = parts[j][k];
							Item currentDesign = design[j][k];
							if(module.ID==GamePanel.repair_module) {
								if(currentPart!=null&&parts[j][k].hp<currentPart.maxHP) {
									currentPart.repair(module.repairAmt);
									changeEnergy(-module.energyCost);
									found = true;
								}
							}
							else if(module.ID==GamePanel.replacer_module) {
								if(parts[j][k]==null&&design[j][k]!=null&&GamePanel.randomNumber(1, 6000)<=10) {//10
									Item itemToRemove = new Item(currentDesign.ID,-1,-1,-1,-1, 1, null);
									if(removeItemFromInventory(itemToRemove)) {
										addPartToShip(design[j][k].ID,j,k);
										parts[j][k].takeDamage(parts[j][k].maxHP/2);
										changeEnergy(-module.energyCost);
										found = true;
										GamePanel.combatText.add(new CombatText((int)(xpos+(radius*8)+100),(int)ypos,parts[j][k].name+" replaced", Color.cyan));
									}
								}
							}
						}
					}
				}
				Graphics g = ship.getGraphics();
				g.clearRect(partList.get(i).x*8, partList.get(i).y*8, 8, 8);
				g.drawImage(GamePanel.parts[partList.get(i).icon.x][partList.get(i).icon.y], partList.get(i).x*8, partList.get(i).y*8, null);
				if(found) {
					g.setColor(new Color(0,255,255,100));
					g.fillRect(partList.get(i).x*8, partList.get(i).y*8, 8, 8);
				}
				g.dispose();
			}

		}

	}
	public void tryToFacePoint(double x, double y, boolean towards) {
		double angleToDest;
		if(towards) {
			angleToDest = Math.toDegrees(Math.atan2(((double)(ypos)-y),(((double)(xpos)-x)))-Math.PI);
		}
		else {
			angleToDest = Math.toDegrees(Math.atan2(((double)(ypos)-y),(((double)(xpos)-x)))-Math.PI)+180;
		}
		setAngle(angleToDest);
		//		if(angleToDest<180)
		//			setAngle(angleInDegrees-turnSpeed);
		//		else {
		//			setAngle(angleInDegrees+turnSpeed);
		//		}
		//		if(Math.abs(angleToDest-angleInDegrees)<=Math.abs(turnSpeed*2)) {
		//			setAngle(angleToDest);
		//		}
	}
	public double angleTo(double x, double y) {
		return Math.atan2(((double)y-ypos),(((double)x-xpos)));
	}
	public Point nearestBlock() {
		int x = ((int)xpos/8);
		int y = ((int)ypos/8);
		double shortestDistance = 99999;
		Point nearest = null;
		for(int i = x-4; i<=x+4;i++) {
			for(int j = y-4; i<=y+4;j++) {
				if(GamePanel.blocks.get(i, j)!=null) {
					if(nearest==null) {
						nearest=new Point(i,j);
					}
					else {
						double distance = GamePanel.distanceBetween(xpos,ypos,i*8,j*8);
						if(distance<shortestDistance) {
							shortestDistance=distance;
							nearest=new Point(i,j);
						}
					}		
				}
			}
		}
		return nearest;
	}
	public boolean nearHostileProjectile() {
		return false;
	}
	public boolean nearPlayer() {
		return true;
	}
	public ArrayList<Item> getEdges(double a, double b){
		ArrayList<Item> result = new ArrayList<Item>();
		if((int)a>(int)b) {
			for(int i=(int)b; i<=(int)a;i++) {
				result.addAll(partsByRadius.get(i));
			}
		}
		else {
			for(int i=(int)a; i<=(int)b;i++) {
				result.addAll(partsByRadius.get(i));
			}
		}
		return result;
	}

	public boolean checkForCollisionWithOtherShip(Ship ship) {
		boolean collided=false;
		double distanceBetween = GamePanel.distanceBetween(xpos, ypos, ship.xpos, ship.ypos);
		double minimumDistance = (radius*8)+(ship.radius*8);
		//if these two ships are close enough to collide
		if(distanceBetween<minimumDistance) {
			//double myInnerEdge = (radius-(distanceBetween/8));
			////double shipInnerEdge = (ship.radius-(distanceBetween/8));
			//if(myInnerEdge<0||shipInnerEdge<0) return false;
			ArrayList<Item> myEdgeParts = edgeParts;//getEdges(myInnerEdge, radius);//partList;//partsByRadius.get(myKey);
			ArrayList<Item> shipEdgeParts = ship.edgeParts;//ship.getEdges(shipInnerEdge, ship.radius);//ship.partList;//ship.partsByRadius.get(shipKey);
			if(myEdgeParts!=null&&shipEdgeParts!=null) {
				for(int i = 0; i<myEdgeParts.size();i++) {
					Item myCurrentPart = myEdgeParts.get(i);
					for(int j = 0; j<shipEdgeParts.size();j++) {
						Item theirCurrentPart = shipEdgeParts.get(j);
						double myX=(xpos)+(Math.cos(angleInRadians+myCurrentPart.angleFromCore+Math.PI/2)*myCurrentPart.distanceFromCore);
						double myY=(ypos)+(Math.sin(angleInRadians+myCurrentPart.angleFromCore+Math.PI/2)*myCurrentPart.distanceFromCore);
						double shipX=(ship.xpos)+(Math.cos(ship.angleInRadians+theirCurrentPart.angleFromCore+Math.PI/2)*theirCurrentPart.distanceFromCore);
						double shipY=(ship.ypos)+(Math.sin(ship.angleInRadians+theirCurrentPart.angleFromCore+Math.PI/2)*theirCurrentPart.distanceFromCore);
						//double distance = GamePanel.distanceBetween(myX, myY, shipX, shipY);
						double distance = Math.pow(((double)myX-shipX),2)+Math.pow(((double)myY-shipY),2);
						if(distance<64) {
							double dmgToShip = ((myCurrentPart.hardness*speed)/(theirCurrentPart.hardness*ship.speed))*5;
							double dmgToMe = ((theirCurrentPart.hardness*ship.speed)/(myCurrentPart.hardness*speed))*5;
							myCurrentPart.hp-=dmgToMe;
							theirCurrentPart.hp-=dmgToShip;
							if(myCurrentPart.hp<=0) {
								myCurrentPart.takeDamage(0);
								myEdgeParts.remove(myCurrentPart);
								if(i>0) {
									i--;
								}
							}
							else {
								myCurrentPart.takeDamage(0);
							}
							//System.out.println("here 2, "+i+","+j);
							if(theirCurrentPart.hp<=0) {
								if(theirCurrentPart.ID==GamePanel.shipCore) { 
									theirCurrentPart.takeDamage(0);
									if(j>0)j--;
									break;
								}
								else {
									theirCurrentPart.takeDamage(0);
									shipEdgeParts.remove(theirCurrentPart);
									if(j>0) {
										j--;
									}
								}
							}
							else {
								theirCurrentPart.takeDamage(0);
							}
							//System.out.println("here 3, "+i+","+j);
							collided=true;
						}
						else {
							//System.out.println(distance);
						}
					}
				}
				//}
			}
		}
		return collided;
	}
	public void update() {
		changeFuel(fuelRegen);
		changeEnergy(energyRegen);
		if(id==0) {
			move();
			decelerate(500);
			for(int i = 0; i<GamePanel.ships.size();i++) {
				Ship currentShip = GamePanel.ships.get(i);
				boolean collided = checkForCollisionWithOtherShip(currentShip);
				if(collided) {
					double angleToShip = angleTo(currentShip.xpos, currentShip.ypos);
					currentShip.xpos+=(Math.cos(angleToShip)*(xMomentum+currentShip.speed));
					currentShip.ypos+=(Math.sin(angleToShip)*(yMomentum+currentShip.speed));
					decelerate(50);
				}
			}

		}
		applyBuffs();
	}
	public void Draw(Graphics2D g) {

		updateWeapons();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform at = new AffineTransform();
		int centerX = ship.getWidth()/2;
		int centerY = ship.getHeight()/2;
		if(id==0) {//player
			at.translate((AppletUI.windowWidth/2)-centerX, (AppletUI.windowHeight/2)-centerY);
		}
		else {//not player
			//			Point nearestBlock = nearestBlock();
			//			//if distance to closest block is less than 50
			//			if(false||distanceBetween(xpos,ypos,nearestBlock.x,nearestBlock.y)<50) {
			//				//move away from closest block
			//				double angle = angleTo(nearestBlock.x,nearestBlock.y)-180;
			//				double newX = xpos+(Math.cos(angle)*speed);
			//				double newY = ypos+(Math.sin(angle)*speed);
			//			}
			//			//else if distance from closest projectile is less than 80
			//			else if(nearHostileProjectile()) {
			//				//move away from closest projectile
			//			}
			//			//else if distance from player is greater than 400
			//			else if(nearPlayer()==false) {
			//				//move towards the player
			//			}
			//			//else
			//			else {
			//				//move at an angle + or - 90 degrees from the player
			//				//shoot at the player
			//			}
			if(weapons.size()>0||id==8) {
				tryToFacePoint(GamePanel.player.xpos, GamePanel.player.ypos, true);
				if(id!=8&&GamePanel.distanceBetween(xpos,ypos,GamePanel.player.xpos,GamePanel.player.ypos)<AI_attack_range&&GamePanel.blocks.get((int)xpos/8, (int)ypos/8)==null)
					fireWeapons(false);
				else {
					double newX = xpos+(Math.cos(angleInRadians)*speed*AppletUI.delta);
					double newY = ypos+(Math.sin(angleInRadians)*speed*AppletUI.delta);
					xpos=newX;
					ypos=newY;
				}
			}
			else {
				tryToFacePoint(GamePanel.player.xpos, GamePanel.player.ypos, false);
				double newX = xpos+(Math.cos(angleInRadians)*speed*AppletUI.delta);
				double newY = ypos+(Math.sin(angleInRadians)*speed*AppletUI.delta);
				xpos=newX;
				ypos=newY;

			}
			for(int i = 0; i<GamePanel.ships.size();i++) {
				Ship currentShip = GamePanel.ships.get(i);
				if(currentShip!=this) {
					if(GamePanel.distanceBetween(xpos, ypos, currentShip.xpos, GamePanel.ships.get(i).ypos)<(radius*8)+(currentShip.radius*8)) {
						double angleToShip = angleTo(currentShip.xpos, currentShip.ypos);
						currentShip.xpos+=(Math.cos(angleToShip)*speed*AppletUI.delta);
						currentShip.ypos+=(Math.sin(angleToShip)*speed*AppletUI.delta);
					}
				}
			}
			at.translate((xpos-(int)GamePanel.player.xpos)+(AppletUI.windowWidth/2)-centerX,(ypos-(int)GamePanel.player.ypos)+(AppletUI.windowHeight/2)-centerY);
		}

		at.rotate(angleInRadians+(Math.PI/2),centerX,centerY);
		if(xpos>=GamePanel.worldWidth*8) xpos-=(GamePanel.worldWidth*8);
		if(ypos>=GamePanel.worldHeight*8) ypos-=(GamePanel.worldHeight*8);
		if(xpos<0) xpos+=(GamePanel.worldWidth*8);
		if(ypos<0) ypos+=(GamePanel.worldHeight*8);

		g.drawImage(ship, at, null);


	}
}
