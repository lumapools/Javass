package ch.epfl.javass;

/**
 * Contient des méthodes qui vérifient si les arguments sont valides
 * 
 * @author Benedek Hauer (301364)
 * @author Emi Sakamoto (302290)
 *
 */
public class Preconditions {

	private Preconditions() {
	}

	/**
	 * check une condition b et lance une exception si b est fausse
	 * 
	 * @param b (boolean) condition à checker
	 * @throws IllegalArgumentException si b est faux
	 */
	public static void checkArgument(boolean b) throws IllegalArgumentException {
		if (!b) {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * check si l'index est compris entre 0 et une taille voulue
	 * 
	 * @param index (int) index à checker
	 * @param size  (int) taille sur laquelle il faut regarder l'index
	 * @return (int) index
	 * @throws IllegalArgumentException si l'index est pas compris entre 0 (inclus)
	 *                                  et taille (exclu)
	 */
	public static int checkIndex(int index, int size) throws IllegalArgumentException {
		if ((index < 0) || index >= size) {
			throw new IndexOutOfBoundsException();
		}
		return index;
	}

}