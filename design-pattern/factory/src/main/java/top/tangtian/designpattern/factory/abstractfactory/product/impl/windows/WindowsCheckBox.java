package top.tangtian.designpattern.factory.abstractfactory.product.impl.windows;

import top.tangtian.designpattern.factory.abstractfactory.product.CheckBox;

public class WindowsCheckBox implements CheckBox {
    private boolean checked = false;

    @Override
    public void render() {
        System.out.println("  [Windows CheckBox]");
        String box = checked ? "[✓]" : "[ ]";
        System.out.println("  " + box + " I agree to terms");
        System.out.println("  Style: Square, Blue checkmark");
    }

    @Override
    public void setChecked(boolean checked) {
        this.checked = checked;
        System.out.println("  Windows CheckBox " + (checked ? "checked" : "unchecked") +
                " with fade animation");
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public String getStyle() {
        return "Windows 11 Fluent Design";
    }
}