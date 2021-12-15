# Tasks
## TODO:
- [ ] Clear spawn place
- [ ] Manage DB connections, nothing fancy
- [ ] Implement /resetscores [playername]|[all]
- [ ] Implement /deleterooms [roomname]|[all]

## DONE:
- [x] Commands to create a room by given coordinates
- [x] Store rooms in the SQLite DB
- [x] Load DB rooms onEnable
- [x] Command to join/create games on existent rooms
- [x] Keep control of the players in a game
- [x] Replace inv for the players
- [x] Change gamemode to adventure and set back to default
- [x] Teleport back on death (tag random_fighter)
- [x] Change the way /listrooms works
- [x] Save the starting room geometry, replace it back at the end of the game
- [x] Fix the sqlite file placement
- [x] Custom item drops for the enemies
- [x] Create a /updateroom [roomname] command
- [x] Configure the waves with different enemies
- [x] Custom scoreboard for competition
- [x] Amount of enemies per wave according to players in game
- [x] Automatically divide time for waves
- [x] Only clear inv if the player is in the same world as the room
- [x] Wave messages (and game start message)
- [x] Implement weight based spawn system
- [x] Don't let minigame slimes split
- [x] Implement weight based item system
- [x] Make it so mobs can't pick up items
- [x] General values tweaking for balancing
- [x] Make more item special habilities
- [x] Use prepared statements

## TODO-IF-RELEASED:
I don't intend to release this, but if I were to do it, here are some things that need to be taken
into consideration, in case someone else wants to fix my mistakes.
- [ ] YAML config file for waves, enemies, loot, special items...
- [ ] Make a comprehensive but quick guide of usage
- [ ] Manage DB connections in a smart way
- [ ] Custom LootTables for the enemies (Bukkit API has no tools for this)
- [ ] Handle correctly onDisable while games are running
- [ ] Clean task management for waves and each gameloop
