package data;

import javax.swing.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Observable;
import java.io.Serializable;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Phases implements ActionListener, Serializable, Cloneable{
	/**
	 * the sequence of game phases
	 */
	protected List<GamePhase> phaseSequence;

	/**
	 * the previous phase
	 */
	private GamePhase previous;

	/**
	 * the current phase
	 */
	private GamePhase current;

	/**
	 * number of phases elapsed
	 */
	private int phasesElapsed = 0;

	/**
	 * flag indicating whether the phase sequence loops or not
	 */
	protected boolean isLoop = false;

	/**
	 * The number of seconds that have elapsed in the current phase.
	 */
	protected int currentelapsed;

	/**
	 * The number of seconds left in the current phase.
	 */
	protected int currentleft;
	
	public long time = System.currentTimeMillis();


	/**
	 * The timer used to count the seconds of phases.
	 */
	public static final Timer tm = new Timer(1000, null);
	static { tm.start(); }
	
	public static boolean isStarted()
	{
		return tm.isRunning();
	}


	/***************************************************************
		Constructors
	 ***************************************************************/
	
	/**
	 * Constructor with no phases.
	 */
	protected Phases()
	{
		// the sequence of game phases
		this.phaseSequence =  new ArrayList<GamePhase>();
		// the previous game phase
		this.previous = null;
		// the current game phase
		this.current = null;
		// number of seconds that have elapsed in current phase
		this.currentelapsed = 0;
		// number of seconds left in current phase
		this.currentleft = 0;
		// number of phases that have elapsed
		this.phasesElapsed = 0;
		// flag indicating whether the phase sequence loops
		this.isLoop = false;
		// current time
		this.time =  new Long(System.currentTimeMillis());

		tm.addActionListener(this);
	}
	
	
	protected Phases( ArrayList<GamePhase> arr) {
		this.setPhaseSequence(arr);
		
		// the previous game phase
		this.previous = null;
		// number of phases that have elapsed
		this.phasesElapsed = 0;
		// the current game phase
		this.current = arr.get(this.phasesElapsed);
		// number of seconds that have elapsed in current phase
		this.currentelapsed = 0;
		// number of seconds left in current phase
		this.currentleft = this.current.getDuration();
		
		// flag indicating whether the phase sequence loops
		this.isLoop = false;
		// current time
		this.time =  new Long(System.currentTimeMillis());

		tm.addActionListener(this);
	}
	
	
	

	/**
	 * Coppy constructor
	 * @param p The Phase to be coppied.
	 */
	public Phases( Phases p )
	{
		//super(p);
		
		this.phaseSequence =  new ArrayList<GamePhase>();
		for( GamePhase h : p.getPhaseSequence() )
		{
			GamePhase g = new GamePhase( h.getName(),h.getDuration(),h.isIndefinite() );
			if( p.getCurrentPhase() == h )
				this.current = g;
			else if( p.getPreviousPhase() == h )
				this.previous = g;
			getPhaseSequence().add( g );
		}
		
		
/*
		this();

		phasesElapsed = p.phasesElapsed;
		isLoop = p.isLoop;

		for( GamePhase h : p.phaseSequence ) {
			GamePhase g = new GamePhase( h );
			if( p.current == h )
				current = g;
			else if( p.previous == h )
				previous = g;
			phaseSequence.add( g );
		}

		this.currentelapsed = p.currentelapsed;
		this.currentleft    = p.currentleft;
		
		this.time = p.time;
*/
	}
	
	public Object clone()
	{
		return new Phases(this);
	}
	
	
	/***************************************************************
		Accessors
	 ***************************************************************/

	/**
		Returns the game phase sequence
	*/
	public List<GamePhase> getPhaseSequence()
	{
		return this.phaseSequence;
	}
	
	/**
		Returns the previous game phase that was actually played
	*/
	protected GamePhase getPreviousPhase()
	{
		return this.previous;
	}
	
	/**
		Returns the current game phase
	*/
	public GamePhase getCurrentPhase()
	{
		return this.current;
	}
	
	/**
		Returns the number of phases that have elapsed
	*/
	public int getPhasesElapsed()
	{
		return this.phasesElapsed;
	}
	
	/**
		Returns true if the phase loops, false otherwise
	*/
	public boolean getIsLoop()
	{
		return this.isLoop;
	}
	
	/**
		Returns the number of seconds left in the current phase
	*/
	public int getCurrentSecsLeft()
	{
		return this.currentleft;
	}
	
	/**
		Returns the number of seconds elapsed in the current phase
	*/
	public int getCurrentSecsElapsed()
	{
		return this.currentelapsed;
	}
	
	public long getTime()
	{
		return this.time;
	}
	
	
	/***************************************************************
		Basic Phases methods
	 ***************************************************************/
	
	/**
	 * Gets the index of the specified phase in the phase sequence
	 *
	 * @param phaseName Name of the phase
	 * @return Index of the phase
	 */
	public int getPhaseIndex( String phaseName )
	{
		GamePhase gp = getPhase( phaseName );
		int i = getPhaseSequence().indexOf( gp );
		if( 0 <= i )
			return i;

		throw new Error( "Phases.getPhaseIndex(): unrecognized phase name " + phaseName );
	}

	/**
	 * Returns true if the parameter is a phase name, false otherwise
	 *
	 * @param phaseName Name to be checked
	 * @return phaseName is a phase name or not
	 */
	public boolean contains( String phaseName ) {
		return getPhase( phaseName ) != null;
	}

	/**
	 * Returns the number of phases in phase sequence
	 *
	 * @return Number of phases
	 */
	public int getNumPhases()
	{
		return getPhaseSequence().size();
	}
	
	
	/***************************************************************
		Methods for getting GamePhases and GamePhase names
	 ***************************************************************/
	
	/**
	 * Returns the game phase with the specified name
	 * or null if specified name not found
	 *
	 * @param phaseName the name of the phase.
	 * @return the game phase with name phaseName, or null if it was not found.
	 */
	protected GamePhase getPhase( String phaseName )
	{
		for( GamePhase gp : getPhaseSequence() )
			if( gp.getName().equals( phaseName ) )
				return gp;
		
		return null;
	}
	
	/**
	 * Gets the name of the current phase
	 *
	 * @return Name of the current phase
	 */
	public String getCurrentPhaseName()
	{
		//TODO: this should probably return null instead of "" when (current == null)
		return ( getCurrentPhase() == null ) ? "" : getCurrentPhase().getName();
	}

	/**
	 * Gets the next Phase
	 *
	 * @return the next Phase in the list of Phases.  If the current Phase is null then the first Phase in the list
	 *         is returned.  If the Phase is the last Phase and there are no loops then null is returned, otherwise the
	 *         first Phase in the list is returned.
	 */
	public GamePhase getNextPhase()
	{
		return getNextPhase( getCurrentPhase() );
	}

	public String getNextPhaseName() {
		return getNextPhase().getName();
	}

	/**
	 * Returns the name of the phase that succeeds the specified phase
	 * in the Phases sequence
	 *
	 * @param gp phase whose successor we want
	 * @return Name of the next phase
	 */
	public GamePhase getNextPhase( GamePhase gp )
	{
		if( gp == null )
		{
			return getPhaseSequence().get( 0 );
		} 
		else if( getLastPhase() == gp )
		{
			if( !getIsLoop() )
				return null;
			return getPhaseSequence().get( 0 );
		}
		
		return getPhaseSequence().get( getPhaseSequence().indexOf( gp ) + 1 );
	}

	public String getNextPhaseName( GamePhase gp ) {
		return getNextPhase( gp ).getName();	
	}
	
	/**
	 * Gets the name of the previous phase that actually executed
	 * (NOTE that the phase that actually preceeded the current phase
	 * may have been something other than what preceeds the current
	 * phase in the Phases sequence; this is because we can manually
	 * jump arbitrarily between phases)
	 *
	 * @return Name of the previous phase
	 */
	public String getPreviousPhaseName()
	{
		return getPreviousPhase() == null ? "" : getPreviousPhase().getName();
	}

	/**
	 * Gets the name of the phase that appears before the specified
	 * phase in the Phases sequence
	 *
	 * @param gp GamePhase to be referenced
	 * @return Name of the previous phase
	 */
	public GamePhase getPreviousPhase( GamePhase gp )
	{
		if( gp == null )
			return null;

		if( gp == getPhaseSequence().get(0) )
			if( getIsLoop() )
				return getLastPhase();
			else
				return null;
		else
			return getPhaseSequence().get( getPhaseSequence().indexOf( gp ) - 1 );
	}

	/**
	 * Gets the name of the phase that appears before the specified
	 * phase in the Phases sequence
	 *
	 * @param refPhaseName Name of the phase to be referenced
	 * @return Name of the previous phase
	 */
	public String getPreviousPhaseName( String refPhaseName )
	{
		if( refPhaseName.equals( "" ) )
			return "";

		GamePhase gp = getPreviousPhase( getPhase( refPhaseName ) );
		if( gp == null )
			return "";
		return gp.getName();
	}

	/**
	 * Gets the last phase of the phase sequence.
	 *
	 * @return The last phase of the phase sequence.
	 */
	protected GamePhase getLastPhase()
	{
		return (GamePhase)getPhaseSequence().get( getPhaseSequence().size() - 1 );
	}

	/**
	 * Gets the name of the last phase of the phase sequence.
	 *
	 * @return The last phase of the phase sequence.
	 */
	public String getLastPhaseName() {
		return getLastPhase().getName();
	}

	/* **************************************************************
		 *
		 * Methods for advancing phases.
		 *
		 */

	/**
	 * Ends the current game phase and advances the
	 * game to the specified game phase
	 *
	 * @param phaseName the name of the phase.
	 */
	public void advanceToPhase( String phaseName ) {
		advanceToPhase( getPhase( phaseName ) );
	}

	private synchronized void advanceToPhase( GamePhase phase )
	{
		if( phase == null )
		{
			if( getCurrentPhase() != null )
				endPhase();  // end the current phase before moving to next phase
			
			this.previous = this.getCurrentPhase();  // note the phase that is ending
			this.current = null;
//			remove("current");
			
			return;
		}

		if( getCurrentPhase() != null )
			endPhase();  // end the current phase before moving to next phase
		
		// note the phase that is ending
		this.previous = this.getCurrentPhase();
		this.current = phase;
		
		this.currentleft = this.getCurrentPhase().getDuration();
		this.currentelapsed = 0;
		
		
		this.time = System.currentTimeMillis();
		//setChanged();
		//notifyObservers( "PHASE_ADVANCED" );
		//clearChanged();
		
		beginPhase();            // eventually invokes game configuration class
	}


	/**
	 * Ends the current game phase and advances the
	 * game to the next phase in the Phases sequence
	 * (may be invoked manually in configuration class
	 * or automatically by a phase time out)
	 */
	public synchronized void advancePhase() {
		GamePhase nextPhase = getNextPhase();
		advanceToPhase( nextPhase );
	}

	/**
	 * Ends the phase; eventually invokes endPhase() in game configuration class
	 */
	protected void endPhase()
	{
		this.phasesElapsed =  getPhasesElapsed() + 1;
	}

	/**
	 * Begins the phase; eventually invokes the beginPhase() in game configuration class
	 */
	protected void beginPhase() { }

	/* **************************************************************
		 *
		 * Methods for dealing with the timing of phases.
		 *
		 */
	
  /**
   * Called when an action e is performed
   */
	public void actionPerformed( ActionEvent e)
	{
		this.currentelapsed = getCurrentSecsElapsed() + 1;
		
		if( (getCurrentPhase() != null) && (! getCurrentPhase().isIndefinite()) )
		{
			this.currentleft = getCurrentSecsLeft() - 1;
			// if game phase is not indefinite, and decremented time is <= 0 ...
			if( getCurrentSecsLeft() <= 0 )
				advancePhase();
		}
	}

	/**
	 * Gets the duration of the specified phase
	 *
	 * @param phaseName Name of the phase whose duration we want
	 * @return Duration of the phase
	 */
	public int getPhaseDuration( String phaseName ) {
		if( phaseName.equals( "" ) )
			return 0;

		GamePhase gp = getPhase( phaseName );
		if( gp != null )
			return gp.getDuration();

		throw new Error( "Phases.getPhaseDuration(): unrecognized phase name " + phaseName );
	}

	/**
	 * Gets the duration of the current phase
	 *
	 * @return Duration of the current phase
	 */
	public int getPhaseDuration()
	{
		return getPhaseDuration( getCurrentPhase().getName() );
	}
	
	
	public String toString()
	{
		String str = "Phases: " + this.hashCode() + "\n";
		for( GamePhase g : getPhaseSequence() )
			str += g.getName() + " " + g.hashCode() + ",  ";
		return str;
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
	             throws java.io.IOException
	{
		 out.writeObject( getPhaseSequence() );
		 out.writeObject( getPreviousPhase() );
		 out.writeObject( getCurrentPhase() );
		 out.writeInt( getPhasesElapsed() );
		 out.writeBoolean( getIsLoop() );
		 out.writeInt( getCurrentSecsElapsed() );
		 out.writeInt( getCurrentSecsLeft() );
		 out.writeLong( getTime() );
	 }
	 
	private void readObject(java.io.ObjectInputStream in)
	             throws java.io.IOException, ClassNotFoundException
	{
		this.phaseSequence = (List<GamePhase>) in.readObject();
		this.previous = (GamePhase) in.readObject();
 		this.current = (GamePhase) in.readObject();
 		this.phasesElapsed = in.readInt();
		this.isLoop = in.readBoolean();
		this.currentelapsed = in.readInt();
		this.currentleft = in.readInt();
		this.time = in.readLong();
	}
	
	public void finalize() throws Throwable {
	    tm.removeActionListener(this);
	    super.finalize();
	}


	public GamePhase getPrevious() {
		return previous;
	}


	public void setPrevious(GamePhase previous) {
		this.previous = previous;
	}


	public GamePhase getCurrent() {
		return current;
	}


	public void setCurrent(GamePhase current) {
		this.current = current;
	}


	public int getCurrentelapsed() {
		return currentelapsed;
	}


	public void setCurrentelapsed(int currentelapsed) {
		this.currentelapsed = currentelapsed;
	}


	public int getCurrentleft() {
		return currentleft;
	}


	public void setCurrentleft(int currentleft) {
		this.currentleft = currentleft;
	}


	public static Timer getTm() {
		return tm;
	}


	public void setPhaseSequence(List<GamePhase> phaseSequence) {
		this.phaseSequence = new ArrayList<GamePhase>();
		for(GamePhase g : phaseSequence)
			this.phaseSequence.add(new GamePhase(g));
	}


	public void setPhasesElapsed(int phasesElapsed) {
		this.phasesElapsed = phasesElapsed;
	}


	public void setLoop(boolean isLoop) {
		this.isLoop = isLoop;
	}


	public void setTime(long time) {
		this.time = time;
	}
}
