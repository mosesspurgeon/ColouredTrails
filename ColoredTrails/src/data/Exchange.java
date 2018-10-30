package data;

import java.util.HashMap;

import ui.CtColor;

public class Exchange {
	
	private String agentName;
	private HashMap<CtColor, Integer> chipsAsked;
	private HashMap<CtColor, Integer> chipsOffered;
	
	
	/**
	 * Ctor for when only asking chips and offering nothing in return
	 * @param numChipsAsked
	 * @param agentName
	 * @param colorAsked
	 */
	public Exchange(String agentName, int numChipsAsked, CtColor colorAsked) {
		this.chipsAsked = new HashMap<CtColor, Integer>();
		this.chipsOffered = new HashMap<CtColor, Integer>();
		this.agentName = new String(agentName);
		askForChips(numChipsAsked,colorAsked);
	}
	
	
	public void askForChips(int numChipsAsked, CtColor colorAsked) {
		this.chipsAsked.put( colorAsked,numChipsAsked);
	}
	
	public void offerChips(int numChipsOffered, CtColor colorOffered) {
		this.chipsOffered.put( colorOffered,numChipsOffered);
	}
	

	public Exchange(String agentName, int numChipsAsked, CtColor colorAsked, int numChipsGiven, CtColor colorGiven) {
		this.chipsAsked = new HashMap<CtColor,Integer>();
		this.chipsOffered = new HashMap<CtColor,Integer>();
		this.agentName = new String(agentName);
		askForChips(numChipsAsked,colorAsked);
		offerChips(numChipsGiven,colorGiven);
	}
	
	public Exchange(String agentName, HashMap<CtColor, Integer> chipsAsked, HashMap<CtColor,Integer> chipsOffered) {
		this.agentName = new String(agentName);
		this.setChipsAsked(chipsAsked);
		this.setChipsOffered(chipsOffered);
		
	}
	

	public Exchange(Exchange ex) {
		this.chipsAsked = new HashMap<CtColor,Integer>();
		this.chipsOffered = new HashMap<CtColor,Integer>();
		this.agentName = new String(ex.getAgentName());
		for(CtColor color : ex.chipsAsked.keySet()){
			this.chipsAsked.put(new CtColor(color),ex.chipsAsked.get(color));
		}
		
		for(CtColor color : ex.chipsOffered.keySet()){
			this.chipsOffered.put(new CtColor(color),ex.chipsOffered.get(color));
		}
		
	}



	public String toString() {
		String output = new String();
		output += "Ask " + this.agentName + " for ";
		for(CtColor color : this.chipsAsked.keySet() ) 
			output+= this.chipsAsked.get(color) + " chips with color number " + color.getColorNum() + " ";
		output += "in exchange for ";	
		for(CtColor color : this.chipsOffered.keySet() ) 
			output+= this.chipsOffered.get(color) + " chips with color number " + color.getColorNum() + " ";
		return output; 
	}

	public String getAgentName() {
		return agentName;
	}


	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	
	
	public boolean asksSameAgentForSameColor(Exchange e) {
		 if( this.agentName.equals(e.agentName)) {
			 for(CtColor color : this.chipsAsked.keySet()) {
				 for(CtColor color_2 : e.chipsAsked.keySet()) {
					 if(color.equals(color_2))
						 return true;
				 }
			 }
		 }
		 return false;
	}

	
//	 @Override //Only checks if what the agent wants to exchange is the same. Not what the agent wants to offer
//	 public boolean equals(Object o) {
//		 if(o==this)
//			 return true;
//		 if(!(o instanceof Exchange))
//			 return false;
//		 
//		 Exchange e = (Exchange)o;
//		 if( this.agentName.equals(e.agentName)) {
//			 if(this.chipsAsked.size() != e.chipsAsked.size() ) {
//				 for(int num : this.chipsAsked.keySet() ) {
//					 for(int key : this.chipsAsked.keySet()) {
//						 	if(e.chipsAsked.containsKey(key)) {
//						 		if(!e.chipsAsked.get(key).equals(chipsAsked.get(key)))
//						 			return false;
//						 	}
//						 	else
//						 		return false;
//						 }
//					 }
//				 	return true;
//				 }
//		 }
//
//			 
//		 return false;
//		 
//	 }
	
	public void adjustNumChipsAsked(CtColor color, int num) {
		this.chipsAsked.remove(color);
		this.chipsAsked.put(color, num);
	}


	public HashMap<CtColor,Integer> getChipsAsked() {
		return chipsAsked;
	}


	public void setChipsAsked(HashMap<CtColor,Integer> chipsAsked) {
		this.chipsAsked = new HashMap<CtColor,Integer>();
		for(CtColor key : chipsAsked.keySet()) {
			int colorAmount = chipsAsked.get(key);
			this.chipsAsked.put(key,colorAmount);
		}
	}


	public void setChipsOffered(HashMap<CtColor,Integer> chipsOffered) {
		this.chipsOffered = new HashMap<CtColor,Integer>();
		for(CtColor key : chipsOffered.keySet()) {
			int colorAmount = chipsOffered.get(key);
			this.chipsOffered.put(key,colorAmount);
		}
	}


	public HashMap<CtColor,Integer> getChipsOffered() {
		return chipsOffered;
	}



	 
	 

}
