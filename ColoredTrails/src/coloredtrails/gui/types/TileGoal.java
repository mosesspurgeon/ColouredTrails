package coloredtrails.gui.types;

import org.json.simple.JsonObject;

import coloredtrails.common.JsonSerializable;

public enum TileGoal implements JsonSerializable {

	// Grass("grassTexture",true),
	// Water("waterTexture",false),
	// Dirt("dirtTexture",true),
	Goal("goalTexture", false);
	/*
	 * so your 200 pixel 2D sprite becomes a 256 pixel texture. It is a requirement,
	 * at least on old graphics hardware, that all textures had
	 * "two to the power something" width and height and Slick enforces this for
	 * you.
	 */

	public String goalName;
	public boolean buildable;

	TileGoal(String goalName, boolean buildable) {
		this.goalName = goalName;
		this.buildable = buildable;
	}

	public static TileGoal getTileGoal(String goalName, boolean buildable) {
		for (TileGoal tg : TileGoal.values()) {
			if (tg.goalName.equals(goalName) && tg.buildable == buildable)
				return tg;
		}
		return null;
	}

	public static TileGoal getTileGoal(String tileGoal) {
		for (TileGoal tg : TileGoal.values()) {
			if (tg.name().equals(tileGoal))
				return tg;
		}
		return null;
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject tileGoal = new JsonObject();
		tileGoal.put("name", this.name());
		return tileGoal;
	}
	public static TileGoal getTileGoal(JsonObject tileGoal) {
		return getTileGoal(tileGoal.getString("name"));
	}
}
