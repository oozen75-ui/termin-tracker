package com.termintracker.repository.search

import com.termintracker.database.DatabaseManager
import com.termintracker.model.search.*
import kotlinx.datetime.*
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

class AppointmentSearchRepository {

    private fun getConnection(): Connection = DatabaseManager.getConnection()

    fun save(search: AppointmentSearch): Long {
        val conn = getConnection()
        val stmt = if (search.id == 0L) {
            conn.prepareStatement(
                """
                INSERT INTO appointment_searches (
                    name, search_type, appointment_category, city, postal_code, street, radius_km,
                    start_date, end_date, start_time, end_time, is_active, check_interval_minutes,
                    max_search_duration_days, match_criteria, email_enabled, email_address,
                    telegram_enabled, telegram_chat_id, desktop_notification_enabled,
                    notify_on_new_slot, notify_on_better_slot, created_at, last_checked_at, next_check_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """,
                java.sql.Statement.RETURN_GENERATED_KEYS
            )
        } else {
            conn.prepareStatement(
                """
                UPDATE appointment_searches SET
                    name = ?, search_type = ?, appointment_category = ?, city = ?, postal_code = ?,
                    street = ?, radius_km = ?, start_date = ?, end_date = ?, start_time = ?,
                    end_time = ?, is_active = ?, check_interval_minutes = ?, max_search_duration_days = ?,
                    match_criteria = ?, email_enabled = ?, email_address = ?, telegram_enabled = ?,
                    telegram_chat_id = ?, desktop_notification_enabled = ?, notify_on_new_slot = ?,
                    notify_on_better_slot = ?, last_checked_at = ?, next_check_at = ?
                WHERE id = ?
                """
            )
        }

        stmt.use { statement ->
            var index = 1

            // Basic info
            statement.setString(index++, search.name)
            statement.setString(index++, search.searchType.name)
            statement.setString(index++, search.appointmentCategory.name)

            // Location
            statement.setString(index++, search.location.city)
            statement.setString(index++, search.location.postalCode)
            statement.setString(index++, search.location.street)
            statement.setInt(index++, search.location.radiusKm)

            // Date range
            statement.setString(index++, search.dateRange.startDate.toString())
            statement.setString(index++, search.dateRange.endDate.toString())

            // Time range
            statement.setString(index++, search.timeRange?.startTime?.toString())
            statement.setString(index++, search.timeRange?.endTime?.toString())

            // Settings
            statement.setBoolean(index++, search.isActive)
            statement.setInt(index++, search.checkIntervalMinutes)
            statement.setInt(index++, search.maxSearchDurationDays)
            statement.setString(index++, search.matchCriteria.name)

            // Notification settings
            statement.setBoolean(index++, search.notificationSettings.emailEnabled)
            statement.setString(index++, search.notificationSettings.emailAddress)
            statement.setBoolean(index++, search.notificationSettings.telegramEnabled)
            statement.setString(index++, search.notificationSettings.telegramChatId)
            statement.setBoolean(index++, search.notificationSettings.desktopNotificationEnabled)
            statement.setBoolean(index++, search.notificationSettings.notifyOnNewSlot)
            statement.setBoolean(index++, search.notificationSettings.notifyOnBetterSlot)

            // Timestamps
            if (search.id == 0L) {
                statement.setString(index++, search.createdAt.toString())
                statement.setString(index++, search.lastCheckedAt?.toString())
                statement.setString(index++, search.nextCheckAt?.toString())
            } else {
                statement.setString(index++, search.lastCheckedAt?.toString())
                statement.setString(index++, search.nextCheckAt?.toString())
                statement.setLong(index++, search.id)
            }

            statement.executeUpdate()

            return if (search.id == 0L) {
                val generatedKeys = statement.generatedKeys
                generatedKeys.use { rs ->
                    if (rs.next()) rs.getLong(1) else 0L
                }
            } else {
                search.id
            }
        }
    }

    fun getById(id: Long): AppointmentSearch? {
        val conn = getConnection()
        val stmt = conn.prepareStatement("SELECT * FROM appointment_searches WHERE id = ?")

        stmt.use { statement ->
            statement.setLong(1, id)
            val rs = statement.executeQuery()

            rs.use { resultSet ->
                return if (resultSet.next()) {
                    mapResultSetToSearch(resultSet)
                } else null
            }
        }
    }

