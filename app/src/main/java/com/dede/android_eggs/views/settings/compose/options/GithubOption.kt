package com.dede.android_eggs.views.settings.compose.options

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dede.android_eggs.R
import com.dede.android_eggs.api.request.GithubRequests
import com.dede.android_eggs.ui.composes.icons.Github
import com.dede.android_eggs.util.CustomTabsBrowser
import com.dede.android_eggs.util.launchCatchable
import com.dede.android_eggs.views.settings.compose.basic.Option
import com.dede.android_eggs.views.settings.compose.basic.imageVectorIconBlock
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun GithubOption(
//    viewModel: GithubViewModel = viewModel()
) {
    val context = LocalContext.current
    Option(
        leadingIcon = imageVectorIconBlock(
            imageVector = Icons.Github,
            contentDescription = stringResource(R.string.label_github)
        ),
        title = stringResource(R.string.label_github),
        desc = stringResource(R.string.url_github),
        trailingContent = imageVectorIconBlock(imageVector = Icons.Rounded.Star),
//        trailingContent = {
//            Column(
//                modifier = Modifier.animateContentSize(alignment = Alignment.TopCenter),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Icon(
//                    imageVector = Icons.Rounded.Star,
//                    contentDescription = null,
//                    tint = Color(0xFFDFAA39)
//                )
//                if (viewModel.starCount != null) {
//                    Text(
//                        text = viewModel.starCount!!,
//                        style = MaterialTheme.typography.labelSmall,
//                        fontWeight = FontWeight.Bold,
//                        modifier = Modifier
//                            .background(
//                                MaterialTheme.colorScheme.secondaryContainer,
//                                RoundedCornerShape(10.dp)
//                            )
//                            .padding(horizontal = 4.dp)
//                    )
//                }
//            }
//        },
        onClick = {
            CustomTabsBrowser.launchUrl(context, R.string.url_github)
        }
    )
}

@HiltViewModel
class GithubViewModel @Inject constructor(val githubRequests: GithubRequests) : ViewModel() {

    var starCount by mutableStateOf<String?>(null)
        private set

    init {
        getRepositoryInfo()
    }

    private fun getRepositoryInfo() {
        viewModelScope.launchCatchable {
            val repositoryInfo = githubRequests.getRepositoryInfo()
            if (repositoryInfo != null) {
                val stargazersCount = repositoryInfo.stargazersCount
                starCount = if (stargazersCount >= 1000) {
                    "%.1f".format(stargazersCount / 1000f)
                } else {
                    stargazersCount.toString()
                }
            }
        }
    }
}
