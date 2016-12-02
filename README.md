# battleship-2016

TEAM7 Battleship bot at [Purdue's Battleship 2016 competition](http://battleship.purduehackers.com/).
Majority of the source code was provided by the Purdue Hackers.
We only implemented the algorithms for make move and placement.

Feel free to clone, then run two instances of [Battleship.java](https://github.com/TomFanella4/battleship-2016/blob/master/Battleship.java) and [watch them play](http://battleship.purduehackers.com/game).

### Attack Algorithm

The attack or "make move" algorithm is based on this [statistical analysis approach](http://www.datagenetics.com/blog/december32011/). 
The idea is to create a probability distribution of the board after each move and 
attack on the cell with the highest probability.

