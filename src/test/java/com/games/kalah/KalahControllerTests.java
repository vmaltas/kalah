package com.games.kalah;

import com.games.kalah.controller.KalahController;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;


@RunWith(SpringRunner.class)
@WebMvcTest(KalahController.class)
public class KalahControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private int gameId;


    @Test
    public void testCreateGameAndMakeMovSuccessful() throws Exception {

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

        mockMvc.perform(put("/games/$.id/pits/3"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(Matchers.notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.url", Matchers.is(Matchers.notNullValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status", Matchers.is(Matchers.notNullValue())));
    }


}
