package test.hedera.sdk.common;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import com.hedera.sdk.common.HederaKeyPair;
import com.hedera.sdk.common.HederaKeyPair.KeyType;
import com.hedera.sdk.common.HederaKeyThreshold;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class HederaKeyThresholdTest {
	protected static byte[] keyBytes = new byte[] {12};
	protected static String description = "A Description";
	protected static List<HederaKeyPair> keyList = new ArrayList<HederaKeyPair>();
	protected static HederaKeyThreshold masterKey;
	protected static HederaKeyThreshold protoKey;
	protected static HederaKeyThreshold jsonKey = new HederaKeyThreshold();
	protected static int threshold = 10;

	@BeforeAll
	static void initAll() {
		keyList.add(new HederaKeyPair(KeyType.ED25519, keyBytes));
		keyList.add(new HederaKeyPair(KeyType.RSA3072, keyBytes));
	}
	
	@Test
	@DisplayName("Threshold key init")
	void testKeyInit() {
		HederaKeyThreshold masterKey = new HederaKeyThreshold(threshold, keyList);
		protoKey = new HederaKeyThreshold(masterKey.getProtobuf());
		jsonKey.fromJSON(masterKey.JSON());

		assertEquals(keyList.size(), masterKey.keys.size());
		assertEquals(threshold, masterKey.threshold);
		assertArrayEquals(keyList.get(0).getKey(), masterKey.keys.get(0).getKey());
		assertArrayEquals(keyList.get(1).getKey(), masterKey.keys.get(1).getKey());
	}
	
	@Test
	@DisplayName("Threshold key add remove")
	void testKeyAddRemove() {
		HederaKeyThreshold masterKey = new HederaKeyThreshold(threshold, keyList);

		HederaKeyPair key = new HederaKeyPair(KeyType.ED25519, keyBytes);
		masterKey.addKey(key);
		assertEquals(keyList.size() + 1, masterKey.keys.size());
		
		masterKey.deleteKey(key);
		assertEquals(keyList.size(), masterKey.keys.size());
	}
	
	@Test
	@DisplayName("Threshold key protobuf")
	void testKeyProtobuf() {
		HederaKeyThreshold masterKey = new HederaKeyThreshold(threshold, keyList);
		protoKey = new HederaKeyThreshold(masterKey.getProtobuf());
		assertEquals(keyList.size(), protoKey.keys.size());
		assertEquals(threshold, protoKey.threshold);
		assertArrayEquals(keyList.get(0).getKey(), protoKey.keys.get(0).getKey());
		assertArrayEquals(keyList.get(1).getKey(), protoKey.keys.get(1).getKey());
	}

	@Test
	@DisplayName("Threshold key json")
	void testKeyJson() {
		HederaKeyThreshold masterKey = new HederaKeyThreshold(threshold, keyList);
		jsonKey.fromJSON(masterKey.JSON());

		assertEquals(keyList.size(), jsonKey.keys.size());
		assertEquals(threshold, jsonKey.threshold);
		assertArrayEquals(keyList.get(0).getKey(), jsonKey.keys.get(0).getKey());
		assertArrayEquals(keyList.get(1).getKey(), jsonKey.keys.get(1).getKey());
		
		assertNotNull(masterKey.JSONString());
	}
}
