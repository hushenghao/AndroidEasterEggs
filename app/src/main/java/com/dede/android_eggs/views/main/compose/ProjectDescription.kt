package com.dede.android_eggs.views.main.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dede.android_eggs.BuildConfig
import com.dede.android_eggs.R
import com.dede.android_eggs.resources.R as StringsR

@Preview(showBackground = true)
@Composable
fun ProjectDescription() {
    var konfettiState by LocalKonfettiState.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            EasterEggLogo(
                res = R.mipmap.ic_launcher_round,
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(false)
                    ) {
                        konfettiState = true
                    },
                contentDescription = stringResource(id = StringsR.string.app_name)
            )
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Text(
                    text = stringResource(StringsR.string.app_name),
                    style = typography.titleSmall,
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(
                        R.string.label_version,
                        BuildConfig.VERSION_NAME,
                        BuildConfig.VERSION_CODE
                    ),
                    style = typography.bodySmall
                )
            }
        }
        Text(
            text = stringResource(StringsR.string.label_project_desc),
            modifier = Modifier.padding(top = 20.dp),
            style = typography.bodyMedium
        )
        Wavy(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 26.dp),
            color = colorScheme.secondaryContainer,
            strokeWidth = 0.9.dp,
            amplitude = 0.7f,
            wavelength = 26.dp,
        )
    }
}