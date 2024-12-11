
import java.lang.Math;
public class ShipBoard {

    Ship[][] board;

    Ship[] ships;
    int shipsLeft;
    int currentShip = 0;
    ShipBoard() {
        board = new Ship[10][10];
        ships = new Ship[5];
        Ship s1 = new Ship(2);
        Ship s2 = new Ship(3);
        Ship s3 = new Ship(3);
        Ship s4 = new Ship(4);
        Ship s5 = new Ship(5);
        ships[0] = s1;
        ships[1] = s2;
        ships[2] = s3;
        ships[3] = s4;
        ships[4] = s5;
        shipsLeft = 5;
    }

    //those will be from 0 to 9 but where we call it we will modify aruemts
    //return 0 if invalid
    //return 1 if valid
    int placeShip(Ship s, int r1, int c1, int r2, int c2) {

        if (r1 > 9 || r1 < 0 || r2 > 9 || r2 < 0 || c1 > 9 || c1 < 0 || c2 > 9 || c2 < 0) {
            System.out.println("Cords out of bounds");
            return 0;
        }
        else if (r1 == r2 && c1 == c2) {     //both x and y coordinates are the same so ship is a point
            System.out.println("Invalid cords");
            return 0;
        }
        else if  (r1 != r2 && c1 != c2) {   //if both x's and y's are different then ship is diagonal
            System.out.println("Invalid cords");
            return 0;
        }
        // check if the coordinates match the ship size
        int rowDiff = Math.abs(r2 - r1);
        int colDiff = Math.abs(c2 - c1);
        System.out.println("row diff " + rowDiff);
        System.out.println("col diff " + colDiff);

        boolean horizontal;

        if (rowDiff == 0) {  //in the same row
            horizontal = true;
        }
        else {     //different rows
            horizontal = false;
        }
        System.out.println(horizontal);

        //why + 1
        //ship at cords x = 1 and x = 4 has length 4 but diff in cords is 3
        //check if coords match length
        if (horizontal && colDiff + 1 != s.size) {
            System.out.println("Wrong cords for a ship with given size");
            return 0;
        }
        else if (!horizontal && rowDiff + 1 != s.size) {
            System.out.println("Wrong cords for a ship with given size");
            return 0;
        }

        //test that later
        //why max and min are in the loop. Because there is if the first coord is bigger than the second one
        //r1 > r2 then there needs to be two more if and 2 more loops that do i--. Instead we will always go from
        //smaller to bigger

        //horizontal ship would ne  (2,4)  (2,5)   (2,6)   (2,7)
        //horizontal measn same row and different colums
        if (horizontal) {
            for (int i = Math.min(c1, c2); i <= Math.max(c1, c2); i++) {
                if (board[r1][i] != null) {
                    System.out.println("Something exists there arleadt");
                    return 0;
                }
            }
            //coordinates will be in the form A1   B4  C10

            int shipArrayIndex = 0;
            //this places the ship but also updates its coordinates in its own array
            //maybe we will delet this later but i think it will be good to check if ship was hit in
            //the spot already
            for (int i = Math.min(c1, c2); i <= Math.max(c1, c2); i++) {
                board[r1][i] = s;
                s.coordinatesR[shipArrayIndex] = r1;
                s.coordinatesC[shipArrayIndex] = i;
                shipArrayIndex++;
            }
        }
        else {
            //vertical means same column different row
            //(1,5) (2,5)  (3,5)  (4,5)
            for (int i = Math.min(r1, r2); i <= Math.max(r1, r2); i++) {
                if (board[i][c1] != null) {
                    System.out.println("Something already exists there");
                    return 0;
                }
            }
            int shipArrayIndex = 0; //same as above
            for (int i = Math.min(r1, r2); i <= Math.max(r1, r2); i++) {
                board[i][c1] = s;
                s.coordinatesR[shipArrayIndex] = i;
                s.coordinatesC[shipArrayIndex] = c1;
                shipArrayIndex++;
            }
        }
        return 1;
    }


    //Returns  0 if missed   1 if hit and didnt destory 2 if hit and destroyed
    //3 if you hit but it was already hit
    //5 if there is an issue locating the index which will be deleted once we have test cases done

    int hitShip(int row, int col) {

        if (board[row][col] == null) {
            return 0;
        }
        else {
            Ship s = board[row][col];
            //based on the coordinates locate which part of the ship is hit
            int shipIndex = -1;
            for (int i = 0; i < s.size; i++) {
                if (s.coordinatesR[i] == row && s.coordinatesC[i] == col) {
                    shipIndex = i;
                    break;
                }
            }
            //Just for testing inthe begging
            if (shipIndex == -1) {
                System.out.println("Something wrong with indexing"); {
                    return 5;
                }
            }
            if (s.hit[shipIndex]) {
                System.out.println("You hit it before dummy");
                return 3;
            }
            else {
                System.out.println("Nice hit");
                s.hit[shipIndex] = true;
                s.hitpoints--;
                if (s.hitpoints == 0) {
                    System.out.println("You destryed the ship");
                    s.isDestroyed = true;
                    shipsLeft--;
                    return 2;
                }
                else {
                    return 1;
                }
            }


        }
    }

    void printBoard() {
        System.out.print("  | ");
        for (int i = 0; i < 10; i++) {
            System.out.print(i + " ");
        }
        System.out.println("\n------------------------");


        for (int i = 0; i < 10; i++) {
            System.out.print(i + " | ");
            for (int j = 0; j < 10; j++) {
                if (board[i][j] == null) {
                    System.out.print("0");
                }
                else {
                    System.out.print(board[i][j].size);
                }
                System.out.print(" ");
            }
            System.out.println("");
        }
    }



}

