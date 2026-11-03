# -*- coding: utf-8 -*-
"""Adds the Nexus line (2010-2015) to pixel_knowledge.json, catalog.json and the iFixit scores."""
import json, os
A = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
kp = os.path.join(A, "pixel_knowledge.json")
k = json.load(open(kp, encoding="utf-8"))
k["generations"] = [g for g in k["generations"] if not g["id"].startswith("n_")]
k["models"] = [m for m in k["models"] if not m["id"].startswith("nexus") and m["id"] != "galaxy_nexus"]
L = lambda en, ur: {"en": en, "ur": ur}

G = [
("n_one","Nexus One (2010)","نیکسس ون (2010)",2010,
 "The Nexus One was Google's first own-brand phone, built by HTC and announced in January 2010. Instead of selling through a carrier, Google sold it unlocked from its own web store, an idea that was new for Android at the time. It ran an almost untouched version of Android and received updates straight from Google, which became the heart of the Nexus promise. Sales were modest and Google closed the web store later in 2010, but the phone proved that a pure, fast-updating Android could win over enthusiasts and developers.",
 "نیکسس ون گوگل کا اپنا پہلا برانڈڈ فون تھا جسے ایچ ٹی سی نے بنایا اور جنوری 2010 میں متعارف کرایا گیا۔ کیریئر کے ذریعے بیچنے کے بجائے گوگل نے اسے اپنے آن لائن اسٹور سے انلاک شدہ فروخت کیا، جو اس وقت اینڈرائیڈ کے لیے ایک نیا تصور تھا۔ اس میں اینڈرائیڈ تقریباً اپنی اصل شکل میں تھا اور اپ ڈیٹس براہ راست گوگل سے آتی تھیں، جو نیکسس کے وعدے کی بنیاد بنا۔ فروخت محدود رہی اور گوگل نے اسی سال ویب اسٹور بند کر دیا، مگر اس فون نے ثابت کیا کہ خالص اور تیزی سے اپ ڈیٹ ہونے والا اینڈرائیڈ شوقین صارفین اور ڈویلپرز کو اپنی طرف کھینچ سکتا ہے۔",
 [("First phone sold unlocked directly by Google","گوگل کی طرف سے براہ راست انلاک فروخت ہونے والا پہلا فون"),("Stock Android with updates straight from Google","گوگل سے براہ راست اپ ڈیٹس کے ساتھ اسٹاک اینڈرائیڈ"),("Second microphone for noise cancellation and a trackball","شور کم کرنے کے لیے دوسرا مائیکروفون اور ٹریک بال"),("Bright 3.7-inch AMOLED screen","روشن 3.7 انچ AMOLED اسکرین")]),
("n_s","Nexus S (2010)","نیکسس ایس (2010)",2010,
 "Made with Samsung and released in December 2010, the Nexus S launched Android 2.3 Gingerbread. It was the first Android phone with NFC, years before tap-to-pay became common, and its curved Contour Display glass gave it a distinctive look. It added a front camera and a faster processor, and it was sold through retail stores as well as online. The Nexus S showed how Google and Samsung could ship a phone and a new Android version at the same time.",
 "سام سنگ کے ساتھ تیار کردہ اور دسمبر 2010 میں جاری ہونے والے نیکسس ایس نے اینڈرائیڈ 2.3 جنجر بریڈ متعارف کرایا۔ یہ این ایف سی والا پہلا اینڈرائیڈ فون تھا، اس سے برسوں پہلے کہ ٹیپ کر کے ادائیگی عام ہوتی، اور اس کا خمدار کنٹور ڈسپلے شیشہ اسے منفرد بناتا تھا۔ اس میں فرنٹ کیمرا اور تیز پروسیسر شامل کیا گیا، اور یہ آن لائن کے ساتھ ریٹیل اسٹورز پر بھی دستیاب تھا۔ نیکسس ایس نے دکھایا کہ گوگل اور سام سنگ فون اور نیا اینڈرائیڈ ورژن ایک ساتھ پیش کر سکتے ہیں۔",
 [("Launched Android 2.3 Gingerbread","اینڈرائیڈ 2.3 جنجر بریڈ کے ساتھ آیا"),("First Android phone with NFC","این ایف سی والا پہلا اینڈرائیڈ فون"),("Curved Contour Display glass","خمدار کنٹور ڈسپلے شیشہ"),("Front-facing camera for video calls","ویڈیو کالز کے لیے فرنٹ کیمرا")]),
("n_galaxy","Galaxy Nexus (2011)","گیلیکسی نیکسس (2011)",2011,
 "The Galaxy Nexus, again with Samsung, arrived in late 2011 as the showcase for Android 4.0 Ice Cream Sandwich, the version that merged the phone and tablet designs and introduced the Holo look. It dropped physical buttons for on-screen navigation keys and had a large 720p HD Super AMOLED display. Features such as Face Unlock and Android Beam made it feel futuristic. It was one of the best-loved developer phones, though its camera and battery life drew criticism.",
 "گیلیکسی نیکسس، ایک بار پھر سام سنگ کے ساتھ، 2011 کے آخر میں اینڈرائیڈ 4.0 آئس کریم سینڈوچ کے نمائشی فون کے طور پر آیا، جس نے فون اور ٹیبلیٹ کے ڈیزائن کو یکجا کیا اور ہولو انداز متعارف کرایا۔ اس میں جسمانی بٹن ختم کر کے اسکرین پر نیویگیشن بٹن دیے گئے اور 720p ایچ ڈی سپر AMOLED ڈسپلے تھا۔ فیس انلاک اور اینڈرائیڈ بیم جیسی خصوصیات نے اسے مستقبل کا فون بنا دیا۔ یہ ڈویلپرز کے پسندیدہ فونز میں سے تھا، اگرچہ اس کے کیمرے اور بیٹری پر تنقید ہوئی۔",
 [("Debuted Android 4.0 Ice Cream Sandwich","اینڈرائیڈ 4.0 آئس کریم سینڈوچ متعارف کرایا"),("On-screen navigation keys instead of hardware buttons","جسمانی بٹنوں کی جگہ اسکرین پر نیویگیشن بٹن"),("Face Unlock and Android Beam","فیس انلاک اور اینڈرائیڈ بیم"),("720p HD Super AMOLED display","720p ایچ ڈی سپر AMOLED ڈسپلے")]),
("n_4","Nexus 4 (2012)","نیکسس 4 (2012)",2012,
 "The Nexus 4 was built by LG and announced in late 2012 with Android 4.2. Its biggest surprise was the price: a high-end phone at 299 US dollars unlocked, far below its rivals. It sold out repeatedly in the first days, and stock shortages became part of its story. It added wireless charging and Photo Sphere, but it officially had no LTE, and the glass back was easy to crack.",
 "نیکسس 4 ایل جی نے بنایا اور 2012 کے آخر میں اینڈرائیڈ 4.2 کے ساتھ متعارف ہوا۔ اس کی سب سے بڑی حیرت قیمت تھی: ایک اعلیٰ فون انلاک شدہ 299 امریکی ڈالر میں، جو حریفوں سے کہیں کم تھی۔ ابتدائی دنوں میں یہ بار بار ختم ہو گیا اور اسٹاک کی کمی اس کی کہانی کا حصہ بن گئی۔ اس میں وائرلیس چارجنگ اور فوٹو اسفیئر شامل تھے، لیکن سرکاری طور پر ایل ٹی ای نہیں تھا اور پچھلا شیشہ آسانی سے ٹوٹ جاتا تھا۔",
 [("High-end hardware at 299 US dollars unlocked","اعلیٰ ہارڈ ویئر انلاک شدہ 299 امریکی ڈالر میں"),("Built-in Qi wireless charging","بلٹ ان کیو آئی وائرلیس چارجنگ"),("Photo Sphere and Gesture Typing with Android 4.2","اینڈرائیڈ 4.2 کے ساتھ فوٹو اسفیئر اور جیسچر ٹائپنگ"),("Glass front and back","آگے اور پیچھے شیشہ")]),
("n_7_2012","Nexus 7 (2012)","نیکسس 7 (2012)",2012,
 "Announced at Google I/O 2012 and built by ASUS, the Nexus 7 was Google's first tablet and the launch device for Android 4.1 Jelly Bean. At 199 US dollars it undercut most tablets and helped make Android tablets a real option. It introduced Google Now and Project Butter smoothness. Over time its storage and memory became slow after updates, a common complaint from owners.",
 "گوگل آئی/او 2012 میں متعارف کردہ اور ایسس کا بنایا ہوا نیکسس 7 گوگل کا پہلا ٹیبلیٹ اور اینڈرائیڈ 4.1 جیلی بین کا لانچ ڈیوائس تھا۔ 199 امریکی ڈالر کی قیمت پر اس نے اکثر ٹیبلیٹس کو پیچھے چھوڑ دیا اور اینڈرائیڈ ٹیبلیٹس کو ایک حقیقی انتخاب بنانے میں مدد دی۔ اس نے گوگل ناؤ اور پروجیکٹ بٹر کی روانی متعارف کرائی۔ وقت کے ساتھ اپ ڈیٹس کے بعد اس کی اسٹوریج اور میموری سست ہو گئی، جو صارفین کی عام شکایت تھی۔",
 [("Google's first tablet","گوگل کا پہلا ٹیبلیٹ"),("Debuted Android 4.1 Jelly Bean and Google Now","اینڈرائیڈ 4.1 جیلی بین اور گوگل ناؤ متعارف کرایا"),("Priced at 199 US dollars","قیمت 199 امریکی ڈالر"),("Quad-core Tegra 3 processor","کواڈ کور ٹیگرا 3 پروسیسر")]),
("n_10","Nexus 10 (2012)","نیکسس 10 (2012)",2012,
 "The Nexus 10, made by Samsung, arrived in November 2012 with a 10-inch 2560 by 1600 screen, the sharpest tablet display of its time. It launched alongside Android 4.2, which added multiple user accounts for tablets. It was a strong media tablet with good speakers, but its battery drained quickly under load and it was never a big seller.",
 "نیکسس 10، جو سام سنگ نے بنایا، نومبر 2012 میں 10 انچ کی 2560×1600 اسکرین کے ساتھ آیا، جو اس وقت کے ٹیبلیٹس میں سب سے واضح ڈسپلے تھا۔ یہ اینڈرائیڈ 4.2 کے ساتھ جاری ہوا جس نے ٹیبلیٹس پر ایک سے زیادہ صارف اکاؤنٹس شامل کیے۔ یہ اچھے اسپیکرز کے ساتھ ایک عمدہ میڈیا ٹیبلیٹ تھا، لیکن بھاری استعمال میں اس کی بیٹری جلد ختم ہو جاتی تھی اور یہ زیادہ فروخت نہ ہوا۔",
 [("2560 by 1600 display, the sharpest tablet screen of its day","2560×1600 ڈسپلے، اپنے وقت کی سب سے واضح ٹیبلیٹ اسکرین"),("Multi-user accounts on tablets with Android 4.2","اینڈرائیڈ 4.2 کے ساتھ ٹیبلیٹس پر متعدد صارف اکاؤنٹس"),("Front-facing stereo speakers","سامنے کی طرف اسٹیریو اسپیکرز")]),
("n_5","Nexus 5 (2013)","نیکسس 5 (2013)",2013,
 "Made by LG and launched in October 2013, the Nexus 5 introduced Android 4.4 KitKat with its hands-free OK Google hotword on the home screen. At 349 US dollars with a 4.95-inch full HD screen, a Snapdragon 800 chip and LTE, it is remembered as one of the best-value phones ever made. Many people still call it the classic Nexus. Battery life and early camera performance were weak points, and software updates improved the camera.",
 "ایل جی کا بنایا ہوا اور اکتوبر 2013 میں جاری ہونے والا نیکسس 5 اینڈرائیڈ 4.4 کٹ کیٹ کے ساتھ آیا جس میں ہوم اسکرین پر ہاتھ لگائے بغیر OK Google کہنے کی سہولت تھی۔ 349 امریکی ڈالر میں 4.95 انچ فل ایچ ڈی اسکرین، اسنیپ ڈریگن 800 چپ اور ایل ٹی ای کے ساتھ اسے اب تک کے بہترین قدر والے فونز میں یاد کیا جاتا ہے۔ بہت سے لوگ اسے آج بھی کلاسک نیکسس کہتے ہیں۔ بیٹری اور ابتدائی کیمرے کی کارکردگی کمزور پہلو تھے، اور سافٹ ویئر اپ ڈیٹس سے کیمرا بہتر ہوا۔",
 [("Debuted Android 4.4 KitKat and the OK Google hotword","اینڈرائیڈ 4.4 کٹ کیٹ اور OK Google متعارف کرایا"),("Full HD screen and Snapdragon 800 for 349 US dollars","349 امریکی ڈالر میں فل ایچ ڈی اسکرین اور اسنیپ ڈریگن 800"),("Optical image stabilisation in the 8 MP camera","8 میگا پکسل کیمرے میں آپٹیکل امیج اسٹیبلائزیشن"),("Wireless charging and worldwide LTE","وائرلیس چارجنگ اور عالمی ایل ٹی ای")]),
("n_7_2013","Nexus 7 (2013)","نیکسس 7 (2013)",2013,
 "The second Nexus 7, again from ASUS, arrived in July 2013 with a 1920 by 1200 screen, a faster Snapdragon chip and a better design. It launched with Android 4.3 and gained wireless charging and stereo speakers. It is often named one of the best small tablets of its era. Owners later reported GPS and touch problems on some units.",
 "دوسرا نیکسس 7، دوبارہ ایسس کی طرف سے، جولائی 2013 میں 1920×1200 اسکرین، تیز اسنیپ ڈریگن چپ اور بہتر ڈیزائن کے ساتھ آیا۔ یہ اینڈرائیڈ 4.3 کے ساتھ جاری ہوا اور اس میں وائرلیس چارجنگ اور اسٹیریو اسپیکرز شامل تھے۔ اسے اکثر اپنے دور کے بہترین چھوٹے ٹیبلیٹس میں شمار کیا جاتا ہے۔ بعد میں کچھ یونٹس پر جی پی ایس اور ٹچ کے مسائل کی اطلاع ملی۔",
 [("1920 by 1200 screen","1920×1200 اسکرین"),("Qi wireless charging and stereo speakers","کیو آئی وائرلیس چارجنگ اور اسٹیریو اسپیکرز"),("Faster Snapdragon processor","تیز اسنیپ ڈریگن پروسیسر")]),
("n_6","Nexus 6 (2014)","نیکسس 6 (2014)",2014,
 "Motorola built the large Nexus 6, which launched in October 2014 with Android 5.0 Lollipop and the new Material Design look. Its 5.96-inch quad HD screen, front-facing speakers and fast Turbo charging made it Google's first true big-screen flagship. At 649 US dollars it was also the priciest Nexus phone, and many fans felt it was too large and too expensive.",
 "موٹرولا نے بڑا نیکسس 6 بنایا جو اکتوبر 2014 میں اینڈرائیڈ 5.0 لالی پاپ اور نئے میٹیریل ڈیزائن کے ساتھ جاری ہوا۔ اس کی 5.96 انچ کواڈ ایچ ڈی اسکرین، سامنے کے اسپیکرز اور تیز ٹربو چارجنگ نے اسے گوگل کا پہلا بڑی اسکرین والا فلیگ شپ بنایا۔ 649 امریکی ڈالر میں یہ سب سے مہنگا نیکسس فون بھی تھا، اور بہت سے مداحوں کو لگا کہ یہ بہت بڑا اور مہنگا ہے۔",
 [("Debuted Android 5.0 Lollipop and Material Design","اینڈرائیڈ 5.0 لالی پاپ اور میٹیریل ڈیزائن متعارف کرایا"),("5.96-inch quad HD AMOLED screen","5.96 انچ کواڈ ایچ ڈی AMOLED اسکرین"),("Front-facing stereo speakers","سامنے کی طرف اسٹیریو اسپیکرز"),("Turbo fast charging","ٹربو فاسٹ چارجنگ")]),
("n_9","Nexus 9 (2014)","نیکسس 9 (2014)",2014,
 "HTC made the Nexus 9, an 8.9-inch tablet announced with Android 5.0 Lollipop in October 2014. It was the first device with NVIDIA's 64-bit Tegra K1 Denver chip and had a 4:3 screen that suited reading and browsing. Reviewers liked the design and speakers, but its performance dipped over time and some units had backlight bleed and a creaking back. It was Google's last Nexus tablet.",
 "نیکسس 9، جو ایچ ٹی سی نے بنایا، 8.9 انچ کا ٹیبلیٹ تھا جو اکتوبر 2014 میں اینڈرائیڈ 5.0 لالی پاپ کے ساتھ متعارف ہوا۔ یہ این ویڈیا کی 64 بٹ ٹیگرا کے 1 ڈینور چپ والا پہلا ڈیوائس تھا اور اس کی 4:3 اسکرین پڑھنے اور براؤزنگ کے لیے موزوں تھی۔ ناقدین کو ڈیزائن اور اسپیکرز پسند آئے، لیکن وقت کے ساتھ اس کی کارکردگی کم ہوئی اور کچھ یونٹس میں بیک لائٹ بلیڈ اور پچھلے حصے کی چرچراہٹ تھی۔ یہ گوگل کا آخری نیکسس ٹیبلیٹ تھا۔",
 [("First 64-bit Android device with NVIDIA Tegra K1","این ویڈیا ٹیگرا کے 1 کے ساتھ پہلا 64 بٹ اینڈرائیڈ ڈیوائس"),("4:3 8.9-inch screen","4:3 تناسب کی 8.9 انچ اسکرین"),("Front-facing BoomSound speakers","سامنے کے BoomSound اسپیکرز")]),
("n_5x_6p","Nexus 5X and 6P (2015)","نیکسس 5X اور 6P (2015)",2015,
 "The last two Nexus phones came in September 2015: the LG-built Nexus 5X and the Huawei-built Nexus 6P, both shipping with Android 6.0 Marshmallow. They introduced the USB-C port to Google phones and the Nexus Imprint fingerprint sensor, and the 6P brought a premium all-metal body. The Nexus 6P and 5X later became known for boot-loop failures. In 2016 Google replaced the Nexus line with the Pixel.",
 "آخری دو نیکسس فون ستمبر 2015 میں آئے: ایل جی کا بنایا نیکسس 5X اور ہواوے کا بنایا نیکسس 6P، دونوں اینڈرائیڈ 6.0 مارش میلو کے ساتھ۔ انہوں نے گوگل فونز میں یو ایس بی سی پورٹ اور نیکسس امپرنٹ فنگر پرنٹ سینسر متعارف کرایا، اور 6P نے مکمل دھاتی باڈی دی۔ بعد میں نیکسس 6P اور 5X بوٹ لوپ خرابیوں کے لیے مشہور ہوئے۔ 2016 میں گوگل نے نیکسس سیریز کی جگہ پکسل متعارف کرایا۔",
 [("Debuted Android 6.0 Marshmallow with Doze and Now on Tap","ڈوز اور ناؤ آن ٹیپ کے ساتھ اینڈرائیڈ 6.0 مارش میلو متعارف کرایا"),("USB-C port and Nexus Imprint fingerprint sensor","یو ایس بی سی پورٹ اور نیکسس امپرنٹ فنگر پرنٹ سینسر"),("All-metal body on the Nexus 6P","نیکسس 6P پر مکمل دھاتی باڈی"),("The last Nexus phones before the Pixel","پکسل سے پہلے کے آخری نیکسس فون")]),
]
for gid,en,ur,year,hen,hur,feats in G:
    k["generations"].append({"id":gid,"name":L(en,ur),"year":year,"history":L(hen,hur),"features":[L(a,b) for a,b in feats]})

