package sig;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.requests.restaction.MessageAction;
import sig.gacha.Card;
import sig.gacha.Member;
import sig.gacha.Player;

public class GachaBot {
	JDA bot;
	static int cardcount = 0;
	static int membercount = 0;
	public static int databasecheck = 21400;
	static boolean initialcheck=false;
	public static HashMap<Integer,Member> memberlist = new HashMap<Integer,Member>();
	public static HashMap<Integer,Card> card_idmap = new HashMap<Integer,Card>(); 
	public static HashMap<Integer,List<Card>> card_raritymap = new HashMap<Integer,List<Card>>();
	public static HashMap<Integer,List<Card>> card_membermap = new HashMap<Integer,List<Card>>();
	public static HashMap<Long,Long> gacha_reroll_timer = new HashMap<Long,Long>();
	public static Font programFont = new Font("Century Schoolbook L",Font.PLAIN,24);
	final public static int GACHADELAY = 2000; 
	public GachaBot(JDA bot) {
		this.bot=bot;
		UpdateCardDatabase();
		CreateFileDirectories();
	    String fonts[] = 
	      GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

	    for ( int i = 0; i < fonts.length; i++ )
	    {
	      System.out.println(fonts[i]);
	    }
	}
	
	private void CreateFileDirectories() {
		new File(BandoriBot.BASEDIR+"art/").mkdirs();
		new File(BandoriBot.BASEDIR+"card_art/").mkdirs();
		new File(BandoriBot.BASEDIR+"gacha_results/").mkdirs();
		new File(BandoriBot.BASEDIR+"profiles/").mkdirs();
	}
	
	public static void tick() {
		databasecheck--;
		if (databasecheck==0) {
			databasecheck=0;
			UpdateCardDatabase();
		}
	}

