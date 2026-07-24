package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AeoBottomNavBar
import com.example.ui.components.AeoTopBar
import com.example.ui.components.WalletModal
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AeoTab
import com.example.ui.viewmodel.AeoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AeoApp()
            }
        }
    }
}

@Composable
fun AeoApp(viewModel: AeoViewModel = viewModel()) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val showWalletModal by viewModel.showWalletModal.collectAsState()
    val showBuyCryptoDialog by viewModel.showBuyCryptoDialog.collectAsState()
    val showDevDonationDialog by viewModel.showDevDonationDialog.collectAsState()

    Scaffold(
        topBar = {
            AeoTopBar(
                selectedTab = selectedTab,
                currentUser = currentUser,
                onWalletClick = { viewModel.showWalletModal.value = true },
                onSearchClick = { /* Global search */ },
                onAddClick = {
                    when (selectedTab) {
                        AeoTab.FUNDING -> viewModel.showCreateFundingDialog.value = true
                        AeoTab.MARKETPLACE -> viewModel.postItem("중고 물품", "기타", 10000L, "설명")
                        AeoTab.FEED -> viewModel.showCreatePostDialog.value = true
                        AeoTab.CHAT -> viewModel.searchUsers("@")
                        AeoTab.PROFILE -> viewModel.showWalletModal.value = true
                    }
                }
            )
        },
        bottomBar = {
            AeoBottomNavBar(
                selectedTab = selectedTab,
                onTabSelected = { tab -> viewModel.selectTab(tab) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                AeoTab.FUNDING -> FundingScreen(viewModel = viewModel)
                AeoTab.MARKETPLACE -> MarketplaceScreen(viewModel = viewModel)
                AeoTab.FEED -> FeedScreen(viewModel = viewModel)
                AeoTab.CHAT -> ChatScreen(viewModel = viewModel)
                AeoTab.PROFILE -> ProfileScreen(viewModel = viewModel)
            }
        }
    }

    if (showWalletModal) {
        WalletModal(
            currentUser = currentUser,
            onDismiss = { viewModel.showWalletModal.value = false },
            onBuyCryptoClick = { viewModel.showBuyCryptoDialog.value = true }
        )
    }

    if (showBuyCryptoDialog) {
        com.example.ui.components.BuyCryptoDialog(
            onDismiss = { viewModel.showBuyCryptoDialog.value = false },
            onConfirm = { amt -> viewModel.buyCrypto(amt) }
        )
    }

    if (showDevDonationDialog) {
        com.example.ui.components.DevDonationDialog(
            onDismiss = { viewModel.showDevDonationDialog.value = false },
            onConfirm = { net, amt -> viewModel.donateToDev(net, amt) }
        )
    }
}
