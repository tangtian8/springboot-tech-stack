package top.tangtian.designpattern.factory.abstractfactory.app;

import top.tangtian.designpattern.factory.abstractfactory.factory.UIFactory;
import top.tangtian.designpattern.factory.abstractfactory.product.Button;
import top.tangtian.designpattern.factory.abstractfactory.product.CheckBox;
import top.tangtian.designpattern.factory.abstractfactory.product.TextBox;

/**
 * 客户端应用
 * 使用抽象工厂创建UI组件，不依赖具体实现
 */
public class Application {
    private UIFactory factory;
    private Button submitButton;
    private TextBox usernameInput;
    private CheckBox agreeCheckBox;

    public Application(UIFactory factory) {
        this.factory = factory;
    }

    /**
     * 创建UI组件
     */
    public void createUI() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Creating Application UI");
        System.out.println("Theme: " + factory.getThemeName());
        System.out.println("=".repeat(60) + "\n");

        // 创建组件（不关心具体平台）
        this.submitButton = factory.createButton();
        this.usernameInput = factory.createTextBox();
        this.agreeCheckBox = factory.createCheckBox();

        System.out.println("\n✓ All UI components created successfully\n");
    }

    /**
     * 渲染UI
     */
    public void render() {
        System.out.println("=".repeat(60));
        System.out.println("Rendering Login Form");
        System.out.println("=".repeat(60) + "\n");

        System.out.println("Username:");
        usernameInput.render();

        System.out.println("\nTerms & Conditions:");
        agreeCheckBox.render();

        System.out.println("\nSubmit:");
        submitButton.render();

        System.out.println("\n" + "=".repeat(60));
    }

    /**
     * 模拟用户交互
     */
    public void simulateUserInteraction() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("Simulating User Interaction");
        System.out.println("=".repeat(60) + "\n");

        // 用户输入用户名
        System.out.println("1. User types username:");
        usernameInput.setText("john.doe");

        // 用户勾选复选框
        System.out.println("\n2. User checks the agreement:");
        agreeCheckBox.setChecked(true);

        // 用户点击按钮
        System.out.println("\n3. User clicks submit button:");
        submitButton.onClick();

        System.out.println("\n✓ Form submitted successfully!");
        System.out.println("  Username: " + usernameInput.getText());
        System.out.println("  Agreed: " + agreeCheckBox.isChecked());

        System.out.println("\n" + "=".repeat(60));
    }
}