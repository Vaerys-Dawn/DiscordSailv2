package com.github.vaerys.objects;

public class DualVar<A, B> {
    A var1;
    B var2;

    public DualVar(A var1, B var2) {
        this.var1 = var1;
        this.var2 = var2;
    }

    public A getVar1() {
        return var1;
    }

    public B getVar2() {
        return var2;
    }

    public void setVar1(A var1) {
        this.var1 = var1;
    }

    public void setVar2(B var2) {
        this.var2 = var2;
    }
}
