package sig;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Channel;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class BandoriBot extends ListenerAdapter{
	final public static String BASEDIR = "./"; 
	public static HashMap<String,List<String>> stamp_map = new HashMap<String,List<String>>();
	
	public static void main(String[] arguments) {
		populateStampMap();
		String[] filedata = FileUtils.readFromFile(BASEDIR+"clientToken.txt");
		try {
			JDA bot = new JDABuilder(filedata[0])
			.addEventListener(new BandoriBot()).build();
			bot.awaitReady();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void populateStampMap() {
		stamp_map.put("kasumi_gogo",Arrays.asList("gogo"));
		stamp_map.put("tae_letsplay",Arrays.asList("playtogether","wanttoplay","multilive","letsplay"));
		stamp_map.put("rimi_choco",Arrays.asList("choco","cornet"));
		stamp_map.put("saya_bread",Arrays.asList("bread"));
		stamp_map.put("arisa_doki",Arrays.asList("doki","chomama"));
		stamp_map.put("ran_same",Arrays.asList("sameasalways","alwayssame","alwaysthesame"));
		stamp_map.put("moca_youdidit",Arrays.asList("youdidit","congratulations","buns","mocatastic"));
		stamp_map.put("himari_heyheyhoh",Arrays.asList("heyo","heyhey","hihi","hiyo"));
		stamp_map.put("tomoe_letsdothis",Arrays.asList("letsdothis","letsdoit"));
		stamp_map.put("tsugumi_wecandoit",Arrays.asList("wegotthis","wegotit","wecan"));
		stamp_map.put("kokoro_happylucky",Arrays.asList("happy!","lucky"));
		stamp_map.put("kaoru_fleeting",Arrays.asList("fleeting"));
		stamp_map.put("aya_fever",Arrays.asList("fever","ayay"));
		stamp_map.put("hagumi_smileyay",Arrays.asList("smileyay","yay"));
		stamp_map.put("kanon_fuee",Arrays.asList("fue","waa","reee"));
		stamp_map.put("misaki_ready",Arrays.asList("amready","beenready","ready!"));
		stamp_map.put("hina_fullcombo",Arrays.asList("fcd","fullcombo","nomiss","allperfect","notasinglemiss","thefc","anfc","fullperfect"));
		stamp_map.put("chisato_planned",Arrays.asList("justasplanned","allplanned","calculated","thatcoming"));
		stamp_map.put("maya_huhehe",Arrays.asList("hehe","huehe","huehue","shuwashuwa"));
		stamp_map.put("eve_bushido",Arrays.asList("bushido"));
		stamp_map.put("yukina_notbad",Arrays.asList("notbad","veryclose"));
		stamp_map.put("sayo_goodwork",Arrays.asList("goodwork","goodjob","nicejob","welldone","greatwork","greatjob"));
		stamp_map.put("lisa_nextonelastone",Arrays.asList("lastone","mylast"));
		stamp_map.put("ako_onemoretime",Arrays.asList("onemore","goagain","keepgoing","dontstop"));
		stamp_map.put("rinko_jam",Arrays.asList("lovethissong","jam"));
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
		stamp_map.put("hina_nowwereboppin",Arrays.asList("bop","nowwere"));
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
						CreateStamp(channel,key);
						System.out.println("Stamp "+key+" created by user "+user+" MESSAGE:"+message+".");
						break;
					}
				}
			}
			if (foundmatch) {
				return;
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
			
			if (user.getUser().getId().equalsIgnoreCase("494666451765035009")) {
				return;
			}
			
			if (channel.getId().equalsIgnoreCase("485297375665979414")) {
				System.out.println("Detected in Bandori Channel....");
				checkForStamp(messaging_channel,user.getEffectiveName(),message);
			}
		}
		
	}
}
