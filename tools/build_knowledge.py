# -*- coding: utf-8 -*-
"""Builds app/src/main/assets/pixel_knowledge.json and pixel_drops.json (English + Urdu)."""
import json, os
OUT = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")

def T(en, ur): return {"en": en, "ur": ur}

# ------------------------------------------------------------------ generations
G = []
def gen(id, name_en, name_ur, year, hist, feats):
    G.append({"id": id, "name": T(name_en, name_ur), "year": year, "history": T(*hist),
              "features": [T(*f) for f in feats]})

gen("g1", "Pixel (2016)", "پکسل (2016)", 2016,
 ("The first phones Google designed and sold itself, replacing the Nexus line in October 2016. They debuted Google Assistant and showed that Google wanted to own the whole Android experience.",
  "یہ پہلے فون تھے جو گوگل نے خود ڈیزائن کر کے فروخت کیے اور اکتوبر 2016 میں نیکسس سلسلے کی جگہ لی۔ ان کے ساتھ گوگل اسسٹنٹ متعارف ہوا اور یہ واضح ہو گیا کہ گوگل اینڈرائیڈ کا مکمل تجربہ اپنے ہاتھ میں رکھنا چاہتا ہے۔"),
 [("First phone with Google Assistant built in", "گوگل اسسٹنٹ کے ساتھ آنے والا پہلا فون"),
  ("Unlimited full-resolution Google Photos backup", "گوگل فوٹوز میں اصل ریزولوشن کا لامحدود بیک اپ"),
  ("Daydream VR ready", "ڈے ڈریم وی آر کے لیے تیار")])
gen("g2", "Pixel 2 series", "پکسل 2 سلسلہ", 2017,
 ("Launched October 2017 with a single-lens portrait mode that impressed reviewers. It dropped the headphone jack and introduced the squeezable frame.",
  "اکتوبر 2017 میں پیش کیا گیا۔ ایک لینس والے پورٹریٹ موڈ کو جائزہ نگاروں نے بہت سراہا۔ اس میں ہیڈ فون جیک ختم کر دیا گیا اور دبانے والا فریم متعارف ہوا۔"),
 [("Active Edge: squeeze the frame to launch Assistant", "ایکٹو ایج: فریم کو دبا کر اسسٹنٹ کھولیں"),
  ("Now Playing identifies songs on-device", "ناؤ پلئنگ فون ہی پر گانے پہچانتا ہے"),
  ("Always-on display and IP67 water resistance", "ہمیشہ روشن اسکرین اور IP67 پانی سے تحفظ")])
gen("g3", "Pixel 3 series", "پکسل 3 سلسلہ", 2018,
 ("Launched October 2018 as the camera-first Pixel, adding wireless charging and the Titan M security chip. Night Sight later arrived by update and changed low-light photography on phones.",
  "اکتوبر 2018 میں کیمرے پر توجہ دینے والے پکسل کے طور پر آیا۔ اس میں وائرلیس چارجنگ اور ٹائٹن ایم سکیورٹی چپ شامل ہوئی۔ بعد میں اپ ڈیٹ کے ذریعے نائٹ سائٹ آیا جس نے فونز میں کم روشنی کی فوٹوگرافی بدل دی۔"),
 [("Call Screen and Top Shot", "کال اسکرین اور ٹاپ شاٹ"),
  ("Night Sight (added by software update)", "نائٹ سائٹ (سافٹ ویئر اپ ڈیٹ کے ذریعے)"),
  ("Titan M security chip and wireless charging", "ٹائٹن ایم سکیورٹی چپ اور وائرلیس چارجنگ")])
gen("g3a", "Pixel 3a series", "پکسل 3a سلسلہ", 2019,
 ("The first 'a' phones, launched May 2019, brought the Pixel camera and clean software to a mid-range price and kept the headphone jack.",
  "مئی 2019 میں آنے والے پہلے 'a' فونز نے پکسل کیمرا اور صاف سافٹ ویئر درمیانی قیمت میں پیش کیا اور ہیڈ فون جیک برقرار رکھا۔"),
 [("Flagship Pixel camera at a mid-range price", "درمیانی قیمت میں اعلیٰ درجے کا پکسل کیمرا"),
  ("3.5 mm headphone jack kept", "3.5 ملی میٹر ہیڈ فون جیک برقرار"),
  ("Night Sight and Call Screen", "نائٹ سائٹ اور کال اسکرین")])
gen("g4", "Pixel 4 series", "پکسل 4 سلسلہ", 2019,
 ("Launched October 2019 with a radar chip for touch-free gestures and 3D face unlock, and a 90 Hz display. Battery life drew the most criticism.",
  "اکتوبر 2019 میں آیا۔ اس میں بغیر چھوئے اشاروں اور تھری ڈی فیس انلاک کے لیے ریڈار چپ اور 90 ہرٹز اسکرین تھی۔ سب سے زیادہ تنقید بیٹری کی کارکردگی پر ہوئی۔"),
 [("Soli radar 'Motion Sense' gestures", "سولی ریڈار 'موشن سینس' اشارے"),
  ("Face Unlock and Astrophotography mode", "فیس انلاک اور ایسٹرو فوٹوگرافی موڈ"),
  ("Recorder app with live transcription, 90 Hz display", "ریکارڈر ایپ اور براہ راست تحریری نقل، 90 ہرٹز اسکرین")])
gen("g4a", "Pixel 4a series", "پکسل 4a سلسلہ", 2020,
 ("The 4a (August 2020) became a favourite budget phone with its compact body, headphone jack and strong camera. A 5G version followed in November 2020.",
  "4a (اگست 2020) اپنے چھوٹے سائز، ہیڈ فون جیک اور مضبوط کیمرے کی وجہ سے سستا پسندیدہ فون بنا۔ نومبر 2020 میں 5G ورژن آیا۔"),
 [("Punch-hole display and headphone jack", "پنچ ہول اسکرین اور ہیڈ فون جیک"),
  ("4a 5G adds an ultrawide camera and 5G", "4a 5G میں الٹرا وائیڈ کیمرا اور 5G"),
  ("Pixel camera quality at a low price", "کم قیمت میں پکسل کیمرے کا معیار")])
gen("g5", "Pixel 5 series", "پکسل 5 سلسلہ", 2020,
 ("Google's 2020 flagship leaned on a mid-range chip and a big battery, with 5G, wireless charging and reverse charging. The Pixel 5a (2021) was the last Pixel with a headphone jack.",
  "2020 کے اس فلیگ شپ میں درمیانے درجے کی چپ اور بڑی بیٹری تھی، ساتھ 5G، وائرلیس چارجنگ اور ریورس چارجنگ۔ پکسل 5a (2021) ہیڈ فون جیک والا آخری پکسل تھا۔"),
 [("5G, 90 Hz display and Battery Share", "5G، 90 ہرٹز اسکرین اور بیٹری شیئر"),
  ("Extreme Battery Saver", "ایکسٹریم بیٹری سیور"),
  ("5a: IP67 and a 4,680 mAh battery", "5a: IP67 اور 4,680 mAh بیٹری")])
