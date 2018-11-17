package sig.gacha;

import java.util.List;

import org.json.JSONObject;

public class Member {
	int id;
	String name;
	String jap_name;
	String imageURL;
	String square_imageURL;
	String band;
	String school;
	String school_year;
	String va;
	String jap_va;
	String birthday;
	String food_likes;
	String food_dislikes;
	String horoscope;
	String hobbies;
	String desc;
	
	public Member(JSONObject data) {
		id = getIntFromJson(data,"id");
		name = getStringFromJson(data,"name");
		jap_name = getStringFromJson(data,"japanese_name");
		imageURL = getStringFromJson(data,"image");
		square_imageURL = getStringFromJson(data,"square_image");
		band = getStringFromJson(data,"i_band");
		school = getStringFromJson(data,"school");
		school_year = getStringFromJson(data,"i_school_year");
		va = getStringFromJson(data,"romaji_CV");
		jap_va = getStringFromJson(data,"CV");
		birthday = getStringFromJson(data,"birthday");
		food_likes = getStringFromJson(data,"food_likes");
		food_dislikes = getStringFromJson(data,"food_dislikes");
		horoscope = getStringFromJson(data,"i_astrological_sign");
		hobbies = getStringFromJson(data,"hobbies");
		desc = getStringFromJson(data,"description");
	}
	
	public static Member getMemberByName(List<Member> database, String name) {
		for (Member m : database) {
			if (m.name.split(" ")[0].equalsIgnoreCase(name)) {
				return m;
			}
		}
		return null;
	}
	
	public static Member getMemberByID(List<Member> database, int id) {
		for (Member m : database) {
			if (m.id==id) {
				return m;
			}
		}
		return null;
	}
	
	public int getMemberID() {
		return id;
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
}
