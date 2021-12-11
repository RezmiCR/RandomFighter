# RandomFighter
Minigame plugin for Minecraft Spigot Servers, tested in version 1.17.1

Written in Java 16 using the Spigot API and storing data using SQLite and the SQLite-JDBC driver,
mainly done for fun and learning purposes, I haven't included a license yet so standard GitHub 
rules apply to the licensing.

## Compiling
### IDE
As this was created only using Maven it's really easy to integrate into any IDE or fancy setup that
you like, therefore ***if you use an IDE just load the project for Maven and the `pom.xml` should
configure almost everything.***

### CLI
Using Maven just set yourself at the root of the project (where the `pom.xml` is located)

For compiling individual `.java` files into bytecode files, use
```bash
mvn compile
```

For packaging into a `.jar` at the `target\` folder, use
```bash
mvn assembly:single
```

For cleaning both compiling and packaging process, use
```bash
mvn clean
```

All of these operations can be combined into a single one
```bash
mvn clean compile assembly:single
```

## Adding to your server
I have to make a guide for this... But the plugin is still in development, so I don't know how you
got here really.

## Useful sources of information
In case you want to learn more of this subject, I'll leave here some websites that came in handy
while I was searching for information.
- [Spigot Wiki](https://www.spigotmc.org/wiki/index/)
- [Spigot API Javadocs](https://hub.spigotmc.org/javadocs/spigot/)
- [Java SDK 16 API Javadocs](https://docs.oracle.com/en/java/javase/16/docs/api/index.html)
- [SQLite JDBC repo](https://github.com/xerial/sqlite-jdbc)
- [Bukkit wiki](https://bukkit.fandom.com/wiki/Main_Page)
- [Maven CLI blog post](https://www.sohamkamani.com/java/cli-app-with-maven/)

## Tasks
### TODO:
- [ ] Custom lootTables for the enemies **
- [ ] Make more item special habilities *
- [ ] Configure the waves with different enemies *
- [ ] Amount of enemies per wave according to players in game *
- [ ] Create a /updateroom [roomName] command
- [ ] Figure out the sqlite file placement
- [ ] Manage DB connections, nothing fancy yet

### DONE:
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

### TODO-IF-RELEASED:
I don't intend to release this, but if I were to do it, here are some things that need to be taken
into consideration.
- [ ] Make a comprehensive but quick guide of usage
- [ ] Swap back the inv when shutting down if the player is midgame
- [ ] Manage DB connections in a smart way
- [ ] Implement /deleteroom
- [ ] Handle correctly onDisable while games are running
- [ ] Clean task management for waves and each gameloop

