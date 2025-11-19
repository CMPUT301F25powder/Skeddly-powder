package com.example.skeddly.business.dbnew;

import java.util.Objects;

public class TestNotificationSettings {
    private boolean a;
    private boolean b;

    public TestNotificationSettings() {

    }

    public TestNotificationSettings(boolean a, boolean b) {
        this.a = a;
        this.b = b;
    }

    public boolean isA() {
        return a;
    }

    public boolean isB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TestNotificationSettings that = (TestNotificationSettings) o;
        return a == that.a && b == that.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }
}
