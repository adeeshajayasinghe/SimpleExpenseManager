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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class InMemoryAccountDAO extends SQLiteOpenHelper implements AccountDAO {
    private static final String DB_NAME = "MyDatabase";
    private static final int DB_VERSION = 1;
    private static final String TABLE_NAME = "accounts";
    private static final String ACCOUNT_NO_COL = "accountNo";
    private static final String BANK_NAME_COL = "bankName";
    private static final String HOLDER_COL = "accountHolderName";
    private static final String BALANCE_COL = "balance";
    public InMemoryAccountDAO(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ACCOUNT_NO_COL + " TEXT PRIMARY KEY, "
                + BANK_NAME_COL + " TEXT, "
                + HOLDER_COL + " TEXT, "
                + BALANCE_COL + " NUMERIC)";

        sqLiteDatabase.execSQL(query);
    }


    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        List<String> accountNumbersList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                accountNumbersList.add(new Account(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4)).getAccountNo()); // Check this...........................
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME , null);
        List<Account> accountsList = new ArrayList<>();
        if (cursor.moveToFirst()){
            do {
                accountsList.add(new Account(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE " + accountNo + " = ?", new String[]{accountNo});
        if (cursor.getCount() > 0){
            return new Account(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getDouble(4));
        }
        cursor.close();
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ACCOUNT_NO_COL, account.getAccountNo());
        values.put(BANK_NAME_COL, account.getBankName());
        values.put(HOLDER_COL, account.getAccountHolderName());
        values.put(BALANCE_COL, account.getBalance());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE " + accountNo + " = ?", new String[]{accountNo});
        if (cursor.getCount() > 0){
            db.delete(TABLE_NAME, "accountNo=?", new String[]{accountNo});
        }else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME +" WHERE " + accountNo + " = ?", new String[]{accountNo});
        if (cursor.getCount() > 0){
            Account account = getAccount(accountNo);
            switch (expenseType) {
            case EXPENSE:
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
            }
            contentValues.put("balance", account.getBalance());
            db.update(TABLE_NAME, contentValues, "accountNo=?", new String[]{accountNo});
        }else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
