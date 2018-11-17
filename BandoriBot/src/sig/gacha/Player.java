package sig.gacha;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import sig.BandoriBot;
import sig.FileUtils;
import sig.GachaBot;

public class Player {
	long discordID;
	String discordName;
	int pulls4;
	int pulls3;
	int pulls2;
	int dupepulls4;
	int dupepulls3;
	int dupepulls2;
	HashMap<Integer,Integer> card_collection = new HashMap<Integer,Integer>();
	
	public Player(long discordID, String discordName) {
		this.discordID = discordID;
		this.discordName = discordName;
		pulls4=0;
		pulls3=0;
		pulls2=0;
		dupepulls4=0;
		dupepulls3=0;
		dupepulls2=0;
		//SavePlayerProfile();
	}
	
	public void SavePlayerProfile() {
		String[] format = GetFileData();
		FileUtils.writetoFile(format, BandoriBot.BASEDIR+"profiles/"+discordID);
	}
	
	public int[] GetPullData() {
		return new int[]{pulls2,pulls3,pulls4};
	}
	
	public int[] GetDupeData() {
		return new int[]{dupepulls2,dupepulls3,dupepulls4};
	}

	private String[] GetFileData() {
		List<String> fileFormat = new ArrayList<String>();
		fileFormat.add(Long.toString(discordID));
		fileFormat.add(discordName);
		fileFormat.add(Integer.toString(pulls4));
		fileFormat.add(Integer.toString(pulls3));
		fileFormat.add(Integer.toString(pulls2));
		for (Integer card_id : card_collection.keySet()) {
			Integer amt = card_collection.get(card_id);
			fileFormat.add(card_id+";"+amt);
		}
		return fileFormat.toArray(new String[fileFormat.size()]);
	}

	public boolean hasCardInCollection(Card c) {
		return card_collection.containsKey(c.id);
	}
	
	public int getNumberOfCardsInCollection(Card c) {
		if (card_collection.containsKey(c.id)) {
			return card_collection.get(c.id);
		} else {
			return 0;
		}
	}
	
	public void addCardToCollection(Card c) {
		switch (c.rarity) {
			case 3:{
				pulls3++;
			}break;
			case 4:{
				pulls4++;
			}break;
			default:{
				pulls2++;
			}
		}
		if (card_collection.containsKey(c.id)) {
			card_collection.put(c.id,card_collection.get(c.id)+1);
		switch (c.rarity) {
			case 3:{
				dupepulls3++;
			}break;
			case 4:{
				dupepulls4++;
			}break;
			default:{
				dupepulls2++;
			}
		}
		} else {
			card_collection.put(c.id, 1);
		}
	}

	public static Player GetPlayerProfile(Long discordID,String discordName) {
		File f = new File(BandoriBot.BASEDIR+"profiles/"+discordID);
		if (f.exists()) {
			return GetPlayerProfile(discordID);
		} else {
			return new Player(discordID,discordName);
		}
	}

	private static Player GetPlayerProfile(long discordID) {
		String[] filedata = FileUtils.readFromFile(BandoriBot.BASEDIR+"profiles/"+discordID);
		int i = 0;
		long tempid = Long.parseLong(filedata[i++]);
		String myname = filedata[i++];
		Player p = new Player(tempid,myname);
		p.pulls4 = Integer.parseInt(filedata[i++]);
		p.pulls3 = Integer.parseInt(filedata[i++]);
		p.pulls2 = Integer.parseInt(filedata[i++]);
		while (i<filedata.length) {
			String s = filedata[i++];
			String[] parse = s.split(";");
			Card c = Card.findCardByID(GachaBot.cardlist, Integer.parseInt(parse[0]));
			//p.addCardToCollection(c);
			int cardcount = Integer.parseInt(parse[1]);
			p.card_collection.put(c.id, cardcount);
			if (cardcount>1) {
				switch (c.rarity) {
					case 3:{
						p.dupepulls3+=cardcount-1;
					}break;
					case 4:{
						p.dupepulls4+=cardcount-1;
					}break;
					default:{
						p.dupepulls2+=cardcount-1;
					}
				}
			}
			System.out.println("Loaded card "+c.id+" to profile "+p.discordName);
		}
		return p;
	}

