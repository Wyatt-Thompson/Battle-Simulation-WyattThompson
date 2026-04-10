# Battle-Simulation-WyattThompson
This project was one of the various Java projects I have created out of my own accord, designed to be a "battle" between bouncing balls of different types (heavily inspired by Earclacks). By choosing not to use a game engine like Unity or Godot, I felt that I challenged myself to think outside of the box, while still achieving a satisfying end result. I particularly enjoyed this project, because it gave me the freedom to make my own design choices and experiment with sound effects.

## Inspiration
This project was heavily inspired by Weapon Balls, a simulation created by Earclacks.

[Visit Earclacks's Channel](https://www.youtube.com/@Earclacks)

## Executing Program

To compile this program, run the following command:

**javac \*.java**

To execute this program, run the following command to run the main class:

**java main**

## Features

### Players
The main premise of this simulation is spawning in physics-based balls (players) to make them fight against each other. Each player has a unique type, as listed below:
- **Melee:** A basic, slow-moving player with no gimmicks. *(100 HP)*
- **Dagger:** A small player that attacks with a dagger instead of using contact damage *(80 HP)*
- **Speedster:** A fast-moving player that gradually increases in speed over time *(90 HP)*

### Clashing
If two weapons collide, they will "clash," inverting the direction of rotation for both players.

### Winning
Once one player remains, the game pauses and ends. It is possible for a tie to happen where the last two players die at once.

### Pausing
Upon pressing space, the game can pause and unpause.
