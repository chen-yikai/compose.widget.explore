package eliaschen.dev.composewidgetlearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import eliaschen.dev.composewidgetlearn.ui.theme.ComposewidgetlearnTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = getDatabase(this)
        val eventViewModel = EventViewModel(db)
        setContent {
            ComposewidgetlearnTheme {
                Home(eventViewModel)
            }
        }
    }
}