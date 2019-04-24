package com.games.kalah.dto;

import lombok.Data;

import java.util.HashMap;

@Data
public class MoveResponseDto {

    private String id;

    private String url;

    private HashMap<Integer, Integer> status;
}
