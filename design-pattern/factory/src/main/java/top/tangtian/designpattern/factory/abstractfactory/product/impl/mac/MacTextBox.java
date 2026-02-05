package top.tangtian.designpattern.factory.abstractfactory.product.impl.mac;

import top.tangtian.designpattern.factory.abstractfactory.product.TextBox;

public class MacTextBox implements TextBox {
    private String text = "";

    @Override
    public void render() {
        System.out.println("  [Mac TextBox]");
        System.out.println("  ╭────────────────────────╮");
        System.out.println("  │ " + (text.isEmpty() ? "Enter text..." : text) +
                " ".repeat(Math.max(0, 21 - text.length())) + "│");
        System.out.println("  ╰────────────────────────╯");
        System.out.println("  Style: Glossy, Inner shadow, Focus ring");
    }

    @Override
    public void setText(String text) {
        this.text = text;
        System.out.println("  Text set in Mac style with cursor animation: " + text);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getStyle() {
        return "macOS Aqua Design";
    }
}