	public void checkForGacha(MessageChannel channel,String name,Long discordID,String message) {
		System.out.println(message+" | "+message.charAt(0));
		if (message.length()>0 && message.charAt(0)=='.') {
			//This is a bot message. Try to work with it.
			String[] wordparse = message.split(" ");
			System.out.println(Arrays.toString(wordparse));
			switch (wordparse[0]) {
				case ".stats":{
					Player p = Player.GetPlayerProfile(discordID, name);
					DecimalFormat df = new DecimalFormat("0.0");
					channel.sendMessage(new MessageBuilder("*Stats for* "+name+":").append('\n')
							.append("```Total Rolls: "+(p.GetPullData()[0]+p.GetPullData()[1]+p.GetPullData()[2])).append('\n')
							.append("4* Cards: "+(p.GetPullData()[2])+"  ("+df.format((double)(p.GetPullData()[2])/(p.GetPullData()[0]+p.GetPullData()[1]+p.GetPullData()[2])*100)+"%)").append(" ["+p.GetDupeData()[2]+" dupe"+((p.GetDupeData()[2]==1)?"":"s")+"]").append('\n')
							.append("3* Cards: "+(p.GetPullData()[1])+"  ("+df.format((double)(p.GetPullData()[1])/(p.GetPullData()[0]+p.GetPullData()[1]+p.GetPullData()[2])*100)+"%)").append(" ["+p.GetDupeData()[1]+" dupe"+((p.GetDupeData()[1]==1)?"":"s")+"]").append('\n')
							.append("2* Cards: "+(p.GetPullData()[0])+"  ("+df.format((double)(p.GetPullData()[0])/(p.GetPullData()[0]+p.GetPullData()[1]+p.GetPullData()[2])*100)+"%)").append(" ["+p.GetDupeData()[0]+" dupe"+((p.GetDupeData()[0]==1)?"":"s")+"]").append('\n')
							.append('\n')
							.append("Collection: "+(p.GetCollectionData()[2]+p.GetCollectionData()[1]+p.GetCollectionData()[0])+"/"+(Card.star4total+Card.star3total+Card.star2total)).append('\n')
							.append("4* Cards: "+(p.GetCollectionData()[2])+"/"+Card.star4total).append(" ("+df.format(((double)p.GetCollectionData()[2]/Card.star4total)*100)+"%)").append('\n')
							.append("3* Cards: "+(p.GetCollectionData()[1])+"/"+Card.star3total).append(" ("+df.format(((double)p.GetCollectionData()[1]/Card.star3total)*100)+"%)").append('\n')
							.append("2* Cards: "+(p.GetCollectionData()[0])+"/"+Card.star2total).append(" ("+df.format(((double)p.GetCollectionData()[0]/Card.star2total)*100)+"%)").append('\n')
							.append("```").build()).queue();
				}break;
				case ".gacha":{
					if (gacha_reroll_timer.containsKey(discordID)) {
						if (gacha_reroll_timer.get(discordID)>System.currentTimeMillis()) {
							gacha_reroll_timer.put(discordID,Math.min(gacha_reroll_timer.get(discordID)+500,System.currentTimeMillis()+GACHADELAY));
							return;
						}
					}
					gacha_reroll_timer.put(discordID, System.currentTimeMillis()+GACHADELAY);
					//System.out.print("This is a gacha attempt~!");
					int amt = 1;
					if (wordparse.length>1) {
						try {
							amt = Integer.parseInt(wordparse[1]);
						} catch (NumberFormatException e) {
							
						}
						if (amt!=5 && amt!=10) {
							amt = 1;
						}
					}
					int original_amt = amt;
					BufferedImage img = new BufferedImage(180*((amt==5||amt==10)?5:1),180*((amt==10)?2:1),BufferedImage.TYPE_INT_ARGB);
					Graphics2D g = img.createGraphics();
					int x_offset = 0;
					int y_offset = 0;
					List<Card> picked_cards = new ArrayList<Card>();
					Player p = Player.GetPlayerProfile(discordID,name);
					do {
						double rng = Math.random();
						int star_rating = 2;
						if (rng<0.03) {
							//4-star.
							star_rating=4;
						} else 
						if (rng<0.085){
							//3-star.
							star_rating=3;
						} else {
							star_rating=2;
						}
						if (original_amt>1 && amt==1) {
							if (rng<0.03) {
								//4-star.
								star_rating=4;
							} else {
								star_rating=3;
							}
						}
						Card c = Card.pickRandomCardByStarRating(star_rating);
						boolean trained = (star_rating>2 && p.getNumberOfCardsInCollection(c)%2==1);
						picked_cards.add(c);
						File card_file = new File(BandoriBot.BASEDIR+"card_art/"+c.getCardID()+((trained)?"_trained":"")+".png");
						if (!card_file.exists()) {
							System.out.println("Requesting Card "+c+" from "+c.getCardURL(trained));
							try {
								FileUtils.downloadFileFromUrl(c.getCardURL(trained), "card_art/"+c.getCardID()+((trained)?"_trained":"")+".png");
							} catch (JSONException | IOException e) {
								e.printStackTrace();
							}					
							
						}
						File star_file = new File(BandoriBot.BASEDIR+"newstar.png");
						//channel.sendFile(card_file).queue();
						BufferedImage card_img = null;
						try {
							card_img = ImageIO.read(card_file);
						} catch (IOException e) {
							e.printStackTrace();
						}
						g.drawImage(card_img, x_offset, y_offset, null);
						card_img.flush();
						if (!p.hasCardInCollection(c)) {
							try {
								card_img = ImageIO.read(star_file);
								g.drawImage(card_img, x_offset+180-64, y_offset+180-64, null);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						if (p.getNumberOfCardsInCollection(c)>0) {
							g.setFont(programFont);
							FontRenderContext frc = g.getFontMetrics(programFont).getFontRenderContext();
							String displayString = Integer.toString(p.getNumberOfCardsInCollection(c)+1);
							Rectangle2D size = programFont.getStringBounds(displayString, frc);
							//g.drawString(displayString, (float)(x_offset+180-size.getX()-4), (float)(y_offset+180-size.getY()-4));
							DrawUtils.drawOutlineText(g, programFont, (float)(x_offset+180-size.getWidth()-12), (float)(y_offset+180-size.getHeight()+20), 1, 2, Color.BLACK, Color.WHITE, displayString);
						}
						x_offset+=180;
						if (x_offset>=900) {
							x_offset=0;
							y_offset+=180;
						}
						card_img.flush();
					} while (--amt>0);
					//
					long systemTime = System.currentTimeMillis();
					File gacha_result = new File(BandoriBot.BASEDIR+"gacha_results/"+systemTime+".png");
					try {
						ImageIO.write(img, "png", gacha_result);
					} catch (IOException e) {
						e.printStackTrace();
					}
					Message msg = new MessageBuilder().append("*Gacha Result for "+name+"*").build();
					channel.sendFile(gacha_result,msg).queue();
					g.dispose();
					img.flush();
					
					//Player p = Player.GetPlayerProfile(discordID,name);
					Player.ApplyAllCardProfileChanges(bot,channel,name,discordID,message,picked_cards);
				}break;
				case ".display":{
					if (wordparse.length>1) { //This is a valid ID to try.
						/*try {
							int card_id = Integer.parseInt(wordparse[1]);
							Card c = Card.findCardByID(cardlist, card_id);
							if (c!=null) {
								//Download the image...
								try {
									System.out.println(c.getCardArtURL(false));
									FileUtils.downloadFileFromUrl(c.getCardArtURL(false), "art/"+card_id+".png");
									channel.sendFile(new File(BandoriBot.BASEDIR+"art/"+card_id+".png")).queue();
								} catch (JSONException | IOException e) {
									e.printStackTrace();
								}
							} else {
								channel.sendMessage("*I am sorry, but I cannot find that!!!*").queue();
							}
						}
						catch (NumberFormatException e) {
							channel.sendMessage("*I am sorry, but I would like a number there instead...*").queue();
						}*/
						/*try {
							FileUtils.downloadFileFromUrl("https://i.bandori.party/u/c/art/1023Lisa-Imai-Pure-%E3%82%B3%E3%83%9F%E3%83%A5%E5%8A%9BMAX-kYqpMS.png", "art/testart.png");
							channel.sendFile(new File(BandoriBot.BASEDIR+"art/testart.png")).queue();
						} catch (JSONException | IOException e) {
							e.printStackTrace();
						}*/
						String character = wordparse[1];
						Member m = Member.getMemberByName(memberlist, character);
						if (m!=null) {
							int character_id = m.getMemberID();
							Card c = Card.pickRandomCardByMemberID(character_id);
							if (c!=null) {
								//Download the image...
								try {
									int card_id = c.getCardID();
									boolean trained = false;
									if (c.getCardStarRating()>2) {
										trained = Math.random()<0.5;
									}
									System.out.println("Requesting Card "+c+" from "+c.getCardArtURL(trained));
									FileUtils.downloadFileFromUrl(c.getCardArtURL(trained), "art/"+card_id+((trained)?"_trained":"")+".png");
									channel.sendFile(new File(BandoriBot.BASEDIR+"art/"+card_id+((trained)?"_trained":"")+".png")).queue();
								} catch (JSONException | IOException e) {
									e.printStackTrace();
								}
							} else {
								channel.sendMessage("*I am sorry, but something went wrong!*").queue();
							}
						} else {
							channel.sendMessage("*I am sorry, but I cannot find the person you are looking for! Check your spelling!* :xD:").queue();	
						}
					}
				}break;
			}
		}
	}
	
	private static void UpdateCardDatabase() {
		int cardsLoaded = 0;
		int membersLoaded = 0;
		try {
			int pagecount = 1;
			do {
				JSONObject jsondata = FileUtils.readJsonFromUrl("https://bandori.party/api/cards/?page="+pagecount++);
				//jsondata = FileUtils.readJsonFromFile(file);
				cardcount = jsondata.getInt("count");
				String nexturl = "";
				String prevurl = "";
				if (jsondata.get("next") instanceof String) {
					nexturl = (String)jsondata.get("next");
				} else {
					nexturl = "";
				}
				if (jsondata.get("previous") instanceof String) {
					prevurl = (String)jsondata.get("previous");
				} else {
					prevurl = "";
				}
				JSONArray carddata = jsondata.getJSONArray("results");
				//System.out.println(cardcount+";"+nexturl+";"+prevurl+";"+carddata);
				for (Object obj : carddata) {
					JSONObject card = (JSONObject) obj;
					Card c = new Card(card);
					if (!card_idmap.containsKey(c.getCardID())) {
						card_idmap.put(c.getCardID(), c);
						if (card_raritymap.containsKey(c.getCardStarRating())) {
							List<Card> raritylist = card_raritymap.get(c.getCardStarRating());
							raritylist.add(c);
							card_raritymap.put(c.getCardStarRating(), raritylist);
						} else {
							List<Card> raritylist = new ArrayList<Card>();
							raritylist.add(c);
							card_raritymap.put(c.getCardStarRating(), raritylist);
						}
						
						if (card_membermap.containsKey(c.getMember())) {
							List<Card> memberlist = card_membermap.get(c.getMember());
							memberlist.add(c);
							card_membermap.put(c.getMember(),memberlist);
						} else {
							List<Card> memberlist = new ArrayList<Card>();
							memberlist.add(c);
							card_membermap.put(c.getMember(), memberlist);
						}
						switch (c.getCardStarRating()) {
							case 3: {
								Card.star3total++;
							}break;
							case 4: {
								Card.star4total++;
							}break;
							case 2: {
								Card.star2total++;
							}break;
						}
						cardcount++;
						cardsLoaded++;
					}
				}
				if (nexturl.length()==0) {
					break;
				}
			} while (true);
			System.out.println("Loaded "+cardsLoaded+" / "+cardcount+" cards.");
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		try {
			int pagecount = 1;
			do {
				JSONObject jsondata = FileUtils.readJsonFromUrl("https://bandori.party/api/members/?page="+pagecount++);
				//jsondata = FileUtils.readJsonFromFile(file);
				membercount = jsondata.getInt("count");
				String nexturl = "";
				String prevurl = "";
				if (jsondata.get("next") instanceof String) {
					nexturl = (String)jsondata.get("next");
				} else {
					nexturl = "";
				}
				if (jsondata.get("previous") instanceof String) {
					prevurl = (String)jsondata.get("previous");
				} else {
					prevurl = "";
				}
				JSONArray memberdata = jsondata.getJSONArray("results");
				//System.out.println(cardcount+";"+nexturl+";"+prevurl+";"+carddata);
				for (Object obj : memberdata) {
					JSONObject member = (JSONObject) obj;
					Member m = new Member(member);
					if (!memberlist.containsKey(m.getMemberID())) {
						memberlist.put(m.getMemberID(),m);
						membercount++;
						membersLoaded++;
					}
				}
				if (nexturl.length()==0) {
					break;
				}
			} while (true);
			System.out.println("Loaded "+membersLoaded+" / "+membercount+" characters.");
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
		if (!initialcheck) {
			initialcheck=true;
		} else {
			if (cardsLoaded>0) {
				BandoriBot.bot.getTextChannelById(509845287284768801l).sendMessage("**"+cardsLoaded+" new cards are now available! Good Luck!** ("+(Card.star2total+Card.star3total+Card.star4total)+" total)").queue();
			}
		}
	}
}
