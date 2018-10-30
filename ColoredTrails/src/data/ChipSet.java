package data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


import ui.CtColor;


public class ChipSet implements Cloneable{
	private ArrayList<CtColor> chips;
	private int numChips;
	
	
	
	public ChipSet() {
		chips = new ArrayList<CtColor>();
		numChips = 0;
	}
	
	/**
	 * A Ctor that generates a random chipset distribution.
	 * @param numOfChips
	 */
	public ChipSet(int numChips) {
		this.numChips = numChips;
		chips = new ArrayList<CtColor>();
		generateRandomChips();
	}
	
	/**
	 * Copy Ctor
	 * @param cs
	 */
	public ChipSet(ChipSet cs) {
		numChips = cs.numChips;
		chips = new ArrayList<CtColor>();
		for(CtColor chip : cs.getChips()) {
			chips.add(new CtColor(chip.getColorNum()));
		}
	}
	
	public ChipSet(CtColor color) {
		//create empty chips array
		chips = new ArrayList<CtColor>();
		this.setNumChips(color, 1);
	}
	
	public Object clone() {
		return new ChipSet(this);
	}
	
	void generateRandomChips() {
		Random randomGenerator = new Random();
		int randomNum;
		
		for(int i=0; i<numChips;i++) {
			//The colors used are according to indexes. 
			randomNum = randomGenerator.nextInt(Board.getNum_colors_used());
			this.chips.add(new CtColor(randomNum));
		}
		
	}

	public ArrayList<CtColor> getChips() {
		return chips;
	}

	public void setChips(ArrayList<CtColor> chips) {
		this.chips = chips;
	}

	/**
	 * Returns overall num of chips
	 * @return
	 */
	public int getNumChips() {
		return numChips;
	}
	
	/**
	 * Gets the number of chips of that specific color index
	 * @param color
	 * @return
	 */
	public int getNumChips(CtColor color) {
		int num = 0;
		for(CtColor chip : chips) {
			if( chip.getColorNum() == color.getColorNum() )
				num++;
		}
		return num;
	}

