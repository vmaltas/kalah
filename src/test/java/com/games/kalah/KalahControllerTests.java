package com.games.kalah;

import com.games.kalah.controller.KalahController;
import com.games.kalah.domain.EPlayer;
import com.games.kalah.domain.KalahBoard;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@RunWith(SpringRunner.class)
@WebMvcTest(KalahController.class)
public class KalahControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private String gameId;


    @Before
    public void createGame() throws Exception {
        MvcResult result = mockMvc.perform(post("/games/")).andReturn();
        String content = result.getResponse().getContentAsString();
        gameId = content.substring(content.indexOf("id") + 5, content.indexOf("id") + 9);
    }

    @Test
    public void testCreateGameSuccessful() throws Exception {

        mockMvc.perform(post("/games/"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(Matchers.notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.uri", Matchers.is(Matchers.notNullValue())));
    }

    @Test
    public void testMoveClientError() throws Exception {
        mockMvc.perform(put("/games/100000/pits/3"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void testMoveSuccessful() throws Exception {

        mockMvc.perform(put("/games/" + gameId + "/pits/3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(Matchers.notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url", Matchers.is(Matchers.notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(Matchers.notNullValue())));
    }

    @Test
    public void testCreateGameAndMakeMoveSuccessful() throws Exception {

        mockMvc.perform(post("/games/"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(Matchers.notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.uri", Matchers.is(Matchers.notNullValue())));
    }

    @Test
    public void testSwapPlayerSuccessful() {
        KalahController kalahController = new KalahController();
        EPlayer playerTest = EPlayer.Player1;
        EPlayer returnPlayer = kalahController.swapPlayer(playerTest);
        assertThat(returnPlayer, is(equalTo(EPlayer.Player2)));
    }

    @Test
    public void testInitializeBoard() {
        KalahController kalahController = new KalahController();
        HashMap<Integer, Integer> testBoard = kalahController.initializeBoard();
        boolean check = true;
        for (int i = 1; i <= 14; i++) {
            if ((i == 7 || i == 14) && testBoard.get(i) != 0) {
                check = false;
                break;
            } else if ((i != 7 && i != 14) && testBoard.get(i) != 4) {
                check = false;
                break;
            }
        }
        assertThat(check, is(equalTo(true)));
    }

    @Test
    public void testSowPits() {
        KalahController kalahController = new KalahController();
        HashMap<Integer, Integer> testBoard = kalahController.initializeBoard();
        EPlayer playerTest = EPlayer.Player1;
        KalahBoard kalahBoard = new KalahBoard();
        kalahBoard.setTurn(playerTest);
        kalahBoard.setBoard(testBoard);
        KalahBoard kalahResultBoard = kalahController.sowPits(kalahBoard,1,gameId);
        boolean check = true;
        for (int i = 1; i <= 14; i++) {
            if ((i == 1) && kalahResultBoard.getBoard().get(i) != 0) {
                check = false;
                break;
            } else if ((i>1 && i<6) && kalahResultBoard.getBoard().get(i) != 5) {
                check = false;
                break;
            } else if ((i==6 || (i>7 && i<14) ) && kalahResultBoard.getBoard().get(i) != 4) {
                check = false;
                break;
            } else if ((i == 7 || i == 14) && kalahResultBoard.getBoard().get(i) != 0) {
                check = false;
                break;
            }
        }
        assertThat(check, is(equalTo(true)));
    }


    @Test
    public void testCheckIfLastRound() {
        KalahController kalahController = new KalahController();
        HashMap<Integer, Integer> testBoard = kalahController.initializeBoard();
        for (int i = 1; i < 7; i++) {
            testBoard.put(i, 0);
        }
        EPlayer playerTest = EPlayer.Player1;
        KalahBoard kalahBoard = new KalahBoard();
        kalahBoard.setTurn(playerTest);
        kalahBoard.setBoard(testBoard);
        boolean lastRoundTest = kalahController.checkIfLastRound(kalahBoard, kalahBoard.getTurn());
        assertThat(lastRoundTest, is(equalTo(true)));
    }


    @Test
    public void testCheckIfNotLastRound() {
        KalahController kalahController = new KalahController();
        HashMap<Integer, Integer> testBoard = kalahController.initializeBoard();
        EPlayer playerTest = EPlayer.Player1;
        KalahBoard kalahBoard = new KalahBoard();
        kalahBoard.setTurn(playerTest);
        kalahBoard.setBoard(testBoard);
        boolean lastRoundTest = kalahController.checkIfLastRound(kalahBoard, kalahBoard.getTurn());
        assertThat(lastRoundTest, is(equalTo(false)));
    }

    @Test
    public void testGetOpponentsAllSeeds() {
        KalahController kalahController = new KalahController();
        HashMap<Integer, Integer> testBoard = kalahController.initializeBoard();
        EPlayer playerTest = EPlayer.Player1;
        KalahBoard kalahBoard = new KalahBoard();
        kalahBoard.setTurn(playerTest);
        kalahBoard.setBoard(testBoard);
        KalahBoard kalahResultBoard = kalahController.getOpponentsAllSeeds(kalahBoard,playerTest);
        boolean check = true;
        Integer seedInPlayer1HouseTest = kalahResultBoard.getBoard().get(7);
        assertThat(seedInPlayer1HouseTest, is(equalTo(24)));
    }



}
