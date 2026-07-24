package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FundingEntity
import com.example.data.local.UserEntity
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContributeDialog(
    funding: FundingEntity,
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onConfirm: (amount: Long, method: String) -> Unit
) {
    var amountText by remember { mutableStateOf("50000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "모금 코인 전송 (MetaMask)",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = funding.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1C1E)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "수신 모금 풀 스마트컨트랙트: 0x${kotlin.math.abs(funding.id.hashCode()).toString(16).padStart(8, '0')}...AO_VAULT",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                Surface(
                    color = Color.Black,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MonoMetaMaskGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "송신 지갑: MetaMask 연동계정",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = currentUser?.walletAddress ?: "0x7a83F99b2C1d445E81A912",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Text(
                    text = "기여 금액 선택 (KRW 환산)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1C1E)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("10000", "50000", "100000", "300000").forEach { preset ->
                        OutlinedButton(
                            onClick = { amountText = preset },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (amountText == preset) Color.Black else Color(0xFFE0E0E0)
                            )
                        ) {
                            Text(
                                text = "${preset.toLong() / 10000}만원",
                                fontSize = 11.sp,
                                fontWeight = if (amountText == preset) FontWeight.Bold else FontWeight.Normal,
                                color = Color.Black
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("직접 입력 (원)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = { Text("원", modifier = Modifier.padding(end = 12.dp)) },
                    singleLine = true
                )

                Text(
                    text = "🔒 모금액은 계정에 연동된 MetaMask 지갑에서 직접 전송되며, 동일 액수의 참정권(VP)과 오픈채팅 입장 권한이 부여됩니다.",
                    fontSize = 11.sp,
                    color = Color(0xFF6B7280)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toLongOrNull() ?: 50000L
                    onConfirm(amt, "METAMASK_ETH")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("메타마스크에서 코인 전송")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = Color(0xFF6B7280))
            }
        }
    )
}

@Composable
fun CreateFundingDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, target: Long, handle: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var handle by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("3000000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 지역 모금(오픈채팅) 개설", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("모금 제목") },
                    placeholder = { Text("예: 역삼동 공공 벤치 설치 project") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it },
                    label = { Text("모금 검색 아이디 (Handle)") },
                    placeholder = { Text("예: @fund_yeoksam_bench") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text("목표 모금액 (KRW)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("모금 및 공동체 설명") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )

                Text(
                    text = "🔒 모금 개설 시 암호화된 시드 문구 지갑 풀이 생성되며 목표 달성 및 약속 과반 동의 전까지 방장에게도 비밀 처리됩니다.",
                    fontSize = 11.sp,
                    color = MonoAccentSubtle
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val target = targetText.toLongOrNull() ?: 3000000L
                        onConfirm(title, desc, target, handle)
                    }
                }
            ) {
                Text("개설하기")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}

@Composable
fun CreateAgendaDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("새 안건 등록", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("안건 제목") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("안건 상세 내용 및 제안 이유") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) onConfirm(title, content)
                }
            ) {
                Text("안건 제출")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}

@Composable
fun ConsensusVoteDialog(
    fundingTitle: String,
    promiseTitle: String,
    onDismiss: () -> Unit,
    onVote: (agree: Boolean) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("자금 사용 승인 동의 투표", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "모금: $fundingTitle", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = "약속: $promiseTitle", fontSize = 13.sp)
                Text(
                    text = "목표 모금액이 달성되어 약속 시각에 도달했습니다.\n모금 자금 사용에 동의하십니까?\n참정권자 과반 동의 시 방장에게 펀딩 시드문구가 개봉됩니다.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onVote(true) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("동의 (Yes)")
                }
                OutlinedButton(onClick = { onVote(false) }) {
                    Text("비동의 (No)")
                }
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("닫기") } }
    )
}

