package wilson.poker;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Driver {
	public static Scanner scanner = new Scanner(System.in);

	/*
		visibilty_modifier return_type name(type1 param1, typ2 param2) {


		}

	*/

	public static void main(String[] args) {
		List<Player> players = new ArrayList<>();
		
		boolean ready = false;
		boolean validInput;
		String input = null;
		int playerNum;

		if (ready) {
			players.add(new Player("Jackson", 200));
			players.add(new Player("Nate", 300));
			players.add(new Player("Zoe", 192));
			players.add(new Player("Susan", 243));
			players.add(new Player("Tom", 220));
		}

		while (!ready) {
			validInput = false;
			playerNum = players.size() + 1;
			
			do {
				if (players.size() < 2) {
					System.out.print("Enter Player " + playerNum + "\'s name: ");
				} else {
					System.out.print("Enter Player " + playerNum + "\'s name (or press [Enter] to begin): ");
				}

				if (scanner.hasNextLine())
					input = scanner.nextLine();

				if (input.isEmpty() && players.size() >= 2) {
					ready = validInput = true;
				} else if (!input.isEmpty()) {
					validInput = true;
				} else {
					System.out.println("\tPlayer name cannot be empty.");
				}
			} while (!validInput);

			if (!ready) {
				String playerName = input;
				Player player = new Player(playerName);
				Double playerStartMoney = null;

				do {
					validInput = false;
					System.out.print("Enter " + playerName + "\'s starting money: ");
					if (scanner.hasNextLine())
						input = scanner.nextLine();

					if (!input.isEmpty()) {
						try {
							playerStartMoney = Double.parseDouble(input);
							if (playerStartMoney >= Game.BIG_BLIND) {
								validInput = true;
							} else {
								System.out.println("\tStarting money must be at least $" + Game.BIG_BLIND + ".");
							}
						} catch (NumberFormatException e) {
							System.out.println("\tStarting money must be a number.");
						}
					} else {
						System.out.println("\tStarting money cannot be empty.");
					}
				} while (!validInput);
				
				player.addMoney(playerStartMoney);
				players.add(player);

				System.out.println();
			}
		}
		
		Game g1 = new Game(players);
		g1.start();

		scanner.close();
	}
}
