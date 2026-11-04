# -*- coding: utf-8 -*-
"""Adds launch information (date, event, place, presenter, YouTube search) to every model."""
import json, os
A = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
p = os.path.join(A, "pixel_knowledge.json")
k = json.load(open(p, encoding="utf-8"))
OS = ("Rick Osterloh, Google's head of devices", "رِک اوسٹرلوہ، گوگل کے ڈیوائسز کے سربراہ")
ONLINE = ("Announced online by Google (blog post and video, no live stage event)", "گوگل نے آن لائن اعلان کیا (بلاگ پوسٹ اور ویڈیو، براہ راست اسٹیج تقریب نہیں)")
def E(date, ev_en, ev_ur, pl_en, pl_ur, pres, q): return dict(date=date, event={"en":ev_en,"ur":ev_ur}, place={"en":pl_en,"ur":pl_ur}, presenter={"en":pres[0],"ur":pres[1]}, query=q)
MBG = ("Made by Google","میڈ بائی گوگل")
ev = {
 "m16": E("2016-10-04","Made by Google 2016","میڈ بائی گوگل 2016","San Francisco, USA","سان فرانسسکو، امریکہ",("Rick Osterloh, with Sundar Pichai","رِک اوسٹرلوہ، سندر پچائی کے ساتھ"),"Made by Google 2016 event full"),
 "m17": E("2017-10-04","Made by Google 2017","میڈ بائی گوگل 2017","San Francisco, USA","سان فرانسسکو، امریکہ",OS,"Made by Google 2017 event full"),
 "m18": E("2018-10-09","Made by Google 2018","میڈ بائی گوگل 2018","New York, USA","نیویارک، امریکہ",OS,"Made by Google 2018 event full"),
 "io19": E("2019-05-07","Google I/O 2019 (launch announcement)","گوگل آئی/او 2019 (اجرا کا اعلان)","Mountain View, USA","ماؤنٹین ویو، امریکہ",("Announced by Google","گوگل کی طرف سے اعلان"),"Google Pixel 3a launch video"),
 "m19": E("2019-10-15","Made by Google 2019","میڈ بائی گوگل 2019","New York, USA","نیویارک، امریکہ",OS,"Made by Google 2019 event full"),
 "o4a": E("2020-08-03","Pixel 4a announcement","پکسل 4a کا اعلان","Online","آن لائن",ONLINE,"Google Pixel 4a introduction video"),
 "lni": E("2020-09-30","Launch Night In (online event)","لانچ نائٹ اِن (آن لائن تقریب)","Online","آن لائن",OS,"Google Launch Night In 2020 Pixel 5 event"),
 "o5a": E("2021-08-17","Pixel 5a (5G) announcement","پکسل 5a (5G) کا اعلان","Online","آن لائن",ONLINE,"Google Pixel 5a 5G introduction video"),
 "pfl": E("2021-10-19","Pixel Fall Launch 2021","پکسل فال لانچ 2021","Online","آن لائن",OS,"Pixel Fall Launch 2021 Pixel 6 event"),
 "io22": E("2022-05-11","Google I/O 2022","گوگل آئی/او 2022","Mountain View, USA","ماؤنٹین ویو، امریکہ",OS,"Google I/O 2022 Pixel 6a keynote"),
 "m22": E("2022-10-06","Made by Google 2022","میڈ بائی گوگل 2022","New York, USA","نیویارک، امریکہ",OS,"Made by Google 2022 event full"),
 "io23": E("2023-05-10","Google I/O 2023","گوگل آئی/او 2023","Mountain View, USA","ماؤنٹین ویو، امریکہ",OS,"Google I/O 2023 Pixel Fold Pixel Tablet Pixel 7a"),
 "m23": E("2023-10-04","Made by Google 2023","میڈ بائی گوگل 2023","New York, USA","نیویارک، امریکہ",OS,"Made by Google 2023 event full"),
 "o8a": E("2024-05-07","Pixel 8a announcement","پکسل 8a کا اعلان","Online","آن لائن",ONLINE,"Google Pixel 8a introduction video"),
 "m24": E("2024-08-13","Made by Google 2024","میڈ بائی گوگل 2024","Mountain View, USA","ماؤنٹین ویو، امریکہ",OS,"Made by Google 2024 event full"),
 "o9a": E("2025-03-19","Pixel 9a announcement","پکسل 9a کا اعلان","Online","آن لائن",ONLINE,"Google Pixel 9a introduction video"),
 "m25": E("2025-08-20","Made by Google 2025","میڈ بائی گوگل 2025","New York, USA","نیویارک، امریکہ",("Rick Osterloh, with host Jimmy Fallon","رِک اوسٹرلوہ، میزبان جمی فیلن کے ساتھ"),"Made by Google 2025 event full Pixel 10"),
 "o10a": E("2026-02-18","Pixel 10a announcement","پکسل 10a کا اعلان","Online","آن لائن",ONLINE,"Google Pixel 10a introduction video"),
 "m26": E("2026-08-12","Made by Google 2026","میڈ بائی گوگل 2026","New York, USA","نیویارک، امریکہ",("Google's devices team (presenter names not confirmed here)","گوگل کی ڈیوائسز ٹیم (مقررین کے نام یہاں تصدیق شدہ نہیں)"),"Made by Google 2026 event Pixel 11"),
 "nx1": E("2010-01-05","Nexus One press event","نیکسس ون کی پریس تقریب","Mountain View, USA","ماؤنٹین ویو، امریکہ",("Google's Android team (Andy Rubin)","گوگل کی اینڈرائیڈ ٹیم (اینڈی روبن)"),"Google Nexus One announcement 2010"),
 "nxs": E("2010-12-06","Nexus S announcement with Android 2.3","اینڈرائیڈ 2.3 کے ساتھ نیکسس ایس کا اعلان","Online / press","آن لائن / پریس",("Announced by Google and Samsung","گوگل اور سام سنگ کی طرف سے اعلان"),"Google Nexus S announcement Gingerbread"),
 "gnx": E("2011-10-19","Google and Samsung event (Android 4.0 launch)","گوگل اور سام سنگ کی تقریب (اینڈرائیڈ 4.0 کا اجرا)","Hong Kong","ہانگ کانگ",("Google's Android team with Samsung","گوگل کی اینڈرائیڈ ٹیم، سام سنگ کے ساتھ"),"Galaxy Nexus Ice Cream Sandwich launch event 2011"),
 "io12": E("2012-06-27","Google I/O 2012","گوگل آئی/او 2012","San Francisco, USA","سان فرانسسکو، امریکہ",("Hugo Barra, Google's Android product lead","ہیوگو بارا، گوگل کے اینڈرائیڈ پروڈکٹ سربراہ"),"Google I/O 2012 keynote Nexus 7 announcement"),
 "n412": E("2012-10-29","Nexus 4 and Nexus 10 announcement (event cancelled by Hurricane Sandy)","نیکسس 4 اور نیکسس 10 کا اعلان (طوفان سینڈی کی وجہ سے تقریب منسوخ)","Online / press","آن لائن / پریس",("Announced by Google (blog post)","گوگل کی طرف سے اعلان (بلاگ پوسٹ)"),"Nexus 4 Nexus 10 announcement Android 4.2"),
 "n7b": E("2013-07-24","Breakfast with Sundar Pichai","بریک فاسٹ ود سندر پچائی","San Francisco, USA","سان فرانسسکو، امریکہ",("Sundar Pichai","سندر پچائی"),"Breakfast with Sundar Pichai 2013 Nexus 7"),
 "n513": E("2013-10-31","Nexus 5 and Android 4.4 KitKat announcement","نیکسس 5 اور اینڈرائیڈ 4.4 کٹ کیٹ کا اعلان","Online / press","آن لائن / پریس",("Announced by Google (blog post)","گوگل کی طرف سے اعلان (بلاگ پوسٹ)"),"Nexus 5 announcement KitKat Google"),
 "n614": E("2014-10-15","Nexus 6, Nexus 9 and Android 5.0 Lollipop announcement","نیکسس 6، نیکسس 9 اور اینڈرائیڈ 5.0 لالی پاپ کا اعلان","Online / press","آن لائن / پریس",("Announced by Google (blog post and video)","گوگل کی طرف سے اعلان (بلاگ پوسٹ اور ویڈیو)"),"Nexus 6 Nexus 9 Android Lollipop announcement"),
 "n15": E("2015-09-29","Google event: Nexus 5X, Nexus 6P and Android 6.0","گوگل کی تقریب: نیکسس 5X، نیکسس 6P اور اینڈرائیڈ 6.0","San Francisco, USA","سان فرانسسکو، امریکہ",("Sundar Pichai and Google's Android team","سندر پچائی اور گوگل کی اینڈرائیڈ ٹیم"),"Google event 2015 Nexus 5X Nexus 6P Chromecast"),
}
M = {"pixel_1":"m16","pixel_1_xl":"m16","pixel_2":"m17","pixel_2_xl":"m17","pixel_3":"m18","pixel_3_xl":"m18","pixel_3a":"io19","pixel_3a_xl":"io19",
 "pixel_4":"m19","pixel_4_xl":"m19","pixel_4a":"o4a","pixel_4a_5g":"lni","pixel_5":"lni","pixel_5a":"o5a","pixel_6":"pfl","pixel_6_pro":"pfl","pixel_6a":"io22",
 "pixel_7":"m22","pixel_7_pro":"m22","pixel_7a":"io23","pixel_fold":"io23","pixel_tablet":"io23","pixel_8":"m23","pixel_8_pro":"m23","pixel_8a":"o8a",
 "pixel_9":"m24","pixel_9_pro":"m24","pixel_9_pro_xl":"m24","pixel_9_pro_fold":"m24","pixel_9a":"o9a",
 "pixel_10":"m25","pixel_10_pro":"m25","pixel_10_pro_xl":"m25","pixel_10_pro_fold":"m25","pixel_10a":"o10a",
 "pixel_11":"m26","pixel_11_pro":"m26","pixel_11_pro_xl":"m26","pixel_11_pro_fold":"m26",
 "nexus_one":"nx1","nexus_s":"nxs","galaxy_nexus":"gnx","nexus_7_2012":"io12","nexus_4":"n412","nexus_10":"n412","nexus_7_2013":"n7b","nexus_5":"n513",
 "nexus_6":"n614","nexus_9":"n614","nexus_5x":"n15","nexus_6p":"n15"}
miss=[m["id"] for m in k["models"] if m["id"] not in M]; print("no launch mapping:",miss)
for m in k["models"]:
    if m["id"] in M: m["launch"]=ev[M[m["id"]]]
json.dump(k, open(p,"w",encoding="utf-8"), ensure_ascii=False, indent=1)
print("ok")
