package bots;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.StringTokenizer;

import arena.BattleBotArena;
import arena.BotInfo;
import arena.Bullet;

/**
 * The SentryBot just stays put and shoots randomly. It yells at anything that gets too close to it.
 *
 * @author Sam Scott
 * @version 1.0 (March 3, 2011)
 */
public class AsseelBot extends Bot {

	/**
	 * Image for drawing
	 */
	private Image image = null;
	/**
	 * Next message to send
	 */
	private String nextMessage;
	/**
	 * My name
	 */
	private String name;

	/**
	 * Name of my image
	 */
	public String[] imageNames()
	{
		String[] images = {"drake.png"};
		return images;
	}

	/**
	 * Save my image
	 */
	public void loadedImages(Image[] images)
	{
		if (images != null && images.length > 0)
			image = images[0];
	}

	/**
	 * This is your main method.  Decide on what your bot will do here. Please use the constants
	 * in the Arena class.
	 */
	public int getMove(BotInfo me, boolean shotOK, BotInfo[] liveBots, BotInfo[] deadBots, Bullet[] bullets)
	{
		int res = dodge(me, bullets); 

		if (res != BattleBotArena.STAY) { // if the dodge method is not telling my bot to stay in place (meaning there is danger)
			System.out.println("dodge");
			return dodge(me, bullets); // dodge the bullet
		}
		else { // else there is no danger
			System.out.println("attack");
			return attack(me, liveBots); // attack a bot
		}
	}

	/** This method is designed to calculate distance using pythag (distance formula)
	 * @param x1: x coordinates of me
	 * @param y1: y coordinates of me
	 * @param x2: x coordinates of bullet
	 * @param y2: y coordinates of bullet
	 * @return distance between me and the bullet
	 */
	public double getDistance(double x1, double y1, double x2, double y2) {
		return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
	}

	/** This method is designed to find the most dangerous bullet to my bot
	 * @param me: my bot
	 * @param bullets: array of bullets that are present in the map
	 * @return Bullet of the most dangerous bullet
	 */
	public Bullet mostDangerous(BotInfo me, Bullet[] bullets) {
		double dangerDist = 250; // this is the defaulted danger zone - the bullet is deemed dangerous when it is less that 200 pixels away
		Bullet dangerBullet = null; // set the most dangerous bullet to null to start off; if it is null then the bot will attack instead 

		for (int i = 0; i < bullets.length; i++) { // for loop which loops through all the bullets on the map
			double foc = getDistance(me.getX(), me.getY(), bullets[i].getX(), bullets[i].getY()); // get the distance between me and the focused bullet and save it in a variable
			if (foc < dangerDist) { // if the bullet at i has a distance less than the most dangerous (shortest) distance
				dangerDist = foc; // set the most dangerous distance to that distance
				dangerBullet = bullets[i]; // set the most dangerous bullet to bullets at i
			}
		}

		return dangerBullet; // return most dangerous bullet
	}

	/** This method is designed to designed to help my bot dodge the bullets which deemed to be the most
	 * dangerous
	 * @param me: my bot
	 * @param bullets: array of bullets on the map
	 * @return int declaring to move up, down, left or right
	 */
	public int dodge (BotInfo me, Bullet[] bullets) {
		Bullet bullet = mostDangerous(me, bullets); // find the most dangerous bullet and save it

		if (bullet != null) { // if there is a bullet to dodge

			if (bullet.getXSpeed() == 0 && bullet.getX() >= me.getX() && bullet.getX() <= me.getX() + 20) { // if the bot is moving in the y direction and it is between my bot's width

				if (me.getX() + 20 >= BattleBotArena.RIGHT_EDGE) { // if the edge of my bot is at the right edge of the arena
					System.out.println(me.getX());
					return BattleBotArena.LEFT; // go left instead of right
				}
				else if (me.getX() <= BattleBotArena.LEFT_EDGE) { // else if the edge of my bot is at the left edge of the arena
					System.out.println(me.getX());
					return BattleBotArena.RIGHT; // go right instead of left
				}
				else {
					return BattleBotArena.LEFT; // otherwise just go left
				}

			}

			if (bullet.getYSpeed() == 0 && bullet.getY() >= me.getY() && bullet.getY() <= me.getY() + 20) { // if the bot is moving in the x direction and it is between my bot's height
				if (me.getY() + 20 >= BattleBotArena.BOTTOM_EDGE) { // if the edge of my bot is at the bottom edge of the arena
					System.out.println(me.getY());
					return BattleBotArena.UP; // go up instead of down
				}
				else if (me.getY() <= BattleBotArena.TOP_EDGE) { // else if the edge of my bot is at the top edge of the arena
					System.out.println(me.getY());
					return BattleBotArena.DOWN; // go down instead of up
				}
				else {
					return BattleBotArena.UP; // otherwise go up

				}
			}
		}
		return BattleBotArena.STAY;

	}

	/** This method is designed to find the bot that my bot is going to attack. My bot will go to the 
	 * closest bot
	 * @param me: my bot
	 * @param liveBots: array of live bots that are possible preys
	 * @return preyBot
	 */
	public BotInfo findPrey (BotInfo me, BotInfo[] liveBots) {
		double closestDist = 860; // closest distance is defaulted to 200
		BotInfo preyBot = null; 

		for (int i = 0; i < liveBots.length; i++) { // for loop which loops through the bots to determine the next prey
			double foc = getDistance(me.getX(), me.getY(), liveBots[i].getX(), liveBots[i].getY()); // find the distance between me and the bot at live bots[i]
			if (foc <= closestDist) { // if the distance between me and the bot focused smaller than the previous closest distance
				closestDist = foc; // set the closest distance to the new closest distance
				preyBot = liveBots[i]; // set the preyBot as the bot with the closest distance
			}
		}
		return preyBot;

	}

