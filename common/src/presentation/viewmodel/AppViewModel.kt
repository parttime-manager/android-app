package com.example.ptmanagercommon.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.example.ptmanagercommon.domain.repository.AppRepository

class AppViewModel(repository: AppRepository) : ViewModel(), AppRepository by repository