def fault(en,ur,official=False): return {"en":en,"ur":ur,"official":official}
def spec(chip,ram,storage,display,battery,camera,protection,price):
    return {"chip":chip,"ram":ram,"storage":storage,"display":display,"battery":battery,"camera":camera,"protection":protection,"price":price}
Mdl = [
("nexus_one","Nexus One","n_one","PHONE","2010-01-05",7,"Ended 2011 (last update Android 2.3.6)",10,
 spec("Qualcomm Snapdragon QSD8250, 1 GHz","512 MB","512 MB + microSD up to 32 GB",'3.7" AMOLED, 480 x 800',"1,400 mAh","5 MP with LED flash","—","$529 unlocked ($179 on contract)"),
 [fault("Reported touch-screen responsiveness problems and weak 3G reception on some networks; Google investigated the 3G reports in early 2010.","کچھ نیٹ ورکس پر ٹچ اسکرین کی حساسیت اور کمزور 3G سگنل کی شکایات ملیں؛ گوگل نے 2010 کے آغاز میں 3G رپورٹس کی تحقیق کی۔")]),
("nexus_s","Nexus S","n_s","PHONE","2010-12-16",9,"Ended 2013 (last update Android 4.1.2)",16,
 spec("Samsung Hummingbird, 1 GHz","512 MB","16 GB (no microSD)",'4.0" Super AMOLED Contour Display, 480 x 800',"1,500 mAh","5 MP + VGA front camera","—","$529 unlocked / $199 on contract"),
 [fault("No widely documented hardware fault found in our sources; owners mostly noted the missing microSD slot and modest battery life.","ہمارے ذرائع میں کوئی وسیع طور پر دستاویزی ہارڈ ویئر خرابی نہیں ملی؛ صارفین نے زیادہ تر مائیکرو ایس ڈی سلاٹ کی کمی اور معمولی بیٹری کا ذکر کیا۔")]),
("galaxy_nexus","Galaxy Nexus","n_galaxy","PHONE","2011-11-17",14,"Ended 2013 (last update Android 4.3)",18,
 spec("TI OMAP 4460, 1.2 GHz dual-core","1 GB","16 / 32 GB (no microSD)",'4.65" HD Super AMOLED, 720 x 1280',"1,750 mAh","5 MP, 1.3 MP front","—","$299 on contract (US)"),
 [fault("Camera quality and battery life were widely criticised; some LTE models were also reported to have unstable connectivity.","کیمرے کے معیار اور بیٹری پر وسیع تنقید ہوئی؛ کچھ ایل ٹی ای ماڈلز میں کنکٹیوٹی کے غیر مستحکم ہونے کی بھی اطلاعات ملیں۔")]),
("nexus_4","Nexus 4","n_4","PHONE","2012-11-13",17,"Ended 2015 (last update Android 5.1.1)",22,
 spec("Qualcomm Snapdragon S4 Pro APQ8064, 1.5 GHz quad-core","2 GB","8 / 16 GB",'4.7" IPS, 768 x 1280',"2,100 mAh","8 MP, 1.3 MP front","Corning Gorilla Glass 2","$299 (8 GB) / $349 (16 GB)"),
 [fault("The glass back and front cracked easily, and battery life was short. LTE was not officially supported even though the hardware had a dormant LTE band.","اگلا اور پچھلا شیشہ آسانی سے ٹوٹ جاتا تھا اور بیٹری کم چلتی تھی۔ سرکاری طور پر ایل ٹی ای سپورٹ نہیں تھی حالانکہ ہارڈ ویئر میں ایک غیر فعال ایل ٹی ای بینڈ موجود تھا۔")]),
("nexus_7_2012","Nexus 7 (2012)","n_7_2012","TABLET","2012-07-13",16,"Ended 2015 (last update Android 5.1.1)",22,
 spec("NVIDIA Tegra 3, 1.2 GHz quad-core","1 GB","8 / 16 GB",'7" IPS, 800 x 1280',"4,325 mAh","1.2 MP front only","—","$199 (8 GB) / $249 (16 GB)"),
 [fault("Many owners reported the tablet slowing down after updates because of storage and memory performance; Android 4.3 added TRIM support to help.","بہت سے صارفین نے اپ ڈیٹس کے بعد اسٹوریج اور میموری کی کارکردگی کی وجہ سے ٹیبلیٹ کے سست ہونے کی اطلاع دی؛ اینڈرائیڈ 4.3 نے TRIM سپورٹ شامل کر کے مدد کی۔")]),
("nexus_10","Nexus 10","n_10","TABLET","2012-11-13",17,"Ended 2015 (last update Android 5.1.1)",22,
 spec("Samsung Exynos 5250, 1.7 GHz dual-core","2 GB","16 / 32 GB",'10.1" PLS, 2560 x 1600',"9,000 mAh","5 MP, 1.9 MP front","Corning Gorilla Glass 2","$399 (16 GB) / $499 (32 GB)"),
 [fault("No widely documented hardware fault found in our sources; reviewers noted fast battery drain under heavy use.","ہمارے ذرائع میں کوئی وسیع طور پر دستاویزی ہارڈ ویئر خرابی نہیں ملی؛ ناقدین نے بھاری استعمال میں بیٹری کے تیزی سے ختم ہونے کا ذکر کیا۔")]),
("nexus_5","Nexus 5","n_5","PHONE","2013-11-01",19,"Ended 2016 (last update Android 6.0.1)",23,
 spec("Qualcomm Snapdragon 800, 2.3 GHz quad-core","2 GB","16 / 32 GB",'4.95" Full HD IPS, 1080 x 1920',"2,300 mAh","8 MP with optical image stabilisation, 1.3 MP front","Corning Gorilla Glass 3","$349 (16 GB) / $399 (32 GB)"),
 [fault("Battery life and camera speed were criticised at launch; software updates improved the camera, but the battery stayed a weak point.","لانچ کے وقت بیٹری اور کیمرے کی رفتار پر تنقید ہوئی؛ سافٹ ویئر اپ ڈیٹس سے کیمرا بہتر ہوا مگر بیٹری کمزور پہلو رہی۔")]),
("nexus_7_2013","Nexus 7 (2013)","n_7_2013","TABLET","2013-07-26",18,"Ended 2016 (last update Android 6.0.1)",23,
 spec("Qualcomm Snapdragon S4 Pro APQ8064, 1.5 GHz quad-core","2 GB","16 / 32 GB",'7" IPS, 1200 x 1920',"3,950 mAh","5 MP rear, 1.2 MP front","—","$229 (16 GB) / $269 (32 GB)"),
 [fault("Owners reported GPS accuracy problems and touch-screen glitches on some units, and slowdowns after later Android updates.","کچھ یونٹس پر جی پی ایس کی درستی کے مسائل اور ٹچ اسکرین کی خرابیوں اور بعد کی اینڈرائیڈ اپ ڈیٹس کے بعد سست روی کی اطلاعات ملیں۔")]),
("nexus_6","Nexus 6","n_6","PHONE","2014-11-10",21,"Ended 2017 (last update Android 7.1.1)",25,
 spec("Qualcomm Snapdragon 805, 2.7 GHz quad-core","3 GB","32 / 64 GB",'5.96" AMOLED, 1440 x 2560',"3,220 mAh","13 MP with optical image stabilisation, 2 MP front","Corning Gorilla Glass 3, splash resistant","$649 (32 GB) / $699 (64 GB)"),
 [fault("No widely documented hardware fault found in our sources; many reviews said it was too big and too expensive.","ہمارے ذرائع میں کوئی وسیع طور پر دستاویزی ہارڈ ویئر خرابی نہیں ملی؛ بہت سے جائزوں نے اسے بہت بڑا اور مہنگا قرار دیا۔")]),
("nexus_9","Nexus 9","n_9","TABLET","2014-11-03",21,"Ended 2017 (last update Android 7.1.1)",25,
 spec("NVIDIA Tegra K1 64-bit, 2.3 GHz dual-core","2 GB","16 / 32 GB",'8.9" IPS, 2048 x 1536',"6,700 mAh","8 MP, 1.6 MP front","—","$399 (16 GB) / $479 (32 GB)"),
 [fault("Some units had backlight bleed and a creaking back, and performance became sluggish over time.","کچھ یونٹس میں بیک لائٹ بلیڈ اور پچھلے حصے کی چرچراہٹ تھی، اور وقت کے ساتھ کارکردگی سست ہو گئی۔")]),
("nexus_5x","Nexus 5X","n_5x_6p","PHONE","2015-10-26",23,"Ended 2018 (last update Android 8.1)",27,
 spec("Qualcomm Snapdragon 808, 1.8 GHz hexa-core","2 GB","16 / 32 GB",'5.2" Full HD IPS, 1080 x 1920',"2,700 mAh","12.3 MP, 5 MP front","Corning Gorilla Glass 3","$379 (16 GB) / $429 (32 GB)"),
 [fault("Many units suffered random shutdowns and boot loops that left the phone unable to start; a US class action followed.","بہت سے یونٹس میں اچانک بند ہونے اور بوٹ لوپ کے مسائل ہوئے جن سے فون چالو نہیں ہوتا تھا؛ امریکہ میں اجتماعی مقدمہ بھی دائر ہوا۔")]),
("nexus_6p","Nexus 6P","n_5x_6p","PHONE","2015-10-19",23,"Ended 2018 (last update Android 8.1)",27,
 spec("Qualcomm Snapdragon 810 v2.1, 2.0 GHz octa-core","3 GB","32 / 64 / 128 GB",'5.7" AMOLED, 1440 x 2560',"3,450 mAh","12.3 MP, 8 MP front","Corning Gorilla Glass 4, aluminium body","$499 (32 GB) / $549 (64 GB) / $649 (128 GB)"),
 [fault("Widespread boot loops and sudden shutdowns were reported, and a US class action over the 6P followed.","وسیع پیمانے پر بوٹ لوپ اور اچانک بند ہونے کی اطلاعات ملیں، اور 6P کے بارے میں امریکہ میں اجتماعی مقدمہ دائر ہوا۔")]),
]
for id_,name,gen,type_,rel,api,supp,latest,specs,faults in Mdl:
    q = name.replace(" ", "+")
    k["models"].append({"id":id_,"name":name,"gen":gen,"type":type_,"released":rel,"launchApi":api,"supportEnds":supp,
        "specs":specs,"faults":faults,"latestApi":latest,
        "officialUrl":"https://www.google.com/search?q="+q+"+Nexus+site%3Agoogle.com","officialIsStore":False})

