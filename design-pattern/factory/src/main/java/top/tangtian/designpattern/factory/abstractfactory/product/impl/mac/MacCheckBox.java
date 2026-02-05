package top.tangtian.designpattern.factory.abstractfactory.product.impl.mac;

import top.tangtian.designpattern.factory.abstractfactory.product.CheckBox;

public class MacCheckBox implements CheckBox {
    private boolean checked = false;

    @Override
    public void render() {
        System.out.println("  [Mac CheckBox]");
        String box = checked ? "(✓)" : "( )";
        System.out.println("  " + box + " I agree to terms");
        System.out.println("  Style: Circular, Blue gradient");
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        System.out.println("  Mac CheckBox " + (checked ? "checked" : "unchecked") +
                " with spring animation");
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public String getStyle() {
        return "macOS Aqua Design";
    }
}