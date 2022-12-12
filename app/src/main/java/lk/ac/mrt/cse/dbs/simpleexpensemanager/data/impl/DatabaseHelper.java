package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "200255M.db";
    private static final int DB_VERSION = 15;
    public static final String TABLE_NAME1 = "accounts";
    public static final String ACCOUNT_NO_COL = "accountNo";
    public static final String TRANSACTION_ACCOUNT_NO_COL = "accountNo";
    public static final String BANK_NAME_COL = "bankName";
    public static final String HOLDER_COL = "accountHolderName";
    public static final String BALANCE_COL = "balance";
    public static final String TABLE_NAME2 = "transactions";
    public static final String DATE_COL = "date";
    public static final String EXPENSE_COL = "expenseType";
    public static final String AMOUNT_COL = "amount";
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query1 = "CREATE TABLE " + TABLE_NAME1 + " ("
                + ACCOUNT_NO_COL + " TEXT PRIMARY KEY, "
                + BANK_NAME_COL + " TEXT, "
                + HOLDER_COL + " TEXT, "
                + BALANCE_COL + " NUMERIC)";
        String query2 = "CREATE TABLE " + TABLE_NAME2 + " ("
                + DATE_COL + " TEXT, "
                + TRANSACTION_ACCOUNT_NO_COL + " TEXT, "
                + EXPENSE_COL + " TEXT, "
                + AMOUNT_COL + " NUMERIC)";

        sqLiteDatabase.execSQL(query1);
        sqLiteDatabase.execSQL(query2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME1);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
        onCreate(sqLiteDatabase);
    }
    public static DatabaseHelper getHelperInstance(Context context){
        return new DatabaseHelper(context);
    }
}