@Composable
fun WalletModal(
    currentUser: UserEntity?,
    onDismiss: () -> Unit,
    onBuyCryptoClick: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("MetaMask DID 지갑 정보", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MonoDarkCard),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("탈중앙 식별자 (DID)", fontSize = 11.sp, color = MonoTextSecondaryDark)
                        Text(
                            text = currentUser?.did ?: "did:ao:0x7a83f99b2c1d",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MonoTextPrimaryDark,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("메타마스크 지갑 주소", fontSize = 11.sp, color = MonoTextSecondaryDark)
                        Text(
                            text = currentUser?.walletAddress ?: "0x7a83F99b2C1d445E81A912",
                            fontSize = 13.sp,
                            color = MonoTextPrimaryDark,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Button(
                    onClick = {
                        onDismiss()
                        onBuyCryptoClick()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("💳 현금/카드로 코인 구매하기 (MetaMask On-Ramp)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("네트워크", fontSize = 13.sp)
                    Text("AO Decentralized Mesh", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("메시징 프로토콜", fontSize = 13.sp)
                    Text("XMTP E2E Encrypted", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("닫기") }
        }
    )
}

@Composable
fun BuyCryptoDialog(
    onDismiss: () -> Unit,
    onConfirm: (amountKrw: Long) -> Unit
) {
    var selectedKrw by remember { mutableStateOf("50000") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("메타마스크 카드 결제 코인 구매", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "MetaMask 자체 Fiat On-Ramp 결제 모듈(MoonPay / Transak / Ramp)을 사용해 신용/체크카드로 현금 결제하여 코인을 즉시 지갑에 충전합니다.",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )

                Text(
                    text = "구매 금액 선택 (KRW -> ETH)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("30000", "50000", "100000", "300000").forEach { preset ->
                        OutlinedButton(
                            onClick = { selectedKrw = preset },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (selectedKrw == preset) Color.Black else Color(0xFFE0E0E0)
                            )
                        ) {
                            Text(
                                text = "${preset.toLong() / 10000}만원",
                                fontSize = 11.sp,
                                fontWeight = if (selectedKrw == preset) FontWeight.Bold else FontWeight.Normal,
                                color = Color.Black
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        val krw = selectedKrw.toLongOrNull() ?: 50000L
                        val ethEst = String.format("%.4f", krw / 3500000.0)
                        Text("예상 충전 수량: 약 $ethEst ETH", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("결제 수단: 신용/체크카드 (Visa, Mastercard, Apple Pay)", fontSize = 10.sp, color = Color(0xFF6B7280))
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val krw = selectedKrw.toLongOrNull() ?: 50000L
                    onConfirm(krw)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("메타마스크 카드 결제 진행")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소", color = Color(0xFF6B7280)) }
        }
    )
}

@Composable
fun DevDonationDialog(
    onDismiss: () -> Unit,
    onConfirm: (network: String, amount: Double) -> Unit
) {
    var selectedNetwork by remember { mutableStateOf("이더리움 네트워크") }
    var isCopied by remember { mutableStateOf(false) }

    val walletAddress = when (selectedNetwork) {
        "비트코인" -> "bc1qao_dev_btc_donate_9999"
        "트론 네트워크" -> "TAODevTrxDonateWallet7777"
        else -> "0xAO_DEV_ETH_DONATE_8888"
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("AO 코어 개발자 후원", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "AO 탈중앙 지배구조 및 XMTP 메시징 네트워크를 지속 개발 및 유지보수하는 개발팀을 후원합니다.",
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280)
                )

                Text(
                    text = "후원 네트워크 선택",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("비트코인", "이더리움 네트워크", "트론 네트워크").forEach { net ->
                        val isSelected = selectedNetwork == net
                        OutlinedButton(
                            onClick = {
                                selectedNetwork = net
                                isCopied = false
                            },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) Color.Black else Color(0xFFE0E0E0)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) Color.Black else Color.White
                            )
                        ) {
                            Text(
                                text = net,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color.Black
                            )
                        }
                    }
                }

                Surface(
                    color = Color(0xFF1F2937),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$selectedNetwork 지갑 주소",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        Text(
                            text = walletAddress,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = MonoMetaMaskGold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { isCopied = true },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isCopied) Color(0xFF10B981) else MonoMetaMaskGold
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCopied) Icons.Default.Check else Icons.Default.ContentCopy,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCopied) "복사 완료!" else "주소 복사",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(selectedNetwork, 0.0)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("닫기", fontWeight = FontWeight.Bold)
            }
        }
    )
}
