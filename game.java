
public class game {
    //board is a 2D array that represents the game board, with 7 columns and 6 rows
    private String[][] board = new String[6][7];
    private int lastMoveColumn;
    private int lastMoveRow;

    public static void main(String[] args){
        game game = new game();
        game.printBoard();
        //Game test cases
        /** 
        game.insertPiece(7, "X");
        game.insertPiece(6, "O");
        game.insertPiece(5, "X");
        game.insertPiece(4, "X");
        game.insertPiece(6, "X");
        game.insertPiece(5, "X");
        game.insertPiece(5, "X");
        game.insertPiece(4, "O");
        game.insertPiece(4, "X");
        game.insertPiece(4, "X"); */
        game.printBoard();
        System.out.println(game.checkWin("X",game.lastMoveColumn,game.lastMoveRow));
    }
    public void playAMovePlayer1(int column, String piece){
        insertPiece(column, piece);
        printBoard();
    }
    public void playAMovePlayer2(int column, String piece){
        String result = "";
        insertPiece(column, piece);
        printBoard();
    }
    public game(){
        //initialize the board with empty spaces
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                board[i][j] = " ";
            }
        }   
    }
    public void printBoard(){
        //print the game board with numbered columns
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                System.out.print(" | " + board[i][j]);
            }
            System.out.println(" |");
        }
        System.out.println("   1   2   3   4   5   6   7");
        System.out.println("-------------------------------");
    }
    public void insertPiece(int column, String piece){
        //insert a piece into the specified column
        if(column < 1 || column > 7){
            System.out.println("Invalid column number. Please enter a number between 1 and 7.");
        }
        else{
            dropPiece(column, piece);
        }
    }

    //method to drop a piece in a column
    public boolean dropPiece(int column, String piece){
        //find the lowest empty row in the column
        int row = 5;
        while(board[row][column-1] != " "){
            row--;
        }
        //place the piece in the lowest empty row
        board[row][column-1] = piece;
        lastMoveColumn = column-1;
        lastMoveRow = row;
        return true;
    }

    public boolean checkWinHorizontal(String piece, int column, int row){
        //check horizontal
        if(column <= 3 && board[row][column] == piece && board[row][column+1] == piece && board[row][column+2] == piece && board[row][column+3] == piece){
            return true;
        }
        else if(column >=3 && board[row][column] == piece && board[row][column-1] == piece && board[row][column-2] == piece && board[row][column-3] == piece){
            return true;
        }
        
        return false;
    }
    public boolean checkWinVertical(String piece, int column, int row){
        boolean win = false;
        System.out.println("row: " + row);
        //check vertical
        if(row < 3 && this.board[row][column] == piece && board[row+1][column] == piece && board[row+2][column] == piece && board[row+3][column] == piece){
            win = true;
        }
        else if(row >= 3 && board[row][column] == piece && this.board[row-1][column] == piece && board[row-2][column] == piece && board[row-3][column] == piece){
            win = true;
        }
        return win;
    }
    public boolean checkWinDiagonal(String piece,int column, int row){
        //check diagonal
        if(row < 3 && column >= 3 && board[row][column] == piece && board[row+1][column-1] == piece && board[row+2][column-2] == piece && board[row+3][column-3] == piece){
            return true;
        }
        else if(row >=3 && column >=2 && board[row][column] == piece && board[row-1][column+1] == piece && board[row-2][column+2] == piece && board[row-3][column+3] == piece){
            return true;
        }
        else if(row <= 2 && column <=3 && board[row][column] == piece && board[row+1][column+1] == piece && board[row+2][column+2] == piece && board[row+3][column+3] == piece){
            return true;
        }
        else if(row >= 3 && column >= 3 && board[row][column] == piece && board[row-1][column-1] == piece && board[row-2][column-2] == piece && board[row-3][column-3] == piece){
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
    public String result(boolean win){
        String message = "";
        if(win){
            message = "You win!";
        }
        return message;
    }
}