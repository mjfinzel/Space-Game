package Game;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class LootText {
	Item item;
	int xpos;
	int ypos;
	int fontSize = 0;
	int duration = 8000;//1500
	int growthRate = 1;
	int growthLimit = 18;
	int updates = 0;
	long appearTime;
	Color color;
	int amount;
	public LootText(Item item){
		this.item = item;

		color = Color.red;
		color = Rarity.rarityColor[item.rarity];
		amount = item.amount;
		appearTime = System.currentTimeMillis();
	}
	public void Draw(Graphics g, int pos){
		long timePassed = System.currentTimeMillis()-appearTime;
		if(updates%growthRate==0){
			if(fontSize<growthLimit){
				fontSize++;
			}
			else if(timePassed>duration||pos>10){
				GamePanel.lootText.remove(this);
			}
		}
		String text = item.name;
		if(amount>1) {
			text = text+" ("+amount+")";
		}
		Font font = new Font("Iwona Heavy",Font.BOLD,fontSize);
		g.setFont(font);
		g.setColor(this.color);
		FontMetrics metrics = g.getFontMetrics();
		//center the text on the position specified
		int x = ((AppletUI.windowWidth/2)-(metrics.stringWidth(text)/2));
		int y = (AppletUI.windowHeight/2)-((int)GamePanel.player.radius*8);
		g.drawString(text, x, y-(pos*(growthLimit+5)));
		updates++;
	}
}
