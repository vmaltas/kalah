package com.games.kalah.controller;

import com.games.kalah.domain.EPlayer;
import com.games.kalah.domain.KalahBoard;
import com.games.kalah.dto.CreateGameResponseDto;
import com.games.kalah.dto.MoveResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(path = "/games", produces = "application/json")
public class KalahController {

    protected String url = "http://localhost:8080/";
    protected int maxGameSlot = 9999;
    protected int playerOneHouse = 7;
    protected int playerTwoHouse = 14;
    protected int startingSeedNumber = 4;

    HashMap<String, KalahBoard> gameMap = new HashMap<>();

    @PostMapping(value = "/")
    public ResponseEntity createGame() {
        UUID uuid = UUID.randomUUID();
        log.info("create game started.UUID" + uuid);

        CreateGameResponseDto createGameResponseDto = new CreateGameResponseDto();
        KalahBoard kalahBoard = new KalahBoard();
        String generatedGameId;
        if (gameMap.size() > maxGameSlot) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Not enough game channel at the moment. Please try again later.");
        }

        do {
            generatedGameId = String.format("%04d", (int) (Math.random() * maxGameSlot));
        } while (gameMap.containsKey(generatedGameId));

        kalahBoard.setTurn(null);
        kalahBoard.setBoard(initializeBoard());
        gameMap.put(generatedGameId, kalahBoard);

        createGameResponseDto.setId(generatedGameId);
        createGameResponseDto.setUri(url + "games/" + generatedGameId);
        return new ResponseEntity<>(createGameResponseDto, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{gameId}/pits/{pitId}")
    public ResponseEntity move(@PathVariable("gameId") String gameId, @PathVariable("pitId") Integer pitId) {
        UUID uuid = UUID.randomUUID();
        log.info("moving the pit.UUID" + uuid + " gameId: " + gameId + " pitId: " + pitId);
        if (pitId < 1 || pitId > playerTwoHouse-1 || pitId == playerOneHouse) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong Pit Number");
        }
        KalahBoard currentBoard = gameMap.get(gameId);
        KalahBoard responseBoard = null;

        if (currentBoard == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game not found.");
        } else {

            EPlayer playerTurn = currentBoard.getTurn();
            if (playerTurn != null) {
                if ((playerTurn.equals(EPlayer.Player1) && pitId > playerOneHouse-1)
                        || (playerTurn.equals(EPlayer.Player2) && pitId < playerOneHouse+1)) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Wrong Turn");
                }
            }
            if (currentBoard.getBoard().get(pitId) == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No seed on pit:" + pitId);
            }
            responseBoard = sowPits(currentBoard, pitId, gameId);
        }


