package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Séquence de 64 bits
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public final class Bits64 {

	private Bits64() {
	}

	/**
	 * Construit un masque de bits (séquence de 1 dans un bitstring de 64 bits, les
	 * autres bits valent 0) à l'endroit voulu.
	 * 
	 * @param start (int) l'index du début de la plage de bits du masque
	 * @param size  (int) la longueur de la plage de bits du masque
	 * @throws IllegalArgumentException si la plage de bits voulue n'est pas valable
	 * @return (long) le nombre crée par le masque
	 */
	public static long mask(int start, int size) {
		Preconditions.checkArgument(0 <= start && size >= 0 && start + size <= Long.SIZE);
		long masked = (size == Long.SIZE ? -1L : (1L << size) - 1L);
		return masked << start;
	}

	/**
	 * 
	 * Extrait une partie d'une séquence de 64 bits
	 * 
	 * @param bits  nombre dont on veut extraire une partie (long)
	 * @param start début de la partie qu'on veut extraire (int)
	 * @param size  nombre de bits qu'on veut extraire à partir de start
	 * @throws IllegalArgumentException si la plage de bits voulue n'est pas valide
	 * @return (long) le nombre final qui a été extrait de bits
	 */
	public static long extract(long bits, int start, int size) {
		Preconditions.checkArgument(start >= 0 && start <= Long.SIZE && start + size <= Long.SIZE && size >= 0);
		return bits << (Long.SIZE - (start + size)) >>> Long.SIZE - size;
	}

	/**
	 * Construit une séquence de 64 bits en mettant bout à bout les bits donnés en
	 * arguments
	 * 
	 * @param v1 (long) premier nombre dans dans la séquence de 64 bits
	 * @param s1 (int) la longueur voulue qui sera occupée par v1
	 * @param v2 (long) vient après v1 dans la séquence de 64 bits
	 * @param s2 (int) longueur voulue qui sera occupée par v2
	 * @throws IllegalArgumentException si 1) Si la somme s1+s2 dépasse 64 2) Si on
	 *                                  donne une longueur négative pour s1 ou s2 3)
	 *                                  Si le long donné en paramètre occupe plus de
	 *                                  place (bits) que voulu en argument
	 * @return (long) les bits mis bout à bout par la méthode
	 */
	public static long pack(long v1, int s1, long v2, int s2) throws IllegalArgumentException {
		Preconditions
				.checkArgument(s1 >= 0 && s2 >= 0 && s1 + s2 <= Long.SIZE && packVerify(v1, s1) && packVerify(v2, s2));
		return v1 | (v2 << s1);
	}

	/*
	 * Méthode auxiliaire qui vérifie si une paire valeur/taille est valable. Càd:
	 * Si la taille (en bits) est plus petite que la taille en bits occupée
	 * normalement par la valeur, il retourne fase
	 * 
	 * @param valeur (long) valeur à vérifier
	 * 
	 * @param taille taille à vérifier
	 * 
	 * @return (boolean) un boolean qui est true si les conditions sont vraies pour
	 * une paire valeur/taille
	 */
	private static boolean packVerify(long valeur, int taille) {
		if (taille > Long.SIZE - 1L) {
			return false;
		}
		return Math.pow(2, taille) - 1L >= valeur;
	}
}