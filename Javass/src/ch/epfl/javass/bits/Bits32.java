package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * Une séquence de 32 bits
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */

public final class Bits32 {

	private Bits32() {
	}

	/**
	 * Construit un masque de bits (séquence de 1 dans un bitstring de 32 bits, les
	 * autres bits valent 0) à l'endroit voulu.
	 * 
	 * @param start (int) l'index du début de la plage de bits du masque
	 * @param size  (int) la longueur de la plage de bits du masque
	 * @throws IllegalArgumentException si la plage de bits voulue n'est pas valable
	 * @return (int) le nombre créé
	 */
	public static int mask(int start, int size) {
		Preconditions.checkArgument(0 <= start && size >= 0 && start + size <= Integer.SIZE);
		int masked = (size == Integer.SIZE ? -1 : (1 << size) - 1);
		return masked << start;
	}

	/**
	 * 
	 * Extrait une partie d'une séquence de 32 bits
	 * 
	 * @param bits  nombre dont on veut extraire une partie (int)
	 * @param start début de la partie qu'on veut extraire (int)
	 * @param size  nombre de bits qu'on veut extraire à partir de start
	 * @throws IllegalArgumentException si la plage de bits voulue n'est pas valide
	 * @return (int) le nombre final qui a été extrait de bits
	 */
	public static int extract(int bits, int start, int size) {
		Preconditions.checkArgument(
				(start >= 0 && start <= Integer.SIZE && start + size <= Integer.SIZE && size >= 0));
		return bits << (Integer.SIZE - start - size) >>> Integer.SIZE - size;

	}

	/**
	 * Construit une séquence de 32 bits en mettant bout à bout les bits donnés en
	 * arguments
	 * 
	 * @param v1 (int) premier nombre dans dans la séquence de 32 bits
	 * @param s1 (int) la longueur voulue qui sera occupée par v1
	 * @param v2 (int) vient après v1 dans la séquence de 32 bits
	 * @param s2 (int) longueur voulue qui sera occupée par v2
	 * @throws IllegalArgumentException si 1) Si la somme s1+s2 dépasse 32 2) Si on
	 *                                  donne une longueur négative pour s1 ou s2 3)
	 *                                  Si l'int donné en paramètre occupe plus de
	 *                                  place (bits) que voulu en argument
	 * @return (int) les bits mis bout à bout par la fonction
	 */
	public static int pack(int v1, int s1, int v2, int s2) throws IllegalArgumentException {
		Preconditions.checkArgument(s1 >= 0 && s2 >= 0 && s1 + s2 <= Integer.SIZE
				&& packVerify(v1, s1) && packVerify(v2, s2));
		return (v1 | (v2 << s1));

	}