	public static void ApplyAllCardProfileChanges(JDA bot, MessageChannel channel, String name, Long discordID,
			String message, List<Card> cards) {
		Player p = Player.GetPlayerProfile(discordID,name);
		for (Card c : cards) {
			p.addCardToCollection(c);
			PerformRewards(bot,channel,p,c);
		}
		p.SavePlayerProfile();
	}

	private static void PerformRewards(JDA bot, MessageChannel channel, Player p, Card c) {
		int card_amt = p.card_collection.get(c.id);
		if (c.rarity==4 && card_amt>=1 &&
				card_amt<3) {
			//Download the image...
			try {
				int card_id = c.getCardID();
				boolean trained = (card_amt==2)?true:false;
				System.out.println("Requesting Card "+c+" from "+c.getCardArtURL(trained));
				FileUtils.downloadFileFromUrl(c.getCardArtURL(trained), "art/"+card_id+((trained)?"_trained":"")+".png");
				Message msg;
				if (card_amt==1) {
					msg = new MessageBuilder().append("*Congratulations for unlocking a new* 4\\* **"+p.discordName+"**!").append('\n').
							append("**"+Member.getMemberByID(GachaBot.memberlist, c.member).name+"** ["+c.name+"]").build();
				} else {
					msg = new MessageBuilder().append("**"+p.discordName+"** unlocked the trained version of **"+Member.getMemberByID(GachaBot.memberlist, c.member).name+"** ["+c.name+"]! **Congratulations!**").build();
				}
				channel.sendFile(new File(BandoriBot.BASEDIR+"art/"+card_id+((trained)?"_trained":"")+".png"),msg).queue();
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		} else 
		if (c.rarity==3 && (card_amt==5 ||
		card_amt==10)) {
			//Download the image...
			try {
				int card_id = c.getCardID();
				boolean trained = (card_amt==10)?true:false;
				System.out.println("Requesting Card "+c+" from "+c.getCardArtURL(trained));
				FileUtils.downloadFileFromUrl(c.getCardArtURL(trained), "art/"+card_id+((trained)?"_trained":"")+".png");
				Message msg;
				if (card_amt==5) {
					msg = new MessageBuilder().append("**Congratulations for collecting 5 **"+Member.getMemberByID(GachaBot.memberlist, c.member).name+"** ["+c.name+"] **"+p.discordName+"**!").build();
				} else {
					msg = new MessageBuilder().append("**Congratulations for collecting 10 **"+Member.getMemberByID(GachaBot.memberlist, c.member).name+"** ["+c.name+"] **"+p.discordName+"**!").build();
				}
				channel.sendFile(new File(BandoriBot.BASEDIR+"art/"+card_id+((trained)?"_trained":"")+".png"),msg).queue();
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		} else 
		if (c.rarity==2 && card_amt==100) {
			//Download the image...
			try {
				int card_id = c.getCardID();
				boolean trained = false;
				System.out.println("Requesting Card "+c+" from "+c.getCardArtURL(trained));
				FileUtils.downloadFileFromUrl(c.getCardArtURL(trained), "art/"+card_id+((trained)?"_trained":"")+".png");
				Message msg;
				msg = new MessageBuilder().append("**Congratulations for collecting 100 **"+Member.getMemberByID(GachaBot.memberlist, c.member).name+"** ["+c.name+"] **"+p.discordName+"**!").build();
				channel.sendFile(new File(BandoriBot.BASEDIR+"art/"+card_id+((trained)?"_trained":"")+".png"),msg).queue();
			} catch (JSONException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