gen("g6", "Pixel 6 series", "پکسل 6 سلسلہ", 2021,
 ("Launched October 2021 with Google's own Tensor chip and the camera-bar design. It marked the start of the modern Pixel line and, after Google's 2024 extension, five years of Android updates.",
  "اکتوبر 2021 میں گوگل کی اپنی ٹینسر چپ اور کیمرا بار ڈیزائن کے ساتھ آیا۔ یہاں سے جدید پکسل سلسلہ شروع ہوا اور گوگل کی 2024 کی توسیع کے بعد اسے اینڈرائیڈ کی پانچ سال کی اپ ڈیٹس ملیں۔"),
 [("First Google Tensor chip with Titan M2", "پہلی گوگل ٹینسر چپ اور ٹائٹن M2"),
  ("Magic Eraser, Real Tone and Face Unblur", "میجک ایریزر، ریئل ٹون اور فیس اَن بلر"),
  ("Material You design debuts with Android 12", "اینڈرائیڈ 12 کے ساتھ میٹیریل یو ڈیزائن")])
gen("g6a", "Pixel 6a", "پکسل 6a", 2022,
 ("Launched July 2022 with the same Tensor chip as the Pixel 6 in a smaller, cheaper body.",
  "جولائی 2022 میں پکسل 6 والی ٹینسر چپ کے ساتھ چھوٹے اور سستے جسم میں آیا۔"),
 [("Tensor and Titan M2 at a mid-range price", "درمیانی قیمت میں ٹینسر اور ٹائٹن M2"),
  ("Magic Eraser and Real Tone", "میجک ایریزر اور ریئل ٹون")])
gen("g7", "Pixel 7 series", "پکسل 7 سلسلہ", 2022,
 ("Launched October 2022 as a refinement of the Pixel 6 with Tensor G2 and the return of Face Unlock. Google promised five years of security updates.",
  "اکتوبر 2022 میں پکسل 6 کی بہتر شکل کے طور پر آیا۔ اس میں ٹینسر G2 تھی اور فیس انلاک واپس آیا۔ گوگل نے پانچ سال کی سکیورٹی اپ ڈیٹس کا وعدہ کیا۔"),
 [("Tensor G2, Photo Unblur and Cinematic Blur", "ٹینسر G2، فوٹو اَن بلر اور سینیمیٹک بلر"),
  ("Face Unlock returns; Macro Focus", "فیس انلاک کی واپسی؛ میکرو فوکس"),
  ("Built-in VPN by Google (Pixel VPN)", "گوگل کا بلٹ اِن وی پی این (پکسل وی پی این)")])
gen("g7a", "Pixel 7a", "پکسل 7a", 2023,
 ("Launched May 2023 as the first a-series phone with wireless charging and a 90 Hz display.",
  "مئی 2023 میں آیا۔ یہ 'a' سلسلے کا پہلا فون تھا جس میں وائرلیس چارجنگ اور 90 ہرٹز اسکرین تھی۔"),
 [("First a-series phone with wireless charging", "وائرلیس چارجنگ والا پہلا 'a' فون"),
  ("90 Hz display and a 64 MP main camera", "90 ہرٹز اسکرین اور 64 میگا پکسل مین کیمرا")])
gen("gfold", "Pixel Fold", "پکسل فولڈ", 2023,
 ("Google's first foldable, launched June 2023, with a passport-style wide shape and an IPX8 rating.",
  "گوگل کا پہلا فولڈ ایبل، جون 2023 میں آیا۔ اس کی شکل پاسپورٹ جیسی چوڑی تھی اور IPX8 درجہ بندی تھی۔"),
 [("Dual Screen Interpreter mode", "ڈوئل اسکرین انٹرپریٹر موڈ"),
  ("Optimised foldable apps and taskbar", "فولڈ ایبل کے لیے بہتر ایپس اور ٹاسک بار"),
  ("5x telephoto camera on a foldable", "فولڈ ایبل میں 5x ٹیلی فوٹو کیمرا")])
gen("gtablet", "Pixel Tablet", "پکسل ٹیبلیٹ", 2023,
 ("Launched June 2023 with a charging speaker dock that turns it into a smart-home display.",
  "جون 2023 میں آیا۔ چارجنگ اسپیکر ڈاک اسے اسمارٹ ہوم ڈسپلے میں بدل دیتا ہے۔"),
 [("Charging speaker dock and Hub Mode", "چارجنگ اسپیکر ڈاک اور حب موڈ"),
  ("Chromecast built-in and multi-user support", "بلٹ اِن کروم کاسٹ اور ایک سے زیادہ صارفین کی سہولت"),
  ("Tensor G2", "ٹینسر G2")])
gen("g8", "Pixel 8 series", "پکسل 8 سلسلہ", 2023,
 ("Launched October 2023 with Tensor G3 and a promise of seven years of updates, the longest in Android at the time.",
  "اکتوبر 2023 میں ٹینسر G3 کے ساتھ آیا اور سات سال کی اپ ڈیٹس کا وعدہ کیا جو اس وقت اینڈرائیڈ میں سب سے طویل تھا۔"),
 [("Seven years of OS and security updates", "سات سال تک او ایس اور سکیورٹی اپ ڈیٹس"),
  ("Best Take, Magic Editor and Audio Magic Eraser", "بیسٹ ٹیک، میجک ایڈیٹر اور آڈیو میجک ایریزر"),
  ("8 Pro: temperature sensor and Video Boost", "8 پرو: درجہ حرارت سینسر اور ویڈیو بوسٹ")])
gen("g8a", "Pixel 8a", "پکسل 8a", 2024,
 ("Launched May 2024 with Tensor G3 and seven years of updates in an affordable body.",
  "مئی 2024 میں ٹینسر G3 اور سات سال کی اپ ڈیٹس کے ساتھ کم قیمت میں آیا۔"),
 [("Tensor G3 and seven years of updates", "ٹینسر G3 اور سات سال کی اپ ڈیٹس"),
  ("Gemini Nano support added by a later update", "بعد کی اپ ڈیٹ میں جیمنائی نینو کی سہولت")])
gen("g9", "Pixel 9 series", "پکسل 9 سلسلہ", 2024,
 ("Launched August 2024 with a flat-sided redesign, Tensor G4 and a heavy AI focus built around Gemini.",
  "اگست 2024 میں سیدھے کناروں کے نئے ڈیزائن، ٹینسر G4 اور جیمنائی پر مبنی بھرپور AI کے ساتھ آیا۔"),
 [("Pixel Studio, Pixel Screenshots and Add Me", "پکسل اسٹوڈیو، پکسل اسکرین شاٹس اور ایڈ می"),
  ("Call Notes and Gemini Live", "کال نوٹس اور جیمنائی لائیو"),
  ("Satellite SOS on the Pixel 9 line", "پکسل 9 سلسلے میں سیٹلائٹ SOS")])
