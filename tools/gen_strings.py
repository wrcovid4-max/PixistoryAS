# -*- coding: utf-8 -*-
import os, sys
sys.path.insert(0, os.path.dirname(__file__))
from strings_data import S
root = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res")
def esc(t): return t.replace("&", "&amp;").replace("<", "&lt;").replace("'", "\\'").replace('"', '\\"').replace("\n", "\\n").replace("@", "\\@")
def write(folder, idx, extra):
    os.makedirs(os.path.join(root, folder), exist_ok=True)
    lines = ['<?xml version="1.0" encoding="utf-8"?>', "<resources>"] + [f'    <string name="{k}">{esc(v[idx])}</string>' for k, v in S.items()] + extra + ["</resources>"]
    open(os.path.join(root, folder, "strings.xml"), "w", encoding="utf-8").write("\n".join(lines) + "\n")
write("values", 0, ['    <string name="shortcut_add">Add device</string>', '    <string name="shortcut_ask">Ask Pixi</string>'])
write("values-ur", 1, ['    <string name="shortcut_add">آلہ شامل کریں</string>', '    <string name="shortcut_ask">پکسی سے پوچھیں</string>'])
