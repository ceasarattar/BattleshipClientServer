import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;

	ArrayList<ClientThread> waitingForGame = new ArrayList<>();  //users that want to play online game
	//will be added here once there is 2 a new game is created for them. This takes place in ClientThread
	//on the server side once the thread receives GameInfo
	ArrayList<BattleShipsGame> currentGames = new ArrayList<>();
	//This will be list of current games being played out
	int battleShipGameIndex = 0;
	//Used to track the games. Each client thread has a game index so that server knows which game they
	//are playing





	Server(Consumer<Serializable> call){

		callback = call;
		server = new TheServer();
		server.start();
	}


	public class TheServer extends Thread{

		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);){
				System.out.println("Server is waiting for a client!");


				while(true) {

					ClientThread c = new ClientThread(mysocket.accept(), count);
					callback.accept("client has connected to server: " + "client #" + count);
					clients.add(c);
					c.start();

					count++;

				}
			}//end of try
			catch(Exception e) {
				callback.accept("Server socket did not launch");
			}
		}//end of while
	}


	class ClientThread extends Thread{


		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;

		ClientThread opponent;
		int gameIndex;
		int playerNumber;


		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;
		}

		public void updateClients(String message) {
			for(int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				try {
					t.out.writeObject(message);
				}
				catch(Exception e) {}
			}
		}

		public void run(){

			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}

			updateClients("new client on server: client #"+count);

			while(true) {
				try {
					GameInfo data = (GameInfo)in.readObject();
					callback.accept("client: " + count + " sent: " + data);

					if (data.lookingForGame) {
						waitingForGame.add(this); //add clientthread to arraylist
						//if there is 2 thread there, clear the list and match them together
						if (waitingForGame.size() == 2) {
							//each client thread has the oponets thread for easier communication
							waitingForGame.get(0).opponent = waitingForGame.get(1);
							waitingForGame.get(1).opponent = waitingForGame.get(0);
							GameInfo newGame = new GameInfo();
							newGame.gameFound = true;
							BattleShipsGame game = new BattleShipsGame();
							currentGames.add(game);
							//new game was created and added to list of games
							System.out.println("New game started index: " + battleShipGameIndex +
									" between user number: " + waitingForGame.get(0).count +
									" and player number: " + waitingForGame.get(1).count);
							//give each waiting player the index of the current game
							waitingForGame.get(0).gameIndex = battleShipGameIndex;
							waitingForGame.get(1).gameIndex = battleShipGameIndex;
							battleShipGameIndex++;
							//give them palyer numbers
							waitingForGame.get(0).playerNumber = 1;
							waitingForGame.get(1).playerNumber = 2;
							//send to them that game was found and clear the list
							waitingForGame.get(0).out.writeObject(newGame);
							waitingForGame.get(1).out.writeObject(newGame);
							waitingForGame.clear();
						}
					}
					else if (data.lookingForOfflineGame) {
						BattleShipsGame game = new BattleShipsGame();
						currentGames.add(game);
						System.out.println("Ai game found");
						gameIndex = battleShipGameIndex;
						battleShipGameIndex++;
						playerNumber = 1;
						ServerAIPlayer ai = new ServerAIPlayer();
						ai.enemyBoard = game.player1Board;
						game.player2Board = ai.aiBoard;
						game.AiPlayer = ai;

						System.out.println("Ai board");
						ai.aiBoard.printBoard();
						System.out.println(game.player2Board.currentShip);
						game.player2Board.currentShip = 5;
					}
					//ships is being placed
					else if (data.placeShip) {
						ShipBoard temp;
						BattleShipsGame game = currentGames.get(gameIndex);
						//get the board from given player
						if (playerNumber == 1) {
							temp = game.player1Board;
						}
						else {
							temp = game.player2Board;
						}
						int r1 = data.r1;
						int c1 = data.c1;
						int r2 = data.r2;
						int c2 = data.c2;
						//try to place ship at given location
						int val = temp.placeShip(temp.ships[temp.currentShip], r1, c1, r2, c2);
						GameInfo placementMessage = new GameInfo();
						placementMessage.placeShip = true;
						if (val == 1) {
							temp.currentShip++;
							placementMessage.validPlacement = true;
							//cords for the gui
							placementMessage.r1 = r1;
							placementMessage.c1 = c1;
							placementMessage.r2 = r2;
							placementMessage.c2 = c2;
							//if all ships are placed add the flag to disable the placement gui
							if (temp.currentShip == 5) {
								placementMessage.allShipsPlaced = true;
								System.out.println("All ships placed");
							}
						}
						//send the message
						//if placement was invalid then valid placement is false by default
						out.writeObject(placementMessage);
						//server/debugging purposes
						System.out.println("Ship placement for game index " + gameIndex);
						System.out.println("P1 board");
						game.player1Board.printBoard();
						System.out.println("P2 board");
						game.player2Board.printBoard();

						//if both players placed all ships start the game
						if (game.player1Board.currentShip == 5 && game.player2Board.currentShip == 5) {
							System.out.println("All ships placed for both players in game " + gameIndex);
							GameInfo startGame = new GameInfo();
							startGame.allShipsPlacedBothPlayers = true;
							if (game.AiPlayer != null) {
								startGame.yourTurn = true;
								out.writeObject(startGame);
							}
							else {
								out.writeObject(startGame);
								startGame.yourTurn = true; //whoever placed first, its their turn
								opponent.out.writeObject(startGame);
							}
						}


					}
					else if (data.hitShip && data.aiGame) {
						int hitRow = data.hitShipRow;
						int hitCol = data.hitShipCol;
						int result;
						int shipsLeft;
						BattleShipsGame game = currentGames.get(gameIndex);
						result = game.player2Board.hitShip(hitRow, hitCol);
						shipsLeft = game.player2Board.shipsLeft;
						GameInfo hitResult = new GameInfo();
						hitResult.shipHitResult = result;
						hitResult.shipsLeftAfterHit = shipsLeft;
						hitResult.hitShipRow = hitRow;
						hitResult.hitShipCol = hitCol;
						hitResult.youHitShip = true;
						System.out.println("Your move " + hitRow + " " + hitCol);
						out.writeObject(hitResult);

						GameInfo aiHit = game.AiPlayer.makeAIMove();
						hitRow = aiHit.hitShipRow;
						hitCol = aiHit.hitShipCol;
						System.out.println("Ai move " + hitRow + " " + hitCol );

						result = game.player1Board.hitShip(hitRow, hitCol);
						shipsLeft = game.player1Board.shipsLeft;
						GameInfo aiHitResult = new GameInfo();
						aiHitResult.shipHitResult = result;
						aiHitResult.shipsLeftAfterHit = shipsLeft;
						aiHitResult.hitShipRow = hitRow;
						aiHitResult.hitShipCol = hitCol;
						aiHitResult.yourShipWasHit = true;
						out.writeObject(aiHitResult);

					}
					else if (data.hitShip) {
						int hitRow = data.hitShipRow;
						int hitCol = data.hitShipCol;
						int result;
						int shipsLeft;
						//a message that ship was hit
						BattleShipsGame game = currentGames.get(gameIndex);
						//check from who was the message and hit the ship on enemy's board
						if (playerNumber == 1) {
							result = game.player2Board.hitShip(hitRow, hitCol);
							shipsLeft = game.player2Board.shipsLeft;
						}
						else {
							result = game.player1Board.hitShip(hitRow, hitCol);
							shipsLeft = game.player1Board.shipsLeft;
						}
						//the one who shot needs to know the cords they shot, result, how many ships opponent has left
						GameInfo hitResult = new GameInfo();
						hitResult.shipHitResult = result;
						hitResult.shipsLeftAfterHit = shipsLeft;
						hitResult.hitShipRow = hitRow;
						hitResult.hitShipCol = hitCol;
						hitResult.youHitShip = true;
						out.writeObject(hitResult);
						//the one who got hit needs to know which ship, cords, and how many ships they have left
						//why, for the auto lose and win screen
						GameInfo oponentHitResult = new GameInfo();
						oponentHitResult.yourShipWasHit = true;
						oponentHitResult.hitShipRow = hitRow;
						oponentHitResult.hitShipCol = hitCol;
						oponentHitResult.shipHitResult = result;
						oponentHitResult.shipsLeftAfterHit = shipsLeft;
						opponent.out.writeObject(oponentHitResult);

					}

				}
				catch(Exception e) {
					callback.accept("OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!");
					clients.remove(this);
					break;
				}
			}
		}//end of run


	}//end of client thread
}





