package dev.sebas1705.careereditor.data.repository

import dev.sebas1705.careereditor.data.api.CareerApiClient
import dev.sebas1705.careereditor.data.api.DEFAULT_BASE_URL
import dev.sebas1705.careereditor.data.model.*
import dev.sebas1705.careereditor.security.SecureKeys
import dev.sebas1705.careereditor.security.SecureStorage

class CareerRepository(private val secureStorage: SecureStorage = SecureStorage()) {

    private fun buildClient(): CareerApiClient {
        val token = secureStorage.get(SecureKeys.API_TOKEN)
        val baseUrl = secureStorage.get(SecureKeys.API_BASE_URL) ?: DEFAULT_BASE_URL
        return CareerApiClient(baseUrl = baseUrl, token = token)
    }

    suspend fun getPersonal() = buildClient().getPersonal()
    suspend fun getJobs() = buildClient().getJobs()
    suspend fun getProjects() = buildClient().getProjects()
    suspend fun getSkills() = buildClient().getSkills()
    suspend fun getEducation() = buildClient().getEducation()
    suspend fun getCertifications() = buildClient().getCertifications()
    suspend fun getSoftSkills() = buildClient().getSoftSkills()

    suspend fun validateConnection(): Boolean = buildClient().validateConnection()

    fun saveToken(token: String) = secureStorage.put(SecureKeys.API_TOKEN, token.trim())
    fun getToken(): String? = secureStorage.get(SecureKeys.API_TOKEN)
    fun clearToken() = secureStorage.remove(SecureKeys.API_TOKEN)

    fun saveBaseUrl(url: String) = secureStorage.put(SecureKeys.API_BASE_URL, url.trim())
    fun getBaseUrl(): String = secureStorage.get(SecureKeys.API_BASE_URL) ?: DEFAULT_BASE_URL

    suspend fun updatePersonal(personal: Personal) = buildClient().updatePersonal(personal)
    suspend fun updateJob(job: Job) = buildClient().updateJob(job)
    suspend fun updateProject(project: Project) = buildClient().updateProject(project)
    suspend fun updateSkill(skill: Skill) = buildClient().updateSkill(skill)
    suspend fun updateEducation(education: Education) = buildClient().updateEducation(education)
    suspend fun updateCertification(cert: Certification) = buildClient().updateCertification(cert)
    suspend fun updateSoftSkill(skill: SoftSkill) = buildClient().updateSoftSkill(skill)
}
