package ch.epfl.javass;
//package ch.epfl.javass.jass;

import static ch.epfl.test.TestRandomizer.RANDOM_ITERATIONS;
import static ch.epfl.test.TestRandomizer.newRandom;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.SplittableRandom;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PackedScore;
import ch.epfl.javass.jass.TeamId;

public final class PackedScoreTest{

	@Test
	void checkIsValidWorks(){
		for(int tT = 0 ;tT <= 9 ; tT++) {
			for(int tP = 0 ; tP <= 257 ; tP++) {
				for(int gP = 0; gP <= 2000 ; gP++) {
					assertTrue(PackedScore.isValid(Bits64.pack(Bits32.pack(tT, 4, tP, 9, gP, 11), Integer.SIZE, Bits32.pack(tT ,4 , tP, 9, gP, 11), Integer.SIZE)));
				}
			}
		}
	}

	@Test
	void cehckIsValidFailsIfAddOneBeforeScoreValue(){
		for(int tT = 0 ;tT <= 9 ; tT++) {
			for(int tP = 0 ; tP <= 257 ; tP++) {
				for(int gP = 0; gP <= 2000 ; gP++) {
					for(int i=1;i<=255;i++) {
						assertFalse(PackedScore.isValid(((i<<24) + (gP<<13) + (tP << 4) + tT) | ((long)((i<<24) + (gP<<13) + (tP << 4) + tT))<<Integer.SIZE));
					}
				}
			}
		}	
	}

	@Test
	void checkIsValidFailsForValuesTooBig(){
		SplittableRandom rng = newRandom();
		for(int i=0; i< RANDOM_ITERATIONS ; i++) {
			int tT = rng.nextInt(6) + 10;
			int tP = rng.nextInt(254) + 258;
			int gP = rng.nextInt(47) + 2001;
			assertFalse(PackedScore.isValid(tT));
			assertFalse(PackedScore.isValid(tP<<4));
			assertFalse(PackedScore.isValid(gP<<13));
			assertFalse(PackedScore.isValid((gP<<13) + (tP <<4) + tT));
			assertFalse(PackedScore.isValid(((long)tP)<<36));
		}
		
	}
	
	@Test
	void checkPackWorks() {
		for(int tT = 0 ;tT <= 9 ; tT++) {
			for(int tP = 0 ; tP <= 257 ; tP++) {
				for(int gP = 0; gP <= 2000 ; gP++) {
					assertEquals(((gP<<13) + (tP << 4) + tT) | ((long)((gP<<13) + (tP << 4) + tT)<<Integer.SIZE) , PackedScore.pack(tT, tP, gP, tT, tP, gP));
				}
			}
		}
	
	}

	@Test
	void turnTrickWorks() {
		SplittableRandom rng = newRandom();
		for(int i=0 ; i<RANDOM_ITERATIONS ; i++) {
			long tT = rng.nextLong(10);
			long pkScore = (tT<<Integer.SIZE) + tT;
			assertEquals(tT , PackedScore.turnTricks(pkScore, TeamId.TEAM_1));
			assertEquals(tT , PackedScore.turnTricks(pkScore, TeamId.TEAM_2));
			
		}
	}

	@Test
	void turnPointsWorks() {
		SplittableRandom rng = newRandom();
		for(int i=0 ; i<RANDOM_ITERATIONS ; i++) {
			long tP = rng.nextLong(258);
			long pkScore = (tP<<Integer.SIZE+4) + (tP<<4);
			assertEquals(tP , PackedScore.turnPoints(pkScore, TeamId.TEAM_1));
			assertEquals(tP , PackedScore.turnPoints(pkScore, TeamId.TEAM_2));
		}
	}

	@Test
	void gamePointWorks() {
		SplittableRandom rng = newRandom();
		for(int i=0 ; i<RANDOM_ITERATIONS ; i++) {
			long gP = rng.nextLong(2001);
			long pkScore = (gP<<Integer.SIZE+13) + (gP<<13);
			assertEquals(gP , PackedScore.gamePoints(pkScore, TeamId.TEAM_1));
			assertEquals(gP , PackedScore.gamePoints(pkScore, TeamId.TEAM_2));
		}
	}

	@Test
	void totalPointsWorks() {
		SplittableRandom rng = newRandom();
		for(int i=0 ; i<RANDOM_ITERATIONS ; i++) {
			long tP = rng.nextLong(258);
			long gP = rng.nextLong(2001);
			long pkScore = (tP<<Integer.SIZE+4) + (tP<<4) + (gP<<Integer.SIZE+13) + (gP<<13);
			assertEquals( (int)(gP+tP) , PackedScore.totalPoints(pkScore, TeamId.TEAM_1));
			assertEquals( (int)(gP+tP) , PackedScore.totalPoints(pkScore, TeamId.TEAM_2));
		}
	}

	@Test
	void withAdditionalTrickWorksWithoutExtraPoints() {
		long s = PackedScore.INITIAL;
		int countTeam1 = 0, countTeam2 = 0;
		for(int i=0 ; i<Jass.TRICKS_PER_TURN ; i++) {
			int p = (i==0 ? 13 : 18);
			TeamId w = ( i%2 == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2);
			s = PackedScore.withAdditionalTrick(s, w, p);
			if(i%2==0) {
				++countTeam1;
				assertEquals(countTeam1, Bits64.extract(s, 0, 4));
			}else {
				++countTeam2;
				assertEquals(countTeam2, Bits64.extract(s, 32, 4));
			}
		}
	}

	@Test
	void wiithAdditionalTrickWorksWithExtraMatchPoints() {
		long v1 = PackedScore.INITIAL;
		int countTeam1 = 0, countTeam2 = 0;
		for(int i=0 ; i<Jass.TRICKS_PER_TURN ; i++) {
			int p = (i==0 ? 13 : 18);
			v1 = PackedScore.withAdditionalTrick(v1, TeamId.TEAM_1, p);
			v1 = PackedScore.withAdditionalTrick(v1, TeamId.TEAM_2, p);
			countTeam1 += p;
			countTeam2 += p;
			if(i!=8) {
				assertEquals(countTeam1, Bits64.extract(v1, 4, 9));
				assertEquals(countTeam2, Bits64.extract(v1, 36,9));
			}else {
				assertEquals((countTeam1+100), Bits64.extract(v1, 4, 9));
				assertEquals((countTeam2+100), Bits64.extract(v1, 36, 9));
			}
		}
	}

	@Test
	void nextTurnWorks() {
		for(int tT = 0 ;tT <= 9 ; tT++) {
			for(int tP = 0 ; tP <= 257 ; tP++) {
				for(int gP = 0; gP <= 1700 ; gP++) {
					long pkScore = Bits64.pack(Bits32.pack(tT, 4, tP, 9, gP, 11), Integer.SIZE, Bits32.pack(tT ,4 , tP, 9, gP, 11), Integer.SIZE);
					assertEquals(0L, PackedScore.turnPoints(PackedScore.nextTurn(pkScore), TeamId.TEAM_1));
					assertEquals(0L, PackedScore.turnPoints(PackedScore.nextTurn(pkScore), TeamId.TEAM_2));
					assertEquals(0, PackedScore.turnTricks(PackedScore.nextTurn(pkScore), TeamId.TEAM_2));
					assertEquals(0, PackedScore.turnTricks(PackedScore.nextTurn(pkScore), TeamId.TEAM_2));
					assertEquals((tP+gP), PackedScore.totalPoints(pkScore, TeamId.TEAM_1));
					assertEquals((tP+gP), PackedScore.totalPoints(pkScore, TeamId.TEAM_2));
				}
			}
		}
	}
}



	
	


