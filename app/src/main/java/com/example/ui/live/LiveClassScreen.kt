package com.example.ui.live

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LiveClassEntity
import com.example.data.repository.AppRepository
import com.example.ui.theme.BearishRed
import com.example.ui.theme.BullishGreen
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.GoldAccent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveClassScreen(
    repository: AppRepository
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState()
    val username = currentUser?.username ?: ""

    val liveClasses by repository.getLiveClasses().collectAsState(initial = emptyList())
    val subscription by repository.getUserSubscription(username).collectAsState(initial = null)

    var currentFee by remember { mutableIntStateOf(repository.getCourseFee()) }
    var currentUpi by remember { mutableStateOf(repository.getUpiId()) }

    var showPaymentDialog by remember { mutableStateOf(false) }
    var showAddClassDialog by remember { mutableStateOf(false) }
    var classToEdit by remember { mutableStateOf<LiveClassEntity?>(null) }
    var showFeeAdminDialog by remember { mutableStateOf(false) }

    val isSubscribed = subscription != null && subscription?.isApproved == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "লাইভ ক্লাস (Google Meet)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = if (isSubscribed) "প্রিমিয়াম অ্যাক্সেস সক্রিয় (৩ মাস)" else "৩ মাসের মেম্বারশিপ: ₹$currentFee",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showFeeAdminDialog = true },
                        modifier = Modifier.testTag("admin_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "কোর্স ফি ও UPI পরিবর্তন",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddClassDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_live_class_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "নতুন লাইভ ক্লাস শিডিউল যোগ করুন")
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Subscription Status Card / Payment Banner
            item {
                if (isSubscribed) {
                    SubscriptionActiveCard(
                        subscription = subscription!!,
                        onRenewOrInfo = { showPaymentDialog = true }
                    )
                } else {
                    PaymentRequiredCard(
                        courseFee = currentFee,
                        upiId = currentUpi,
                        onOpenPayment = { showPaymentDialog = true }
                    )
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "আসন্ন লাইভ ক্লাস শিডিউল",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "গুগল মিটের মাধ্যমে সরাসরি প্রশ্ন ও উত্তর",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${liveClasses.size} টি শিডিউল",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            if (liveClasses.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoCall,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "বর্তমানে কোনো লাইভ ক্লাস নেই",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "নিচের '+' বোতামে চাপ দিয়ে নতুন লাইভ ক্লাস ও গুগল মিট লিংক যোগ করুন।",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showAddClassDialog = true },
                                modifier = Modifier.testTag("add_first_class_button")
                            ) {
                                Text("ক্লাস যোগ করুন")
                            }
                        }
                    }
                }
            } else {
                items(liveClasses, key = { it.id }) { liveClass ->
                    LiveClassCard(
                        liveClass = liveClass,
                        isSubscribed = isSubscribed,
                        onJoinMeet = {
                            if (!isSubscribed) {
                                showPaymentDialog = true
                            } else {
                                val url = if (liveClass.meetLink.startsWith("http://") || liveClass.meetLink.startsWith("https://")) {
                                    liveClass.meetLink
                                } else {
                                    "https://${liveClass.meetLink}"
                                }
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "লিংক খুলতে সমস্যা হচ্ছে: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        onEdit = { classToEdit = liveClass },
                        onDelete = {
                            coroutineScope.launch {
                                repository.deleteLiveClass(liveClass.id)
                                Toast.makeText(context, "ক্লাসটি মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }

            // Instructor & Safety Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldAccent.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = GoldAccent)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ক্লাস সংক্রান্ত নিয়মাবলী",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "পেমেন্ট সফল হলে আপনার অ্যাকাউন্ট তাৎক্ষণিক ৩ মাসের জন্য লাইভ ক্লাসের লিঙ্ক আনলক করবে। গুগল মিট অ্যাপে সরাসরি অংশগ্রহণ করতে পারবেন।",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    // Payment Dialog
    if (showPaymentDialog) {
        PaymentSubscriptionDialog(
            courseFee = currentFee,
            upiId = currentUpi,
            onDismiss = { showPaymentDialog = false },
            onSubmitPayment = { utr ->
                coroutineScope.launch {
                    val result = repository.recordPaymentSubscription(username, utr, currentFee)
                    if (result.isSuccess) {
                        Toast.makeText(context, "পেমেন্ট সফল হয়েছে! ৩ মাসের মেম্বারশিপ সক্রিয়।", Toast.LENGTH_LONG).show()
                        showPaymentDialog = false
                    } else {
                        Toast.makeText(context, result.exceptionOrNull()?.message ?: "ত্রুটি হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    // Add/Edit Live Class Dialog
    if (showAddClassDialog || classToEdit != null) {
        val editing = classToEdit
        AddEditClassDialog(
            existing = editing,
            onDismiss = {
                showAddClassDialog = false
                classToEdit = null
            },
            onSave = { title, topic, schedule, meetLink, instructor ->
                coroutineScope.launch {
                    if (editing == null) {
                        repository.addLiveClass(title, topic, schedule, meetLink, instructor)
                        Toast.makeText(context, "নতুন লাইভ ক্লাস সফলভাবে যোগ করা হয়েছে", Toast.LENGTH_SHORT).show()
                    } else {
                        repository.updateLiveClass(editing.id, title, topic, schedule, meetLink)
                        Toast.makeText(context, "ক্লাস শিডিউল আপডেট করা হয়েছে", Toast.LENGTH_SHORT).show()
                    }
                    showAddClassDialog = false
                    classToEdit = null
                }
            }
        )
    }

    // Fee and UPI ID Admin Setting Dialog
    if (showFeeAdminDialog) {
        FeeAdminSettingDialog(
            initialFee = currentFee,
            initialUpi = currentUpi,
            onDismiss = { showFeeAdminDialog = false },
            onSave = { newFee, newUpi ->
                repository.setCourseFee(newFee)
                repository.setUpiId(newUpi)
                currentFee = newFee
                currentUpi = newUpi
                showFeeAdminDialog = false
                Toast.makeText(context, "কোর্স ফি ও UPI আইডি আপডেট হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
fun SubscriptionActiveCard(
    subscription: com.example.data.local.SubscriptionEntity,
    onRenewOrInfo: () -> Unit
) {
    val expiryDateFormat = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
    val expiryDateStr = expiryDateFormat.format(Date(subscription.expiresAt))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BullishGreen.copy(alpha = 0.12f)),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, BullishGreen.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BullishGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "প্রিমিয়াম সাবস্ক্রিপশন সক্রিয়",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = BullishGreen
                    )
                    Text(
                        text = "মেয়াদ: ৩ মাস ($expiryDateStr পর্যন্ত)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "₹${subscription.courseFee}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BullishGreen
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "রেফারেন্স UTR: ${subscription.transactionRef} (PhonePe UPI: ${subscription.paymentUpiId})",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun PaymentRequiredCard(
    courseFee: Int,
    upiId: String,
    onOpenPayment: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoldAccent)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "৩ মাসের মেম্বারশিপ আবশ্যক",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "লাইভ ক্লাসে যোগ দিন",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "সরাসরি গুগল মিটে ৩ মাস যাবত লাইভ মার্কেট ক্লাস ও ট্রেডিং ডিসকাশন।",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "₹$courseFee",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "৩ মাসের জন্য",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.6f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = Color(0xFF5F259F), // PhonePe purple brand vibe
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "PhonePe UPI ID:",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = upiId,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("PhonePe UPI ID", upiId)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "UPI ID কপি করা হয়েছে: $upiId", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "কপি করুন",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onOpenPayment,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("pay_and_unlock_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "পেমেন্ট সম্পন্ন করে আনলক করুন (₹$courseFee)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun LiveClassCard(
    liveClass: LiveClassEntity,
    isSubscribed: Boolean,
    onJoinMeet: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE8F0FE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoCall,
                            contentDescription = null,
                            tint = Color(0xFF1A73E8), // Google Meet Blue
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = liveClass.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = liveClass.instructorName,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "সম্পাদনা",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "মুছুন",
                            tint = BearishRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = liveClass.topicDescription,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Schedule Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ক্লাস শিডিউল: ${liveClass.dateSchedule}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Google Meet Link Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        tint = if (isSubscribed) Color(0xFF1A73E8) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isSubscribed) liveClass.meetLink else "পেমেন্টের পর গুগল মিট লিংক দৃশ্যমান হবে",
                        fontSize = 12.sp,
                        color = if (isSubscribed) Color(0xFF1A73E8) else MaterialTheme.colorScheme.outline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (isSubscribed) {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Meet Link", liveClass.meetLink)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "মিট লিংক কপি হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "কপি করুন",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button (Join Meet or Pay)
            Button(
                onClick = onJoinMeet,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("join_live_class_button_${liveClass.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSubscribed) Color(0xFF00796B) else MaterialTheme.colorScheme.primary
                )
            ) {
                if (isSubscribed) {
                    Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("গুগল মিটে ক্লাসে ঢুকুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ক্লাসে ঢুকতে পেমেন্ট করুন", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PaymentSubscriptionDialog(
    courseFee: Int,
    upiId: String,
    onDismiss: () -> Unit,
    onSubmitPayment: (String) -> Unit
) {
    val context = LocalContext.current
    var utrInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Payment,
                    contentDescription = null,
                    tint = Color(0xFF5F259F),
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text("কোর্স ফি ও পেমেন্ট বিবরণ", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("৩ মাসের জন্য লাইভ ক্লাস অ্যাক্সেস", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Payment Instructions Box
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("মোট কোর্স ফি:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("₹$courseFee", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = BullishGreen)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("মেয়াদ:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("৩ মাস (৯০ দিন)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        Text("PhonePe / Google Pay / Paytm UPI:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = upiId,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("PhonePe UPI ID", upiId)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "UPI ID কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text("কপি করুন", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        // One-tap PhonePe / UPI App intent launcher
                        OutlinedButton(
                            onClick = {
                                val upiUri = Uri.parse("upi://pay?pa=$upiId&pn=SanjayDas&am=$courseFee&cu=INR&tn=ShareMarket3MonthsCourse")
                                val intent = Intent(Intent.ACTION_VIEW, upiUri)
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "PhonePe বা UPI অ্যাপ খুঁজে পাওয়া যায়নি। দয়া করে ম্যানুয়ালি UPI ID তে পেমেন্ট করুন।", Toast.LENGTH_LONG).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("PhonePe / UPI অ্যাপ দিয়ে পে করুন", fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "পেমেন্ট শেষে PhonePe-এর ১২ সংখ্যার UTR বা Transaction ID নিচে দিন:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = utrInput,
                    onValueChange = {
                        utrInput = it
                        errorMessage = ""
                    },
                    placeholder = { Text("যেমন: 426819284712") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("utr_input_field"),
                    isError = errorMessage.isNotBlank()
                )

                if (errorMessage.isNotBlank()) {
                    Text(
                        text = errorMessage,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (utrInput.trim().length < 4) {
                        errorMessage = "অনুগ্রহ করে সঠিক UTR বা লেনদেন নম্বর লিখুন"
                    } else {
                        onSubmitPayment(utrInput.trim())
                    }
                },
                modifier = Modifier.testTag("submit_payment_button")
            ) {
                Text("যাচাই করে মেম্বারশিপ সক্রিয় করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun AddEditClassDialog(
    existing: LiveClassEntity?,
    onDismiss: () -> Unit,
    onSave: (title: String, topic: String, schedule: String, meetLink: String, instructor: String) -> Unit
) {
    var title by remember { mutableStateOf(existing?.title ?: "") }
    var topic by remember { mutableStateOf(existing?.topicDescription ?: "") }
    var schedule by remember { mutableStateOf(existing?.dateSchedule ?: "রবিবার ও বুধবার, রাত ৯:০০ টা") }
    var meetLink by remember { mutableStateOf(existing?.meetLink ?: "https://meet.google.com/abc-defg-hij") }
    var instructor by remember { mutableStateOf(existing?.instructorName ?: "সঞ্জয় দাস (মেন্টর)") }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existing == null) "নতুন লাইভ ক্লাস শিডিউল" else "লাইভ ক্লাস সম্পাদনা",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("ক্লাসের শিরোনাম / বিষয়") },
                    placeholder = { Text("যেমন: চার্ট রিডিং ও লাইভ ট্রেডিং স্ট্র্যাটেজি") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("class_title_input")
                )

                OutlinedTextField(
                    value = schedule,
                    onValueChange = { schedule = it },
                    label = { Text("ক্লাসের শিডিউল (দিন ও সময়)") },
                    placeholder = { Text("যেমন: শুক্রবার ও শনিবার, রাত ৮:৩০ টা") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("class_schedule_input")
                )

                OutlinedTextField(
                    value = meetLink,
                    onValueChange = { meetLink = it },
                    label = { Text("গুগল মিট লিংক (Google Meet Link)") },
                    placeholder = { Text("https://meet.google.com/...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("class_meet_link_input")
                )

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("বিস্তারিত বিবরণ / কী শেখানো হবে") },
                    placeholder = { Text("ক্লাসে ক্যান্ডেলস্টিক, RSI এবং লাইভ সাপোর্ট-রেজিস্ট্যান্স দেখানো হবে।") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("class_topic_input")
                )

                OutlinedTextField(
                    value = instructor,
                    onValueChange = { instructor = it },
                    label = { Text("শিক্ষক / মেন্টরের নাম") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("class_instructor_input")
                )

                if (errorText.isNotBlank()) {
                    Text(
                        text = errorText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || meetLink.isBlank() || schedule.isBlank()) {
                        errorText = "শিরোনাম, শিডিউল এবং গুগল মিট লিংক আবশ্যক"
                    } else {
                        onSave(title, topic, schedule, meetLink, instructor)
                    }
                },
                modifier = Modifier.testTag("save_class_button")
            ) {
                Text("সংরক্ষণ করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}

@Composable
fun FeeAdminSettingDialog(
    initialFee: Int,
    initialUpi: String,
    onDismiss: () -> Unit,
    onSave: (newFee: Int, newUpi: String) -> Unit
) {
    var feeText by remember { mutableStateOf(initialFee.toString()) }
    var upiText by remember { mutableStateOf(initialUpi) }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("কোর্স ফি ও UPI সেটিংস", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "এখানে আপনি কোর্সের ৩ মাসের ফি বাড়ানো বা কমানো এবং পেমেন্টের PhonePe UPI আইডি পরিবর্তন করতে পারেন।",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = feeText,
                    onValueChange = { feeText = it },
                    label = { Text("কোর্স ফি (টাকা/রুপি)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_fee_input"),
                    leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) }
                )

                OutlinedTextField(
                    value = upiText,
                    onValueChange = { upiText = it },
                    label = { Text("PhonePe UPI ID") },
                    placeholder = { Text("8967254968@ybl") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("admin_upi_input")
                )

                if (errorText.isNotBlank()) {
                    Text(
                        text = errorText,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedFee = feeText.toIntOrNull()
                    if (parsedFee == null || parsedFee <= 0) {
                        errorText = "দয়া করে সঠিক ফি উল্লেখ করুন"
                    } else if (upiText.isBlank() || !upiText.contains("@")) {
                        errorText = "সঠিক UPI ID দিন (যেমন 8967254968@ybl)"
                    } else {
                        onSave(parsedFee, upiText.trim())
                    }
                },
                modifier = Modifier.testTag("admin_save_fee_button")
            ) {
                Text("আপডেট করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল")
            }
        }
    )
}
