package sig;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BandoriBot extends ListenerAdapter{
	final public static String BASEDIR = "./"; 
	public static HashMap<String,List<String>> stamp_map = new HashMap<String,List<String>>();
	public static int noMessageTimer = 0; //How long it's been since no messages.
	public static int currentEventTimer = 0;
	public static String currentEvent = "";
	public static JDA bot;
	public static int messageCounter = 0;
	Random r = new Random();
	static GachaBot gacha;
	
	public static String[] eventsList = new String[]{
			"Making Choco Cornets",
			"Making Choco Cornets w/Rimi",
			"Hanging out w/<CHARACTER>",
			"Guitar Practice",
			"Practice w/The Band",
			"Band Rehearsal",
			"Fuwa-Fuwa Time (again)",
			"Multi-Live w/4* Aya",
			"Gazing at the stars",
			"<MAKEEAT> Star Candy w/<WITHPOPIPA>",
			"Tentai Kansoku w/Afterglow",
			"Hanging out w/<CHARACTER>",
			"Writing a new song",
			"Not doing homework",
			"Bothering Arisa",
			"Looking for Arisa",
			"Getting <SCOLDYELL> by Arisa",
			"Having a picnic at Kokoro's Mansion",
			"Trying to make Arisa smile",
			"Shopping at the mall w/<CHARACTER>",
			"Shopping at <STORE> <POSSIBLECHAR>",
			"Eating at <FOOD> <POSSIBLECHAR>",
			"a Heart-pounding Song",
			"Doki-Doki SING OUT!",
			"at the Live House",
			"at CiRCLE",
			"with Oddie",
			"Collecting <ITEMS>",
			"Rolling Gacha"
	};

	public static String[] makeeatList = new String[]{
			"Making",
			"Eating"
	};
	public static String[] itemList = new String[]{
			"Studio Tickets",
			"Coins",
			"Michelle Monakas",
			"Miracle Crystals",
	};
	public static String[] scoldyellList = new String[]{
			"Scolded",
			"Yelled at"
	};
	public static String[] storeList = new String[]{
			"Edogawa Music",
			"Ryuseido",
			"the mall",
			"CiRCLE"
	};
	public static String[] foodList = new String[]{
			"the Fast Food Store",
			"the Cafeteria",
			"Yamabuki Bakery",
			"CiRCLE"
	};
	public static String[] popipaList = new String[]{
			"Tae",
			"Rimi",
			"Saaya",
			"Arisa",
			"Poppin' Party",
	};
	
	public static String[] possibleCharList = new String[]{
			"Tae",
			"Rimi",
			"Saaya",
			"Arisa",
			"Ran",
			"Moca",
			"Himari",
			"Tomoe",
			"Tsugumi",
			"Kokoro",
			"Kaoru",
			"Hagumi",
			"Kanon",
			"Misaki",
			"Aya",
			"Hina",
			"Chisato",
			"Maya",
			"Eve",
			"Yukina",
			"Sayo",
			"Lisa",
			"Ako",
			"Rinko"
	};
	
	public static String[] characterList = new String[]{
			"Tae",
			"Rimi",
			"Saaya",
			"Arisa",
			"Ran",
			"Moca",
			"Himari",
			"Tomoe",
			"Tsugumi",
			"Kokoro",
			"Kaoru",
			"Hagumi",
			"Kanon",
			"Misaki",
			"Aya",
			"Hina",
			"Chisato",
			"Maya",
			"Eve",
			"Yukina",
			"Sayo",
			"Lisa",
			"Ako",
			"Rinko"
	};
	
	
	public static void main(String[] arguments) {
		populateStampMap();
		String[] filedata = FileUtils.readFromFile(BASEDIR+"clientToken.txt");
		try {
			bot = new JDABuilder(filedata[0])
			.addEventListener(new BandoriBot()).build();
			bot.awaitReady();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
		gacha = new GachaBot(bot);
		File store_file = new File(BASEDIR+"bot_status.txt");
		if (store_file.exists()) {
			try {
				FileReader rd = new FileReader(store_file);
				BufferedReader reader = new BufferedReader(rd);
				String status = reader.readLine();
				if (status==null) {
					status = "";
				}
				UpdateBotStatus(status,900 + (int)((90*60)*Math.random()));
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		while (true) {
			noMessageTimer++;
			GachaBot.tick();
			if (noMessageTimer>7200) {
				if (noMessageTimer>18000) {
					currentEvent = "Dreaming about stars";
					if (currentEventTimer==0) {
						UpdateBotStatus(currentEvent,0);
						currentEventTimer = 300;
						messageCounter = 0;
					}
				} else
				if (noMessageTimer>14400) {
					currentEvent = "Sleeping";
					if (currentEventTimer==0) {
						UpdateBotStatus(currentEvent,0);
						currentEventTimer = 300;
						messageCounter = 0;
					}
				} else {
					currentEvent = "Taking a cat-nap";
					if (currentEventTimer==0) {
						UpdateBotStatus(currentEvent,0);
						currentEventTimer = 300;
						messageCounter = 0;
					}
				}
			} else {
				if (currentEventTimer==0 && Math.random()<(1/300d)) {
					//Start a new event.
					currentEvent = eventsList[(int)(Math.random()*eventsList.length)];
					if (Math.random()<0.8) {
						currentEvent = currentEvent.replace("<CHARACTER>", popipaList[(int)(Math.random()*popipaList.length)]);
					} else {
						currentEvent = currentEvent.replace("<CHARACTER>", characterList[(int)(Math.random()*characterList.length)]);
					}
					currentEvent = currentEvent.replace("<MAKEEAT>", makeeatList[(int)(Math.random()*makeeatList.length)]);
					currentEvent = currentEvent.replace("<WITHPOPIPA>", popipaList[(int)(Math.random()*popipaList.length)]);
					currentEvent = currentEvent.replace("<SCOLDYELL>", scoldyellList[(int)(Math.random()*scoldyellList.length)]);
					currentEvent = currentEvent.replace("<FOOD>", foodList[(int)(Math.random()*foodList.length)]);
					currentEvent = currentEvent.replace("<STORE>", storeList[(int)(Math.random()*storeList.length)]);
					currentEvent = currentEvent.replace("<STORE>", storeList[(int)(Math.random()*storeList.length)]);
					currentEvent = currentEvent.replace("<ITEMS>", itemList[(int)(Math.random()*itemList.length)]);
					if (Math.random()<=0.6) {
						currentEvent = currentEvent.replace("<POSSIBLECHAR>", "w/"+possibleCharList[(int)(Math.random()*possibleCharList.length)]);
					} else {
						currentEvent = currentEvent.replace("<POSSIBLECHAR>", "");
					}
					UpdateBotStatus(currentEvent,900 + (int)((90*60)*Math.random()));
				}
			}
				
			if (currentEventTimer>0) {
				currentEventTimer--;
				if (currentEventTimer==0) {
					currentEvent = "";
					UpdateBotStatus(currentEvent,0);
				}
			}
			try {
				//System.out.println("Current Event: "+currentEvent+"; Timer: "+currentEventTimer+"; No Message Timer: "+noMessageTimer+"; Message Counter: "+messageCounter);
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void UpdateBotStatus(String event, int eventTimer) {
		currentEvent = event;
		currentEventTimer = eventTimer;
		if (currentEvent.equalsIgnoreCase("")) {
			bot.getPresence().setGame(null);
		} else {
			bot.getPresence().setGame(Game.of(GameType.DEFAULT,currentEvent));
		}
		File store_file = new File(BASEDIR+"bot_status.txt");
		try {
			FileWriter wr = new FileWriter(store_file);
			wr.write(currentEvent);
			wr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void populateStampMap() {
		stamp_map.put("kasumi_gogo",Arrays.asList("gogo"));
		stamp_map.put("tae_letsplay",Arrays.asList("playtogether","wanttoplay","multilive","letsplay"));
		stamp_map.put("rimi_choco",Arrays.asList("choco","cornet"));
		stamp_map.put("saya_bread",Arrays.asList("bread"));
		stamp_map.put("arisa_doki",Arrays.asList("doki","chomama","baka"));
		stamp_map.put("ran_same",Arrays.asList("sameasalways","alwayssame","alwaysthesame"));
		stamp_map.put("moca_youdidit",Arrays.asList("youdidit","congratulations","buns","mocatastic"));
		stamp_map.put("himari_heyheyhoh",Arrays.asList("heyo","heyhey","hihi","hiyo"));
		stamp_map.put("tomoe_letsdothis",Arrays.asList("letsdothis","letsdoit"));
		stamp_map.put("tsugumi_wecandoit",Arrays.asList("wegotthis","wegotit","wecan"));
		stamp_map.put("kokoro_happylucky",Arrays.asList("happy!","lucky"));
		stamp_map.put("kaoru_fleeting",Arrays.asList("fleeting"));
		stamp_map.put("aya_fever",Arrays.asList("fever","ayay"));
		stamp_map.put("hagumi_smileyay",Arrays.asList("smileyay","yay!"));
		stamp_map.put("kanon_fuee",Arrays.asList("fuu","fue","waa","reee"));
		stamp_map.put("misaki_ready",Arrays.asList("amready","beenready","ready!"));
		stamp_map.put("hina_fullcombo",Arrays.asList("fcd","fullcombo","nomiss","allperfect","notasinglemiss","thefc","anfc","fullperfect","easyfc","ezfc"));
		stamp_map.put("chisato_planned",Arrays.asList("justasplanned","allplanned","calculated","thatcoming"));
		stamp_map.put("maya_huhehe",Arrays.asList("hehe","huehe","huehue","shuwashuwa"));
		stamp_map.put("eve_bushido",Arrays.asList("bushido"));
		stamp_map.put("yukina_notbad",Arrays.asList("notbad","veryclose"));
		stamp_map.put("sayo_goodwork",Arrays.asList("goodwork","goodjob","nicejob","welldone","greatwork","greatjob"));
		stamp_map.put("lisa_nextonelastone",Arrays.asList("lastone","mylast"));
		stamp_map.put("ako_onemoretime",Arrays.asList("onemore","goagain","onceagain","dontstop","runit"));
		stamp_map.put("rinko_jam",Arrays.asList("lovethissong","jam"/*,"happybirthday"*/));
		stamp_map.put("marina_yeahyeah",Arrays.asList("yeahyeah","letsgo"));
		stamp_map.put("kokoro_moremore",Arrays.asList("moremore","iwantmore"));
		stamp_map.put("arisa_huh",Arrays.asList("huh?","hh?","yy?","aat?","aa?","tt?","nani","nand"));
		stamp_map.put("yukina_followmylead",Arrays.asList("followmylead","takethelead","guideyou","fullydevoted"));
		stamp_map.put("kaoru_suchalovelyevening",Arrays.asList("goodevening","lovelyevening","beautifulnight","grandnight","wonderfulevening"));
		stamp_map.put("rimi_congrats",Arrays.asList("grats"));
		stamp_map.put("ran_somethingbigiscoming",Arrays.asList("somethingbig","iscoming"));
		stamp_map.put("tsugumi_comeon",Arrays.asList("comeon","dontbeafraid","dontbeshy","tsugurific"));
		stamp_map.put("tae_fufusocute",Arrays.asList("socute","kawaii","fufu","adorable","cute"));
		stamp_map.put("eve_marchintobattle",Arrays.asList("marchintobattle","chargeintobattle"));
		stamp_map.put("saya_illtry",Arrays.asList("illtry","itachance","itatry","atleastonce"));
		stamp_map.put("lisa_imsohappy",Arrays.asList("ecstatic","sohappy","toohappy"));
		stamp_map.put("sayo_ohwell",Arrays.asList("ohwell","ahwell","youtried"));
		stamp_map.put("ako_areyouokay",Arrays.asList("youok","beok","daijo"));
		stamp_map.put("chisato_thisissomuchfun",Arrays.asList("muchfun","veryfun","reallyfun","extremelyfun","offun"));
		stamp_map.put("rinko_theresnoway",Arrays.asList("noway"));
		stamp_map.put("tae_thisisgreat",Arrays.asList("thisisgreat","thisisawesome","thisiswonderful"));
		stamp_map.put("moca_thisisgettinginteresting",Arrays.asList("gettinginteresting","thingsaregetting","thisisgetting"));
		stamp_map.put("kaoru_takemyhand",Arrays.asList("takemyhand","allowmeto","demonstrate","romeo"));
		stamp_map.put("kokoro_letsmaketheworldsmile",Arrays.asList("hhw","happyworld","hellohappy","worldsmile"));
		stamp_map.put("hina_nowwereboppin",Arrays.asList("bop","nowwere","zap"));
		stamp_map.put("kokoro_what_a_great_idea",Arrays.asList("greatidea","goodidea","greatthinking","goodthinking"));
		stamp_map.put("sayo_im_sorry",Arrays.asList("sorry","gomen","apologize","somethingwrong","forgive"));
		stamp_map.put("hagumi_hooray",Arrays.asList("hooray","hiphip","whoo","yahoo"));
		stamp_map.put("kanon_keepgoing",Arrays.asList("keepgoing","dontstop","youcandoit","makeit","gaja","petan","pettan","pengu"));
		stamp_map.put("tsugumi_amazing",Arrays.asList("amazing","wow","sugoi","wooo","cool!","tsugurific"));
		stamp_map.put("eve_thefruits",Arrays.asList("fruits","labor","hardwork","effort"));
		
		
		/// BOT ONLY!!! DO NOT ADD THE BELOW LINES TO BANDORI MODULE IN SIGIRC!!!
		stamp_map.put("aya_pressure",Arrays.asList("feelingheavy","thisfeeling","pressure","toomuch","waa","ahhhhh","ayya"));
		stamp_map.put("aya_miracles",Arrays.asList("miracles","wishes","cometrue","dreams","ayaay"));
	}
	
	public static void checkForStamp(MessageChannel channel, String user,String message) {
		boolean foundmatch = false;
		message = message.toLowerCase().replaceAll("[ ]", "");
		if (message.length()>480) {
			return;
		}
		for (String key : stamp_map.keySet()) {
			for (String message_search : stamp_map.get(key)) {
				String filteredmessage = message;
				filteredmessage = filteredmessage.replaceAll("[^A-Za-z0-9]","");
				//System.out.println(filteredmessage);
				if (message_search.contains("?") || message_search.contains("!")) {
					if (message.contains(message_search)) {
						foundmatch = true;
						CreateStamp(channel,key);
						System.out.println("Stamp "+key+" created by user "+user+" MESSAGE:"+message+".");
						break;
					}
				} else {
					if (filteredmessage.contains(message_search)) {
						foundmatch=true;
						if (key.equalsIgnoreCase("kanon_fuee")) {
							//If odd, other fuee.
							if (filteredmessage.length()%2==0) {
								CreateStamp(channel,key);
							} else {
								CreateStamp(channel,"kanon_keepgoing");
							}
						} else {
							CreateStamp(channel,key);
						}
						System.out.println("Stamp "+key+" created by user "+user+" MESSAGE:"+message+".");
						break;
					}
				}
			}
			if (foundmatch) {
				noMessageTimer=0;
				return;
			} else
			{
				if (noMessageTimer>7200) {
					messageCounter++;
					if (Math.random()>1d/messageCounter) {
						currentEvent = "";
						UpdateBotStatus(currentEvent,0);
						messageCounter = 0;
						noMessageTimer = 0;
					}
				}
			}
		}
	}

	public static void CreateStamp(MessageChannel channel, String stamp_name) {
		channel.sendFile(new File(BASEDIR+"stamps/"+stamp_name+".png")).queue();
	}
	
	@Override
	public void onMessageReceived(MessageReceivedEvent ev) {
		JDA bot = ev.getJDA();
		
		String message = ev.getMessage().getContentDisplay();
		MessageChannel messaging_channel = ev.getChannel();
		
		if (ev.isFromType(ChannelType.TEXT)) {
			Channel channel = ev.getTextChannel(); 
			Member user = ev.getMember();
			
			System.out.println("Channel "+channel+": "+user+" - "+message);
			
			if (user.getUser().getIdLong()==494666451765035009l) {
				return;
			}
			
			if (message.contains("@Kasumi Toyama")) {
				if (message.replace("@Kasumi Toyama", "").equalsIgnoreCase("best bot")) {
					CreateStamp(messaging_channel,"kasumi_gogo");
				} else {
					r.setSeed(user.getUser().getIdLong()+message.hashCode());
					int stamp = r.nextInt(stamp_map.size());
					String stamp_name = "";
					for (String s : stamp_map.keySet()) {
						if (stamp--==0) {
							CreateStamp(messaging_channel,s);
						}
					}
				}
				return;
			}
			
			if (channel.getId().equalsIgnoreCase("485297375665979414") ||
					channel.getId().equalsIgnoreCase("509845287284768801")) {
				System.out.println("Detected in Bandori Channel....");
				checkForStamp(messaging_channel,user.getEffectiveName(),message);
				gacha.checkForGacha(messaging_channel,user.getEffectiveName(),user.getUser().getIdLong(),message);
			}
				
			
		}
		
	}
}
