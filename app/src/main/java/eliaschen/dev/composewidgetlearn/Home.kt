package eliaschen.dev.composewidgetlearn

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(db: EventViewModel = viewModel()) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showSheet by rememberSaveable { mutableStateOf(false) }
    var showDateDialog by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf("") }
    var selectedDate by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedDateFormated by rememberSaveable { mutableStateOf<String>("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val event by db.event.observeAsState(emptyList())
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(showSheet) {
        name = ""
        selectedDate = null
    }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }, floatingActionButton = {
        FloatingActionButton(onClick = { showSheet = true }) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
        }
    }) { _ ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .statusBarsPadding()
                .padding(horizontal = 15.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
            items(event) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        it.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        it.date.let {
                            if (it > currentTimeMillis) {
                                "arrived in"
                            } else {
                                "has been"
                            }
                        },
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            it.date.let {
                                if (it > currentTimeMillis) {
                                    val diff = it - currentTimeMillis
                                    val days = diff / (1000 * 60 * 60 * 24)
                                    days.toString()
                                } else {
                                    val diff = currentTimeMillis - it
                                    val days = diff / (1000 * 60 * 60 * 24)
                                    days.toString()
                                }
                            },
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "Days",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showSheet) {
        if (showDateDialog) {
            DatePickerModal(onDateSelected = {
                selectedDate = it
                val date = Date(selectedDate!!)
                selectedDateFormated =
                    SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(date)
            }, onDismiss = {
                showDateDialog = false
            })
        }
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
            }, sheetState = sheetState
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text("Add new Event", fontWeight = FontWeight.Bold, fontSize = 25.sp)
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedTextField(value = selectedDateFormated,
                        onValueChange = { },
                        singleLine = true,
                        enabled = false,
                        label = { Text("Date") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDateDialog = true
                            })
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(modifier = Modifier.fillMaxWidth(), onClick = {
                        db.add(EventSchema(name = name, date = selectedDate!!))
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showSheet = false
                            }
                        }
                        scope.launch {
                            snackbarHostState.showSnackbar("New Event \"$name\" added")
                            name = ""
                            selectedDate = null
                            selectedDateFormated = ""
                        }
                    }) {
                        Text("Add")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit, onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
            onDismiss()
        }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DatePicker(state = datePickerState)
    }
}