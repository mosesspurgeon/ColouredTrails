package coloredtrails.common;

/*
Colored Trails

Copyright (C) 2006-2008, President and Fellows of Harvard College.  All Rights Reserved.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/



import java.util.Hashtable;

import org.json.simple.JsonObject;

import java.io.Serializable;

import coloredtrails.gui.types.HistoryEntry;

/**
<b>Description</b>

Represents the basic state information of a single player in a particular CT3 game.
<p>
This class allows for rudimentary representation of what a player can and cannot do.  
In particular, the booleans 'movesAllowed', 'transfersAllowed', and 'communicationAllowed' 
provide coarse control over the agent's permissions.
<p>
[it is yet unclear how these booleans are actually used to determine whether a 
player will be able to take some action; for example, are these booleans used by 
the game configuration class to decide what a player can and cannot do?]

<table width="100%" cellpadding=10 bgcolor=silver>
<tr><td>Method</td><td>Used By</td></tr>
<tr><td>areMovesAllowed()</td><td>.client.ClientGameStatus.myPlayerCanMove(), 
                                which is used by .client.ui.BoardPanel.drawPlayers()
                                and .BoardPanel.PanelBoardMouseListener.mouseReleased()</td></tr>
<tr><td>areMovesAllowed()</td><td>.server.ServerGameStatus.doMove()</td></tr>
</table>


<p>

<b>Observers</b>
GameStatus, [others?]

<p>

<b>Notifications</b>
<table width="100%" cellpadding=10 bgcolor=silver>
<tr><td>Method</td><td>Message Sent</td></tr>
<tr><td>setScore()</td><td>PLAYER_CHANGED</td></tr>
<tr><td>setMovesAllowed()</td><td>PLAYER_CHANGED</td></tr>
<tr><td>setChips()</td><td>PLAYER_CHANGED</td></tr>
<tr><td>setPosition()</td><td>PLAYER_CHANGED</td></tr>
</table>

<p>

<b>Issues</b>
Several 'set' methods do not cause notifications to be sent.  These are:
<ul>
<li>public void setTransfersAllowed(boolean transfersAllowed)</li>
<li>public void setCommunicationAllowed(boolean communicationAllowed)</li>
<li>public void setPerGameId(int perGameId)</li>
<li>public void setPin(int pin)</li>
</ul>

The first two methods above likely need correction.  The purpose of the 
latter two 'set' methods is less clear; 'setPin' in particular is surprising, 
as this value is set in the constructor and there appears to be no circumstance 
under which this should change for a player.  The 'setPerGameId' is similarly 
worth considering; the relationship between the game ID and the concept of a 
role needs to be clarified.  Is the game ID a short-hand for a particular setting 
of permissions and prohibitions that defines a role? If a player's role can change 
during a game, then would its ID change as well?  While the notion of a role is 
clear in simple games, we can imagine that more complex scenarios may lead to a 
combinatorial explosion of permissions and prohibitions, potentially making the 
concept of a role tenuous.
<p>
Alternatively, the game ID may exist simply to ease record keeping, and not have 
any deep relationship to player permissions.  In this case, the need for the 
'setPerGameId' method is unclear, given that this ID is provided to the class constructor.
<p>
The 'score' field appears to represent the outcome of a single game; we also need
a way to represent cumulative scores of an player over an experiment.  (Need to verify
that a PlayerStatus instance persists for a player over an entire experiment.)
<p>
The 'score' field is not updated by the system, for example, when a player moves
or trades chips; this calculation of score is most flexibly left to the 
GameConfigDetailsRunnable subclass that defines the game

<p>

<b>Future Development</b>
We can provide finer-grained representation of permissions in this class.  For example, 
we can specify to whom transfers are allowed, and with whom communication is allowed.  
Such enhancements provide the essential requirements for roles.

<p>

*
* @author Paul Heymann (ct3@heymann.be)
@author Sevan G. Ficici (modifications for partial visibility)
*/
public class PlayerStatus 
                      implements Serializable, Cloneable, JsonSerializable
{
/**
 * 
 */
private static final long serialVersionUID = -4078997047715675866L;
private String role;
private int perGameId;
private int pin;
private RowCol position;
private ChipSet chips;
private boolean movesAllowed;
private boolean transfersAllowed;
private boolean communicationAllowed;
private int teamId;
private int score;
private int myDormantRounds;
private int hisDormantRounds;
private int timeStampID;
private String relationship;


public PlayerStatus()
{
	// the role of this player; note initialization
	role = "";
	// the ID of this player in current game; note initialization
	perGameId = -1;
	// the global/persistent ID of this player; note initialization
	pin = -1;
	// position of player's piece on board; note initialization
	position =  new RowCol(-1, -1);
	// player's chips
	chips =  new ChipSet();
	// whether player is allowed to move on board
	movesAllowed = false;
	// whether player is allowed to transfer chips
	transfersAllowed = false;
	// whether player is allowed to send messages
	communicationAllowed = false;
	// team ID of this player; note initialization
	teamId = -1;
	// player's score for this game (not cumulative over all games?)
	score = 0;
    myDormantRounds=0;
    hisDormantRounds=0;
    // unique ID for questionnaire
    timeStampID=0;
    relationship = new String();
}

public PlayerStatus(int pin)
{
	this();
	this.pin = pin;
}

public PlayerStatus(int perGameId, int pin)
{
	this(pin);
	this.perGameId = perGameId;
}


public PlayerStatus(int perGameId, int pin,RowCol position,ChipSet chips,boolean movesAllowed,boolean transfersAllowed,
		boolean communicationAllowed,int teamId, int score, int myDormantRounds, int hisDormantRounds, int timeStampId, String relationship)
{
	this(perGameId,pin);
	this.position = new RowCol(position);
	this.chips = new ChipSet(chips);
	this.movesAllowed =  movesAllowed;
	this.transfersAllowed = transfersAllowed;
	this.communicationAllowed = communicationAllowed;
	this.teamId = teamId;
	this.score = score;
	this.myDormantRounds = myDormantRounds;
	this.hisDormantRounds = hisDormantRounds;
	this.timeStampID = timeStampId;
	this.relationship = new String(relationship);
}

/**
 * Copy Ctor
 * @param p
 */
public PlayerStatus(PlayerStatus p)
{
	this(p.getPerGameId(),p.getPin());
	this.position = new RowCol(p.getPosition().getxCoord(),p.getPosition().getyCoord());
	this.chips = new ChipSet(p.getChips());
	this.movesAllowed =  p.areMovesAllowed();
	this.transfersAllowed = p.areTransfersAllowed();
	this.communicationAllowed = p.isCommunicationAllowed();
	this.teamId = p.getTeamId();
	this.score = p.getScore();
	this.myDormantRounds = p.getMyDormantRounds();
	this.hisDormantRounds = p.getHisDormantRounds();
	this.timeStampID = p.getTimeStampID();
	this.relationship = p.getRelationship();
}

public Object clone()
{
	return new PlayerStatus(this);
}

// WE SHOULD MAKE SCORES double!
/**
 * Get the score of the player in the current game.
 *
 * @return The score of the player in the current game.
 */
public int getScore()
{
	return score;
}

public String getRelationship()
{
	return relationship;
}

public void setRelationship(String rltp)
{
	this.relationship = new String(rltp);
	//setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

/**
 * Set the score of the player in the current game.
 *
 * @param score The score of the player in the current game.
 */
public void setScore(int score)
{
	this.score = score;
    //setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

 public void setMyNumDormantRounds(int num)
{
	this.myDormantRounds = num;
    //setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

 public void setHisNumDormantRounds(int num)
{
	this.hisDormantRounds = num;
    //setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

 
 public void setTimeStampID(int tsID)
 {
 	this.timeStampID = tsID;
     //setChanged();
     //notifyObservers("PLAYER_CHANGED");
 }
 
 
 public int getTimeStampID()
 {
 	return timeStampID;
 }
 
public void setRound(int score)
{
	this.score = score;
    //setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

/**
 * Determine if the player is currently allowed to transfer chips.
 *
 * @return Whether the player is currently allowed to transfer chips.
 */
public boolean areTransfersAllowed()
{
	return transfersAllowed;
}

/**
 * Set whether the player is currently allowed to transfer chips.
 *
 * @param transfersAllowed Whether the player is currently allowed to
 *                         transfer chips.
 */
public void setTransfersAllowed(boolean transfersAllowed)
{
	this.transfersAllowed = transfersAllowed;
    //MONIRA
    //setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

/**
 * Determine if the player is allowed to send discourse messages.
 *
 * @return Whether the player is allowed to send discourse messages.
 */
public boolean isCommunicationAllowed()
{
	return communicationAllowed;
}

/**
 * Set whether the player is allowed to send discourse messages.
 *
 * @param communicationAllowed Whether the player is allowed to send
 *                             discourse messages.
 */
public void setCommunicationAllowed(boolean communicationAllowed)
{
	this.communicationAllowed = communicationAllowed;
    // MONIRA
    //setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

/**
 * Determine if the player is allowed to move.
 *
 * @return Whether the player is allowed to move.
 */
public boolean areMovesAllowed()
{
	return this.movesAllowed;
}

/**
 * Set whether the player is allowed to move.
 *
 * @param movesAllowed Whether the player is allowed to move.
 */
public void setMovesAllowed(boolean movesAllowed)
{
	this.movesAllowed = movesAllowed;
    //setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

/**
 * Get the chips allocated to the player.
 *
 * @return The chips allocated to the player.
 */
public ChipSet getChips()
{
	return chips;
}

public Integer getMyDormantRounds()
{
	return myDormantRounds;
}

 public Integer getHisDormantRounds()
{
	return hisDormantRounds;
}

/**
 * Set the chips allocated to the player.
 *
 * @param chips The chips allocated to the player.
 */
public void setChips(ChipSet chips)
{
	this.chips = chips;
    //setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

/**
 * Get the current position of the player on the board.
 *
 * @return The current position of the player on the board.
 */
public RowCol getPosition()
{
	return this.position;
}

/**
 * Set the current position of the player on the board.
 *
 * @param position The current position of the player on the board.
 */
public void setPosition(RowCol position)
{
	this.position.setxCoord(position.getxCoord());
	this.position.setyCoord(position.getyCoord());
    //setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

/**
 * Returns role
 * @return
 */
public String getRole()
{
	return role;
}

/**
 * Sets the player's role. This should probably be in ServerGameStatus
 * If set, this causes a field to appear in the GUI informing the client of the role
 * Agents should query it on their own
 * @param role Experimenter-defined role
 */
public void setRole(String role)
{
	this.role = role;
	//setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

/**
 * Get the per game id of the player.
 *
 * @return The per game id of the player.
 */
public int getPerGameId()
{
	return perGameId;
}

/**
 * Set the per game id of the player.
 *
 * @param perGameId The per game id of the player.
 */
public void setPerGameId(int perGameId)
{
	this.perGameId = perGameId;
	// presumably, we want to send update for this
	//setChanged();
    //notifyObservers("PLAYER_CHANGED");
}

/**
 * Gets the teamId of the player
 * @return teamId of the player
 */
public int getTeamId()
{
	return this.teamId;
}

/**
 * Sets the teamId of the player to the argument teamId
 * @param teamId New teamId of the player
 */
public void setTeamId(int teamId)
{
	this.teamId = teamId;
}

/**
 * Get the PIN of this player.
 *
 * @return The PIN of this player.
 */
public int getPin()
{
	return pin;
}

/**
 * Set the PIN of this player.
 *
 * @param pin The PIN of this player.
 */
public void setPin(int pin)
{
	this.pin = pin;
}

public String toString()
{
    return "Game Player...\n" +
            "PerGameId: " + getPerGameId() + ".\n" +
            "PIN: " + getPin() + ".\n" +
            "Pos: " + getPosition().toString() + ".\n" +
            "Chips: " + getChips().toString() + ".\n" +
            "Score: " + getScore() + ".\n" +
            "Team: " + getTeamId() + ".\n";
}

/**
 * Return a history entry describing this player status suitable
 * for adding to the history log.
 *
 * @param phaseName        The name of the phase when this discourse message
 *                         was sent.
 * @param phaseNum         The phase number of the phase when this discourse
 *                         message was sent.
 * @param secondsIntoPhase How many seconds into the current phase
 *                         the discourse message was sent.
 * @return A new HistoryEntry describing the player status.
 */
public HistoryEntry 
		toHistoryEntry(String phaseName, int phaseNum, int secondsIntoPhase)
{
    Hashtable<String, Object> entry = new Hashtable<String, Object>();
    entry.put("type", "playerStatus");
    entry.put("PerGameId", getPerGameId());
    entry.put("Position", getPosition());
    entry.put("Score", getScore());
    entry.put("ChipSet", getChips());
	
    return new HistoryEntry(phaseName, phaseNum, secondsIntoPhase, entry);
}

@Override
public JsonObject toJsonObject() {

	JsonObject playerStatus = new JsonObject();

	playerStatus.put("role", role);	
	playerStatus.put("perGameId", perGameId);	
	playerStatus.put("pin", pin);	
	playerStatus.put("position", position==null?null:position.toJsonObject());	
	playerStatus.put("chips", chips==null?null:chips.toJsonObject());
	playerStatus.put("movesAllowed", movesAllowed);	
	playerStatus.put("transfersAllowed", transfersAllowed);	
	playerStatus.put("communicationAllowed", communicationAllowed);	
	playerStatus.put("teamId", teamId);	
	playerStatus.put("score", score);	
	playerStatus.put("myDormantRounds", myDormantRounds);	
	playerStatus.put("hisDormantRounds", hisDormantRounds);	
	playerStatus.put("timeStampID", timeStampID);	
	playerStatus.put("relationship", relationship);	
	return playerStatus;
}

public PlayerStatus(JsonObject playerStatus) {
	this.role = playerStatus.getString("role");
	this.perGameId = playerStatus.getInteger("perGameId");
	this.pin = playerStatus.getInteger("pin");
	this.position = new RowCol((JsonObject)playerStatus.get("position"));
	this.chips = new ChipSet((JsonObject)playerStatus.get("chips"));	
	this.movesAllowed = playerStatus.getBoolean("movesAllowed");
	this.transfersAllowed = playerStatus.getBoolean("transfersAllowed");
	this.communicationAllowed = playerStatus.getBoolean("communicationAllowed");
	this.teamId = playerStatus.getInteger("teamId");
	this.score = playerStatus.getInteger("score");
	this.myDormantRounds = playerStatus.getInteger("myDormantRounds");
	this.hisDormantRounds = playerStatus.getInteger("hisDormantRounds");
	this.timeStampID = playerStatus.getInteger("timeStampID");
	this.relationship = playerStatus.getString("relationship");
		
}
}