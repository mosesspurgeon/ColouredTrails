

ColouredTrails

Game starts from boot.java. Opens the StartGameWindow.java and there gets input from user along with the game number, X. Then initialzes the GameStatus and the ColoredTrailsBoardGui. 
Reads configuration files ( in GameStatus.java in method loadGameFromFile) gameX.txt which can be found in games/X/ directory along with the gameXPhases.txt


Explanation of Configuration files : 
gameN.txt


Rows 3
Columns 3
Scoring a,b,c // goalweight-value of being at the goal, distweight-cost of unit distance from goal,chipweight-value of chips still in posession
Number of Colors Used 2
Map 0 1 0
1 0 0
1 1 0
Number of human players
Initial human player 1 location x,y,name //X increases from left to right. Y increases from top to bottom
Number of computer players
Initial computer player 1 location x,y,name
Initial computer player 2 location x,y,name
Number of goals
Initial goal location for goal 1 : x,y
Number of Chips for human player 1: 5
Chip distribution for human player 1: 0 1 1 0 0 
Number of Chips for computer player 1: 5
Chip distribution for computer player 1: 0 1 1 0 0 
Number of Chips for computer player 2: 5
Chip distribution for computer player 2: 0 1 1 0 0 



gameNPhases.txt



3 // Number of phases
Communication  //Name of first Phase
120 // time of first phase
True //is the phase indefinite
Exchange //Name of second Phase
120 //time of second phase
False //is the phase indefinite
Movement //Name of third Phase
0 //time of third phase
True //is the phase indefinite


The python code createRandomBoard.py randomly generates configuration files. 


Currently the game operates with up to 10 colors. Additional colors will have to be added directly to the GUI in InitialChipDistribution.InitialChipDistribution(), in CtColor.java  as well as updating Board.NUM_COLORS. 


Current Game Scenario : 

Each Player asks for all chips according to best path to goal ( while not offering anything in exchange ). Only if the request doesn't coincide with the other players request will the agent be successfull ( see method BestUse.performExchanges and BestUse.checkIfExchangeIsValid ). 
Currently the phases are not timed but set consecutively :
Communication - in which chip exchanges are calculated and may be suggested by the human player. 
Exchange - in which chip exchanges take place. At the moment all possible exchanges happen. 
Movement - in which each player moves one step. This can be easily adjusted to move each player to the final location instead (in ColoredTrailsBoardGui.movePlayers call GameStatus.moveAllSteps instead of GameStatus.moveOneStep ). 



Methods affecting game other than configuration files : 

Player.java


addColors
useColor
giveColors   //Does not give colors that the agent needs 



BestUse.java

performeExchanges   // performs exchanges but calls chooseFirstRequest
chooseFirstRequest  // if there are multiple requests for the same chip allocates it to the first agent in 	sequential order
calcBestPath // calculates best path according to DFS. It might be a good idea to change the way this is done. 
calcExchanges // calculates which exchanges each agent will want  to perform



Update To Offering Chips. 

Currently the game supports only exchanging with one agent at each turn. You may offer a lot of chips of different colours and request a lot of chips of different colours but only from one agent at a time. 
The Exchange class has been updated. It now has two hashmaps one for chips offered and one for chips exchanged. The chips offered by the human player are not taken into account at the moment and the computer players currently do not offer any chips in exchange. 

If there are multiple agents requesting the same chips - the winners will be determined by the chooseFirstRequest method in sequential order, starting from the Human player.  





