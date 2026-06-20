package dev.sebas1705.careereditor.data.repository

import dev.sebas1705.careereditor.data.api.CareerApiClient
import dev.sebas1705.careereditor.data.model.*

class CareerRepository(private val api: CareerApiClient = CareerApiClient()) {
    suspend fun getPersonal() = api.getPersonal()
    suspend fun getJobs() = api.getJobs()
    suspend fun getProjects() = api.getProjects()
    suspend fun getSkills() = api.getSkills()
    suspend fun getEducation() = api.getEducation()
    suspend fun getCertifications() = api.getCertifications()
    suspend fun getSoftSkills() = api.getSoftSkills()

    suspend fun updatePersonal(personal: Personal) = api.updatePersonal(personal)
    suspend fun updateJob(job: Job) = api.updateJob(job)
    suspend fun updateProject(project: Project) = api.updateProject(project)
    suspend fun updateSkill(skill: Skill) = api.updateSkill(skill)
    suspend fun updateEducation(education: Education) = api.updateEducation(education)
    suspend fun updateCertification(cert: Certification) = api.updateCertification(cert)
    suspend fun updateSoftSkill(skill: SoftSkill) = api.updateSoftSkill(skill)
}
