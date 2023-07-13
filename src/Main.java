import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static Scanner scanner;
    public static Map<Integer, List<String>> gameTable;
    public static Map<Integer, List<String>> players;
    public static int playersCount;
    public static Map<String, List<String>> avenueProperties;
    public static Map<String, List<String>> railroadProperties;
    public static Map<String, List<String>> companyProperties;
    public static String ANSI_RESET = "\u001B[0m";
    public static String ANSI_RED = "\u001B[31m";
    public static String ANSI_GREEN = "\u001B[32m";
    public static String ANSI_YELLOW = "\u001B[33m";
    public static String ANSI_BLUE = "\u001B[34m";
    public static String ANSI_PURPLE = "\u001B[35m";
    public static String ANSI_CYAN = "\u001B[36m";
    public static String ANSI_INTENSE_RED = "\u001B[41m";
    public static String ANSI_INTENSE_GREEN = "\u001B[42m";
    public static String ANSI_INTENSE_GRAY = "\u001B[47m";
    public static List<String> chancesCarts;

    public static void main(String[] args) {
        fillGameTable();
        fillChancesCarts();
        printTable();
        scanner = new Scanner(System.in);
        initializePlayers();
        runGame();
    }

    public static void runGame() {
        while (playersCount > 2) {
            for (Map.Entry<Integer, List<String>> player : players.entrySet()) {
                if(player.getValue().get(3).equals("active")) {
                    String playerInJail = player.getValue().get(15);
                    if(playerInJail.equals("In jail")) {
                        player.getValue().set(15, "Not in jail");
                        System.out.println(getPlayerName(player) + " is in jail. You wait next turn.");
                    } else {
                        playerTurn(player);
                        printTable();
                    }
                }
            }
        }
    }

    public static void upPropertiesLevel(Map.Entry<Integer, List<String>> player) {
        List<String> playerProperties = player.getValue();
        for (int i = 6; i < playerProperties.size(); i++) {
            String[] coloredProperties = playerProperties.get(i).split(":");
            String neighborhoodColor = coloredProperties[0];
            if (coloredProperties[1].equals("3")) {
                List<String> neighborhoodProperties = new ArrayList<>();
                List<String> propertyLevels = new ArrayList<>();

                for (List<String> proprety : gameTable.values()) {
                    if (proprety.get(4).equals(neighborhoodColor)) {
                        String propertyName = proprety.get(2);
                        String propertyLevel = avenueProperties.get(propertyName).get(10);
                        neighborhoodProperties.add(proprety.get(2));
                        propertyLevels.add(propertyLevel);
                    }
                }

                if(propertyLevels.get(0).equals("5") && propertyLevels.get(1).equals("5") && propertyLevels.get(2).equals("5")) {
                    continue;
                }

                System.out.println("Do you want to upgrade " + neighborhoodColor + " neighborhood?");
                if(getAnswerInput()) {
                    boolean isUpdateFinished = false;
                    while (!isUpdateFinished) {
                        System.out.println("Please choose which property do you want to upgrade? 1, 2 or 3");
                        int inputChoice = getInputChoice(neighborhoodProperties);
                        String propertyName = neighborhoodProperties.get(inputChoice - 1);
                        List<String> avenueProperty = avenueProperties.get(propertyName);
                        int levelProperty = Integer.parseInt(avenueProperty.get(10));
                        if (levelProperty < 5) {
                            levelProperty++;
                        } else {
                            System.out.println("This property is with maximum level - 5");
                        }
                    }
                }
            }
        }
    }

    public static int getInputChoice(List<String> neighborhoodProperties) {
        for (int i = 0; i < neighborhoodProperties.size(); i++) {
            System.out.println((i + 1) + ". " + neighborhoodProperties);
        }

        while (true) {
            int choice = getInputChoiceNumber();
            if (choice < 1 || choice > 3) {
                return choice;
            } else {
                System.out.println("The entered character is not in range 1 - 3!");
            }
        }
    }

    private static int getInputChoiceNumber() {
        boolean isEnteredChoiceInRange = false;

        while (!isEnteredChoiceInRange) {
            if (scanner.hasNextInt()){
                isEnteredChoiceInRange = true;
            }
            else {
                scanner.next();
                System.out.println("The entered character is not a number!");
            }
        }

        int playersCount = scanner.nextInt();
        scanner.nextLine();

        return playersCount;
    }

    public static void playerTurn(Map.Entry<Integer, List<String>> player) {
        printTurnMessage(getPlayerName(player));
        playerRollDices(player);
        dealNewPosition(player);
        printPlayersInfo();
    }

    public static void printPlayersInfo() {
        int nameIndex = 0;
        int moneyIndex = 1;
        System.out.print("-".repeat(65));
        System.out.println();
        printPlayerInfo(nameIndex);
        printPlayerInfo(moneyIndex);
        System.out.print("-".repeat(65));
    }

    public static void printPlayerInfo(int rowIndex) {
        System.out.print("|");
        for (Map.Entry<Integer, List<String>> player : players.entrySet()) {
            System.out.printf("%-15s|", player.getValue().get(rowIndex));
        }
        System.out.println();
    }

    public static void dealNewPosition(Map.Entry<Integer, List<String>> player) {
        int playerNewPosition = Integer.parseInt(player.getValue().get(2));
        List<String> tablePosition = gameTable.get(playerNewPosition);
        String positionStatus = tablePosition.get(3);

        if(positionStatus.equals("Game")) {
            gamePositionDeal(player, tablePosition);
        } else if (positionStatus.equals("Free")) {
            String propertyName = tablePosition.get(2);
            String propertyPrice = tablePosition.get(5);
            printPlayer(player);
            System.out.println("Do you want to buy " + propertyName + " for $" + propertyPrice + ".");
            if(getAnswerInput()) {
                getFreePositionDeal(player, tablePosition);
            }
        } else {
            Map.Entry<Integer, List<String>> playerInThePosition = getPlayerInPosition(positionStatus);
            if (!getPlayerName(player).equals(getPlayerName(playerInThePosition))) {
                String propertyName = tablePosition.get(2);
                String propertyType = tablePosition.get(4);
                if (propertyType.equals("Rail")) {
                    Map.Entry<String, List<String>> property = getRailProperty(propertyType, propertyName);
                    getRailRentDeal(player, playerInThePosition);
                } else if (propertyType.equals("Comp")) {
                    Map.Entry<String, List<String>> property = getCompProperty(propertyType, propertyName);
                    getCompRentDeal(player, playerInThePosition);
                } else {
                    Map.Entry<String, List<String>> property = getAvenueProperty(propertyName);
                    getRentDeal(player, playerInThePosition, property);
                }
            } else {
                System.out.println("This field is yours!");
            }

        }
    }

    public static void getCompRentDeal(Map.Entry<Integer, List<String>> playerTenant,
                                       Map.Entry<Integer, List<String>> playerLandlord) {
        int playerTenantMoney = getPlayerMoney(playerTenant);
        int rentalPrice = getCompRentalPrice(playerTenant, playerLandlord);
        if(playerTenantMoney < rentalPrice) {
            setPlayerInactive(playerTenant);
        } else {
            getDeal(playerTenant, playerLandlord, playerTenantMoney, rentalPrice);
        }
    }

    public static void getRailRentDeal(Map.Entry<Integer, List<String>> playerTenant,
                                       Map.Entry<Integer, List<String>> playerLandlord) {
        String[] playerLandlordRailInfo = playerLandlord.getValue().get(5).split(":");
        int playerLandlordRailCount = Integer.parseInt(playerLandlordRailInfo[1]);

        int playerTenantMoney = getPlayerMoney(playerTenant);
        int rentalPrice = getRailRentalPrice(playerLandlordRailCount);
        if(playerTenantMoney < rentalPrice) {
            setPlayerInactive(playerTenant);
        } else {
            getDeal(playerTenant, playerLandlord, playerTenantMoney, rentalPrice);
        }
    }

    private static void getDeal(Map.Entry<Integer, List<String>> playerTenant, Map.Entry<Integer, List<String>> playerLandlord, int playerTenantMoney, int rentalPrice) {
        playerTenantMoney -= rentalPrice;
        setPlayerMoney(playerTenant, playerTenantMoney);
        int playerLandLordMoney = getPlayerMoney(playerLandlord);
        playerLandLordMoney += rentalPrice;
        setPlayerMoney(playerLandlord, playerLandLordMoney);
        rentPaidMessage(playerTenant, playerLandlord, rentalPrice);
    }

    private static void rentPaidMessage(Map.Entry<Integer, List<String>> playerTenant, Map.Entry<Integer, List<String>> playerLandlord, int rentalPrice) {
        System.out.println(getPlayerName(playerTenant) + " paid $" + rentalPrice + " rent to " + getPlayerName(playerLandlord));
    }

    public static void getRentDeal(Map.Entry<Integer, List<String>> playerTenant,
                                   Map.Entry<Integer, List<String>> playerLandlord,
                                   Map.Entry<String, List<String>> property) {
        int playerTenantMoney = getPlayerMoney(playerTenant);
        int rentalPrice = getPropertyRentalPrice(property.getValue());
        if(playerTenantMoney < rentalPrice) {
            setPlayerInactive(playerTenant);
        } else {
            getDeal(playerTenant, playerLandlord, playerTenantMoney, rentalPrice);
        }
    }


    public static int getCompRentalPrice(Map.Entry<Integer, List<String>> playerTenant,
                                         Map.Entry<Integer, List<String>> playerLandlord) {
        int rentPrice;
        int resultOfLastDices = Integer.parseInt(playerTenant.getValue().get(14));
        String[] playerLandlordCompInfo = playerLandlord.getValue().get(4).split(":");
        int playerLandlordCompCount = Integer.parseInt(playerLandlordCompInfo[1]);

        if(playerLandlordCompCount == 1) {
            rentPrice = resultOfLastDices * 4;
        } else {
            rentPrice = resultOfLastDices * 10;
        }

        return rentPrice;
    }

    public static int getRailRentalPrice (int railPropertiesCount) {
        switch (railPropertiesCount) {
            case 1 -> {
                return 25;
            }
            case 2 -> {
                return 50;
            }
            case 3 -> {
                return 100;
            }
            case 4 -> {
                return 200;
            }
        }

        return 0;
    }

    public static Map.Entry<String, List<String>> getRailProperty(String propertyType, String propertyName) {
        Map.Entry<String, List<String>> property = null;
        for (Map.Entry<String, List<String>> railProperty : railroadProperties.entrySet()) {
            if(railProperty.getValue().get(0).equals(propertyName)) {
                property = railProperty;
            }
        }

        return property;
    }

    public static Map.Entry<String, List<String>> getCompProperty(String propertyType, String propertyName) {
        Map.Entry<String, List<String>> property = null;
        for (Map.Entry<String, List<String>> compProperty : companyProperties.entrySet()) {
            if(compProperty.getValue().get(0).equals(propertyName)) {
                property = compProperty;
            }
        }

        return property;
    }

    public static void gamePositionDeal (Map.Entry<Integer, List<String>> player, List<String> tablePosition) {
        String tableGamePosition = tablePosition.get(4);
        switch (tableGamePosition) {
            case "Chance" -> chancePositionDeal(player);
            case "Jail" -> playerInJail(player);
            case "Judje" -> goToJail(player);
            case "Parking" -> goToParking(player);
        }
    }

    public static void goToParking(Map.Entry<Integer, List<String>> player) {
        System.out.println(getPlayerName(player) + ", you went to parking.");
    }

    public static void goToJail(Map.Entry<Integer, List<String>> player) {
        int jailPosition = 11;
        int playerPosition = getPlayerPosition(player);
        setGameTable(player, playerPosition, jailPosition);
        System.out.println();
    }

    public static int getPlayerPosition(Map.Entry<Integer, List<String>> player) {
        return Integer.parseInt(player.getValue().get(2));
    }

    public static void playerInJail(Map.Entry<Integer, List<String>> player) {
        player.getValue().set(15, "In jail");
        System.out.println(getPlayerName(player) + ", you went to jail.");
    }

    private static String getPlayerName(Map.Entry<Integer, List<String>> player) {
        return player.getValue().get(0);
    }

    public static void chancePositionDeal(Map.Entry<Integer, List<String>> player) {
        int randomChanceCart = getCard();
        System.out.println("You hit a chance field. " + chancesCarts.get(randomChanceCart));
        switch (randomChanceCart) {
            case 0 -> goToFirstColoredField(player, "Purple");
            case 1, 3, 5, 7, 9, 11, 13, 15 -> goChanceDeal(player, randomChanceCart);
            case 2 -> goToFirstColoredField(player, "Cyan");
            case 4 -> goToFirstColoredField(player, "IntenseRed");
            case 6 -> goToFirstColoredField(player, "Gray");
            case 8 -> goToFirstColoredField(player, "Red");
            case 10 -> goToFirstColoredField(player, "Yellow");
            case 12 -> goToFirstColoredField(player, "Green");
            case 14 -> goToFirstColoredField(player, "Blue");
            default -> throw new IllegalStateException("Unexpected value: " + randomChanceCart);
        }
    }

    public static void goChanceDeal(Map.Entry<Integer, List<String>> player, int chanceCardNumber) {
        String[] chance = chancesCarts.get(chanceCardNumber).split(";");
        int chanceMoney = Integer.parseInt(chance[1]);
        int playerMoney = Integer.parseInt(player.getValue().get(1));
        playerMoney += chanceMoney;
        if (playerMoney < 0) {
            System.out.println("You are bankrupt!");
            setPlayerInactive(player);
        }
        setPlayerMoney(player, playerMoney);
    }

    public static void goToFirstColoredField(Map.Entry<Integer, List<String>> player, String fieldColor) {
        for (Map.Entry<Integer, List<String>> field : gameTable.entrySet()) {
            if (field.getValue().get(4).equals(fieldColor)) {
                int playerPosition = Integer.parseInt(player.getValue().get(2));
                int playerNewPosition = field.getKey();
                player.getValue().set(2, String.valueOf(playerNewPosition));
                setGameTable(player, playerPosition, playerNewPosition);
                String fieldOwner = field.getValue().get(3);
                String playerName = player.getValue().get(0).substring(0, 4);
                if (fieldOwner.equals("Free")) {
                    int propertyPrice = 0;
                    int playerBankAmount = Integer.parseInt(player.getValue().get(1));
                    dealProperty(player, field.getValue(), propertyPrice, playerBankAmount);
                    return;
                } else if (fieldOwner.equals(playerName)) {
                    return;
                } else {
                    Map.Entry<Integer, List<String>> playerLandlord = getFieldOwner(fieldOwner);
                    Map.Entry<String, List<String>> property = getAvenueProperty(field.getValue().get(2));
                    assert property != null;
                    getRentDeal(player, playerLandlord, property);
                    return;
                }
            }
        }
    }

    public static Map.Entry<Integer, List<String>> getFieldOwner (String fieldOwnerName) {
        Map.Entry<Integer, List<String>> fieldOwner = null;
        for (Map.Entry<Integer, List<String>> player : players.entrySet()) {
            String playerName = player.getValue().get(0).substring(0, 4);
            if(playerName.equals(fieldOwnerName)) {
                fieldOwner = player;
            }
        }

        return fieldOwner;
    }

    public static int getCard() {
        Random random = new Random();
        int maximumCartNumber = 16;
        int minimumCartNumber = 1;
        int cartNumber = random.nextInt((maximumCartNumber - minimumCartNumber) + 1) + minimumCartNumber;

        return cartNumber - 1;
    }

    public static Map.Entry<String, List<String>> getAvenueProperty (String propertyName) {
        for (Map.Entry<String, List<String>> property : avenueProperties.entrySet()) {
            if(property.getKey().equals(propertyName)) {
                return property;
            }
        }

        return null;
    }


    public static void setPlayerMoney(Map.Entry<Integer, List<String>> player, int money) {
        player.getValue().set(1, String.valueOf(money));
    }

    public static void setPlayerInactive(Map.Entry<Integer, List<String>> player) {
        System.out.println("You are bankrupt!");
        player.getValue().set(3, "Inactive");
        playersCount--;
    }

    public static int getPropertyRentalPrice(List<String> property) {
        int propertyLevel = getPropertyLevel(property);
        switch (propertyLevel) {
            case 1 : return getLevelRentalPrice(property, 5);
            case 2 : return getLevelRentalPrice(property, 6);
            case 3 : return getLevelRentalPrice(property, 7);
            case 4 : return getLevelRentalPrice(property, 8);
            case 5 : return getLevelRentalPrice(property, 9);
            default: return Integer.parseInt(property.get(3));
        }
    }

    public static int getLevelRentalPrice(List<String> property, int position) {
        String[] levelPriceDescription = property.get(position).split(":");
        return Integer.parseInt(levelPriceDescription[1]);
    }

    public static int getPropertyLevel(List<String> property) {
        return Integer.parseInt(property.get(10));
    }

    public static Map.Entry<Integer, List<String>> getPlayerInPosition(String playerName) {
        for (Map.Entry<Integer, List<String>> player : players.entrySet()) {
            if(getPlayerName(player).equals(playerName)) {
                return player;
            }
        }

        return null;
    }

    private static boolean getAnswerInput() {
        String playerChoice;
        System.out.println("Press Y (for YES) or N (for NO)");
        while (true) {
            playerChoice = scanner.nextLine();
            if (playerChoice.equals("Y")) {
                return true;
            } else if (playerChoice.equals("N")) {
                return false;
            } else {
                System.out.println("Incorrect input! Please enter 'Y' or 'N'!");
            }
        }
    }

    public static void printPlayer(Map.Entry<Integer, List<String>> player) {
        System.out.println("Your account sum: " + getPlayerMoney(player));
    }

    public static int getPlayerMoney(Map.Entry<Integer, List<String>> player) {
        return Integer.parseInt(player.getValue().get(1));
    }

    public static void getFreePositionDeal(Map.Entry<Integer, List<String>> player, List<String> tablePosition) {
        int propertyPrice = Integer.parseInt(tablePosition.get(5));
        int playerBankAmount = getPlayerMoney(player);
        if(playerBankAmount < propertyPrice) {
            System.out.println("You do not have enough money for buy this property!");
        } else {
            dealProperty(player, tablePosition, propertyPrice, playerBankAmount);
        }
    }

    private static void dealProperty(Map.Entry<Integer, List<String>> player, List<String> tablePosition, int propertyPrice, int playerBankAmount) {
        playerBankAmount -= propertyPrice;
        player.getValue().set(1, String.valueOf(playerBankAmount));
        String playerName = getPlayerName(player);

        if(playerName.length() > 5) {
            playerName = playerName.substring(0, 4);
        }

        tablePosition.set(3, playerName);
        String colorField = tablePosition.get(4);
        int playerColorPosition = getPlayerColorIndex(colorField);
        String[] playerColorInfo = player.getValue().get(playerColorPosition).split(":");
        String color = playerColorInfo[0];
        int coloredPropertyCount = Integer.parseInt(playerColorInfo[1]);
        coloredPropertyCount++;
        String revisedColoredInfo = color + ":" + coloredPropertyCount;
        player.getValue().set(playerColorPosition, revisedColoredInfo);
    }

    public static int getPlayerColorIndex(String colorField) {
        int colorPosition = 0;
        switch (colorField) {
            case "Comp" : colorPosition = 4; break;
            case "Rail" : colorPosition = 5; break;
            case "Purple" : colorPosition = 6; break;
            case "Cyan" : colorPosition = 7; break;
            case "IntenseRed" : colorPosition = 8; break;
            case "Gray" : colorPosition = 9; break;
            case "Red" : colorPosition = 10; break;
            case "Yellow" : colorPosition = 11; break;
            case "Green" : colorPosition = 12; break;
            case "Blue" : colorPosition = 13; break;
        }

        return colorPosition;
    }

    public static void playerRollDices(Map.Entry<Integer, List<String>> player) {
        int[] dices = resultOfRollingDices();
        System.out.println("Your dices are " + dices[0] + ", " + dices[1]);
        boolean isBothDices = (dices[0] == dices[1]);
        boolean isFirstTurn = true;

        int resultOfDices = dices[0] + dices[1];
        setPlayerLastDicesResult(player, resultOfDices);
        int playerPosition = Integer.parseInt(player.getValue().get(2));
        int playerNewPosition = playerPosition + resultOfDices;
        if(playerNewPosition > 40) {
            playerNewPosition -= 40;
            int playerMoney = getPlayerMoney(player);
            playerMoney += 200;
            setPlayerMoney(player, playerMoney);
            System.out.println(getPlayerName(player) + ", you are collect $200.");
        }
        player.getValue().set(2, String.valueOf(playerNewPosition));
        setGameTable(player, playerPosition, playerNewPosition);
    }

    public static void setPlayerLastDicesResult(Map.Entry<Integer, List<String>> player, int resultOfDices) {
        player.getValue().set(14, String.valueOf(resultOfDices));
    }

    private static void setGameTable(Map.Entry<Integer, List<String>> player, int playerPosition, int playerNewPosition) {
        List<String> playerOldGameField = gameTable.get(playerPosition);
        List<String> playerNewGameField = gameTable.get(playerNewPosition);

        List<String> oldFieldUsages = Arrays.asList(playerOldGameField.get(3).split(" "));
        List<String> newFieldUsages = Arrays.asList(playerNewGameField.get(3).split(" "));

        if (oldFieldUsages.size() > 1) {
            oldFieldUsages.remove(oldFieldUsages.size() - 1);
            StringBuilder oldFieldRevizeUsages = new StringBuilder();
            for (String user : oldFieldUsages) {
                oldFieldRevizeUsages.append(user).append(" ");
            }
            gameTable.get(playerPosition).set(6, oldFieldRevizeUsages.toString());
        } else {
            gameTable.get(playerPosition).set(6, "Free");
        }

        String currentUserName = player.getValue().get(0).substring(0,2);
        if(gameTable.get(playerNewPosition).get(6).equals("Free")) {
            gameTable.get(playerNewPosition).set(6, currentUserName);
        } else {
            StringBuilder newFieldRevizeUsages = new StringBuilder();
            newFieldRevizeUsages.append(gameTable.get(playerNewPosition).get(6)).append(" ").append(currentUserName);
            gameTable.get(playerNewPosition).set(6, newFieldRevizeUsages.toString());
        }
    }

    public static int[] resultOfRollingDices() {
        Random random = new Random();
        int maximumDiceNumber = 6;
        int minimumDiceNumber = 1;
        int[] dices = new int[2];
        dices[0] = random.nextInt((maximumDiceNumber - minimumDiceNumber) + 1) + minimumDiceNumber;
        dices[1] = random.nextInt((maximumDiceNumber - minimumDiceNumber) + 1) + minimumDiceNumber;

        return dices;
    }

    public static void printTurnMessage(String playerName) {
        System.out.print(playerName + ", your turn. Press Enter to roll the dice!");
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println();
    }

    public static void initializePlayers() {
        while (true) {
            playersCount = getPlayersCount();

            if(playersCount < 2 || playersCount > 4) {
                System.out.print("The entered number does not in the range! Plase enter number of players again [2 - 4]: ");
            } else {
                createPlayers(playersCount);
                break;
            }
        }
    }

    public static int getPlayersCount() {
        boolean isPlayersCountinRange = false;

        while (!isPlayersCountinRange) {
            System.out.print("Please enter number of players [2 - 4]: ");
            if (scanner.hasNextInt()){
                isPlayersCountinRange = true;
            }
            else {
                scanner.next();
                System.out.println("The entered character is not a number!");
            }
        }

        int playersCount = scanner.nextInt();
        scanner.nextLine();

        return playersCount;
    }

    public static void createPlayers(int numberOfPlayers) {
        players = new HashMap<>();
        for (int i = 1; i <= numberOfPlayers; i++) {
            List<String> playerInfo = createNewPlayer(i);
            players.put(i, playerInfo);
        }
    }

    public static String createPlayerName(int playerNumber) {
        System.out.print("Please enter "+ playerNumber + (playerNumber == 1 ? "'st" : (playerNumber == 2 ? "'nd" :
                (playerNumber == 3 ? "'rd" : (playerNumber == 4 ? "'th" : "")))) + " player: ");
        String playerName = scanner.nextLine();

        return playerName;
    }

    public static List<String> createNewPlayer(int playerNumber) {
        List<String> playerInfo = new ArrayList<>();
        playerInfo.add(createPlayerName(playerNumber));
        playerInfo.add("2000");
        playerInfo.add("1");
        playerInfo.add("active");
        playerInfo.add("Comp:0");
        playerInfo.add("Rail:0");
        playerInfo.add("Purple:0");
        playerInfo.add("Cyan:0");
        playerInfo.add("IntenseRed:0");
        playerInfo.add("Gray:0");
        playerInfo.add("Red:0");
        playerInfo.add("Yellow:0");
        playerInfo.add("Green:0");
        playerInfo.add("Blue:0");
        playerInfo.add("0");
        playerInfo.add("Not in jail");

        return playerInfo;
    }

    public static void printTable() {
        int firstRowStartIndex = 1;
        int firstRowEndIndex = 11;
        int nameIndex = 1;
        int descriptionIndex = 2;
        int userIndex = 6;
        int middleRowStartIndex = 1;
        int middleRowEndIndex = 9;
        int lastRowStartIndex = 31;
        int lastRowEndIndex = 21;

        System.out.println("-".repeat(132));
        printBorderRow(firstRowStartIndex, firstRowEndIndex, nameIndex, descriptionIndex, userIndex, true);
        System.out.println("-".repeat(132));
        printMiddleRows(nameIndex, descriptionIndex, userIndex, middleRowStartIndex, middleRowEndIndex);
        printBorderRow(lastRowStartIndex, lastRowEndIndex, nameIndex, descriptionIndex, userIndex, false);
        System.out.println("-".repeat(132));
    }

    private static void printBorderRow(int firstRowStartIndex, int firstRowEndIndex, int nameIndex, int descriptionIndex, int userIndex, boolean isIncreasingIndex) {
        printRow(firstRowStartIndex, firstRowEndIndex, nameIndex, true, isIncreasingIndex);
        printRow(firstRowStartIndex, firstRowEndIndex, descriptionIndex, true, isIncreasingIndex);
        printRow(firstRowStartIndex, firstRowEndIndex, userIndex, true, isIncreasingIndex);
    }

    public static void printRow(int startIndex, int endIndex, int rowIndex, boolean isNewLine, boolean isIncreaseIndex) {
        if (isIncreaseIndex) {
            for (int i = startIndex; i <= endIndex; i++) {
                List<String> currentTale = gameTable.get(i);
                System.out.print(getCurrentTaleMessage(currentTale, rowIndex));
            }
        } else {
            for (int i = startIndex; i >= endIndex; i--) {
                List<String> currentTale = gameTable.get(i);
                System.out.print(getCurrentTaleMessage(currentTale, rowIndex));
            }
        }
        if (isNewLine) {
            System.out.println();
        }
    }

    public static String getCurrentTaleMessage(List<String> currentTale, int rowIndex) {
        String returnedMessage = String.format("%-11s|", currentTale.get(rowIndex));

        if(rowIndex == 6) {
            if(currentTale.get(rowIndex).equals("Free") || currentTale.get(rowIndex).equals("Game")) {
                returnedMessage = String.format("%-11s|", "");
            } else {
                returnedMessage = String.format("%-11s|", currentTale.get(rowIndex));
            }
        }

        if(rowIndex == 1) {
            String dollarSign = "$";
            switch (currentTale.get(4)) {
                case "Purple" : returnedMessage = String.format("%s%-6s%s %s%-3s|", ANSI_PURPLE, currentTale.get(3), ANSI_RESET, dollarSign, currentTale.get(5)); break;
                case "Cyan" : returnedMessage = String.format("%s%-6s%s %s%-3s|", ANSI_CYAN, currentTale.get(3), ANSI_RESET, dollarSign, currentTale.get(5)); break;
                case "IntenseRed" : returnedMessage = String.format("%s%-6s%s %s%-3s|", ANSI_INTENSE_RED, currentTale.get(3), ANSI_RESET, dollarSign, currentTale.get(5)); break;
                case "Gray" : returnedMessage = String.format("%s%-6s%s %s%-3s|", ANSI_INTENSE_GRAY, currentTale.get(3), ANSI_RESET, dollarSign, currentTale.get(5)); break;
                case "Red" : returnedMessage = String.format("%s%-6s%s %s%-3s|", ANSI_RED, currentTale.get(3), ANSI_RESET, dollarSign, currentTale.get(5)); break;
                case "Yellow" : returnedMessage = String.format("%s%-6s%s %s%-3s|", ANSI_YELLOW, currentTale.get(3), ANSI_RESET, dollarSign, currentTale.get(5)); break;
                case "Green" : returnedMessage = String.format("%s%-6s%s %s%-3s|", ANSI_GREEN, currentTale.get(3), ANSI_RESET, dollarSign, currentTale.get(5)); break;
                case "Blue" : returnedMessage = String.format("%s%-6s%s %s%-3s|", ANSI_BLUE, currentTale.get(3), ANSI_RESET, dollarSign, currentTale.get(5)); break;
                case "Rail" : returnedMessage = String.format("%s%-6s%s %s%-3s|", ANSI_INTENSE_GREEN, currentTale.get(3), ANSI_RESET, dollarSign, currentTale.get(5)); break;
                case "Go" : returnedMessage = String.format("%-11s|", currentTale.get(1)); break;
            }
        }

        return returnedMessage;
    }

    private static void printMiddleRows(int nameIndex, int descriptionIndex, int userIndex, int middleRowStartIndex, int middleRowEndIndex) {
        int leftTalePosition = 40;
        int rightTalePosition = 12;
        for (int i = middleRowStartIndex; i <= middleRowEndIndex; i++) {
            printMiddleRow(leftTalePosition, rightTalePosition, nameIndex);
            printMiddleRow(leftTalePosition, rightTalePosition, descriptionIndex);
            printMiddleRow(leftTalePosition, rightTalePosition, userIndex);
            leftTalePosition--;
            rightTalePosition++;
            System.out.println("-".repeat(132));
        }
    }

    public static void printMiddleRow(int leftTalePosition, int rightTalePosition, int rowIndex) {
        printMiddleTalePart(leftTalePosition, rowIndex, false);
        printEmptySpaces();
        printMiddleTalePart(rightTalePosition, rowIndex, true);
    }

    public static void printMiddleTalePart(int talePosition, int rowIndex, boolean isNewLine) {
        printRow(talePosition, talePosition, rowIndex, false, false);
        if (isNewLine) {
            System.out.println();
        }
    }

    public static void printEmptySpaces() {
        System.out.print((" ").repeat(107) + ("|"));
    }

    public static void fillGameTable() {
        gameTable = new HashMap<>();
        avenueProperties = new HashMap<>();
        railroadProperties = new HashMap<>();
        companyProperties = new HashMap<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("data.txt"))) {
            int gameFieldsCount = 40;
            for (int i = 1; i <= gameFieldsCount; i++) {
                List<String> fieldInfo = Arrays.asList(bufferedReader.readLine().split(";"));
                gameTable.put(i, fieldInfo);
                if(fieldInfo.get(3).equals("Free")) {
                    enterProperty(fieldInfo);
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fillChancesCarts() {
        chancesCarts = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("chance-carts.txt"))){
            int chanceCartsCount = 16;
            for (int i = 0; i < chanceCartsCount; i++) {
                String chanceCart = bufferedReader.readLine();
                chancesCarts.add(i, chanceCart);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void enterProperty(List<String> fieldInfo) {
        String propertyTablePosition = fieldInfo.get(0);
        String propertyName = fieldInfo.get(2);
        String[] propertyColorAndPrice = getPropertyColorAndPrice(fieldInfo.get(1));
        String propertyColor = propertyColorAndPrice[0];
        int propertyPrice = Integer.parseInt(propertyColorAndPrice[2]);
        String propertyStatus = fieldInfo.get(3);
        List<String> propertyInfo = new ArrayList<>(Arrays.asList(propertyTablePosition, propertyName, propertyColor, String.valueOf(propertyPrice), propertyStatus));
        if(propertyColor.equals("Rail")) {
            railroadProperties.put(propertyName, propertyInfo);
        } else if (propertyColor.equals("Comp")) {
            companyProperties.put(propertyName, propertyInfo);
        } else {
            propertyInfo.addAll(getPropertyRentInfo(propertyPrice));
            propertyInfo.add("1");
            avenueProperties.put(propertyName, propertyInfo);
        }
    }

    public static List<String> getPropertyRentInfo(int propertyPrice) {
        List<String> propertyRentInfo = new ArrayList<>();
        int propertyLevels = 5;
        for (int i = 1; i <= propertyLevels; i++) {
            switch (i) {
                case 1 -> propertyRentInfo.add("Level 1:" + (propertyPrice / 100 * 50));
                case 2 -> propertyRentInfo.add("Level 2:" + propertyPrice);
                case 3 -> propertyRentInfo.add("Level 3:" + (propertyPrice / 100 * 200));
                case 4 -> propertyRentInfo.add("Level 4:" + (propertyPrice / 100 * 350));
                case 5 -> propertyRentInfo.add("Level 5:" + (propertyPrice / 100 * 500));
            }
        }

        return propertyRentInfo;
    }

    public static String[] getPropertyColorAndPrice(String property) {
        String[] propertyInfo = property.split("[ $]");
        return propertyInfo;
    }
}