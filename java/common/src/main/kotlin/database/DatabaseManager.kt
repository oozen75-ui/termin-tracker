package com.termintracker.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.Statement
import java.io.File

object DatabaseManager {
    private var connection: Connection? = null
    private const val DB_NAME = "termin_tracker.db"
    private var dbPath: String = ""

    fun initialize(customPath: String? = null) {
        dbPath = customPath ?: getDefaultDbPath()
        
        // Ensure directory exists
        File(dbPath).parentFile?.mkdirs()
        
        connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
        createTables()
    }

    private fun getDefaultDbPath(): String {
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".termin_tracker")
        appDir.mkdirs()
        return File(appDir, DB_NAME).absolutePath
    }

    fun getConnection(): Connection {
        if (connection == null || connection!!.isClosed) {
            connection = DriverManager.getConnection("jdbc:sqlite:$dbPath")
        }
        return connection!!
    }

    private fun createTables() {
        val stmt = getConnection().createStatement()
        
        // Personal Info table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS personal_info (
                id INTEGER PRIMARY KEY,
                first_name TEXT NOT NULL,
                last_name TEXT NOT NULL,
                birth_date TEXT,
                email TEXT,
                phone TEXT,
                street TEXT,
                house_number TEXT,
                postal_code TEXT,
                city TEXT,
                district TEXT,
                country TEXT DEFAULT 'Deutschland',
                preferred_language TEXT DEFAULT 'de'
            )
        """)

        // Appointments table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS appointments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                type TEXT NOT NULL,
                date_time TEXT NOT NULL,
                location TEXT,
                street TEXT,
                house_number TEXT,
                postal_code TEXT,
                city TEXT,
                district TEXT,
                country TEXT,
                latitude REAL,
                longitude REAL,
                notes TEXT,
                reminder_minutes INTEGER DEFAULT 30,
                is_reminder_enabled INTEGER DEFAULT 1,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL,
                is_completed INTEGER DEFAULT 0
            )
        """)

        // Documents table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS documents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                appointment_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                file_path TEXT,
                is_uploaded INTEGER DEFAULT 0,
                upload_date TEXT,
                notes TEXT,
                FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
            )
        """)

        // Required Documents table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS required_documents (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                appointment_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                description TEXT,
                is_required INTEGER DEFAULT 1,
                is_checked INTEGER DEFAULT 0,
                appointment_type TEXT,
                FOREIGN KEY (appointment_id) REFERENCES appointments(id) ON DELETE CASCADE
            )
        """)

        // Settings table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS settings (
                key TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
        """)

        // Insert default settings
        stmt.execute("""
            INSERT OR IGNORE INTO settings (key, value) VALUES 
            ('language', 'de'),
            ('default_reminder_minutes', '30'),
            ('auto_backup', 'true')
        """)

        // ONLINE APPOINTMENT SEARCH TABLES

        // Appointment searches table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS appointment_searches (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                appointment_types TEXT NOT NULL, -- JSON array: ["MRT", "Nuklearmedizin"]
                start_date TEXT NOT NULL,
                end_date TEXT NOT NULL,
                preferred_time_start TEXT,
                preferred_time_end TEXT,
                location TEXT NOT NULL,
                radius_km INTEGER DEFAULT 10,
                search_interval_minutes INTEGER DEFAULT 15,
                is_active INTEGER DEFAULT 1,
                last_search_time TEXT,
                notification_email TEXT,
                notification_telegram TEXT,
                created_at TEXT NOT NULL,
                updated_at TEXT NOT NULL
            )
        """)

        // Search results table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS search_results (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                search_id INTEGER NOT NULL,
                source TEXT NOT NULL, -- "doctolib", "klinik", etc.
                doctor_name TEXT NOT NULL,
                specialty TEXT NOT NULL,
                location TEXT NOT NULL,
                appointment_date TEXT NOT NULL,
                appointment_time TEXT NOT NULL,
                url TEXT NOT NULL,
                is_available INTEGER DEFAULT 1,
                found_at TEXT NOT NULL,
                notified_at TEXT,
                is_booked INTEGER DEFAULT 0,
                FOREIGN KEY (search_id) REFERENCES appointment_searches(id) ON DELETE CASCADE
            )
        """)

        // Notifications sent table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS notifications_sent (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                result_id INTEGER NOT NULL,
                notification_type TEXT NOT NULL, -- "email", "telegram", "desktop"
                sent_at TEXT NOT NULL,
                status TEXT NOT NULL, -- "success", "failed"
                error_message TEXT,
                FOREIGN KEY (result_id) REFERENCES search_results(id) ON DELETE CASCADE
            )
        """)

        // Search history table
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS search_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                search_id INTEGER NOT NULL,
                search_time TEXT NOT NULL,
                results_count INTEGER DEFAULT 0,
                duration_ms INTEGER,
                error_message TEXT,
                FOREIGN KEY (search_id) REFERENCES appointment_searches(id) ON DELETE CASCADE
            )
        """)

        // Insert default online search settings
        stmt.execute("""
            INSERT OR IGNORE INTO settings (key, value) VALUES 
            ('online_search_enabled', 'true'),
            ('default_search_interval', '15'),
            ('doctolib_enabled', 'true'),
            ('email_notifications_enabled', 'false'),
            ('telegram_notifications_enabled', 'false'),
            ('notification_cooldown_minutes', '60')
        """)

        stmt.close()
    }

    fun close() {
        connection?.close()
        connection = null
    }

    fun backup(backupPath: String): Boolean {
        return try {
            val backupConn = DriverManager.getConnection("jdbc:sqlite:$backupPath")
            val stmt = getConnection().createStatement()
            stmt.executeUpdate("BACKUP TO '$backupPath'")
            stmt.close()
            backupConn.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
