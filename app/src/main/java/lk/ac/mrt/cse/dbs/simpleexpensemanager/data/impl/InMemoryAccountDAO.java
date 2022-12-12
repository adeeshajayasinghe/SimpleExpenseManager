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
import java.util.ArrayList;
import java.util.List;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class InMemoryAccountDAO implements AccountDAO {
    private Context context;
    public InMemoryAccountDAO(Context context){
        this.context = context;
    }



    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase db = DatabaseHelper.getHelperInstance(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME1 , null);
        List<String> accountNumbersList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            do {
                accountNumbersList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase db = DatabaseHelper.getHelperInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME1 , null);
        List<Account> accountsList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()){
            do {
                accountsList.add(new Account(cursor.getString(0), cursor.getString(1), cursor.getString(2), Double.parseDouble(cursor.getString(3))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return accountsList;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = DatabaseHelper.getHelperInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME1 +" WHERE " + DatabaseHelper.ACCOUNT_NO_COL + " = ?", new String[]{accountNo});
        if (cursor.moveToFirst() && cursor.getCount()>0){
            return new Account(cursor.getString(cursor.getColumnIndex("accountNo")), cursor.getString(cursor.getColumnIndex("bankName")), cursor.getString(cursor.getColumnIndex("accountHolderName")), cursor.getDouble(cursor.getColumnIndex("balance")));
        }
        cursor.close();
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = DatabaseHelper.getHelperInstance(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.ACCOUNT_NO_COL, account.getAccountNo());
        values.put(DatabaseHelper.BANK_NAME_COL, account.getBankName());
        values.put(DatabaseHelper.HOLDER_COL, account.getAccountHolderName());
        values.put(DatabaseHelper.BALANCE_COL, account.getBalance());

        db.insert(DatabaseHelper.TABLE_NAME1, null, values);
        db.close();
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase db = DatabaseHelper.getHelperInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME1 +" WHERE " + DatabaseHelper.ACCOUNT_NO_COL + " = ?", new String[]{accountNo});
        if (cursor!=null && cursor.getCount()>0){
            db.delete(DatabaseHelper.TABLE_NAME1, "accountNo=?", new String[]{accountNo});
        }else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        cursor.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase db = DatabaseHelper.getHelperInstance(context).getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_NAME1 +" WHERE " + DatabaseHelper.ACCOUNT_NO_COL + " = ?", new String[]{accountNo});
        if (cursor!=null && cursor.getCount()>0){
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
            db.update(DatabaseHelper.TABLE_NAME1, contentValues, "accountNo=?", new String[]{accountNo});
        }else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        cursor.close();
    }

}
