@file:OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)

package com.dede.android_eggs.views.stats

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dede.android_eggs.composable.ScrollableModalBottomSheet
import com.dede.android_eggs.data.EasterEggRepository
import com.dede.android_eggs.egg_stats.EggStatsRepository
import com.dede.android_eggs.egg_stats.EggStatsSummary
import com.dede.android_eggs.navigation.OverlayContentProvider
import com.dede.android_eggs.navigation.OverlayRoute
import com.dede.android_eggs.views.main.compose.EasterEggLogo
import com.dede.android_eggs.views.main.util.EasterEggHelp
import com.dede.basic.provider.EasterEgg
import com.dede.basic.toast
import com.dede.basic.utils.AppLocaleDateFormatter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import java.util.Date
import javax.inject.Inject
import kotlinx.coroutines.launch
import com.dede.android_eggs.resources.R as StringsR

@Module
@InstallIn(SingletonComponent::class)
object EggStatsOverlayProvider : OverlayContentProvider {

    override val route: OverlayRoute = OverlayRoute.EggStatsDialog

    @Provides
    @IntoSet
    fun provide(): OverlayContentProvider = this

    @Composable
    override fun Content(onDismiss: () -> Unit) {
        EggStatsDialog(onDismiss = onDismiss)
    }
}

@HiltViewModel
class EggStatsViewModel @Inject constructor(
    repository: EasterEggRepository,
    private val eggStatsRepository: EggStatsRepository,
) : ViewModel() {
    val easterEggs = repository.pureEasterEggs
    val summaries = eggStatsRepository.observeSummaries()

    suspend fun clearStats() {
        eggStatsRepository.clear()
    }
}

private data class EggStatsRow(
    val egg: EasterEgg,
    val stats: EggStatsSummary?,
)

private enum class EggStatsSort(@StringRes val labelRes: Int) {
    MOST_PLAYED(StringsR.string.egg_stats_sort_most_played),
    RECENTLY_PLAYED(StringsR.string.egg_stats_sort_recently_played),
    FIRST_OPENED(StringsR.string.egg_stats_first_opened),
}

private val EggStatsSorts = listOf(
    EggStatsSort.MOST_PLAYED,
    EggStatsSort.RECENTLY_PLAYED,
    EggStatsSort.FIRST_OPENED,
)

@Composable
fun EggStatsDialog(
    onDismiss: () -> Unit,
    viewModel: EggStatsViewModel = hiltViewModel(),
) {
    val summaries by viewModel.summaries
        .collectAsStateWithLifecycle()
    val rows = remember(viewModel.easterEggs, summaries) {
        val summaryMap = summaries.associateBy { it.eggKey }
        viewModel.easterEggs.map { EggStatsRow(it, summaryMap[it.id]) }
    }
    val playedCount = rows.count { (it.stats?.playCount ?: 0) > 0 }
    val totalPlays = rows.sumOf { it.stats?.playCount ?: 0 }

    var sortIndex by rememberSaveable { mutableStateOf(0) }
    val sort = EggStatsSorts[sortIndex]
    val sortedRows = remember(rows, sort) {
        rows.sortedWith(sort.comparator())
    }

    val scrollState = rememberLazyListState()
    val bottomPadding = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showClearDialog by rememberSaveable { mutableStateOf(false) }

    ScrollableModalBottomSheet(
        onDismissRequest = onDismiss,
        scrollState = scrollState,
    ) {
        LazyColumn(
            state = scrollState,
            contentPadding = PaddingValues(bottom = bottomPadding),
            modifier = Modifier.fillMaxWidth(),
        ) {
            item("header") {
                EggStatsHeader(
                    playedCount = playedCount,
                    totalCount = rows.size,
                    totalPlays = totalPlays,
                    onClear = { showClearDialog = true },
                )
            }
            item("sort") {
                // FilterChip wraps its 32.dp container in a 48.dp touch target, so the
                // default 8.dp line spacing would leave a 24.dp visual gap when the chips
                // wrap. A 40.dp target with zero line spacing keeps an 8.dp visual gap
                // while retaining a touch area larger than the chip itself.
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 40.dp) {
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp),
                    ) {
                        EggStatsSorts.forEachIndexed { index, option ->
                            FilterChip(
                                selected = index == sortIndex,
                                onClick = { sortIndex = index },
                                label = { Text(stringResource(option.labelRes)) },
                            )
                        }
                    }
                }
            }
            items(
                items = sortedRows,
                key = { row ->
                    row.egg.id
                },
                contentType = { "egg" },
            ) { row ->
                EggStatsItem(row = row)
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text(text = stringResource(StringsR.string.egg_stats_clear_confirm_title)) },
            text = { Text(text = stringResource(StringsR.string.egg_stats_clear_confirm_message)) },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text(text = stringResource(android.R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        scope.launch {
                            viewModel.clearStats()
                            context.toast(StringsR.string.egg_stats_cleared)
                        }
                    },
                ) {
                    Text(text = stringResource(android.R.string.ok))
                }
            },
        )
    }
}

