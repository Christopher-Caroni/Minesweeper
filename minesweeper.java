class minesweeper extends Program {

	class Cell {
		boolean isDiscovered = false;
		boolean isBomb = false;
		int neighbours = 0;
	}

	final int WIDTH = 30;
	final int HEIGHT  = 30;
	Cell[][] map = new Cell[HEIGHT][WIDTH];
	boolean lost = false;
	boolean won = false;
	int debug = 0;
	boolean playagain = false;

	void algorithm() {
		do {
			// reset stats for new game
			clearScreen();
			cursor(0,0);
			playagain = false;
			lost = false;
			won = false;
			// parameters for game
			text("white");
			print("Probability for a cell to be a bomb? (0-100) ");
			double percent = readDouble() / 100;
			initCells();
			initBombs(percent);
			print("Debugging mode?  (Enter \"0\" if you don't know) ");
			debug = readInt();
			
			// core algorithm
			do{
				
				clearScreen();
				cursor(0,0);
				displayMap(-2,-2);
				// displaymap() shows all bombs if called with -1 or a number 
				// from 0 to 30 to pinpoint which bomb caused the game to end
				if(debug==1) {
					displayMap(-1,-1);
				}
				selectCell();
				testWin();
			} while (!lost && !won);
		} while (playagain);
		text("white");
	}
		
	void testWin() {
		String again = "";
		boolean notFinished = false;
		// false means this algorithm has found a non-bomb cell that 
		// has not been discovered yet. that means the player can not have won
		int line = 1;
		do {
			for (int column = 1; column<length(map,2)-1;column++) {
				if ((!map[line][column].isDiscovered && !map[line][column].isBomb) || lost) {
					notFinished = true;
				}
			}
			line = line + 1;
		} while (!notFinished && (line<length(map,1)-1));
		
		// all cells have been discovered, player has won
		if (!notFinished) {
			clearScreen();
			cursor(0,0);
			won = true;
			println();
			displayMap(-2,-2);
			println();
			text("yellow");
			println("    Congratulations! You have won!");
			text("white");
			print("    Do you want to play again? (Y/N)  ");
			again = readString();
			if (equals(again,"Y") || equals(again,"y") ) {
				playagain = true;
			}
		}
	}
	
	void displayLost(int lineOfBomb, int columnOfBomb) {
		String again = "";
		clearScreen();
		cursor(0,0);
		displayMap(lineOfBomb, columnOfBomb);
		println();
		text("white");
		println("     Sorry! You have lost");
		lost = true;
		print("    Do you want to play again? (Y/N)  ");
		again = readString();
		if (equals(again,"Y") || equals(again,"y") ) {
			playagain = true;
			println("Second chance");
		}
	}
	
	void selectCell() {
		int selectedLine= 0;
		int selectedColumn = 0;
		text("white");
		
		do {
			println();
			println();
			println("    What cell do you want to reveal ? ");
			print("    Line ? ");
			// frame is from 1 to 30 but real map is from 0 to 29
			// so we must substract to be able to call other functions
			selectedLine = (readInt() - 1);
			print("    Column ? ");
			selectedColumn = (readInt() - 1);
			if (map[selectedLine][selectedColumn].isDiscovered) {
				println();
				println("    You have already revealed this cell. Chose another one");
			}
		} while (map[selectedLine][selectedColumn].isDiscovered);
		
		
		if (map[selectedLine][selectedColumn].isBomb) {
			displayLost(selectedLine, selectedColumn);
		} else {
			revealAdjacentCells(selectedLine, selectedColumn);
		}
	}
	
	void revealAdjacentCells(int line, int column) {
			if ( (line>0) && (line<HEIGHT-1) && (column>0) && (column<WIDTH-1) ) {
				for (int nextLine = line-1; nextLine<=line+1;nextLine++) {
					for (int nextColumn = column-1; nextColumn<=column+1;nextColumn++) {
						if (!map[nextLine][nextColumn].isDiscovered && map[line][column].neighbours == 0) {
							// we must reveal the cell right away so the recursive algorithm wont be able
							// to call the same cell twice
							map[line][column].isDiscovered = true;
							revealAdjacentCells(nextLine, nextColumn);
						} else { 
						// the cell is adjacent to a bomb and we reveal it right a way because the recursive loop
						// is still runnning
							map[line][column].isDiscovered = true;
						}
					}
				}
			} else {
				map[line][column].isDiscovered = true;
			}
	}	
	
	void initCells() {
		for (int line = 0; line < HEIGHT ; line++) {
			for (int column = 0; column < WIDTH ; column++) {
				map[line][column] = new Cell();
			}
		}
	}

	void initBombs(double percent) {
		for (int line = 1; line < length(map,1) - 1 ; line++) {
			for (int column = 1; column < length(map,2) - 1 ; column++) {
				if ((random() <= percent)) {
					map[line][column].isBomb = true;
					incrementNeighboursOfACell(line, column);
				}
			}
		}
	}


	void incrementNeighboursOfACell(int centerLine, int centerCol) {
		for (int line = centerLine-1;line<=(centerLine+1); line++) {
			for (int column = centerCol-1;column<=(centerCol+1); column++) {
				map[line][column].neighbours = map[line][column].neighbours + 1;
			}
		}
		// above loop increments the bomb cell itself, so we must substact a "neighbour"
		map[centerLine][centerCol].neighbours = map[centerLine][centerCol].neighbours - 1;
	}

	void displayMap(int lineBomb, int columnBomb) {
		println();
		
		for (int line = -1; line<length(map,1) ; line++) {
			text("white");
			for (int column = 0; column<length(map,2); column++) {
				if (column==0 && line==-1) {
					print("      " + (column+1) + "  ");
				} else if (column==0 && line>-1 && line<9) {
					print(" " + (line+1) + "    ");
				} else if(column==0 && line>-1) {
					print((line+1) + "    ");
				} else if (column<9 && line==-1) {
					print((column+1) + "  ");
				} else if (line==-1) {
					print((column+1) + " ");
				}
				
				// REAL PRINTING
				if(line>-1) {
					text("green");
					if (map[line][column].isBomb && (lineBomb==line) && (columnBomb==column) ) {
						text("yellow");
						print("B  ");
					} else if (map[line][column].isBomb && lineBomb > -2) {
						text("red");
						print("B  ");
					} else {
						if (map[line][column].isDiscovered) {
							printNeighbours(line, column);
						} else {
							print(".  ");
						}
					}
				}
			}
			println();
		}
	}
	
	// special function to change colors according to number of neighbours
	void printNeighbours(int lineCell, int columnCell) {
		if (map[lineCell][columnCell].neighbours == 0) {
			print("   ");
		} else if (map[lineCell][columnCell].neighbours == 1) {
			text("cyan");
			print(map[lineCell][columnCell].neighbours + "  ");
		} else if (map[lineCell][columnCell].neighbours == 2) {
			text("green");
			print(map[lineCell][columnCell].neighbours + "  ");
		} else if (map[lineCell][columnCell].neighbours >= 3) {
			text("red");
			print(map[lineCell][columnCell].neighbours + "  ");
		}
	}

}


