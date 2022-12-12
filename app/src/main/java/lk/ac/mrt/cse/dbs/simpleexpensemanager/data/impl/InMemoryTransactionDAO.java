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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class InMemoryTransactionDAO implements TransactionDAO {
    private static final SimpleDateFormat simpleDataFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Context context;

    public InMemoryTransactionDAO(Context context) {
        this.context = context;
    }



    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = DatabaseHelper.getHelperInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.DATE_COL, simpleDataFormat.format(date));
        values.put(DatabaseHelper.TRANSACTION_ACCOUNT_NO_COL, accountNo);
        values.put(DatabaseHelper.EXPENSE_COL, String.valueOf(expenseType));
        values.put(DatabaseHelper.AMOUNT_COL, amount);

        db.insert(DatabaseHelper.TABLE_NAME2, null, values);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() throws ParseException {
        //return transactions;
        SQLiteDatabase db = DatabaseHelper.getHelperInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME2 , null);
        List<Transaction> transactionsList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            do {
                transactionsList.add(new Transaction(simpleDataFormat.parse(cursor.getString(0)), cursor.getString(1), ExpenseType.valueOf(cursor.getString(2)), Double.parseDouble(cursor.getString(3))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return transactionsList;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) throws ParseException {
        List<Transaction> transactionList = new ArrayList<>();
        transactionList = getAllTransactionLogs();
        int size = transactionList.size();
        if (size <= limit){
            return transactionList;
        }
        return transactionList.subList(size - limit, size);
    }

}
