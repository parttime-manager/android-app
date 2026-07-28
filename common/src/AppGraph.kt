package com.example.ptmanagercommon

import androidx.lifecycle.findViewTreeViewModelStoreOwner
import com.example.ptmanagercommon.data.repository.RemoteAppRepository
import com.example.ptmanagercommon.data.Network
import com.example.ptmanagercommon.presentation.viewmodel.NotificationsViewModel
import com.example.ptmanagercommon.presentation.viewmodel.AppViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

object AppGraph {
    val repository = RemoteAppRepository { Network.api }
    private val factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass == NotificationsViewModel::class.java) {
                return NotificationsViewModel(repository) as T
            }
            require(modelClass == AppViewModel::class.java)
            return AppViewModel(repository) as T
        }
    }
    fun notifications(owner: ViewModelStoreOwner) = ViewModelProvider(owner, factory)[NotificationsViewModel::class.java]
    fun model(owner: ViewModelStoreOwner): AppViewModel = ViewModelProvider(owner, factory)[AppViewModel::class.java]
}

internal val androidx.appcompat.app.AppCompatActivity.screenModel get() = AppGraph.model(this)
internal val androidx.fragment.app.Fragment.screenModel get() = AppGraph.model(this)
internal val android.view.View.screenModel get() = AppGraph.model(requireNotNull(findViewTreeViewModelStoreOwner()))
