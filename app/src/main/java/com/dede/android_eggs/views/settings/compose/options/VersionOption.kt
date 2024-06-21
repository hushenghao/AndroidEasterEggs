package com.dede.android_eggs.views.settings.compose.options

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.NavigateNext
import androidx.compose.material.icons.outlined.NewReleases
import androidx.compose.material.icons.rounded.Upgrade
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.api.upgrade.Github
import com.dede.android_eggs.api.upgrade.UpgradeChecker
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.launchCatchable
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.OptionShapes
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@Composable
fun VersionOption(
    viewModel: VersionViewModel = viewModel()
) {
    val context = LocalContext.current
    val latestVersion by viewModel.latestVersion.observeAsState()
    val haveUpgrade = remember(viewModel.upgradeChecker, latestVersion) {
        viewModel.upgradeChecker.haveUpgrade(latestVersion)
    }
    val scale = if (!haveUpgrade) {
        1f
    } else {
        val infiniteTransition =
            rememberInfiniteTransition(label = "UpgradeScaleInfiniteTransition")
        val value by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "UpgradeScaleInfiniteAnimation"
        )
        value
    }
    Option(
        modifier = Modifier.scale(scale),
        shape = OptionShapes.firstShape(),
        leadingIcon = imageVectorIconBlock(imageVector = Icons.Outlined.NewReleases),
        title = stringResource(
            R.string.label_version,
            BuildConfig.VERSION_NAME,
            BuildConfig.VERSION_CODE
        ),
        desc = BuildConfig.GIT_HASH,
        trailingContent = {
            Crossfade(
                targetState = haveUpgrade,
                label = "VersionNavigateCrossfade"
            ) {
                if (it) {
                    Icon(
                        imageVector = Icons.Rounded.Upgrade,
                        contentDescription = null,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
                                RoundedCornerShape(50)
                            )
                            .padding(4.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.NavigateNext,
                        contentDescription = null,
                    )
                }
            }
        },
        onClick = {
            val version = latestVersion
            if (haveUpgrade && version != null) {
                CustomTabsBrowser.launchUrl(context, version.upgradeUrl!!.toUri())
            } else {
                CustomTabsBrowser.launchUrl(
                    context,
                    context.getString(R.string.url_github_commit, BuildConfig.GIT_HASH).toUri()
                )
            }
        }
    )
}

@HiltViewModel
class VersionViewModel @Inject constructor(
    @Github
    val upgradeChecker: UpgradeChecker
) : ViewModel() {

    private val _latestVersion: MutableLiveData<UpgradeChecker.Version> = MutableLiveData(null)

    val latestVersion: LiveData<UpgradeChecker.Version> = _latestVersion

    init {
        getLatestVersion()
    }

    private fun getLatestVersion() {
        viewModelScope.launchCatchable {
            _latestVersion.value = upgradeChecker.getLatestVersion()
        }
    }
}
