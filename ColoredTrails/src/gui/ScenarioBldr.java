package gui;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import data.Board;
import data.Tile;
import ui.CtColor;


public class ScenarioBldr {

	
	public static void SaveMap(String mapName, Board grid) {
		String mapData = "";
		mapData += grid.getRows() + "\n";
		mapData+= grid.getColumns() + "\n";
		mapData+= grid.getNum_colors_used() + "\n";
		for( int i=0;i<grid.getRows();i++) {
			for(int j=0;j<grid.getColumns();j++) {
				//changed it to j,i so it looks corresponding to the screen
				mapData += getTileColor(grid.getTile(j, i)) + " ";
			}
			mapData+="\n";
		}
		
		try {
			File file = new File(mapName);
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write(mapData);
			bw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Board LoadMap(String fileName) {
		Board board = null;
		
		//String filePath = "src/games/game"+fileName+".txt";
		
		try {
			Scanner scanner = new Scanner( new File(fileName));
			int rows = scanner.nextInt();
			int columns = scanner.nextInt();
			int numColors = scanner.nextInt();
			int [][]map = new int[rows][columns];
			for(int i=0;i<rows;i++) {
				for(int j=0;j<columns;j++) 
					map[j][i] = scanner.nextInt();
			}
			
			board = new Board(numColors,map);
			board.setRows(rows);
			board.setColumns(columns);
			
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			
		return board;
	}
	
	public static CtColor getTileColor(int index) {
		CtColor t = new CtColor(index);
		return t;
	}
	
	
	
	public static String getTileColor(Tile t){
		return t.getColor().getColorName();
	}

}
