package dev.sebas1705.careereditor.data.api

import dev.sebas1705.careereditor.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

const val DEFAULT_BASE_URL = "https://career-api.sebas1705.workers.dev"

class CareerApiClient(
    private val baseUrl: String = DEFAULT_BASE_URL,
    private val token: String? = null
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    val httpClient = HttpClient {
        install(ContentNegotiation) { json(json) }
        install(Logging) { level = LogLevel.INFO }
        if (!token.isNullOrBlank()) {
            install(Auth) {
                bearer {
                    loadTokens { BearerTokens(token, "") }
                    sendWithoutRequest { true }
                }
            }
        }
    }

    suspend fun getLanguages(): Languages = httpClient.get("$baseUrl/languages").body()
    suspend fun getPersonal(): Personal = httpClient.get("$baseUrl/personal").body()
    suspend fun getJobs(): List<Job> = httpClient.get("$baseUrl/jobs").body()
    suspend fun getProjects(): List<Project> = httpClient.get("$baseUrl/projects").body()
    suspend fun getSkills(): List<Skill> = httpClient.get("$baseUrl/skills").body()
    suspend fun getEducation(): List<Education> = httpClient.get("$baseUrl/education").body()
    suspend fun getCertifications(): List<Certification> = httpClient.get("$baseUrl/certifications").body()
    suspend fun getSoftSkills(): List<SoftSkill> = httpClient.get("$baseUrl/soft-skills").body()

    /** Returns true if the API responds with 2xx (token + URL valid) */
    suspend fun validateConnection(): Boolean = runCatching {
        httpClient.get(baseUrl).status.isSuccess()
    }.getOrDefault(false)

    suspend fun updateLanguages(languages: Languages): Languages =
        httpClient.put("$baseUrl/languages") {
            contentType(ContentType.Application.Json); setBody(languages)
        }.body()

    suspend fun updatePersonal(personal: Personal): Personal =
        httpClient.put("$baseUrl/personal") {
            contentType(ContentType.Application.Json); setBody(personal)
        }.body()

    suspend fun updateJob(job: Job): Job =
        httpClient.patch("$baseUrl/jobs/${job.id}") {
            contentType(ContentType.Application.Json); setBody(job)
        }.body()

    suspend fun updateProject(project: Project): Project =
        httpClient.patch("$baseUrl/projects/${project.id}") {
            contentType(ContentType.Application.Json); setBody(project)
        }.body()

    suspend fun updateSkill(skill: Skill): Skill =
        httpClient.patch("$baseUrl/skills/${skill.id}") {
            contentType(ContentType.Application.Json); setBody(skill)
        }.body()

    suspend fun updateEducation(education: Education): Education =
        httpClient.patch("$baseUrl/education/${education.id}") {
            contentType(ContentType.Application.Json); setBody(education)
        }.body()

    suspend fun updateCertification(cert: Certification): Certification =
        httpClient.patch("$baseUrl/certifications/${cert.id}") {
            contentType(ContentType.Application.Json); setBody(cert)
        }.body()

    suspend fun updateSoftSkill(skill: SoftSkill): SoftSkill =
        httpClient.patch("$baseUrl/soft-skills/${skill.id}") {
            contentType(ContentType.Application.Json); setBody(skill)
        }.body()
}
