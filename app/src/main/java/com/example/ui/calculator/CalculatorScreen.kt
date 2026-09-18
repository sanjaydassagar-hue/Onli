package com.example.ui.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.GoldAccent
import java.text.DecimalFormat
import kotlin.math.pow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen() {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) } // 0 = SIP, 1 = Profit/Loss, 2 = Risk-Reward

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "বিনিয়োগ ক্যালকুলেটর",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("এসআইপি (SIP)") },
                    modifier = Modifier.testTag("tab_sip")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("লাভ ও ক্ষতি") },
                    modifier = Modifier.testTag("tab_profit_loss")
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("রিস্ক ও রিওয়ার্ড") },
                    modifier = Modifier.testTag("tab_risk_reward")
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> SipCalculatorSection()
                    1 -> ProfitLossCalculatorSection()
                    2 -> RiskRewardCalculatorSection()
                }
            }
        }
    }
}

@Composable
fun SipCalculatorSection() {
    var monthlyAmountStr by rememberSaveable { mutableStateOf("5000") }
    var returnRateStr by rememberSaveable { mutableStateOf("12") }
    var yearsStr by rememberSaveable { mutableStateOf("10") }

    val monthlyAmount = monthlyAmountStr.toDoubleOrNull() ?: 0.0
    val returnRate = returnRateStr.toDoubleOrNull() ?: 0.0
    val years = yearsStr.toIntOrNull() ?: 0

    val months = years * 12
    val monthlyRate = (returnRate / 100.0) / 12.0

    val totalInvested = monthlyAmount * months
    val totalFutureValue = if (monthlyRate > 0 && months > 0) {
        monthlyAmount * ((1 + monthlyRate).pow(months) - 1) / monthlyRate * (1 + monthlyRate)
    } else {
        totalInvested
    }
    val estimatedReturns = totalFutureValue - totalInvested

    val formatter = DecimalFormat("#,##,###")

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "নিয়মিত এসআইপি (SIP) গণনা",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "চক্রবৃদ্ধি হারে দীর্ঘমেয়াদে আপনার অর্থ কত বৃদ্ধি পাবে",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = monthlyAmountStr,
                    onValueChange = { monthlyAmountStr = it },
                    label = { Text("মাসিক বিনিয়োগ (টাকা)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = returnRateStr,
                    onValueChange = { returnRateStr = it },
                    label = { Text("প্রত্যাশিত বার্ষিক মুনাফার হার (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = yearsStr,
                    onValueChange = { yearsStr = it },
                    label = { Text("বিনিয়োগের মেয়াদ (বছর)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "সম্ভাব্য ভবিষ্যৎ তহবিল",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "৳ ${formatter.format(totalFutureValue.toLong())}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("মোট আসল বিনিয়োগ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Text("৳ ${formatter.format(totalInvested.toLong())}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("প্রত্যাশিত নিট লাভ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                        Text("৳ ${formatter.format(estimatedReturns.toLong())}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = BullishGreen)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "* এটি একটি আনুমানিক শিক্ষামূলক হিসাব। বাজার ঝুঁকি সাপেক্ষ হওয়ায় প্রকৃত রিটার্ন ভিন্ন হতে পারে।",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun ProfitLossCalculatorSection() {
    var buyPriceStr by rememberSaveable { mutableStateOf("120") }
    var sellPriceStr by rememberSaveable { mutableStateOf("145") }
    var quantityStr by rememberSaveable { mutableStateOf("500") }

    val buyPrice = buyPriceStr.toDoubleOrNull() ?: 0.0
    val sellPrice = sellPriceStr.toDoubleOrNull() ?: 0.0
    val quantity = quantityStr.toIntOrNull() ?: 0

    val totalBuy = buyPrice * quantity
    val totalSell = sellPrice * quantity
    val profitLoss = totalSell - totalBuy
    val profitPercent = if (totalBuy > 0) (profitLoss / totalBuy) * 100 else 0.0
    val isProfit = profitLoss >= 0

    val formatter = DecimalFormat("#,##,###.##")

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ট্রেড লাভ-ক্ষতি নির্ণয়",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = buyPriceStr,
                    onValueChange = { buyPriceStr = it },
                    label = { Text("ক্রয় মূল্য প্রতি শেয়ার (টাকা)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = sellPriceStr,
                    onValueChange = { sellPriceStr = it },
                    label = { Text("বিক্রয় মূল্য প্রতি শেয়ার (টাকা)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("শেয়ারের সংখ্যা (Quantity)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isProfit) BullishGreen.copy(alpha = 0.12f) else BearishRed.copy(alpha = 0.12f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isProfit) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (isProfit) BullishGreen else BearishRed,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isProfit) "মোট সম্ভাব্য লাভ (Profit)" else "মোট সম্ভাব্য ক্ষতি (Loss)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isProfit) BullishGreen else BearishRed
                    )
                }

                Text(
                    text = "৳ ${formatter.format(profitLoss)} (${DecimalFormat("#,##0.00").format(profitPercent)}%)",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isProfit) BullishGreen else BearishRed,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("মোট ক্রয় মূল্য", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("৳ ${formatter.format(totalBuy)}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("মোট বিক্রয় মূল্য", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("৳ ${formatter.format(totalSell)}", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun RiskRewardCalculatorSection() {
    var entryStr by rememberSaveable { mutableStateOf("100") }
    var stopLossStr by rememberSaveable { mutableStateOf("94") }
    var targetStr by rememberSaveable { mutableStateOf("115") }

    val entry = entryStr.toDoubleOrNull() ?: 0.0
    val stopLoss = stopLossStr.toDoubleOrNull() ?: 0.0
    val target = targetStr.toDoubleOrNull() ?: 0.0

    val risk = entry - stopLoss
    val reward = target - entry
    val ratio = if (risk > 0) reward / risk else 0.0
    val isGoodRatio = ratio >= 2.0

    val df = DecimalFormat("#,##0.00")

    Column(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "রিস্ক-টু-রিওয়ার্ড অনুপাত (1:R Ratio)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "ট্রেডে প্রবেশের আগে ঝুঁকি ও সম্ভাব্য লাভের সঠিক হিসাব",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = entryStr,
                    onValueChange = { entryStr = it },
                    label = { Text("শেয়ার কেনার দর (Entry Price)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = stopLossStr,
                    onValueChange = { stopLossStr = it },
                    label = { Text("স্টপ লস দর (Stop Loss)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = targetStr,
                    onValueChange = { targetStr = it },
                    label = { Text("টার্গেট বিক্রয় দর (Target Price)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ratio Display
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isGoodRatio) EmeraldContainer else GoldAccent.copy(alpha = 0.15f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "রিস্ক ও রিওয়ার্ড অনুপাত",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "১ : ${df.format(ratio)}",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isGoodRatio) BullishGreen else GoldAccent
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isGoodRatio) {
                        "✓ দুর্দান্ত ট্রেড সেটআপ! অনুপাত ১:২ বা তার বেশি হওয়ায় ট্রেডটি ইতিবাচক ঝুঁকির আওতায় রয়েছে।"
                    } else {
                        "⚠ সতর্ক হোন: অনুপাত ১:২ এর নিচে। ১ টাকা লাভের জন্য প্রয়োজনের চেয়ে বেশি ঝুঁকি নিচ্ছেন।"
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("শেয়ার প্রতি ঝুঁকি", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("৳ ${df.format(risk.coerceAtLeast(0.0))}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BearishRed)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("শেয়ার প্রতি সম্ভাব্য লাভ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("৳ ${df.format(reward.coerceAtLeast(0.0))}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = BullishGreen)
                    }
                }
            }
        }
    }
}
