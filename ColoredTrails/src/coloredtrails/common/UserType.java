package coloredtrails.common;

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