gen("g9fold", "Pixel 9 Pro Fold", "پکسل 9 پرو فولڈ", 2024,
 ("The second-generation foldable (September 2024) dropped the passport shape for a taller outer screen and a thinner, lighter hinge.",
  "دوسری نسل کا فولڈ ایبل (ستمبر 2024) پاسپورٹ شکل چھوڑ کر زیادہ لمبی بیرونی اسکرین اور پتلے، ہلکے قبضے کے ساتھ آیا۔"),
 [("Larger 8-inch inner display", "8 انچ کی بڑی اندرونی اسکرین"),
  ("Thinner and lighter than the first Fold", "پہلے فولڈ سے پتلا اور ہلکا"),
  ("Gemini-powered AI features", "جیمنائی سے چلنے والی AI سہولیات")])
gen("g9a", "Pixel 9a", "پکسل 9a", 2025,
 ("Launched April 2025 with a flat camera back and the biggest battery in the a-series so far.",
  "اپریل 2025 میں ہموار پشت والے کیمرے اور اب تک 'a' سلسلے کی سب سے بڑی بیٹری کے ساتھ آیا۔"),
 [("Flush camera design", "ہموار کیمرا ڈیزائن"),
  ("5,100 mAh battery and seven years of updates", "5,100 mAh بیٹری اور سات سال کی اپ ڈیٹس")])
gen("g10", "Pixel 10 series", "پکسل 10 سلسلہ", 2025,
 ("Launched August 2025 with Tensor G5, the first Tensor made on TSMC's process, and magnetic Qi2 charging called Pixelsnap.",
  "اگست 2025 میں ٹینسر G5 کے ساتھ آیا جو TSMC کے عمل پر بننے والی پہلی ٹینسر ہے، اور مقناطیسی Qi2 چارجنگ (پکسل اسنیپ) متعارف ہوئی۔"),
 [("Tensor G5 and Pixelsnap magnetic charging", "ٹینسر G5 اور پکسل اسنیپ مقناطیسی چارجنگ"),
  ("Telephoto camera on the standard Pixel 10", "عام پکسل 10 میں ٹیلی فوٹو کیمرا"),
  ("Magic Cue and Camera Coach AI features", "میجک کیو اور کیمرا کوچ AI سہولیات")])
gen("g10fold", "Pixel 10 Pro Fold", "پکسل 10 پرو فولڈ", 2025,
 ("Shipped October 2025 as Google's first foldable with an IP68 rating, covering both water and dust.",
  "اکتوبر 2025 میں آیا۔ یہ گوگل کا پہلا فولڈ ایبل ہے جسے IP68 درجہ ملا، یعنی پانی اور گرد دونوں سے تحفظ۔"),
 [("IP68 water and dust resistance on a foldable", "فولڈ ایبل میں IP68 پانی اور گرد سے تحفظ"),
  ("Tensor G5", "ٹینسر G5")])
gen("g10a", "Pixel 10a", "پکسل 10a", 2026,
 ("Announced February 2026 and on sale from March 5, 2026 at $499. It reuses Tensor G4 instead of the Pixel 10's chip.",
  "فروری 2026 میں اعلان ہوا اور 5 مارچ 2026 سے 499 ڈالر میں فروخت ہوا۔ اس میں پکسل 10 کی چپ کے بجائے ٹینسر G4 ہی استعمال ہوئی۔"),
 [("Faster 45 W charging than the 9a", "9a سے تیز، 45 واٹ چارجنگ"),
  ("Seven years of updates", "سات سال کی اپ ڈیٹس")])
gen("g11", "Pixel 11 series", "پکسل 11 سلسلہ", 2026,
 ("Announced August 12, 2026 at Made by Google, with Tensor G6 and 256 GB as the base storage on every model. Each model starts $100 above its Pixel 10 equivalent.",
  "12 اگست 2026 کو میڈ بائی گوگل میں اعلان ہوا۔ تمام ماڈلز میں ٹینسر G6 اور کم از کم 256 جی بی اسٹوریج ہے۔ ہر ماڈل کی ابتدائی قیمت پکسل 10 کے مساوی ماڈل سے 100 ڈالر زیادہ ہے۔"),
 [("Tensor G6 chip", "ٹینسر G6 چپ"),
  ("256 GB base storage on every model", "ہر ماڈل میں 256 جی بی بنیادی اسٹوریج"),
  ("Launch details are still settling; check Google's site", "اجراء کی تفصیلات ابھی طے ہو رہی ہیں؛ گوگل کی سائٹ دیکھیں")])

# ------------------------------------------------------------------ models
M = []
def model(id, name, g, typ, released, api, ends, specs, faults=()):
    M.append({"id": id, "name": name, "gen": g, "type": typ, "released": released, "launchApi": api,
              "supportEnds": ends, "specs": specs, "faults": [T(*f) for f in faults]})
def S(chip, ram, storage, display, battery, camera, protection, price):
    d = {"chip": chip, "ram": ram, "storage": storage, "display": display, "battery": battery,
         "camera": camera, "protection": protection, "price": price}
    return {k: v for k, v in d.items() if v}

model("pixel_1","Pixel","g1","PHONE","2016-10-20",25,"2019-10",S("Snapdragon 821","4 GB","32 / 128 GB",'5.0" 1080p AMOLED',"2,770 mAh","12.3 MP rear","IP53","$649"))
model("pixel_1_xl","Pixel XL","g1","PHONE","2016-10-20",25,"2019-10",S("Snapdragon 821","4 GB","32 / 128 GB",'5.5" 1440p AMOLED',"3,450 mAh","12.3 MP rear","IP53","$769"))
model("pixel_2","Pixel 2","g2","PHONE","2017-10-19",26,"2020-10",S("Snapdragon 835","4 GB","64 / 128 GB",'5.0" 1080p AMOLED',"2,700 mAh","12.2 MP rear","IP67","$649"))
model("pixel_2_xl","Pixel 2 XL","g2","PHONE","2017-10-19",26,"2020-10",S("Snapdragon 835","4 GB","64 / 128 GB",'6.0" 1440p P-OLED',"3,520 mAh","12.2 MP rear","IP67","$849"),
 [("Display complaints at launch (colour shift, graininess and early burn-in reports). Google responded with software tweaks and extended the warranty.",
   "لانچ کے وقت اسکرین کی شکایات (رنگوں میں تبدیلی، دھندلاہٹ اور جلد برن اِن کی اطلاعات)۔ گوگل نے سافٹ ویئر اصلاحات کیں اور وارنٹی میں توسیع کی۔")])