	/**
	 * Construit une séquence de 32 bits en mettant bout à bout les bits donnés en
	 * arguments On va dans le sens des bits de poids faible aux bits de poids fort
	 * lors de l'assemblage
	 * 
	 * @param v1 (int) vient en premier dans la séquence de 32 bits
	 * @param s1 (int) longueur voulue de v1
	 * @param v2 (int) vient en deuxième dans la séquence de 32 bits
	 * @param s2 (int) longueur voulue de v2
	 * @param v3 (int) vient en troisième dans la séquence de 32 bits
	 * @param s3 (int) longueur voulue de v3
	 * @return (int) la séquence de 32 bits construite
	 * @throws IllegalArgumentException
	 * @throws IllegalArgumentException si 1) Si la somme s1+s2+s3 dépasse 32 2) Si
	 *                                  on donne une longueur négative pour s1, s2
	 *                                  ou s3 3) Si l'int donné en paramètre occupe
	 *                                  plus de place (bits) que voulu en argument
	 */
	public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) throws IllegalArgumentException {
		Preconditions.checkArgument(s1 >= 0 && s2 >= 0 && s3 >= 0
				&& s1 + s2 + s3 <= Integer.SIZE && packVerify(v1, s1) && packVerify(v2, s2) && packVerify(v3, s3));
		return (v1 | (v2 << s1) | (v3 << s2 + s1));

	}

	/**
	 * Construit une séquence de 32 bits en mettant bout à bout les bits v1 à v7. On
	 * va dans le sens des bits de poids faible aux bits de poids fort lors de
	 * l'assemblage
	 * 
	 * @param v1 (int) vient en premier dans la séquence de 32 bits
	 * @param s1 (int) longueur voulue de v1
	 * @param v2 (int) vient en deuxième dans la séquence de 32 bits
	 * @param s2 (int) longueur voulue de v2
	 * @param v3 (int) vient en troisième dans la séquence de 32 bits
	 * @param s3 (int) longueur voulue de v3
	 * @param v4 (int) vient en quatrième dans la séquence de 32 bits
	 * @param s4 (int) longueur voulue de v4
	 * @param v5 (int) vient en cinquième dans la séquence de 32 bits
	 * @param s5 (int) longueur voulue de v5
	 * @param v6 (int) vient en sixième dans la séquence de 32 bits
	 * @param s6 (int) longueur voulue de v6
	 * @param v7 (int) vient en septième dans la séquence de 32 bits
	 * @param s7 (int) longueur voulue de v7
	 * @return (int) la séquence de 32 bits construite
	 * @throws IllegalArgumentException 1) Si la somme s1+s2+s3+s4+s5+s6+s7 dépasse
	 *                                  32 2) Si on donne une longueur négative pour
	 *                                  s1, s2, s3, s4, s5, s6 ou s7 3) Si l'int
	 *                                  donné en paramètre occupe plus de place
	 *                                  (bits) que voulu en argument
	 * 
	 */
	public static int pack(int v1, int s1, int v2, int s2, int v3, int s3, int v4, int s4, int v5, int s5, int v6,
			int s6, int v7, int s7) throws IllegalArgumentException {
		Preconditions.checkArgument(
				(s1 >= 0 && s2 >= 0 && s3 >= 0 && s4 >= 0 && s5 >= 0
						&& s6 >= 0 && s7 >= 0 && s1 + s2 + s3 + s4 + s5 + s6 + s7 <= Integer.SIZE
						&& packVerify(v1, s1) && packVerify(v2, s2) && packVerify(v3, s3) && packVerify(v4, s4)
						&& packVerify(v5, s5) && packVerify(v6, s6) && packVerify(v7, s7)));
		Preconditions.checkArgument(
				(s1 >= 0 && s2 >= 0 && s3 >= 0 && s4 >= 0 && s5 >= 0
						&& s6 >= 0 && s7 >= 0 && s1 + s2 + s3 + s4 + s5 + s6 + s7 <= Integer.SIZE
						&& packVerify(v1, s1) && packVerify(v2, s2) && packVerify(v3, s3) && packVerify(v4, s4)
						&& packVerify(v5, s5) && packVerify(v6, s6) && packVerify(v7, s7)));
		return v1 | (v2 << s1) | (v3 << s2 + s1) | (v4 << s3 + s2 + s1) | (v5 << s4 + s3 + s2 + s1)
				| (v6 << s5 + s4 + s3 + s2 + s1) | (v7 << s6 + s5 + s4 + s3 + s2 + s1);
	}

	/*
	 * Vérifie si une paire valeur/taille est valable. Càd: Si la taille (en bits)
	 * est plus petite que la taille en bits occupée normalement par la valeur, il
	 * retourne fase
	 * 
	 * @param valeur (int) valeur à vérifier
	 * 
	 * @param taille (int) taille à vérifier
	 * 
	 * @return (boolean) un boolean qui est true si les conditions sont vraies pour
	 * une paire valeur/taille
	 */
	private static boolean packVerify(int valeur, int taille) {
		return Math.pow(2, taille) - 1 >= valeur && taille <= Integer.SIZE - 1;
	}
}