# -*- coding: utf-8 -*-
"""Adds Google's laptops (Chromebook Pixel, Pixelbook family and the 2026 Googlebooks) and trims the catalogue to Google devices."""
import json, os
A = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
kp = os.path.join(A, "pixel_knowledge.json"); k = json.load(open(kp, encoding="utf-8"))
L = lambda en, ur: {"en": en, "ur": ur}
NEW_GENS = ("g_cbp", "g_pixelbook", "g_slate", "g_pbgo", "g_googlebook")
k["generations"] = [g for g in k["generations"] if g["id"] not in NEW_GENS]
LAPTOP_IDS = ("chromebook_pixel_2013","chromebook_pixel_2015","pixelbook","pixel_slate","pixelbook_go","googlebook_acer_14","googlebook_dell_xps","googlebook_hp_14","googlebook_asus_14","googlebook_lenovo_15")
k["models"] = [m for m in k["models"] if m["id"] not in LAPTOP_IDS]
G = [
("g_cbp","Chromebook Pixel (2013 and 2015)","کروم بک پکسل (2013 اور 2015)",2013,
 "The Chromebook Pixel was Google's first laptop, announced in February 2013 as a showcase for what a premium Chrome OS machine could be. Its sharp 2560 by 1700 touchscreen, aluminium body and 1,299 US dollar price were bold for a laptop that mainly ran a web browser. The 2015 model kept the screen, added Intel Broadwell chips and became the first laptop with USB-C ports, at 999 US dollars. Google stopped selling it in 2016 and moved the idea into the Pixelbook.",
 "کروم بک پکسل گوگل کا پہلا لیپ ٹاپ تھا جو فروری 2013 میں اس بات کی نمائش کے طور پر متعارف ہوا کہ اعلیٰ درجے کی کروم او ایس مشین کیسی ہو سکتی ہے۔ اس کی واضح 2560×1700 ٹچ اسکرین، المونیم باڈی اور 1,299 امریکی ڈالر کی قیمت اس لیپ ٹاپ کے لیے جرات مندانہ تھی جو زیادہ تر ویب براؤزر چلاتا تھا۔ 2015 کے ماڈل نے وہی اسکرین رکھی، انٹیل براڈویل چپس شامل کیں اور 999 امریکی ڈالر میں یو ایس بی سی پورٹس والا پہلا لیپ ٹاپ بنا۔ گوگل نے 2016 میں اسے بیچنا بند کر دیا اور یہ خیال پکسل بک میں منتقل کیا۔",
 [("Google's first own-brand laptop","گوگل کا پہلا اپنے برانڈ کا لیپ ٹاپ"),("2560 by 1700 touchscreen in a 3:2 shape","3:2 شکل میں 2560×1700 ٹچ اسکرین"),("2015 model: first laptop with USB-C ports","2015 کا ماڈل: یو ایس بی سی پورٹس والا پہلا لیپ ٹاپ"),("Premium aluminium body and a glowing light bar","اعلیٰ المونیم باڈی اور روشن لائٹ بار")]),
("g_pixelbook","Pixelbook (2017)","پکسل بک (2017)",2017,
 "The Pixelbook arrived at the Made by Google event in October 2017 as a premium convertible with a 360-degree hinge, a thin aluminium and glass body and the optional Pixelbook Pen. It was the first Chromebook with a dedicated Google Assistant key and one of the first to run Android apps from Google Play. Priced from 999 US dollars, it was admired and expensive, and Google removed it from its store in 2020.",
 "پکسل بک اکتوبر 2017 میں میڈ بائی گوگل تقریب میں 360 ڈگری قبضے، پتلی المونیم اور شیشے کی باڈی اور اختیاری پکسل بک پین والے اعلیٰ کنورٹیبل کے طور پر آیا۔ یہ گوگل اسسٹنٹ کی مخصوص کلید والا پہلا کروم بک تھا اور ان اولین میں سے جو گوگل پلے سے اینڈرائیڈ ایپس چلاتے تھے۔ 999 امریکی ڈالر سے شروع ہونے والی قیمت پر اسے سراہا گیا مگر یہ مہنگا تھا، اور گوگل نے 2020 میں اسے اپنے اسٹور سے ہٹا دیا۔",
 [("360-degree convertible hinge","360 ڈگری کنورٹیبل قبضہ"),("Pixelbook Pen with Google Assistant","گوگل اسسٹنٹ کے ساتھ پکسل بک پین"),("Dedicated Google Assistant key","گوگل اسسٹنٹ کی مخصوص کلید"),("Runs Android apps from Google Play","گوگل پلے سے اینڈرائیڈ ایپس چلاتا ہے")]),
("g_slate","Pixel Slate (2018)","پکسل سلیٹ (2018)",2018,
 "Announced in October 2018, the Pixel Slate was a detachable Chrome OS tablet with a sharp 3000 by 2000 display and an optional keyboard. It tried to make Chrome OS work as a touch-first tablet, but the software felt unfinished, and lower-end models with Celeron and Core m3 chips were criticised for being slow. Google stopped planning new Pixel Slate hardware in 2019.",
 "اکتوبر 2018 میں متعارف ہونے والا پکسل سلیٹ علیحدہ ہو جانے والا کروم او ایس ٹیبلیٹ تھا جس میں 3000×2000 واضح ڈسپلے اور اختیاری کی بورڈ تھا۔ اس نے کروم او ایس کو ٹچ پر مبنی ٹیبلیٹ کے طور پر چلانے کی کوشش کی، مگر سافٹ ویئر ادھورا محسوس ہوا، اور سیلیرون اور کور ایم 3 چپس والے کم درجے کے ماڈلز کو سست ہونے پر تنقید ملی۔ گوگل نے 2019 میں پکسل سلیٹ کے نئے ہارڈ ویئر کا منصوبہ ختم کر دیا۔",
 [("Detachable Chrome OS tablet","علیحدہ ہونے والا کروم او ایس ٹیبلیٹ"),("3000 by 2000 Molecular Display","3000×2000 مالیکیولر ڈسپلے"),("Optional keyboard and Pixelbook Pen support","اختیاری کی بورڈ اور پکسل بک پین کی سپورٹ")]),
("g_pbgo","Pixelbook Go (2019)","پکسل بک گو (2019)",2019,
 "The Pixelbook Go, shown in October 2019, was Google's lighter and cheaper answer to the Pixelbook. It started at 649 US dollars, had a ribbed magnesium case that was easy to grip, a very quiet keyboard and a battery rated at up to 12 hours. It dropped the convertible hinge in favour of a classic clamshell. It was the last laptop in Google's own Pixel family before the Googlebook.",
 "پکسل بک گو، جو اکتوبر 2019 میں دکھایا گیا، پکسل بک کا ہلکا اور سستا جواب تھا۔ اس کی ابتدائی قیمت 649 امریکی ڈالر تھی، اس کا دھاری دار میگنیشیم کیس پکڑنے میں آسان تھا، کی بورڈ بہت خاموش تھا اور بیٹری 12 گھنٹے تک کی درجہ بند تھی۔ اس نے کنورٹیبل قبضہ ترک کر کے روایتی کلیم شیل ڈیزائن اپنایا۔ یہ گوگل کے اپنے پکسل خاندان میں گوگل بک سے پہلے کا آخری لیپ ٹاپ تھا۔",
 [("Lighter and cheaper than the Pixelbook","پکسل بک سے ہلکا اور سستا"),("Ribbed magnesium body and a very quiet keyboard","دھاری دار میگنیشیم باڈی اور بہت خاموش کی بورڈ"),("Up to 12 hours of battery life","12 گھنٹے تک کی بیٹری")]),
("g_googlebook","Googlebook (2026)","گوگل بک (2026)",2026,
 "Googlebook is Google's successor to the Chromebook. It was first revealed in May 2026 as a laptop platform built on Android, running an operating system codenamed Aluminium OS, and the full line-up followed in September 2026 with pre-orders from the 21st and sale from 4 October. Instead of making the laptops itself, Google launched the platform with five partners: Acer, ASUS, Dell, HP and Lenovo. Googlebooks lean on Gemini AI, connect closely to Android phones and run the Android app catalogue, and Google has said cheaper models will follow.",
 "گوگل بک گوگل کے کروم بک کا جانشین ہے۔ اسے پہلی بار مئی 2026 میں اینڈرائیڈ پر مبنی لیپ ٹاپ پلیٹ فارم کے طور پر ظاہر کیا گیا جو ایلومینیم او ایس نامی آپریٹنگ سسٹم پر چلتا ہے، اور مکمل لائن اپ ستمبر 2026 میں آیا جس کے پری آرڈر 21 تاریخ سے اور فروخت 4 اکتوبر سے شروع ہوئی۔ لیپ ٹاپ خود بنانے کے بجائے گوگل نے پانچ شراکت داروں کے ساتھ پلیٹ فارم متعارف کرایا: ایسر، ایسس، ڈیل، ایچ پی اور لینووو۔ گوگل بکس جیمنائی اے آئی پر انحصار کرتے ہیں، اینڈرائیڈ فونز سے گہرا تعلق رکھتے ہیں اور اینڈرائیڈ ایپس چلاتے ہیں، اور گوگل نے کہا ہے کہ سستے ماڈلز بعد میں آئیں گے۔",
 [("Android-based laptop platform (Aluminium OS) replacing Chrome OS","کروم او ایس کی جگہ اینڈرائیڈ پر مبنی لیپ ٹاپ پلیٹ فارم (ایلومینیم او ایس)"),("Gemini AI built into the experience","تجربے میں شامل جیمنائی اے آئی"),("Every Googlebook starts at 16 GB of RAM","ہر گوگل بک 16 جی بی ریم سے شروع ہوتی ہے"),("NPU of over 45 TOPS, haptic glass trackpad, up to 14 hours of battery (Google's figure)","45 ٹاپس سے زائد NPU، ہیپٹک شیشے کا ٹریک پیڈ، 14 گھنٹے تک بیٹری (گوگل کا دعویٰ)")]),
]
for gid,en,ur,year,hen,hur,feats in G:
    k["generations"].append({"id":gid,"name":L(en,ur),"year":year,"history":L(hen,hur),"features":[L(a,b) for a,b in feats]})