model("pixel_3","Pixel 3","g3","PHONE","2018-10-18",28,"2021-10",S("Snapdragon 845","4 GB","64 / 128 GB",'5.5" 1080p OLED',"2,915 mAh","12.2 MP rear + dual front","IP68","$799"))
model("pixel_3_xl","Pixel 3 XL","g3","PHONE","2018-10-18",28,"2021-10",S("Snapdragon 845","4 GB","64 / 128 GB",'6.3" 1440p OLED (notch)',"3,430 mAh","12.2 MP rear + dual front","IP68","$899"))
model("pixel_3a","Pixel 3a","g3a","PHONE","2019-05-07",28,"2022-05",S("Snapdragon 670","4 GB","64 GB",'5.6" 1080p OLED',"3,000 mAh","12.2 MP rear",None,"$399"))
model("pixel_3a_xl","Pixel 3a XL","g3a","PHONE","2019-05-07",28,"2022-05",S("Snapdragon 670","4 GB","64 GB",'6.0" 1080p OLED',"3,700 mAh","12.2 MP rear",None,"$479"))
model("pixel_4","Pixel 4","g4","PHONE","2019-10-24",29,"2022-10",S("Snapdragon 855","6 GB","64 / 128 GB",'5.7" 1080p OLED, 90 Hz',"2,800 mAh","12.2 MP + 16 MP telephoto","IP68","$799"))
model("pixel_4_xl","Pixel 4 XL","g4","PHONE","2019-10-24",29,"2022-10",S("Snapdragon 855","6 GB","64 / 128 GB",'6.3" 1440p OLED, 90 Hz',"3,700 mAh","12.2 MP + 16 MP telephoto","IP68","$899"))
model("pixel_4a","Pixel 4a","g4a","PHONE","2020-08-20",29,"2023-08",S("Snapdragon 730G","6 GB","128 GB",'5.81" 1080p OLED',"3,140 mAh","12.2 MP rear",None,"$349"),
 [("Battery stability problem: in January 2025 Google pushed a mandatory update that limits battery capacity and charging on affected units, and offered a free battery replacement or compensation.",
   "بیٹری کے استحکام کا مسئلہ: جنوری 2025 میں گوگل نے لازمی اپ ڈیٹ جاری کی جو متاثرہ فونز میں بیٹری کی گنجائش اور چارجنگ محدود کرتی ہے، اور مفت بیٹری تبدیلی یا معاوضہ پیش کیا۔")])
model("pixel_4a_5g","Pixel 4a (5G)","g4a","PHONE","2020-11-05",30,"2023-11",S("Snapdragon 765G","6 GB","128 GB",'6.2" 1080p OLED',"3,885 mAh","12.2 MP + 16 MP ultrawide",None,"$499"))
model("pixel_5","Pixel 5","g5","PHONE","2020-10-15",30,"2023-10",S("Snapdragon 765G","8 GB","128 GB",'6.0" 1080p OLED, 90 Hz',"4,080 mAh","12.2 MP + 16 MP ultrawide","IP68","$699"))
model("pixel_5a","Pixel 5a (5G)","g5","PHONE","2021-08-26",30,"2024-08",S("Snapdragon 765G","6 GB","128 GB",'6.34" 1080p OLED',"4,680 mAh","12.2 MP + 16 MP ultrawide","IP67","$449"))
model("pixel_6","Pixel 6","g6","PHONE","2021-10-28",31,"2026-10",S("Google Tensor","8 GB","128 / 256 GB",'6.4" 1080p AMOLED, 90 Hz',"4,614 mAh","50 MP + 12 MP ultrawide","IP68","$599"),
 [("Slow or unreliable in-display fingerprint sensor at launch. Google blamed enhanced security algorithms and improved it with updates.",
   "لانچ کے وقت اسکرین کے اندر لگا فنگر پرنٹ سینسر سست یا ناقابل اعتماد تھا۔ گوگل نے اس کی وجہ بہتر سکیورٹی الگورتھم بتائی اور اپ ڈیٹس سے بہتری کی۔"),
  ("Early connectivity (mobile data / Wi-Fi) and display bugs reported on the series.",
   "اس سلسلے میں ابتدائی طور پر کنیکٹیویٹی (موبائل ڈیٹا / وائی فائی) اور اسکرین کی خرابیوں کی اطلاعات ملیں۔")])
model("pixel_6_pro","Pixel 6 Pro","g6","PHONE","2021-10-28",31,"2026-10",S("Google Tensor","12 GB","128 / 256 / 512 GB",'6.71" 1440p LTPO, 120 Hz',"5,003 mAh","50 MP + 12 MP ultrawide + 48 MP 4x telephoto","IP68","$899"),
 [("Slow or unreliable in-display fingerprint sensor at launch. Google blamed enhanced security algorithms and improved it with updates.",
   "لانچ کے وقت اسکرین کے اندر لگا فنگر پرنٹ سینسر سست یا ناقابل اعتماد تھا۔ گوگل نے اس کی وجہ بہتر سکیورٹی الگورتھم بتائی اور اپ ڈیٹس سے بہتری کی۔"),
  ("Early connectivity (mobile data / Wi-Fi) and display bugs reported on the series.",
   "اس سلسلے میں ابتدائی طور پر کنیکٹیویٹی (موبائل ڈیٹا / وائی فائی) اور اسکرین کی خرابیوں کی اطلاعات ملیں۔")])
model("pixel_6a","Pixel 6a","g6a","PHONE","2022-07-28",31,"2027-07",S("Google Tensor","6 GB","128 GB",'6.1" 1080p OLED',"4,410 mAh","12.2 MP + 12 MP ultrawide","IP67","$449"),
 [("Battery overheating risk: after reports of overheating, Google pushed a mandatory update from July 8, 2025 that limits charging on affected units, with free battery replacement from July 21, 2025 or up to $150 compensation.",
   "بیٹری کے زیادہ گرم ہونے کا خطرہ: گرم ہونے کی اطلاعات کے بعد گوگل نے 8 جولائی 2025 سے لازمی اپ ڈیٹ جاری کی جو متاثرہ فونز میں چارجنگ محدود کرتی ہے۔ 21 جولائی 2025 سے مفت بیٹری تبدیلی یا 150 ڈالر تک معاوضہ دستیاب ہے۔")])
