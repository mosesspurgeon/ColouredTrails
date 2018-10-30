package coloredtrails.common;

/**
 * This introduces two user types among the player clients
 * 
 * @author Moses Satyam (msatyam@student.unimelb.edu.au)
 * @version 1.0
 * @since 2018-09-01
 */
public enum UserType {

	HUMAN,
	COMPUTER;
	
	public static UserType getUserType(String userType) {
		for (UserType ut : UserType.values()) {
			if (ut.name().equals(userType))
				return ut;
		}
		return null;
	}
}