package top.tangtian.designpattern.factory.abstractfactory.product.impl.windows;

import top.tangtian.designpattern.factory.abstractfactory.product.TextBox;

public class WindowsTextBox implements TextBox {
    private String text = "";

    @Override
    public void render() {
        System.out.println("  [Windows TextBox]");
        System.out.println("  ┌────────────────────────┐");
        System.out.println("  │ " + (text.isEmpty() ? "Enter text..." : text) +
                " ".repeat(Math.max(0, 21 - text.length())) + "│");
        System.out.println("  └────────────────────────┘");
        System.out.println("  Style: Rounded corners, Light gray background");
    }

    @Override
    public void setText(String text) {
        this.text = text;
        System.out.println("  Text set in Windows style: " + text);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getStyle() {
        return "Windows 11 Fluent Design";
    }
}