# iFixit repairability scores found on iFixit's own device pages (and press for the Pixel 10 / 4 XL)
IF = {"pixel_1":(7,"Google_Pixel",""),"pixel_4_xl":(4,"Google_Pixel_4_XL",""),"pixel_fold":(3,"Google_Pixel_Fold",""),
      "pixel_8_pro":(6,"Google_Pixel_8_Pro",""),"pixel_9_pro_xl":(5,"Google_Pixel_9_Pro_XL",""),"pixel_10":(6,"Google_Pixel_10","provisional")}
for m in k["models"]:
    if m["id"] in IF:
        s,slug,note=IF[m["id"]]
        m["ifixitScore"]=s; m["ifixitUrl"]="https://www.ifixit.com/Device/"+slug
        if note: m["ifixitNote"]=note
json.dump(k, open(kp,"w",encoding="utf-8"), ensure_ascii=False, indent=1)

# catalogue entries
cp = os.path.join(A, "catalog.json")
c = json.load(open(cp, encoding="utf-8"))
c = [x for x in c if not x["name"].startswith("Nexus") and x["name"] != "Galaxy Nexus"]
add = [("Nexus One","PHONE",2010,7),("Nexus S","PHONE",2010,9),("Galaxy Nexus","PHONE",2011,14),("Nexus 4","PHONE",2012,17),("Nexus 5","PHONE",2013,19),
       ("Nexus 6","PHONE",2014,21),("Nexus 5X","PHONE",2015,23),("Nexus 6P","PHONE",2015,23),("Nexus 7 (2012)","TABLET",2012,16),
       ("Nexus 7 (2013)","TABLET",2013,18),("Nexus 9","TABLET",2014,21),("Nexus 10","TABLET",2012,17)]
for n,t,y,a in add: c.append({"name":n,"brand":"Google","type":t,"year":y,"api":a})
json.dump(c, open(cp,"w",encoding="utf-8"), ensure_ascii=False, indent=1)
print(len(k["generations"]), len(k["models"]), len(c))
