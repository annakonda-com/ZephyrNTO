package ru.myitschool.work.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import ru.myitschool.work.R
import ru.myitschool.work.core.TestIds
import ru.myitschool.work.ui.nav.MainScreenDestination

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    navController: NavController
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect {
            navController.navigate(MainScreenDestination)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.auth_title),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        when (val currentState = state) {
            is AuthState.Data -> Content(viewModel, currentState)
            is AuthState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp)
                )
            }
        }
    }
}

@Composable
private fun Content(
    viewModel: AuthViewModel,
    state: AuthState.Data
) {
    var inputText by remember { mutableStateOf("") }
    Spacer(modifier = Modifier.size(16.dp))
    TextField(
        modifier = Modifier.testTag(TestIds.Auth.CODE_INPUT).fillMaxWidth(),
        value = inputText,
        onValueChange = {
            inputText = it
            viewModel.onIntent(AuthIntent.TextInput(it))
        },
        label = { Text(stringResource(R.string.auth_label)) }
    )
    Spacer(modifier = Modifier.size(16.dp))
    Button(
        modifier = Modifier.testTag(TestIds.Auth.SIGN_BUTTON).fillMaxWidth(),
        onClick = {
            viewModel.onIntent(AuthIntent.Send(inputText))
        },
        enabled = true

    ) { Text(stringResource(R.string.auth_sign_in))


    }
}