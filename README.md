
# Build
Clone the Git repo, check out the default `develop` branch. The build the solution with Maven:

`$ mvn clean install`

Some maven build and test messages should end with

` [INFO] BUILD SUCCESS `

Then locate the largest jar file in the `target` directory
 $ cd target
 $ ls -lh

..
 18M Jul 30 13:42 game-of-three-0.0.1-SNAPSHOT.jar
..

# Run

When in the `target` folder, run the `player1` app in the terminal:

`$ java -jar -Dspring.profiles.active=player1 game-of-three-0.0.1-SNAPSHOT.jar`

Then open the second terminal (Cmd+T) and run the `player2` app:


`java -jar -Dspring.profiles.active=player2 game-of-three-0.0.1-SNAPSHOT.jar `


Use web browser, `curl` console command or 'Postman'-like client to check the output of endpoints:

1. Play the game with a given first move value (sync endpoint): 'http://localhost:10001/player1/play/56'
2. Play the game with a given first move value (async endpoint): 'http://localhost:10001/player1/async/play/56'

# Game Workflow

The player in role `player1` always starts the game with a given or random int value in the range (0, Integer.MAX_VALUE). To use the random fisrt move value,
call the endpoint without the least path variable 'http://localhost:10001/player1/play'
The player who ends the game with eventType:'END_GAME' and moveValue:'1' is the winner
```
  {
    "gameId": "a2f0dd5b-5f36-421e-bd71-3c57dc2f0efa",
    "created": "2018-07-30T14:02:53.275",
    "eventId": "179b78d6-3978-457b-80e1-f4fe565c1180",
    "previousEventId": "318a9197-6f79-41e9-b9ae-ea4ad68be545",
    "previousMoveValue": 2,
    "player": "PLAYER_1",
    "type": "END_GAME",
    "moveValue": 1
  }
```

To play the game with maximum possible initial value: 'http://localhost:10001/player1/play/0xffffffe'
which corresponds to `Integer.MAX_VALUE-1`

# Available endpoints
## Next move for all roles
Both application profiles 'player1' and 'player2' provide the POST endpoints to make the next move, receiving GameEvent POJO as JSON:
'http://$host:$port/$role'
## Role `player1`
In addition to the basic 'next move' endpoints, two endpoints available: for making the first move and for playing the game in the automatic mode:
GET 'http://localhost:10001/player1/start/44' - returns the GameEvent of START_GAME type with the given value
GET 'http://localhost:10001/player1/start' - same as previous but with random move value

# Remotely-managed workflow

Th GameEvent entity is the single communication element between the services. The first move's entity can be produced by calling an endpoint described in the 'Role `player1`' section.

Then remote client can produce its one move and post it to an appropriate player by using 'http://$host:$port/$role' template.
The return value should be passed to the opponent.

When producing GameEvent instances remotely make sure to preserve consistent game identifier, player and event type information and move values.

# Local development environment

As the application shares the codebase, there's an option to run all services on the same host which is convenient
for development and integration testing purposes. Just run the app with both profiles specified:

`java -jar -Dspring.profiles.active=player1,player2 game-of-three-0.0.1-SNAPSHOT.jar`

and then use port 10002 for all communications.

# Running on separate hosts

When launching the 'player1' service, override the property -Dopponent.host=http://player-2-host:10002

To override both service's running ports use standard Springs property -Dserver.port=any-int-val


## Potential improvements and TODOs

1. Replace Random UUIDs with time-based UUIDs to make sure the sequential nature of the workflow.
2. Add Secure Hash field to ensure the consistency of communicated values.
3. Replace RestTemplate with KafkaTemplate to enforce async communication between services in the automatic play mode.