	public void setNumChips(int numOfChips) {
		this.numChips = numChips;
	}
	
	
	
	
	/**
	 * Determine whether a chipset is completely contained within this one. In
	 * other words, for every chip in the contained chipset, is there a chip of
	 * the same color in the container chipset? Throws a
	 * NegativeChipSetException when one of the chipsets has negative amount of
	 * chips
	 * 
	 * @param contained
	 *            The possibly contained chipset.
	 * @return Whether this chipset contains the contained chipset.
	 */
	public boolean contains(ChipSet contained)
    {
    	try
    	{
    		// check this chipset for negative values
	    	for (CtColor color : chips)
	    		if (getNumChips(color) < 0)
	    		   	throw new Exception("NegativeChipSetException");
			
			// check other chipset of negative values
	    	for (CtColor color : contained.getChips())
	    		if (contained.getNumChips(color) < 0)
	    			throw new Exception("NegativeChipSetException");
			
			// check for containment
	        ChipSet subend = subChipSets(this, contained);
	        for (CtColor color : subend.getChips())
	            if (subend.getNumChips(color) < 0)
	                return false;

	        return true;
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
		return false;
    }
	
	public boolean contains(CtColor color) {
		for(CtColor c : chips) {
			if(color.getColorNum() == c.getColorNum())
				return true;
		}
		return false;
	}
	
	
	/**
	 * Subtract the second given chipset from the first, returning a new chipset
	 * representing the result.
	 * 
	 * @param initial
	 *            The chipset to be subtracted from.
	 * @param subtracted
	 *            The chipset to be subtracted.
	 * @return A new chipset representing the result of the subtraction
	 *         operation.
	 */
	public static ChipSet subChipSets(ChipSet initial, ChipSet subtracted) {
		ChipSet newChipSet = new ChipSet(initial);
		ArrayList<CtColor> colors = subtracted.getChips();
		for(CtColor subtract_color : colors) {
			ArrayList<CtColor> chips = newChipSet.getChips();
			for(CtColor color : chips) {
				if(color.getColorNum() == subtract_color.getColorNum()) {
					int formerNumChips = newChipSet.getNumChips(color);
					newChipSet.setNumChips(color, formerNumChips-1);
					//initial.setNumChips(initial.getNumChips()-1);
					break;
				}
			}
		}
		
		return newChipSet;
		
	}
	
	
	/**
	 * Get the negation of a chipset, e.g., the chipset with all chip totals
	 * multiplied by -1.
	 * 
	 * @param cs
	 *            The chipset to negate.
	 * @return A new chipset which is the negation of the given chipset.
	 */
	public static ChipSet getNegation(ChipSet cs) {
		ChipSet newchips = new ChipSet();

		for (CtColor color : cs.getColors())
			newchips.setNumChips(color, -1 * cs.getNumChips(color));

		return newchips;
	}
	
	
	public boolean isEmpty() {
		return getNumChips() == 0;
	}
	
	
	/**
	 * Add two chipsets, adding the numbers of each color in each chipset.
	 * 
	 * @param cs1
	 *            The first chipset to be added.
	 * @param cs2
	 *            The second chpiset to be added.
	 * @return A new chipset representing the sum of the two component chipsets.
	 */
	public static ChipSet addChipSets(ChipSet cs1, ChipSet cs2) {
		ChipSet cs = new ChipSet();

		HashSet<CtColor> allcolors = new HashSet<CtColor>();
		allcolors.addAll(cs1.getColors());
		allcolors.addAll(cs2.getColors());

		for (CtColor color : allcolors)
			cs.setNumChips(color,
					cs1.getNumChips(color) + cs2.getNumChips(color));

		return cs;
	}
	
	/**
	 * If the color exists in the chipset, remove it, reduce the number of chips of that color, and the 
	 * overall number of chips and return the chipset, otherwise return null
	 * @param cs1
	 * @param subtract_color
	 * @return
	 */
	public static ChipSet subtractColor(ChipSet cs1, CtColor subtract_color) {
		ArrayList<CtColor> chips = cs1.getChips();
		for(CtColor color : chips) {
			if(color.getColorNum() == subtract_color.getColorNum()) {
				int formerNumChips = cs1.getNumChips(color);
				cs1.setNumChips(color, formerNumChips-1);
				cs1.setNumChips(cs1.getNumChips()-1);
				return cs1;
			}
		}
		
		return null;
	}
	
	
	public static void addColor(ChipSet cs1, CtColor add_color) {
		
		int formerNumChips = cs1.getNumChips(add_color);
		cs1.setNumChips(add_color, formerNumChips+1);
		
	}
	
	
	
	/**
	 * Calculates the chips missing by my ChipSet in ordered to satisfy the
	 * required array. It returns those chips ordered in an array.
	 * 
	 * @param array
	 *            ArrayList<String> that the caller is compared with
	 * @return Array of missing chips in the order needed to take a certain path
	 * @author Yael Ejgenberg
	 */
	public ArrayList<CtColor> getMissingChips(ArrayList<CtColor> array) {
		ArrayList<CtColor> missing = new ArrayList<CtColor>();
		ChipSet myClone = (ChipSet) this.clone();
		for (CtColor color : array)
			// If there is at least one chip of this color at this place then is
			// not missing and decrement
			// its counter so that we know that we have already used it.
			if (myClone.getNumChips(color) >= 1)
				myClone.setNumChips(color, myClone.getNumChips(color) - 1);
			else
				missing.add(color);

		return missing;
	}
	
	
	/**
	 * Add the number of chips of color to num. If the color of the chips does
	 * not exist in the ChipSet then it will be added.
	 * 
	 * @param color
	 *            The color to be set
	 * @param num
	 *            The number of chips.
	 * @author PGH
	 * @author SGF modified code
	 */
	public void add(CtColor color, int num) {
		int newNum = getNumChips(color) + num;
		setNumChips(color, newNum);
	}
	
	
	/**
	 * Sets the number of chips of specified color to specified number
	 */
	public void setNumChips(CtColor color, int num) {
		int currentNum = getNumChips(color);
		if ( currentNum < num) { //if we have less chips of that color, add
			for(int i=currentNum;i<num;i++)
				chips.add(color);
		}
		else if (currentNum > num)//if we have more chips of that color, remove
		{
			for(int i=0 ; i < (currentNum-num) ; i++) {
				chips.remove(color);//removes the first occurence of the specified element from the list if exists
			}
		}
		
		this.numChips = chips.size();
			
		//put("Color." + color, new Integer(num));
		//dirty = true;
	}

	
	
	/**
	 * Get a Set of all colors in the ChipSet
	 * 
	 * @return A set of all the colors.
	 * @author PGH
	 * @rewritten SGF
	 */
	public Set<CtColor> getColors() {
		HashSet<CtColor> hs = new HashSet<CtColor>();
		int colorIndexArr[] = {0,0,0,0,0,0,0,0,0,0};
		for(CtColor chip : chips) {
			colorIndexArr[chip.getColorNum()] = 1;
		}

		for(int i=0;i<10;i++) {
			if(colorIndexArr[i]!=0)
				hs.add(new CtColor(i));
		}
		return hs;
	}
	
	public boolean hasColor(CtColor color) {
		for( CtColor chip : chips) {
			if(color == chip)
				return true;
		}
		return false;
	}

}
