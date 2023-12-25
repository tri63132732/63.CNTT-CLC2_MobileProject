package com.nguyentrongtri.ailatrieuphu4.manager;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.nguyentrongtri.ailatrieuphu4.model.HighScore;
import com.nguyentrongtri.ailatrieuphu4.model.Question;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DatabaseManager {
    private String DATABASE_NAME = "Question.sqlite";
    private String DATABASE_PATH =
            Environment.getDataDirectory().getAbsolutePath()
                    + "/data/com.nguyentrongtri.ailatrieuphu4/databases";

    private static final String SQL_GET_15_QUESTION = "select * from (select* from Question order by random()) group by level order by level limit 15";

    private Context context;

    private SQLiteDatabase sqLiteDatabase;

    public DatabaseManager(Context context) {
        this.context = context;
        copyDatabases();
    }

    private void copyDatabases() {
        try {
            File file = new File(DATABASE_PATH + DATABASE_NAME);
            if (file.exists()) {
                return;
            }
            AssetManager asset = context.getAssets();
            DataInputStream in = new DataInputStream(asset.open(DATABASE_NAME));
            DataOutputStream out = new DataOutputStream(
                    new FileOutputStream(file));
            byte[] b = new byte[1024];
            int length;
            while ((length = in.read(b)) != -1) {
                out.write(b, 0, length);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openDatabase() {
        if (sqLiteDatabase == null || !sqLiteDatabase.isOpen()) {
            sqLiteDatabase = SQLiteDatabase.openDatabase(DATABASE_PATH + DATABASE_NAME,
                    null, SQLiteDatabase.OPEN_READWRITE);
        }
    }

    private void closeDatabase() {
        if (sqLiteDatabase != null
                && sqLiteDatabase.isOpen()) {
            sqLiteDatabase.close();
        }
    }

    public void writeScore() {
        ContentValues values = new ContentValues();
        values.put("Name", "player5");
        values.put("Score", 200000);
        insert("HighScore", values);
    }

    public ArrayList<HighScore> getHighScore() {
        openDatabase();
        String sql = "select * from HighScore ORDER BY Score DESC";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor == null
                || cursor.getCount() == 0) {
            return null;
        }
        ArrayList<HighScore> highScores = new ArrayList<>();
        cursor.moveToFirst();
        String name;
        int score;
        while (!cursor.isAfterLast()){
            name = cursor.getString(0);
            score = cursor.getInt(1);

            highScores.add(new HighScore(name, score));
            cursor.moveToNext();
        }
        closeDatabase();
        return highScores;
    }

    public void insert(String tableName, ContentValues values) {
        openDatabase();
        sqLiteDatabase.insert(tableName, null, values);
        closeDatabase();
    }

    public void delete(String tableName, String whereClause, String[] whereArgs) {
        openDatabase();
        sqLiteDatabase.delete(tableName, whereClause, whereArgs);
        closeDatabase();
    }

    public void update(String tableName, ContentValues values, String whereClause, String[] whereArgs) {
        openDatabase();
        sqLiteDatabase.update(tableName, values, whereClause, whereArgs);
        closeDatabase();
    }

    public Question getQuestionByLevel(int lv) {
        openDatabase();
        String sql = "select * from Question where level = '" + lv + "' order by random()  limit 1";
        Cursor cursor = sqLiteDatabase.rawQuery(sql, null);
        if (cursor == null
                || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();

        String question, caseA, caseB, caseC, caseD;
        int level, trueCase;

        question = cursor.getString(0);
        level = cursor.getInt(2);
        caseA = cursor.getString(3);
        caseB = cursor.getString(4);
        caseC = cursor.getString(5);
        caseD = cursor.getString(6);
        trueCase = cursor.getInt(7);
        Question question1 = new Question(question, caseA, caseB, caseC, caseD, trueCase, level);
        closeDatabase();
        return question1;
    }

    public ArrayList<Question> get15Questions() {
        openDatabase();
        ArrayList<Question> questions = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.rawQuery(SQL_GET_15_QUESTION, null);
        if (cursor == null
                || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String q = cursor.getString(0);
            int l = cursor.getInt(2);
            String a = cursor.getString(3);
            String b = cursor.getString(4);
            String c = cursor.getString(5);
            String d = cursor.getString(6);
            int t = cursor.getInt(7);

            questions.add(new Question(q, a, b, c, d, t, l));
            cursor.moveToNext();
        }
        closeDatabase();
        return questions;
    }
}
