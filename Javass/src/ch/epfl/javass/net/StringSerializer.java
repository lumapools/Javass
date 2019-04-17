package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Facilite la (dé)sérialisation des valeurs échangées entre le client et le serveur
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class StringSerializer {
	public static void main(String args[]) {
	}

	private StringSerializer() {}
	
	public static String serializeInt(int n) {
		return Integer.toUnsignedString(n, 16);
	}
	
	public static String serializeLong(long m) {
		return Long.toUnsignedString(m, 16);
	}
	
	public static String serializeString(String s) {
		return new String(Base64.getEncoder().encode(s.getBytes(StandardCharsets.UTF_8)));

	}
	
	public static String combine(char separator, String... ss) {
		return String.join(String.valueOf(separator), ss);
	}
	
	public static int deserializeInt(String s){
        return Integer.parseUnsignedInt(s, 16);
    }
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, 16);
    }
    public static String deserializeString(String s){
        return new String(Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8)));
    }
    public static String[] split(char separation, String s) {
        return s.split(String.valueOf(separation));
    }
	
}