model("pixel_7","Pixel 7","g7","PHONE","2022-10-13",33,"2027-10",S("Google Tensor G2","8 GB","128 / 256 GB",'6.3" 1080p OLED, 90 Hz',"4,355 mAh","50 MP + 12 MP ultrawide","IP68","$599"))
model("pixel_7_pro","Pixel 7 Pro","g7","PHONE","2022-10-13",33,"2027-10",S("Google Tensor G2","12 GB","128 / 256 / 512 GB",'6.7" 1440p LTPO, 120 Hz',"5,000 mAh","50 MP + 12 MP ultrawide + 48 MP 5x telephoto","IP68","$899"))
model("pixel_7a","Pixel 7a","g7a","PHONE","2023-05-10",33,"2028-05",S("Google Tensor G2","8 GB","128 GB",'6.1" 1080p OLED, 90 Hz',"4,385 mAh","64 MP + 13 MP ultrawide","IP67","$499"),
 [("Swollen battery reports: Google opened a repair and compensation programme for affected Pixel 7a units in April 2025.",
   "بیٹری پھولنے کی اطلاعات: اپریل 2025 میں گوگل نے متاثرہ پکسل 7a کے لیے مرمت اور معاوضے کا پروگرام شروع کیا۔")])
model("pixel_fold","Pixel Fold","gfold","FOLDABLE","2023-06-27",33,"2028-06",S("Google Tensor G2","12 GB","256 / 512 GB",'7.6" 120 Hz inner, 5.8" outer',"4,821 mAh","48 MP + 10.8 MP ultrawide + 10.8 MP 5x telephoto","IPX8","$1,799"),
 [("Inner display failures reported soon after launch: debris in the gutter, peeling screen protector, dents and a pink line on some units.",
   "لانچ کے فوراً بعد اندرونی اسکرین کی خرابیاں رپورٹ ہوئیں: کناروں میں گرد، اسکرین پروٹیکٹر اکھڑنا، ابھار اور بعض فونز میں گلابی لکیر۔"),
  ("Some owners reported a popping noise from the hinge when opening the phone.",
   "بعض صارفین نے فون کھولتے وقت قبضے سے ٹک ٹک کی آواز کی شکایت کی۔")])
model("pixel_tablet","Pixel Tablet","gtablet","TABLET","2023-06-20",33,"2028-06",S("Google Tensor G2","8 GB","128 / 256 GB",'10.95" 2560x1600 LCD',"27 Wh","8 MP front and rear",None,"$499 (with dock)"))
model("pixel_8","Pixel 8","g8","PHONE","2023-10-12",34,"2030-10",S("Google Tensor G3","8 GB","128 / 256 GB",'6.2" 1080p OLED, 120 Hz',"4,575 mAh","50 MP + 12 MP ultrawide","IP68","$699"))
model("pixel_8_pro","Pixel 8 Pro","g8","PHONE","2023-10-12",34,"2030-10",S("Google Tensor G3","12 GB","128 / 256 / 512 GB / 1 TB",'6.7" 1344x2992 LTPO, 120 Hz',"5,050 mAh","50 MP + 48 MP ultrawide + 48 MP 5x telephoto","IP68","$999"))
model("pixel_8a","Pixel 8a","g8a","PHONE","2024-05-14",34,"2031-05",S("Google Tensor G3","8 GB","128 / 256 GB",'6.1" 1080p OLED, 120 Hz',"4,492 mAh","64 MP + 13 MP ultrawide","IP67","$499"))
model("pixel_9","Pixel 9","g9","PHONE","2024-08-22",34,"2031-08",S("Google Tensor G4","12 GB","128 / 256 GB",'6.3" 1080p Actua OLED, 120 Hz',"4,700 mAh","50 MP + 48 MP ultrawide","IP68","$799"))
model("pixel_9_pro","Pixel 9 Pro","g9","PHONE","2024-09-04",34,"2031-09",S("Google Tensor G4","16 GB","128 / 256 / 512 GB / 1 TB",'6.3" LTPO OLED, 120 Hz',"4,700 mAh","50 MP + 48 MP ultrawide + 48 MP 5x telephoto","IP68","$999"))
model("pixel_9_pro_xl","Pixel 9 Pro XL","g9","PHONE","2024-08-22",34,"2031-08",S("Google Tensor G4","16 GB","128 / 256 / 512 GB / 1 TB",'6.8" LTPO OLED, 120 Hz',"5,060 mAh","50 MP + 48 MP ultrawide + 48 MP 5x telephoto","IP68","$1,099"))
model("pixel_9_pro_fold","Pixel 9 Pro Fold","g9fold","FOLDABLE","2024-09-04",34,"2031-09",S("Google Tensor G4","16 GB","256 / 512 GB",'8.0" inner 120 Hz, 6.3" outer',"4,650 mAh","48 MP + 10.5 MP ultrawide + 10.8 MP 5x telephoto","IPX8","$1,799"),
 [("The glossy plastic around the inner display picks up marks and scratches quickly, according to long-term reviewers.",
   "طویل مدتی جائزہ نگاروں کے مطابق اندرونی اسکرین کے گرد چمکدار پلاسٹک پر نشانات اور خراشیں جلدی پڑ جاتی ہیں۔")])
model("pixel_9a","Pixel 9a","g9a","PHONE","2025-04-10",35,"2032-04",S("Google Tensor G4","8 GB","128 / 256 GB",'6.3" 1080p Actua OLED, 120 Hz',"5,100 mAh","48 MP + 13 MP ultrawide","IP68","$499"))
PIXEL10_FAULT = ("Early software bugs on Pixel 10 phones, including gaming-app crashes, Wi-Fi / Bluetooth toggle problems and crashes while installing or updating apps. Google addressed them in Android 17 QPR1 (September 2026).",
                 "پکسل 10 فونز میں ابتدائی سافٹ ویئر خرابیاں، جن میں گیمنگ ایپس کا کریش ہونا، وائی فائی / بلوٹوتھ بند کھولنے کے مسائل اور ایپس انسٹال یا اپ ڈیٹ کرتے وقت کریش شامل ہیں۔ گوگل نے انہیں اینڈرائیڈ 17 QPR1 (ستمبر 2026) میں دور کیا۔")
