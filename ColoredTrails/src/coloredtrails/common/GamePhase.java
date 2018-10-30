package coloredtrails.common;

import java.io.Serializable;

import org.json.simple.JsonObject;

public class GamePhase implements Serializable, Cloneable, JsonSerializable{
	/**
	 * the phase's name
	 */
	private final String name;
	/**
	 * the phase's duration in seconds
	 */
	private final int duration;
	/**
	 * flag indicating whether this game phase has indefinite length
	 */
	private final boolean indefinite;

	/**
	 * Private constructor that sets all the private values.
	 * @param name the phase's name
	 * @param duration the phase's duration in seconds
	 * @param indefinite whether the phase has indefinite duration.
	 */
	public GamePhase( String name, int duration, boolean indefinite )
	{
		// phase's name
		this.name = name;
		// phase's duration in seconds
		this.duration = duration;
		// flag indicating whether this game phase has indefinite length
		this.indefinite = indefinite;
	}

	/**
	 * Constructor
	 *
	 * @param name     the phase's name
	 * @param duration the phase's duration in seconds
	 */
	public GamePhase( String name, int duration )
	{
		this( name, duration, false );
	}

	/**
	 * Constructor for GamePhase with an indefinite duration
	 *
	 * @param name the phase's name
	 */
	public GamePhase( String name )
	{
		this( name, -1, true );
	}

	public GamePhase(JsonObject gamePhase )
	{
		// phase's name
		this.name = gamePhase.getString("name");
		// phase's duration in seconds
		this.duration = gamePhase.getInteger("duration");
		// flag indicating whether this game phase has indefinite length
		this.indefinite = gamePhase.getBoolean("indefinite");
	}
//	/**
//	 * Copy constructor
//	 * @param g The GamePhase to be coppied.
//	 */
//	public GamePhase( GamePhase g )
//	{
//		super(g);
//	}
//	
//	public Object clone()
//	{
//		return new GamePhase(this);
//	}

	public GamePhase(GamePhase g) {
		// TODO Auto-generated constructor stub
		this.duration = g.getDuration();
		this.indefinite = g.isIndefinite();
		this.name = g.getName();
	}

	/**
	 * Gets the name of the phase
	 *
	 * @return Name of the phase
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * Gets the duration of the phase
	 *
	 * @return Duration of the phase
	 */
	public int getDuration()
	{
		return this.duration;
	}
	
	

	/**
	 * Returns true if this game phase has indefinite length
	 *
	 * @return true if this is a indefinite phase.
	 */
	public boolean isIndefinite()
	{
		return this.indefinite;
	}

	@Override
	public JsonObject toJsonObject() {
		JsonObject gamePhase = new JsonObject();
		gamePhase.put("name", name);
		gamePhase.put("duration", duration);
		gamePhase.put("indefinite", indefinite);
		
		return gamePhase;
	}
}
