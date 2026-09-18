package com.example.data.sample

import com.example.data.model.MockStock

object BengaliStockData {
    val stocks: List<MockStock> = listOf(
        MockStock(
            symbol = "SQURPHARMA",
            nameBn = "স্কয়ার ফার্মাসিউটিক্যালস",
            sectorBn = "ফার্মাসিউটিক্যালস ও হেলথকেয়ার",
            price = 216.50,
            changePercent = 1.45,
            peRatio = 14.8,
            dividendYield = 4.2,
            isBullish = true
        ),
        MockStock(
            symbol = "GP",
            nameBn = "গ্রামীণফোন লিমিটেড",
            sectorBn = "টেলিকমিউনিকেশন",
            price = 286.00,
            changePercent = -0.85,
            peRatio = 12.5,
            dividendYield = 7.1,
            isBullish = false
        ),
        MockStock(
            symbol = "BRACBANK",
            nameBn = "ব্র্যাক ব্যাংক পিএলসি",
            sectorBn = "ব্যাংকিং ও ফাইন্যান্স",
            price = 48.20,
            changePercent = 2.10,
            peRatio = 8.6,
            dividendYield = 3.8,
            isBullish = true
        ),
        MockStock(
            symbol = "BATBC",
            nameBn = "ব্রিটিশ আমেরিকান টোবাকো",
            sectorBn = "ফুড ও কনজিউমার গুডস",
            price = 378.40,
            changePercent = 0.65,
            peRatio = 11.2,
            dividendYield = 6.8,
            isBullish = true
        ),
        MockStock(
            symbol = "RENATA",
            nameBn = "রেনোটা লিমিটেড",
            sectorBn = "ফার্মাসিউটিক্যালস",
            price = 680.00,
            changePercent = -1.20,
            peRatio = 18.4,
            dividendYield = 2.5,
            isBullish = false
        ),
        MockStock(
            symbol = "WALTONHIL",
            nameBn = "ওয়ালটন হাইটেক ইন্ডাস্ট্রিজ",
            sectorBn = "ইলেকট্রনিক্স ও প্রযুক্তি",
            price = 540.50,
            changePercent = 1.80,
            peRatio = 16.2,
            dividendYield = 3.5,
            isBullish = true
        ),
        MockStock(
            symbol = "LHBL",
            nameBn = "লাফার্জহোলসিম সিমেন্ট",
            sectorBn = "সিমেন্ট ও কন্সট্রাকশন",
            price = 62.80,
            changePercent = 3.10,
            peRatio = 13.9,
            dividendYield = 5.2,
            isBullish = true
        ),
        MockStock(
            symbol = "ISLAMIBANK",
            nameBn = "ইসলামী ব্যাংক বাংলাদেশ",
            sectorBn = "ব্যাংকিং",
            price = 32.40,
            changePercent = -0.40,
            peRatio = 7.9,
            dividendYield = 4.0,
            isBullish = false
        )
    )
}
