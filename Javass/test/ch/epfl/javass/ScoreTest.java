package ch.epfl.javass;
//package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
public final class ScoreTest {

	@Test
	void withAdditionalTricksFailsWithNegativeValues() {
		Score s = Score.INITIAL;
		assertThrows(IllegalArgumentException.class, () ->{
			s.withAdditionalTrick(TeamId.TEAM_1, -1);
		});
	}

	@Test
	void equalsWorks() {
		SplittableRandom rng = newRandom();
		for(int i=0 ; i<RANDOM_ITERATIONS ; ++i) {
			int tT = rng.nextInt(10);
			int tP = rng.nextInt(258);
			int gP = rng.nextInt(2001);
			Score s1 = Score.ofPacked(Bits64.pack(Bits32.pack(tT, 4, tP, 9, gP, 11), Integer.SIZE, Bits32.pack(tT ,4 , tP, 9, gP, 11), Integer.SIZE));
			Score s2 = Score.ofPacked(Bits64.pack(Bits32.pack(tT, 4, tP, 9, gP, 11), Integer.SIZE, Bits32.pack(tT ,4 , tP, 9, gP, 11), Integer.SIZE));
			assertTrue(s1.equals(s2));
		}
	}
	
	@Test
	void equalsFails() {
		SplittableRandom rng = newRandom();
		for(int i=0 ; i<RANDOM_ITERATIONS ; ++i) {
			int tT = rng.nextInt(9);
			int tP = rng.nextInt(258);
			int gP = rng.nextInt(2001);
			Score s1 = Score.ofPacked(Bits64.pack(Bits32.pack(tT, 4, tP, 9, gP, 11), Integer.SIZE, Bits32.pack(tT ,4 , tP, 9, gP, 11), Integer.SIZE));
			Score s2 = Score.ofPacked(Bits64.pack(Bits32.pack(tT+1, 4, tP, 9, gP, 11), Integer.SIZE, Bits32.pack(tT+1 ,4 , tP, 9, gP, 11), Integer.SIZE));
			assertFalse(s1.equals(s2));
			assertFalse(s1.equals(new String()));
		}
	}
	
}
