package dev.sebas1705.careereditor.data.api

import dev.sebas1705.careereditor.data.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://career-api.sebas1705.workers.dev"

class CareerApiClient {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    val httpClient = HttpClient {
        install(ContentNegotiation) { json(json) }
        install(Logging) { level = LogLevel.INFO }
    }

    suspend fun getPersonal(): Personal = httpClient.get("$BASE_URL/personal").body()
    suspend fun getJobs(): List<Job> = httpClient.get("$BASE_URL/jobs").body()
    suspend fun getProjects(): List<Project> = httpClient.get("$BASE_URL/projects").body()
    suspend fun getSkills(): List<Skill> = httpClient.get("$BASE_URL/skills").body()
    suspend fun getEducation(): List<Education> = httpClient.get("$BASE_URL/education").body()
    suspend fun getCertifications(): List<Certification> = httpClient.get("$BASE_URL/certifications").body()
    suspend fun getSoftSkills(): List<SoftSkill> = httpClient.get("$BASE_URL/soft-skills").body()

    suspend fun updatePersonal(personal: Personal): Personal =
        httpClient.put("$BASE_URL/personal") {
            contentType(ContentType.Application.Json)
            setBody(personal)
        }.body()

    suspend fun updateJob(job: Job): Job =
        httpClient.put("$BASE_URL/jobs/${job.id}") {
            contentType(ContentType.Application.Json)
            setBody(job)
        }.body()

    suspend fun updateProject(project: Project): Project =
        httpClient.put("$BASE_URL/projects/${project.id}") {
            contentType(ContentType.Application.Json)
            setBody(project)
        }.body()

    suspend fun updateSkill(skill: Skill): Skill =
        httpClient.put("$BASE_URL/skills/${skill.id}") {
            contentType(ContentType.Application.Json)
            setBody(skill)
        }.body()

    suspend fun updateEducation(education: Education): Education =
        httpClient.put("$BASE_URL/education/${education.id}") {
            contentType(ContentType.Application.Json)
            setBody(education)
        }.body()

    suspend fun updateCertification(cert: Certification): Certification =
        httpClient.put("$BASE_URL/certifications/${cert.id}") {
            contentType(ContentType.Application.Json)
            setBody(cert)
        }.body()

    suspend fun updateSoftSkill(skill: SoftSkill): SoftSkill =
        httpClient.put("$BASE_URL/soft-skills/${skill.id}") {
            contentType(ContentType.Application.Json)
            setBody(skill)
        }.body()
}
