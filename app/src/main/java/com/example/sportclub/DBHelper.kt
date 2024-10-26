package com.example.sportclub

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "USERS.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_USERS = "users"
        private const val COLUMN_ID = "ID"
        private const val COLUMN_USERNAME = "Username"
        private const val COLUMN_FIRSTNAME = "FirstName"
        private const val COLUMN_LASTNAME = "LastName"
        private const val COLUMN_PASS = "Pass"
        private const val COLUMN_PHONE = "Phone"
        private const val COLUMN_EMAIL = "Email"
        private const val COLUMN_BIRTHDATE = "Birthdate"
        private const val COLUMN_USERROLE = "UserRole"
        private const val COLUMN_ACTIVEUSER = "ActiveUser"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = ("CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT UNIQUE NOT NULL, "
                + COLUMN_FIRSTNAME + " TEXT NOT NULL, "
                + COLUMN_LASTNAME + " TEXT NOT NULL, "
                + COLUMN_PASS + " TEXT NOT NULL, "
                + COLUMN_PHONE + " TEXT, "
                + COLUMN_EMAIL + " TEXT NOT NULL, "
                + COLUMN_BIRTHDATE + " TEXT, "
                + COLUMN_USERROLE + " TEXT DEFAULT 'Admin', "
                + COLUMN_ACTIVEUSER + " INTEGER DEFAULT 1)")
        db.execSQL(createTable)

        // Insertar un usuario de prueba
        val insertUser = ("INSERT INTO " + TABLE_USERS + " ("
                + COLUMN_USERNAME + ", " + COLUMN_FIRSTNAME + ", " + COLUMN_LASTNAME + ", " + COLUMN_PASS + ", "
                + COLUMN_PHONE + ", " + COLUMN_EMAIL + ", " + COLUMN_BIRTHDATE + ", " + COLUMN_USERROLE + ", " + COLUMN_ACTIVEUSER + ")"
                + " VALUES ('testuser', 'John', 'Doe', 'password123', '123456789', 'john@example.com', '1990-01-01', 'Admin', 1)")
        db.execSQL(insertUser)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // Método para verificar las credenciales del usuario
    fun checkUserCredentials(username: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASS = ?",
            arrayOf(username, password)
        )
        val userExists = cursor.count > 0
        cursor.close()
        db.close()
        return userExists
    }
}