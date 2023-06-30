import java.util.*;

public class Main {
    public static Map<Integer, List<String>> gameTable;
    public static List<String> purpleAvenues;

    public static void main(String[] args) {
        fillTable();
        printTable();
    }

    public static void printTable() {
        int firstRowStartIndex = 1;
        int firstRowEndIndex = 11;
        int nameIndex = 0;
        int descriptionIndex = 1;
        int middleRowStartIndex = 1;
        int middleRowEndIndex = 9;
        int lastRowStartIndex = 31;
        int lastRowEndIndex = 21;

        System.out.println("-".repeat(132));
        printBorderRow(firstRowStartIndex, firstRowEndIndex,  nameIndex, descriptionIndex, true);
        System.out.println("-".repeat(132));
        printMiddleRows(nameIndex, descriptionIndex, middleRowStartIndex, middleRowEndIndex);
        printBorderRow(lastRowStartIndex, lastRowEndIndex, nameIndex, descriptionIndex, false);
        System.out.println("-".repeat(132));
    }

    private static void printBorderRow(int firstRowStartIndex, int firstRowEndIndex, int nameIndex, int descriptionIndex, boolean isIncreasingIndex) {
        printRow(firstRowStartIndex, firstRowEndIndex, nameIndex, true, isIncreasingIndex);
        printRow(firstRowStartIndex, firstRowEndIndex, descriptionIndex, true, isIncreasingIndex);
    }

    public static void printRow(int startIndex, int endIndex, int rowIndex, boolean isNewLine, boolean isIncreaseIndex) {
        if(isIncreaseIndex) {
            for (int i = startIndex; i <= endIndex; i++) {
                List<String> currentTale = gameTable.get(i);
                System.out.printf("%-11s|", currentTale.get(rowIndex));
            }
        } else {
            for (int i = startIndex; i >= endIndex; i--) {
                List<String> currentTale = gameTable.get(i);
                System.out.printf("%-11s|", currentTale.get(rowIndex));
            }
        }
        if(isNewLine) {
            System.out.println();
        }
    }

    private static void printMiddleRows(int nameIndex, int descriptionIndex, int middleRowStartIndex, int middleRowEndIndex) {
        int leftTalePosition = 40;
        int rightTalePosition = 12;
        for (int i = middleRowStartIndex; i <= middleRowEndIndex; i++) {
            printMiddleRow(leftTalePosition, rightTalePosition, nameIndex);
            printMiddleRow(leftTalePosition, rightTalePosition, descriptionIndex);
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
        if(isNewLine) {
            System.out.println();
        }
    }

    public static void printEmptySpaces() {
        System.out.print((" ").repeat(107) + ("|"));
    }



    public static void fillTable() {
        gameTable = new HashMap<>();
        gameTable.put(1, new ArrayList<>() {
            {
                add("Bank Go");
                add("Collect$200");
            }
        });

        gameTable.put(2, new ArrayList<>() {
            {
                add("Purple $60");
                add("Ruse Av");
            }
        });

        gameTable.put(3, new ArrayList<>() {
            {
                add("Community");
                add("Chest");
            }
        });

        gameTable.put(4, new ArrayList<>() {
            {
                add("Purple $60");
                add("Drovska Av");
            }
        });

        gameTable.put(5, new ArrayList<>() {
            {
                add("Income Tax");
                add("Pay $200");
            }
        });

        gameTable.put(6, new ArrayList<>() {
            {
                add("Railroad");
                add("Main $200");
            }
        });

        gameTable.put(7, new ArrayList<>() {
            {
                add("Violet $100");
                add("Peristar Av");
            }
        });

        gameTable.put(8, new ArrayList<>() {
            {
                add("Chance");
                add("?");
            }
        });

        gameTable.put(9, new ArrayList<>() {
            {
                add("Violet $100");
                add("Mesta Av");
            }
        });

        gameTable.put(10, new ArrayList<>() {
            {
                add("Violet $120");
                add("Veles Av");
            }
        });

        gameTable.put(11, new ArrayList<>() {
            {
                add("Jail");
                add("Visiting");
            }
        });

        gameTable.put(12, new ArrayList<>() {
            {
                add("Pink $140");
                add("Vitosha Av");
            }
        });

        gameTable.put(13, new ArrayList<>() {
            {
                add("Comp $150");
                add("Electric");
            }
        });

        gameTable.put(14, new ArrayList<>() {
            {
                add("Pink $140");
                add("Maritsa Av");
            }
        });

        gameTable.put(15, new ArrayList<>() {
            {
                add("Pink $160");
                add("Lom Av");
            }
        });

        gameTable.put(16, new ArrayList<>() {
            {
                add("Railroad");
                add("Orel $200");
            }
        });

        gameTable.put(17, new ArrayList<>() {
            {
                add("Orange $180");
                add("Iskar Av");
            }
        });

        gameTable.put(18, new ArrayList<>() {
            {
                add("Community");
                add("Chest");
            }
        });

        gameTable.put(19, new ArrayList<>() {
            {
                add("Orange $180");
                add("Kamchiya Av");
            }
        });

        gameTable.put(20, new ArrayList<>() {
            {
                add("Orange $200");
                add("Cherna Av");
            }
        });

        gameTable.put(21, new ArrayList<>() {
            {
                add("Parking");
                add("Free");
            }
        });

        gameTable.put(22, new ArrayList<>() {
            {
                add("Red $220");
                add("B.Lom Av");
            }
        });

        gameTable.put(23, new ArrayList<>() {
            {
                add("Chance");
                add("?");
            }
        });

        gameTable.put(24, new ArrayList<>() {
            {
                add("Red $220");
                add("Dunav Av");
            }
        });

        gameTable.put(25, new ArrayList<>() {
            {
                add("Red $240");
                add("Osam Av");
            }
        });

        gameTable.put(26, new ArrayList<>() {
            {
                add("Railroad");
                add("Entral $200");
            }
        });

        gameTable.put(27, new ArrayList<>() {
            {
                add("Yellow $260");
                add("Yantra Av");
            }
        });

        gameTable.put(28, new ArrayList<>() {
            {
                add("Yellow $260");
                add("Mesta Av");
            }
        });

        gameTable.put(29, new ArrayList<>() {
            {
                add("Comp $150");
                add("Water");
            }
        });

        gameTable.put(30, new ArrayList<>() {
            {
                add("Yellow $280");
                add("Vit Av");
            }
        });

        gameTable.put(31, new ArrayList<>() {
            {
                add("Judje");
                add("Go To Jail");
            }
        });

        gameTable.put(32, new ArrayList<>() {
            {
                add("Green $300");
                add("Ticha Av");
            }
        });

        gameTable.put(33, new ArrayList<>() {
            {
                add("Green $300");
                add("Arda Av");
            }
        });

        gameTable.put(34, new ArrayList<>() {
            {
                add("Community");
                add("Chest");
            }
        });

        gameTable.put(35, new ArrayList<>() {
            {
                add("Green $320");
                add("Varna Av");
            }
        });

        gameTable.put(36, new ArrayList<>() {
            {
                add("Railroad");
                add("Park $200");
            }
        });

        gameTable.put(37, new ArrayList<>() {
            {
                add("Chance");
                add("?");
            }
        });

        gameTable.put(38, new ArrayList<>() {
            {
                add("Blue $350");
                add("Rila Av");
            }
        });

        gameTable.put(39, new ArrayList<>() {
            {
                add("Lux Task");
                add("Pay: $75");
            }
        });

        gameTable.put(40, new ArrayList<>() {
            {
                add("Blue $400");
                add("Pirin Av");
            }
        });
    }
}