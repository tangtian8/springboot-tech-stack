package top.tangtian.designpattern.factory.abstractfactory;

import top.tangtian.designpattern.factory.abstractfactory.app.Application;
import top.tangtian.designpattern.factory.abstractfactory.factory.UIFactory;
import top.tangtian.designpattern.factory.abstractfactory.factory.impl.LinuxUIFactory;
import top.tangtian.designpattern.factory.abstractfactory.factory.impl.MacUIFactory;
import top.tangtian.designpattern.factory.abstractfactory.factory.impl.WindowsUIFactory;

public class AbstractFactoryDemo {
    public static void main(String[] args) {
        // 测试1: Windows平台
        testPlatform(new WindowsUIFactory());

        // 测试2: Mac平台
        testPlatform(new MacUIFactory());

        // 测试3: Linux平台
        testPlatform(new LinuxUIFactory());

        // 测试4: 动态选择平台
        testDynamicPlatform();
    }

    private static void testPlatform(UIFactory factory) {
        System.out.println("\n\n" + "█".repeat(70));
        System.out.println("Testing Platform: " + factory.getThemeName());
        System.out.println("█".repeat(70));

        Application app = new Application(factory);
        app.createUI();
        app.render();
        app.simulateUserInteraction();
    }

    private static void testDynamicPlatform() {
        System.out.println("\n\n" + "█".repeat(70));
        System.out.println("Dynamic Platform Selection");
        System.out.println("█".repeat(70));

        // 根据操作系统动态选择工厂
        String os = detectOS();
        UIFactory factory = getFactoryForOS(os);

        System.out.println("\nDetected OS: " + os);
        System.out.println("Selected Theme: " + factory.getThemeName());

        Application app = new Application(factory);
        app.createUI();
        app.render();
    }

    /**
     * 检测操作系统
     */
    private static String detectOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "Windows";
        } else if (os.contains("mac")) {
            return "Mac";
        } else {
            return "Linux";
        }
    }

    /**
     * 根据操作系统获取对应的工厂
     */
    private static UIFactory getFactoryForOS(String os) {
        switch (os) {
            case "Windows":
                return new WindowsUIFactory();
            case "Mac":
                return new MacUIFactory();
            case "Linux":
            default:
                return new LinuxUIFactory();
        }
    }
}
