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

    public static final String STATUS_ACTIVE_QUERY = "'ACTIVE'";
    public static final String STATUS_INACTIVE_QUERY = "'INACTIVE'";
    public static final String STATUS_PENDING_QUERY = "'PENDING'";

    public static final String STATUS_ACTIVE_CREATE = "ACTIVE";
    public static final String STATUS_PENDING_CREATE = "PENDING";

    public static final Long USER_ID_SYSTEM = 0L;
}