private fun EggStatsSort.comparator(): Comparator<EggStatsRow> {
    return when (this) {
        EggStatsSort.MOST_PLAYED -> {
            compareByDescending<EggStatsRow> { it.stats?.playCount ?: 0 }
                .thenByDescending { it.egg.primaryVersion.fullApiLevel }
        }
        EggStatsSort.RECENTLY_PLAYED -> {
            compareByDescending<EggStatsRow> { it.stats?.lastPlayedAt ?: 0L }
                .thenByDescending { it.egg.primaryVersion.fullApiLevel }
        }
        EggStatsSort.FIRST_OPENED -> {
            compareBy<EggStatsRow> { it.stats?.firstOpenedAt?.takeIf { t -> t > 0 } ?: Long.MAX_VALUE }
                .thenByDescending { it.egg.primaryVersion.fullApiLevel }
        }
    }
}

@Composable
private fun EggStatsHeader(
    playedCount: Int,
    totalCount: Int,
    totalPlays: Int,
    onClear: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(StringsR.string.label_egg_stats),
                style = typography.headlineSmall,
                modifier = Modifier.weight(1f),
            )
            if (playedCount > 0) {
                TextButton(onClick = onClear) {
                    Text(text = stringResource(StringsR.string.egg_stats_clear))
                }
            }
        }
        Text(
            text = stringResource(
                StringsR.string.egg_stats_summary_format,
                playedCount,
                totalCount,
                totalPlays,
            ),
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun EggStatsItem(row: EggStatsRow) {
    val context = LocalContext.current
    val egg = row.egg
    val stats = row.stats
    val version = remember(egg.versions) {
        EasterEggHelp.VersionFormatter.create(egg.versions, egg.nicknameRes)
            .format(context)
    }
    val apiLevel = remember(egg.versions) {
        EasterEggHelp.ApiLevelFormatter.create(egg.versions).format(context)
    }
    val dateFormat = remember(LocalConfiguration.current) {
        AppLocaleDateFormatter.getInstance("MMM d, yyyy")
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EasterEggLogo(egg = egg, size = 44.dp)
        Column(
            modifier = Modifier
                .padding(start = 14.dp)
                .weight(1f),
        ) {
            Text(
                text = stringResource(egg.nameRes),
                style = typography.titleMedium,
            )
            Text(
                text = "$version · $apiLevel",
                style = typography.bodySmall,
                color = colorScheme.onSurfaceVariant,
            )
            if (stats == null || stats.playCount == 0) {
                Text(
                    text = stringResource(StringsR.string.egg_stats_never_played),
                    style = typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
            } else {
                StatsLine(
                    label = stringResource(StringsR.string.egg_stats_first_opened),
                    value = dateFormat.format(Date(stats.firstOpenedAt)),
                )
                StatsLine(
                    label = stringResource(StringsR.string.egg_stats_last_played),
                    value = dateFormat.format(Date(stats.lastPlayedAt)),
                )
                StatsLine(
                    label = stringResource(StringsR.string.egg_stats_play_count),
                    value = stats.playCount.toString(),
                )
            }
        }
    }
}

@Composable
private fun StatsLine(label: String, value: String) {
    Row(modifier = Modifier.padding(top = 4.dp)) {
        Text(
            text = label,
            style = typography.bodySmall,
            color = colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            style = typography.bodySmall,
        )
    }
}
