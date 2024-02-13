package com.moco.moco.util;

import java.util.UUID;

public class NineCharacterUUIDGenerator {

	public static String generateNineCharacterUUID() {
		UUID uuid = UUID.randomUUID();
		long mostSignificantBits = uuid.getMostSignificantBits();
		long leastSignificantBits = uuid.getLeastSignificantBits();
		String uuidStr = Long.toHexString(mostSignificantBits) + Long.toHexString(leastSignificantBits);
		return uuidStr.substring(0, 9);
	}
}