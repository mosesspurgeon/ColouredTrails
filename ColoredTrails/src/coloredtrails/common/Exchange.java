package coloredtrails.common;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;

public class Exchange implements JsonSerializable{
	
	private Integer exchangeId=-1;
	private String agentName;
	private HashMap<CtColor, Integer> chipsAsked;
	private HashMap<CtColor, Integer> chipsOffered;
	private Boolean ownerApproval=false;
	//invalid exchange if the owner does not even possess the chips
	private Boolean valid = false;
	private int round=-1;
	
	
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
		this.exchangeId = new Integer(ex.exchangeId);
		this.chipsAsked = new HashMap<CtColor,Integer>();
		this.chipsOffered = new HashMap<CtColor,Integer>();
		this.agentName = new String(ex.getAgentName());
		this.ownerApproval = new Boolean(ex.ownerApproval);
		this.valid =  new Boolean(ex.valid);
		this.round = ex.round;
		
		for(CtColor color : ex.chipsAsked.keySet()){
			this.chipsAsked.put(new CtColor(color),ex.chipsAsked.get(color));
		}
		
		for(CtColor color : ex.chipsOffered.keySet()){
			this.chipsOffered.put(new CtColor(color),ex.chipsOffered.get(color));
		}
		
	}



	public String toString() {
		String output = new String();
		output +=  this.agentName + "  for  ";
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


	public Boolean getValid() {
		return valid;
	}


	public void setValid(Boolean valid) {
		this.valid = valid;
	}


	public Boolean isOwnerApproved() {
		return ownerApproval;
	}


	public void setOwnerApproval(Boolean ownerApproval) {
		this.ownerApproval = ownerApproval;
	}


	public Integer getExchangeId() {
		return exchangeId;
	}


	public void setExchangeId(Integer exchangeId) {
		this.exchangeId = exchangeId;
	}


	public int getRound() {
		return round;
	}


	public void setRound(int round) {
		this.round = round;
	}


	@Override
	public JsonObject toJsonObject() {
		
		JsonObject exchange = new JsonObject();
		exchange.put("exchangeId", exchangeId);
		exchange.put("agentName", agentName);

		JsonArray chipsAskedArray = new JsonArray();
		if (chipsAsked != null) {
			for (Map.Entry<CtColor, Integer> entry : chipsAsked.entrySet()) {
				JsonObject chipAsked = new JsonObject();
				CtColor key = entry.getKey();
				Integer value = entry.getValue();
				chipAsked.put("ctColor", key.toJsonObject());
				chipAsked.put("integer", value);
				chipsAskedArray.add(chipAsked);
			}
		}
		exchange.put("chipsAsked", chipsAskedArray);
		
		
		JsonArray chipsOfferedArray = new JsonArray();
		if (chipsOffered != null) {
			for (Map.Entry<CtColor, Integer> entry : chipsOffered.entrySet()) {
				JsonObject chipOffered = new JsonObject();
				CtColor key = entry.getKey();
				Integer value = entry.getValue();
				chipOffered.put("ctColor", key.toJsonObject());
				chipOffered.put("integer", value);
				chipsOfferedArray.add(chipOffered);
			}
		}
		exchange.put("chipsOffered", chipsOfferedArray);	
		exchange.put("ownerApproval", ownerApproval);
		exchange.put("valid", valid);
		exchange.put("round", round);
		
		return exchange;
	}
	
	public Exchange(JsonObject exchange) {
		
		this.exchangeId = exchange.getInteger("exchangeId");
		this.agentName = exchange.getString("agentName");
		
		this.chipsAsked = new HashMap<CtColor,Integer>();
		JsonArray chipsAskedArray = (JsonArray) exchange.get("chipsAsked");
		for (int i = 0; i < chipsAskedArray.size(); i++) {
			JsonObject chipAsked = (JsonObject) chipsAskedArray.get(i);
			CtColor key = new CtColor((JsonObject) chipAsked.get("ctColor"));
			Integer value = chipAsked.getInteger("integer");
			chipsAsked.put(key, value);
		}		

		this.chipsOffered = new HashMap<CtColor,Integer>();
		JsonArray chipsOfferedArray = (JsonArray) exchange.get("chipsOffered");
		for (int i = 0; i < chipsOfferedArray.size(); i++) {
			JsonObject chipOffered = (JsonObject) chipsOfferedArray.get(i);
			CtColor key = new CtColor((JsonObject) chipOffered.get("ctColor"));
			Integer value = chipOffered.getInteger("integer");
			chipsOffered.put(key, value);
		}	
		
		this.ownerApproval = exchange.getBoolean("ownerApproval");
		this.valid = exchange.getBoolean("valid");
		this.round = exchange.getInteger("round");
	}


	@Override
	public boolean equals(Object o) {
		 if(o==this)
			 return true;
		 if(!(o instanceof Exchange))
			 return false;
		 
		 Exchange e = (Exchange)o;
		 if(this.chipsAsked.equals(e.chipsAsked) && this.chipsOffered.equals(e.chipsOffered))
			 return true;
		 return false;
	}



	 
	 

}
