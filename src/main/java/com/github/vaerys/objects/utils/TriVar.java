package com.github.vaerys.objects.utils;

public class TriVar <A, B, C> {

    private A var1;
    private B var2;
    private C var3;

    public TriVar(A var1, B var2, C var3) {
        this.var1 = var1;
        this.var2 = var2;
        this.var3 = var3;
    }

    public A getVar1() {
        return var1;
    }

    public B getVar2() {
        return var2;
    }

    public C getVar3() {
        return var3;
    }

    public void setVar1(A var1) {
        this.var1 = var1;
    }

    public void setVar2(B var2) {
        this.var2 = var2;
    }

    public void setVar3(C var3) {
        this.var3 = var3;
    }
}
