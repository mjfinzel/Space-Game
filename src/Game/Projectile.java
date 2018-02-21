package Game;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Projectile {
	int ID;
	double distanceTravelled=0;
	double xpos, ypos;
	double angleInRadians;
	double speed;
	double range;
	boolean collided = false;
	int faction=0;//0=player, 1=enemy
	double originX;
	double originY;
	double damageMultiplier=1.0;

	public Projectile(int id, int x, int y, double angleInRadians, double r, int f) {
		ID = id;
		originX=x;
		originY=y;
		xpos = x;
		ypos = y;
		faction = f;
		this.angleInRadians=angleInRadians;
		if(ID==0) {
			speed=5+GamePanel.player.projectileSpeed;
			if(faction==0)
				range=2000;//=r
			else {
				range=2000;
			}
		}
		else if(id==1) {
			speed=6+(double)GamePanel.player.partList.size()/100.0;
			range=2000;
		}
		else if(id==2) {
			speed = 7;
			range = 800;
			damageMultiplier=8.0;
		}
	}
	public void move() {
		//move the projectile
		double newX = xpos+(Math.cos(angleInRadians)*speed*AppletUI.delta);
		double newY = ypos+(Math.sin(angleInRadians)*speed*AppletUI.delta);
		if(speed>range-distanceTravelled) {
			newX = xpos+(Math.cos(angleInRadians)*(range-distanceTravelled));
			newY = ypos+(Math.sin(angleInRadians)*(range-distanceTravelled));
		}
		xpos = newX;
		ypos = newY;
		distanceTravelled+=(speed*AppletUI.delta);

		//if the projectile has collided with a block
		if(GamePanel.blocks.get((int)xpos/8, (int)ypos/8)!=null&&((distanceTravelled>32)||faction==0)) {
			//if the projectile is faction 0 or the faction is 1 and it hits ice or hull
			if(faction==0||(faction==1&&GamePanel.blocks.get((int)xpos/8, (int)ypos/8).ID!=GamePanel.ice&&GamePanel.blocks.get((int)xpos/8, (int)ypos/8).ID!=GamePanel.hull)) {

				GamePanel.blocks.get((int)xpos/8, (int)ypos/8).takeDamageAndCheckIfDead(speed*damageMultiplier, ID);
				if(GamePanel.blocks.get((int)xpos/8, (int)ypos/8)!=null) {
					GamePanel.blocks.get((int)xpos/8, (int)ypos/8).isEdge=true;
					//if the block is a sentry
					if(GamePanel.blocks.get((int)xpos/8, (int)ypos/8).ID==GamePanel.sentry) {
						if(speed==5+GamePanel.player.projectileSpeed)
							GamePanel.combatText.add(new CombatText((int)xpos, (int)(ypos), ""+(int)speed, new Color(255,100,0)));
						else
							GamePanel.combatText.add(new CombatText((int)xpos, (int)(ypos), ""+(int)speed, new Color(255,200,0)));
					}
					//if the block at it's position was destroyed
					if(GamePanel.blocks.get((int)xpos/8, (int)ypos/8).hp<=0) {
						if(GamePanel.blocks.get((int)xpos/8, (int)ypos/8).ID==GamePanel.ice&&faction==0) {
							int amt = GamePanel.randomNumber(25, 35);
							GamePanel.player.changeFuel(amt*20);
							GamePanel.combatText.add(new CombatText((int)xpos, (int)ypos, "+"+amt*20+"f", new Color(230,230,230)));
						}
						if(GamePanel.blocks.get((int)xpos/8, (int)ypos/8).ID==GamePanel.sentry&&faction==0) {
							int amt = GamePanel.randomNumber(3, 5);
							GamePanel.player.changeEnergy(amt*10);
							GamePanel.combatText.add(new CombatText((int)xpos, (int)ypos, "+"+amt*10+"e", Color.pink));
						}

						GamePanel.blocks.set((int)xpos/8, (int)ypos/8, null);

						for(int i = (int)(xpos/8)-1; i<=(int)(xpos/8)+1;i++) {
							for(int j = (int)(ypos/8)-1; j<=(int)(ypos/8)+1;j++) {
								if(GamePanel.blocks.get(i,j)!=null) {
									GamePanel.blocks.get(i,j).isEdge = true;
									GamePanel.blocks.get(i,j).updateLayout();
								}
							}
						}
					}
					//				else if(faction==0) {
					//					GamePanel.explosions.add(new Explosion((int)xpos/8, (int)ypos/8, faction,20));
					//					collided=true;
					//				}
				}
			}
			if(GamePanel.blocks.get((int)xpos/8, (int)ypos/8)!=null) {
				if(ID==2) {
					speed-=(GamePanel.blocks.get((int)xpos/8, (int)ypos/8).hardness/8);
				}
				else {
					speed-=GamePanel.blocks.get((int)xpos/8, (int)ypos/8).hardness;
				}

				if(speed<2)
					collided=true;
			}
			//new Explosion(xpos/8,ypos/8,faction,40);collided=true;

		}
		else {//did not hit a block

			//if this is an enemy projectile
			if(faction!=0) {
				double distanceBetween = GamePanel.distanceBetween(GamePanel.player.xpos, GamePanel.player.ypos, xpos, ypos);
				if(distanceBetween<GamePanel.player.radius*8) {
					ArrayList<Item> possiblyHit = GamePanel.player.getEdges((distanceBetween/8)+1, (distanceBetween/8)-1);
					for(int i = 0; i<possiblyHit.size();i++) {
						Item currentPart = possiblyHit.get(i);
						double xOffset=(GamePanel.player.xpos)+(Math.cos(GamePanel.player.angleInRadians+currentPart.angleFromCore+Math.PI/2)*currentPart.distanceFromCore);
						double yOffset=(GamePanel.player.ypos)+(Math.sin(GamePanel.player.angleInRadians+currentPart.angleFromCore+Math.PI/2)*currentPart.distanceFromCore);
						//get distance from this part
						//GamePanel.combatText.add(new CombatText((int)xOffset,(int)yOffset,"+",Color.pink));
						double distance = Math.pow(((double)xpos-xOffset),2)+Math.pow(((double)ypos-yOffset),2);
						if(distance<=64) {
							double oldSpeed=new Double(speed);
							if(ID==2) {
								speed-=(currentPart.hardness/8);
							}
							else {
								speed-=currentPart.hardness;
							}
							if(speed<2)
								collided=true;
							currentPart.takeDamage(oldSpeed*damageMultiplier);
							GamePanel.combatText.add(new CombatText((int)xpos,(int)ypos,(int)(double)(oldSpeed*damageMultiplier)+"",Color.red));
							break;
						}
					}
				}
			}
			else {
				for(int j = 0; j<GamePanel.ships.size();j++) {
					if(GamePanel.distanceBetween(xpos, ypos, GamePanel.ships.get(j).xpos, GamePanel.ships.get(j).ypos)<GamePanel.ships.get(j).radius*8) {
						for(int i = 0; i<GamePanel.ships.get(j).partList.size();i++) {
							double xOffset=(GamePanel.ships.get(j).xpos)+(Math.cos(GamePanel.ships.get(j).angleInRadians+GamePanel.ships.get(j).partList.get(i).angleFromCore+Math.PI/2)*GamePanel.ships.get(j).partList.get(i).distanceFromCore);
							double yOffset=(GamePanel.ships.get(j).ypos)+(Math.sin(GamePanel.ships.get(j).angleInRadians+GamePanel.ships.get(j).partList.get(i).angleFromCore+Math.PI/2)*GamePanel.ships.get(j).partList.get(i).distanceFromCore);
							//get distance from this part
							//GamePanel.combatText.add(new CombatText((int)xOffset,(int)yOffset,"+",Color.pink));
							double distance = Math.pow(((double)xpos-xOffset),2)+Math.pow(((double)ypos-yOffset),2);
							if(distance<=64) {
								double oldSpeed=new Double(speed);
								if(ID==2) {
									speed-=(GamePanel.ships.get(j).partList.get(i).hardness/8);
								}
								else {
									speed-=GamePanel.ships.get(j).partList.get(i).hardness;
								}
								if(speed<2)
									collided=true;
								GamePanel.ships.get(j).partList.get(i).takeDamage(oldSpeed*damageMultiplier);
								GamePanel.combatText.add(new CombatText((int)xpos,(int)ypos,(int)(double)(oldSpeed*damageMultiplier)+"",Color.yellow));
								break;
							}
						}
					}
				}
			}
		}
	}

	public void Draw(Graphics2D g) {
		if(ID==0) {
			if(faction==0)
				g.setColor(new Color(0,255,0,(int)((1.0-(distanceTravelled/range))*40)));
			else
				g.setColor(new Color(255,0,0,(int)((1.0-(distanceTravelled/range))*40)));
			g.drawLine(((int)originX-(int)GamePanel.player.xpos)+(AppletUI.windowWidth/2), ((int)originY-(int)GamePanel.player.ypos)+(AppletUI.windowHeight/2), ((int)xpos-(int)GamePanel.player.xpos)+(AppletUI.windowWidth/2), ((int)ypos-(int)GamePanel.player.ypos)+(AppletUI.windowHeight/2));
		}
		move();
		if(ID==0) {
			g.drawImage(GamePanel.projectileImages[faction][0], ((int)xpos-(int)GamePanel.player.xpos)+(AppletUI.windowWidth/2), ((int)ypos-(int)GamePanel.player.ypos)+(AppletUI.windowHeight/2), null);
		}
		else if(ID==1) {
			g.drawImage(GamePanel.projectileImages[1][0], ((int)xpos-(int)GamePanel.player.xpos)+(AppletUI.windowWidth/2), ((int)ypos-(int)GamePanel.player.ypos)+(AppletUI.windowHeight/2), null);
		}
		else if(ID==2) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			AffineTransform at = new AffineTransform();
			int centerX = 0;
			int centerY = 4;
			at.translate(((int)originX-(int)GamePanel.player.xpos)+(AppletUI.windowWidth/2)-centerX, ((int)originY-(int)GamePanel.player.ypos)+(AppletUI.windowHeight/2)-centerY);
			at.rotate(angleInRadians,centerX,centerY);
			at.scale(((distanceTravelled/range)), 1.0);


			g.drawImage(GamePanel.beamImage[0][faction], at, null);
		}
		//Graphics l = GamePanel.lightMask.getGraphics();
//		int x = ((int)xpos-(int)GamePanel.player.xpos)+(AppletUI.windowWidth/2);
//		int y = ((int)ypos-(int)GamePanel.player.ypos)+(AppletUI.windowHeight/2);
//		l.drawImage(GamePanel.light, (x/4)-3, (y/4)-3,7,7, null);
//		l.dispose();
		GamePanel.addLight(xpos, ypos, 7, 7,false);
	}
}
