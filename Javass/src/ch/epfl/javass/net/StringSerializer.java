package ch.epfl.javass.net;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Facilite la (dé)sérialisation des valeurs échangées entre le client et le
 * serveur
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class StringSerializer {
    private static final int BASE_16 = 16;

    private StringSerializer() {
    }

    /**
     * Sérialise l'entier de type int sous la forme de leur représentation
     * textuelle en base 16
     * 
     * @param n
     *            (int) l'entier à sérialiser
     * @return (String) l'entier sous sa forme sérialisé
     */
    public static String serializeInt(int n) {
        return Integer.toUnsignedString(n, BASE_16);
    }

    /**
     * Sérialise l'entier de type long sous la forme de leur représentation
     * textuelle en base 16
     * 
     * @param n
     *            (long) l'entier à sérialiser
     * @return (String) l'entier sous sa forme sérialisé
     */
    public static String serializeLong(long m) {
        return Long.toUnsignedString(m, BASE_16);
    }

    /**
     * Sérialise la chaine de caractère de type String par encodage en base64
     * des octets constituant leur encodage en UTF-8
     * 
     * @param s
     *            (String) la chaine de caractère à sérialiser
     * @return (String) la chaine sérialisée
     */
    public static String serializeString(String s) {
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Prend un nombre variable de chaînes en argument, un caractère de
     * séparation (ici la virgule) et retourne la chaîne composée des chaînes
     * séparées par le séparateur
     * 
     * @param separator
     *            (char) le séparateur
     * @param ss
     *            (Srting) la chaîne composée
     */
    public static String combine(char separator, String... ss) {
        return String.join(String.valueOf(separator), ss);
    }

    /**
     * Désérialise l'entier de type int, qui est sous la forme de sa
     * représentation textuelle en base 16
     * 
     * @param s
     *            (String) l'entier sérialisé
     * @return (int) l'entier désérialisé
     */
    public static int deserializeInt(String s) {
        return Integer.parseUnsignedInt(s, BASE_16);
    }

    /**
     * Désérialise l'entier de type long, qui est sous la forme de sa
     * représentation textuelle en base 16
     * 
     * @param s
     *            (String) l'entier sérialisé
     * @return (int) l'entier désérialisé
     */
    public static long deserializeLong(String s) {
        return Long.parseUnsignedLong(s, BASE_16);
    }

    /**
     * Désérialise la chaine de caractère de type String sérialisée par encodage
     * en base64 des octets constituant leur encodage en UTF-8
     * 
     * @param s
     *            (String) la chaine de caractère sérialisée
     * @return (String) la chaîne de caractère désérialisée
     */
    public static String deserializeString(String s) {
        return new String(
                Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }

    /**
     * Prend une chaîne unique et un caractère de séparation et retourne un
     * tableau contenant les chaînes individuelles
     * 
     * @param separator
     *            (char) le caractère de séparation
     * @param s
     *            (String) la chaine de caractère
     * @return (String[]) tableau contenant les chaînes individuelles
     */
    public static String[] split(char separator, String s) {
        return s.split(String.valueOf(separator));
    }

}