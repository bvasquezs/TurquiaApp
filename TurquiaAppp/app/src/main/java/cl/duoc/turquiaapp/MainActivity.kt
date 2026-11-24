package cl.duoc.turquiaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cl.duoc.turquiaapp.ui.navigation.AppNavigation
import cl.duoc.turquiaapp.ui.theme.TurquiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TurquiaTheme {
                AppNavigation()
            }
        }
    }
}
