package coloredtrails.gui.types;

import coloredtrails.common.ChipSet;

public class PathAndChips implements Comparable{
	public Path p;
	public ChipSet cs;
	public PathAndChips(Path p, ChipSet cs) {
		this.p = p;
		this.cs = cs;
	}
	
	 /**
     * Compare the path with an object
     * Added for the priority queue of the path finder.
     * @param o Object to be compared with
     * @return 0 if they're equal, 1 if the argument is lesser and -1 if the argument is greater
     * @author ilke
     */
    public int compareTo(Object o) {
		PathAndChips pcs = (PathAndChips) o;
		int weight2 = pcs.p.getWeight(), weight1 = this.p.getWeight();
		
		if(weight1 > weight2) {
			return 1;
		}
		else if (weight1 < weight2) {
			return -1;
		}
		else {
			return 0;
		}
    }
  
  
}