import java.util.*;

public class ClueGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number of players (between 3 and 6): ");
        int numPlayers = scanner.nextInt();
        scanner.nextLine();

        Game game = new Game();
        for (int i = 0; i < numPlayers; i++) {
            System.out.print("Enter name for player " + (i + 1) + ": ");
            String playerName = scanner.nextLine();
            game.addPlayer(new Player(playerName));
        }

        game.start();
    }
}

class Game {
    private List<Player> players;
    private List<String> characters;
    private List<String> locations;
    private List<String> rooms;
    private Map<String, String> solution;
    private boolean gameWon;

    public Game() {
        players = new ArrayList<>();
        characters = new ArrayList<>(Arrays.asList("Emma", "Liam", "Jack", "Sophia", "Emily", "Ella"));
        locations = new ArrayList<>(Arrays.asList("under the vase", "hidden drawer", "behind the photo", "inside the box", "under the table", "on top of the closet"));
        rooms = new ArrayList<>(Arrays.asList("greenhouse", "billiard room", "study room", "reception room", "bedroom", "piano room", "dining room", "kitchen", "library"));
        solution = new HashMap<>();
        gameWon = false;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void start() {
        setupGame();
        playGame();
    }

    private void setupGame() {
        Random random = new Random();
        String solutionCharacter = characters.get(random.nextInt(characters.size()));
        String solutionLocation = locations.get(random.nextInt(locations.size()));
        String solutionRoom = rooms.get(random.nextInt(rooms.size()));
        solution.put("character", solutionCharacter);
        solution.put("location", solutionLocation);
        solution.put("room", solutionRoom);
        characters.remove(solutionCharacter);
        locations.remove(solutionLocation);
        rooms.remove(solutionRoom);
        List<String> remainingCards = new ArrayList<>();
        remainingCards.addAll(characters);
        remainingCards.addAll(locations);
        remainingCards.addAll(rooms);
        Collections.shuffle(remainingCards);

        int numPlayers = players.size();
        int cardsPerPlayer = remainingCards.size() / numPlayers;
        int extraCards = remainingCards.size() % numPlayers;

        for (int i = 0; i < numPlayers; i++) {
            for (int j = 0; j < cardsPerPlayer; j++) {
                players.get(i).addCard(remainingCards.remove(0));
            }
            if (extraCards > 0) {
                players.get(i).addCard(remainingCards.remove(0));
                extraCards--;
            }
        }
    }

    private void playGame() {
        Random random = new Random();
        int currentPlayerIndex = 0;

        while (!gameWon) {
            Player currentPlayer = players.get(currentPlayerIndex);
            int diceRoll = rollDice();

            // Calculation of the new room based on the number of dice
            String newRoom = "";
            List<String> possibleRooms = new ArrayList<>();
            if (diceRoll % 2 == 0) {
                // Move to the even rooms
                for (int i = 1; i <= rooms.size(); i += 2) {
                    possibleRooms.add(rooms.get(i - 1));
                }
            } else {
                // Move to the odd rooms
                for (int i = 2; i <= rooms.size(); i += 2) {
                    possibleRooms.add(rooms.get(i - 1));
                }
            }
            newRoom = possibleRooms.get(random.nextInt(possibleRooms.size()));
            currentPlayer.setCurrentRoom(newRoom);

            // The player guesses
            String guess = currentPlayer.randomGuess();
            System.out.println(currentPlayer.getName() + " guesses: " + guess);

            // Check the guess
            boolean guessCorrect = checkGuess(currentPlayer, guess);
            if (guessCorrect) {
                System.out.println(currentPlayer.getName() + " won the game!");
                gameWon = true;
            } else {
                System.out.println("Wrong guess.");
                revealCardFromOtherPlayer(currentPlayer, guess);
            }

            // Move to the next player
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }

    private int rollDice() {
        Random rand = new Random();
        return rand.nextInt(6) + 1;
    }

    private boolean checkGuess(Player player, String guess) {
        String[] parts = guess.split(", ");
        String guessedCharacter = parts[0];
        String guessedLocation = parts[1];

        return guessedCharacter.equals(solution.get("character")) && guessedLocation.equals(solution.get("location"));
    }

    private void revealCardFromOtherPlayer(Player currentPlayer, String guess) {
        for (Player player : players) {
            if (player != currentPlayer) {
                String[] guessParts = guess.split(", ");
                for (String part : guessParts) {
                    if (player.hasCard(part)) {
                        System.out.println(player.getName() + " has the card: " + part);
                        return;
                    }
                }
            }
        }
    }
}

class Player {
    private String name;
    private List<String> cards;
    private String currentRoom;
    private static List<String> characters;
    private static List<String> locations;
    private static List<String> rooms;

    public Player(String name) {
        this.name = name;
        cards = new ArrayList<>();
        currentRoom = "";
        characters = Arrays.asList("Emma", "Liam", "Jack", "Sophia", "Emily", "Ella");
        locations = Arrays.asList("under the vase", "hidden drawer", "behind the photo", "inside the box", "under the table", "on top of the closet");
        rooms = Arrays.asList("greenhouse", "billiard room", "study room", "reception room", "bedroom", "piano room", "dining room", "kitchen", "library");
    }

    public String getName() {
        return name;
    }

    public String getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(String room) {
        currentRoom = room;
    }

    public void addCard(String card) {
        cards.add(card);
    }

    public boolean hasCard(String card) {
        return cards.contains(card);
    }

    public String randomGuess() {
        Random rand = new Random();
        String character = characters.get(rand.nextInt(characters.size()));
        String location = locations.get(rand.nextInt(locations.size()));
        return character + ", " + location;
    }
}