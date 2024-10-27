/**
 * NAME : SALIEM ABRAHALEY KIDANE
 * STUDENT ID: C3395781
 * COURSE : SENG4500 
 * ASSIGNMENT : ASSIGNMENT 2
 * PROGRAM : Game file, runs the game and checks for a win
 */

public class game {
    //board is a 2D array that represents the game board, with 7 columns and 6 rows
    private String[][] board = new String[6][7]; 
    private int lastMoveColumn; //column of the last move
    private int lastMoveRow; //row of the last move

    //initialize the board with empty spaces
    public game(){
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                board[i][j] = " ";
            }
        }   
    }

    //method to get the column of the last move
    public int getLastColumn(){
        return lastMoveColumn;
    }
    //method to get the row of the last move
    public int getLastRow(){
        return lastMoveRow;
    }
   
    //print the game board with numbered columns
    public void printBoard(){
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                System.out.print(" | " + board[i][j]);
            }
            System.out.println(" |");
        }
        System.out.println("   1   2   3   4   5   6   7");
        System.out.println("-------------------------------");
    }

    //method to insert a piece into a column
    public boolean insertPiece(int column, String piece){
        boolean valid = dropPiece(column, piece);
        if (valid == false){
            System.out.println("Invalid move");
        }
        return valid;
    }

    //method to drop a piece in a column
    public boolean dropPiece(int column, String piece){
        //find the lowest empty row in the column
        int row = 5;
        while(board[row][column-1] != " " && row > 0){
            row--;
            System.out.println(row);
        }
        System.out.println("Entered in: "+row);
        if(row == 0 && board[row][column-1] != " "){
            System.out.println("Column is full. Please choose another column."); //check if the column is full
            return false;
        }
        else if(column < 1 || column > 7){
            System.out.println("Invalid column number. Please enter a number between 1 and 7."); //check if the column number is valid
            return false;
        }
        else{
            //place the piece in the lowest empty row
            board[row][column-1] = piece;
            lastMoveColumn = column-1; //update the last move column
            lastMoveRow = row; //update the last move row
            return true;
        }
    }

    //check if there is a win in the horizontal direction
    public boolean checkWinHorizontal(String piece, int column, int row){
        if(column <= 3 && 
        board[row][column] == piece 
        && board[row][column+1] == piece 
        && board[row][column+2] == piece 
        && board[row][column+3] == piece){
            return true;
        }
        else if(column >=3 
        && board[row][column] == piece 
        && board[row][column-1] == piece 
        && board[row][column-2] == piece 
        && board[row][column-3] == piece){
            return true;
        }
        
        return false;
    }
    
    //check if there is a win in the vertical direction
    public boolean checkWinVertical(String piece, int column, int row){
        boolean win = false;
        //check vertical
        if(row < 3 
        && board[row][column] == piece 
        && board[row+1][column] == piece 
        && board[row+2][column] == piece 
        && board[row+3][column] == piece){
            win = true;
        }
        else if(row >= 3 
        && board[row][column] == piece 
        && board[row-1][column] == piece 
        && board[row-2][column] == piece 
        && board[row-3][column] == piece){
            win = true;
        }
        return win;
    }

    //check if there is a win in the diagonal direction
    public boolean checkWinDiagonal(String piece,int column, int row){
        if(row < 3 && column >= 3 
        && board[row][column] == piece 
        && board[row+1][column-1] == piece 
        && board[row+2][column-2] == piece 
        && board[row+3][column-3] == piece){
            return true;
        }
        else if(row >=3 && column <=2 
        && board[row][column] == piece 
        && board[row-1][column+1] == piece 
        && board[row-2][column+2] == piece 
        && board[row-3][column+3] == piece){
            return true;
        }
        else if(row <= 2 && column <=3 
        && board[row][column] == piece 
        && board[row+1][column+1] == piece 
        && board[row+2][column+2] == piece 
        && board[row+3][column+3] == piece){
            return true;
        }
        else if(row >= 3 && column >= 3 
        && board[row][column] == piece 
        && board[row-1][column-1] == piece 
        && board[row-2][column-2] == piece 
        && board[row-3][column-3] == piece){
            return true;
        }
        return false;
    }
    
    //method to check if a player has won
    public boolean checkWin(String piece, int column, int row){
        boolean win = false;
        //check horizontal
        if (win == false){
            win = checkWinHorizontal(piece, column, row);
        }
        if (win == false){
            win = checkWinVertical(piece, column, row);
        }
        //check diagonal
        if (win == false){
            win = checkWinDiagonal(piece,column,row);
        }
        return win;
    }
    
    //method to check if there is a win
    public String result(boolean win){
        String message = "";
        if(win){
            message = "You win!";
        }
        return message;
    }
}