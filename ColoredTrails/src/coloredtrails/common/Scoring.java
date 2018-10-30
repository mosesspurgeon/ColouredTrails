package coloredtrails.common;
/*
Colored Trails

Copyright (C) 2006-2007, President and Fellows of Harvard College.  All Rights Reserved.

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

import java.io.Serializable;
import java.lang.Math;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

//import ctagents.FileLogger;
//import ctagents.alternateOffersAgent.ProposerResponderPlayer.EndingReasons;
//import edu.harvard.eecs.airg.coloredtrails.shared.types.*;

/**
 * Represents the rule used to score a player based on the game state
 * 
 * @author Sevan G. Ficici
 */
public class Scoring implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2723739772252499436L;
	/** points earned for reaching the goal */
	public int goalweight;
	/** penalty for each unit distance away from goal (< 0) */
	public int distweight;
	/** points earned for each chip in possession */
	public int chipweight;

	// This is the default score
	double basescore = 0.0;

	/** weights for each color */
	// NEED TO CHANGE THIS and clarify semantics (SGF)
	private HashMap<CtColor, String> colorweights = null;

	/**
	 * Constructor.
	 * 
	 * @param goalweight
	 *            value of being at the goal
	 * @param distweight
	 *            cost of unit distance from goal (<= 0)
	 * @param chipweight
	 *            value of chip still in possession
	 */
	public Scoring(int goalweight, int distweight, int chipweight) {
		this.goalweight = goalweight;
		this.distweight = distweight;
		this.chipweight = chipweight;
	}

	public JsonObject toJsonObject() {
		JsonObject scoring = new JsonObject();
		scoring.put("goalweight", goalweight);
		scoring.put("distweight", distweight);
		scoring.put("chipweight", chipweight);
		scoring.put("basescore", basescore);

		JsonArray colorweightsArray = new JsonArray();
		if (colorweights != null) {
			for (Map.Entry<CtColor, String> entry : colorweights.entrySet()) {
				JsonObject colorweight = new JsonObject();
				CtColor key = entry.getKey();
				String value = entry.getValue();
				colorweight.put("ctColor", key.toJsonObject());
				colorweight.put("string", value);
				colorweightsArray.add(colorweight);
			}
		}
		scoring.put("colorweights", colorweightsArray);

		return scoring;
	}

	public Scoring(JsonObject scoring) {
		this.goalweight = scoring.getInteger("goalweight");
		this.distweight = scoring.getInteger("distweight");
		this.chipweight = scoring.getInteger("chipweight");
		this.basescore = scoring.getDouble("basescore");
		JsonArray colorweightsArray = (JsonArray) scoring.get("colorweights");
		for (int i = 0; i < colorweightsArray.size(); i++) {
			JsonObject colorweight = (JsonObject) colorweightsArray.get(i);
			CtColor key = new CtColor((JsonObject) colorweight.get("ctColor"));
			String value = (String) colorweight.get("string");
			if (colorweights == null)
				colorweights = new HashMap<CtColor, String>();
			colorweights.put(key, value);
		}

	}

	/**
	 * 
	 * @param goalweight
	 *            value of being at the goal
	 * @param distweight
	 *            cost of unit distance from goal (<= 0)
	 * @param chipweight
	 *            value of chip still in possession
	 * @param defaultscore
	 *            base score (equals zero unless specified here)
	 */
	public Scoring(int goalweight, int distweight, int chipweight, double basescore) {
		this.goalweight = goalweight;
		this.distweight = distweight;
		this.chipweight = chipweight;
		this.basescore = basescore;
	}

	/**
	 * Constructor
	 * 
	 * @param goalweight
	 *            value of being at the goal
	 * @param distweight
	 *            cost of unit distance from goal (<= 0)
	 * @param chipweight
	 *            value of chip still in possession
	 * @param colorweights
	 *            weights for each color
	 */
	public Scoring(int goalweight, int distweight, int chipweight, HashMap<CtColor, String> colorweights) {
		this.goalweight = goalweight;
		this.distweight = distweight;
		this.chipweight = chipweight;
		this.colorweights = colorweights;
	}

	/**
	 * Copy constructor
	 */
	public Scoring(Scoring scoring) {
		this.goalweight = scoring.goalweight;
		this.distweight = scoring.distweight;
		this.chipweight = scoring.chipweight;
		this.colorweights = scoring.colorweights;
	}

	/**
	 * Calculates the score of an agent with the specified state. NOTE that we need
	 * to generalize this to accept an entire game state; we would then add an
	 * argument to indicate which player we are interested to score. NOTE also that
	 * we are applying color weights for chips that are in posession, but are not
	 * using weights in calculating the penalty for not reaching the goal; of
	 * course, given a distance N from the goal, there may be many length-N paths,
	 * and each such path may traverse a different set of colors. Thus, there may
	 * not be a unique set of colors to weight in calculating the penalty; we would
	 * need to decide how we'd apply the weights to calculate the penalty.
	 * 
	 * @param ps
	 *            an agent's state
	 * @param gpos
	 *            the target goal's location
	 * @return the agent's score
	 */
	public double score(PlayerStatus ps, RowCol gpos) {

		double score = basescore;
		int manhattan = getManhattanDist(ps,gpos);

		if (manhattan == 0)
			score += goalweight;
		else
			score += distweight * manhattan;

		score += getChipSetWeight(ps.getChips());

		return score;
	}
	
	
	public int getManhattanDist(PlayerStatus ps, RowCol gpos) {
		RowCol ppos = ps.getPosition();

		int delta_r = Math.abs(ppos.getRow() - gpos.getRow());// rows
		int delta_c = Math.abs(ppos.getCol() - gpos.getCol());// columns
		int manhattan = delta_r + delta_c;
		return manhattan;
		
	}

	//// Leaving this inProcessScore for now since is used by
	//// ProposerResponderPlayer:calcMoveUtilityFunction and
	//// I don't want to touch that part that works fine.
	//// Revise!
	// public double inProcessScore(PlayerStatus ps, RowCol gpos,
	// int numOfChipsLacking, EndingReasons isGameAboutToEnd) {
	// RowCol ppos = ps.getPosition();
	//
	// int delta_r = Math.abs(ppos.row - gpos.row);
	// int delta_c = Math.abs(ppos.col - gpos.col);
	// int manhattan = delta_r + delta_c;
	//
	// double score = 0.0;
	// double prob = 0.0;
	// if (manhattan == 0 || numOfChipsLacking == 0)
	// score += goalweight;
	// else {
	// // If game is about to end because opponent is one square away from
	// // the goal and it has the chip to get there
	// // the game is lost to me and my probability to get to the goal is
	// // zero.
	// if (isGameAboutToEnd == EndingReasons.OPP_ONE_STEP_FROM_GOAL)
	// // && numOfChipsLacking > 0)
	// prob = 0.0;
	// // If the game is about to end because I haven't moved consecutively
	// // maximum allowed -1 and the set chips I will have
	// // if the agreement is kept will allow me to move this round then
	// // the probability of getting to the goal is bigger.
	// // If the game is about to end because opponent hasn't moved
	// // consecutively maximum allowed -1 and the set chips she will
	// // if the agreement is kept will allow me to move this round then
	// // the probability of getting to the goal is bigger.
	// // If the game is not about to end then prob = 1.0 -
	// // (double)numOfChipsLacking/ manhattan;
	// else if ((isGameAboutToEnd == EndingReasons.I_HAVE_TO_MOVE)
	// || (isGameAboutToEnd == EndingReasons.OPP_HAS_TO_MOVE))
	// prob = 0.8;
	// else
	// prob = 1.0 - (double) numOfChipsLacking / manhattan;
	// score += prob * goalweight + (1 - prob) * distweight * manhattan;
	// }
	// // TODO check this part of the score, In this manner we are summing up
	// // the weight of the chips we have now, before we use them.
	// score += getChipSetWeight(ps.getChips());
	//
	// return score;
	// }
	//
	// public double inProcessScore(PlayerStatus ps, RowCol gpos,
	// int numOfChipsLacking, EndingReasons isGameAboutToEnd,
	// boolean willExchangeAllowMovementAgent,
	// boolean willExchangeAllowMovementOther) {
	// RowCol ppos = ps.getPosition();
	//
	// int delta_r = Math.abs(ppos.row - gpos.row);
	// int delta_c = Math.abs(ppos.col - gpos.col);
	// int manhattan = delta_r + delta_c;
	// FileLogger.getInstance("Agent").writeln(
	// " In inProcessScore, manhattan is: " + manhattan);
	//
	// double score = 0.0;
	// double prob = 0.0;
	// if (manhattan == 0 || numOfChipsLacking == 0)
	// score += goalweight;
	// else {
	// // If game is about to end because opponent is one square away from
	// // the goal and it has the chip to get there
	// // the game is lost to me and my probability to get to the goal is
	// // zero.
	// if (isGameAboutToEnd == EndingReasons.OPP_ONE_STEP_FROM_GOAL)
	// // && numOfChipsLacking > 0)
	// prob = 0.0;
	// // If the game is about to end because I haven't moved consecutively
	// // maximum allowed -1 and the set chips I will have
	// // if the agreement is kept will allow me to move this round then
	// // the probability of getting to the goal is bigger.
	// // If the game is about to end because opponent hasn't moved
	// // consecutively maximum allowed -1 and the set chips she will
	// // if the agreement is kept will allow me to move this round then
	// // the probability of getting to the goal is bigger.
	// // If the game is not about to end then prob = 1.0 -
	// // (double)numOfChipsLacking/ manhattan;
	// else if (isGameAboutToEnd == EndingReasons.I_HAVE_TO_MOVE
	// && !willExchangeAllowMovementAgent)
	// prob = 0.0;
	// else if (isGameAboutToEnd == EndingReasons.OPP_HAS_TO_MOVE
	// && !willExchangeAllowMovementOther)
	// prob = 0.0;
	// else if ((isGameAboutToEnd == EndingReasons.I_HAVE_TO_MOVE)
	// || (isGameAboutToEnd == EndingReasons.OPP_HAS_TO_MOVE))
	// prob = 0.8;
	// else
	// prob = 1.0 - (double) numOfChipsLacking / manhattan;
	// score += prob * goalweight + (1 - prob) * distweight * manhattan;
	// }
	// // TODO check this part of the score, In this manner we are summing up
	// // the weight of the chips we have now, before we use them.
	// score += getChipSetWeight(ps.getChips());
	//
	// FileLogger.getInstance("Agent")
	// .writeln(
	// " In inProcessScore: chips weight = "
	// + getChipSetWeight(ps.getChips()) + " score = "
	// + score);
	// return score;
	// }

	public String toString() {
		StringBuffer sb = new StringBuffer(
				"GoalWeight: " + goalweight + "  DistWeight: " + distweight + "  ChipWeight: " + chipweight);
		return sb.toString();
	}

	/**
	 * Sets the HashMap of color weights to the argument
	 * 
	 * @param hm
	 *            HashMap of the colors' weights
	 * @author ilke
	 */
	public void setColorWeights(HashMap<CtColor, String> hm) {
		colorweights = hm;
	}

	/**
	 * Sets the weight of the specified color
	 * 
	 * @param color
	 *            the color whose weight we are setting
	 * @param weight
	 *            the weight of the color
	 */
	public void setColorWeight(CtColor color, int weight) {
		if (colorweights == null)
			colorweights = new HashMap<CtColor, String>();

		colorweights.put(color, Integer.toString(weight));
	}

	/**
	 * Gets the weight of a specific color
	 * 
	 * @param color
	 *            Color
	 * @return Weight of the color
	 * @author ilke
	 */
	public int getColorWeight(CtColor color) {
		if (colorweights == null)
			return chipweight;
		else
			return Integer.parseInt((String) (colorweights.get(color)));
	}

	public int getChipSetWeight(ChipSet cs) {
		int sum = 0;
		for (CtColor color : cs.getColors())
			sum += cs.getNumChips(color) * getColorWeight(color);

		return sum;
	}
}