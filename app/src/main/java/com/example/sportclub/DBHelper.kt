package com.example.sportclub

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.database.Cursor

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

        internal const val TABLE_MEMBERS = "members"
        internal const val MEMBER_COLUMN_ID = "ID"
        private const val MEMBER_COLUMN_FIRSTNAME = "FirstName"
        private const val MEMBER_COLUMN_LASTNAME = "LastName"
        private const val MEMBER_COLUMN_DOCUMENTTYPE = "DocumentType"
        internal const val MEMBER_COLUMN_DOCUMENT = "Document"
        private const val MEMBER_COLUMN_INSCRIPTIONDATE = "InscriptionDate"
        internal const val MEMBER_COLUMN_EXPIRATIONDATE = "ExpirationDate"
        private const val MEMBER_COLUMN_HEALTHCERT = "HealthCert"
        internal const val MEMBER_COLUMN_ISACTIVE = "IsActive"
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

    // Método para obtener los datos de un miembro específico por su ID
    fun getMember(memberId: Int): Map<String, String>? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM Member WHERE id = ?", arrayOf(memberId.toString()))

        // Verificar si el cursor tiene datos
        return if (cursor.moveToFirst()) {
            // Crear un mapa con los datos del cursor
            val memberData = mapOf(
                "cardNumber" to cursor.getString(cursor.getColumnIndexOrThrow("cardNumber")),
                "name" to cursor.getString(cursor.getColumnIndexOrThrow("name")),
                "surname" to cursor.getString(cursor.getColumnIndexOrThrow("surname")),
                "docType" to cursor.getString(cursor.getColumnIndexOrThrow("docType")),
                "docNumber" to cursor.getString(cursor.getColumnIndexOrThrow("docNumber")),
                "issueDate" to cursor.getLong(cursor.getColumnIndexOrThrow("issueDate")).toString(),
                "expirationDate" to cursor.getLong(cursor.getColumnIndexOrThrow("expirationDate")).toString()
            )
            cursor.close()
            db.close()
            memberData
        } else {
            cursor.close()
            db.close()
            null
        }
    }
}