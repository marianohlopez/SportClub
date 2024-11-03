package com.example.sportclub

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.util.Log

data class Member(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val documentType: String,
    val document: Int,
    val inscriptionDate: String,
    val expirationDate: String
)

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {

        private const val DATABASE_NAME = "SportClub.db"
        private const val DATABASE_VERSION = 2

        // Constantes para la tabla users
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

        // Constantes para la tabla members

        private const val TABLE_MEMBERS = "members"
        private const val MEMBER_COLUMN_ID = "ID"
        private const val MEMBER_COLUMN_FIRSTNAME = "FirstName"
        private const val MEMBER_COLUMN_LASTNAME = "LastName"
        private const val MEMBER_COLUMN_DOCUMENTTYPE = "DocumentType"
        private const val MEMBER_COLUMN_DOCUMENT = "Document"
        private const val MEMBER_COLUMN_INSCRIPTIONDATE = "InscriptionDate"
        private const val MEMBER_COLUMN_EXPIRATIONDATE = "ExpirationDate"
        private const val MEMBER_COLUMN_HEALTHCERT = "HealthCert"
        private const val MEMBER_COLUMN_ISACTIVE = "IsActive"
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

        val createMembersTable = ("CREATE TABLE $TABLE_MEMBERS ("
                + "$MEMBER_COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$MEMBER_COLUMN_FIRSTNAME TEXT NOT NULL, "
                + "$MEMBER_COLUMN_LASTNAME TEXT NOT NULL, "
                + "$MEMBER_COLUMN_DOCUMENTTYPE TEXT NOT NULL, "
                + "$MEMBER_COLUMN_DOCUMENT INTEGER UNIQUE NOT NULL, "
                + "$MEMBER_COLUMN_INSCRIPTIONDATE DATE DEFAULT CURRENT_DATE, "
                + "$MEMBER_COLUMN_EXPIRATIONDATE DATE, "
                + "$MEMBER_COLUMN_HEALTHCERT INTEGER DEFAULT 1, "
                + "$MEMBER_COLUMN_ISACTIVE INTEGER DEFAULT 0)")
        db.execSQL(createMembersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MEMBERS")
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

    // Método para agregar un socio
    fun addMember(firstName: String, lastName: String, documentType: String, document: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(MEMBER_COLUMN_FIRSTNAME, firstName)
            put(MEMBER_COLUMN_LASTNAME, lastName)
            put(MEMBER_COLUMN_DOCUMENTTYPE, documentType)
            put(MEMBER_COLUMN_DOCUMENT, document)

            // Asignar InscriptionDate a la fecha actual
            val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            put(MEMBER_COLUMN_INSCRIPTIONDATE, currentDate)

            // Calcular ExpirationDate 30 días después de la fecha actual
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 30)
            val expirationDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
            put(MEMBER_COLUMN_EXPIRATIONDATE, expirationDate)
        }

        return db.insert(TABLE_MEMBERS, null, values)
    }

    // Método para listar socios

    fun getAllMembers(): ArrayList<Member> {
        val memberList = ArrayList<Member>()
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_MEMBERS, // Table name
            null, // Columns to return (null selects all)
            null, // Selection criteria (null selects all rows)
            null, // Selection arguments
            null, // Group by clause
            null, // Having clause
            null // Order by clause
        )

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex(MEMBER_COLUMN_ID)
                val firstNameIndex = cursor.getColumnIndex(MEMBER_COLUMN_FIRSTNAME)
                val lastNameIndex = cursor.getColumnIndex(MEMBER_COLUMN_LASTNAME)
                val documentTypeIndex = cursor.getColumnIndex(MEMBER_COLUMN_DOCUMENTTYPE)
                val documentIndex = cursor.getColumnIndex(MEMBER_COLUMN_DOCUMENT)
                val inscriptionDateIndex = cursor.getColumnIndex(MEMBER_COLUMN_INSCRIPTIONDATE)
                val expirationDateIndex = cursor.getColumnIndex(MEMBER_COLUMN_EXPIRATIONDATE)

                if (idIndex != -1 && firstNameIndex != -1 && lastNameIndex != -1 &&
                    documentTypeIndex != -1 && documentIndex != -1 && inscriptionDateIndex != -1 &&
                    expirationDateIndex != -1) {
                    val member = Member(
                        cursor.getInt(idIndex),
                        cursor.getString(firstNameIndex),
                        cursor.getString(lastNameIndex),
                        cursor.getString(documentTypeIndex),
                        cursor.getInt(documentIndex),
                        cursor.getString(inscriptionDateIndex),
                        cursor.getString(expirationDateIndex)
                    )
                    memberList.add(member)
                } else {
                    Log.e("DatabaseError", "Column not found in result set")
                }
            } while (cursor.moveToNext())
        } else {
            Log.e("DatabaseError", "No results found")
        }

        cursor.close()
        db.close()
        return memberList
    }

}