model("pixel_10","Pixel 10","g10","PHONE","2025-08-28",36,"2032-08",S("Google Tensor G5","12 GB","128 / 256 GB",'6.3" 1080p Actua OLED, 120 Hz',"4,970 mAh","48 MP + 13 MP ultrawide + 10.8 MP 5x telephoto","IP68","$799"),[PIXEL10_FAULT])
model("pixel_10_pro","Pixel 10 Pro","g10","PHONE","2025-08-28",36,"2032-08",S("Google Tensor G5","16 GB","128 / 256 / 512 GB / 1 TB",'6.3" LTPO OLED, 120 Hz',"4,870 mAh","50 MP + 48 MP ultrawide + 48 MP 5x telephoto","IP68","$999"),[PIXEL10_FAULT])
model("pixel_10_pro_xl","Pixel 10 Pro XL","g10","PHONE","2025-08-28",36,"2032-08",S("Google Tensor G5","16 GB","256 / 512 GB / 1 TB",'6.8" LTPO OLED, 120 Hz',"5,200 mAh","50 MP + 48 MP ultrawide + 48 MP 5x telephoto","IP68","$1,199"),[PIXEL10_FAULT])
model("pixel_10_pro_fold","Pixel 10 Pro Fold","g10fold","FOLDABLE","2025-10-09",36,"2032-10",S("Google Tensor G5","16 GB","256 / 512 GB / 1 TB",'8" inner 120 Hz, 6.4" outer',"5,015 mAh",None,"IP68","$1,799"),[PIXEL10_FAULT])
model("pixel_10a","Pixel 10a","g10a","PHONE","2026-03-05",36,"2033-03",S("Google Tensor G4","8 GB","128 / 256 GB",'6.3" 1080p pOLED, 60-120 Hz',"5,100 mAh","48 MP + 13 MP ultrawide","IP68","$499"),[PIXEL10_FAULT])
model("pixel_11","Pixel 11","g11","PHONE","2026-08-20",37,"2033-08",S("Google Tensor G6","12 GB","256 / 512 GB",'6.3" 1080p Actua OLED, 60-120 Hz',"4,985 mAh",None,None,"$899"))
model("pixel_11_pro","Pixel 11 Pro","g11","PHONE","2026-08-20",37,"2033-08",S("Google Tensor G6","12 / 16 GB","256 / 512 GB / 1 TB",'6.3" LTPO OLED, 120 Hz',"4,850 mAh",None,None,"$1,099"))
model("pixel_11_pro_xl","Pixel 11 Pro XL","g11","PHONE","2026-08-20",37,"2033-08",S("Google Tensor G6","12 / 16 GB","256 / 512 GB / 1 TB",'6.8" LTPO OLED, 120 Hz',"5,115 mAh",None,None,"$1,299"))
model("pixel_11_pro_fold","Pixel 11 Pro Fold","g11","FOLDABLE","2026-08-20",37,"2033-08",S("Google Tensor G6","16 GB","256 / 512 GB / 1 TB",None,"4,806 mAh (reported)",None,None,"$1,899"))

json.dump({"generations": G, "models": M}, open(os.path.join(OUT, "pixel_knowledge.json"), "w"), ensure_ascii=False, indent=1)

# ------------------------------------------------------------------ Pixel Drops
P6 = ["pixel_6","pixel_6_pro","pixel_6a"]; P7 = ["pixel_7","pixel_7_pro","pixel_7a"]; P8 = ["pixel_8","pixel_8_pro","pixel_8a"]
P9 = ["pixel_9","pixel_9_pro","pixel_9_pro_xl","pixel_9_pro_fold"]; P10 = ["pixel_10","pixel_10_pro","pixel_10_pro_xl","pixel_10_pro_fold"]
P11 = ["pixel_11","pixel_11_pro","pixel_11_pro_xl","pixel_11_pro_fold"]
BASE = P6 + P7 + ["pixel_fold","pixel_tablet"] + P8 + P9
D = []
def drop(id, date, title, tie, summary, feats, devices, note=None):
    D.append({"id": id, "date": date, "title": T(*title), "tie": T(*tie), "summary": T(*summary),
              "features": [T(*f) for f in feats], "devices": devices,
              "note": T(*note) if note else None})

drop("2026-09","2026-09-15",("September 2026 Pixel Drop","ستمبر 2026 پکسل ڈراپ"),("Android 17 QPR1","اینڈرائیڈ 17 QPR1"),
 ("A lighter drop built around Android 17 QPR1 with interface polish and fixes. It was the first drop to include the Pixel 11 series; Pixel 6 and 6 Pro receive their final major update.",
  "اینڈرائیڈ 17 QPR1 پر مبنی ہلکا ڈراپ جس میں انٹرفیس کی بہتری اور اصلاحات ہیں۔ اس میں پہلی بار پکسل 11 سلسلہ شامل ہے؛ پکسل 6 اور 6 پرو کو آخری بڑی اپ ڈیٹ مل رہی ہے۔"),
 [("More blur effects in the volume panel, power menu and folders (reported to be limited on Pixel 6 and 7 series)", "والیوم پینل، پاور مینو اور فولڈرز میں مزید بلر اثرات (پکسل 6 اور 7 سلسلے میں محدود ہونے کی اطلاع)"),
  ("Pause Point in Digital Wellbeing", "ڈیجیٹل ویل بیئنگ میں پاز پوائنٹ"),
  ("Redesigned Pixel VIPs widget with badges and a floating menu", "نئے ڈیزائن کا پکسل VIPs ویجٹ، بیجز اور تیرتے مینو کے ساتھ"),
  ("Scam Detection in the Pixel keyboard (US, Pixel 6 and newer)", "پکسل کی بورڈ میں اسکیم ڈیٹیکشن (امریکہ، پکسل 6 اور نئے)"),
  ("Fixes for gaming crashes, Wi-Fi / Bluetooth and app-install crashes on Pixel 10", "پکسل 10 پر گیمنگ کریش، وائی فائی / بلوٹوتھ اور ایپ انسٹال کریش کی اصلاحات")],
 BASE + ["pixel_9a"] + P10 + ["pixel_10a"] + P11,
 ("Some features arrive later or only in selected countries.", "کچھ سہولیات بعد میں یا صرف منتخب ممالک میں آتی ہیں۔"))
drop("2026-06","2026-06-16",("June 2026 Pixel Drop","جون 2026 پکسل ڈراپ"),("Android 17 + Wear OS 7","اینڈرائیڈ 17 + ویئر او ایس 7"),
 ("The launch of Android 17 on Pixel, together with new features for phones and watches.",
  "پکسل پر اینڈرائیڈ 17 کا اجراء، ساتھ فونز اور گھڑیوں کے لیے نئی سہولیات۔"),
 [("Bubbles: turn any app into a floating bubble", "بلبلز: کسی بھی ایپ کو تیرتے بلبلے میں بدلیں"),
  ("Screen Reactions: record a selfie video together with your screen", "اسکرین ری ایکشنز: اپنی سیلفی ویڈیو کے ساتھ اسکرین ریکارڈ کریں"),
  ("Gemini soundtrack generation and Gemini Omni (for Gemini Pro users)", "جیمنائی ساؤنڈ ٹریک تخلیق اور جیمنائی اومنی (جیمنائی پرو صارفین کے لیے)"),
  ("A gaming UI for foldables and wider Quick Share support", "فولڈ ایبلز کے لیے گیمنگ انٹرفیس اور کوئیک شیئر کی وسیع سہولت"),
  ("Pixel Watch: emergency sharing in Car Crash, Fall (all models) and Loss of Pulse (Watch 3 and 4) detection", "پکسل واچ: کار کریش، فال (تمام ماڈلز) اور لاس آف پلس (واچ 3 اور 4) ڈیٹیکشن میں ہنگامی اطلاع")],
 BASE + ["pixel_9a"] + P10 + ["pixel_10a"],
 ("Every officially supported Pixel at the time was eligible. Gemini Intelligence is limited to newer hardware.", "اس وقت باقاعدہ سپورٹ ہونے والے تمام پکسل اہل تھے۔ جیمنائی انٹیلی جنس صرف نئے ہارڈ ویئر تک محدود ہے۔"))
