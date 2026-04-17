package com.termintracker.repository

import com.termintracker.database.DatabaseManager
import com.termintracker.model.*
import kotlinx.datetime.*
import java.sql.ResultSet

class AppointmentRepository {
    private val conn = DatabaseManager.getConnection()

    fun save(appointment: Appointment): Long {
        val sql = """
            INSERT INTO appointments 
            (title, type, date_time, location, street, house_number, postal_code, city, district, country, 
             latitude, longitude, notes, reminder_minutes, is_reminder_enabled, created_at, updated_at, is_completed)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
        
        conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS).use { stmt ->
            stmt.setString(1, appointment.title)
            stmt.setString(2, appointment.type.name)
            stmt.setString(3, appointment.dateTime.toString())
            stmt.setString(4, appointment.location)
            stmt.setString(5, appointment.address.street)
            stmt.setString(6, appointment.address.houseNumber)
            stmt.setString(7, appointment.address.postalCode)
            stmt.setString(8, appointment.address.city)
            stmt.setString(9, appointment.address.district)
            stmt.setString(10, appointment.address.country)
            stmt.setObject(11, appointment.address.latitude)
            stmt.setObject(12, appointment.address.longitude)
            stmt.setString(13, appointment.notes)
            stmt.setInt(14, appointment.reminderMinutes)
            stmt.setInt(15, if (appointment.isReminderEnabled) 1 else 0)
            stmt.setString(16, appointment.createdAt.toString())
            stmt.setString(17, appointment.updatedAt.toString())
            stmt.setInt(18, if (appointment.isCompleted) 1 else 0)
            
            stmt.executeUpdate()
            
            val rs = stmt.generatedKeys
            if (rs.next()) {
                val id = rs.getLong(1)
                
                // Save required documents
                appointment.requiredDocuments.forEach { doc ->
                    saveRequiredDocument(id, doc)
                }
                
                // Save documents
                appointment.documents.forEach { doc ->
                    saveDocument(id, doc)
                }
                
                return id
            }
        }
        return -1
    }

    fun update(appointment: Appointment): Boolean {
        val sql = """
            UPDATE appointments SET
            title = ?, type = ?, date_time = ?, location = ?, street = ?, house_number = ?, 
            postal_code = ?, city = ?, district = ?, country = ?, latitude = ?, longitude = ?,
            notes = ?, reminder_minutes = ?, is_reminder_enabled = ?, updated_at = ?, is_completed = ?
            WHERE id = ?
        """
        
        conn.prepareStatement(sql).use { stmt ->
            stmt.setString(1, appointment.title)
            stmt.setString(2, appointment.type.name)
            stmt.setString(3, appointment.dateTime.toString())
            stmt.setString(4, appointment.location)
            stmt.setString(5, appointment.address.street)
            stmt.setString(6, appointment.address.houseNumber)
            stmt.setString(7, appointment.address.postalCode)
            stmt.setString(8, appointment.address.city)
            stmt.setString(9, appointment.address.district)
            stmt.setString(10, appointment.address.country)
            stmt.setObject(11, appointment.address.latitude)
            stmt.setObject(12, appointment.address.longitude)
            stmt.setString(13, appointment.notes)
            stmt.setInt(14, appointment.reminderMinutes)
            stmt.setInt(15, if (appointment.isReminderEnabled) 1 else 0)
            stmt.setString(16, Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString())
            stmt.setInt(17, if (appointment.isCompleted) 1 else 0)
            stmt.setLong(18, appointment.id)
            
            return stmt.executeUpdate() > 0
        }
    }

    fun delete(id: Long): Boolean {
        conn.prepareStatement("DELETE FROM appointments WHERE id = ?").use { stmt ->
            stmt.setLong(1, id)
            return stmt.executeUpdate() > 0
        }
    }

    fun getById(id: Long): Appointment? {
        conn.prepareStatement("SELECT * FROM appointments WHERE id = ?").use { stmt ->
            stmt.setLong(1, id)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                return mapResultSetToAppointment(rs)
            }
        }
        return null
    }

    fun getAll(): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        conn.createStatement().use { stmt ->
            val rs = stmt.executeQuery("SELECT * FROM appointments ORDER BY date_time ASC")
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs))
            }
        }
        return appointments
    }

    fun getUpcoming(): List<Appointment> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val appointments = mutableListOf<Appointment>()
        conn.prepareStatement("SELECT * FROM appointments WHERE date_time > ? ORDER BY date_time ASC").use { stmt ->
            stmt.setString(1, now.toString())
            val rs = stmt.executeQuery()
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs))
            }
        }
        return appointments
    }

    fun getToday(): List<Appointment> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        val appointments = mutableListOf<Appointment>()
        conn.prepareStatement("SELECT * FROM appointments WHERE date(date_time) = date(?) ORDER BY date_time ASC").use { stmt ->
            stmt.setString(1, today.toString())
            val rs = stmt.executeQuery()
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs))
            }
        }
        return appointments
    }

    fun getByType(type: AppointmentType): List<Appointment> {
        val appointments = mutableListOf<Appointment>()
        conn.prepareStatement("SELECT * FROM appointments WHERE type = ? ORDER BY date_time ASC").use { stmt ->
            stmt.setString(1, type.name)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs))
            }
        }
        return appointments
    }

    fun markAsCompleted(id: Long, completed: Boolean = true): Boolean {
        conn.prepareStatement("UPDATE appointments SET is_completed = ?, updated_at = ? WHERE id = ?").use { stmt ->
            stmt.setInt(1, if (completed) 1 else 0)
            stmt.setString(2, Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString())
            stmt.setLong(3, id)
            return stmt.executeUpdate() > 0
        }
    }

    private fun mapResultSetToAppointment(rs: ResultSet): Appointment {
        val id = rs.getLong("id")
        return Appointment(
            id = id,
            title = rs.getString("title"),
            type = AppointmentType.fromString(rs.getString("type")),
            dateTime = LocalDateTime.parse(rs.getString("date_time")),
            location = rs.getString("location") ?: "",
            address = Address(
                street = rs.getString("street") ?: "",
                houseNumber = rs.getString("house_number") ?: "",
                postalCode = rs.getString("postal_code") ?: "",
                city = rs.getString("city") ?: "",
                district = rs.getString("district") ?: "",
                country = rs.getString("country") ?: "Deutschland",
                latitude = rs.getObject("latitude") as? Double,
                longitude = rs.getObject("longitude") as? Double
            ),
            notes = rs.getString("notes") ?: "",
            reminderMinutes = rs.getInt("reminder_minutes"),
            isReminderEnabled = rs.getInt("is_reminder_enabled") == 1,
            documents = getDocumentsForAppointment(id),
            requiredDocuments = getRequiredDocumentsForAppointment(id),
            createdAt = LocalDateTime.parse(rs.getString("created_at")),
            updatedAt = LocalDateTime.parse(rs.getString("updated_at")),
            isCompleted = rs.getInt("is_completed") == 1
        )
    }

    private fun saveRequiredDocument(appointmentId: Long, doc: RequiredDocument) {
        val sql = """
            INSERT INTO required_documents (appointment_id, name, description, is_required, is_checked, appointment_type)
            VALUES (?, ?, ?, ?, ?, ?)
        """
        conn.prepareStatement(sql).use { stmt ->
            stmt.setLong(1, appointmentId)
            stmt.setString(2, doc.name)
            stmt.setString(3, doc.description)
            stmt.setInt(4, if (doc.isRequired) 1 else 0)
            stmt.setInt(5, if (doc.isChecked) 1 else 0)
            stmt.setString(6, doc.appointmentType?.name)
            stmt.executeUpdate()
        }
    }

    private fun saveDocument(appointmentId: Long, doc: Document) {
        val sql = """
            INSERT INTO documents (appointment_id, name, file_path, is_uploaded, upload_date, notes)
            VALUES (?, ?, ?, ?, ?, ?)
        """
        conn.prepareStatement(sql).use { stmt ->
            stmt.setLong(1, appointmentId)
            stmt.setString(2, doc.name)
            stmt.setString(3, doc.filePath)
            stmt.setInt(4, if (doc.isUploaded) 1 else 0)
            stmt.setString(5, doc.uploadDate?.toString())
            stmt.setString(6, doc.notes)
            stmt.executeUpdate()
        }
    }

    private fun getRequiredDocumentsForAppointment(appointmentId: Long): List<RequiredDocument> {
        val docs = mutableListOf<RequiredDocument>()
        conn.prepareStatement("SELECT * FROM required_documents WHERE appointment_id = ?").use { stmt ->
            stmt.setLong(1, appointmentId)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                docs.add(RequiredDocument(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    description = rs.getString("description") ?: "",
                    isRequired = rs.getInt("is_required") == 1,
                    isChecked = rs.getInt("is_checked") == 1,
                    appointmentType = rs.getString("appointment_type")?.let { AppointmentType.fromString(it) }
                ))
            }
        }
        return docs
    }

    private fun getDocumentsForAppointment(appointmentId: Long): List<Document> {
        val docs = mutableListOf<Document>()
        conn.prepareStatement("SELECT * FROM documents WHERE appointment_id = ?").use { stmt ->
            stmt.setLong(1, appointmentId)
            val rs = stmt.executeQuery()
            while (rs.next()) {
                docs.add(Document(
                    id = rs.getLong("id"),
                    name = rs.getString("name"),
                    filePath = rs.getString("file_path"),
                    isUploaded = rs.getInt("is_uploaded") == 1,
                    uploadDate = rs.getString("upload_date")?.let { LocalDateTime.parse(it) },
                    notes = rs.getString("notes") ?: ""
                ))
            }
        }
        return docs
    }
}
