package sig.gacha;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import sig.GachaBot;

public class Card {
	public static int star4total = 0;
	public static int star3total = 0;
	public static int star2total = 0;
	int id;
	int member;
	int rarity;
	String attribute;
	String name;
	String jap_name;
	String imgURL;
	String imgURL_trained;
	String artURL;
	String artURL_trained;
	String transparentURL;
	String transparentURL_trained;
	String skill_name;
	String jap_skill_name;
	String skill_type;
	String skill_desc;
	int performance_min;
	int performance_max;
	int performance_trained_max;
	int technique_min;
	int technique_max;
	int technique_trained_max;
	int visual_min;
	int visual_max;
	int visual_trained_max;
	JSONArray cameo_members;
	
	public Card(JSONObject data) {
		id = getIntFromJson(data,"id");
		member = getIntFromJson(data,"member");
		rarity = getIntFromJson(data,"i_rarity");
		attribute = getStringFromJson(data,"i_attribute");
		name = getStringFromJson(data,"name");
		jap_name = getStringFromJson(data,"japanese_name");
		imgURL = getStringFromJson(data,"image");
		imgURL_trained= getStringFromJson(data,"image_trained");
		artURL = getStringFromJson(data,"art");
		artURL_trained = getStringFromJson(data,"art_trained");
		transparentURL = getStringFromJson(data,"transparent");
		transparentURL_trained = getStringFromJson(data,"transparent_trained");
		skill_name = getStringFromJson(data,"skill_name");
		jap_skill_name = getStringFromJson(data,"japanese_skill_name");
		skill_type = getStringFromJson(data,"i_skill_type");
		skill_desc = getStringFromJson(data,"skill_details");
		performance_min = getIntFromJson(data,"performance_min");
		performance_max = getIntFromJson(data,"performance_max");
		performance_trained_max = getIntFromJson(data,"performance_trained_max");
		technique_min = getIntFromJson(data,"technique_min");
		technique_max = getIntFromJson(data,"technique_max");
		technique_trained_max = getIntFromJson(data,"technique_trained_max");
		visual_min = getIntFromJson(data,"visual_min");
		visual_max = getIntFromJson(data,"visual_max");
		visual_trained_max = getIntFromJson(data,"visual_trained_max");
		cameo_members = getJSONArrayFromJson(data,"cameo_members");
		System.out.println("Card Data: "+this);
	}
	
	public static Card findCardByID(int cardID) {
		return GachaBot.card_idmap.get(cardID);
	}
	
	public static Card pickRandomCardByStarRating(int stars) {
		List<Card> cardList = GachaBot.card_raritymap.get(stars);
		return cardList.get((int)(Math.random()*cardList.size()));
	}
	
	public static Card pickRandomCardByMemberID(int memberID) {
		List<Card> cardList = GachaBot.card_membermap.get(memberID);
		return cardList.get((int)(Math.random()*cardList.size()));
	}
	
	public String getCardURL(boolean trained) {
		if (trained) {
			return imgURL_trained;
		} else {
			return imgURL;
		}
	}
	
	public String getCardArtURL(boolean trained) {
		if (trained) {
			return artURL_trained;
		} else {
			return artURL;
		}
	}
	
	public int getCardID() {
		return id;
	}
	
	public int getCardStarRating() {
		return rarity;
	}
	
	public int getMember() {
		return member;
	}
	
	int getIntFromJson(JSONObject data, String key) {
		if (data.has(key) && data.get(key) instanceof Integer) {
			return data.getInt(key);
		}
		return -1;
	}
	String getStringFromJson(JSONObject data, String key) {
		if (data.has(key) && data.get(key) instanceof String) {
			return data.getString(key);
		}
		return "";
	}
	JSONArray getJSONArrayFromJson(JSONObject data, String key) {
		if (data.has(key) && data.get(key) instanceof JSONArray) {
			return data.getJSONArray(key);
		}
		JSONArray empty = new JSONArray();
		return empty;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName()+"(");
		boolean first=false;
		for (Field f : this.getClass().getDeclaredFields()) {
			if (!first) {
				try {
					sb.append(f.getName()+"="+f.get(this));
					first=true;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			} else {
				try {
					sb.append(","+f.getName()+"="+f.get(this));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}
}