	/** This method is designed to move to the prey if the preyBot 
	 * 
	 * @param me
	 * @param preyBot
	 * @return int declaring whether to move up, down, left or right
	 */
	public int moveToPrey (BotInfo me, BotInfo preyBot) {
		if (preyBot.getX() < me.getX()) { // if the prey bot is to the left of me
			if (me.getX() <= BattleBotArena.LEFT_EDGE || me.getX() + 20 >= BattleBotArena.RIGHT_EDGE) { // if my bot is at the left or right edge
				if (preyBot.getY() < me.getY()) // if the prey bot is above me
					return BattleBotArena.UP; // move up
				if (preyBot.getY() > me.getY()) // if the prey bot is below me
					return BattleBotArena.DOWN; // move down
			}
			return BattleBotArena.LEFT; // move left if the code from 175-179 is not true (default)
		}

		if (preyBot.getX() > me.getX()) { // if the prey bot is to the right of me
			if (me.getX() <= BattleBotArena.LEFT_EDGE || me.getX() + 20 >= BattleBotArena.RIGHT_EDGE) { // if my bot is at the left or right edge
				if (preyBot.getY() < me.getY()) // if the prey bot is above me
					return BattleBotArena.UP; // move up
				if (preyBot.getY() > me.getY()) // if the prey bot is below me
					return BattleBotArena.DOWN; // move down
			}
			return BattleBotArena.RIGHT; // move right if the code from 185-190 is not true (default)
		}

		if (preyBot.getY() < me.getY()) { // if the prey bot is to the bottom of me
			if (me.getY() <= BattleBotArena.TOP_EDGE || me.getY() >= BattleBotArena.BOTTOM_EDGE) { // if my bot is at the top or bottom edge
				if (preyBot.getX() < me.getX()) // if the prey bot is to the left of me
					return BattleBotArena.LEFT; // move left
				if (preyBot.getX() > me.getX()) // if the prey bot is to the right of me
					return BattleBotArena.RIGHT; // move right
			}
			return BattleBotArena.UP; // move up if the code from 195-200 is not true (default)
		}

		if (preyBot.getY() > me.getY()) { // if the prey bot is to the top of me
			if (me.getY() <= BattleBotArena.TOP_EDGE || me.getY() >= BattleBotArena.BOTTOM_EDGE) { // if my bot is at the top or bottom edge
				if (preyBot.getX() < me.getX()) // if the prey bot is to the left of me
					return BattleBotArena.LEFT; // move left
				if (preyBot.getX() > me.getX()) // if the prey bot is to the right of me
					return BattleBotArena.RIGHT; //move right
			}
			return BattleBotArena.DOWN; // move down if lines 205-210 is not true (default)
		}

		return BattleBotArena.STAY; // otherwise the bullet is not coming to you - stay (instead of staying the bot will attack)

	}

	/** This method is designed to allow my bot to attack any prey bot. The attack method will either make my bot 
	 * shoot at the prey bot or move towards the prey bot
	 * @param me: my bot
	 * @param liveBots: array of live bots
	 * @return int corresponding to the attack move (shoot left, right, up or down or move left, right, up, or down)
	 */
	public int attack (BotInfo me, BotInfo[] liveBots) {
		BotInfo preyBot = findPrey(me, liveBots); // find the prey bot 

		if ((preyBot.getX() >= me.getX() && preyBot.getX() <= me.getX() + 10) || (preyBot.getX() + 20 < me.getX() + 20 && preyBot.getX() + 20 > me.getX() + 10)) { // if the bot is anywhere between my left edge and my centre of the bot or between my right edge or the centre of the bot (if the bullet will be able to hit it when it shoots from the middle of my bot)
			if (me.getY() <= preyBot.getY()) { // if the prey bot is below me
				System.out.println("fire down");
				return BattleBotArena.FIREDOWN; // fire down
			}

			if (me.getY() >= preyBot.getY() + 20) { // if the prey bot is above me
				System.out.println("fire up");
				return BattleBotArena.FIREUP; // fire up
			}
		}

		if ((preyBot.getY() >= me.getY() && preyBot.getY() <= me.getY() + 20) || (preyBot.getY() + 20 < me.getY() + 20 && preyBot.getY() + 20 > me.getY() + 10)) { // if the bot is anywhere between my top edge and my centre of the bot or between my bottom edge or the centre of the bot (if the bullet will be able to hit it when it shoots from the middle of my bot)
			if (me.getX() <= preyBot.getX()) { // if the prey bot is to the right of me
				System.out.println("fire right");
				return BattleBotArena.FIRERIGHT; // fire right
			}

			if (me.getX() >= preyBot.getX() + 20) { // if the prey bot is to the left of me
				System.out.println("fire left");
				return BattleBotArena.FIRELEFT; // fire left
			}
		}
		return moveToPrey(me, preyBot); // if the bot is not aligned with my bot - move towards the prey bot to be able to shoot

	}



	/**
	 * Construct and return my name
	 */
	public String getName()
	{
		return "Asseel";
	}

	/**
	 * What do you want to do before the round starts?
	 */
	public void newRound()
	{

	}

	/**
	 * Draw the bot
	 */
	public void draw (Graphics g, int x, int y)
	{
		g.drawImage(image, x,y,Bot.RADIUS*2, Bot.RADIUS*2, null);
	}

	/**
	 * Required method
	 */
	public void incomingMessage(int id, String msg)
	{
	}

	/**
	 * Team name
	 */
	public String getTeamName()
	{
		return "Team";
	}

	/**
	 * Send and clear my message buffer
	 */
	public String outgoingMessage()
	{
		String msg = nextMessage;
		nextMessage = null;
		return msg;
	}
}
