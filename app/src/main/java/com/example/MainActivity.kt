package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.MainViewModelFactory
import com.example.ui.DashboardScreen
import com.example.ui.SalesEntryScreen
import com.example.ui.CustomersScreen
import com.example.ui.ImportersScreen
import com.example.ui.PaymentsScreen

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val app = application as FishCommissionApp
    setContent {
      MyApplicationTheme {
        val viewModel: MainViewModel = viewModel(factory = MainViewModelFactory(app.repository))
        val navController = rememberNavController()
        
        Scaffold(
          bottomBar = { AppBottomNavigation(navController) },
          contentWindowInsets = WindowInsets.systemBars
        ) { innerPadding ->
          NavHost(
            navController = navController,
            startDestination = "sales",
            modifier = Modifier.padding(innerPadding)
          ) {
            composable("dashboard") { DashboardScreen(viewModel) }
            composable("sales") { SalesEntryScreen(viewModel) }
            composable("customers") { CustomersScreen(viewModel) }
            composable("importers") { ImportersScreen(viewModel) }
            composable("payments") { PaymentsScreen(viewModel) }
          }
        }
      }
    }
  }
}

@Composable
fun AppBottomNavigation(navController: NavHostController) {
  val items = listOf(
    NavigationItem("Dashboard", "dashboard", Icons.Default.Home),
    NavigationItem("Sales", "sales", Icons.Default.ShoppingCart),
    NavigationItem("Customers", "customers", Icons.Default.Person),
    NavigationItem("Importers", "importers", Icons.Default.Build),
    NavigationItem("Payments", "payments", Icons.Default.CheckCircle)
  )
  
  var selectedItem by remember { mutableStateOf(1) } // Default to sales

  NavigationBar {
    items.forEachIndexed { index, item ->
      NavigationBarItem(
        icon = { Icon(item.icon, contentDescription = item.title) },
        label = { Text(item.title) },
        selected = selectedItem == index,
        onClick = {
          selectedItem = index
          navController.navigate(item.route) {
            popUpTo(navController.graph.startDestinationId) { saveState = true }
            launchSingleTop = true
            restoreState = true
          }
        }
      )
    }
  }
}

data class NavigationItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