drop("2026-03","2026-03-03",("March 2026 Pixel Drop","مارچ 2026 پکسل ڈراپ"),("Android 16 QPR3","اینڈرائیڈ 16 QPR3"),
 ("One of the larger drops, with more than a dozen features for phones and watches.",
  "بڑے ڈراپس میں سے ایک جس میں فونز اور گھڑیوں کے لیے ایک درجن سے زیادہ سہولیات ہیں۔"),
 [("Circle to Search: 'Find the look' for whole outfits and 'Try it on'", "سرکل ٹو سرچ: پورے لباس کے لیے 'فائنڈ دی لُک' اور 'ٹرائی اِٹ آن'"),
  ("Now Playing becomes a standalone Play Store app (Pixel 6 and newer)", "ناؤ پلئنگ علیحدہ پلے اسٹور ایپ بن گئی (پکسل 6 اور نئے)"),
  ("At a Glance shows live sports scores and Google Finance movers (not on Pixel Tablet)", "ایٹ اے گلانس میں براہ راست اسپورٹس اسکور اور گوگل فنانس (پکسل ٹیبلیٹ پر نہیں)"),
  ("Custom AI-generated app icons in six styles (not on Pixel Tablet)", "چھ انداز میں AI سے بنے ایپ آئیکن (پکسل ٹیبلیٹ پر نہیں)"),
  ("Desktop mode available on Pixel Tablet without workarounds", "پکسل ٹیبلیٹ پر ڈیسک ٹاپ موڈ بغیر کسی ترکیب کے دستیاب")],
 BASE + ["pixel_9a"] + P10 + ["pixel_10a"],
 ("21 supported devices, including the new Pixel 10a.", "21 سپورٹ شدہ ڈیوائسز، بشمول نیا پکسل 10a۔"))
drop("2025-11","2025-11-11",("November 2025 Pixel Drop","نومبر 2025 پکسل ڈراپ"),("Android 16 QPR1","اینڈرائیڈ 16 QPR1"),
 ("Personalisation and notification tools for phones and tablets.",
  "فونز اور ٹیبلیٹس کے لیے ذاتی نوعیت اور نوٹیفکیشن کے اوزار۔"),
 [("Pixel Themes (theme packs) for the whole look", "پورے انداز کے لیے پکسل تھیمز (تھیم پیکس)"),
  ("Notification Summaries and Notification Organizer", "نوٹیفکیشن سمریز اور نوٹیفکیشن آرگنائزر"),
  ("New Messages and Photos features for all Android devices", "تمام اینڈرائیڈ ڈیوائسز کے لیے میسجز اور فوٹوز کی نئی سہولیات")],
 BASE + ["pixel_9a"] + P10)
drop("2025-09","2025-09-03",("September 2025 Pixel Drop","ستمبر 2025 پکسل ڈراپ"),("Android 16","اینڈرائیڈ 16"),
 ("Brought the Material 3 Expressive look to older Pixels and improved earbuds and watch features.",
  "پرانے پکسل فونز میں میٹیریل 3 ایکسپریسو انداز آیا اور ایربڈز و گھڑی کی سہولیات بہتر ہوئیں۔"),
 [("Material 3 Expressive design on Pixel 6 and newer", "پکسل 6 اور نئے فونز پر میٹیریل 3 ایکسپریسو ڈیزائن"),
  ("Refreshed Quick Share interface", "کوئیک شیئر کا نیا انٹرفیس"),
  ("Adaptive Audio on Pixel Buds Pro 2", "پکسل بڈز پرو 2 پر اڈاپٹو آڈیو"),
  ("Glanceable Maps navigation on Pixel Watch", "پکسل واچ پر نظر آنے والی میپس نیویگیشن")],
 BASE + ["pixel_9a","pixel_10","pixel_10_pro","pixel_10_pro_xl"],
 ("The Pixel 10 Pro Fold shipped later, in October 2025.", "پکسل 10 پرو فولڈ بعد میں، اکتوبر 2025 میں آیا۔"))
drop("2025-06","2025-06-10",("June 2025 Pixel Drop","جون 2025 پکسل ڈراپ"),("Android 16","اینڈرائیڈ 16"),
 ("Android 16 reached Pixel first, with new Pixel-only features.",
  "اینڈرائیڈ 16 سب سے پہلے پکسل پر آیا، ساتھ صرف پکسل کی نئی سہولیات۔"),
 [("Android 16 stable release", "اینڈرائیڈ 16 کا مستحکم اجراء"),
  ("Pixel VIPs: quick access to your important contacts", "پکسل VIPs: اہم رابطوں تک فوری رسائی"),
  ("New Pixel camera, call and notification features", "پکسل کیمرا، کال اور نوٹیفکیشن کی نئی سہولیات")],
 BASE + ["pixel_9a"])
drop("2025-03","2025-03-04",("March 2025 Pixel Drop","مارچ 2025 پکسل ڈراپ"),("Android 15 QPR2","اینڈرائیڈ 15 QPR2"),
 ("A smaller drop that expanded Gemini and scam protection.",
  "نسبتاً چھوٹا ڈراپ جس نے جیمنائی اور فراڈ سے تحفظ کو وسعت دی۔"),
 [("Real-time alerts for suspicious text messages", "مشکوک ٹیکسٹ پیغامات پر فوری انتباہ"),
  ("Gemini Live multimodal features expand to Pixel 6 and newer and Pixel Fold", "جیمنائی لائیو کی ملٹی موڈل سہولیات پکسل 6 اور نئے فونز اور پکسل فولڈ تک"),
  ("Pixel Screenshots suggests images to add to collections", "پکسل اسکرین شاٹس کلیکشنز میں شامل کرنے کے لیے تصاویر تجویز کرتا ہے"),
  ("Pixel Studio: text-to-image prompts", "پکسل اسٹوڈیو: متن سے تصویر بنانے کی سہولت"),
  ("Pixel Watch: better step tracking", "پکسل واچ: قدموں کی بہتر پیمائش")],
 BASE)