def fault(en,ur,off=False): return {"en":en,"ur":ur,"official":off}
def sp(os_,chip,ram,storage,display,battery,camera,ports,price): return {"os":os_,"chip":chip,"ram":ram,"storage":storage,"display":display,"battery":battery,"camera":camera,"ports":ports,"price":price}
NONE=fault("No widely documented hardware fault found in our sources. That is not a guarantee.","ہمارے ذرائع میں کوئی وسیع طور پر دستاویزی ہارڈ ویئر خرابی نہیں ملی۔ یہ کوئی ضمانت نہیں ہے۔")
NEWF=fault("Too new for a reliable fault history; none reported in our sources yet.","قابل اعتماد خرابی کی تاریخ کے لیے بہت نیا ہے؛ ہمارے ذرائع میں ابھی تک کوئی اطلاع نہیں۔")
INTEL=fault("Google warned that some Android apps may not run well on Intel-based Googlebooks at launch.","گوگل نے خبردار کیا کہ انٹیل پر مبنی گوگل بکس پر لانچ کے وقت کچھ اینڈرائیڈ ایپس اچھی طرح نہیں چلیں گی۔",True)
def ev(date,en,ur,pl_en,pl_ur,pres_en,pres_ur,q): return {"date":date,"event":L(en,ur),"place":L(pl_en,pl_ur),"presenter":L(pres_en,pres_ur),"query":q}
GB=ev("2026-09-21","Googlebook reveal (May 12, 2026) and launch announcement (September 21, 2026)","گوگل بک کا اعلان (12 مئی 2026) اور اجرا کا اعلان (21 ستمبر 2026)","Online","آن لائن","Announced by Google; teased with Rick Osterloh at Snapdragon Summit","گوگل کی طرف سے اعلان؛ رِک اوسٹرلوہ کے ساتھ سنیپ ڈریگن سمٹ میں اشارہ","Googlebook launch Google Aluminium OS announcement")
ONL=lambda d,en,ur,q: ev(d,en,ur,"Online","آن لائن","Announced by Google (blog post)","گوگل کی طرف سے اعلان (بلاگ پوسٹ)",q)
MBG=lambda d,en,ur,pl,plu,q: ev(d,en,ur,pl,plu,"Rick Osterloh, Google's head of devices","رِک اوسٹرلوہ، گوگل کے ڈیوائسز کے سربراہ",q)
SUPP="Googlebook updates: Google says feature updates will be faster than on traditional laptops (support period to be confirmed)"
OS="Googlebook OS (Aluminium OS, based on Android)"
M=[
("chromebook_pixel_2013","Chromebook Pixel (2013)","g_cbp","2013-02-28","Chrome OS auto-updates have ended; see Google's auto-update policy",
 sp("Chrome OS","Intel Core i5-3427U, 1.8 GHz","4 GB","32 GB SSD (64 GB with LTE)",'12.85" touchscreen, 2560 x 1700 (3:2)',"59 Wh (about 5 hours)","720p webcam","2 x USB 2.0, Mini DisplayPort, SD card slot, headphone/mic","$1,299 ($1,449 with LTE)"),[NONE],
 ONL("2013-02-21","Chromebook Pixel announcement","کروم بک پکسل کا اعلان","Chromebook Pixel Google announcement 2013"),"https://www.google.com/search?q=Chromebook+Pixel+2013+site%3Agoogle.com"),
("chromebook_pixel_2015","Chromebook Pixel (2015)","g_cbp","2015-03-11","Chrome OS auto-updates have ended; see Google's auto-update policy",
 sp("Chrome OS","Intel Core i5-5200U or Core i7-5500U","8 / 16 GB","32 / 64 GB SSD",'12.85" touchscreen, 2560 x 1700 (3:2)',"Up to 12 hours (Google's figure)","720p webcam","2 x USB-C, 2 x USB 3.0, SD card slot, headphone/mic","$999 / $1,299"),
 [fault("Concerns about non-compliant third-party USB-C cables and chargers followed its launch, since it was one of the first laptops to rely on USB-C.","اس کے اجرا کے بعد غیر معیاری تیسرے فریق یو ایس بی سی کیبلز اور چارجرز کے بارے میں خدشات سامنے آئے، کیونکہ یہ یو ایس بی سی پر انحصار کرنے والے اولین لیپ ٹاپس میں سے تھا۔")],
 ONL("2015-03-11","Chromebook Pixel (2015) announcement","کروم بک پکسل (2015) کا اعلان","Chromebook Pixel 2015 Google announcement"),"https://www.google.com/search?q=Chromebook+Pixel+2015+site%3Agoogle.com"),
("pixelbook","Pixelbook","g_pixelbook","2017-10-30","See Google's Chromebook auto-update policy",
 sp("Chrome OS","Intel Core i5-7Y57 or Core i7-7Y75","8 / 16 GB","128 / 256 / 512 GB",'12.3" touchscreen, 2400 x 1600, 360-degree hinge',"Up to 10 hours (Google's figure)","720p webcam","2 x USB-C, headphone jack","From $999"),[NONE],
 MBG("2017-10-04","Made by Google 2017","میڈ بائی گوگل 2017","San Francisco, USA","سان فرانسسکو، امریکہ","Made by Google 2017 event full"),"https://store.google.com/us/product/google_pixelbook_specs"),
("pixel_slate","Pixel Slate","g_slate","2018-12","See Google's Chromebook auto-update policy",
 sp("Chrome OS","Intel Celeron, Core m3, Core i5 or Core i7 (8th generation)","4 / 8 / 16 GB","32 / 64 / 128 / 256 GB",'12.3" touchscreen, 3000 x 2000',"Up to 10 hours (Google's figure)","8 MP rear, 8 MP front","2 x USB-C, headphone jack","From $599 (keyboard sold separately)"),
 [fault("Lower-spec models with Celeron and Core m3 chips were criticised for slow performance, and early tablet-mode software felt unfinished. Google stopped planning new Pixel Slate hardware in 2019.","سیلیرون اور کور ایم 3 چپس والے کم درجے کے ماڈلز کو سست کارکردگی پر تنقید ملی، اور ابتدائی ٹیبلیٹ موڈ سافٹ ویئر ادھورا محسوس ہوا۔ گوگل نے 2019 میں پکسل سلیٹ کے نئے ہارڈ ویئر کا منصوبہ ختم کر دیا۔")],
 MBG("2018-10-09","Made by Google 2018","میڈ بائی گوگل 2018","New York, USA","نیویارک، امریکہ","Made by Google 2018 event full"),"https://www.google.com/search?q=Pixel+Slate+site%3Agoogle.com"),
("pixelbook_go","Pixelbook Go","g_pbgo","2019-10","See Google's Chromebook auto-update policy",
 sp("Chrome OS","Intel Core m3, Core i5 or Core i7 (8th generation)","8 / 16 GB","64 / 128 / 256 GB",'13.3" touchscreen, Full HD (1920 x 1080) or 4K',"Up to 12 hours (Google's figure)","1080p webcam","2 x USB-C, headphone jack","From $649"),[NONE],
 MBG("2019-10-15","Made by Google 2019","میڈ بائی گوگل 2019","New York, USA","نیویارک، امریکہ","Made by Google 2019 event full"),"https://www.google.com/search?q=Pixelbook+Go+site%3Agoogle.com"),
("googlebook_acer_14","Acer Googlebook 14","g_googlebook","2026-10-04",SUPP,
 sp(OS,"Intel Core Ultra 5 325 or Core Ultra 7 355","16 / 32 GB","256 / 512 GB",'14" 2.8K (2880 x 1800) OLED touchscreen, up to 500 nits, Gorilla Glass',"71 Wh","—","Thunderbolt 4, USB-C, DisplayPort, USB Power Delivery","From $899"),[INTEL],GB,"https://googlebook.google"),
("googlebook_dell_xps","Dell XPS Googlebook","g_googlebook","2026-10-04",SUPP,
 sp(OS,"Qualcomm Snapdragon X Elite","16 - 32 GB","256 GB or more",'13.4" 2.5K display',"—","—","—","From $1,199"),[NEWF],GB,"https://googlebook.google"),
("googlebook_hp_14","HP Googlebook 14","g_googlebook","2026-10-04",SUPP,
 sp(OS,"Qualcomm Snapdragon X Elite","16 - 32 GB","256 GB or more",'14" 2.8K OLED',"—","—","—","From $1,299"),[NEWF],GB,"https://googlebook.google"),
("googlebook_asus_14","ASUS Googlebook 14","g_googlebook","2026-10-04",SUPP,
 sp(OS,"Intel Core Ultra 5 or Core Ultra 7","16 - 32 GB","256 GB or more",'14" 2.8K OLED',"—","—","—","From $1,299"),[INTEL],GB,"https://googlebook.google"),
("googlebook_lenovo_15","Lenovo Googlebook 15","g_googlebook","2026-10-04",SUPP,
 sp(OS,"Intel Core Ultra 5","16 GB","256 GB or more",'15.3" 2.8K OLED',"—","—","—","From $1,299"),[INTEL],GB,"https://googlebook.google"),
]
for id_,name,gen,rel,supp,specs,faults,launch,url in M:
    k["models"].append({"id":id_,"name":name,"gen":gen,"type":"LAPTOP","released":rel,"launchApi":0,"supportEnds":supp,"specs":specs,"faults":faults,"latestApi":0,
        "officialUrl":url,"officialIsStore":url.startswith("https://store.google.com") or url=="https://googlebook.google","launch":launch})
json.dump(k, open(kp,"w",encoding="utf-8"), ensure_ascii=False, indent=1)
cp = os.path.join(A, "catalog.json"); c = json.load(open(cp, encoding="utf-8"))
names=[m[1] for m in M]
c = [x for x in c if x.get("brand") == "Google" and x["name"] not in names]
for id_,name,gen,rel,supp,specs,faults,launch,url in M:
    c.append({"name":name,"brand":"Google","type":"LAPTOP","year":int(rel[:4])})
json.dump(c, open(cp,"w",encoding="utf-8"), ensure_ascii=False, indent=1)
print(len(k["models"]), "models,", len(k["generations"]), "generations,", len(c), "catalogue entries (all Google)")
