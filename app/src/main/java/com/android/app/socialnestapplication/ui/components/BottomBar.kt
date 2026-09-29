package com.android.app.socialnestapplication.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Photo
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.app.socialnestapplication.ui.theme.SocialNestApplicationTheme

enum class BottomNavigation(
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    Home("Home", Icons.Outlined.Home, Icons.Filled.Home),
    Members("Members", Icons.Outlined.People, Icons.Filled.People),
    Groups("Groups", Icons.Outlined.Groups, Icons.Filled.Groups),
    Photos("Photos", Icons.Outlined.Photo, Icons.Filled.Photo),
    Profile("Profile", Icons.Outlined.Person, Icons.Filled.Person)
}

@Composable
fun BottomBar(
    selected: BottomNavigation,
    onSelect: (BottomNavigation) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        BottomNavigation.entries.forEach { destination ->
            val isSelected = destination == selected
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelect(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) },
                alwaysShowLabel = true,
                modifier = Modifier.testTag("bottom_${destination.name}"),
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SocialNestBottomBarPreview() {
    SocialNestApplicationTheme {
        BottomBar(selected = BottomNavigation.Home, onSelect = {})
    }
}
