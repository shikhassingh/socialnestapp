package com.android.app.socialnestapplication.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.app.socialnestapplication.ui.theme.SocialNestApplicationTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(48.dp),
        enabled = enabled && !loading,
        shape = MaterialTheme.shapes.large,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.height(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(text)
        }
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(48.dp),
        enabled = enabled,
        shape = MaterialTheme.shapes.large
    ) {
        Text(text)
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialNestButtonPreview() {
    SocialNestApplicationTheme {
        PrimaryButton(text = "Log In", onClick = {})
    }
}
