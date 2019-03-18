import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PackedScore;
import ch.epfl.javass.jass.TeamId;

/*
class TestProjet {

    @Test
    void test() {
        assertEquals(false, PackedCard.isValid(0b1111));
        assertThrows(IllegalArgumentException.class, () -> {Card.ofPacked(0b1111); });
    }

*/
    

class TestProjet {

        /**
         * Test isValid
         */
        @Test
        void isValidWorksForAllValidScores() {
            for (int r = 0; r <= 9; ++r) {
                for (int c = 0; c <= 257; ++c) {
                    for(int j = 0; j<=2000; ++j) {
                        assertTrue(PackedScore.isValid((long)j << 45 | (long)c << 36 | (long)r <<32 | (long)j<< 13 | (long)c << 4 | (long)r)); 
                    }
                }
            }
            assertTrue(PackedScore.isValid(2000L << 45 | 257L << 36 | 9L <<32 | 2000L<< 13 | 257L << 4 | 9L));
        }

        @Test
        void isValidWorksForSomeInvalidScores() {
            for (int r = 0; r <=9; ++r) {
                for (int c = 0; c <= 257; ++c) {
                    for(int j = 0; j<=2000; ++j) {
                        assertFalse(PackedScore.isValid(1L << 56 | (long)j << 45 | (long)c << 36 | (long)r <<32 | (long)j<< 13 | (long)c << 4 | (long)r));
                        assertFalse(PackedScore.isValid((long)j << 45 | (long)c << 36 | (long)r <<32 | 1L <<24 | (long)j<< 13 | (long)c << 4 | (long)r));
                    }
                }
            }
        }
        /**
         * 
         */
        @Test
        void PackedScoreWorks() {
            long s = PackedScore.INITIAL;
            System.out.println(PackedScore.toString(s));
            for (int i = 0; i < Jass.TRICKS_PER_TURN; ++i) {
              int p = (i == 0 ? 13 : 18);
              TeamId w = (i % 2 == 0 ? TeamId.TEAM_1 : TeamId.TEAM_2);
              s = PackedScore.withAdditionalTrick(s, w, p);
              System.out.println(PackedScore.toString(s));
            }
            s = PackedScore.nextTurn(s);
            System.out.println(PackedScore.toString(s));
        

        }
}
