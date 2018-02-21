package Game;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;




import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;



public class Controller extends JPanel implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//public static Unit currentUnit;
	public static String currentTileName = "No tiles selected";
	private JPanel gamePanel;
	long starttime = 0;
	private static boolean[] keyboardState = new boolean[525];
	public static boolean[] wasPressed = new boolean[525];
	public static boolean mouseDragging = false;
	public static boolean leftMousePressed = false;
	public static boolean rightMousePressed = false;
	public static boolean lmbWasPressed=false;
	public static boolean rmbWasPressed=false;
	static Point mousePosition = new Point(0,0);

	public Controller(){
		this.setDoubleBuffered(true);

	}
	public static boolean keyboardKeyState(int key)
	{
		return keyboardState[key];
	}
	public void setGamePanel(JPanel panelRef) {
		gamePanel = panelRef;
		gamePanel.addKeyListener(this);
		gamePanel.addMouseListener(this);
		gamePanel.addMouseMotionListener(this);
		gamePanel.addMouseWheelListener(this);
	}
	public void setGamePanelPos(int x, int y){
		gamePanel.setAlignmentX(x);
		gamePanel.setAlignmentX(y);
	}
	public void updateAll(){
		if (gamePanel != null)
			gamePanel.getParent().repaint();
	}
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mousePosition.x = arg0.getX();
		mousePosition.y = arg0.getY();
	}
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mousePosition.x = arg0.getX();
		mousePosition.y = arg0.getY();
	}
	public void mouseClicked(MouseEvent arg0) {
		mousePosition.x = arg0.getX();
		mousePosition.y = arg0.getY();
	}
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mousePosition.x = arg0.getX();
		mousePosition.y = arg0.getY();
	}
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mousePosition.x = arg0.getX();
		mousePosition.y = arg0.getY();
	}
	public void mousePressed(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON1){
			leftMousePressed=true;
			if(GamePanel.buildMode==false) lmbWasPressed=true;
		}
		else if(arg0.getButton() == MouseEvent.BUTTON3){
			rightMousePressed=true;
			if(GamePanel.buildMode==false) rmbWasPressed=true;
		}

		mousePosition.x = arg0.getX();
		mousePosition.y = arg0.getY();

		int width = 350;
		int leftEdge = AppletUI.windowWidth-width;
		//		if(arg0.getButton() == MouseEvent.BUTTON1){//left click
		//			if(GamePanel.buildMode) {
		//				if(GamePanel.cursorItem==null) {
		//					for(int i = 0; i<GamePanel.player.inventory.size();i++) {
		//						if(GamePanel.player.inventory.get(i)!=null) {
		//							if(Controller.mousePosition.x>=leftEdge+4+((i%16)*20) && Controller.mousePosition.x<=leftEdge+4+((i%16)*20)+16) {
		//								if(Controller.mousePosition.y>=4+((i/16)*20) && Controller.mousePosition.y<=4+((i/16)*20)+16) {
		//									GamePanel.cursorItem=GamePanel.player.inventory.get(i);
		//								}
		//							}
		//						}
		//					}
		//				}
		//				else {
		//					for(int i = 0; i<GamePanel.player.maxWidth; i++) {
		//						for(int j = 0; j<GamePanel.player.maxHeight;j++) {
		//							if(Controller.mousePosition.x>=0+(i*16)&&Controller.mousePosition.x<0+(i*16)+16){
		//								if(Controller.mousePosition.y>=0+(j*16)&&Controller.mousePosition.y<0+(j*16)+16){
		//									if(GamePanel.player.parts[i][j]==-1) {
		//										GamePanel.player.addPartToShip(GamePanel.cursorItem.ID, i, j);
		//									}
		//								}
		//							}
		//						}
		//					}
		//				}
		//			}
		//		}
		//		else if(arg0.getButton() == MouseEvent.BUTTON3){//right click
		//			if(GamePanel.cursorItem!=null) {
		//				GamePanel.cursorItem=null;
		//			}
		//			else {
		//				for(int i = 0; i<GamePanel.player.maxWidth; i++) {
		//					for(int j = 0; j<GamePanel.player.maxHeight;j++) {
		//						if(Controller.mousePosition.x>=0+(i*16)&&Controller.mousePosition.x<0+(i*16)+16){
		//							if(Controller.mousePosition.y>=0+(j*16)&&Controller.mousePosition.y<0+(j*16)+16){
		//								GamePanel.player.removePartFromShip(GamePanel.player.parts[i][j], i, j);
		//							}
		//						}
		//					}
		//				}
		//			}
		//		}

	}
	public void mouseReleased(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON1){
			leftMousePressed=false;
		}
		else if(arg0.getButton() == MouseEvent.BUTTON3){
			rightMousePressed=false;
		}
		mousePosition.x = arg0.getX();
		mousePosition.y = arg0.getY();
	}
	public void keyPressed(KeyEvent e) {
		keyboardState[e.getKeyCode()] = true;
		wasPressed[e.getKeyCode()] = true;
		if(e.getKeyCode()==KeyEvent.VK_B) {
			if(GamePanel.buildMode==true) {
				GamePanel.buildMode=false;
				GamePanel.player.storeDesign();
			}
			else {
				GamePanel.buildMode=true;
			}
		}
		if(e.getKeyCode()==KeyEvent.VK_H) {
			for(int i = 0; i<wasPressed.length;i++) {
				wasPressed[i]=false;
			}
			lmbWasPressed=false;
			rmbWasPressed=false;
		}
		if(e.getKeyCode()==KeyEvent.VK_G) {
			if(GamePanel.godMode==true) {
				GamePanel.godMode=false;
			}
			else {
				GamePanel.godMode=true;
			}
		}
		if(GamePanel.godMode) {
			if(e.getKeyCode()==KeyEvent.VK_K) {
				GamePanel.player.printLayout();
			}
			if(e.getKeyCode()==KeyEvent.VK_0) {
				GamePanel.player.addCheaterItems();
			}
		}
	}
	public void keyReleased(KeyEvent e) {
		keyboardState[e.getKeyCode()] = false;

	}
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	public void mouseWheelMoved(MouseWheelEvent e) {
		//scale down
		//move the center of the map to the mouse position before the scaling

		if(e.getWheelRotation()<0){//mouse wheel moved up (zoom in)
			if(GamePanel.cameraScale<5){
				GamePanel.cameraScale=GamePanel.cameraScale+.02;
			}
		}
		else{//mouse wheel moved down (zoom out)
			if(GamePanel.cameraScale>.04){
				GamePanel.cameraScale=GamePanel.cameraScale-.02;
			}
		}
	}
	public static void checkKeys(){
		if(keyboardState[KeyEvent.VK_W]==true) {
			//GamePanel.player.move(1);
			if(GamePanel.player.fuel>0) {
				double newXmom = GamePanel.player.xMomentum + Math.cos(GamePanel.player.angleInRadians)*(GamePanel.player.speed/100.0)*1;
				double newYmom = GamePanel.player.yMomentum + Math.sin(GamePanel.player.angleInRadians)*(GamePanel.player.speed/100.0)*1;

				if(Math.pow(newXmom,2)+Math.pow(GamePanel.player.yMomentum,2)<=Math.sqrt(GamePanel.player.speed))
					GamePanel.player.xMomentum = newXmom;
				else
					GamePanel.player.xMomentum = Math.cos(GamePanel.player.angleInRadians)*(GamePanel.player.speed)*1;

				if(Math.pow(GamePanel.player.xMomentum,2)+Math.pow(newYmom,2)<=Math.sqrt(GamePanel.player.speed))
					GamePanel.player.yMomentum = newYmom;
				else
					GamePanel.player.yMomentum = Math.sin(GamePanel.player.angleInRadians)*(GamePanel.player.speed)*1;
			}
			GamePanel.player.changeFuel(-GamePanel.player.speed);
		}
		else if(keyboardState[KeyEvent.VK_S]==true) {
			if(GamePanel.player.fuel>0) {
				double newXmom = GamePanel.player.xMomentum + Math.cos(GamePanel.player.angleInRadians)*(GamePanel.player.speed/100.0)*-1;
				double newYmom = GamePanel.player.yMomentum + Math.sin(GamePanel.player.angleInRadians)*(GamePanel.player.speed/100.0)*-1;

				if(Math.pow(newXmom,2)+Math.pow(GamePanel.player.yMomentum,2)<=Math.sqrt(GamePanel.player.speed))
					GamePanel.player.xMomentum = newXmom;
				else
					GamePanel.player.xMomentum = Math.cos(GamePanel.player.angleInRadians)*(GamePanel.player.speed)*-1;

				if(Math.pow(GamePanel.player.xMomentum,2)+Math.pow(newYmom,2)<=Math.sqrt(GamePanel.player.speed))
					GamePanel.player.yMomentum = newYmom;
				else
					GamePanel.player.yMomentum = Math.sin(GamePanel.player.angleInRadians)*(GamePanel.player.speed)*-1;
			}
			GamePanel.player.changeFuel(-GamePanel.player.speed);
		}
		if(keyboardState[KeyEvent.VK_SPACE]==true) {
			GamePanel.player.decelerate(60);
			GamePanel.player.changeFuel(-1);
		}
		if(keyboardState[KeyEvent.VK_A]==true) {
			GamePanel.player.setAngle(GamePanel.player.angleInDegrees-GamePanel.player.turnSpeed);
		}
		else if(keyboardState[KeyEvent.VK_D]==true) {
			GamePanel.player.setAngle(GamePanel.player.angleInDegrees+GamePanel.player.turnSpeed);
		}
		if(GamePanel.buildMode) {
			int width = 764;
			int w = 21;
			int leftEdge = AppletUI.windowWidth-width;
			if(leftMousePressed) {
				if(GamePanel.buildMode) {
					for(int i = 0; i<GamePanel.player.inventory.size();i++) {
						if(GamePanel.player.inventory.get(i)!=null) {
							//leftEdge+3+((i%w)*36), 30+((i/w)*48), 34, 46
							if(Controller.mousePosition.x>=leftEdge+3+((i%w)*36) && Controller.mousePosition.x<=leftEdge+3+((i%w)*36)+32) {
								if(Controller.mousePosition.y>=30+((i/w)*48) && Controller.mousePosition.y<=30+((i/w)*48)+32) {
									GamePanel.cursorItem=GamePanel.player.inventory.get(i);
								}
							}
						}
					}

					if(GamePanel.cursorItem!=null) {
						for(int i = 0; i<GamePanel.player.maxWidth; i++) {
							for(int j = 0; j<GamePanel.player.maxHeight;j++) {
								if(Controller.mousePosition.x>=0+(i*16)&&Controller.mousePosition.x<0+(i*16)+16){
									if(Controller.mousePosition.y>=0+(j*16)&&Controller.mousePosition.y<0+(j*16)+16){
										if(GamePanel.cursorItem!=null&&GamePanel.player.parts[i][j]==null) {
											if((i>0&&GamePanel.player.parts[i-1][j]!=null)||(i<GamePanel.player.maxWidth-1&&GamePanel.player.parts[i+1][j]!=null)||(j>0&&GamePanel.player.parts[i][j-1]!=null)||(j<GamePanel.player.maxWidth-1&&GamePanel.player.parts[i][j+1]!=null)) {
												if(GamePanel.cursorItem.amount>0&&GamePanel.cursorItem.maxHP>0) {
													GamePanel.player.addPartToShip(GamePanel.cursorItem.ID, i, j);
													GamePanel.cursorItem.amount-=1;
													if(GamePanel.cursorItem.amount==0) {
														GamePanel.player.inventory.remove(GamePanel.cursorItem);
														GamePanel.cursorItem=null;
													}
												}
											}
										}
									}
								}
							}
						}
					}

				}
			}
			else if(rightMousePressed) {
				for(int i = 0; i<GamePanel.player.maxWidth; i++) {
					for(int j = 0; j<GamePanel.player.maxHeight;j++) {
						if(Controller.mousePosition.x>=0+(i*16)&&Controller.mousePosition.x<0+(i*16)+16){
							if(Controller.mousePosition.y>=0+(j*16)&&Controller.mousePosition.y<0+(j*16)+16){
								if(GamePanel.player.parts[i][j]!=null) {
									Item tmp = GamePanel.player.parts[i][j];
									int exists = GamePanel.player.getStackableInventoryItem(tmp, false);
									if(GamePanel.player.inventory.size()<(GamePanel.player.inventoryLimit-GamePanel.player.parts[i][j].inventorySlots)||exists!=-1) {
										if(GamePanel.player.parts[i][j].ID!=GamePanel.shipCore)
											GamePanel.player.addItemToInventory(GamePanel.player.parts[i][j]);
										GamePanel.player.removePartFromShip(GamePanel.player.parts[i][j].ID, i, j);
										int index = GamePanel.player.getStackableInventoryItem(tmp,false);
										if(index>=0)
											GamePanel.cursorItem=GamePanel.player.inventory.get(index);
									}
								}

							}
						}
					}
				}
			}


		}
		else {
			if(leftMousePressed) {
				GamePanel.player.fireWeapons(false);
			}
			else if(rightMousePressed) {
				GamePanel.player.fireWeapons(true);
			}
		}
	}
}
