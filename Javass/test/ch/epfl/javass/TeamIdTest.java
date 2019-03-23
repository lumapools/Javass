package ch.epfl.javass;
//package ch.epfl.javass.jass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.epfl.javass.jass.TeamId;

public final class TeamIdTest {
    @Test
    void teamIdTeamsAreCorrect() throws Exception {
        TeamId team1 = TeamId.TEAM_1;
        TeamId team2 = TeamId.TEAM_2;
        
        assertEquals(TeamId.TEAM_2, team1.other());
        assertEquals(TeamId.TEAM_1, team2.other());
        
        assertEquals(2, TeamId.COUNT);
    }
}

