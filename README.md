Bricker Game
A classic Brick Breaker game implemented in Java, focusing on Object-Oriented Design Patterns and robust game logic. This project was developed as part of my studies at The Hebrew University of Jerusalem.

🎮 Game OverviewBricker is an interactive game where the player controls a paddle to bounce a ball and destroy a grid of bricks. The game features various power-ups, multiple lives, and dynamic collision effects.

🛠 Key Features & Design PatternsThe project emphasizes clean code and extensibility through several design principles:Strategy Pattern: Used to handle diverse brick collision behaviors. This allows for easy integration of new effects like exploding bricks or extra balls without modifying the core Brick class (upholding the Open/Closed Principle).  

Composite Pattern: Implemented via CombinedCollisionStrategy to allow a single brick to trigger multiple special effects simultaneously.  Encapsulation & SRP: * Life Management: A dedicated LifeCounter acts as the single source of truth for player health, while LifeDisplay handles the UI representation.  Special Objects: Custom classes like TemporaryPaddle and Heart manage their own internal states and lifespans, keeping the main game manager lean. 

🚀 Special Abilities & MechanicsExtra Health: Falling hearts that restore player lives upon collection.  Extra Paddle: A temporary second paddle that disappears after a certain number of hits.  Exploding Bricks: Collision triggers a chain reaction affecting adjacent bricks in a 3x3 grid.  Extra Balls (Pucks): Adds multiple balls into play to increase difficulty and excitement.

💻 Technologies UsedLanguage: JavaDesign Patterns: Strategy, Composite, Factory.  Tools: Standard Java libraries and game engine frameworks.
