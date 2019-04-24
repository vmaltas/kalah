# Kalah Game API
This project aims to create a 6-stone Kalah game to 2 players.
Version:Api-0.0.1-SNAPSHOT

# Installation
-Kalah Game API is a maven project written in Java 8.
-Simply run the KalahApplication with default parameters in Application Server
-No database connection required

# Usage

Project has 2 API's included, which can be called via any REST API Testing Tool (Postman...etc)

-/games : to create a Kalah game

-/games/{gameId}/pits/{pitId} : to make a move on specific pit 

# Authentication

No Authentication is required. 


# Data Format

-/games 

Request Example:

POST /games/ HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: 06600861-de36-4c42-f3f8-5865669799d2
{}

Response Example:
{
    "id": "7128",
    "uri": "http://localhost:8080/games/7128"
}


-/games/{gameId}/pits/{pitId} 

Request Example:

PUT /games/7128/pits/13 HTTP/1.1
Host: localhost:8080
Content-Type: application/json
Cache-Control: no-cache
Postman-Token: 6c552661-061a-6bbb-7c61-7bd9e1ae45f2

{}

Response Example:
{
    "id": "7128",
    "url": "http://localhost:8080/games/7128",
    "status": {
        "1": 0,
        "2": 5,
        "3": 5,
        "4": 5,
        "5": 5,
        "6": 4,
        "7": 0,
        "8": 4,
        "9": 4,
        "10": 4,
        "11": 4,
        "12": 4,
        "13": 4,
        "14": 0
    }
}


# Credits

https://www.thesprucecrafts.com/: For providing Kalah game rules step by step.