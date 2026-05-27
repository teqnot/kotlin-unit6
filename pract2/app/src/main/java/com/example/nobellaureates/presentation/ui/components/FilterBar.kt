package com.example.nobellaureates.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nobellaureates.domain.model.NobelCategory
import com.example.nobellaureates.presentation.viewmodel.NobelUiFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBar(
    currentFilter: NobelUiFilter,
    availableYears: List<Int>,
    availableCategories: List<NobelCategory>,
    onFilterChange: (NobelUiFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedYear by remember { mutableStateOf(false) }
    var expandedCategory by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expandedYear,
            onExpandedChange = { expandedYear = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = currentFilter.year?.toString() ?: "Все годы",
                onValueChange = {},
                readOnly = true,
                label = { Text("Год") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedYear) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedYear,
                onDismissRequest = { expandedYear = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Все годы") },
                    onClick = {
                        onFilterChange(currentFilter.copy(year = null))
                        expandedYear = false
                    }
                )
                availableYears.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year.toString()) },
                        onClick = {
                            onFilterChange(currentFilter.copy(year = year))
                            expandedYear = false
                        }
                    )
                }
            }
        }

        ExposedDropdownMenuBox(
            expanded = expandedCategory,
            onExpandedChange = { expandedCategory = it },
            modifier = Modifier.weight(1f)
        ) {
            OutlinedTextField(
                value = currentFilter.category.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Категория") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                availableCategories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName) },
                        onClick = {
                            onFilterChange(currentFilter.copy(category = category))
                            expandedCategory = false
                        }
                    )
                }
            }
        }
    }
}