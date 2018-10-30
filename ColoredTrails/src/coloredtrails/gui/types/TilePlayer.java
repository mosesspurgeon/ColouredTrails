package coloredtrails.gui.types;

import org.json.simple.JsonObject;

import coloredtrails.common.JsonSerializable;

/**
 * Enum describing different textures, different tile tyes
 * 
 * @author morvered
 *
 */
public enum TilePlayer implements JsonSerializable {

	// Grass("grassTexture",true),
	// Water("waterTexture",false),
	// Dirt("dirtTexture",true),
	SpaceshipTexture("spaceshipTexture"), Player("player"), GoalTexture("goalTexture");
	/*
	 * so your 200 pixel 2D sprite becomes a 256 pixel texture. It is a requirement,
	 * at least on old graphics hardware, that all textures had
	 * "two to the power something" width and height and Slick enforces this for
	 * you.
	 */

	public String playerName;
	// public boolean buildable;

	TilePlayer(String playerName) {// , boolean buildable){
		this.playerName = playerName;
		// this.buildable = buildable;
	}

	public static TilePlayer getTilePlayer(String tilePlayer) {
		for (TilePlayer tp : TilePlayer.values()) {
			if (tp.name().equals(tilePlayer))
				return tp;
		}
		return null;
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject tilePlayer = new JsonObject();
		tilePlayer.put("name", this.name());
		return tilePlayer;
	}

	public static TilePlayer getTilePlayer(JsonObject tilePlayer) {
		return getTilePlayer(tilePlayer.getString("name"));
	}

}
