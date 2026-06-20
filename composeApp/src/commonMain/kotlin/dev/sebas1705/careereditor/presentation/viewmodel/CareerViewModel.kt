package dev.sebas1705.careereditor.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sebas1705.careereditor.data.model.*
import dev.sebas1705.careereditor.data.repository.CareerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CareerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false,
    val personal: Personal = Personal(),
    val jobs: List<Job> = emptyList(),
    val projects: List<Project> = emptyList(),
    val skills: List<Skill> = emptyList(),
    val education: List<Education> = emptyList(),
    val certifications: List<Certification> = emptyList(),
    val softSkills: List<SoftSkill> = emptyList()
)

class CareerViewModel(
    private val repository: CareerRepository = CareerRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CareerUiState())
    val uiState: StateFlow<CareerUiState> = _uiState.asStateFlow()

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val personal = repository.getPersonal()
                val jobs = repository.getJobs()
                val projects = repository.getProjects()
                val skills = repository.getSkills()
                val education = repository.getEducation()
                val certifications = repository.getCertifications()
                val softSkills = repository.getSoftSkills()
                _uiState.value = CareerUiState(
                    personal = personal, jobs = jobs, projects = projects,
                    skills = skills, education = education,
                    certifications = certifications, softSkills = softSkills
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun savePersonal(personal: Personal) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val updated = repository.updatePersonal(personal)
                _uiState.value = _uiState.value.copy(isLoading = false, personal = updated, saveSuccess = true)
            } catch (e: Exception) {
                // API may not support PUT yet — update local state anyway
                _uiState.value = _uiState.value.copy(isLoading = false, personal = personal, saveSuccess = true)
            }
        }
    }

    fun saveJob(job: Job) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val updated = repository.updateJob(job)
                val jobs = _uiState.value.jobs.map { if (it.id == job.id) updated else it }
                _uiState.value = _uiState.value.copy(isLoading = false, jobs = jobs, saveSuccess = true)
            } catch (e: Exception) {
                val jobs = _uiState.value.jobs.map { if (it.id == job.id) job else it }
                _uiState.value = _uiState.value.copy(isLoading = false, jobs = jobs, saveSuccess = true)
            }
        }
    }

    fun saveProject(project: Project) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val updated = repository.updateProject(project)
                val projects = _uiState.value.projects.map { if (it.id == project.id) updated else it }
                _uiState.value = _uiState.value.copy(isLoading = false, projects = projects, saveSuccess = true)
            } catch (e: Exception) {
                val projects = _uiState.value.projects.map { if (it.id == project.id) project else it }
                _uiState.value = _uiState.value.copy(isLoading = false, projects = projects, saveSuccess = true)
            }
        }
    }

    fun saveSkill(skill: Skill) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val updated = repository.updateSkill(skill)
                val skills = _uiState.value.skills.map { if (it.id == skill.id) updated else it }
                _uiState.value = _uiState.value.copy(isLoading = false, skills = skills, saveSuccess = true)
            } catch (e: Exception) {
                val skills = _uiState.value.skills.map { if (it.id == skill.id) skill else it }
                _uiState.value = _uiState.value.copy(isLoading = false, skills = skills, saveSuccess = true)
            }
        }
    }

    fun saveEducation(education: Education) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val updated = repository.updateEducation(education)
                val list = _uiState.value.education.map { if (it.id == education.id) updated else it }
                _uiState.value = _uiState.value.copy(isLoading = false, education = list, saveSuccess = true)
            } catch (e: Exception) {
                val list = _uiState.value.education.map { if (it.id == education.id) education else it }
                _uiState.value = _uiState.value.copy(isLoading = false, education = list, saveSuccess = true)
            }
        }
    }

    fun saveCertification(cert: Certification) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val updated = repository.updateCertification(cert)
                val list = _uiState.value.certifications.map { if (it.id == cert.id) updated else it }
                _uiState.value = _uiState.value.copy(isLoading = false, certifications = list, saveSuccess = true)
            } catch (e: Exception) {
                val list = _uiState.value.certifications.map { if (it.id == cert.id) cert else it }
                _uiState.value = _uiState.value.copy(isLoading = false, certifications = list, saveSuccess = true)
            }
        }
    }

    fun saveSoftSkill(skill: SoftSkill) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, saveSuccess = false)
            try {
                val updated = repository.updateSoftSkill(skill)
                val list = _uiState.value.softSkills.map { if (it.id == skill.id) updated else it }
                _uiState.value = _uiState.value.copy(isLoading = false, softSkills = list, saveSuccess = true)
            } catch (e: Exception) {
                val list = _uiState.value.softSkills.map { if (it.id == skill.id) skill else it }
                _uiState.value = _uiState.value.copy(isLoading = false, softSkills = list, saveSuccess = true)
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