    fun getAllActive(): List<AppointmentSearch> {
        val conn = getConnection()
        val stmt = conn.prepareStatement("SELECT * FROM appointment_searches WHERE is_active = true")

        stmt.use { statement ->
            val rs = statement.executeQuery()
            return rs.use { resultSet ->
                val searches = mutableListOf<AppointmentSearch>()
                while (resultSet.next()) {
                    mapResultSetToSearch(resultSet)?.let { searches.add(it) }
                }
                searches
            }
        }
    }

    fun getAll(): List<AppointmentSearch> {
        val conn = getConnection()
        val stmt = conn.prepareStatement("SELECT * FROM appointment_searches ORDER BY created_at DESC")

        stmt.use { statement ->
            val rs = statement.executeQuery()
            return rs.use { resultSet ->
                val searches = mutableListOf<AppointmentSearch>()
                while (resultSet.next()) {
                    mapResultSetToSearch(resultSet)?.let { searches.add(it) }
                }
                searches
            }
        }
    }

    fun delete(id: Long): Boolean {
        val conn = getConnection()
        val stmt = conn.prepareStatement("DELETE FROM appointment_searches WHERE id = ?")

        stmt.use { statement ->
            statement.setLong(1, id)
            return statement.executeUpdate() > 0
        }
    }

    fun updateLastChecked(id: Long, checkedAt: LocalDateTime, nextCheckAt: LocalDateTime?) {
        val conn = getConnection()
        val stmt = conn.prepareStatement(
            "UPDATE appointment_searches SET last_checked_at = ?, next_check_at = ? WHERE id = ?"
        )

        stmt.use { statement ->
            statement.setString(1, checkedAt.toString())
            statement.setString(2, nextCheckAt?.toString())
            statement.setLong(3, id)
            statement.executeUpdate()
        }
    }

    fun toggleActive(id: Long, isActive: Boolean): Boolean {
        val conn = getConnection()
        val stmt = conn.prepareStatement(
            "UPDATE appointment_searches SET is_active = ? WHERE id = ?"
        )

        stmt.use { statement ->
            statement.setBoolean(1, isActive)
            statement.setLong(2, id)
            return statement.executeUpdate() > 0
        }
    }

    private fun mapResultSetToSearch(rs: ResultSet): AppointmentSearch? {
        return try {
            val timeRangeStart = rs.getString("start_time")
            val timeRangeEnd = rs.getString("end_time")

            AppointmentSearch(
                id = rs.getLong("id"),
                name = rs.getString("name"),
                searchType = SearchType.valueOf(rs.getString("search_type")),
                appointmentCategory = AppointmentCategory.valueOf(rs.getString("appointment_category")),
                location = SearchLocation(
                    city = rs.getString("city"),
                    postalCode = rs.getString("postal_code"),
                    street = rs.getString("street"),
                    radiusKm = rs.getInt("radius_km")
                ),
                dateRange = DateRange(
                    startDate = LocalDate.parse(rs.getString("start_date")),
                    endDate = LocalDate.parse(rs.getString("end_date"))
                ),
                timeRange = if (timeRangeStart != null && timeRangeEnd != null) {
                    TimeRange(
                        startTime = LocalTime.parse(timeRangeStart),
                        endTime = LocalTime.parse(timeRangeEnd)
                    )
                } else null,
                isActive = rs.getBoolean("is_active"),
                checkIntervalMinutes = rs.getInt("check_interval_minutes"),
                maxSearchDurationDays = rs.getInt("max_search_duration_days"),
                matchCriteria = MatchCriteria.valueOf(rs.getString("match_criteria")),
                notificationSettings = NotificationSettings(
                    emailEnabled = rs.getBoolean("email_enabled"),
                    emailAddress = rs.getString("email_address"),
                    telegramEnabled = rs.getBoolean("telegram_enabled"),
                    telegramChatId = rs.getString("telegram_chat_id"),
                    desktopNotificationEnabled = rs.getBoolean("desktop_notification_enabled"),
                    notifyOnNewSlot = rs.getBoolean("notify_on_new_slot"),
                    notifyOnBetterSlot = rs.getBoolean("notify_on_better_slot")
                ),
                createdAt = rs.getString("created_at")?.let { 
                    LocalDateTime.parse(it) 
                } ?: LocalDateTime(2024, 1, 1, 0, 0, 0),
                lastCheckedAt = rs.getString("last_checked_at")?.let { 
                    LocalDateTime.parse(it) 
                },
                nextCheckAt = rs.getString("next_check_at")?.let { 
                    LocalDateTime.parse(it) 
                }
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
