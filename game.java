
public class game {
    //board is a 2D array that represents the game board, with 7 columns and 6 rows
    private String[][] board = new String[7][6];

    public static void main(String[] args){
        game game = new game();
        game.printBoard();
    }
    public game(){
        //initialize the board with empty spaces
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 6; j++){
                board[i][j] = " ";
            }
        }
    }
    public void printBoard(){
        //print the game board with numbered columns
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 7; j++){
                System.out.print(" | " + board[j][i]);
            }
            System.out.println(" |");
        }
        System.out.println("   1   2   3   4   5   6   7");
        System.out.println("-------------------------------");
    }

    //method to drop a piece in a column
    public boolean dropPiece(int column, String piece){
        //find the lowest empty row in the column
        int row = 5;
        while(board[column-1][row] != " "){
            row--;
        }
        //place the piece in the lowest empty row
        board[column-1][row] = piece;
        return true;
    }
    
    //method to check if a player has won
    public boolean checkWin(String piece){
        //check horizontal
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 6; j++){
                if(board[i][j] == piece && board[i+1][j] == piece && board[i+2][j] == piece && board[i+3][j] == piece){
                    return true;
                }
            }
        }
        //check vertical
        for(int i = 0; i < 7; i++){
            for(int j = 0; j < 3; j++){
                if(board[i][j] == piece && board[i][j+1] == piece && board[i][j+2] == piece && board[i][j+3] == piece){
                    return true;
                }
            }
        }
        //check diagonal
        for(int i = 0; i < 4; i++){
            for(int j = 0; j < 3; j++){
                if(board[i][j] == piece && board[i+1][j+1] == piece && board[i+2][j+2] == piece && board[i+3][j+3] == piece){
                    return true;
                }
            }
        }
        for(int i = 0; i < 4; i++){
            for(int j = 3; j < 6; j++){
                if(board[i][j] == piece && board[i+1][j-1] == piece && board[i+2][j-2] == piece && board[i+3][j-3] == piece){
                    return true;
                }
            }
        }
        return false;
    }
    public String result(boolean win){
        String message = "";
        if(win){
            message = "You win!";
        }
        return message;
    }
}