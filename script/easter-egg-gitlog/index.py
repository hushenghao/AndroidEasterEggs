import json
import os
from typing import Dict, Optional

import requests

base_url = "https://android.googlesource.com/platform/frameworks/base/+log/refs/heads/main/"
remote_git_log_tuples = [
    ("EasterEgg", f"{base_url}packages/EasterEgg?format=JSON"),
    ("PlatLogoActivity.java",
     f"{base_url}core/java/com/android/internal/app/PlatLogoActivity.java?format=JSON"
     ),
]

git_logs_path = os.path.join(os.path.dirname(__file__), "git_logs.json")


def load_git_logs() -> Dict[str, any]:
    if not os.path.exists(git_logs_path):
        return {}
    with open(git_logs_path, "r", encoding="utf-8") as file:
        try:
            data = json.load(file)
        except json.JSONDecodeError:
            return {}
    if isinstance(data, dict):
        return {str(key): value for key, value in data.items()}
    return {}


def save_git_logs(hashes: Dict[str, any]) -> None:
    with open(git_logs_path, "w", encoding="utf-8") as file:
        json.dump(hashes, file, ensure_ascii=True, indent=2, sort_keys=True)


def fetch_latest_git_log(url: str) -> Optional[any]:
    response = requests.get(url, timeout=15)
    response.raise_for_status()
    text = response.text
    if text.startswith(")]}'"):
        text = text.split("\n", 1)[1] if "\n" in text else ""
    if not text:
        return None
    payload = json.loads(text)
    logs = payload.get("log", [])
    if not logs:
        return None
    return logs[0]


def save_git_log(name: str, log: any) -> None:
    hashes = load_git_logs()
    hashes[name] = log
    save_git_logs(hashes)


if __name__ == "__main__":
    hashes = load_git_logs()
    for name, url in remote_git_log_tuples:
        print(f"Fetching git log from: {url}")
        try:
            latest = fetch_latest_git_log(url)
            latest_hash = latest.get("commit") if latest else None
        except Exception as exc:
            print(f"[{name}] Failed to fetch git log: {exc}")
            continue
        if not latest_hash:
            print(f"[{name}] No git log found.")
            continue

        previous = hashes.get(name)
        previous_hash = previous.get("commit") if previous else None
        if previous_hash and previous_hash != latest_hash:
            print(
                f"[{name}] changed: {previous_hash} -> {latest_hash} \ncommit: \n{latest.get('message')}"
            )
        elif previous_hash == latest_hash:
            print(f"[{name}] no change: {latest_hash}")
        else:
            print(f"[{name}] initial: {latest_hash} \ncommit: \n{latest.get('message')}")
        hashes[name] = latest
    save_git_logs(hashes)
