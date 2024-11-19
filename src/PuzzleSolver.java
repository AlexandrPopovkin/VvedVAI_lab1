import java.util.*;

public class PuzzleSolver {

    // Целевое состояние головоломки
    private static final int[][] GOAL_STATE = {
            {0, 1, 2},
            {3, 4, 5},
            {6, 7, 8}
    };

    // Узел поиска
    private static class Node {
        int[][] state;        // Текущее состояние
        Node parent;          // Родительский узел
        String action;        // Действие, которое привело к этому узлу
        int pathCost;         // Стоимость пути (количество шагов)
        int depth;            // Глубина узла

        Node(int[][] state, Node parent, String action, int pathCost, int depth) {
            this.state = state;
            this.parent = parent;
            this.action = action;
            this.pathCost = pathCost;
            this.depth = depth;
        }
    }

    // Проверка, является ли текущее состояние целевым
    private boolean isGoal(int[][] state) {
        return Arrays.deepEquals(state, GOAL_STATE);
    }

    // Поиск позиции пустой клетки (0)
    private int[] findZero(int[][] state) {
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[i].length; j++) {
                if (state[i][j] == 0) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    // Проверка, является ли движение допустимым
    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < 3 && col >= 0 && col < 3;
    }

    // Обмен местами двух элементов
    private void swap(int[][] state, int row1, int col1, int row2, int col2) {
        int temp = state[row1][col1];
        state[row1][col1] = state[row2][col2];
        state[row2][col2] = temp;
    }

    // Генерация возможных состояний после выполнения действия
    private List<Node> generateSuccessors(Node node) {
        List<Node> successors = new ArrayList<>();
        int[] zeroPos = findZero(node.state);
        int row = zeroPos[0];
        int col = zeroPos[1];

        // Возможные движения: вверх, вниз, влево, вправо
        String[] actions = {"Вверх", "Вниз", "Влево", "Вправо"};
        int[][] moves = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int i = 0; i < actions.length; i++) {
            int newRow = row + moves[i][0];
            int newCol = col + moves[i][1];
            if (isValidMove(newRow, newCol)) {
                int[][] newState = deepCopy(node.state);
                swap(newState, row, col, newRow, newCol);
                successors.add(new Node(newState, node, actions[i], node.pathCost + 1, node.depth + 1));
            }
        }

        return successors;
    }

    // Глубокое копирование двумерного массива
    private int[][] deepCopy(int[][] state) {
        int[][] copy = new int[state.length][];
        for (int i = 0; i < state.length; i++) {
            copy[i] = state[i].clone();
        }
        return copy;
    }

    // Сериализация состояния для хранения в HashSet
    private String serializeState(int[][] state) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : state) {
            for (int cell : row) {
                sb.append(cell);
            }
        }
        return sb.toString();
    }

    // Вывод состояния на экран
    private void printState(int[][] state) {
        for (int[] row : state) {
            for (int cell : row) {
                System.out.print(cell == 0 ? "_" : cell);
                System.out.print(" ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // Метод решения головоломки с помощью поиска в глубину (DFS)
    public void solveByDFS(int[][] initialState) {
        Set<String> visitedStates = new HashSet<>();
        if (isGoal(initialState)) {
            System.out.println("Целевое состояние уже достигнуто.");
            return;
        }

        Node root = new Node(initialState, null, null, 0, 0);
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        visitedStates.add(serializeState(initialState));

        int nodesExpanded = 0;

        Scanner scanner = new Scanner(System.in);

        while (!stack.isEmpty()) {
            nodesExpanded++;
            Node current = stack.pop();

            System.out.println("Шаг: " + nodesExpanded);
            printState(current.state);

            if (isGoal(current.state)) {
                System.out.println("Целевое состояние достигнуто за " + nodesExpanded + " шагов.");
                return;
            }

            List<Node> successors = generateSuccessors(current);
            for (Node successor : successors) {
                String stateRepresentation = serializeState(successor.state);

                if (!visitedStates.contains(stateRepresentation)) {
                    stack.push(successor);
                    visitedStates.add(stateRepresentation);
                }
            }

            // Ожидание нажатия клавиши Enter
            //System.out.println("Нажмите Enter для продолжения...");
            //scanner.nextLine();
        }

        System.out.println("Решение не найдено.");
    }

    // Метод решения головоломки с помощью поиска по стоимости (A*)
    public void solveByAStar(int[][] initialState) {
        Set<String> visitedStates = new HashSet<>();
        if (isGoal(initialState)) {
            System.out.println("Целевое состояние уже достигнуто.");
            return;
        }

        Node root = new Node(initialState, null, null, 0, 0);
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(n -> n.pathCost + calculateManhattanDistance(n.state)));
        priorityQueue.add(root);
        visitedStates.add(serializeState(initialState));

        int nodesExpanded = 0;

        Scanner scanner = new Scanner(System.in);

        while (!priorityQueue.isEmpty()) {
            nodesExpanded++;
            Node current = priorityQueue.poll();

            System.out.println("Шаг: " + nodesExpanded);
            printState(current.state);

            if (isGoal(current.state)) {
                System.out.println("Целевое состояние достигнуто за " + nodesExpanded + " шагов.");
                return;
            }

            List<Node> successors = generateSuccessors(current);
            for (Node successor : successors) {
                String stateRepresentation = serializeState(successor.state);

                if (!visitedStates.contains(stateRepresentation)) {
                    priorityQueue.add(successor);
                    visitedStates.add(stateRepresentation);
                }
            }

            // Ожидание нажатия клавиши Enter
            //System.out.println("Нажмите Enter для продолжения...");
            //scanner.nextLine();
        }

        System.out.println("Решение не найдено.");
    }

    // Метод для вычисления эвристического расстояния Манхэттена
    private int calculateManhattanDistance(int[][] state) {
        int distance = 0;

        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[i].length; j++) {
                int value = state[i][j];
                if (value != 0) {
                    int targetRow = (value - 1) / 3;
                    int targetCol = (value - 1) % 3;
                    distance += Math.abs(i - targetRow) + Math.abs(j - targetCol);
                }
            }
        }

        return distance;
    }

    public static void main(String[] args) {
        PuzzleSolver solver = new PuzzleSolver();

        // Начальное состояние головоломки
        int[][] initialState = {
                {6, 2, 8},
                {4, 1, 7},
                {5, 3, 0}
        };

        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите метод решения:");
        System.out.println("1. Поиск в глубину (DFS)");
        System.out.println("2. Поиск по стоимости (A*)");
        System.out.print("Введите 1 или 2: ");
        int choice = scanner.nextInt();

        if (choice == 1) {
            solver.solveByDFS(initialState);
        } else if (choice == 2) {
            solver.solveByAStar(initialState);
        } else {
            System.out.println("Неверный выбор.");
        }
    }
}
