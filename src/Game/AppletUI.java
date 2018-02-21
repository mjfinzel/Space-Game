package Game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;
import javax.swing.JPanel;






public class AppletUI extends JFrame{
	public BufferStrategy myStrategy;
	private static final long serialVersionUID = -6215774992938009947L;
	public static final long milisecInNanosec = 1000000L;
	public static final long secInNanosec = 1000000000L;
	public static int GAME_FPS = 120;
	private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
	public long lastDrawTime = System.currentTimeMillis();
	public static int windowWidth=1920;
	public static int windowHeight=1080;
	public static double delta = 1.0;
	GamePanel drawPanel;

	//detect the fps the game is running at
	int currentFPS = 0;
	double avgXpos = 0;
	double avgYpos = 0;
	static int fps = 0;
	//time since last second fps was measured
	long lastFPStimeUpdate = System.currentTimeMillis();

	long lastUpdateTime = System.nanoTime();

	Controller ctrl;
	public static void main(String[] args){
		AppletUI f = new AppletUI ();
		f.setSize(windowWidth,windowHeight);
	}
	public AppletUI() {

		setIgnoreRepaint(true);
		setTitle("Space Game - By:mfinzel5@gmail.com");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//setSize(windowWidth,windowHeight);
		this.setExtendedState(Frame.MAXIMIZED_BOTH);  
		this.setUndecorated(true);  
		setVisible(true);
		this.setFocusable(true);
		
		createBufferStrategy(2);
		myStrategy = getBufferStrategy();
		
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		drawPanel = new GamePanel();
		drawPanel.setBackground(Color.BLACK);
		drawPanel.setIgnoreRepaint(true);
		
		
		
		ctrl = new Controller();
		
		ctrl.setGamePanel(drawPanel);
		this.addKeyListener(ctrl);
		this.addMouseListener(ctrl);
		this.addMouseMotionListener(ctrl);
		
		pane.add(drawPanel);
		
		//We start game in new thread.
		Thread gameThread = new Thread() {			
			public void run(){
				gameLoop();
			}
		};
		gameThread.start();
		//BattleshorePanel.game_is_running=true;
	}
	int delay = 0;
	public void gameLoop(){

		// This variables are used for calculating the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
		long beginTime, timeTaken, timeLeft;
		int ticks = 0;
		while(true)
		{	
			//System.out.println("looping!");
			beginTime = System.nanoTime();
			Controller.checkKeys();
			Graphics2D g = (Graphics2D) myStrategy.getDrawGraphics();
			
			drawPanel.Draw(g);
			myStrategy.show();
			g.setBackground(Color.black);
			g.clearRect(0,0,getWidth(),getHeight());
			g.dispose();

			if(lastFPStimeUpdate+1000>=System.currentTimeMillis()){
				currentFPS++;
			}
			else{
				fps=currentFPS;
				currentFPS=0;
				lastFPStimeUpdate=System.currentTimeMillis();
			}
			if(fps>0)
				delta = (double)(GAME_FPS/2.0)/(double)fps;
			else
				delta = (double)(GAME_FPS/2.0);
			//while( now - lastUpdateTime > TIME_BETWEEN_UPDATES
			lastUpdateTime = System.nanoTime();


			// Here we calculate the time that defines for how long we should put threat to sleep to meet the GAME_FPS.
			timeTaken = System.nanoTime() - beginTime;

			//System.out.println("time taken: "+timeTaken+"game update period"+GAME_UPDATE_PERIOD+" delta: "+delta);
			//System.out.println("Took "+timeTaken+" nanoseconds to update all");
			timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
			// If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
			if (timeLeft < 10) 
				timeLeft = 10; //set a minimum
			try {
				//Provides the necessary delay and also yields control so that other thread can do work.
				Thread.sleep(timeLeft);
			} catch (InterruptedException ex) { }

		}
	}

	public void Draw(Graphics g){

	}

}

