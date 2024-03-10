# MinigameFramework

## General Info
This framework was built with the purpose of making arena based minigames such as woolwars, sumo, skywars etc. However with minimal addition of an arena loader and other interfaces it can likely
be adapted to work with any minigame.

## Archetecture
All minigame state is contained within or linked to an instance of [Game](Core/src/main/java/com/readutf/inari/core/game/Game.java).
Each Game instance must have an [ActiveArena](Core/src/main/java/com/readutf/inari/core/arena/ActiveArena.java) tied to the game at all times as many different modules are tied into this (e.g Spectators)

## Game
A game is created using the builder pattern, and is provided the arena, event manager, and the teams fighting in the match.
*The following code snippet is taken from the Development module* [SumoGameStarter](Development/src/main/java/com/readutf/inari/test/games/sumo/SumoGameStarter.java)
```java
Game game = Game.builder(InariDemo.getInstance(), load, eventManager, teams,
      (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound),
      (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound),
      (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound),
      (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound),
      (game1, previousRound) -> new SumoRound(game1, (SumoRound) previousRound))
        .setPlayerSpawnHandler(new TeamBasedSpawning("spawn"))
        .setSpectatorSpawnHandler(new SumoSpectatorSpawnFinder())
        .build();
```


### Rounds
The flow of the gamemode is tied directly to your use of Rounds. A round can signify a literal round within the minigame or be used to manage state and flow. Within the Development module, specifically the sumo minigame,
you can see how Rounds can be used to manage the pre-game, fighting, and post game states.

# Thanks To
![68747470733a2f2f7777772e796f75726b69742e636f6d2f696d616765732f796b6c6f676f2e706e67](https://github.com/utfunderscore/MinigameFramework/assets/83981186/6e298393-5076-4b1a-9df9-ec1cca9b3b5a)


YourKit supports open source projects with innovative and intelligent tools
for monitoring and profiling Java and .NET applications.
YourKit is the creator of <a href="https://www.yourkit.com/java/profiler/">YourKit Java Profiler</a>,
<a href="https://www.yourkit.com/dotnet-profiler/">YourKit .NET Profiler</a>,
and <a href="https://www.yourkit.com/youmonitor/">YourKit YouMonitor</a>.

