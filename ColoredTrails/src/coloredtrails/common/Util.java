package coloredtrails.common;

import java.util.List;

import org.json.simple.JsonArray;

public class Util {

	// https://stackoverflow.com/questions/5439529/determine-if-a-string-is-an-integer-in-java
	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		// only got here if we didn't return false
		return true;
	}

	public static JsonArray toJsonList(List<? extends JsonSerializable> objs) {
		JsonArray array = new JsonArray();
		if (objs != null) {
			for (JsonSerializable obj : objs)
				array.add(obj==null?null:obj.toJsonObject());
		}
		return array;
	}

}
