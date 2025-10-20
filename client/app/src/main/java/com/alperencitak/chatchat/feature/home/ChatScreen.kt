package com.alperencitak.chatchat.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(hostIp: String) {
    var username by remember { mutableStateOf("") }
    var joined by remember { mutableStateOf(false) }
    val chatViewModel: ChatViewModel = hiltViewModel()
    val messages by chatViewModel.messages.collectAsState()
    var input by remember { mutableStateOf("") }

    val algorithms = listOf("Caesar", "RSA", "Vigenère")
    var expanded by remember { mutableStateOf(false) }
    var selectedAlgorithm by remember { mutableStateOf("Caesar") }

    if (!joined) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Kullanıcı adı") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedAlgorithm,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Şifreleme Algoritması") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    algorithms.forEach { algo ->
                        DropdownMenuItem(
                            text = { Text(algo) },
                            onClick = {
                                selectedAlgorithm = algo
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (username.isNotBlank()) {
                        chatViewModel.connectWebSocket(hostIp, username)
                        joined = true
                    }
                },
                enabled = username.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Katıl")
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                messages.forEach { msg ->
                    val prefix = if (msg.username == username) "Me: " else "${msg.username}: "
                    Text("$prefix${msg.text}")
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = {
                    if (input.isNotBlank()) {
                        chatViewModel.sendMessage(input)
                        input = ""
                    }
                }) {
                    Text("Gönder")
                }
            }
        }
    }
}
