package com.trackwize.common.constant;

public class NotificationConst {

    private NotificationConst() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final char EMAIL_NTF_TYPE = 'E';
    public static final char INBOX_NTF_TYPE = 'I';

    public static final int PASSWORD_RESET_TEMPLATE = 1;
    public static final int ACCOUNT_IS_CREATED_TEMPLATE = 2;
    public static final int ACCOUNT_IS_VERIFIED_TEMPLATE = 3;
    public static final int ACCOUNT_IS_ACTIVATE_TEMPLATE = 4;


}
