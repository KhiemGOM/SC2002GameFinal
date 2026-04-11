# SC2002 Turn-Based Combat Arena

A command-line turn-based combat game developed for **SC2002 Object Oriented Design & Programming**.

This project focuses on:
- object-oriented design
- SOLID principles
- extensibility and maintainability
- UML-based software design
- clean separation between UI, control, and domain logic

---

## Project Overview

The game is a **turn-based combat arena** where a player selects a character and fights waves of enemies using:
- **Basic Attacks**
- **Defend**
- **Items**
- **Special Skills**
- **Status Effects**

The battle continues round by round until:
- the **player wins** by defeating all enemies, or
- the **player loses** when HP reaches 0

---

## Features

### Player Classes
- **Warrior**
  - High HP and defense
  - Special skill: **Shield Bash**
  - Deals damage and stuns a selected enemy

- **Wizard**
  - Higher attack but lower defense
  - Special skill: **Arcane Blast**
  - Deals damage to all enemies
  - Gains attack bonus when enemies are defeated by Arcane Blast

### Enemy Types
- **Goblin**
- **Wolf**

### Player Actions
- **Basic Attack**
- **Defend**
- **Use Item**
- **Special Skill**

### Items
- **Potion**  
  Restores HP

- **Power Stone**  
  Triggers special skill once without affecting cooldown

- **Smoke Bomb**  
  Makes enemy attacks deal 0 damage for the current and next turn

### Status Effects
- **Stun**
- **Defend Buff**
- **Smoke Bomb Invulnerability**
- **Arcane Blast Attack Bonus**

### Difficulty Levels
- **Easy**
- **Medium**
- **Hard**

Each level has different:
- enemy composition
- initial wave
- backup wave

### Battle System
- round-based combat flow
- turn order determined by speed
- backup enemy spawning
- battle-end checking
- cooldown handling
- persistent status effect handling

---

## Project Structure

The system is designed with clear separation of responsibilities:

- **Boundary / UI**
  - command-line interface
  - player input
  - battle display

- **Control / Engine**
  - battle flow
  - round logic
  - action processing
  - turn order
  - win/loss checking

- **Domain / Entity**
  - combatants
  - actions
  - items
  - status effects
  - stats and battle state

---

## OODP and SOLID Design

This project was designed to demonstrate key software engineering principles.

### OOP Concepts Used
- **Abstraction**
- **Encapsulation**
- **Inheritance**
- **Polymorphism**

### SOLID Principles Used
- **SRP** – each class has a focused responsibility
- **OCP** – new actions, status effects, items, or turn strategies can be added with minimal modification
- **LSP** – different combatants can be treated through common abstractions
- **ISP** – interfaces are kept focused
- **DIP** – core logic depends on abstractions rather than concrete implementations

---

## UML Diagrams

The UML diagrams for this project are included in the repository.
```text
