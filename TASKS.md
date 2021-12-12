# Tasks
## TODO:
- [ ] Amount of enemies per wave according to players in game
- [ ] Automatically divide time for waves
- [ ] Make more item special habilities
- [ ] Manage DB connections, nothing fancy yet

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

## TODO-IF-RELEASED:
I don't intend to release this, but if I were to do it, here are some things that need to be taken
into consideration.
- [ ] Prevent SQL injections (prepared statements)
- [ ] Make a comprehensive but quick guide of usage
- [ ] Manage DB connections in a smart way
- [ ] Use custom LootTables for the enemies (It'd have to be implemented at API)
- [ ] Implement /deleteroom
- [ ] Handle correctly onDisable while games are running
- [ ] Clean task management for waves and each gameloop
