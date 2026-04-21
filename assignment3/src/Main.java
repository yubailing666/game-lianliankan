import java.lang.reflect.*;
import java.util.Scanner;

class Q2LocalTest {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("========== CS109 Assignment 3 - Q2 本地测试工具 ==========");

        // 第一步：函数签名检查
        System.out.println("\n[1/2] 正在进行类结构与方法签名检查...");
        if (!checkStructure()) {
            System.out.println("\n❌ 结构检查未通过，请先修复上述签名或封装错误，再进行功能测试！");
            sc.close();
            return;
        }
        System.out.println("✅ 结构检查通过！\n");

        System.out.println("[2/2] 请选择测试模式：");
        System.out.println("1. 自动测试 (一键运行 Sample 1 与 Sample 2)");
        System.out.println("2. 手动测试 (自行粘贴或输入测试数据)");
        System.out.print("\n请输入 (1-2): ");

        int choice = sc.hasNextInt() ? sc.nextInt() : 0;

        switch (choice) {
            case 1:
                runAutoTests();
                break;
            case 2:
                runManualTest(sc);
                break;
            default:
                System.out.println("❌ 无效选择，程序退出。");
        }
        sc.close();
    }

    /**
     * 利用反射检查题目要求的类结构、属性可见性和方法签名
     */
    public static boolean checkStructure() {
        boolean isAllPassed = true;

        // 1. 检查 Window 类
        try {
            Class<?> winClass = Class.forName("Window");

            // 检查属性是否全为 private
            Field[] fields = winClass.getDeclaredFields();
            if (fields.length == 0) {
                System.out.println("  ❌ Window 类中没有定义任何属性。");
                isAllPassed = false;
            }
            for (Field f : fields) {
                if (!Modifier.isPrivate(f.getModifiers())) {
                    System.out.println("  ❌ Window 类的属性 '" + f.getName() + "' 必须是 private！(请注意良好的封装习惯)");
                    isAllPassed = false;
                }
            }

            // 检查构造器参数是否匹配 (int, int, int, int, int)
            try {
                winClass.getConstructor(int.class, int.class, int.class, int.class, int.class);
            } catch (NoSuchMethodException e) {
                System.out.println("  ❌ Window 类缺少符合要求的构造方法，参数应为 (int r1, int c1, int r2, int c2, int id)。");
                isAllPassed = false;
            }

        } catch (ClassNotFoundException e) {
            System.out.println("  ❌ 找不到 Window 类，请检查类名拼写。");
            return false; // 类都没有，直接短路返回
        }

        // 2. 检查 Desktop 类
        try {
            Class<?> winClass = Class.forName("Window");
            Class<?> deskClass = Class.forName("Desktop");
            Class<?> winArrClass = Array.newInstance(winClass, 0).getClass();

            // 检查 openWindow: public static void openWindow(Window w, int[][] mat)
            try {
                Method openWin = deskClass.getMethod("openWindow", winClass, int[][].class);
                if (!Modifier.isStatic(openWin.getModifiers())) {
                    System.out.println("  ❌ Desktop.openWindow 方法必须使用 static 修饰！");
                    isAllPassed = false;
                }
            } catch (NoSuchMethodException e) {
                System.out.println("  ❌ Desktop 中找不到正确签名的 openWindow 方法。请检查参数是否为 (Window, int[][])。");
                isAllPassed = false;
            }

            // 检查 click: public static void click(int r, int c, Window[] wins, int[][] mat)
            try {
                Method click = deskClass.getMethod("click", int.class, int.class, winArrClass, int[][].class);
                if (!Modifier.isStatic(click.getModifiers())) {
                    System.out.println("  ❌ Desktop.click 方法必须使用 static 修饰！");
                    isAllPassed = false;
                }
            } catch (NoSuchMethodException e) {
                System.out.println("  ❌ Desktop 中找不到正确签名的 click 方法。请严格核对参数顺序：(int, int, Window[], int[][])！");
                isAllPassed = false;
            }

            // 检查 printScreen: public static void printScreen(int[][] mat)
            try {
                Method print = deskClass.getMethod("printScreen", int[][].class);
                if (!Modifier.isStatic(print.getModifiers())) {
                    System.out.println("  ❌ Desktop.printScreen 方法必须使用 static 修饰！");
                    isAllPassed = false;
                }
            } catch (NoSuchMethodException e) {
                System.out.println("  ❌ Desktop 中找不到正确签名的 printScreen 方法。请检查参数是否为 (int[][])。");
                isAllPassed = false;
            }

        } catch (ClassNotFoundException e) {
            System.out.println("  ❌ 找不到 Desktop 类，请检查类名拼写。");
            return false;
        }

        return isAllPassed;
    }

    public static void runAutoTests() {
        System.out.println("\n================ 开始自动测试 ================");
        boolean pass1 = testSample1();
        System.out.println("\n----------------------------------------------");
        boolean pass2 = testSample2();

        System.out.println("\n==============================================");
        if (pass1 && pass2) {
            System.out.println("🎉 恭喜！所有本地 Sample 测试均已通过！");
            System.out.println("💡 提示：这只是基础测试，提交前请确保你考虑了复杂的窗口重叠情况。");
        } else {
            System.out.println("❌ 还有测试未通过，请仔细对比预期输出与实际输出寻找 Bug。");
        }
    }

    public static void runManualTest(Scanner sc) {
        System.out.println("\n[手动测试模式] 请输入测试数据 (格式: N M W K ...):");

        try {
            /*
            * 请按照OJ：http://10.16.6.205/d/CS109_2026S/p/A1010
            * 数据格式进行数据黏贴
5 6 2 1
1 1 3 3 1
2 2 4 5 2
1 1

            以上数据结果应为
0 0 0 0 0 0
0 1 1 1 0 0
0 1 1 1 2 2
0 1 1 1 2 2
0 0 2 2 2 2
            *祝好！
            * */
            if (!sc.hasNextInt()) return;
            int n = sc.nextInt();
            int m = sc.nextInt();
            int w = sc.nextInt();
            int k = sc.nextInt();

            int[][] mat = new int[n][m];
            Window[] windows = new Window[105];

            for (int i = 0; i < w; i++) {
                int r1 = sc.nextInt();
                int c1 = sc.nextInt();
                int r2 = sc.nextInt();
                int c2 = sc.nextInt();
                int id = sc.nextInt();
                Window win = new Window(r1, c1, r2, c2, id);
                windows[i] = win;
                Desktop.openWindow(win, mat);
            }

            for (int i = 0; i < k; i++) {
                int r = sc.nextInt();
                int c = sc.nextInt();
                Desktop.click(r, c, windows, mat);
            }

            System.out.println("\n[你的代码运行结果]:");
            Desktop.printScreen(mat);

        } catch (Exception e) {
            System.out.println("❌ 输入格式错误或运行崩溃: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean testSample1() {
        System.out.println(">>> 正在运行 Sample 1 测试...");
        int[][] mat = new int[5][6];
        Window[] windows = new Window[10];

        Window w1 = new Window(1, 1, 3, 3, 2);
        windows[1] = w1;
        Desktop.openWindow(w1, mat);

        Window w2 = new Window(2, 2, 4, 5, 1);
        windows[2] = w2;
        Desktop.openWindow(w2, mat);

        Desktop.click(1, 1, windows, mat);

        int[][] expected = {
                {0, 0, 0, 0, 0, 0},
                {0, 2, 2, 2, 0, 0},
                {0, 2, 2, 2, 1, 1},
                {0, 2, 2, 2, 1, 1},
                {0, 0, 1, 1, 1, 1}
        };
        return compareMatrix("Sample 1", mat, expected);
    }

    public static boolean testSample2() {
        System.out.println(">>> 正在运行 Sample 2 测试 (全空边界)...");
        int[][] mat = new int[4][4];
        Window[] windows = new Window[10];

        int[][] expected = {{0,0,0,0},{0,0,0,0},{0,0,0,0},{0,0,0,0}};
        return compareMatrix("Sample 2", mat, expected);
    }

    private static boolean compareMatrix(String name, int[][] actual, int[][] expected) {
        boolean isCorrect = true;
        if (actual.length != expected.length || (actual.length > 0 && actual[0].length != expected[0].length)) {
            isCorrect = false;
        } else {
            for (int i = 0; i < expected.length; i++) {
                for (int j = 0; j < expected[i].length; j++) {
                    if (actual[i][j] != expected[i][j]) { isCorrect = false; break; }
                }
                if (!isCorrect) break;
            }
        }

        if (isCorrect) {
            System.out.println("✅ [" + name + "] 通过！");
            return true;
        } else {
            System.out.println("❌ [" + name + "] 失败！");
            System.out.println("[预期输出]:");
            printMat(expected);
            System.out.println("\n[你的实际输出]:");
            Desktop.printScreen(actual);
            return false;
        }
    }

    private static void printMat(int[][] m) {
        for (int[] row : m) {
            for (int j = 0; j < row.length; j++) {
                System.out.print(row[j] + (j == row.length - 1 ? "" : " "));
            }
            System.out.println();
        }
    }
}