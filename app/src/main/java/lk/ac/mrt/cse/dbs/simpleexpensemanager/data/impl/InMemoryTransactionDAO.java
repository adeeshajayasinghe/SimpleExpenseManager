/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class InMemoryTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    //private final List<Transaction> transactions;
    private static final String DB_NAME = "MyDatabase";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "transactions";
    private static final String DATE_COL = "date";
    private static final String ACCOUNT_NO_COL = "accountNo";
    private static final String EXPENSE_COL = "expenseType";
    private static final String AMOUNT_COL = "amount";

    public InMemoryTransactionDAO(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + DATE_COL + "TEXT"
                + ACCOUNT_NO_COL + " TEXT PRIMARY KEY, "
                + EXPENSE_COL + " TEXT, "
                + AMOUNT_COL + " NUMERIC)";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
//        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
//        transactions.add(transaction);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DATE_COL, String.valueOf(date));
        values.put(ACCOUNT_NO_COL, accountNo);
        values.put(EXPENSE_COL, String.valueOf(expenseType));
        values.put(AMOUNT_COL, amount);

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        //return transactions;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = format.parse(cursor.getString(1));
        List<Transaction> transactionsList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                transactionsList.add(new Transaction(date, cursor.getString(2), ExpenseType.valueOf(cursor.getString(3)), cursor.getDouble(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactionsList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
//        int size = transactions.size();
//        if (size <= limit) {
//            return transactions;
//        }
//        // return the last <code>limit</code> number of transaction logs
//        return transactions.subList(size - limit, size);
        List<Transaction> transactionList = new ArrayList<>();
        transactionList = getAllTransactionLogs();
        int size = transactionList.size();
        if (size <= limit){
            return transactionList;
        }
        return transactionList.subList(size - limit, size);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
