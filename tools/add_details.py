# -*- coding: utf-8 -*-
"""Adds per-feature device compatibility (English) and extra known faults to the Pixel assets."""
import json, os, re, sys
sys.path.insert(0, os.path.dirname(__file__))
from drops_part1 import DROPS_PART1
from drops_part2 import DROPS_PART2
from drops_part3 import DROPS_PART3
from drops_part4 import DROPS_PART4
from drops_part5 import DROPS_PART5
A = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "assets")
ORDER = ["pixel_1","pixel_1_xl","pixel_2","pixel_2_xl","pixel_3","pixel_3_xl","pixel_3a","pixel_3a_xl","pixel_4","pixel_4_xl",
 "pixel_4a","pixel_4a_5g","pixel_5","pixel_5a","pixel_6","pixel_6_pro","pixel_6a","pixel_7","pixel_7_pro","pixel_7a","pixel_fold",
 "pixel_tablet","pixel_8","pixel_8_pro","pixel_8a","pixel_9","pixel_9_pro","pixel_9_pro_xl","pixel_9_pro_fold","pixel_9a","pixel_10",
 "pixel_10_pro","pixel_10_pro_xl","pixel_10_pro_fold","pixel_10a","pixel_11","pixel_11_pro","pixel_11_pro_xl","pixel_11_pro_fold"]
AL = {"p3a":"pixel_3a","p4":"pixel_4","p5a":"pixel_5a","fold":"pixel_fold","tablet":"pixel_tablet"}
for n in ["6","6pro","6a","7","7pro","7a","8","8pro","8a","9","9pro","9proxl","9profold","9a","10","10pro","10proxl","10profold","10a","11","11pro","11proxl","11profold"]:
    s = n.replace("pro","_pro").replace("xl","_xl").replace("fold","_fold")
    s = re.sub(r"^(\d+)a?_?", lambda m: m.group(0), s)
    AL["p"+n] = "pixel_" + s.replace("6a","6a")
AL["p6a"]="pixel_6a"; AL["p7a"]="pixel_7a"; AL["p8a"]="pixel_8a"; AL["p9a"]="pixel_9a"; AL["p10a"]="pixel_10a"
def expand_expr(expr):
    ids = []
    expr = expr.replace("a-series", "aseries")
    for part in re.findall(r"R\(\w+,\w+\)|[+-]?\w+", expr):
        m = re.match(r"R\((\w+),(\w+)\)", part)
        if m:
            a, b = ORDER.index(AL[m.group(1)]), ORDER.index(AL[m.group(2)])
            for i in ORDER[a:b + 1]:
                if i not in ids: ids.append(i)
            continue
        sign = "-" if part.startswith("-") else "+"
        name = part.lstrip("+-")
        if name == "aseries": continue
        pid = AL.get(name)
        if pid is None: raise SystemExit("unknown token " + name)
        if sign == "+":
            if pid not in ids: ids.append(pid)
        elif pid in ids:
            ids.remove(pid)
    return sorted(ids, key=ORDER.index)
alld = DROPS_PART1 + DROPS_PART2 + DROPS_PART3 + DROPS_PART4 + DROPS_PART5
by = {d["id"]: d for d in alld}
path = os.path.join(A, "pixel_drops.json")
data = json.load(open(path, encoding="utf-8"))
REL = {m["id"]: m["released"] for m in json.load(open(os.path.join(A, "pixel_knowledge.json"), encoding="utf-8"))["models"]}
for d in data["drops"]:
    src = by.get(d["id"])
    if not src or d["id"] in ("2026-09", "2025-04") or not src.get("features"):
        d.pop("details", None); continue
    # A phone that was not on sale yet on the drop's date cannot have received it.
    d["details"] = [{"title": t, "desc": desc, "devices": [i for i in expand_expr(expr) if REL.get(i, "9999") <= d["date"]]} for (t, desc, expr) in src["features"]]
json.dump(data, open(path, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
# ---- extra faults
kp = os.path.join(A, "pixel_knowledge.json")
k = json.load(open(kp, encoding="utf-8"))
def fault(en, ur, official): return {"en": en, "ur": ur, "official": official}
pro_prog = fault("Google opened an Extended Repair Program for display problems (a flickering screen or a full-height line) on the Pixel 9 Pro, 9 Pro XL and 9 Pro Fold.",
  "گوگل نے پکسل 9 پرو، 9 پرو ایکس ایل اور 9 پرو فولڈ کی اسکرین کی خرابیوں (جھلملاہٹ یا مکمل اونچائی کی لکیر) کے لیے توسیعی مرمتی پروگرام شروع کیا۔", True)
nine = fault("Some owners reported the fingerprint sensor stopping after the March 2025 update, and inconsistent auto-brightness. These were software-side reports and were not announced as a hardware fault.",
  "کچھ صارفین نے مارچ 2025 کی اپ ڈیٹ کے بعد فنگر پرنٹ سینسر کے رک جانے اور آٹو برائٹنس کے غیر مستقل ہونے کی اطلاع دی۔ یہ سافٹ ویئر سے متعلق شکایات تھیں اور انہیں ہارڈ ویئر کی خرابی کے طور پر اعلان نہیں کیا گیا۔", False)
qmark = fault("A question-mark icon beside the battery was reported on Pixel 9 Pro Fold and 10 Pro Fold units. A reboot clears it for most people; Google had not publicly acknowledged it.",
  "پکسل 9 پرو فولڈ اور 10 پرو فولڈ پر بیٹری کے ساتھ سوالیہ نشان کی علامت کی اطلاع ملی۔ زیادہ تر صارفین کے لیے ری بوٹ سے ٹھیک ہو جاتی ہے؛ گوگل نے اسے عوامی طور پر تسلیم نہیں کیا۔", False)
add = {"pixel_9": [nine], "pixel_9_pro": [pro_prog], "pixel_9_pro_xl": [pro_prog], "pixel_9_pro_fold": [pro_prog, qmark], "pixel_10_pro_fold": [qmark]}
official_ids = {"pixel_4a", "pixel_6a", "pixel_7a", "pixel_2_xl"}
for m in k["models"]:
    for f in m["faults"]:
        f.setdefault("official", m["id"] in official_ids)
    for f in add.get(m["id"], []):
        if not any(x["en"] == f["en"] for x in m["faults"]): m["faults"].append(f)
json.dump(k, open(kp, "w", encoding="utf-8"), ensure_ascii=False, indent=1)
print("ok", sum(1 for d in data["drops"] if d.get("details")), "drops with details")
