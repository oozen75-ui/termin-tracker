package com.termintracker.repository

import com.termintracker.database.DatabaseManager
import com.termintracker.model.Language
import com.termintracker.model.PersonalInfo
import java.sql.ResultSet

class PersonalInfoRepository {
    private val conn = DatabaseManager.getConnection()
    private val DEFAULT_ID = 1L

    fun save(personalInfo: PersonalInfo): Boolean {
        val existing = getById(DEFAULT_ID)
        return if (existing == null) {
            insert(personalInfo)
        } else {
            update(personalInfo)
        }
    }

    private fun insert(personalInfo: PersonalInfo): Boolean {
        val sql = """
            INSERT INTO personal_info 
            (id, first_name, last_name, birth_date, email, phone, street, house_number, 
             postal_code, city, district, country, preferred_language)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """
        conn.prepareStatement(sql).use { stmt ->
            stmt.setLong(1, DEFAULT_ID)
            stmt.setString(2, personalInfo.firstName)
            stmt.setString(3, personalInfo.lastName)
            stmt.setString(4, personalInfo.birthDate)
            stmt.setString(5, personalInfo.email)
            stmt.setString(6, personalInfo.phone)
            stmt.setString(7, personalInfo.defaultAddress.street)
            stmt.setString(8, personalInfo.defaultAddress.houseNumber)
            stmt.setString(9, personalInfo.defaultAddress.postalCode)
            stmt.setString(10, personalInfo.defaultAddress.city)
            stmt.setString(11, personalInfo.defaultAddress.district)
            stmt.setString(12, personalInfo.defaultAddress.country)
            stmt.setString(13, personalInfo.preferredLanguage.code)
            return stmt.executeUpdate() > 0
        }
    }

    private fun update(personalInfo: PersonalInfo): Boolean {
        val sql = """
            UPDATE personal_info SET
            first_name = ?, last_name = ?, birth_date = ?, email = ?, phone = ?, 
            street = ?, house_number = ?, postal_code = ?, city = ?, district = ?, 
            country = ?, preferred_language = ?
            WHERE id = ?
        """
        conn.prepareStatement(sql).use { stmt ->
            stmt.setString(1, personalInfo.firstName)
            stmt.setString(2, personalInfo.lastName)
            stmt.setString(3, personalInfo.birthDate)
            stmt.setString(4, personalInfo.email)
            stmt.setString(5, personalInfo.phone)
            stmt.setString(6, personalInfo.defaultAddress.street)
            stmt.setString(7, personalInfo.defaultAddress.houseNumber)
            stmt.setString(8, personalInfo.defaultAddress.postalCode)
            stmt.setString(9, personalInfo.defaultAddress.city)
            stmt.setString(10, personalInfo.defaultAddress.district)
            stmt.setString(11, personalInfo.defaultAddress.country)
            stmt.setString(12, personalInfo.preferredLanguage.code)
            stmt.setLong(13, DEFAULT_ID)
            return stmt.executeUpdate() > 0
        }
    }

    fun getById(id: Long): PersonalInfo? {
        conn.prepareStatement("SELECT * FROM personal_info WHERE id = ?").use { stmt ->
            stmt.setLong(1, id)
            val rs = stmt.executeQuery()
            if (rs.next()) {
                return mapResultSetToPersonalInfo(rs)
            }
        }
        return null
    }

    fun get(): PersonalInfo? = getById(DEFAULT_ID)

    fun getPreferredLanguage(): Language {
        return get()?.preferredLanguage ?: Language.GERMAN
    }

    private fun mapResultSetToPersonalInfo(rs: ResultSet): PersonalInfo {
        return PersonalInfo(
            id = rs.getLong("id"),
            firstName = rs.getString("first_name") ?: "",
            lastName = rs.getString("last_name") ?: "",
            birthDate = rs.getString("birth_date") ?: "",
            email = rs.getString("email") ?: "",
            phone = rs.getString("phone") ?: "",
            defaultAddress = com.termintracker.model.Address(
                street = rs.getString("street") ?: "",
                houseNumber = rs.getString("house_number") ?: "",
                postalCode = rs.getString("postal_code") ?: "",
                city = rs.getString("city") ?: "",
                district = rs.getString("district") ?: "",
                country = rs.getString("country") ?: "Deutschland"
            ),
            preferredLanguage = Language.fromCode(rs.getString("preferred_language") ?: "de")
        )
    }
}
