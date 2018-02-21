package Game;

public class Weapon {
	int ID;
	double distanceFromCore;
	double angleFromCore;
	int firingRate;
	int updates = 0;
	Ship ship;
	double costMultiplier=1.0;
	int xpos;
	int ypos;
	public Weapon(int id, double d, double a, Ship ship, int x, int y) {
		ID = id;
		this.ship=ship;
		distanceFromCore=d;
		angleFromCore=a;
		xpos = x;
		ypos = y;
		if(id==0) {
			firingRate=50;
		}
		if(id==2) {
			firingRate=250;
			costMultiplier=10;
		}
	}
	public void fire(double angleInRadians, boolean focusFire) {
		if(updates==0&&ship.energy>Math.abs(ship.weaponCost*costMultiplier)) {
			double xOffset=(Math.cos(angleInRadians+angleFromCore+(Math.PI/2))*distanceFromCore);
			double yOffset=(Math.sin(angleInRadians+angleFromCore+(Math.PI/2))*distanceFromCore);
			double angleToMouse;
			double distance;
			double x1 = Controller.mousePosition.x-(Math.cos(GamePanel.player.angleInRadians+GamePanel.player.angleToWeapons)*GamePanel.player.distanceToWeapons);
			double y1 = Controller.mousePosition.y-(Math.sin(GamePanel.player.angleInRadians+GamePanel.player.angleToWeapons)*GamePanel.player.distanceToWeapons);

			if(ship.id==0) {
				if(focusFire==false) {
					angleToMouse = Math.atan2(((double)(AppletUI.windowHeight/2)-y1),(((double)(AppletUI.windowWidth/2)-x1)))-Math.PI;
					//angleToMouse=angleInRadians+GamePanel.player.angleToWeapons;
					distance=1200;
				}
				else {
					distance = Math.sqrt(Math.pow(((double)(double)((AppletUI.windowWidth/2)+xOffset)-Controller.mousePosition.x),2)+Math.pow(((double)(double)((AppletUI.windowHeight/2)+yOffset)-Controller.mousePosition.y),2));
					angleToMouse = Math.atan2(((double)((AppletUI.windowHeight/2)+yOffset)-Controller.mousePosition.y),(((double)((AppletUI.windowWidth/2)+xOffset)-Controller.mousePosition.x)))-Math.PI;
				}
				if(ID==0) {
					GamePanel.projectiles.add(new Projectile(0, (int)(GamePanel.player.xpos+xOffset), (int)(GamePanel.player.ypos+yOffset), angleToMouse, distance,0));
					double x = (double)(GamePanel.player.xpos)+(Math.cos(angleToMouse)*distance);
					double y = (double)(GamePanel.player.ypos)+(Math.sin(angleToMouse)*distance);
					//Explosion temp = new Explosion(x/8,y/8, ship.id,6);
				}
				else {
					if(ship.xMomentum==0&&ship.yMomentum==0)
						GamePanel.projectiles.add(new Projectile(2, (int)(GamePanel.player.xpos+xOffset), (int)(GamePanel.player.ypos+yOffset), angleToMouse, distance,0));
				}

			}
			else {
				GamePanel.projectiles.add(new Projectile(ID, (int)(ship.xpos+xOffset), (int)(ship.ypos+yOffset), ship.angleInRadians, -1,1));
			}
			ship.changeEnergy(ship.weaponCost*costMultiplier);
			updates=firingRate-GamePanel.randomNumber(0, 10);

		}
	}
}
