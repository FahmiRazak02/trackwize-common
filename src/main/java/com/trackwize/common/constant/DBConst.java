package com.trackwize.common.constant;

public class DBConst {

    private DBConst() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final String USER_TABLE = "users";
    public static final String TOKEN_TABLE = "tokens";
    public static final String EXPENSE_TABLE = "expenses";
    public static final String BUDGET_TABLE = "budgest";
    public static final String RECURRING_EXPENSE_TABLE = "recurring_expenses";

    public static final String STATUS_ACTIVE = "'ACTIVE'";
    public static final String STATUS_INACTIVE = "'INACTIVE'";
    public static final String STATUS_PENDING = "'PENDING'";
}
