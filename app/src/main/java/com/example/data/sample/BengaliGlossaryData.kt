package com.example.data.sample

import com.example.data.model.GlossaryTerm

object BengaliGlossaryData {
    val terms: List<GlossaryTerm> = listOf(
        GlossaryTerm(
            id = "term_bull_market",
            termEn = "Bull Market",
            termBn = "বুল মার্কেট (তেজি বাজার)",
            categoryBn = "মার্কেট অবস্থা",
            definitionBn = "যখন বাজারে সামগ্রিকভাবে অধিকাংশ শেয়ারের দাম দীর্ঘ সময় ধরে ক্রমাগত বাড়তে থাকে এবং বিনিয়োগকারীদের মধ্যে প্রচুর আশাবাদ ও আস্থা কাজ করে।",
            realLifeExampleBn = "যখন অর্থনীতি শক্তিশালী থাকে এবং জিডিপি প্রবৃদ্ধি ভালো হয়, তখন বাজারে বুল রান দেখতে পাওয়া যায়।"
        ),
        GlossaryTerm(
            id = "term_bear_market",
            termEn = "Bear Market",
            termBn = "বিয়ার মার্কেট (মন্দা বাজার)",
            categoryBn = "মার্কেট অবস্থা",
            definitionBn = "যখন বাজারে শেয়ারের দাম দীর্ঘ সময় ধরে ২০% বা তার বেশি কমতে থাকে এবং ভীতি ও বিক্রির চাপ বেশি থাকে। ভাল্লুক যেমন থাবা দিয়ে নিচে ফেলে, তেমনি বিয়ার মার্কেট নিম্নমুখী নির্দেশ করে।",
            realLifeExampleBn = "করোনা মহামারীর শুরুতে বিশ্ববাজারে বিয়ারিশ প্রবণতা দেখা গিয়েছিল।"
        ),
        GlossaryTerm(
            id = "term_ipo",
            termEn = "Initial Public Offering (IPO)",
            termBn = "আইপিও (প্রাথমিক শেয়ার প্রস্তাব)",
            categoryBn = "মার্কেট বেসিক্স",
            definitionBn = "কোনো প্রাইভেট কোম্পানি যখন প্রথমবারের মতো সাধারণ জনগণের কাছে তাদের শেয়ার বিক্রি করে অর্থ সংগ্রহ করে স্টক এক্সচেঞ্জে তালিকাভুক্ত হয়।",
            realLifeExampleBn = "গ্রামীণফোন বা রবি যখন সাধারণ বিনিয়োগকারীদের জন্য শেয়ার বিক্রি শুরু করেছিল, সেটি ছিল তাদের আইপিও।"
        ),
        GlossaryTerm(
            id = "term_blue_chip",
            termEn = "Blue Chip Stocks",
            termBn = "ব্লু চিপ শেয়ার",
            categoryBn = "কোম্পানির ধরণ",
            definitionBn = "দীর্ঘদিন ধরে সুনামের সাথে ব্যবসা করে আসা আর্থিকভাবে অত্যন্ত শক্তিশালী, লাভজনক এবং নিয়মিত লভ্যাংশ প্রদানকারী শীর্ষস্থানীয় কোম্পানিগুলোর শেয়ার।",
            realLifeExampleBn = "স্কয়ার ফার্মা, বাটা শু, ইউনিলিভার, অ্যাপল বা মাইক্রোসফটের মতো কোম্পানি।"
        ),
        GlossaryTerm(
            id = "term_dividend",
            termEn = "Dividend",
            termBn = "ডিভিডেন্ড (লভ্যাংশ)",
            categoryBn = "রিটার্ন",
            definitionBn = "কোম্পানি তার অর্জিত নিট মুনাফার একটি অংশ শেয়ারহোল্ডারদের মধ্যে বিতরণ করে। এটি নগদ টাকা (Cash Dividend) বা বিনামূল্যে অতিরিক্ত শেয়ার (Stock/Bonus Dividend) হতে পারে।",
            realLifeExampleBn = "কোনো কোম্পানি ১০% ক্যাশ ডিভিডেন্ড ঘোষণা করলে প্রতি ১০ টাকার ফেসভ্যালুর বিপরীতে ১ টাকা নগদ লাভ পাওয়া যায়।"
        ),
        GlossaryTerm(
            id = "term_stop_loss",
            termEn = "Stop Loss",
            termBn = "স্টপ লস (ক্ষতি নিয়ন্ত্রণ)",
            categoryBn = "ট্রেডিং টুল",
            definitionBn = "ট্রেডারদের একটি পূর্বনির্ধারিত অর্ডার যেখানে শেয়ারের দাম কাঙ্ক্ষিত মূল্যের নিচে নামলে স্বয়ংক্রিয়ভাবে বিক্রি হয়ে যায়, যাতে মূলধনের বড় ক্ষতি না ঘটে।",
            realLifeExampleBn = "১০০ টাকায় কেনা শেয়ার ৯৫ টাকায় স্টপ লস সেট করে রাখলে ৫ টাকার বেশি লোকসান হবে না।"
        ),
        GlossaryTerm(
            id = "term_market_cap",
            termEn = "Market Capitalization",
            termBn = "মার্কেট ক্যাপ (বাজার মূলধন)",
            categoryBn = "মূল্যায়ন",
            definitionBn = "কোম্পানির মোট ইস্যুকৃত শেয়ার সংখ্যাকে প্রতি শেয়ারের বর্তমান বাজার মূল্য দিয়ে গুণ করলে যা পাওয়া যায়, তাই হলো সেই কোম্পানির বাজার মূলধন।",
            realLifeExampleBn = "১ কোটি শেয়ারের বর্তমান দাম ৫০ টাকা হলে কোম্পানির মার্কেট ক্যাপ ৫০ কোটি টাকা।"
        ),
        GlossaryTerm(
            id = "term_pe_ratio",
            termEn = "Price-to-Earnings Ratio (P/E)",
            termBn = "পি/ই রেশিও",
            categoryBn = "ফান্ডামেন্টাল",
            definitionBn = "শেয়ারের বর্তমান বাজার মূল্যকে বিগত ১২ মাসের প্রতি শেয়ারের আয় (EPS) দিয়ে ভাগ করলে P/E পাওয়া যায়। এর মাধ্যমে শেয়ারটি সস্তা নাকি দামি তা বোঝা যায়।",
            realLifeExampleBn = "শেয়ার প্রাইস ১২০ টাকা এবং EPS ১২ টাকা হলে P/E হবে ১০।"
        ),
        GlossaryTerm(
            id = "term_support_resistance",
            termEn = "Support & Resistance",
            termBn = "সাপোর্ট ও রেজিস্ট্যান্স",
            categoryBn = "টেকনিক্যাল",
            definitionBn = "সাপোর্ট হলো চার্টের মেঝে যেখানে এসে দামের পতন সাধারণত থামে। রেজিস্ট্যান্স হলো চার্টের ছাদ যেখানে পৌঁছে শেয়ারের বৃদ্ধি বাধার মুখে পড়ে।",
            realLifeExampleBn = "একটি শেয়ার বারবার ৫০ টাকায় এসে বাউন্স করলে ৫০ টাকা হলো শক্তিশালী সাপোর্ট।"
        ),
        GlossaryTerm(
            id = "term_rsi",
            termEn = "Relative Strength Index (RSI)",
            termBn = "আরএসআই নির্দেশক",
            categoryBn = "টেকনিক্যাল",
            definitionBn = "০ থেকে ১০০ স্কেলের একটি মোমেন্টাম ইন্ডিকেটর। ৭০ এর উপরে গেলে ওভারবট (বেশি কেনা) এবং ৩০ এর নিচে নামলে ওভারসোল্ড (বেশি বিক্রি) ধরা হয়।",
            realLifeExampleBn = "RSI ২৫ থাকলে শেয়ারটি সস্তা দামে ঘুরে দাঁড়ানোর সম্ভাবনা তৈরি হয়।"
        ),
        GlossaryTerm(
            id = "term_portfolio",
            termEn = "Portfolio",
            termBn = "পোর্টফোলিও (বিনিয়োগ খাতা)",
            categoryBn = "বিনিয়োগ",
            definitionBn = "একজন বিনিয়োগকারীর অধীনে থাকা সমস্ত শেয়ার, বন্ড, মিউচুয়াল ফান্ড বা অন্যান্য আর্থিক সম্পদের সামগ্রিক সংগ্রহকে পোর্টফোলিও বলে।",
            realLifeExampleBn = "আপনার একাউন্টে যদি ফার্মা, ব্যাংক ও সিমেন্ট খাতের ১০টি শেয়ার থাকে, সেটি আপনার পোর্টফোলিও।"
        ),
        GlossaryTerm(
            id = "term_bo_account",
            termEn = "BO Account (Beneficiary Owner)",
            termBn = "বিও একাউন্ট",
            categoryBn = "ব্যবহারিক",
            definitionBn = "সেন্ট্রাল ডিপোজিটরি সিস্টেমে খোলা এমন একটি একাউন্ট যেখানে শেয়ার সার্টিফিকেট কাগজের বদলে ডিজিটাল ইলেকট্রনিক হিসেবে জমা থাকে।",
            realLifeExampleBn = "যেমন বিকাশ বা ব্যাংক একাউন্টে টাকা থাকে, তেমনি বিও একাউন্টে কেনা শেয়ারগুলো নিরাপদে থাকে।"
        )
    )
}