        MoveResponseDto moveResponseDto = new MoveResponseDto();
        moveResponseDto.setId(gameId);
        moveResponseDto.setUrl(url + "games/" + gameId);
        moveResponseDto.setStatus(responseBoard.getBoard());//TODO cast int to str for values
        return new ResponseEntity<>(moveResponseDto, HttpStatus.OK);
    }


    public HashMap<Integer, Integer> initializeBoard() {

        HashMap<Integer, Integer> currentBoard = new HashMap<>();
        for (int i = 1; i <= playerTwoHouse; i++) {
            if (i != playerOneHouse && i != playerTwoHouse) {
                currentBoard.put(i, startingSeedNumber);
            } else {
                currentBoard.put(i, 0);
            }
        }
        return currentBoard;
    }

    public KalahBoard sowPits(KalahBoard currentBoard, Integer pitId, String gameId) {

        EPlayer currentPlayer = currentBoard.getTurn();
        HashMap<Integer, Integer> pits = currentBoard.getBoard();
        Integer seedInHand = pits.get(pitId);
        if (seedInHand > 0) {
            pits.put(pitId, 0);
            if (currentPlayer == null) {
                if (pitId >= 1 && pitId < playerOneHouse) {
                    currentPlayer = EPlayer.Player1;
                } else if (pitId > playerOneHouse && pitId < playerTwoHouse) {
                    currentPlayer = EPlayer.Player2;
                }

            }

            currentBoard.setTurn(swapPlayer(currentPlayer));

            while (seedInHand > 0) {

                pitId = (pitId % playerTwoHouse == 0) ? 1 : pitId + 1;
                if (currentPlayer.equals(EPlayer.Player1) && pitId == playerTwoHouse) {
                    pitId = (pitId % playerTwoHouse == 0) ? 1 : pitId + 1;
                } else if (currentPlayer.equals(EPlayer.Player2) && pitId == playerOneHouse) {
                    pitId = (pitId % playerTwoHouse == 0) ? 1 : pitId + 1;
                }
                Integer seedCount = pits.get(pitId);
                if (seedInHand == 1) {
                    int seedInOppositePit = 0;
                    if (currentPlayer.equals(EPlayer.Player1)) {
                        if (pitId < playerOneHouse && seedCount == 0) {
                            seedInOppositePit = pits.get(playerTwoHouse - pitId);
                            pits.put(playerTwoHouse - pitId, 0); //empty the opposite pit.
                            pits.put(playerOneHouse, pits.get(playerOneHouse) + seedInOppositePit + 1); //deposit seeds into P1 house
                        } else if (pitId == playerOneHouse) {
                            currentBoard.setTurn(EPlayer.Player1); //give free turn
                            pits.put(playerOneHouse, pits.get(playerOneHouse) + 1);//deposit seeds into P1 house
                        } else if (pitId > playerOneHouse || pitId < playerOneHouse) {
                            pits.put(pitId, seedCount + 1);
                        }
                    } else if (currentPlayer.equals(EPlayer.Player2)) {
                        if (pitId > playerOneHouse && pitId < playerTwoHouse && seedCount == 0) {
                            seedInOppositePit = pits.get(playerTwoHouse - pitId);
                            pits.put(playerTwoHouse - pitId, 0); //empty the opposite pit.
                            pits.put(playerTwoHouse, pits.get(playerTwoHouse) + seedInOppositePit + 1); //deposit seeds into P2 house
                        } else if (pitId == playerTwoHouse) {
                            currentBoard.setTurn(EPlayer.Player2); //give free turn
                            pits.put(playerTwoHouse, pits.get(playerTwoHouse) + 1); //deposit seeds into P2 house
                        } else if (pitId > playerOneHouse || pitId < playerOneHouse) {
                            pits.put(pitId, seedCount + 1);
                        }
                    }
                } else {
                    pits.put(pitId, seedCount + 1);
                }
                seedInHand--;
            }
        }

        if (checkIfLastRound(currentBoard, currentPlayer)) {
            currentBoard = getOpponentsAllSeeds(currentBoard, currentPlayer);
            gameMap.remove(gameId); //Since it's the last round, removing the game from Map to open slots.
        }


        return currentBoard;
    }

    public EPlayer swapPlayer(EPlayer currentPlayer) {
        return currentPlayer.equals(EPlayer.Player1) ? EPlayer.Player2 : EPlayer.Player1;
    }

    public boolean checkIfLastRound(KalahBoard kalahBoard, EPlayer currentPlayer) {
        boolean lastRound = true;
        if (currentPlayer.equals(EPlayer.Player1)) {
            for (int i = 1; i < playerOneHouse; i++) {
                if (kalahBoard.getBoard().get(i) != 0) {
                    lastRound = false;
                    break;
                }
            }
        } else if (currentPlayer.equals(EPlayer.Player2)) {
            for (int i = playerOneHouse+1; i < playerTwoHouse; i++) {
                if (kalahBoard.getBoard().get(i) != 0) {
                    lastRound = false;
                    break;
                }
            }
        }
        return lastRound;
    }

    public KalahBoard getOpponentsAllSeeds(KalahBoard kalahBoard, EPlayer currentPlayer) {

        int seedInHand = 0;
        if (currentPlayer.equals(EPlayer.Player1)) {
            for (int i = playerOneHouse+1; i < playerTwoHouse; i++) {
                seedInHand = seedInHand + kalahBoard.getBoard().get(i);
                kalahBoard.getBoard().put(i, 0);
            }
            kalahBoard.getBoard().put(playerOneHouse, kalahBoard.getBoard().get(playerOneHouse) + seedInHand);
        } else if (currentPlayer.equals(EPlayer.Player2)) {
            for (int i = 1; i < playerOneHouse; i++) {
                seedInHand = seedInHand + kalahBoard.getBoard().get(i);
                kalahBoard.getBoard().put(i, 0);
            }
            kalahBoard.getBoard().put(playerTwoHouse, kalahBoard.getBoard().get(playerTwoHouse) + seedInHand);
        }


        return kalahBoard;
    }
}