drop("2024-12","2024-12-05",("December 2024 Pixel Drop","دسمبر 2024 پکسل ڈراپ"),("Android 15 QPR1","اینڈرائیڈ 15 QPR1"),
 ("A big drop, and the day Google extended update support for Pixel 6, Pixel 7 and Pixel Fold by two years.",
  "بڑا ڈراپ، اور وہی دن جب گوگل نے پکسل 6، پکسل 7 اور پکسل فولڈ کی اپ ڈیٹ سپورٹ میں دو سال کی توسیع کی۔"),
 [("Pixel Weather app reaches Pixel 6 and newer, including Pixel Tablet", "پکسل ویدر ایپ پکسل 6 اور نئے فونز اور پکسل ٹیبلیٹ تک"),
  ("Next-generation Call Screen", "کال اسکرین کی نئی نسل"),
  ("Pixel Screenshots updates and Pixel Studio stickers in Gboard", "پکسل اسکرین شاٹس کی تازہ کاری اور جی بورڈ میں پکسل اسٹوڈیو اسٹیکرز"),
  ("Emoji Kitchen mixes and more widgets", "ایموجی کچن مکس اور مزید ویجٹس"),
  ("Pixel 6 series, Pixel 7 series and Pixel Fold get two extra years of OS updates", "پکسل 6، پکسل 7 سلسلے اور پکسل فولڈ کو او ایس اپ ڈیٹس کے دو اضافی سال")],
 BASE)
drop("2024-10","2024-10-15",("October 2024 Pixel Drop","اکتوبر 2024 پکسل ڈراپ"),("Android 15","اینڈرائیڈ 15"),
 ("Android 15 arrived for Pixel 6 and newer with a round of Pixel features.",
  "اینڈرائیڈ 15 پکسل 6 اور نئے فونز پر آیا اور ساتھ پکسل کی نئی سہولیات بھی۔"),
 [("Android 15 stable release for Pixel 6 and newer", "پکسل 6 اور نئے فونز کے لیے اینڈرائیڈ 15 کا مستحکم اجراء"),
  ("Cast to nearby devices on Pixel Tablet, 6 Pro, 7 Pro, Fold, 8 Pro and 9 Pro models", "پکسل ٹیبلیٹ، 6 پرو، 7 پرو، فولڈ، 8 پرو اور 9 پرو ماڈلز پر قریبی ڈیوائسز پر کاسٹ"),
  ("More Gemini features and Pixel Watch improvements", "جیمنائی کی مزید سہولیات اور پکسل واچ میں بہتری")],
 BASE)
drop("2024-06","2024-06-11",("June 2024 Pixel Drop","جون 2024 پکسل ڈراپ"),("Android 14 QPR3","اینڈرائیڈ 14 QPR3"),
 ("A feature drop focused on Gemini Nano, external displays and camera control.",
  "جیمنائی نینو، بیرونی اسکرین اور کیمرا کنٹرول پر مرکوز فیچر ڈراپ۔"),
 [("Gemini Nano comes to Pixel 8 and Pixel 8a (developer option)", "جیمنائی نینو پکسل 8 اور پکسل 8a پر (ڈویلپر آپشن)"),
  ("DisplayPort Alt Mode: external monitor over USB-C on Pixel 8, 8 Pro and 8a", "ڈسپلے پورٹ آلٹ موڈ: پکسل 8، 8 پرو اور 8a پر USB-C کے ذریعے بیرونی مانیٹر"),
  ("Manual lens selection on Pixel 6 Pro, 7 Pro, 8 Pro and Fold", "پکسل 6 پرو، 7 پرو، 8 پرو اور فولڈ پر دستی لینس انتخاب"),
  ("Lookup in the Phone app (Pixel 6 and newer)", "فون ایپ میں لُک اپ (پکسل 6 اور نئے)"),
  ("Pixel VPN by Google for Pixel 7 series and Fold", "پکسل 7 سلسلے اور فولڈ کے لیے گوگل کا پکسل وی پی این")],
 ["pixel_5a"] + P6 + P7 + P8 + ["pixel_fold","pixel_tablet"])
drop("2024-03","2024-03-04",("March 2024 Pixel Drop","مارچ 2024 پکسل ڈراپ"),("Android 14 QPR2","اینڈرائیڈ 14 QPR2"),
 ("Brought Circle to Search to the Pixel 7 series and improved Instagram uploads.",
  "پکسل 7 سلسلے میں سرکل ٹو سرچ آیا اور انسٹاگرام اپ لوڈز بہتر ہوئے۔"),
 [("Circle to Search on Pixel 7 and 7 Pro", "پکسل 7 اور 7 پرو پر سرکل ٹو سرچ"),
  ("Ultra HDR photos and 10-bit HDR video to Instagram (Pixel 7, 7 Pro, 8, 8 Pro, Fold)", "انسٹاگرام پر الٹرا ایچ ڈی آر تصاویر اور 10 بٹ ایچ ڈی آر ویڈیو (پکسل 7، 7 پرو، 8، 8 پرو، فولڈ)"),
  ("App screen sharing on Pixel 5a (5G) and newer, including Tablet and Fold", "پکسل 5a (5G) اور نئے فونز، ٹیبلیٹ اور فولڈ پر ایپ اسکرین شیئرنگ"),
  ("Improved Call Screen; Pixel Watch (1st gen) gains Pixel Watch 2 features", "کال اسکرین میں بہتری؛ پہلی پکسل واچ کو پکسل واچ 2 کی سہولیات")],
 ["pixel_5a"] + P6 + P7 + ["pixel_fold","pixel_tablet","pixel_8","pixel_8_pro"])
drop("2024-01","2024-01-24",("January 2024 Pixel Drop","جنوری 2024 پکسل ڈراپ"),("Pixel Feature Drop","پکسل فیچر ڈراپ"),
 ("The first drop of 2024 is best known for bringing Circle to Search to the Pixel 8 series.",
  "2024 کا پہلا ڈراپ پکسل 8 سلسلے میں سرکل ٹو سرچ لانے کی وجہ سے مشہور ہے۔"),
 [("Circle to Search arrives on Pixel 8 and Pixel 8 Pro (late January)", "پکسل 8 اور پکسل 8 پرو پر سرکل ٹو سرچ (جنوری کے آخر میں)")],
 ["pixel_8","pixel_8_pro"],
 ("Only the headline feature is listed; other parts of this drop varied by model.", "صرف نمایاں سہولت درج ہے؛ اس ڈراپ کے دیگر حصے ماڈل کے لحاظ سے مختلف تھے۔"))

json.dump({"drops": D}, open(os.path.join(OUT, "pixel_drops.json"), "w"), ensure_ascii=False, indent=1)

# ------------------------------------------------------------------ catalogue (Pixel entries from knowledge + other brands)
cat_path = os.path.join(OUT, "catalog.json")
cat = [c for c in json.load(open(cat_path)) if c["brand"] != "Google" or c["type"] in ("WATCH", "EARBUDS")]
for m in M:
    cat.append({"name": m["name"], "brand": "Google", "type": m["type"], "year": int(m["released"][:4]), "api": m["launchApi"], "kid": m["id"]})
json.dump(cat, open(cat_path, "w"), ensure_ascii=False, indent=1)
print(len(G), "generations,", len(M), "models,", len(D), "drops,", len(cat), "catalogue entries")
