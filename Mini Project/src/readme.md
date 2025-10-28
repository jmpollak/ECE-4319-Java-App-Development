# Project 1
Created by John

## General Notes

### 10/7/2025

Took the code from in-class and copied it over 
to this folder.

### 10/15/2025

We continued to work on the project a little more.
Made some changes and was able to get the basics of the
project working. There are some style choices that need
to be made, but ultimately it satisfies the assignment. 
The only thing that needs to be added is a play again
button. 



## Project Assignment

### List of requirements

#### 1. Welcome Screen (20%)

1. (10%) Your program has a welcome screen displaying
a game title. 

2. (10%) Create a Start button. After clicking the 
start button, the game  should start.

#### 2. Given_Code.Question Display (20%)

1. (15%) After started, your game should display 
questions one at a time. Each question appears with 
four possible answers, like the one shown in Fig. 1 
above (i.e., use JButton).

2. (5%) A quiz session should have at least 10
questions.

#### 3. Game Flow (25%)

1. (20%) Show whether the player’s answer is 
Correct / Incorrect. Once player selects one option 
(clicks on one button), game immediately checks if it’s 
correct. If correct, show “correct” on the screen;
otherwise,show “incorrect” on the screen. Answers must 
be logically and meaningfully validated (i.e. checking
the correctness of answers).

2. (5%) Move automatically to the next question.

#### 4. Score Tracking (20%)
1. (20%) Keep track of the player’s score. If player’s answer is correct, give
   scores to the player; otherwise, give some other scores to the player.
   The scores you give must be logically correct and meaningful (i.e.
   checking the correctness of answers).

#### 5. Results Screen (15%)
1. (10%) At the end if no more questions, show a Results
Screen with finalscore and the player’s nickname.

2. (5%) At the end of the quiz, offer a Play Again
button to restart and back to welcome screen.

### Extra Features and Extra Credit (25%)

#### 1. Given_Code.Question Categories (5%)
1. Create multiple categories for player to choose 
from on the welcome screen (such as Test preparation
for java, DMV knowledge test question bank, other 
subjects such as History, Sports...etc.)

#### 2. Timer per Given_Code.Question (5%)
1. Set up a timer and define a time limitation that 
the player must answer before selecting one option. 
If the player does not select an answer in time, mark 
incorrect without points and go to the next question.

#### 3. Player Name (5%)
1. Ask players to enter their nicknames in a textfield. 
Show the name on game screen. Your program should handle
the case when a name is not provided.

#### 4. Logic Behind the Questions (5%)
1. Questions bank reaches at least 100 questions, and
every time the program will pick different question
based on scientific way (pick randomly, tree structure 
to organize questions, or other algorithms you prefer.)

#### 5. Animated Feedback (5%)
1. Change label color (e.g., green for correct, red for 
wrong.)

2. Animations on display throughout the whole game.

### Non-Functional Requirements

#### 1. User-Friendly GUI:
1. Layout must be neat, readable, and visually appealing.

#### 2. Code Structure:
1. Use classes (e.g., MainClass, GameEngine, GameFrame
...etc) with good separation of concerns.

#### 3. Data Structures:
1. Store questions in an ArrayList<Given_Code.Question> 
(or another suitable structure).

#### 4. Robustness:
1. Program should not crash when a player enters
unexpected input.