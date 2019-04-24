package com.games.kalah.domain;

import lombok.Data;

import java.util.HashMap;

@Data
public class KalahBoard{

    private EPlayer turn;

    private HashMap<Integer,Integer> board;


}
